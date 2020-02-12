package de.fabmax.kool.pipeline

import de.fabmax.kool.util.MixedBuffer

class Std140Layout(uniforms: List<Uniform<*>>) {

    private val elements = mutableListOf<Element>()
    val size: Int

    init {
        var start = 0
        for (u in uniforms) {
            val e = Element(u, start)
            elements += e
            start += u.size + e.padding
        }
        size = start
    }

    fun putTo(buf: MixedBuffer) {
        for (i in elements.indices) {
            elements[i].putTo(buf)
        }
    }

    private class Element(val uniform: Uniform<*>, start: Int) {
        val offset: Int
        val padding: Int

        init {
            // todo: matrices, ...
            val baseAlign = when (uniform.typeSize) {
                4 -> 4          // scalar
                8 -> 8          // 2-component vector
                else -> 16      // 3- and 4-component vector
            }
            offset = ((start + baseAlign - 1) / baseAlign) * baseAlign
            padding = offset - start
        }

        fun putTo(buf: MixedBuffer) {
            for (i in 0 until padding) {
                buf.putUint8(0)
            }
            uniform.putTo(buf)
        }
    }
}