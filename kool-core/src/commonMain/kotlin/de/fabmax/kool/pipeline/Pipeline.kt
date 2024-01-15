package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.drawqueue.DrawCommand

/**
 * Graphics pipeline class. Also includes rasterizing options like [cullMethod], [blendMode], etc. In contrast
 * to traditional OpenGL, these options cannot be changed after the pipeline is created (this is in line with
 * how modern graphics APIs, like Vulkan, work).
 */
class Pipeline(
    name: String,
    val pipelineConfig: PipelineConfig,
    val vertexLayout: VertexLayout,
    bindGroupLayouts: List<BindGroupLayout>,
    shaderCodeGenerator: (Pipeline) -> ShaderCode
) :
    PipelineBase(name, bindGroupLayouts)
{

    val cullMethod: CullMethod get() = pipelineConfig.cullMethod
    val blendMode: BlendMode get() = pipelineConfig.blendMode
    val depthCompareOp: DepthCompareOp get() = pipelineConfig.depthTest
    val autoReverseDepthFunc: Boolean get() = pipelineConfig.autoReverseDepthFunc
    val isWriteDepth: Boolean get() = pipelineConfig.isWriteDepth
    val lineWidth: Float get() = pipelineConfig.lineWidth

    override val shaderCode: ShaderCode = shaderCodeGenerator(this)

    val onUpdate = mutableListOf<(DrawCommand) -> Unit>()

    init {
        hash += cullMethod
        hash += depthCompareOp
        hash += isWriteDepth
        hash += lineWidth

        hash += vertexLayout.hash
        hash += shaderCode.hash
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