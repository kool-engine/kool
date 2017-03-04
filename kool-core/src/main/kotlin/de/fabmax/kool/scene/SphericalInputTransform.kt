package de.fabmax.kool.scene

import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.clamp
import de.fabmax.kool.util.isZero

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

open class SphericalInputTransform(name: String? = null) : TransformGroup(name) {

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

    var smoothness: Float = 0f
        set(value) {
            field = value
            if (!isZero(value)) {
                stiffness = 10.0f / value
                damping = 2f * Math.sqrt(stiffness.toDouble()).toFloat()
            }
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
        val pointer = ctx.inputHandler.primaryPointer
        if (pointer.isValid) {
            if (!isZero(pointer.deltaScroll)) {
                zoom *= 1f + pointer.deltaScroll / 10f
                zoom = clamp(zoom, minZoom, maxZoom)
            }

            if (pointer.isValid && pointer.isLeftButtonDown) {
                verticalRotation -= pointer.deltaX / 3
                horizontalRotation -= pointer.deltaY / 3
                horizontalRotation = clamp(horizontalRotation, -90f, 90f)
            }

            animRotV.desired = verticalRotation
            animRotH.desired = horizontalRotation
            animZoom.desired = zoom
        }

        val z = animZoom.animate(ctx.deltaT)
        setIdentity()
        scale(z, z, z)
        rotate(animRotV.animate(ctx.deltaT), verticalAxis)
        rotate(animRotH.animate(ctx.deltaT), horizontalAxis)

        super.render(ctx)
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
            if (isZero(smoothness) || deltaT > 0.2f) {
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