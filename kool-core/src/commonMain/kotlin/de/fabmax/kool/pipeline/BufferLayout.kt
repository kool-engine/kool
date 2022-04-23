package de.fabmax.kool.pipeline

import de.fabmax.kool.util.MixedBuffer

abstract class BufferLayout(val uniforms: List<Uniform<*>>) {
    abstract val offsets: IntArray
    abstract val size: Int

    /**
     * Puts the supplied list of uniforms into the given [MixedBuffer]. The supplied uniforms must have the same types
     * and order than the uniforms, this BufferLayout was created for.
     */
    open fun putToBuffer(uniforms: List<Uniform<*>>, target: MixedBuffer) {
        check(uniforms.size == offsets.size) { "Supplied list of uniforms does not match this BufferLayout" }
        check(target.capacity >= size) { "Supplied target buffer is too small" }

        // for performance reasons, we do not check if the given list of uniforms matches types
        // and order of this.uniforms

        for (i in uniforms.indices) {
            target.position = offsets[i]
            uniforms[i].putToBuffer(target)
        }
    }
}

class ExternalBufferLayout(uniforms: List<Uniform<*>>, override val offsets: IntArray, override val size: Int)
    : BufferLayout(uniforms) {
    init {
        check(offsets.size == uniforms.size) { "Given lists of uniforms and offsets mismatch in length" }
    }
}

class Std140BufferLayout(uniforms: List<Uniform<*>>) : BufferLayout(uniforms) {
    override val offsets = IntArray(uniforms.size)
    override val size: Int

    init {
        var pos = 0
        uniforms.forEachIndexed { i, u ->
            // determine alignment of current uniform in basic machine units (aka bytes)
            val alignment = when (u) {
                is Uniform1f -> 4
                is Uniform1i -> 4
                is Uniform2f -> 8
                is Uniform2i -> 8
                // everything else, including all array and matrix types, has vec4 alignment
                else -> 16
            }
            offsets[i] = ((pos + alignment - 1) / alignment) * alignment
            pos = offsets[i] + u.size
        }

        size = pos
    }

}

