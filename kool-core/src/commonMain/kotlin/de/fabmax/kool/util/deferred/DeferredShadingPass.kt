package de.fabmax.kool.util.deferred

import de.fabmax.kool.pipeline.OffscreenRenderPass2dMrt
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene

class DeferredShadingPass(scene: Scene, texFormats: List<TexFormat> = FMTS_DEFERRED) :
        OffscreenRenderPass2dMrt(Group(), 1600, 900, texFormats) {

    val content = drawNode as Group

    init {
        scene.onRenderScene += { ctx ->
            val mapW = mainRenderPass.viewport.width
            val mapH = mainRenderPass.viewport.height
            if (mapW > 0 && mapH > 0 && (mapW != texWidth || mapH != texHeight)) {
                resize(mapW, mapH, ctx)
            }
        }
    }

    companion object {
        val FMT_POSITION_AO = TexFormat.RGBA_F16
        val FMT_NORMAL_ROUGH = TexFormat.RGBA
        val FMT_ALBEDO_METAL = TexFormat.RGBA

        val FMTS_DEFERRED = listOf(FMT_POSITION_AO, FMT_NORMAL_ROUGH, FMT_ALBEDO_METAL)
    }
}