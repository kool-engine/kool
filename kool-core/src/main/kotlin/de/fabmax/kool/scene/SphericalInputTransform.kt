package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.*

/**
 * A special kind of transform group which translates mouse input into a spherical transform. This is mainly useful
 * for camera manipulation.
 *
 * @author fabmax
 */

fun sphericalInputTransform(name: String? = null, block: SphericalInputTransform.() -> Unit): SphericalInputTransform {
    val sit = SphericalInputTransform(name)
    sit.block()
    return sit
}

open class SphericalInputTransform(name: String? = null) : TransformGroup(name), InputManager.DragHandler {

    private var stiffness = 0f
    private var damping = 0f

    private val animRotV = AnimatedVal(0f)
    private val animRotH = AnimatedVal(0f)
    private val animZoom = AnimatedVal(1f)

    var leftDragMethod = DragMethod.ROTATE
    var middleDragMethod = DragMethod.NONE
    var rightDragMethod = DragMethod.PAN

    var verticalAxis = Vec3f.Y_AXIS
    var horizontalAxis = Vec3f.X_AXIS

    var verticalRotation = 0f
    var horizontalRotation = 0f
    var zoom = 1f
    val translation = MutableVec3f()

    var minZoom = 0.1f
    var maxZoom = 10f

    //var panMethod: PanBase = CameraOrthogonalPan()
    var panMethod: PanBase = yPlanePan()

    private val panPlane = Plane()

    private var dragMethod = DragMethod.NONE
    private var dragStart = false

    private val pointerHitStart = MutableVec3f()
    private val pointerHit = MutableVec3f()
    private val tmpVec = MutableVec3f()

    private val deltaPos = MutableVec2f()
    private var deltaScroll = 0f

    var smoothness: Float = 0f
        set(value) {
            field = value
            if (!Math.isZero(value)) {
                stiffness = 50.0f / value
                damping = 2f * Math.sqrt(stiffness.toDouble()).toFloat()
            }
        }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        oldScene?.removeDragHandler(this)
        newScene?.registerDragHandler(this)
    }

    init {
        smoothness = 0.5f
        panPlane.p.set(Vec3f.ZERO)
        panPlane.n.set(Vec3f.Y_AXIS)
    }

    fun setRotation(vertical: Float, horizontal: Float) {
        animRotV.set(vertical)
        animRotH.set(horizontal)
        verticalRotation = vertical
        horizontalRotation = horizontal
    }

    fun setTranslation(x: Float, y: Float, z: Float) {
        translation.set(x, y, z)
    }

    override fun render(ctx: RenderContext) {
        val scene = this.scene
        if (scene != null && panMethod.computePanPoint(pointerHit, scene, ctx.inputMgr.primaryPointer, ctx)) {
            if (dragStart) {
                dragStart = false
                pointerHitStart.set(pointerHit)

                // stop any ongoing smooth motion, as we start a new one
                stopSmoothMotion()

            } else if (dragMethod == DragMethod.PAN) {
                val scale = Math.clamp(1 - smoothness, 0.1f, 1f)
                tmpVec.set(pointerHitStart).subtract(pointerHit).scale(scale)
                val tLen = tmpVec.length()
                if (tLen > scene.camera.globalRange * 0.5f) {
                    // limit panning speed
                    tmpVec.scale(scene.camera.globalRange * 0.5f / tLen)
                }
                translation.add(tmpVec)
            }
        }

        if (!Math.isZero(deltaScroll)) {
            zoom *= 1f + deltaScroll / 10f
            zoom = Math.clamp(zoom, minZoom, maxZoom)
            deltaScroll = 0f
        }

        if (dragMethod == DragMethod.ROTATE) {
            verticalRotation -= deltaPos.x / 3
            horizontalRotation -= deltaPos.y / 3
            horizontalRotation = Math.clamp(horizontalRotation, -90f, 90f)
            deltaPos.set(Vec2f.ZERO)
        }

        animRotV.desired = verticalRotation
        animRotH.desired = horizontalRotation
        animZoom.desired = zoom

        val z = animZoom.animate(ctx.deltaT)

        setIdentity()
        translate(translation)
        scale(z, z, z)
        rotate(animRotV.animate(ctx.deltaT), verticalAxis)
        rotate(animRotH.animate(ctx.deltaT), horizontalAxis)


        super.render(ctx)
    }

    private fun stopSmoothMotion() {
        animRotV.set(animRotV.actual)
        animRotH.set(animRotH.actual)
        animZoom.set(animZoom.actual)

        verticalRotation = animRotV.actual
        horizontalRotation = animRotH.actual
        zoom = animZoom.actual
    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>): Int {
        if (dragPtrs.size == 1 && dragPtrs[0].isValid) {
            if (dragPtrs[0].buttonEventMask != 0) {
                if (dragPtrs[0].isLeftButtonDown) {
                    dragMethod = leftDragMethod
                } else if (dragPtrs[0].isRightButtonDown) {
                    dragMethod = rightDragMethod
                } else if (dragPtrs[0].isMiddleButtonDown) {
                    dragMethod = middleDragMethod
                } else {
                    dragMethod = DragMethod.NONE
                }
                dragStart = dragMethod != DragMethod.NONE
            }

            deltaPos.set(dragPtrs[0].deltaX, dragPtrs[0].deltaY)
            deltaScroll = dragPtrs[0].deltaScroll

        } else {
            deltaPos.set(Vec2f.ZERO)
            deltaScroll = 0f
            dragMethod = DragMethod.NONE
        }
        // let other drag handlers do their job
        return 0
    }

    companion object {
        enum class DragMethod {
            NONE,
            ROTATE,
            PAN
        }
    }

    private inner class AnimatedVal(value: Float) {
        var desired = value
        var actual = value
        var speed = 0f

        fun set(value: Float) {
            desired = value
            actual = value
            speed = 0f
        }

        fun animate(deltaT: Float): Float {
            if (Math.isZero(smoothness) || deltaT > 0.2f) {
                // don't care about smoothing on low frame rates
                actual = desired
                return actual
            }

            var t = 0f
            while (t < deltaT) {
                // with js math library there is no min for Float?!
                val dt = Math.min(0.05, (deltaT - t).toDouble()).toFloat()
                t += dt + 0.001f

                val err = desired - actual
                speed += (err * stiffness - speed * damping) * dt
                actual += speed * dt
            }
            return actual
        }
    }
}

abstract class PanBase {
    abstract fun computePanPoint(result: MutableVec3f,
                                 scene: Scene, ptr: InputManager.Pointer, ctx: RenderContext): Boolean
}

class CameraOrthogonalPan : PanBase() {
    val panPlane = Plane()
    private val pointerRay = Ray()

    override fun computePanPoint(result: MutableVec3f,
                                   scene: Scene, ptr: InputManager.Pointer, ctx: RenderContext): Boolean {
        scene.camera.getGlobalLookAt(panPlane.p)
        scene.camera.getGlobalLookDirection(panPlane.n)
        return scene.camera.computePickRay(pointerRay, ctx.inputMgr.primaryPointer, ctx) &&
                panPlane.intersectionPoint(result, pointerRay)
    }
}

class FixedPlanePan(planeNormal: Vec3f) : PanBase() {
    val panPlane = Plane()
    private val pointerRay = Ray()

    override fun computePanPoint(result: MutableVec3f,
                                 scene: Scene, ptr: InputManager.Pointer, ctx: RenderContext): Boolean {
        scene.camera.getGlobalLookAt(panPlane.p)
        return scene.camera.computePickRay(pointerRay, ctx.inputMgr.primaryPointer, ctx) &&
                panPlane.intersectionPoint(result, pointerRay)
    }
}

fun xPlanePan() = FixedPlanePan(Vec3f.X_AXIS)
fun yPlanePan() = FixedPlanePan(Vec3f.Y_AXIS)
fun zPlanePan() = FixedPlanePan(Vec3f.Z_AXIS)
