package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.OffscreenRenderPass2dMrt
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.Color

class DeferredMrtPass(scene: Scene, val withEmissive: Boolean = false) :
        OffscreenRenderPass2dMrt(TransformGroup(), 1600, 900, if (withEmissive) FORMATS_DEFERRED_EMISSIVE else FORMATS_DEFERRED) {

    val content = drawNode as TransformGroup
    internal val alphaMeshes = mutableListOf<Mesh>()

    val positionAo: Texture
        get() = colorTextures[0]
    val normalRoughness: Texture
        get() = colorTextures[1]
    val albedoMetal: Texture
        get() = colorTextures[2]
    val emissive: Texture?
        get() = if (withEmissive) colorTextures[3] else null

    init {
        val proxyCamera = PerspectiveCamera.Proxy(scene.camera as PerspectiveCamera)
        camera = proxyCamera
        onBeforeCollectDrawCommands += { ctx ->
            proxyCamera.sync(scene.mainRenderPass.viewport, ctx)
        }

        scene.addOffscreenPass(this)

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
        val FMT_EMISSIVE = TexFormat.RGBA

        val FORMATS_DEFERRED = listOf(FMT_POSITION_AO, FMT_NORMAL_ROUGH, FMT_ALBEDO_METAL)
        val FORMATS_DEFERRED_EMISSIVE = listOf(FMT_POSITION_AO, FMT_NORMAL_ROUGH, FMT_ALBEDO_METAL, FMT_EMISSIVE)
    }
}