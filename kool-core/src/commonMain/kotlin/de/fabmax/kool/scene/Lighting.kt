package de.fabmax.kool.scene

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor
import kotlin.math.cos

/**
 * @author fabmax
 */
class Lighting {
    val lights = mutableListOf(Light().setDirectional(Vec3f(-0.8f, -1.2f, -1f)).setColor(Color.WHITE, 1f))

    fun singleLight(block: Light.() -> Unit) {
        lights.clear()
        lights += Light().apply(block)
    }
}

class Light {
    val color = MutableColor(Color.WHITE)

    var type = Type.DIRECTIONAL
    val direction = MutableVec3f(0f, 1f, 0f)
    val position = MutableVec3f()
    var spotAngle = 60f
    var spotAngleInnerFac = 0.5f

    var isEncodingDirty = true
    val encodedPosition = MutableVec4f()
    val encodedDirection = MutableVec4f()
    val encodedColor = MutableVec4f()

    fun updateEncodedValues() {
        if (!isEncodingDirty) {
            return
        }
        isEncodingDirty = false

        encodedColor.set(
            color.r * color.a,
            color.g * color.a,
            color.b * color.a,
            1f
        )
        when (type) {
            Type.DIRECTIONAL -> {
                // directional light direction is stored in position instead of direction, for easier decoding
                encodedPosition.set(direction, type.encoded)
            }
            Type.POINT -> {
                encodedPosition.set(position, type.encoded)
            }
            Type.SPOT -> {
                encodedPosition.set(position, type.encoded)
                encodedDirection.set(direction, cos((spotAngle / 2).toRad()))
                encodedColor.w = spotAngleInnerFac
            }
        }
    }

    fun setColor(color: Color, intensity: Float): Light {
        this.color.set(color)
        this.color.a = intensity
        isEncodingDirty = true
        return this
    }

    fun setDirectional(dir: Vec3f): Light {
        type = Type.DIRECTIONAL
        direction.set(dir).norm()

        position.set(Vec3f.ZERO)
        spotAngle = 0f
        isEncodingDirty = true
        return this
    }

    fun setPoint(pos: Vec3f): Light {
        type = Type.POINT
        position.set(pos)

        direction.set(Vec3f.ZERO)
        spotAngle = 0f
        isEncodingDirty = true
        return this
    }

    fun setSpot(pos: Vec3f, dir: Vec3f, angle: Float, innerAngleFac: Float = 0.5f): Light {
        type = Type.SPOT
        position.set(pos)
        direction.set(dir).norm()
        spotAngle = angle
        spotAngleInnerFac = innerAngleFac
        isEncodingDirty = true
        return this
    }

    enum class Type(val encoded: Float) {
        DIRECTIONAL(0f),
        POINT(1f),
        SPOT(2f)
    }
}
