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
    val pipelineHash: Long
    val pipelineInstanceId = (super.hashCode().toLong() shl 32) + instanceId++

    val cullMethod: CullMethod = builder.cullMethod
    val depthTest: DepthTest = builder.depthTest
    val isWriteDepth: Boolean = builder.isWriteDepth
    val lineWidth: Float = builder.lineWidth

    val vertexLayout: VertexLayoutDescription
    val descriptorLayout: DescriptorLayout

    val shader: Shader

    init {
        val buildCtx = BuildContext(builder)
        builder.onCreatePipeline.forEach { it(buildCtx) }
        shader = builder.shaderLoader(mesh, buildCtx, ctx)
        vertexLayout = buildCtx.vertexLayout.create()
        descriptorLayout = buildCtx.descriptorLayout.create()

        // compute pipelineHash
        var hash = vertexLayout.longHash
        hash = (hash * 71023L) + descriptorLayout.longHash
        hash = (hash * 71023L) + shader.shaderCode.longHash
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
        val vertexLayout = VertexLayoutDescription.Builder()
        val descriptorLayout = DescriptorLayout.Builder()
    }

    class Builder {
        var cullMethod = CullMethod.BACK_FACE
        var depthTest = DepthTest.LESS
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

fun Mesh.pipelineConfig(block: Pipeline.Builder.() -> Unit) {
    pipelineLoader = { ctx ->
        val builder = Pipeline.Builder()
        //builder.onCreatePipeline += { it.vertexLayout.forMesh(this@pipelineConfig) }
        builder.block()
        builder.create(this, ctx)
    }
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
