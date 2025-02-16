package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.PerspectiveProxyCam
import de.fabmax.kool.util.Color

class MaterialPass(pipeline: DeferredPipeline, suffix: String) :
    OffscreenPass2d(
        drawNode = pipeline.sceneContent,
        attachmentConfig = AttachmentConfig {
            colors += FORMATS_DEFERRED_EMISSIVE
            defaultDepth()
        },
        initialSize = Vec2i(128, 128),
        name = "material-pass-$suffix"
    )
{

    internal val alphaMeshes = mutableListOf<Mesh>()

    val positionFlags: Texture2d get() = colorTextures[0]
    val normalRoughness: Texture2d get() = colorTextures[1]
    val albedoMetal: Texture2d get() = colorTextures[2]
    val emissiveAo: Texture2d get() = colorTextures[3]

    var proxyCamera: PerspectiveProxyCam? = null
        set(value) {
            field = value
            if (value != null) {
                camera = value
            }
        }

    init {
        mirrorIfInvertedClipY()
        (pipeline.scene.camera as? PerspectiveCamera)?.let {
            proxyCamera = PerspectiveProxyCam(it)
        }
        onBeforeCollectDrawCommands += { ev ->
            proxyCamera?.sync(ev)
        }

        // do not update draw node from render pass onUpdate, it is updated from DeferredPipeline
        isUpdateDrawNode = false
    }

    override fun afterCollectDrawCommands(updateEvent: UpdateEvent) {
        alphaMeshes.clear()
        val it = defaultView.drawQueue.iterator()
        while (it.hasNext()) {
            val cmd = it.next()
            if (!cmd.mesh.isOpaque) {
                alphaMeshes += cmd.mesh
                it.remove()
                defaultView.drawQueue.recycleDrawCommand(cmd)
            }
        }
        super.afterCollectDrawCommands(updateEvent)
    }

    companion object {
        private val defaultSamplerSettings = SamplerSettings().clamped().nearest()

        val FMT_POSITION_FLAGS = TextureAttachmentConfig {
            textureFormat = TexFormat.RGBA_F16
            samplerSettings = defaultSamplerSettings
            // encoded position is in view space -> z value of valid position is always negative, use a
            // positive z value in clear color to encode clear areas
            clearColor = ClearColorFill(Color(0f, 0f, 1f, 0f))
        }
        val FMT_NORMAL_ROUGH = TextureAttachmentConfig {
            textureFormat = TexFormat.RGBA_F16
            samplerSettings = defaultSamplerSettings
            clearColor = ClearColorFill(Color(0f, 0f, 0f, 0f))
        }
        val FMT_ALBEDO_METAL = TextureAttachmentConfig {
            textureFormat = TexFormat.RGBA
            samplerSettings = defaultSamplerSettings
            clearColor = ClearColorFill(Color(0f, 0f, 0f, 0f))
        }
        val FMT_EMISSIVE_AO = TextureAttachmentConfig {
            textureFormat = TexFormat.RGBA_F16
            samplerSettings = defaultSamplerSettings
            clearColor = ClearColorFill(Color(0f, 0f, 0f, 0f))
        }

        val FORMATS_DEFERRED_EMISSIVE = listOf(FMT_POSITION_FLAGS, FMT_NORMAL_ROUGH, FMT_ALBEDO_METAL, FMT_EMISSIVE_AO)
    }
}