package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.NodeId
import de.fabmax.kool.util.UniqueId


open class DepthMapPass(
    drawNode: Node,
    attachmentConfig: AttachmentConfig = AttachmentConfig { defaultDepth() },
    initialSize: Vec2i = Vec2i(128, 128),
    name: String = UniqueId.nextId("depth-map-pass")
) : OffscreenPass2d(drawNode, attachmentConfig, initialSize, name) {
    protected val shadowPipelines = mutableMapOf<NodeId, DrawPipeline?>()
    protected val depthShaders = mutableMapOf<DepthShaderKey, DepthShader>()

    /**
     * Cull method to use for depth map rendering. If null (the default) the original cull method of meshes is used.
     */
    var cullMethod: CullMethod? = null

    init {
        mirrorIfInvertedClipY()
        depthMode = DepthMode.Legacy
        onAfterCollectDrawCommands += { ev ->
            // replace regular object shaders by cheaper shadow versions
            val q = ev.view.drawQueue
            q.forEach {
                setupDrawCommand(it, ev)
            }
        }
    }

    protected open fun setupDrawCommand(cmd: DrawCommand, updateEvent: UpdateEvent) {
        val pipeline = getDepthPipeline(cmd.mesh, updateEvent.ctx)
        if (pipeline == null) {
            cmd.isActive = false
        } else {
            cmd.pipeline = pipeline
        }
    }

    protected open fun getDepthPipeline(mesh: Mesh, ctx: KoolContext): DrawPipeline? {
        return shadowPipelines.getOrPut(mesh.id) {
            val depthShader = mesh.depthShader
                ?: mesh.depthShaderConfig?.let { cfg -> DepthShader(cfg.copy(outputLinearDepth = false, outputNormals = false)) }
                ?: defaultDepthShader(mesh, ctx)
            depthShader?.getOrCreatePipeline(mesh, ctx)
        }
    }

    private fun defaultDepthShader(mesh: Mesh, ctx: KoolContext): DepthShader? {
        if (!mesh.geometry.hasAttribute(Attribute.POSITIONS)) {
            return null
        }

        val cfg = DepthShader.Config.forMesh(mesh, getMeshCullMethod(mesh, ctx))
        val key = DepthShaderKey(
            vertexLayout = mesh.geometry.vertexAttributes,
            instanceLayout = mesh.instances?.instanceAttributes ?: emptyList(),
            shaderCfg = cfg
        )
        return depthShaders.getOrPut(key) { DepthShader(cfg) }
    }

    protected fun getMeshCullMethod(mesh: Mesh, ctx: KoolContext): CullMethod {
        return this.cullMethod ?: mesh.getOrCreatePipeline(ctx)?.cullMethod ?: CullMethod.CULL_BACK_FACES
    }

    override fun release() {
        super.release()
        shadowPipelines.values
            .filterNotNull()
            .distinct()
            .filter { !it.isReleased }
            .forEach { it.release() }
    }

    protected data class DepthShaderKey(
        val vertexLayout: List<Attribute>,
        val instanceLayout: List<Attribute>,
        val shaderCfg: DepthShader.Config
    )
}

class NormalLinearDepthMapPass(
    drawNode: Node,
    attachmentConfig: AttachmentConfig = AttachmentConfig.singleColorDefaultDepth(TexFormat.RGBA_F16),
    initialSize: Vec2i = Vec2i(128, 128),
    name: String = UniqueId.nextId("normal-linear-depth-map-pass")
) : DepthMapPass(drawNode, attachmentConfig, initialSize, name) {

    val normalDepthMap: Texture2d get() = colorTexture!!

    init {
        depthMode = DepthMode.Legacy
        mirrorIfInvertedClipY()
    }

    override fun getDepthPipeline(mesh: Mesh, ctx: KoolContext): DrawPipeline? {
        return shadowPipelines.getOrPut(mesh.id) {
            val depthShader = mesh.normalLinearDepthShader
                ?: mesh.depthShaderConfig?.let { cfg -> DepthShader(cfg.copy(outputLinearDepth = true, outputNormals = true)) }
                ?: defaultDepthShader(mesh, ctx)
            depthShader?.getOrCreatePipeline(mesh, ctx)
        }
    }

    private fun defaultDepthShader(mesh: Mesh, ctx: KoolContext): DepthShader? {
        if (!mesh.geometry.hasAttribute(Attribute.POSITIONS) || !mesh.geometry.hasAttribute(Attribute.NORMALS)) {
            return null
        }

        val cfg = DepthShader.Config.forMesh(mesh, getMeshCullMethod(mesh, ctx)).copy(
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
}
