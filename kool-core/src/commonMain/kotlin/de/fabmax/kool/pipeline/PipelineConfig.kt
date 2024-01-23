package de.fabmax.kool.pipeline

data class PipelineConfig(
    val blendMode: BlendMode = BlendMode.BLEND_MULTIPLY_ALPHA,
    val cullMethod: CullMethod = CullMethod.CULL_BACK_FACES,
    val depthTest: DepthCompareOp = DepthCompareOp.LESS_EQUAL,
    val isWriteDepth: Boolean = true,
    val lineWidth: Float = 1f,
    val autoReverseDepthFunc: Boolean = true
) {
    class Builder {
        private val defaultConfig = PipelineConfig()

        var blendMode: BlendMode = defaultConfig.blendMode
        var cullMethod: CullMethod = defaultConfig.cullMethod
        var depthTest: DepthCompareOp = defaultConfig.depthTest
        var isWriteDepth: Boolean = defaultConfig.isWriteDepth
        var lineWidth: Float = defaultConfig.lineWidth
        var autoReverseDepthFunc: Boolean = defaultConfig.autoReverseDepthFunc

        fun build() = PipelineConfig(
            blendMode = blendMode,
            cullMethod = cullMethod,
            depthTest = depthTest,
            isWriteDepth = isWriteDepth,
            lineWidth = lineWidth,
            autoReverseDepthFunc = autoReverseDepthFunc
        )
    }
}