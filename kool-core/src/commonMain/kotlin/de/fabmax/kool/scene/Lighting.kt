package de.fabmax.kool.scene

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor

/**
 * @author fabmax
 */
class Lighting() {
    val lights = mutableListOf(Light().setDirectional(Vec3f(-1f)).setColor(Color.WHITE, 1f))

    fun singleLight(block: Light.() -> Unit) {
        lights.clear()
        lights += Light().apply(block)
    }
}

class Light {
    val color = MutableColor(Color.WHITE)

    var type = Type.DIRECTIONAL
    val direction = MutableVec3f(1f)
    val position = MutableVec3f()
    var spotAngle = 60f

    fun setColor(color: Color, intensity: Float): Light {
        this.color.set(color)
        this.color.a = intensity
        return this
    }

    fun setDirectional(dir: Vec3f): Light {
        type = Type.DIRECTIONAL
        direction.set(dir)

        position.set(Vec3f.ZERO)
        spotAngle = 0f
        return this
    }

    fun setPoint(pos: Vec3f): Light {
        type = Type.POINT
        position.set(pos)

        direction.set(Vec3f.ZERO)
        spotAngle = 0f
        return this
    }

    fun setSpot(pos: Vec3f, dir: Vec3f, angle: Float): Light {
        type = Type.SPOT
        position.set(pos)
        direction.set(dir)
        spotAngle = angle
        return this
    }

    enum class Type(val encoded: Float) {
        DIRECTIONAL(0f),
        POINT(1f),
        SPOT(2f)
    }
}
