package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import kotlin.math.*

class Gizmo : Node(), InputStack.PointerListener {

    private var prevHoverHandle: Int = AXIS_NONE
    private var hoverHandle: Int = AXIS_NONE
    private var prevHoverPlane: Int = PLANE_NONE
    private var hoverPlane: Int = PLANE_NONE
    private var prevHoverRot: Int = AXIS_NONE
    private var hoverRot: Int = AXIS_NONE

    private val isAnyHover: Boolean
        get() = hoverHandle != AXIS_NONE || hoverRot != AXIS_NONE || hoverPlane != PLANE_NONE

    private var isDrag: Boolean = false
    private var wasPtrDrag: Boolean = false

    private val pickRay = Ray()
    private val pickPoint = MutableVec3f()
    private val pickPlane = Plane()
    private val dragStartPos = MutableVec3f()
    private val tmpMat4 = MutableMat4f()
    private val tmpMat3 = MutableMat3f()
    private val tmpQuat = MutableQuatF()

    private val dragGroup = Node()
    private val scaleGroup = Node()

    private val lineMesh = TriangulatedLineMesh()
    private val solidMesh = addColorMesh {
        isCastingShadow = false
        shader = KslUnlitShader {
            color { vertexColor() }
            pipeline { cullMethod = CullMethod.NO_CULLING }
        }
    }
    private val lineMeshHidden = TriangulatedLineMesh().apply {
        shader = TriangulatedLineMesh.Shader {
            color {
                vertexColor()
                constColor(Color.WHITE.withAlpha(0.4f), ColorBlockConfig.BlendMode.Multiply)
            }
            pipeline {
                depthTest = DepthCompareOp.GREATER_EQUAL
                isWriteDepth = false
            }
        }
    }
    private val solidMeshHidden = addColorMesh {
        isCastingShadow = false
        shader = KslUnlitShader {
            color {
                vertexColor()
                constColor(Color.WHITE.withAlpha(0.4f), ColorBlockConfig.BlendMode.Multiply)
            }
            pipeline {
                cullMethod = CullMethod.NO_CULLING
                depthTest = DepthCompareOp.GREATER_EQUAL
                isWriteDepth = false
            }
        }
    }

    private var dynamicScaleFactor = 0.1f
    private var scale = 1f
    private var prevCamSign = MutableVec3f(1f, 1f, 1f)
    private var camSign = MutableVec3f(1f, 1f, 1f)
    private var meshDirty = true

    private var hasAxisX = false
    private var hasAxisY = false
    private var hasAxisZ = false
    private var hasAxisNegX = false
    private var hasAxisNegY = false
    private var hasAxisNegZ = false

    var properties = GizmoProperties()
        set(value) {
            field = value
            meshDirty = true
        }

    private var axisHandleX = MutableVec3f(Vec3f.X_AXIS)
    private var axisHandleY = MutableVec3f(Vec3f.Y_AXIS)
    private var axisHandleZ = MutableVec3f(Vec3f.Z_AXIS)
    private var axisHandleNegX = MutableVec3f(Vec3f.NEG_X_AXIS)
    private var axisHandleNegY = MutableVec3f(Vec3f.NEG_Y_AXIS)
    private var axisHandleNegZ = MutableVec3f(Vec3f.NEG_Z_AXIS)

    var gizmoListener: GizmoListener? = null

    private val matTransform = MatrixTransformF()

    init {
        transform = matTransform
        addNode(dragGroup)
        dragGroup += scaleGroup
        scaleGroup.apply {
            addNode(lineMesh)
            addNode(lineMeshHidden)
            addNode(solidMesh)
            addNode(solidMeshHidden)
        }
        updateMesh()

        val gizmoCenter = MutableVec3f()

        onUpdate += {
            if (!isDrag) {
                lineMesh.toLocalCoords(camSign.set(it.camera.globalPos), 1f)
                camSign.x = sign(camSign.x)
                camSign.y = sign(camSign.y)
                camSign.z = sign(camSign.z)
            }

            if (hoverHandle != prevHoverHandle
                || hoverPlane != prevHoverPlane
                || hoverRot != prevHoverRot
                || camSign != prevCamSign
                || meshDirty
            ) {
                prevHoverHandle = hoverHandle
                prevHoverPlane = hoverPlane
                prevHoverRot = hoverRot
                prevCamSign.set(camSign)
                meshDirty = false
                updateMesh()
            }

            if (isDynamicScale()) {
                lineMesh.toGlobalCoords(gizmoCenter.set(Vec3f.ZERO))
                val camDist = gizmoCenter.subtract(it.camera.globalPos).dot(it.camera.globalLookDir)
                scale = camDist * dynamicScaleFactor
            }
            scaleGroup.transform.setIdentity().scale(scale)
        }
    }

    fun isDynamicScale() = dynamicScaleFactor != 0f

    fun setDynamicScale(cameraDistanceFactor: Float = 0.1f): Gizmo {
        dynamicScaleFactor = cameraDistanceFactor
        return this
    }

    fun setFixedScale(scale: Float = 1f): Gizmo {
        dynamicScaleFactor = 0f
        this.scale = scale
        return this
    }

    fun getGizmoTransform(result: MutableMat4f): Mat4f {
        return result.set(transform.matrixF).mul(dragGroup.transform.matrixF)
    }

    fun getGizmoTransform(target: Node) {
        val t = target.transform
        tmpMat4.set(transform.matrixF).mul(dragGroup.transform.matrixF)
        t.setMatrix(tmpMat4)
    }

    fun getTranslation(result: MutableVec3f): MutableVec3f {
        return getGizmoTransform(tmpMat4).transform(result.set(Vec3f.ZERO), 1f)
    }

    fun getEulerAngles(result: MutableVec3f): MutableVec3f {
        return getGizmoTransform(tmpMat4).getUpperLeft(tmpMat3).getEulerAngles(result)
    }

    fun getQuatRotation(result: MutableQuatF): MutableQuatF {
        getGizmoTransform(tmpMat4).decompose(rotation = result)
        return result
    }

    fun setGizmoTransform(transformMatrix: Mat4d) {
        matTransform.setMatrix(tmpMat4.set(transformMatrix))
    }

    fun setTranslation(translation: Vec3f) {
        matTransform.matrixF.apply {
            m03 = translation.x
            m13 = translation.y
            m23 = translation.z
        }
        transform.markDirty()
    }

    fun setEulerAngles(euler: Vec3f) {
        tmpMat3.setIdentity().rotate(euler.x.deg, euler.y.deg, euler.z.deg)
        matTransform.matrixF.setUpperLeft(tmpMat3)
        transform.markDirty()
    }

    fun setQuatRotation(rotation: QuatF) {
        tmpMat3.setIdentity().rotate(rotation)
        matTransform.matrixF.setUpperLeft(tmpMat3)
        transform.markDirty()
    }

    private fun updateAxesVisibility() {
        hasAxisX = properties.hasAxisX && (camSign.x >= 0f || !properties.isOnlyShowAxisTowardsCam)
        hasAxisY = properties.hasAxisY && (camSign.y >= 0f || !properties.isOnlyShowAxisTowardsCam)
        hasAxisZ = properties.hasAxisZ && (camSign.z >= 0f || !properties.isOnlyShowAxisTowardsCam)
        hasAxisNegX = properties.hasAxisNegX && (camSign.x < 0f || !properties.isOnlyShowAxisTowardsCam)
        hasAxisNegY = properties.hasAxisNegY && (camSign.y < 0f || !properties.isOnlyShowAxisTowardsCam)
        hasAxisNegZ = properties.hasAxisNegZ && (camSign.z < 0f || !properties.isOnlyShowAxisTowardsCam)
    }

    fun updateMesh() {
        updateAxesVisibility()

        lineMesh.clear()

        if (hasAxisX) lineMesh.addLine(Vec3f.ZERO, axisHandleX.set(properties.axisLenX, 0f, 0f), properties.axisColorX, 5f)
        if (hasAxisY) lineMesh.addLine(Vec3f.ZERO, axisHandleY.set(0f, properties.axisLenY, 0f), properties.axisColorY, 5f)
        if (hasAxisZ) lineMesh.addLine(Vec3f.ZERO, axisHandleZ.set(0f, 0f, properties.axisLenZ), properties.axisColorZ, 5f)
        if (hasAxisNegX) lineMesh.addLine(Vec3f.ZERO, axisHandleNegX.set(-properties.axisLenNegX, 0f, 0f), properties.axisColorNegX, 5f)
        if (hasAxisNegY) lineMesh.addLine(Vec3f.ZERO, axisHandleNegY.set(0f, -properties.axisLenNegY, 0f), properties.axisColorNegY, 5f)
        if (hasAxisNegZ) lineMesh.addLine(Vec3f.ZERO, axisHandleNegZ.set(0f, 0f, -properties.axisLenNegZ), properties.axisColorNegZ, 5f)

        if (properties.hasRotationX) lineMesh.rotationHandle(Vec3f.X_AXIS, properties.rotationAxisColorX, hoverRot == AXIS_X)
        if (properties.hasRotationY) lineMesh.rotationHandle(Vec3f.Y_AXIS, properties.rotationAxisColorY, hoverRot == AXIS_Y)
        if (properties.hasRotationZ) lineMesh.rotationHandle(Vec3f.Z_AXIS, properties.rotationAxisColorZ, hoverRot == AXIS_Z)

        if (properties.hasPlaneXY) lineMesh.planeHandleBorder(Vec3f.X_AXIS, Vec3f.Y_AXIS, camSign.x, camSign.y, properties.planeColorXY)
        if (properties.hasPlaneXZ) lineMesh.planeHandleBorder(Vec3f.X_AXIS, Vec3f.Z_AXIS, camSign.x, camSign.z, properties.planeColorXZ)
        if (properties.hasPlaneYZ) lineMesh.planeHandleBorder(Vec3f.Y_AXIS, Vec3f.Z_AXIS, camSign.y, camSign.z, properties.planeColorYZ)

        generateSolidMesh()

        lineMeshHidden.geometry.apply {
            clear()
            addGeometry(lineMesh.geometry)
        }
        solidMeshHidden.geometry.apply {
            clear()
            addGeometry(solidMesh.geometry)
        }
    }

    private fun generateSolidMesh() {
        solidMesh.generate {
            // axis handles
            if (hasAxisX) {
                withTransform {
                    rotate((-90f).deg, Vec3f.Z_AXIS)
                    axisHandle(properties.axisHandleColorX, properties.axisLenX, hoverHandle == AXIS_X)
                }
            }
            if (hasAxisY) {
                axisHandle(properties.axisHandleColorY, properties.axisLenY, hoverHandle == AXIS_Y)
            }
            if (hasAxisZ) {
                withTransform {
                    rotate(90f.deg, Vec3f.X_AXIS)
                    axisHandle(properties.axisHandleColorZ, properties.axisLenZ, hoverHandle == AXIS_Z)
                }
            }
            if (hasAxisNegX) {
                withTransform {
                    rotate(90f.deg, Vec3f.Z_AXIS)
                    axisHandle(properties.axisHandleColorX, properties.axisLenNegX, hoverHandle == AXIS_NEG_X)
                }
            }
            if (hasAxisNegY) {
                withTransform {
                    rotate(180f.deg, Vec3f.Z_AXIS)
                    axisHandle(properties.axisHandleColorY, properties.axisLenNegY, hoverHandle == AXIS_NEG_Y)
                }
            }
            if (hasAxisNegZ) {
                withTransform {
                    rotate((-90f).deg, Vec3f.X_AXIS)
                    axisHandle(properties.axisHandleColorZ, properties.axisLenNegZ, hoverHandle == AXIS_NEG_Z)
                }
            }

            // plane handles
            val planeSzX = properties.planeHandleSize
            val planeSzY = properties.planeHandleSize
            val planeOriX = planeSzX * 0.5f + properties.planeHandleGap
            val planeOriY = planeSzY * 0.5f + properties.planeHandleGap
            if (properties.hasPlaneXY && hoverPlane == PLANE_XY) {
                color = properties.planeColorXY
                centeredRect {
                    origin.set(planeOriX * camSign.x, planeOriY * camSign.y, 0f)
                    size.set(planeSzX, planeSzY)
                }
            }
            if (properties.hasPlaneXZ && hoverPlane == PLANE_XZ) {
                withTransform {
                    rotate(90f.deg, Vec3f.X_AXIS)
                    color = properties.planeColorXZ
                    centeredRect {
                        origin.set(planeOriX * camSign.x, planeOriY * camSign.z, 0f)
                        size.set(planeSzX, planeSzY)
                    }
                }
            }
            if (properties.hasPlaneYZ && hoverPlane == PLANE_YZ) {
                withTransform {
                    rotate((-90f).deg, Vec3f.Y_AXIS)
                    color = properties.planeColorYZ
                    centeredRect {
                        origin.set(planeOriX * camSign.z, planeOriY * camSign.y, 0f)
                        size.set(planeSzX, planeSzY)
                    }
                }
            }
        }

    }

    private fun MeshBuilder.axisHandle(color: Color, len: Float, isHovered: Boolean) {
        this.color = color
        val r = if (isHovered) properties.axesHandleRadiusHovered else properties.axesHandleRadius
        when (properties.axesHandleShape) {
            AxisHandleShape.ARROW -> cylinder {
                origin.set(0f, len, 0f)
                steps = 8
                height = r * 2f
                bottomRadius = r
                topRadius = 0f
                topFill = false
            }
            AxisHandleShape.SPHERE -> icoSphere {
                center.set(0f, len, 0f)
                steps = 1
                radius = r
            }
            AxisHandleShape.CUBE -> cube {
                origin.set(0f, len, 0f)
                size.set(r, r, r)
            }
        }
    }

    private fun TriangulatedLineMesh.rotationHandle(axis: Vec3f, color: Color, isHovered: Boolean) {
        val p = MutableVec3f()
        val width = if (isHovered) properties.lineWidthHovered else properties.lineWidth

        for (i in 0..60) {
            val a = i / 30f * PI.toFloat()
            val x = cos(a) * properties.rotationHandleRadius
            val y = sin(a) * properties.rotationHandleRadius
            when (axis) {
                Vec3f.X_AXIS -> p.set(0f, x, y)
                Vec3f.Y_AXIS -> p.set(x, 0f, y)
                Vec3f.Z_AXIS -> p.set(x, y, 0f)
            }
            if (i == 0) moveTo(p, color, width) else lineTo(p, color, width)
        }
        stroke()
    }

    private fun TriangulatedLineMesh.planeHandleBorder(axisX: Vec3f, axisY: Vec3f, signX: Float, signY: Float, color: Color) {
        this.color = color
        this.width = properties.lineWidth
        val sX = MutableVec3f(axisX).mul(properties.planeHandleSize * signX)
        val sY = MutableVec3f(axisY).mul(properties.planeHandleSize * signY)
        val g = MutableVec3f(axisX).mul(properties.planeHandleGap * signX)
            .add(MutableVec3f(axisY).mul(properties.planeHandleGap * signY))
        moveTo(g)
        lineTo(MutableVec3f(g).add(sX))
        lineTo(MutableVec3f(g).add(sX).add(sY))
        lineTo(MutableVec3f(g).add(sY))
        lineTo(g)
        stroke()
    }

    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        if (!isVisible) {
            return
        }

        val ptr = pointerState.primaryPointer
        val scene = findParentOfType<Scene>() ?: return
        val cam = scene.camera
        if (!scene.computePickRay(ptr, ctx, pickRay)) {
            return
        }

        if (!wasPtrDrag && ptr.isDrag && isAnyHover) {
            // pointer is hovering any gizmo handle and user started dragging
            gizmoListener?.onDragStart(ctx)
            isDrag = true
        }
        wasPtrDrag = ptr.isDrag

        if (isDrag && !ptr.isDrag) {
            // user stopped dragging, apply drag transform
            matTransform.mul(dragGroup.transform.matrixF)
            dragGroup.transform.setIdentity()
            gizmoListener?.onDragFinished(ctx)
            isDrag = false
        }

        if (isDrag && isAnyHover) {
            // drag is in progress, continue that and return (even if pointer is not valid, this way we can continue
            // dragging while the pointer is outside the application window)
            doDrag(pickRay, ptr, ctx)
            return
        }

        // clear hover flags
        hoverHandle = AXIS_NONE
        hoverPlane = PLANE_NONE
        hoverRot = AXIS_NONE
        if (!ptr.isValid) {
            return
        }

        checkHoverAxes(cam)
        if (hoverHandle == AXIS_NONE) {
            checkHoverPlanes(cam)
        }

        if (isAnyHover && ptr.isAnyButtonEvent) {
            // consumer any pointer button event whenever it is hovering the gizmo
            ptr.consume()
        }
    }

    private fun doDrag(pickRay: Ray, pointer: Pointer, ctx: KoolContext) {
        val localPickDir = lineMesh.toLocalCoords(MutableVec3f(pickRay.direction), 0f)
        val dotX = abs(localPickDir.dot(Vec3f.X_AXIS))
        val dotY = abs(localPickDir.dot(Vec3f.Y_AXIS))
        val dotZ = abs(localPickDir.dot(Vec3f.Z_AXIS))
        val pickNormalX = if (dotY > dotZ) Vec3f.Y_AXIS else Vec3f.Z_AXIS
        val pickNormalY = if (dotX > dotZ) Vec3f.X_AXIS else Vec3f.Z_AXIS
        val pickNormalZ = if (dotX > dotY) Vec3f.X_AXIS else Vec3f.Y_AXIS

        when {
            hoverHandle == AXIS_X -> { dragAxis(pickRay, pickNormalX, Vec3f.X_AXIS, ctx) }
            hoverHandle == AXIS_Y -> { dragAxis(pickRay, pickNormalY, Vec3f.Y_AXIS, ctx) }
            hoverHandle == AXIS_Z -> { dragAxis(pickRay, pickNormalZ, Vec3f.Z_AXIS, ctx) }
            hoverHandle == AXIS_NEG_X -> { dragAxis(pickRay, pickNormalX, Vec3f.NEG_X_AXIS, ctx) }
            hoverHandle == AXIS_NEG_Y -> { dragAxis(pickRay, pickNormalY, Vec3f.NEG_Y_AXIS, ctx) }
            hoverHandle == AXIS_NEG_Z -> { dragAxis(pickRay, pickNormalZ, Vec3f.NEG_Z_AXIS, ctx) }
            hoverRot == AXIS_X -> { dragRotate(pickRay, Vec3f.X_AXIS, ctx) }
            hoverRot == AXIS_Y -> { dragRotate(pickRay, Vec3f.Y_AXIS, ctx) }
            hoverRot == AXIS_Z -> { dragRotate(pickRay, Vec3f.Z_AXIS, ctx) }
            hoverPlane == PLANE_XY -> { dragPlane(pickRay, Vec3f.Z_AXIS, ctx) }
            hoverPlane == PLANE_YZ -> { dragPlane(pickRay, Vec3f.X_AXIS, ctx) }
            hoverPlane == PLANE_XZ -> { dragPlane(pickRay, Vec3f.Y_AXIS, ctx) }
        }

        pointer.consume()
    }

    private fun dragAxis(pickRay: Ray, normal: Vec3f, axis: Vec3f, ctx: KoolContext) {
        toGlobalCoords(pickPlane.p.set(Vec3f.ZERO))
        toGlobalCoords(pickPlane.n.set(normal), 0f)
        if (pickPlane.intersectionPoint(pickRay, pickPoint)) {
            val dragDist = toLocalCoords(pickPoint).subtract(dragStartPos).dot(axis)
            dragGroup.transform.setIdentity()
            gizmoListener?.onDragAxis(axis, dragDist, dragGroup.transform, ctx)
            dragGroup.transform.markDirty()
        }
    }

    private fun dragPlane(pickRay: Ray, normal: Vec3f, ctx: KoolContext) {
        toGlobalCoords(pickPlane.p.set(Vec3f.ZERO))
        toGlobalCoords(pickPlane.n.set(normal), 0f)
        if (pickPlane.intersectionPoint(pickRay, pickPoint)) {
            val position = toLocalCoords(pickPoint).subtract(dragStartPos)
            dragGroup.transform.setIdentity()
            gizmoListener?.onDragPlane(normal, position, dragGroup.transform, ctx)
            dragGroup.transform.markDirty()
        }
    }

    private fun dragRotate(pickRay: Ray, axis: Vec3f, ctx: KoolContext) {
        toGlobalCoords(pickPlane.p.set(Vec3f.ZERO))
        toGlobalCoords(pickPlane.n.set(axis), 0f)

        if (pickPlane.intersectionPoint(pickRay, pickPoint)) {
            toLocalCoords(pickPoint)
            val aStart: Float
            val aDrag: Float
            when (axis) {
                Vec3f.X_AXIS -> {
                    aStart = atan2(dragStartPos.y, dragStartPos.z)
                    aDrag = atan2(pickPoint.y, pickPoint.z)
                }
                Vec3f.Y_AXIS -> {
                    aStart = atan2(dragStartPos.z, dragStartPos.x)
                    aDrag = atan2(pickPoint.z, pickPoint.x)
                }
                else -> {
                    aStart = atan2(dragStartPos.x, dragStartPos.y)
                    aDrag = atan2(pickPoint.x, pickPoint.y)
                }
            }

            dragGroup.transform.setIdentity()
            gizmoListener?.onDragRotate(axis, (aStart - aDrag).toDeg(), dragGroup.transform, ctx)
            dragGroup.transform.markDirty()
        }
    }

    private fun checkHoverAxes(cam: Camera) {
        toGlobalCoords(pickPlane.p.set(Vec3f.ZERO))

        val localPickDir = lineMesh.toLocalCoords(MutableVec3f(pickRay.direction), 0f)
        val dotX = abs(localPickDir.dot(Vec3f.X_AXIS))
        val dotY = abs(localPickDir.dot(Vec3f.Y_AXIS))
        val dotZ = abs(localPickDir.dot(Vec3f.Z_AXIS))
        val pickNormalX = if (dotY > dotZ) Vec3f.Y_AXIS else Vec3f.Z_AXIS
        val pickNormalY = if (dotX > dotZ) Vec3f.X_AXIS else Vec3f.Z_AXIS
        val pickNormalZ = if (dotX > dotY) Vec3f.X_AXIS else Vec3f.Y_AXIS

        var np = isHoverHandle(AXIS_X, hasAxisX, axisHandleX, Float.MAX_VALUE, pickNormalX, cam)
        np = isHoverHandle(AXIS_Y, hasAxisY, axisHandleY, np, pickNormalY, cam)
        np = isHoverHandle(AXIS_Z, hasAxisZ, axisHandleZ, np, pickNormalZ, cam)
        np = isHoverHandle(AXIS_NEG_X, hasAxisNegX, axisHandleNegX, np, pickNormalX, cam)
        np = isHoverHandle(AXIS_NEG_Y, hasAxisNegY, axisHandleNegY, np, pickNormalY, cam)
        isHoverHandle(AXIS_NEG_Z, hasAxisNegZ, axisHandleNegZ, np, pickNormalZ, cam)
    }

    private fun isHoverHandle(
        axis: Int,
        isHandleActive: Boolean,
        handlePos: Vec3f,
        minDist: Float,
        normal: Vec3f,
        cam: Camera
    ): Float {
        val d = pickRay.distanceToPoint(toGlobalCoords(pickPoint.set(handlePos).mul(scale))) / scale
        val n = pickPoint.distance(cam.globalPos)

        if (isHandleActive && d < properties.axesHandleRadiusHovered && n < minDist) {
            toGlobalCoords(pickPlane.n.set(normal).norm(), 0f)
            pickPlane.intersectionPoint(pickRay, dragStartPos)
            toLocalCoords(dragStartPos)
            hoverHandle = axis
            return n
        }
        return Float.MAX_VALUE
    }

    private fun checkHoverPlanes(cam: Camera) {
        toGlobalCoords(pickPlane.p.set(Vec3f.ZERO))

        var np = checkPlaneHover(PLANE_XY, AXIS_Z, properties.hasPlaneXY, properties.hasRotationZ, Float.MAX_VALUE, Vec3f.Z_AXIS, cam)
        np = checkPlaneHover(PLANE_XZ, AXIS_Y, properties.hasPlaneXZ, properties.hasRotationY, np, Vec3f.Y_AXIS, cam)
        checkPlaneHover(PLANE_YZ, AXIS_X, properties.hasPlaneYZ, properties.hasRotationX, np, Vec3f.X_AXIS, cam)
    }

    private fun checkPlaneHover(
        plane: Int,
        rotAxis: Int,
        isPlane: Boolean,
        isRot: Boolean,
        minDist: Float,
        normal: Vec3f,
        cam: Camera
    ): Float {
        var returnDist = minDist
        val handleGap = properties.planeHandleGap * scale
        val handleSize = properties.planeHandleSize * scale

        toGlobalCoords(pickPlane.n.set(normal), 0f)
        if (pickPlane.intersectionPoint(pickRay, pickPoint)) {
            val d = pickPoint.distance(cam.globalPos) / scale
            toLocalCoords(pickPoint)
            val pickX: Float
            val pickY: Float
            val signX: Float
            val signY: Float
            when {
                normal.x != 0f -> {
                    pickX = pickPoint.y; signX = camSign.y
                    pickY = pickPoint.z; signY = camSign.z
                }

                normal.y != 0f -> {
                    pickX = pickPoint.x; signX = camSign.x
                    pickY = pickPoint.z; signY = camSign.z
                }

                else -> {
                    pickX = pickPoint.x; signX = camSign.x
                    pickY = pickPoint.y; signY = camSign.y
                }
            }

            if (d < minDist) {
                val boundAx = handleGap * signX
                val boundBx = (handleGap + handleSize) * signX
                val boundAy = handleGap * signY
                val boundBy = (handleGap + handleSize) * signY
                if (isPlane && isInInterval(pickX, boundAx, boundBx) && isInInterval(pickY, boundAy, boundBy)) {
                    hoverRot = AXIS_NONE
                    hoverPlane = plane
                    returnDist = d
                } else if (isRot && abs(pickPoint.length() / scale - properties.rotationHandleRadius) < properties.rotationHandleGrabDist) {
                    hoverPlane = PLANE_NONE
                    hoverRot = rotAxis
                    returnDist = d
                }
                if (returnDist == d) dragStartPos.set(pickPoint)
            }
        }
        return returnDist
    }

    private fun isInInterval(value: Float, boundA: Float, boundB: Float): Boolean {
        return value > min(boundA, boundB) && value < max(boundA, boundB)
    }

    interface GizmoListener {
        fun onDragStart(ctx: KoolContext) { }
        fun onDragFinished(ctx: KoolContext) { }
        fun onDragAxis(axis: Vec3f, distance: Float, targetTransform: Transform, ctx: KoolContext) { }
        fun onDragPlane(planeNormal: Vec3f, dragPosition: Vec3f, targetTransform: Transform, ctx: KoolContext) { }
        fun onDragRotate(rotationAxis: Vec3f, angle: Float, targetTransform: Transform, ctx: KoolContext) { }
    }

    data class GizmoProperties(
        var axisLenX: Float = 1f,
        var axisLenY: Float = 1f,
        var axisLenZ: Float = 1f,
        var axisLenNegX: Float = 1f,
        var axisLenNegY: Float = 1f,
        var axisLenNegZ: Float = 1f,

        var axisColorX: Color = MdColor.RED,
        var axisColorY: Color = MdColor.GREEN,
        var axisColorZ: Color = MdColor.BLUE,
        var axisColorNegX: Color = MdColor.RED,
        var axisColorNegY: Color = MdColor.GREEN,
        var axisColorNegZ: Color = MdColor.BLUE,
        var axisHandleColorX: Color = MdColor.RED,
        var axisHandleColorY: Color = MdColor.GREEN,
        var axisHandleColorZ: Color = MdColor.BLUE,
        var axisHandleColorNegX: Color = MdColor.RED,
        var axisHandleColorNegY: Color = MdColor.GREEN,
        var axisHandleColorNegZ: Color = MdColor.BLUE,

        var rotationAxisColorX: Color = MdColor.RED,
        var rotationAxisColorY: Color = MdColor.GREEN,
        var rotationAxisColorZ: Color = MdColor.BLUE,

        var planeColorXY: Color = MdColor.BLUE,
        var planeColorXZ: Color = MdColor.GREEN,
        var planeColorYZ: Color = MdColor.RED,

        var axesHandleRadius: Float = 0.075f,
        var axesHandleRadiusHovered: Float = 0.1f,
        var axesHandleShape: AxisHandleShape = AxisHandleShape.SPHERE,
        var rotationHandleRadius: Float = 0.75f,
        var rotationHandleGrabDist: Float = 0.075f,
        var planeHandleSize: Float = 0.33f,
        var planeHandleGap: Float = 0.15f,

        var lineWidth: Float = 3f,
        var lineWidthHovered: Float = 7f,

        var hasAxisX: Boolean = true,
        var hasAxisY: Boolean = true,
        var hasAxisZ: Boolean = true,
        var hasAxisNegX: Boolean = true,
        var hasAxisNegY: Boolean = true,
        var hasAxisNegZ: Boolean = true,
        var isOnlyShowAxisTowardsCam: Boolean = true,

        var hasRotationX: Boolean = true,
        var hasRotationY: Boolean = true,
        var hasRotationZ: Boolean = true,

        var hasPlaneXY: Boolean = true,
        var hasPlaneXZ: Boolean = true,
        var hasPlaneYZ: Boolean = true
    ) {
        fun setAxesLengths(len: Float) {
            axisLenX = len
            axisLenY = len
            axisLenZ = len
            axisLenNegX = len
            axisLenNegY = len
            axisLenNegZ = len
        }

        fun setAxisHandlesEnabled(enabled: Boolean) {
            hasAxisX = enabled
            hasAxisY = enabled
            hasAxisZ = enabled
            hasAxisNegX = enabled
            hasAxisNegY = enabled
            hasAxisNegZ = enabled
        }

        fun setPlaneHandlesEnabled(enabled: Boolean) {
            hasPlaneXY = enabled
            hasPlaneXZ = enabled
            hasPlaneYZ = enabled
        }

        fun setRotationHandlesEnabled(enabled: Boolean) {
            hasRotationX = enabled
            hasRotationY = enabled
            hasRotationZ = enabled
        }
    }

    enum class AxisHandleShape {
        ARROW,
        SPHERE,
        CUBE
    }

    companion object {
        private const val AXIS_NONE = -1
        private const val AXIS_X = 0
        private const val AXIS_Y = 1
        private const val AXIS_Z = 2
        private const val AXIS_NEG_X = 3
        private const val AXIS_NEG_Y = 4
        private const val AXIS_NEG_Z = 5

        private const val PLANE_NONE = -1
        private const val PLANE_XY = 0
        private const val PLANE_XZ = 1
        private const val PLANE_YZ = 2
    }
}