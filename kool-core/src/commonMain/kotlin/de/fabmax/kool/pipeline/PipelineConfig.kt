package de.fabmax.kool.pipeline

data class PipelineConfig(
    val blendMode: BlendMode = BlendMode.BLEND_MULTIPLY_ALPHA,
    val cullMethod: CullMethod = CullMethod.CULL_BACK_FACES,
    val depthTest: DepthCompareOp = DepthCompareOp.LESS_EQUAL,
    val isWriteDepth: Boolean = true,
    val lineWidth: Float = 1f,
    val autoReverseDepthFunc: Boolean = true
)

class PipelineConfigBuilder(
    var blendMode: BlendMode = BlendMode.BLEND_MULTIPLY_ALPHA,
    var cullMethod: CullMethod = CullMethod.CULL_BACK_FACES,
    var depthTest: DepthCompareOp = DepthCompareOp.LESS_EQUAL,
    var isWriteDepth: Boolean = true,
    var lineWidth: Float = 1f,
    var autoReverseDepthFunc: Boolean = true
) {
    fun build() = PipelineConfig(
        blendMode = blendMode,
        cullMethod = cullMethod,
        depthTest = depthTest,
        isWriteDepth = isWriteDepth,
        lineWidth = lineWidth,
        autoReverseDepthFunc = autoReverseDepthFunc
    )
}