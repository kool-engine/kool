package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color


open class DepthMapPass(drawNode: Node, config: Config) : OffscreenRenderPass2d(drawNode, config) {
    protected val shadowPipelines = mutableMapOf<Int, DrawPipeline?>()
    protected val depthShaders = mutableMapOf<DepthShaderKey, DepthShader>()

    constructor(drawNode: Node, width: Int, height: Int = width) : this(drawNode, defaultSetup(width, height))

    /**
     * Cull method to use for depth map rendering. If null (the default) the original cull method of meshes is used.
     */
    var cullMethod: CullMethod? = null

    init {
        mirrorIfInvertedClipY()
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

    protected open fun getDepthPipeline(mesh: Mesh, updateEvent: UpdateEvent): DrawPipeline? {
        if (!mesh.geometry.hasAttribute(Attribute.POSITIONS)) {
            return null
        }
        return shadowPipelines.getOrPut(mesh.id) {
            val depthShader = mesh.depthShader
                ?: mesh.depthShaderConfig?.let { cfg -> DepthShader(cfg.copy(outputLinearDepth = false, outputNormals = false)) }
                ?: defaultDepthShader(mesh, updateEvent)
            depthShader.getOrCreatePipeline(mesh, updateEvent)
        }
    }

    private fun defaultDepthShader(mesh: Mesh, updateEvent: UpdateEvent): DepthShader {
        val cfg = DepthShader.Config.forMesh(mesh, getMeshCullMethod(mesh, updateEvent))
        val key = DepthShaderKey(
            vertexLayout = mesh.geometry.vertexAttributes,
            instanceLayout = mesh.instances?.instanceAttributes ?: emptyList(),
            shaderCfg = cfg
        )
        return depthShaders.getOrPut(key) { DepthShader(cfg) }
    }

    protected fun getMeshCullMethod(mesh: Mesh, updateEvent: UpdateEvent): CullMethod {
        return this.cullMethod ?: mesh.getOrCreatePipeline(updateEvent)?.cullMethod ?: CullMethod.CULL_BACK_FACES
    }

    override fun release() {
        super.release()
        shadowPipelines.values.filterNotNull().distinct().forEach { it.release() }
    }

    protected data class DepthShaderKey(
        val vertexLayout: List<Attribute>,
        val instanceLayout: List<Attribute>,
        val shaderCfg: DepthShader.Config
    )

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
        mirrorIfInvertedClipY()
        name = "NormalLinearDepthMapPass"
        onAfterCollectDrawCommands += {
            clearColor = Color(0f, 1f, 0f, 1f)
        }
    }

    override fun getDepthPipeline(mesh: Mesh, updateEvent: UpdateEvent): DrawPipeline? {
        if (!mesh.geometry.hasAttribute(Attribute.POSITIONS) || !mesh.geometry.hasAttribute(Attribute.NORMALS)) {
            return null
        }
        return shadowPipelines.getOrPut(mesh.id) {
            val depthShader = mesh.normalLinearDepthShader
                ?: mesh.depthShaderConfig?.let { cfg -> DepthShader(cfg.copy(outputLinearDepth = true, outputNormals = true)) }
                ?: defaultDepthShader(mesh, updateEvent)
            depthShader.getOrCreatePipeline(mesh, updateEvent)
        }
    }

    private fun defaultDepthShader(mesh: Mesh, updateEvent: UpdateEvent): DepthShader {
        val cfg = DepthShader.Config.forMesh(mesh, getMeshCullMethod(mesh, updateEvent)).copy(
            outputLinearDepth = true,
            outputNormals = true
        )
        val key = DepthShaderKey(
            vertexLayout = mesh.geometry.vertexAttributes,
            instanceLayout = mesh.instances?.instanceAttributes ?: emptyList(),
            shaderCfg = cfg
        )
        return depthShaders.getOrPut(key) { DepthShader(cfg) }
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
