package de.fabmax.kool.pipeline

import de.fabmax.kool.util.MixedBuffer

abstract class BufferLayout(val uniforms: List<Uniform<*>>) {
    abstract val uniformPositions: Array<BufferPosition>
    abstract val size: Int

    /**
     * Puts the supplied list of uniforms into the given [MixedBuffer]. The supplied uniforms must have the same types
     * and order than the uniforms, this BufferLayout was created for.
     */
    open fun putToBuffer(uniforms: List<Uniform<*>>, target: MixedBuffer) {
        check(uniforms.size == uniformPositions.size) { "Supplied list of uniforms does not match this BufferLayout" }
        check(target.capacity >= size) { "Supplied target buffer is too small" }

        // for performance reasons, we do not check if the given list of uniforms matches types
        // and order of this.uniforms

        for (i in uniforms.indices) {
            target.position = uniformPositions[i].position
            uniforms[i].putToBuffer(target, uniformPositions[i].len)
        }
    }
}

class ExternalBufferLayout(uniforms: List<Uniform<*>>, override val uniformPositions: Array<BufferPosition>, override val size: Int)
    : BufferLayout(uniforms) {
    init {
        check(uniformPositions.size == uniforms.size) { "Given lists of uniforms and offsets mismatch in length" }
    }
}

class Std140BufferLayout(uniforms: List<Uniform<*>>) : BufferLayout(uniforms) {
    override val uniformPositions: Array<BufferPosition>
    override val size: Int

    init {
        var pos = 0
        uniformPositions = Array(uniforms.size) { i ->
            val u = uniforms[i]

            // determine alignment of current uniform in basic machine units (i.e. bytes)
            val alignment = when (u) {
                is Uniform1f -> 4
                is Uniform1i -> 4
                is Uniform2f -> 8
                is Uniform2i -> 8
                // everything else, including all array and matrix types, has vec4 alignment / 16 bytes
                else -> 16
            }
            val bufPos = ((pos + alignment - 1) / alignment) * alignment
            pos = bufPos + getSize(u)

            BufferPosition(bufPos, pos - bufPos)
        }
        size = pos
    }

    companion object {
        /**
         * Returns the number of bytes used by the given uniform in a buffer with Std140 layout.
         */
        fun getSize(u: Uniform<*>): Int {
            return when (u) {
                is Uniform1f -> 4
                is Uniform2f -> 8
                is Uniform3f -> 12
                is Uniform4f -> 16
                is Uniform1fv -> 16 * u.size
                is Uniform3fv -> 16 * u.size
                is Uniform2fv -> 16 * u.size
                is Uniform4fv -> 16 * u.size

                is Uniform1i -> 4
                is Uniform2i -> 8
                is Uniform3i -> 12
                is Uniform4i -> 16
                is Uniform1iv -> 16 * u.size
                is Uniform2iv -> 16 * u.size
                is Uniform3iv -> 16 * u.size
                is Uniform4iv -> 16 * u.size

                is UniformMat3f -> 3 * 16
                is UniformMat4f -> 4 * 16
                is UniformMat3fv -> 3 * 16 * u.size
                is UniformMat4fv -> 4 * 16 * u.size
            }
        }
    }

}

data class BufferPosition(val position: Int, val len: Int)
