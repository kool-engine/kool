package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color

class DeferredMrtPass(scene: Scene, val withEmissive: Boolean = false) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "DeferredMrtPass"
            setDynamicSize()
            setDepthTexture(false)
            val formats = if (withEmissive) FORMATS_DEFERRED_EMISSIVE else FORMATS_DEFERRED
            formats.forEach { addColorTexture(it) }
        }) {

    val content = drawNode as Group
    internal val alphaMeshes = mutableListOf<Mesh>()

    val positionAo: Texture2d
        get() = colorTextures[0]
    val normalRoughness: Texture2d
        get() = colorTextures[1]
    val albedoMetal: Texture2d
        get() = colorTextures[2]
    val emissive: Texture2d?
        get() = if (withEmissive) colorTextures[3] else null

    init {
        val proxyCamera = PerspectiveCamera.Proxy(scene.camera as PerspectiveCamera)
        camera = proxyCamera
        onBeforeCollectDrawCommands += { ctx ->
            proxyCamera.sync(scene.mainRenderPass.viewport, ctx)
        }

        scene.addOffscreenPass(this)

        // update content group from scene.onUpdate, so it is updated before any offscreen passes are updated
        // otherwise shadow maps are rendered before content group is updated and shadows have a latency of one frame
        isUpdateDrawNode = false
        scene.onUpdate += { ev ->
            content.update(ev)
        }

        // encoded position is in view space -> z value of valid positions is always negative, use a positive z value
        // in clear color to encode clear areas
        clearColors[0] = Color(0f, 0f, 1f, 0f)
        clearColors[1] = null
        clearColors[2] = null
        if (withEmissive) {
            clearColors[3] = null
        }

        content.isFrustumChecked = false

        drawQueue.meshFilter = { it.isOpaque }
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        alphaMeshes.clear()
        super.collectDrawCommands(ctx)
    }

    override fun addMesh(mesh: Mesh, ctx: KoolContext): DrawCommand? {
        return if (mesh.isOpaque) {
            super.addMesh(mesh, ctx)
        } else {
            alphaMeshes += mesh
            null
        }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    companion object {
        val FMT_POSITION_AO = TexFormat.RGBA_F16
        val FMT_NORMAL_ROUGH = TexFormat.RGBA_F16
        val FMT_ALBEDO_METAL = TexFormat.RGBA
        val FMT_EMISSIVE = TexFormat.RGBA_F16

        val FORMATS_DEFERRED = listOf(FMT_POSITION_AO, FMT_NORMAL_ROUGH, FMT_ALBEDO_METAL)
        val FORMATS_DEFERRED_EMISSIVE = listOf(FMT_POSITION_AO, FMT_NORMAL_ROUGH, FMT_ALBEDO_METAL, FMT_EMISSIVE)
    }
}