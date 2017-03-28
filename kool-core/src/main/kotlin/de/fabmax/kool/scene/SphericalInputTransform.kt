package de.fabmax.kool.scene

import de.fabmax.kool.InputManager
import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.MutableVec2f
import de.fabmax.kool.util.Vec2f
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.isEqual

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

    var verticalAxis = Vec3f.Y_AXIS
    var horizontalAxis = Vec3f.X_AXIS

    var verticalRotation = 0f
    var horizontalRotation = 0f
    var zoom = 1f

    var minZoom = 0.1f
    var maxZoom = 10f

    private var isDragging = false
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
    override var scene: Scene?
        get() = super.scene
        set(value) {
            super.scene?.removeDragHandler(this)
            super.scene = value
            super.scene?.registerDragHandler(this)
        }

    init {
        smoothness = 0.1f
    }

    fun setRotation(vertical: Float, horizontal: Float) {
        animRotV.set(vertical)
        animRotH.set(horizontal)
        verticalRotation = vertical
        horizontalRotation = horizontal
    }

    override fun render(ctx: RenderContext) {
        if (!Math.isZero(deltaScroll)) {
            zoom *= 1f + deltaScroll / 10f
            zoom = Math.clamp(zoom, minZoom, maxZoom)
            deltaScroll = 0f
        }

        if (!isEqual(deltaPos, Vec2f.ZERO)) {
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
        scale(z, z, z)
        rotate(animRotV.animate(ctx.deltaT), verticalAxis)
        rotate(animRotH.animate(ctx.deltaT), horizontalAxis)

        super.render(ctx)
    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>): Int {
        if (dragPtrs.size == 1 && dragPtrs[0].isValid) {
            if (dragPtrs[0].isLeftButtonEvent) {
                isDragging = dragPtrs[0].isLeftButtonDown
            }
            if (isDragging && dragPtrs[0].isLeftButtonDown) {
                deltaPos.set(dragPtrs[0].deltaX, dragPtrs[0].deltaY)
            }
            if (dragPtrs[0].deltaScroll != 0f) {
                deltaScroll = dragPtrs[0].deltaScroll
            }
        } else {
            isDragging = false
        }
        // let other drag handlers do their job
        return 0
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