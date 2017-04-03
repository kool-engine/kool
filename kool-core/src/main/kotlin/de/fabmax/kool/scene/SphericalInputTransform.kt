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

    private val animTransX = AnimatedVal(0f)
    private val animTransY = AnimatedVal(0f)
    private val animTransZ = AnimatedVal(0f)

    var leftDragMethod = DragMethod.ROTATE
    var middleDragMethod = DragMethod.NONE
    var rightDragMethod = DragMethod.PAN

    var verticalAxis = Vec3f.Y_AXIS
    var horizontalAxis = Vec3f.X_AXIS
    private val horizontalAxis2 = MutableVec3f()

    var verticalRotation = 0f
    var horizontalRotation = 0f
    var zoom = 1f
    val translation = MutableVec3f()

    var minZoom = 0.1f
    var maxZoom = 10f

    val panPlane = Plane()

    private var dragMethod = DragMethod.NONE
    private var dragStart = false

    private val pointerHitStart = MutableVec3f()
    private val pointerHit = MutableVec3f()
    private val pointerRay = Ray()
    private val tmpVec = MutableVec3f()

    private val deltaPos = MutableVec2f()
    private var deltaScroll = 0f


    var smoothness: Float = 0f
        set(value) {
            field = value
            if (!Math.isZero(value)) {
                stiffness = 10.0f / value
                damping = 2f * Math.sqrt(stiffness.toDouble()).toFloat()
            }
        }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        oldScene?.removeDragHandler(this)
        newScene?.registerDragHandler(this)
    }

    init {
        smoothness = 0.1f
        panPlane.p.set(Vec3f.ZERO)
        panPlane.n.set(Vec3f.Y_AXIS)
    }

    fun setRotation(vertical: Float, horizontal: Float) {
        animRotV.set(vertical)
        animRotH.set(horizontal)
        verticalRotation = vertical
        horizontalRotation = horizontal
    }

    override fun render(ctx: RenderContext) {
        val scene = this.scene
        if (scene != null && scene.camera.computePickRay(pointerRay, ctx.inputMgr.primaryPointer, ctx) &&
                panPlane.intersectionPoint(pointerHit, pointerRay)) {

            scene.camera.getGlobalLookDirection(panPlane.n)
            scene.camera.position.subtract(tmpVec, scene.camera.lookAt)
            val nLen = panPlane.n.length()
            val scaleReciproc = tmpVec.length() / nLen
            panPlane.n.scale(1f / nLen)

            if (dragStart) {
                dragStart = false
                pointerHitStart.set(pointerHit)

            } else if (dragMethod == DragMethod.PAN) {
                tmpVec.set(pointerHitStart).subtract(pointerHit).scale(scaleReciproc)
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

        animTransX.desired = translation.x
        animTransY.desired = translation.y
        animTransZ.desired = translation.z

        val z = animZoom.animate(ctx.deltaT)
        setIdentity()

        //translate(animTransX.animate(ctx.deltaT), animTransY.animate(ctx.deltaT), animTransZ.animate(ctx.deltaT))
        translate(translation)
        scale(z, z, z)

        rotate(animRotV.animate(ctx.deltaT), verticalAxis)
        rotate(animRotH.animate(ctx.deltaT), horizontalAxis)


        super.render(ctx)
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