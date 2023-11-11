package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.drawqueue.DrawCommand

class Pipeline private constructor(builder: Builder) {

    val name = builder.name

    /**
     * pipelineHash is used to determine pipeline equality. In contrast to standard java hashCode() a 64-bit hash is
     * used to make collisions less likely. For fast equality checks the equals() method only uses this value to
     * determine equality.
     */
    val pipelineHash: ULong
    val pipelineInstanceId = instanceId++

    val cullMethod: CullMethod = builder.cullMethod
    val blendMode: BlendMode = builder.blendMode
    val depthCompareOp: DepthCompareOp = builder.depthTest
    val isWriteDepth: Boolean = builder.isWriteDepth
    val lineWidth: Float = builder.lineWidth

    val layout: Layout
    val shaderCode: ShaderCode

    val onUpdate = mutableListOf<(DrawCommand) -> Unit>()

    init {
        val vertexLayout = builder.vertexLayout.create()
        val descriptorSetLayouts = builder.descriptorSetLayouts.mapIndexed { i, b -> b.create(i) }
        val pushConstantRanges = builder.pushConstantRanges.map { it.create() }
        layout = Layout(vertexLayout, descriptorSetLayouts, pushConstantRanges)
        shaderCode = builder.shaderCodeGenerator(layout)

        // compute pipelineHash
        var hash = cullMethod.hashCode().toULong()
        hash = (hash * 71023UL) + depthCompareOp.hashCode().toULong()
        hash = (hash * 71023UL) + isWriteDepth.hashCode().toULong()
        hash = (hash * 71023UL) + lineWidth.hashCode().toULong()
        hash = (hash * 71023UL) + vertexLayout.longHash
        hash = (hash * 71023UL) + shaderCode.longHash.toULong()
        descriptorSetLayouts.forEach { hash = (hash * 71023UL) + it.longHash }
        pushConstantRanges.forEach { hash = (hash * 71023UL) + it.longHash }
        this.pipelineHash = hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pipeline) return false
        return pipelineHash == other.pipelineHash
    }

    override fun hashCode(): Int {
        return pipelineHash.hashCode()
    }

    class Layout(
        val vertices: VertexLayout,
        val descriptorSets: List<DescriptorSetLayout>,
        val pushConstantRanges: List<PushConstantRange>
    ) {
        fun findDescriptorByName(name: String): Pair<DescriptorSetLayout, Descriptor>? {
            for (set in descriptorSets) {
                val desc = set.findDescriptorByName(name)
                if (desc != null) {
                    return set to desc
                }
            }
            return null
        }
    }

    class Builder {
        var name = "pipeline"
        var cullMethod = CullMethod.CULL_BACK_FACES
        var blendMode = BlendMode.DISABLED
        var depthTest = DepthCompareOp.LESS
        var isWriteDepth = true
        var lineWidth = 1f

        lateinit var shaderCodeGenerator: (Layout) -> ShaderCode

        val vertexLayout = VertexLayout.Builder()
        val descriptorSetLayouts = mutableListOf<DescriptorSetLayout.Builder>()
        val pushConstantRanges = mutableListOf<PushConstantRange.Builder>()

        fun create(): Pipeline {
            return Pipeline(this)
        }
    }

    companion object {
        private var instanceId = 1L
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