package de.fabmax.kool.pipeline

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.copy

class DescriptorLayout private constructor(val descriptors: List<Descriptor>) {

    fun getUniformBuffer(name: String): UniformBuffer {
        return descriptors.first { it is UniformBuffer && it.name == name } as UniformBuffer
    }

    fun getTextureSampler(name: String): TextureSampler {
        return descriptors.first { it is TextureSampler && it.name == name } as TextureSampler
    }

    class Builder {
        val descriptors = mutableListOf<Descriptor.Builder<*>>()

        operator fun <T: Descriptor> Descriptor.Builder<T>.unaryPlus() {
            descriptors += this
        }

        fun build(): DescriptorLayout {
            return DescriptorLayout(List(descriptors.size) { descriptors[it].build() })
        }
    }
}

abstract class Descriptor(builder: Builder<*>, val type: DescriptorType) {
    val name: String = builder.name
    val stages: Set<Stage> = builder.stages.copy()

    abstract class Builder<T : Descriptor> {
        var name = ""
        val stages = mutableSetOf<Stage>()

        abstract fun build(): T
    }
}

class TextureSampler private constructor(builder: Builder) :
        Descriptor(builder, DescriptorType.IMAGE_SAMPLER) {

    val onUpdate: ((TextureSampler, DrawCommand) -> Unit) ? = builder.onUpdate
    var texture: Texture? = null

    class Builder : Descriptor.Builder<TextureSampler>() {
        var onUpdate: ((TextureSampler, DrawCommand) -> Unit) ? = null

        init {
            name = "texture"
        }

        override fun build(): TextureSampler {
            return TextureSampler(this)
        }
    }
}

class UniformBuffer private constructor(builder: Builder) : Descriptor(builder, DescriptorType.UNIFORM_BUFFER) {

    val onUpdate: ((UniformBuffer, DrawCommand) -> Unit) ? = builder.onUpdate
    val uniforms: List<Uniform<*>> = List(builder.uniforms.size) { builder.uniforms[it]() }

    /**
     * Overall size of buffer (i.e. all containing uniforms)
     */
    val size = uniforms.sumBy { it.size }

    fun putTo(buffer: Float32Buffer) {
        // fixme: ensure proper alignment! also might be platform specific...
        for (i in uniforms.indices) {
            uniforms[i].putTo(buffer)
        }
        buffer.flip()
    }

    fun updateMvp(modelIdx: Int, viewIdx: Int, projIdx: Int, cmd: DrawCommand) {
        (uniforms[modelIdx] as UniformMat4f).value.set(cmd.modelMat)
        (uniforms[viewIdx] as UniformMat4f).value.set(cmd.viewMat)
        (uniforms[projIdx] as UniformMat4f).apply {
            value.set(cmd.projMat)

            // compensate flipped y coordinate in clip space...
            // this also flips the triangle direction, therefore front-faces are counter-clockwise
            //   -> rasterizer property, createGraphicsPipeline()
            value[1, 1] *= -1f
        }
    }

    class Builder : Descriptor.Builder<UniformBuffer>() {
        val uniforms = mutableListOf<() -> Uniform<*>>()
        var onUpdate: ((UniformBuffer, DrawCommand) -> Unit) ? = null

        init {
            name = "ubo"
        }

        operator fun (() -> Uniform<*>).unaryPlus() {
            uniforms.add(this)
        }

        override fun build(): UniformBuffer {
            return UniformBuffer(this)
        }
    }

    companion object {
        fun uboMvp() = UniformBuffer.Builder().apply {
            stages += Stage.VERTEX_SHADER
            +{ UniformMat4f("model") }
            +{ UniformMat4f("view") }
            +{ UniformMat4f("proj") }

            onUpdate = { ubo, cmd ->
                ubo.updateMvp(0, 1, 2, cmd)
            }
        }
    }
}


enum class DescriptorType {
    IMAGE_SAMPLER,
    UNIFORM_BUFFER
}

enum class Stage {
    VERTEX_SHADER,
    GEOMETRY_SHADER,
    FRAGMENT_SHADER
}
