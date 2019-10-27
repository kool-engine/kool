package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.Mesh

class Pipeline private constructor(builder: Builder) {
    val cullMethod: CullMethod = builder.cullMethod
    val depthTest: DepthTest = builder.depthTest
    val isWriteDepth: Boolean = builder.isWriteDepth
    val lineWidth: Float = builder.lineWidth

    val vertexLayout: VertexLayoutDescription = builder.vertexLayout.build()
    val descriptorLayout: DescriptorLayout = builder.descriptorLayout.build()

    val shaderCode: ShaderCode = builder.shaderCode

    class Builder {
        var cullMethod = CullMethod.BACK_FACE
        var depthTest = DepthTest.LESS
        var isWriteDepth = true
        var lineWidth = 1f

        val vertexLayout = VertexLayoutDescription.Builder()
        val descriptorLayout = DescriptorLayout.Builder()

        lateinit var shaderCode: ShaderCode

        fun build(): Pipeline {
            return Pipeline(this)
        }
    }
}

fun Mesh.pipelineConfig(block: Pipeline.Builder.() -> Unit): Pipeline {
    val builder = Pipeline.Builder()
    builder.vertexLayout.apply { forMesh(this@pipelineConfig) }
    builder.block()
    return builder.build()
}

enum class CullMethod {
    FRONT_FACE,
    BACK_FACE,
    NO_CULL
}

enum class DepthTest {
    DISABLED,
    ALWAYS,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL
}
