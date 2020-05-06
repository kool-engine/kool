package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color


open class DepthMapPass(drawNode: Node, mapSize: Int) : OffscreenRenderPass2D(drawNode, mapSize, mapSize) {
    private val shadowPipelines = mutableMapOf<Long, Pipeline>()

    /**
     * Cull method to use for depth map rendering. If null (the default) the original cull method of meshes is used.
     */
    var cullMethod: CullMethod? = null

    init {
        type = Type.DEPTH
        clearColor = Color.BLACK

        onAfterCollectDrawCommands += { ctx ->
            // replace regular object shaders by cheaper shadow versions
            drawQueue.commands.forEach {
                it.pipeline = getShadowPipeline(it.mesh, ctx)
            }
        }
    }

    private fun getShadowPipeline(mesh: Mesh, ctx: KoolContext): Pipeline? {
        val culling = this.cullMethod ?: mesh.getPipeline(ctx)?.cullMethod ?: CullMethod.CULL_BACK_FACES

        return shadowPipelines.getOrPut(mesh.geometry.attributeHash) {
            // create a minimal dummy shader for each attribute set
            val shadowShader = ModeledShader(ShaderModel("shadow shader").apply {
                vertexStage { positionOutput = simpleVertexPositionNode().outPosition }
                fragmentStage { colorOutput = ShaderNodeIoVar(ModelVar4fConst(Vec4f(1f, 1f, 1f, 1f))) }
            })
            val pipelineBuilder = Pipeline.Builder().apply { cullMethod = culling }
            shadowShader.createPipeline(mesh, pipelineBuilder, ctx)
        }
    }
}
