package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.CursorMode
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxD
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.animation.ExponentialDecayDouble
import de.fabmax.kool.util.Time
import kotlin.math.abs

/**
 * A special kind of transform group which translates mouse input into a orbit transform. This is mainly useful
 * for camera manipulation.
 *
 * @author fabmax
 */

fun orbitCamera(view: RenderPass.View, name: String? = null, block: OrbitInputTransform.() -> Unit): OrbitInputTransform {
    val orbitCam = OrbitInputTransform(name)
    orbitCam.addNode(view.camera)
    orbitCam.block()
    view.drawNode.addNode(orbitCam)

    InputStack.defaultInputHandler.pointerListeners += orbitCam
    orbitCam.onRelease {
        InputStack.defaultInputHandler.pointerListeners -= orbitCam
    }
    return orbitCam
}

fun Scene.orbitCamera(name: String? = null, block: OrbitInputTransform.() -> Unit): OrbitInputTransform {
    return orbitCamera(mainRenderPass.defaultView, name, block)
}

fun Scene.defaultOrbitCamera(yaw: Float = 20f, pitch: Float = -30f): OrbitInputTransform {
    return orbitCamera {
        setRotation(yaw, pitch)
    }
}

open class OrbitInputTransform(name: String? = null) : Node(name), InputStack.PointerListener {
    var leftDragMethod = DragMethod.ROTATE
    var middleDragMethod = DragMethod.NONE
    var rightDragMethod = DragMethod.PAN
    var zoomMethod = ZoomMethod.ZOOM_CENTER
    var isConsumingPtr = true

    var verticalAxis = Vec3d.Y_AXIS
    var horizontalAxis = Vec3d.X_AXIS
    var minHorizontalRot = -90.0
    var maxHorizontalRot = 90.0

    val translation = MutableVec3d()
    val globalTranslation: Vec3d get() = parent?.toGlobalCoords(tmpGlobalTranslation.set(translation)) ?: translation
    var verticalRotation = 0.0
    var horizontalRotation = 0.0
    var zoom = 10.0
        set(value) {
            field = value.clamp(minZoom, maxZoom)
        }

    var isInfiniteDragCursor = false
    var isApplyTranslation = true

    var invertRotX = false
    var invertRotY = false

    var minZoom = 1.0
    var maxZoom = 100.0
    var translationBounds: BoundingBoxD? = null

    var panMethod: PanBase = CameraOrthogonalPan()

    val vertRotAnimator = ExponentialDecayDouble(0.0)
    val horiRotAnimator = ExponentialDecayDouble(0.0)
    val zoomAnimator = ExponentialDecayDouble(zoom)

    private var prevButtonMask = 0
    private var dragMethod = DragMethod.NONE
    private var prevDragMethod = DragMethod.NONE
    private var dragStart = false
    private val deltaPos = MutableVec2f()
    private var deltaScroll = 0f
    private val panStartTranslation = MutableVec3d()

    private val ptrPos = MutableVec2f()
    private val panPlane = PlaneD()
    private val pointerHitStart = MutableVec3d()
    private val pointerHit = MutableVec3d()
    private val tmpVec1 = MutableVec3d()
    private val tmpVec2 = MutableVec3d()
    private val tmpGlobalTranslation = MutableVec3d()

    private val matrixTransform: MatrixTransformD
        get() = transform as MatrixTransformD

    /**
     * Determines how much camera movement is smoothed via exponential decay. Larger numbers mean less smooth
     * movement (however, a value of 0.0 completely disables smoothing). Default value is 16.0.
     */
    var smoothingDecay: Double = 16.0
        set(value) {
            field = value
            vertRotAnimator.decay = value
            horiRotAnimator.decay = value
            zoomAnimator.decay = value
            panMethod.panDecay = value
        }

    init {
        transform = MatrixTransformD()
        smoothingDecay = 16.0
        panPlane.p.set(Vec3f.ZERO)
        panPlane.n.set(Vec3f.Y_AXIS)

        onUpdate += { ev ->
            doCamTransform(ev.view)
        }
    }

    fun setRotation(yaw: Float, pitch: Float) = setRotation(yaw.toDouble(), pitch.toDouble())

    fun setRotation(yaw: Double, pitch: Double) {
        vertRotAnimator.set(yaw)
        horiRotAnimator.set(pitch)
        verticalRotation = yaw
        horizontalRotation = pitch
    }

    fun setTranslation(x: Float, y: Float, z: Float) = setTranslation(x.toDouble(), y.toDouble(), z.toDouble())

    fun setTranslation(x: Double, y: Double, z: Double) {
        translation.set(x, y, z)
    }

    fun setZoom(newZoom: Float) = setZoom(newZoom.toDouble())

    fun setZoom(newZoom: Double, min: Double = minZoom, max: Double = maxZoom) {
        zoom = newZoom
        zoomAnimator.set(zoom)
        minZoom = min
        maxZoom = max
    }

    fun updateTransform() {
        translationBounds?.clampToBounds(translation)

        val z = zoomAnimator.actual
        val vr = vertRotAnimator.actual
        val hr = horiRotAnimator.actual
        matrixTransform.setIdentity()
        if (isApplyTranslation) {
            matrixTransform.translate(translation)
        }
        matrixTransform.scale(z)
        matrixTransform.rotate(vr.deg, verticalAxis)
        matrixTransform.rotate(hr.deg, horizontalAxis)
    }

    private fun doCamTransform(view: RenderPass.View) {
        if (dragMethod == DragMethod.PAN) {
            if (dragStart) {
                dragStart = false
                panMethod.startPan(view, ptrPos)
                pointerHitStart.set(pointerHit)
                panStartTranslation.set(translation)

                // stop any ongoing smooth motion, as we start a new one
                stopSmoothMotion()

            } else {
                tmpVec1.set(panMethod.computePan(view, ptrPos))
                parent?.toLocalCoords(tmpVec1)
                translation.set(panStartTranslation).add(tmpVec1)
            }
        } else {
            pointerHit.set(view.camera.globalLookAt)
        }

        if (!deltaScroll.isFuzzyZero()) {
            zoom *= 1f - deltaScroll / 10f
            deltaScroll = 0f
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
        val z = zoomAnimator.animate(Time.deltaT)
        if (!isFuzzyEqual(oldZ, z) && zoomMethod == ZoomMethod.ZOOM_TRANSLATE) {
            panMethod.startPan(view, ptrPos)
            if (panMethod.computePanPoint(view, ptrPos, pointerHit)) {
                computeZoomTranslationPerspective(view.camera, oldZ, z)
            }
        }

        vertRotAnimator.animate(Time.deltaT)
        horiRotAnimator.animate(Time.deltaT)
        updateTransform()
    }

    /**
     * Computes the required camera translation so that the camera zooms to the point under the pointer (only works
     * with perspective cameras)
     */
    protected open fun computeZoomTranslationPerspective(camera: Camera, oldZoom: Double, newZoom: Double) {
        // tmpVec1 = zoomed pos on pointer ray
        val s = newZoom / oldZoom
        camera.dataD.globalPos.subtract(pointerHit, tmpVec1).mul(s).add(pointerHit)
        // tmpVec2 = zoomed pos on view center ray
        camera.dataD.globalPos.subtract(camera.dataD.globalLookAt, tmpVec2).mul(s)
            .add(camera.globalLookAt)
        tmpVec1.subtract(tmpVec2)
        parent?.toLocalCoords(tmpVec1, 0.0)
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

    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        if (!isVisible) {
            return
        }

        val dragPtr = pointerState.primaryPointer
        if (dragPtr.isConsumed()) {
            deltaPos.set(Vec2d.ZERO)
            deltaScroll = 0f
            return
        }

        if (isInfiniteDragCursor) {
            if (dragPtr.isDrag && dragMethod != DragMethod.NONE && PointerInput.cursorMode != CursorMode.LOCKED) {
                PointerInput.cursorMode = CursorMode.LOCKED
            } else if (prevDragMethod != DragMethod.NONE && dragMethod == DragMethod.NONE) {
                PointerInput.cursorMode = CursorMode.NORMAL
            }
        }

        prevDragMethod = dragMethod
        if (dragPtr.buttonEventMask != 0 || dragPtr.buttonMask != prevButtonMask) {
            dragMethod = when {
                dragPtr.isLeftButtonDown -> leftDragMethod
                dragPtr.isRightButtonDown -> rightDragMethod
                dragPtr.isMiddleButtonDown -> middleDragMethod
                else -> DragMethod.NONE
            }
            dragStart = dragMethod != DragMethod.NONE
        }

        prevButtonMask = dragPtr.buttonMask
        ptrPos.set(dragPtr.pos)
        deltaPos.set(dragPtr.delta).mul(1f / dragPtr.windowScale)
        deltaScroll = dragPtr.scroll.y
        if (isConsumingPtr) {
            dragPtr.consume()
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
    private val tmpVec3d = MutableVec3d()

    private val startPtrPos = MutableVec2f()
    private val startPanPos = MutableVec3d()
    private val panPos = MutableVec3d()
    private val pan = MutableVec3d()

    var panDecay = 50.0

    open fun startPan(view: RenderPass.View, ptrPos: Vec2f) {
        startPtrPos.set(ptrPos)
        pan.set(Vec3f.ZERO)
    }

    open fun computePan(view: RenderPass.View, ptrPos: Vec2f): Vec3d {
        if (computePanPoint(view, startPtrPos, startPanPos) && computePanPoint(view, ptrPos, panPos)) {
            tmpVec3d.set(startPanPos).subtract(panPos)
            if (panDecay > 0.0) {
                pan.expDecay(tmpVec3d, panDecay)
            } else {
                pan.set(tmpVec3d)
            }
        }
        return pan
    }

    abstract fun computePanPoint(view: RenderPass.View, ptrPos: Vec2f, result: MutableVec3d): Boolean
}

class CameraOrthogonalPan : PanBase() {
    private val panPlane = PlaneD()
    private val pointerRay = RayD()

    override fun computePanPoint(view: RenderPass.View, ptrPos: Vec2f, result: MutableVec3d): Boolean {
        panPlane.p.set(view.camera.globalLookAt)
        panPlane.n.set(view.camera.globalLookDir)
        val ok = view.camera.computePickRay(pointerRay, ptrPos.x, ptrPos.y, view.viewport)
        return ok && panPlane.intersectionPoint(pointerRay, result)
    }
}

class FixedPlanePan(planeNormal: Vec3f) : PanBase() {
    private val panPlane = PlaneD()
    private val pointerRay = RayD()

    private val prevPan = MutableVec3d()
    private val deltaPan = MutableVec3d()
    private val panLimited = MutableVec3d()

    init {
        panPlane.n.set(planeNormal)
    }

    override fun startPan(view: RenderPass.View, ptrPos: Vec2f) {
        super.startPan(view, ptrPos)
        prevPan.set(Vec3f.ZERO)
    }

    override fun computePanPoint(view: RenderPass.View, ptrPos: Vec2f, result: MutableVec3d): Boolean {
        panPlane.p.set(view.camera.globalLookAt)
        val ok = view.camera.computePickRay(pointerRay, ptrPos.x, ptrPos.y, view.viewport)
        if (!ok || abs(pointerRay.direction dot panPlane.n) < 0.01) {
            return false
        }
        return panPlane.intersectionPoint(pointerRay, result)
    }

    override fun computePan(view: RenderPass.View, ptrPos: Vec2f): Vec3d {
        panLimited.set(super.computePan(view, ptrPos))
        deltaPan.set(panLimited).subtract(prevPan)

        // limit panning speed
        val tLen = deltaPan.length()
        if (tLen > view.camera.globalRange * 0.5f) {
            deltaPan.mul(view.camera.globalRange * 0.5f / tLen)
            panLimited.set(prevPan).add(deltaPan)
        }
        prevPan.set(panLimited)

        return panLimited
    }
}

fun xPlanePan() = FixedPlanePan(Vec3f.X_AXIS)
fun yPlanePan() = FixedPlanePan(Vec3f.Y_AXIS)
fun zPlanePan() = FixedPlanePan(Vec3f.Z_AXIS)
