package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.pipeline.shading.DepthShaderConfig
import de.fabmax.kool.pipeline.shading.NormalLinearDepthShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import kotlin.math.max


open class DepthMapPass(drawNode: Node, config: Config) : OffscreenRenderPass2d(drawNode, config) {
    protected val shadowPipelines = mutableMapOf<Long, Pipeline?>()

    constructor(drawNode: Node, width: Int, height: Int = width) : this(drawNode, defaultSetup(width, height))

    /**
     * Cull method to use for depth map rendering. If null (the default) the original cull method of meshes is used.
     */
    var cullMethod: CullMethod? = null

    init {
        onAfterCollectDrawCommands += { ctx ->
            // replace regular object shaders by cheaper shadow versions
            for (i in drawQueue.commands.indices) {
                setupDrawCommand(drawQueue.commands[i], ctx)
            }
        }
    }

    protected open fun setupDrawCommand(cmd: DrawCommand, ctx: KoolContext) {
        cmd.pipeline = getDepthPipeline(cmd.mesh, ctx)
    }

    protected open fun getDepthPipeline(mesh: Mesh, ctx: KoolContext): Pipeline? {
        return shadowPipelines.getOrPut(mesh.id) {
            mesh.depthShader?.createPipeline(mesh, ctx) ?: createPipeline(mesh, ctx)
        }
    }

    protected fun getMeshCullMethod(mesh: Mesh, ctx: KoolContext): CullMethod {
        return this.cullMethod ?: mesh.getPipeline(ctx)?.cullMethod ?: CullMethod.CULL_BACK_FACES
    }

    private fun createPipeline(mesh: Mesh, ctx: KoolContext): Pipeline {
        val depthCfg = DepthShaderConfig().apply {
            cullMethod = getMeshCullMethod(mesh, ctx)
            isInstanced = mesh.instances != null
            mesh.skin?.let {
                isSkinned = true
                maxJoints = max(defaultMaxNumberOfJoints, it.nodes.size)
            }

            nMorphWeights = mesh.morphWeights?.size ?: 0
            morphAttributes += mesh.geometry.getMorphAttributes()
        }
        val depthShader = DepthShader(depthCfg)
        return depthShader.createPipeline(mesh, ctx)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        shadowPipelines.values.filterNotNull().forEach { ctx.disposePipeline(it) }
    }

    companion object {
        var defaultMaxNumberOfJoints = 16

        fun defaultSetup(width: Int, height: Int) = renderPassConfig {
            name = "DepthMapPass"
            setSize(width, height)
            clearColorTexture()
            setDepthTexture(false)
        }
    }
}

class NormalLinearDepthMapPass(drawNode: Node, config: Config) : DepthMapPass(drawNode, config) {

    constructor(drawNode: Node, width: Int, height: Int = width) : this(drawNode, normalLinearDepthSetup(width, height))

    init {
        name = "NormalLinearDepthMapPass"

        onAfterCollectDrawCommands += {
            clearColor = Color(0f, 1f, 0f, 1f)
        }
    }

    override fun getDepthPipeline(mesh: Mesh, ctx: KoolContext): Pipeline? {
        return shadowPipelines.getOrPut(mesh.id) {
            mesh.normalLinearDepthShader?.createPipeline(mesh, ctx) ?: createNormalLinearPipeline(mesh, ctx)
        }
    }

    private fun createNormalLinearPipeline(mesh: Mesh, ctx: KoolContext): Pipeline? {
        if (!mesh.geometry.hasAttribute(Attribute.NORMALS)) {
            return null
        }

        val depthCfg = DepthShaderConfig().apply {
            cullMethod = getMeshCullMethod(mesh, ctx)
            isInstanced = mesh.instances != null
            mesh.skin?.let {
                isSkinned = true
                maxJoints = max(defaultMaxNumberOfJoints, it.nodes.size)
            }

            nMorphWeights = mesh.morphWeights?.size ?: 0
            morphAttributes += mesh.geometry.getMorphAttributes()
        }
        val depthShader = NormalLinearDepthShader(depthCfg)
        return depthShader.createPipeline(mesh, ctx)
    }

    companion object {
        private fun normalLinearDepthSetup(width: Int, height: Int) = renderPassConfig {
            name = "NormalLinearDepthMapPass"
            setSize(width, height)
            clearDepthTexture()
            addColorTexture(TexFormat.RGBA_F16)
        }
    }
}
