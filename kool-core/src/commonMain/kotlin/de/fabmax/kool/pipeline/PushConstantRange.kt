package de.fabmax.kool.pipeline

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.copy
import de.fabmax.kool.util.createFloat32Buffer

class PushConstantRange private constructor(builder: Builder, val longHash: Long, val pushConstants: List<Uniform<*>>) {

    val name = builder.name
    val stages = builder.stages.copy()

    /**
     * Overall size of buffer in bytes (i.e. all containing uniforms)
     */
    val size = pushConstants.sumBy { it.size }

    // fixme: push constants can be all kinds of types, not only floats...
    private val buffer = createFloat32Buffer(size / 4)

    val onUpdate: ((PushConstantRange, DrawCommand) -> Unit) ? = builder.onUpdate

    fun toBuffer(): Float32Buffer {
        for (i in pushConstants.indices) {
            pushConstants[i].putTo(buffer)
        }
        buffer.flip()
        return buffer
    }

    class Builder {
        var name = "pushConstants"
        val stages = mutableSetOf<Stage>()

        val pushConstants = mutableListOf<() -> Uniform<*>>()
        var onUpdate: ((PushConstantRange, DrawCommand) -> Unit) ? = null

        operator fun (() -> Uniform<*>).unaryPlus() {
            pushConstants.add(this)
        }

        fun create(): PushConstantRange {
            val pushConstants = List(pushConstants.size) { pushConstants[it]() }
            var hash = name.hashCode().toLong()
            stages.forEach {
                hash = (hash * 71023L) + it.hashCode().toLong()
            }
            pushConstants.forEach {
                hash = (hash * 71023L) + it::class.hashCode().toLong()
                hash = (hash * 71023L) + it.name.hashCode().toLong()
            }
            return PushConstantRange(this, hash, pushConstants)
        }
    }
}