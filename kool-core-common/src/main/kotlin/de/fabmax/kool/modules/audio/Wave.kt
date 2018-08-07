package de.fabmax.kool.modules.audio

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sin

/**
 * @author fabmax
 */
class Wave(val tableSize: Int, generator: (Float) -> Float) {

    private val table = FloatArray(tableSize)

    init {
        for (i in 0 until tableSize) {
            table[i] = generator(i / tableSize.toFloat())
        }
    }

    operator fun get(index: Float): Float {
        return table[(index * tableSize).toInt() % tableSize]
    }

    companion object {
        const val DEFAULT_TABLE_SIZE = 2048

        val SINE = Wave(DEFAULT_TABLE_SIZE) { p -> sin(p * PI.toFloat() * 2) }
        val SAW = Wave(DEFAULT_TABLE_SIZE) { p -> -2f * (p - round(p)) }
        val RAMP = Wave(DEFAULT_TABLE_SIZE) { p -> 2f * (p - round(p)) }
        val TRIANGLE = Wave(DEFAULT_TABLE_SIZE) { p -> 1f - 4f * abs(round(p) - p) }
        val SQUARE = Wave(DEFAULT_TABLE_SIZE) { p -> if (p < 0.5f) 1f else -1f }
    }
}