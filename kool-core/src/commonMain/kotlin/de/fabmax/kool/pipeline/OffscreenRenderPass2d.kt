package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.launchDelayed

open class OffscreenRenderPass2d(drawNode: Node, config: Config) : OffscreenRenderPass(config) {

    override val views = mutableListOf(
        View("default", drawNode, PerspectiveCamera(), Array(numColorAttachments) { null }).apply {
            setFullscreenViewport()
        }
    )

    val mainView: View get() = views[0]
    var drawNode: Node by mainView::drawNode
    var camera: Camera by mainView::camera
    val viewport: Viewport by mainView::viewport
    var clearColor: Color? by mainView::clearColor
    var clearDepth: Boolean by mainView::clearDepth
    var isUpdateDrawNode: Boolean by mainView::isUpdateDrawNode

    var blitRenderPass: OffscreenRenderPass2d? = null

    val depthTexture = makeDepthAttachmentTex()
    val colorTextures = makeColorAttachmentTexs()
    val colorTexture: Texture2d?
        get() = if (colorTextures.isNotEmpty()) colorTextures[0] else null

    val copyTargetsColor = mutableListOf<Texture2d>()

    internal val impl = KoolSystem.requireContext().backend.createOffscreenPass2d(this)

    fun addView(name: String, camera: Camera): View {
        val view = View(name, mainView.drawNode, camera, Array(numColorAttachments) { null })
        views.add(view)
        return view
    }

    fun copyColor(): Texture2d {
        val tex = Texture2d(getColorTexProps(), "$name-copy-${copyTargetsColor.size}")
        copyTargetsColor += tex
        return tex
    }

    override fun release() {
        super.release()
        impl.release()

        launchDelayed(3) {
            if (depthAttachment !is TextureDepthAttachment || depthAttachment.attachment.providedTexture == null) {
                depthTexture?.dispose()
            }
            colorTextures.forEachIndexed { i, tex ->
                if (colorAttachment !is TextureColorAttachment || colorAttachment.attachments[i].providedTexture == null) {
                    tex.dispose()
                }
            }
        }
    }

    override fun applySize(width: Int, height: Int, ctx: KoolContext) {
        super.applySize(width, height, ctx)
        impl.applySize(width, height, ctx)
    }

    private fun makeColorAttachmentTexs(): List<Texture2d> {
        return if (colorAttachment is TextureColorAttachment) {
            colorAttachment.attachments.mapIndexed { i, texCfg ->
                if (texCfg.isProvided) {
                    texCfg.providedTexture!! as Texture2d
                } else {
                    val name = "${name}_color[$i]"
                    val props = texCfg.getTextureProps(mipLevels > 1)
                    Texture2d(props, name)
                }
            }
        } else {
            emptyList()
        }
    }

    private fun makeDepthAttachmentTex(): Texture2d? {
        return if (depthAttachment is TextureDepthAttachment) {
            val cfg = depthAttachment.attachment
            if (cfg.isProvided) {
                cfg.providedTexture!! as Texture2d
            } else {
                val name = "${name}_depth"
                val props = cfg.getTextureProps(mipLevels > 1)
                Texture2d(props, name)
            }
        } else {
            null
        }
    }
}

interface OffscreenPass2dImpl {
    fun applySize(width: Int, height: Int, ctx: KoolContext)

    fun release()

    fun draw(ctx: KoolContext)
}