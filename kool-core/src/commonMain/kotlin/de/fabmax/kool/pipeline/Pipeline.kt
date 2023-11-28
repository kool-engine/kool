package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.drawqueue.DrawCommand

/**
 * Graphics pipeline class. Also includes rasterizing options like [cullMethod], [blendMode], etc. In contrast
 * to traditional OpenGL, these options cannot be changed after the pipeline is created (this is in line with
 * how modern graphics APIs, like Vulkan, work).
 */
class Pipeline private constructor(builder: Builder) : PipelineBase(builder) {

    val cullMethod: CullMethod = builder.pipelineConfig.cullMethod
    val blendMode: BlendMode = builder.pipelineConfig.blendMode
    val depthCompareOp: DepthCompareOp = builder.pipelineConfig.depthTest
    val autoReverseDepthFunc: Boolean = builder.pipelineConfig.autoReverseDepthFunc
    val isWriteDepth: Boolean = builder.pipelineConfig.isWriteDepth
    val lineWidth: Float = builder.pipelineConfig.lineWidth

    val vertexLayout: VertexLayout

    val shaderCode: ShaderCode
    val onUpdate = mutableListOf<(DrawCommand) -> Unit>()

    init {
        hash += cullMethod
        hash += depthCompareOp
        hash += isWriteDepth
        hash += lineWidth

        vertexLayout = builder.vertexLayout.create()
        hash += vertexLayout.hash

        shaderCode = builder.shaderCodeGenerator(this)
        hash += shaderCode.hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pipeline) return false
        return pipelineHash == other.pipelineHash
    }

    override fun hashCode(): Int {
        return pipelineHash.hashCode()
    }

    class Builder : PipelineBase.Builder() {
        val pipelineConfig = PipelineConfig()
        val vertexLayout = VertexLayout.Builder()

        lateinit var shaderCodeGenerator: (Pipeline) -> ShaderCode

        override fun create(): Pipeline {
            return Pipeline(this)
        }
    }
}

enum class BlendMode {
    DISABLED,
    BLEND_ADDITIVE,
    BLEND_MULTIPLY_ALPHA,
    BLEND_PREMULTIPLIED_ALPHA
}

enum class DepthCompareOp {
    DISABLED,
    ALWAYS,
    NEVER,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,
    EQUAL,
    NOT_EQUAL
}

enum class CullMethod(val isFrontVisible: Boolean, val isBackVisible: Boolean) {
    CULL_BACK_FACES(true, false),
    CULL_FRONT_FACES(false, true),
    NO_CULLING(true, true)
}