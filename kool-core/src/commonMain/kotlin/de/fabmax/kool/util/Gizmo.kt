package de.fabmax.kool.util

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import kotlin.math.*

class Gizmo : Group(), InputStack.PointerListener {

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
    private val tmpMat4 = Mat4d()
    private val tmpMat3 = Mat3f()

    private val dragGroup = Group()
    private val scaleGroup = Group()

    private val lineMesh = BetterLineMesh()
    private val solidMesh = colorMesh {
        shader = KslUnlitShader {
            color { vertexColor() }
            colorSpaceConversion = ColorSpaceConversion.AS_IS
            pipeline { cullMethod = CullMethod.NO_CULLING }
        }
    }
    private val lineMeshHidden = BetterLineMesh().apply {
        shader = BetterLineMesh.LineShader(KslUnlitShader.Config().apply {
            color {
                vertexColor()
                constColor(Color.WHITE.withAlpha(0.4f), ColorBlockConfig.MixMode.Multiply)
            }
            colorSpaceConversion = ColorSpaceConversion.AS_IS
            pipeline {
                depthTest = DepthCompareOp.GREATER_EQUAL
                isWriteDepth = false
            }
        })
    }
    private val solidMeshHidden = colorMesh {
        shader = KslUnlitShader {
            color {
                vertexColor()
                constColor(Color.WHITE.withAlpha(0.4f), ColorBlockConfig.MixMode.Multiply)
            }
            colorSpaceConversion = ColorSpaceConversion.AS_IS
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
        set(value) { field = value; meshDirty = true }

    var axisHandleX = MutableVec3f(Vec3f.X_AXIS)
    var axisHandleY = MutableVec3f(Vec3f.Y_AXIS)
    var axisHandleZ = MutableVec3f(Vec3f.Z_AXIS)
    var axisHandleNegX = MutableVec3f(Vec3f.NEG_X_AXIS)
    var axisHandleNegY = MutableVec3f(Vec3f.NEG_Y_AXIS)
    var axisHandleNegZ = MutableVec3f(Vec3f.NEG_Z_AXIS)

    var gizmoListener: GizmoListener? = null

    init {
        +dragGroup
        dragGroup += scaleGroup
        scaleGroup.apply {
            +lineMesh
            +lineMeshHidden
            +solidMesh
            +solidMeshHidden
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
                val camDist = gizmoCenter.distance(it.camera.globalPos)
                scale = camDist * dynamicScaleFactor
            }
            scaleGroup.setIdentity().scale(scale)
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

    fun getGizmoTransform(result: Mat4d): Mat4d {
        return result.set(transform).mul(dragGroup.transform)
    }

    fun getGizmoTransform(target: Group) {
        target.set(transform).mul(dragGroup.transform)
    }

    fun getTranslation(result: MutableVec3f): MutableVec3f {
        return getGizmoTransform(tmpMat4).transform(result.set(Vec3f.ZERO), 1f)
    }

    fun getEulerAngles(result: MutableVec3f): MutableVec3f {
        return getGizmoTransform(tmpMat4).getRotation(tmpMat3).getEulerAngles(result)
    }

    fun setGizmoTransform(transform: Mat4d) {
        set(transform)
    }

    fun setTranslation(translation: Vec3f) {
        transform[0, 3] = translation.x.toDouble()
        transform[1, 3] = translation.y.toDouble()
        transform[2, 3] = translation.z.toDouble()
        setDirty()
    }

    fun setEulerAngles(euler: Vec3f) {
        tmpMat3.setRotate(euler.x, euler.y, euler.z)
        transform.setRotation(tmpMat3)
        setDirty()
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

        if (hasAxisX) lineMesh.line(Vec3f.ZERO, axisHandleX, properties.axisColorX, 5f)
        if (hasAxisY) lineMesh.line(Vec3f.ZERO, axisHandleY, properties.axisColorY, 5f)
        if (hasAxisZ) lineMesh.line(Vec3f.ZERO, axisHandleZ, properties.axisColorZ, 5f)
        if (hasAxisNegX) lineMesh.line(Vec3f.ZERO, axisHandleNegX, properties.axisColorNegX, 5f)
        if (hasAxisNegY) lineMesh.line(Vec3f.ZERO, axisHandleNegY, properties.axisColorNegY, 5f)
        if (hasAxisNegZ) lineMesh.line(Vec3f.ZERO, axisHandleNegZ, properties.axisColorNegZ, 5f)

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
            if (hasAxisX) axisHandle(properties.axisHandleColorX, axisHandleX, hoverHandle == AXIS_X)
            if (hasAxisY) axisHandle(properties.axisHandleColorY, axisHandleY, hoverHandle == AXIS_Y)
            if (hasAxisZ) axisHandle(properties.axisHandleColorZ, axisHandleZ, hoverHandle == AXIS_Z)
            if (hasAxisNegX) axisHandle(properties.axisHandleColorX, axisHandleNegX, hoverHandle == AXIS_NEG_X)
            if (hasAxisNegY) axisHandle(properties.axisHandleColorY, axisHandleNegY, hoverHandle == AXIS_NEG_Y)
            if (hasAxisNegZ) axisHandle(properties.axisHandleColorZ, axisHandleNegZ, hoverHandle == AXIS_NEG_Z)

            // plane handles
            if (properties.hasPlaneXY && hoverPlane == PLANE_XY) {
                color = properties.planeColorXY
                rect {
                    size.set(properties.planeHandleSize * camSign.x, properties.planeHandleSize * camSign.y)
                }
            }
            if (properties.hasPlaneXZ && hoverPlane == PLANE_XZ) {
                withTransform {
                    rotate(90f, Vec3f.X_AXIS)
                    color = properties.planeColorXZ
                    rect {
                        size.set(properties.planeHandleSize * camSign.x, properties.planeHandleSize * camSign.z)
                    }
                }
            }
            if (properties.hasPlaneYZ && hoverPlane == PLANE_YZ) {
                withTransform {
                    rotate(-90f, Vec3f.Y_AXIS)
                    color = properties.planeColorYZ
                    rect {
                        size.set(properties.planeHandleSize * camSign.z, properties.planeHandleSize * camSign.y)
                    }
                }
            }
        }

    }

    private fun MeshBuilder.axisHandle(color: Color, position: Vec3f, isHovered: Boolean) {
        this.color = color
        icoSphere {
            center.set(position)
            steps = 1
            radius = if (isHovered) properties.axesHandleRadiusHovered else properties.axesHandleRadius
        }
    }

    private fun BetterLineMesh.rotationHandle(axis: Vec3f, color: Color, isHovered: Boolean) {
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

    private fun BetterLineMesh.planeHandleBorder(axisX: Vec3f, axisY: Vec3f, signX: Float, signY: Float, color: Color) {
        val x = MutableVec3f(axisX).scale(properties.planeHandleSize * signX)
        val y = MutableVec3f(axisY).scale(properties.planeHandleSize * signY)
        moveTo(x, color, 3f)
        lineTo(x.add(y), color, 3f)
        lineTo(y, color, 3f)
        stroke()
    }

    override fun handlePointer(pointerState: InputManager.PointerState, ctx: KoolContext) {
        val ptr = pointerState.primaryPointer
        val scene = findParentOfType<Scene>() ?: return
        val cam = scene.mainRenderPass.camera
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
            mul(dragGroup.transform)
            dragGroup.setIdentity()
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

        if (isAnyHover) {
            // consumer pointer whenever it is hovering the gizmo
            ptr.consume()
        }
    }

    private fun doDrag(pickRay: Ray, pointer: InputManager.Pointer, ctx: KoolContext) {
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
            val dragDist = toLocalCoords(pickPoint).subtract(dragStartPos) * axis
            dragGroup.setIdentity()
            gizmoListener?.onDragAxis(axis, dragDist, dragGroup.transform, ctx)
            dragGroup.setDirty()
        }
    }

    private fun dragPlane(pickRay: Ray, normal: Vec3f, ctx: KoolContext) {
        toGlobalCoords(pickPlane.p.set(Vec3f.ZERO))
        toGlobalCoords(pickPlane.n.set(normal), 0f)
        if (pickPlane.intersectionPoint(pickRay, pickPoint)) {
            val position = toLocalCoords(pickPoint).subtract(dragStartPos)
            dragGroup.setIdentity()
            gizmoListener?.onDragPlane(normal, position, dragGroup.transform, ctx)
            dragGroup.setDirty()
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

            dragGroup.setIdentity()
            gizmoListener?.onDragRotate(axis, (aStart - aDrag).toDeg(), dragGroup.transform, ctx)
            dragGroup.setDirty()
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

    private fun isHoverHandle(axis: Int, isHandleActive: Boolean, handlePos: Vec3f, minDist: Float, normal: Vec3f, cam: Camera): Float {
        val d = pickRay.distanceToPoint(toGlobalCoords(pickPoint.set(handlePos).scale(scale))) / scale
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

    private fun checkPlaneHover(plane: Int, rotAxis: Int, isPlane: Boolean, isRot: Boolean, minDist: Float, normal: Vec3f, cam: Camera): Float {
        var returnDist = minDist
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
                    pickX = pickPoint.y;    signX = camSign.y
                    pickY = pickPoint.z;    signY = camSign.z
                }
                normal.y != 0f -> {
                    pickX = pickPoint.x;    signX = camSign.x
                    pickY = pickPoint.z;    signY = camSign.z
                }
                else -> {
                    pickX = pickPoint.x;    signX = camSign.x
                    pickY = pickPoint.y;    signY = camSign.y
                }
            }

            if (d < minDist) {
                if (isPlane && isInInterval(pickX, handleSize * signX) && isInInterval(pickY, handleSize * signY)) {
                    hoverRot = AXIS_NONE
                    hoverPlane = plane
                    returnDist = d
                } else if (isRot && abs(pickPoint.length() / scale - properties.rotationHandleRadius) < 0.05f) {
                    hoverPlane = PLANE_NONE
                    hoverRot = rotAxis
                    returnDist = d
                }
                if (returnDist == d) dragStartPos.set(pickPoint)
            }
        }
        return returnDist
    }

    private fun isInInterval(value: Float, bound: Float): Boolean {
        return if (bound < 0f) value > bound && value < 0f else value > 0f && value < bound
    }

    interface GizmoListener {
        fun onDragStart(ctx: KoolContext) { }
        fun onDragFinished(ctx: KoolContext) { }
        fun onDragAxis(axis: Vec3f, distance: Float, targetTransform: Mat4d, ctx: KoolContext) { }
        fun onDragPlane(planeNormal: Vec3f, dragPosition: Vec3f, targetTransform: Mat4d, ctx: KoolContext) { }
        fun onDragRotate(rotationAxis: Vec3f, angle: Float, targetTransform: Mat4d, ctx: KoolContext) { }
    }

    data class GizmoProperties(
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

        var planeColorXY: Color = MdColor.AMBER,
        var planeColorXZ: Color = MdColor.PURPLE,
        var planeColorYZ: Color = MdColor.CYAN,

        var axesHandleRadius: Float = 0.075f,
        var axesHandleRadiusHovered: Float = 0.1f,
        var rotationHandleRadius: Float = 0.75f,
        var planeHandleSize: Float = 0.33f,

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
    )

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