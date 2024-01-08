package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.PerspectiveProxyCam
import de.fabmax.kool.util.Color

class MaterialPass(pipeline: DeferredPipeline, suffix: String) :
        OffscreenRenderPass2d(pipeline.sceneContent, renderPassConfig {
            name = "MaterialPass-$suffix"
            depthTargetTexture(isUsedAsShadowMap = false)
            colorTargetTexture(FORMATS_DEFERRED_EMISSIVE.size) { i ->
                colorFormat = FORMATS_DEFERRED_EMISSIVE[i]
                // don't do any interpolation on output maps, or bad things will happen (especially for positions)
                defaultSamplerSettings = defaultSamplerSettings.nearest()
            }
        }) {

    internal val alphaMeshes = mutableListOf<Mesh>()

    val positionFlags: Texture2d
        get() = colorTextures[0]
    val normalRoughness: Texture2d
        get() = colorTextures[1]
    val albedoMetal: Texture2d
        get() = colorTextures[2]
    val emissiveAo: Texture2d
        get() = colorTextures[3]

    var proxyCamera: PerspectiveProxyCam? = null
        set(value) {
            field = value
            if (value != null) {
                camera = value
            }
        }

    init {
        (pipeline.scene.camera as? PerspectiveCamera)?.let {
            proxyCamera = PerspectiveProxyCam(it)
        }
        onBeforeCollectDrawCommands += { ev ->
            proxyCamera?.sync(ev)
        }

        // do not update draw node from render pass onUpdate, it is updated from DeferredPipeline
        isUpdateDrawNode = false

        // encoded position is in view space -> z value of valid position is always negative, use a positive z value
        // in clear color to encode clear areas
        mainView.clearColors[0] = Color(0f, 0f, 1f, 0f)
        mainView.clearColors[1] = null
        mainView.clearColors[2] = null
        mainView.clearColors[3] = null
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        alphaMeshes.clear()
        super.collectDrawCommands(ctx)
    }

    override fun afterCollectDrawCommands(updateEvent: UpdateEvent) {
        val it = mainView.drawQueue.commands.iterator()
        while (it.hasNext()) {
            val cmd = it.next()
            if (!cmd.mesh.isOpaque) {
                alphaMeshes += cmd.mesh
                it.remove()
                mainView.drawQueue.recycleDrawCommand(cmd)
            }
        }
        super.afterCollectDrawCommands(updateEvent)
    }

    companion object {
        val FMT_POSITION_FLAGS = TexFormat.RGBA_F16
        val FMT_NORMAL_ROUGH = TexFormat.RGBA_F16
        val FMT_ALBEDO_METAL = TexFormat.RGBA
        val FMT_EMISSIVE_AO = TexFormat.RGBA_F16

        val FORMATS_DEFERRED_EMISSIVE = listOf(FMT_POSITION_FLAGS, FMT_NORMAL_ROUGH, FMT_ALBEDO_METAL, FMT_EMISSIVE_AO)
    }
}