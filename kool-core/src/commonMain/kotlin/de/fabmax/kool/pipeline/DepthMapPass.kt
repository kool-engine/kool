package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color


open class DepthMapPass(drawNode: Node, config: Config) : OffscreenRenderPass2d(drawNode, config) {
    protected val shadowPipelines = mutableMapOf<Int, Pipeline?>()

    constructor(drawNode: Node, width: Int, height: Int = width) : this(drawNode, defaultSetup(width, height))

    /**
     * Cull method to use for depth map rendering. If null (the default) the original cull method of meshes is used.
     */
    var cullMethod: CullMethod? = null

    init {
        onAfterCollectDrawCommands += { ev ->
            // replace regular object shaders by cheaper shadow versions
            val q = ev.view.drawQueue
            for (i in q.commands.indices) {
                setupDrawCommand(q.commands[i], ev)
            }
        }
    }

    protected open fun setupDrawCommand(cmd: DrawCommand, updateEvent: UpdateEvent) {
        cmd.pipeline = getDepthPipeline(cmd.mesh, updateEvent)
    }

    protected open fun getDepthPipeline(mesh: Mesh, updateEvent: UpdateEvent): Pipeline? {
        if (!mesh.geometry.hasAttribute(Attribute.POSITIONS)) {
            return null
        }
        return shadowPipelines.getOrPut(mesh.id) {
            val depthShader = mesh.depthShader
                ?: mesh.depthShaderConfig?.let { cfg -> DepthShader(cfg.copy(outputLinearDepth = false, outputNormals = false)) }
                ?: DepthShader(DepthShader.Config.forMesh(mesh, getMeshCullMethod(mesh, updateEvent)))

            depthShader.getOrCreatePipeline(mesh, updateEvent)
        }
    }

    protected fun getMeshCullMethod(mesh: Mesh, updateEvent: UpdateEvent): CullMethod {
        return this.cullMethod ?: mesh.getOrCreatePipeline(updateEvent)?.cullMethod ?: CullMethod.CULL_BACK_FACES
    }

    override fun release() {
        super.release()
        shadowPipelines.values.filterNotNull().forEach { KoolSystem.requireContext().disposePipeline(it) }
    }

    companion object {
        var defaultMaxNumberOfJoints = 16

        fun defaultSetup(width: Int, height: Int) = renderPassConfig {
            name = "DepthMapPass"
            size(width, height)
            colorTargetNone()
            depthTargetTexture(isUsedAsShadowMap = false)
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

    override fun getDepthPipeline(mesh: Mesh, updateEvent: UpdateEvent): Pipeline? {
        if (!mesh.geometry.hasAttribute(Attribute.POSITIONS) || !mesh.geometry.hasAttribute(Attribute.NORMALS)) {
            return null
        }
        return shadowPipelines.getOrPut(mesh.id) {
            val depthShader = mesh.normalLinearDepthShader
                ?: mesh.depthShaderConfig?.let { cfg -> DepthShader(cfg.copy(outputLinearDepth = true, outputNormals = true)) }
                ?: DepthShader(DepthShader.Config.forMesh(mesh, getMeshCullMethod(mesh, updateEvent)).apply {
                    outputLinearDepth = true
                    outputNormals = true
                })

            depthShader.getOrCreatePipeline(mesh, updateEvent)
        }
    }

    companion object {
        private fun normalLinearDepthSetup(width: Int, height: Int) = renderPassConfig {
            name = "NormalLinearDepthMapPass"
            size(width, height)
            depthTargetRenderBuffer()
            colorTargetTexture(TexFormat.RGBA_F16)
        }
    }
}
