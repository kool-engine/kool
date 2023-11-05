package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.PerspectiveProxyCam
import de.fabmax.kool.util.Color

class MaterialPass(pipeline: DeferredPipeline, suffix: String) :
        OffscreenRenderPass2d(pipeline.sceneContent, renderPassConfig {
            name = "MaterialPass-$suffix"
            setDepthTexture(false)
            val formats = FORMATS_DEFERRED_EMISSIVE
            formats.forEach { fmt ->
                addColorTexture {
                    colorFormat = fmt
                    // don't do any interpolation on output maps, or bad things will happen (especially for positions)
                    minFilter = FilterMethod.NEAREST
                    magFilter = FilterMethod.NEAREST
                }
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
        onBeforeCollectDrawCommands += { ctx ->
            //proxyCamera?.sync(scene.mainRenderPass, ctx)
            proxyCamera?.sync(this, ctx)
        }

        // do not update draw node from render pass onUpdate, it is updated from DeferredPipeline
        isUpdateDrawNode = false

        // encoded position is in view space -> z value of valid position is always negative, use a positive z value
        // in clear color to encode clear areas
        clearColors[0] = Color(0f, 0f, 1f, 0f)
        clearColors[1] = null
        clearColors[2] = null
        clearColors[3] = null
    }

    override fun collectDrawCommands(ctx: KoolContext) {
        alphaMeshes.clear()
        super.collectDrawCommands(ctx)
    }

    override fun appendMeshToDrawQueue(mesh: Mesh, ctx: KoolContext): DrawCommand? {
        return if (mesh.isOpaque) {
            super.appendMeshToDrawQueue(mesh, ctx)
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
        val FMT_POSITION_FLAGS = TexFormat.RGBA_F16
        val FMT_NORMAL_ROUGH = TexFormat.RGBA_F16
        val FMT_ALBEDO_METAL = TexFormat.RGBA
        val FMT_EMISSIVE_AO = TexFormat.RGBA_F16

        val FORMATS_DEFERRED_EMISSIVE = listOf(FMT_POSITION_FLAGS, FMT_NORMAL_ROUGH, FMT_ALBEDO_METAL, FMT_EMISSIVE_AO)
    }
}