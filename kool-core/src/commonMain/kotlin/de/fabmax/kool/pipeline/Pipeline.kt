package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.shading.Shader
import de.fabmax.kool.scene.Mesh

class Pipeline private constructor(builder: Builder, mesh: Mesh, ctx: KoolContext) {
    /**
     * pipelineHash is used to determine pipeline equality. In contrast to standard java hashCode() a 64-bit hash is
     * used to make collisions less likely. For fast equality checks the equals() method only uses this value to
     * determine equality.
     */
    val pipelineHash: ULong
    val pipelineInstanceId = instanceId++

    val cullMethod: CullMethod = builder.cullMethod
    val depthCompareOp: DepthCompareOp = builder.depthTest
    val isWriteDepth: Boolean = builder.isWriteDepth
    val lineWidth: Float = builder.lineWidth

    val vertexLayout: VertexLayout
    val descriptorSetLayouts: List<DescriptorSetLayout>
    val pushConstantRanges: List<PushConstantRange>

    val shader: Shader
    val shaderCode: ShaderCode

    init {
        val buildCtx = BuildContext(builder)
        builder.onCreatePipeline.forEach { it(buildCtx) }
        shader = builder.shaderLoader(mesh, buildCtx, ctx)
        vertexLayout = buildCtx.vertexLayout.create()
        descriptorSetLayouts = buildCtx.descriptorSetLayouts.mapIndexed { i, b -> b.create(i) }
        pushConstantRanges = buildCtx.pushConstantRanges.map { it.create() }

        // load / generate shader code
        shaderCode = shader.generateCode(this, ctx)

        // compute pipelineHash
        var hash = cullMethod.hashCode().toULong()
        hash = (hash * 71023UL) + depthCompareOp.hashCode().toULong()
        hash = (hash * 71023UL) + isWriteDepth.hashCode().toULong()
        hash = (hash * 71023UL) + lineWidth.hashCode().toULong()
        hash = (hash * 71023UL) + vertexLayout.longHash
        hash = (hash * 71023UL) + shaderCode.longHash
        descriptorSetLayouts.forEach { hash = (hash * 71023UL) + it.longHash }
        pushConstantRanges.forEach { hash = (hash * 71023UL) + it.longHash }
        this.pipelineHash = hash

        shader.onPipelineCreated(this)
        builder.onPipelineCreated.forEach { it(this) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pipeline) return false
        return pipelineHash == other.pipelineHash
    }

    override fun hashCode(): Int {
        return pipelineHash.hashCode()
    }

    class BuildContext(val builder: Builder) {
        val vertexLayout = VertexLayout.Builder()
        val descriptorSetLayouts = mutableListOf<DescriptorSetLayout.Builder>()
        val pushConstantRanges = mutableListOf<PushConstantRange.Builder>()

        fun vertexLayout(block: VertexLayout.Builder.() -> Unit) {
            vertexLayout.block()
        }

        fun descriptorSetLayout(set: Int = 0, block: DescriptorSetLayout.Builder.() -> Unit) {
            while (set >= descriptorSetLayouts.size) {
                descriptorSetLayouts += DescriptorSetLayout.Builder()
            }
            descriptorSetLayouts[set].block()
        }

        fun pushConstantRange(name: String, vararg stages: ShaderStage, block: PushConstantRange.Builder.() -> Unit) {
            val b = PushConstantRange.Builder()
            b.name = name
            b.stages += stages
            b.block()
            pushConstantRanges.add(b)
        }
    }

    class Builder {
        var cullMethod = CullMethod.CULL_BACK_FACES
        var depthTest = DepthCompareOp.LESS
        var isWriteDepth = true
        var lineWidth = 1f

        val onCreatePipeline = mutableListOf<(BuildContext) -> Unit>()
        val onPipelineCreated = mutableListOf<(Pipeline) -> Unit>()

        var shaderLoader: (mesh: Mesh, buildCtx: BuildContext, ctx: KoolContext) -> Shader = { _, _, _ -> throw KoolException("No shader loader specified") }

        fun create(mesh: Mesh, ctx: KoolContext): Pipeline {
            return Pipeline(this, mesh, ctx)
        }
    }

    companion object {
        private var instanceId = 1L
    }
}

interface PipelineFactory {
    fun createPipeline(mesh: Mesh, builder: Pipeline.Builder, ctx: KoolContext): Pipeline
}

enum class DepthCompareOp {
    DISABLED,
    ALWAYS,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL
}

enum class CullMethod {
    DEFAULT,
    CULL_BACK_FACES,
    CULL_FRONT_FACES,
    NO_CULLING
}