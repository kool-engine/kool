package de.fabmax.kool.audio

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */
class Wave(val tableSize: Int, generator: (Float) -> Float) {

    private val table = FloatArray(tableSize)

    init {
        for (i in 0..tableSize-1) {
            table[i] = generator(i / tableSize.toFloat())
        }
    }

    operator fun get(index: Double): Float {
        return table[((index % 1.0) * tableSize).toInt()]
    }

    companion object {
        const val DEFAULT_TABLE_SIZE = 2048

        val SINE = Wave(DEFAULT_TABLE_SIZE) { p -> Math.sin(p * Math.PI.toFloat() * 2) }
        val SAW = Wave(DEFAULT_TABLE_SIZE) { p -> -2f * (p - Math.round(p)) }
        val RAMP = Wave(DEFAULT_TABLE_SIZE) { p -> 2f * (p - Math.round(p)) }
        val TRIANGLE = Wave(DEFAULT_TABLE_SIZE) { p -> 1f - 4f * Math.abs(Math.round(p) - p) }
        val SQUARE = Wave(DEFAULT_TABLE_SIZE) { p -> if (p < 0.5f) 1f else -1f }
    }
}