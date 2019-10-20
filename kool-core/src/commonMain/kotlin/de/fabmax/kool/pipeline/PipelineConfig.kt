package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.Mesh

enum class CullMethod {
    FRONT_FACE,
    BACK_FACE,
    NO_CULL
}

enum class DepthTest {
    DISABLED,
    LESS,
    LESS_EQUAL
}

interface ShaderCode

data class PipelineConfig(
        val cullMethod: CullMethod,
        val depthTest: DepthTest,
        val isWriteDepth: Boolean,
        val lineWidth: Float,

        val vertexLayout: VertexLayoutDescription,
        val uniformLayout: UniformLayoutDescription,

        val shaderCode: ShaderCode
) {
    class Builder {
        var cullMethod = CullMethod.BACK_FACE
        var depthTest = DepthTest.LESS
        var isWriteDepth = true
        var lineWidth = 1f

        var uniformLayout = UniformLayoutDescription(listOf())
        lateinit var vertexLayout: VertexLayoutDescription

        lateinit var shaderCode: ShaderCode

        fun build(): PipelineConfig {
            return PipelineConfig(cullMethod, depthTest, isWriteDepth, lineWidth, vertexLayout, uniformLayout, shaderCode)
        }
    }
}

fun Mesh.pipelineConfig(block: PipelineConfig.Builder.() -> Unit): PipelineConfig {
    val builder = PipelineConfig.Builder()
    builder.vertexLayout = VertexLayoutDescription.forMesh(this)
    builder.block()
    return builder.build()
}
