package de.fabmax.kool.pipeline

data class PipelineConfig(
    var blendMode: BlendMode = BlendMode.BLEND_MULTIPLY_ALPHA,
    var cullMethod: CullMethod = CullMethod.CULL_BACK_FACES,
    var depthTest: DepthCompareOp = DepthCompareOp.LESS_EQUAL,
    var isWriteDepth: Boolean = true,
    var lineWidth: Float = 1f,
    var autoReverseDepthFunc: Boolean = true
) {
    fun set(that: PipelineConfig) {
        blendMode = that.blendMode
        cullMethod = that.cullMethod
        depthTest = that.depthTest
        isWriteDepth = that.isWriteDepth
        lineWidth = that.lineWidth
        autoReverseDepthFunc = that.autoReverseDepthFunc
    }
}