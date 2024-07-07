package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshId
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.UniqueId


open class DepthMapPass(
    drawNode: Node,
    attachmentConfig: AttachmentConfig = defaultRenderPassAttachmentConfig,
    initialSize: Vec2i = Vec2i(128, 128),
    name: String = UniqueId.nextId("depth-map-pass")
) :
    OffscreenRenderPass2d(drawNode, attachmentConfig, initialSize, name)
{
    protected val shadowPipelines = mutableMapOf<MeshId, DrawPipeline?>()
    protected val depthShaders = mutableMapOf<DepthShaderKey, DepthShader>()

    /**
     * Cull method to use for depth map rendering. If null (the default) the original cull method of meshes is used.
     */
    var cullMethod: CullMethod? = null

    init {
        mirrorIfInvertedClipY()
        onAfterCollectDrawCommands += { ev ->
            // replace regular object shaders by cheaper shadow versions
            val q = ev.view.drawQueue
            q.forEach {
                setupDrawCommand(it, ev)
            }
        }
    }

    protected open fun setupDrawCommand(cmd: DrawCommand, updateEvent: UpdateEvent) {
        val pipeline = getDepthPipeline(cmd.mesh, updateEvent)
        if (pipeline == null) {
            cmd.isActive = false
        } else {
            cmd.pipeline = pipeline
        }
    }

    protected open fun getDepthPipeline(mesh: Mesh, updateEvent: UpdateEvent): DrawPipeline? {
        return shadowPipelines.getOrPut(mesh.id) {
            val depthShader = mesh.depthShader
                ?: mesh.depthShaderConfig?.let { cfg -> DepthShader(cfg.copy(outputLinearDepth = false, outputNormals = false)) }
                ?: defaultDepthShader(mesh, updateEvent)
            depthShader?.getOrCreatePipeline(mesh, updateEvent)
        }
    }

    private fun defaultDepthShader(mesh: Mesh, updateEvent: UpdateEvent): DepthShader? {
        if (!mesh.geometry.hasAttribute(Attribute.POSITIONS)) {
            return null
        }

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

        val defaultRenderPassAttachmentConfig = AttachmentConfig(
            ColorAttachmentNone,
            DepthAttachmentTexture()
        )
    }
}

class NormalLinearDepthMapPass(
    drawNode: Node,
    attachmentConfig: AttachmentConfig = defaultRenderPassAttachmentConfig,
    initialSize: Vec2i = Vec2i(128, 128),
    name: String = UniqueId.nextId("normal-linear-depth-map-pass")
) : DepthMapPass(drawNode, attachmentConfig, initialSize, name) {

    init {
        mirrorIfInvertedClipY()
        onAfterCollectDrawCommands += {
            clearColor = Color(0f, 1f, 0f, 1f)
        }
    }

    override fun getDepthPipeline(mesh: Mesh, updateEvent: UpdateEvent): DrawPipeline? {
        return shadowPipelines.getOrPut(mesh.id) {
            val depthShader = mesh.normalLinearDepthShader
                ?: mesh.depthShaderConfig?.let { cfg -> DepthShader(cfg.copy(outputLinearDepth = true, outputNormals = true)) }
                ?: defaultDepthShader(mesh, updateEvent)
            depthShader?.getOrCreatePipeline(mesh, updateEvent)
        }
    }

    private fun defaultDepthShader(mesh: Mesh, updateEvent: UpdateEvent): DepthShader? {
        if (!mesh.geometry.hasAttribute(Attribute.POSITIONS) || !mesh.geometry.hasAttribute(Attribute.NORMALS)) {
            return null
        }

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
        val defaultRenderPassAttachmentConfig = AttachmentConfig(
            ColorAttachmentTextures(listOf(TextureAttachmentConfig(textureFormat = TexFormat.RGBA_F16)))
        )
    }
}
