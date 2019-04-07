package de.fabmax.kool.util.serialization

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import kotlin.math.abs

/**
 * Based on:
 * Zina H. Cigolle, Sam Donow, Daniel Evangelakos, Michael Mara, Morgan McGuire, and Quirin Meyer
 * Survey of Efficient Representations for Independent Unit Vectors, Journal of Computer Graphics Techniques (JCGT), vol. 3, no. 2, 1-30, 2014
 * Available online http://jcgt.org/published/0003/02/01/
 */
object NormalOctCoding {

    private fun signNotZero(x: Float, y: Float): Vec2f {
        return when {
            x >= 0f && y >= 0f -> SIGN_PP
            x >= 0f && y < 0f -> SIGN_PN
            x < 0f && y >= 0f -> SIGN_NP
            else -> SIGN_NN
        }
    }

    fun encodeNormalToOct(normal: Vec3f, result: MutableVec2f): MutableVec2f {
        val f = 1f / (abs(normal.x) + abs(normal.y) + abs(normal.z))
        result.set(normal.x * f, normal.y * f)
        if (normal.z <= 0f) {
            val s = signNotZero(result.x, result.y)
            val x = (1f - abs(result.y)) * s.x
            val y = (1f - abs(result.x)) * s.y
            result.set(x, y)
        }
        return result
    }

    fun decodeOctToNormal(x: Int, y: Int, bits: Int, result: MutableVec3f): MutableVec3f {
        val f = 1f / ((1 shl bits) - 1)
        return NormalOctCoding.decodeOctToNormal(x * f, y * f, result)
    }

    fun decodeOctToNormal(oct: Vec2f, result: MutableVec3f): MutableVec3f = NormalOctCoding.decodeOctToNormal(oct.x, oct.y, result)

    fun decodeOctToNormal(x: Float, y: Float, result: MutableVec3f): MutableVec3f {
        result.set(x, y, 1f - abs(x) - abs(y))
        if (result.z < 0) {
            val s = signNotZero(result.x, result.y)
            val rx = (1f - abs(result.y)) * s.x
            val ry = (1f - abs(result.x)) * s.y
            result.x = rx
            result.y = ry
        }
        return result.norm()
    }

    private val SIGN_PP = Vec2f(1f, 1f)
    private val SIGN_PN = Vec2f(1f, -1f)
    private val SIGN_NP = Vec2f(-1f, 1f)
    private val SIGN_NN = Vec2f(-1f, -1f)
}