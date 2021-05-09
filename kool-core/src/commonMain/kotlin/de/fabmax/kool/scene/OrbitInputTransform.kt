package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.animation.SpringDamperDouble

/**
 * A special kind of transform group which translates mouse input into a orbit transform. This is mainly useful
 * for camera manipulation.
 *
 * @author fabmax
 */

fun Scene.orbitInputTransform(name: String? = null, block: OrbitInputTransform.() -> Unit): OrbitInputTransform {
    val sit = OrbitInputTransform(this, name)
    sit.block()
    return sit
}

fun Scene.defaultCamTransform(): OrbitInputTransform {
    val ct = orbitInputTransform {
        // Set some initial rotation so that we look down on the scene
        setMouseRotation(20f, -30f)
        // Add camera to the transform group
        +camera
    }
    +ct
    return ct
}

open class OrbitInputTransform(scene: Scene, name: String? = null) : Group(name), Scene.DragHandler {
    var leftDragMethod = DragMethod.ROTATE
    var middleDragMethod = DragMethod.NONE
    var rightDragMethod = DragMethod.PAN
    var zoomMethod = ZoomMethod.ZOOM_CENTER

    var verticalAxis = Vec3d.Y_AXIS
    var horizontalAxis = Vec3d.X_AXIS
    var minHorizontalRot = -90.0
    var maxHorizontalRot = 90.0

    val translation = MutableVec3d()
    var verticalRotation = 0.0
    var horizontalRotation = 0.0
    var zoom = 10.0
        set(value) {
            field = value.clamp(minZoom, maxZoom)
        }

    var isKeepingStandardTransform = false

    var invertRotX = false
    var invertRotY = false

    var minZoom = 1.0
    var maxZoom = 100.0
    var translationBounds: BoundingBox? = null

    var panMethod: PanBase = CameraOrthogonalPan()

    val vertRotAnimator = SpringDamperDouble(0.0)
    val horiRotAnimator = SpringDamperDouble(0.0)
    val zoomAnimator = SpringDamperDouble(zoom)

    private var prevButtonMask = 0
    private var dragMethod = DragMethod.NONE
    private var dragStart = false
    private val deltaPos = MutableVec2d()
    private var deltaScroll = 0.0

    private val ptrPos = MutableVec2d()
    private val panPlane = Plane()
    private val pointerHitStart = MutableVec3f()
    private val pointerHit = MutableVec3f()
    private val tmpVec1 = MutableVec3f()
    private val tmpVec2 = MutableVec3f()

    private val mouseTransform = Mat4d()
    private val mouseTransformInv = Mat4d()

    var smoothness: Double = 0.0
        set(value) {
            field = value
            val stiffness = if (!value.isFuzzyZero()) { 50.0 / value } else { 0.0 }
            vertRotAnimator.stiffness = stiffness
            horiRotAnimator.stiffness = stiffness
            zoomAnimator.stiffness = stiffness
        }

    init {
        smoothness = 0.5
        panPlane.p.set(Vec3f.ZERO)
        panPlane.n.set(Vec3f.Y_AXIS)

        scene.registerDragHandler(this)

        onUpdate += { (rp, ctx) ->
            doCamTransform(rp, ctx)
        }
    }

    fun setMouseRotation(vertical: Float, horizontal: Float) = setMouseRotation(vertical.toDouble(), horizontal.toDouble())

    fun setMouseRotation(vertical: Double, horizontal: Double) {
        vertRotAnimator.set(vertical)
        horiRotAnimator.set(horizontal)
        verticalRotation = vertical
        horizontalRotation = horizontal
    }

    fun setMouseTranslation(x: Float, y: Float, z: Float) = setMouseTranslation(x.toDouble(), y.toDouble(), z.toDouble())

    fun setMouseTranslation(x: Double, y: Double, z: Double) {
        translation.set(x, y, z)
    }

    fun resetZoom(newZoom: Float) = resetZoom(newZoom.toDouble())

    fun resetZoom(newZoom: Double) {
        zoom = newZoom
        zoomAnimator.set(zoom)
    }

    fun updateTransform() {
        translationBounds?.clampToBounds(translation)

        if (isKeepingStandardTransform) {
            mouseTransform.invert(mouseTransformInv)
            mul(mouseTransformInv)
        }

        val z = zoomAnimator.actual
        val vr = vertRotAnimator.actual
        val hr = horiRotAnimator.actual
        mouseTransform.setIdentity()
        mouseTransform.translate(translation.x, translation.y, translation.z)
        mouseTransform.scale(z, z, z)
        mouseTransform.rotate(vr, verticalAxis)
        mouseTransform.rotate(hr, horizontalAxis)

        if (isKeepingStandardTransform) {
            mul(mouseTransform)
        } else {
            set(mouseTransform)
        }
    }

    private fun doCamTransform(renderPass: RenderPass, ctx: KoolContext) {
        if (dragMethod == DragMethod.PAN && panMethod.computePanPoint(pointerHit, renderPass, ptrPos, ctx)) {
            if (dragStart) {
                dragStart = false
                pointerHitStart.set(pointerHit)

                // stop any ongoing smooth motion, as we start a new one
                stopSmoothMotion()

            } else {
                val s = (1 - smoothness).clamp(0.1, 1.0).toFloat()
                tmpVec1.set(pointerHitStart).subtract(pointerHit).scale(s)
                parent?.toLocalCoords(tmpVec1, 0f)

                // limit panning speed
                val tLen = tmpVec1.length()
                if (tLen > renderPass.camera.globalRange * 0.5f) {
                    tmpVec1.scale(renderPass.camera.globalRange * 0.5f / tLen)
                }

                translation.add(tmpVec1)
            }
        } else {
            pointerHit.set(renderPass.camera.globalLookAt)
        }

        if (!deltaScroll.isFuzzyZero()) {
            zoom *= 1f - deltaScroll / 10f
            deltaScroll = 0.0
        }

        if (dragMethod == DragMethod.ROTATE) {
            verticalRotation -= deltaPos.x / 3 * if (invertRotX) { -1f } else { 1f }
            horizontalRotation -= deltaPos.y / 3 * if (invertRotY) { -1f } else { 1f }
            horizontalRotation = horizontalRotation.clamp(minHorizontalRot, maxHorizontalRot)
            deltaPos.set(Vec2d.ZERO)
        }

        vertRotAnimator.desired = verticalRotation
        horiRotAnimator.desired = horizontalRotation
        zoomAnimator.desired = zoom

        val oldZ = zoomAnimator.actual
        val z = zoomAnimator.animate(ctx.deltaT)
        if (!isFuzzyEqual(oldZ, z)
                && zoomMethod == ZoomMethod.ZOOM_TRANSLATE
                && panMethod.computePanPoint(pointerHit, renderPass, ptrPos, ctx)) {
            computeZoomTranslationPerspective(renderPass.camera, oldZ, z)
        }

        vertRotAnimator.animate(ctx.deltaT)
        horiRotAnimator.animate(ctx.deltaT)
        updateTransform()
    }

    /**
     * Computes the required camera translation so that the camera zooms to the point under the pointer (only works
     * with perspective cameras)
     */
    protected open fun computeZoomTranslationPerspective(camera: Camera, oldZoom: Double, newZoom: Double) {
        // tmpVec1 = zoomed pos on pointer ray
        val s = (newZoom / oldZoom).toFloat()
        camera.globalPos.subtract(pointerHit, tmpVec1).scale(s).add(pointerHit)
        // tmpVec2 = zoomed pos on view center ray
        camera.globalPos.subtract(camera.globalLookAt, tmpVec2).scale(s)
                .add(camera.globalLookAt)
        tmpVec1.subtract(tmpVec2)
        parent?.toLocalCoords(tmpVec1, 0f)
        translation.add(tmpVec1)
    }

    private fun stopSmoothMotion() {
        vertRotAnimator.set(vertRotAnimator.actual)
        horiRotAnimator.set(horiRotAnimator.actual)
        zoomAnimator.set(zoomAnimator.actual)

        verticalRotation = vertRotAnimator.actual
        horizontalRotation = horiRotAnimator.actual
        zoom = zoomAnimator.actual
    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>, scene: Scene, ctx: KoolContext) {
        if (dragPtrs.isNotEmpty() && !dragPtrs[0].isConsumed()) {
            if (dragPtrs[0].buttonEventMask != 0 || dragPtrs[0].buttonMask != prevButtonMask) {
                dragMethod = when {
                    dragPtrs[0].isLeftButtonDown -> leftDragMethod
                    dragPtrs[0].isRightButtonDown -> rightDragMethod
                    dragPtrs[0].isMiddleButtonDown -> middleDragMethod
                    else -> DragMethod.NONE
                }
                dragStart = dragMethod != DragMethod.NONE
            }

            prevButtonMask = dragPtrs[0].buttonMask
            ptrPos.set(dragPtrs[0].x, dragPtrs[0].y)
            deltaPos.set(dragPtrs[0].deltaX, dragPtrs[0].deltaY)
            deltaScroll = dragPtrs[0].deltaScroll
            dragPtrs[0].consume()

        } else {
            deltaPos.set(Vec2d.ZERO)
            deltaScroll = 0.0
        }
    }

    private fun MutableVec3d.add(v: Vec3f) {
        x += v.x
        y += v.y
        z += v.z
    }

    enum class DragMethod {
        NONE,
        ROTATE,
        PAN
    }

    enum class ZoomMethod {
        ZOOM_CENTER,
        ZOOM_TRANSLATE
    }
}

abstract class PanBase {
    abstract fun computePanPoint(result: MutableVec3f, renderPass: RenderPass, ptrPos: Vec2d, ctx: KoolContext): Boolean
}

class CameraOrthogonalPan : PanBase() {
    val panPlane = Plane()
    private val pointerRay = Ray()

    override fun computePanPoint(result: MutableVec3f, renderPass: RenderPass, ptrPos: Vec2d, ctx: KoolContext): Boolean {
        panPlane.p.set(renderPass.camera.globalLookAt)
        panPlane.n.set(renderPass.camera.globalLookDir)
        return renderPass.camera.computePickRay(pointerRay, ptrPos.x.toFloat(), ptrPos.y.toFloat(), renderPass.viewport, ctx) &&
                panPlane.intersectionPoint(pointerRay, result)
    }
}

class FixedPlanePan(planeNormal: Vec3f) : PanBase() {
    val panPlane = Plane()
    private val pointerRay = Ray()

    init {
        panPlane.n.set(planeNormal)
    }

    override fun computePanPoint(result: MutableVec3f, renderPass: RenderPass, ptrPos: Vec2d, ctx: KoolContext): Boolean {
        panPlane.p.set(renderPass.camera.globalLookAt)
        return renderPass.camera.computePickRay(pointerRay, ptrPos.x.toFloat(), ptrPos.y.toFloat(), renderPass.viewport, ctx) &&
                panPlane.intersectionPoint(pointerRay, result)
    }
}

fun xPlanePan() = FixedPlanePan(Vec3f.X_AXIS)
fun yPlanePan() = FixedPlanePan(Vec3f.Y_AXIS)
fun zPlanePan() = FixedPlanePan(Vec3f.Z_AXIS)
