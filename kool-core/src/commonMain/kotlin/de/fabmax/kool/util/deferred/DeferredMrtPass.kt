package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.OffscreenRenderPass2dMrt
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.Group
import de.fabmax.kool.util.Color

class DeferredMrtPass() : OffscreenRenderPass2dMrt(Group(), 1600, 900, FMTS_DEFERRED) {

    val content = drawNode as Group

    val positionAo: Texture
        get() = textures[0]
    val normalRoughness: Texture
        get() = textures[1]
    val albedoMetal: Texture
        get() = textures[2]

    init {
        clearColors[0] = Color(0f, 0f, 2f, 0f)
        clearColors[1] = null
        clearColors[2] = null

        content.isFrustumChecked = false
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    companion object {
        val FMT_POSITION_AO = TexFormat.RGBA_F16
        val FMT_NORMAL_ROUGH = TexFormat.RGBA_F16
        val FMT_ALBEDO_METAL = TexFormat.RGBA

        val FMTS_DEFERRED = listOf(FMT_POSITION_AO, FMT_NORMAL_ROUGH, FMT_ALBEDO_METAL)
    }
}