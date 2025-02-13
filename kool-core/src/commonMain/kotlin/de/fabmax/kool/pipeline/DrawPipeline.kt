package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.LongHash

/**
 * Graphics pipeline class. Also includes rasterizing options like [cullMethod], [blendMode], etc. In contrast
 * to traditional OpenGL, these options cannot be changed after the pipeline is created (this is in line with
 * how modern graphics APIs, like Vulkan, work).
 */
class DrawPipeline(
    name: String,
    val pipelineConfig: PipelineConfig,
    val vertexLayout: VertexLayout,
    bindGroupLayouts: BindGroupLayouts,
    shaderCodeGenerator: (DrawPipeline) -> ShaderCode
) : PipelineBase(name, bindGroupLayouts) {
    override val pipelineHash: LongHash

    val cullMethod: CullMethod get() = pipelineConfig.cullMethod
    val blendMode: BlendMode get() = pipelineConfig.blendMode
    val depthCompareOp: DepthCompareOp get() = pipelineConfig.depthTest
    val autoReverseDepthFunc: Boolean get() = pipelineConfig.autoReverseDepthFunc
    val isWriteDepth: Boolean get() = pipelineConfig.isWriteDepth
    val lineWidth: Float get() = pipelineConfig.lineWidth

    override val shaderCode: ShaderCode = shaderCodeGenerator(this)

    private val onUpdate: BufferedList<(DrawCommand) -> Unit> = BufferedList()

    init {
        pipelineHashBuilder += cullMethod
        pipelineHashBuilder += depthCompareOp
        pipelineHashBuilder += isWriteDepth
        pipelineHashBuilder += lineWidth

        pipelineHashBuilder += vertexLayout.hash
        pipelineHashBuilder += shaderCode.hash

        pipelineHash = pipelineHashBuilder.build()
    }

    override fun toString(): String {
        return "DrawPipeline:\"$name\""
    }

    fun update(cmd: DrawCommand) {
        onUpdate.update()
        for (i in onUpdate.indices) {
            onUpdate[i].invoke(cmd)
        }
    }

    fun onUpdate(block: (DrawCommand) -> Unit) {
        onUpdate += block
    }

    fun removeUser(mesh: Mesh) {
        pipelineBackend?.removeUser(mesh)
    }
}

enum class BlendMode {
    DISABLED,
    BLEND_ADDITIVE,
    BLEND_MULTIPLY_ALPHA,
    BLEND_PREMULTIPLIED_ALPHA
}

enum class DepthCompareOp {
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