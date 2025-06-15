package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.Viewport

open class OffscreenPass2d(
    drawNode: Node,
    attachmentConfig: AttachmentConfig,
    initialSize: Vec2i,
    name: String,
    numSamples: Int = 1,
    mipMode: MipMode = MipMode.Single,
) : OffscreenPass(numSamples, mipMode, Vec3i(initialSize, 1), name) {

    override val colorAttachments: List<ColorAttachment> = attachmentConfig.colors.mapIndexed { i, cfg -> ColorAttachment(cfg, i) }
    override val depthAttachment: RenderPassDepthAttachment? = attachmentConfig.depth?.let {
        if (it.isTransient) TransientDepthAttachment(it) else DepthAttachment(it)
    }

    val colorTextures: List<Texture2d> = colorAttachments.map { it.texture }
    val colorTexture: Texture2d? get() = colorTextures.getOrNull(0)
    val depthTexture: Texture2d? get() = (depthAttachment as? DepthAttachment)?.texture

    val defaultView = View("${name}:default-view", drawNode, PerspectiveCamera())
    override val views = mutableListOf(defaultView)

    var drawNode: Node by defaultView::drawNode
    var camera: Camera by defaultView::camera
    var viewport: Viewport by defaultView::viewport

    var isUpdateDrawNode: Boolean by defaultView::isUpdateDrawNode
    var isReleaseDrawNode: Boolean by defaultView::isReleaseDrawNode

    val impl: OffscreenPass2dImpl

    init {
        viewport = Viewport(0, 0, width, height)
        impl = KoolSystem.requireContext().backend.createOffscreenPass2d(this)
    }

    fun createView(name: String, camera: Camera = PerspectiveCamera()): View {
        val view = View(name, defaultView.drawNode, camera)
        view.isUpdateDrawNode = false
        view.isReleaseDrawNode = false
        views += view
        return view
    }

    fun removeView(view: View) {
        views -= view
    }

    /**
     * Convenience function: Creates a single shot FrameCopy of the color attachment. This way, the renderpass can
     * be released while keeping its color output in a separate texture (useful for single-shot renderpasses which
     * generate lookup-tables, etc.)
     */
    fun copyColor(): Texture2d {
        val copy = FrameCopy(this, isCopyColor = true, isCopyDepth = false, isSingleShot = true)
        frameCopies += copy
        return copy.colorCopy2d
    }

    fun setSize(width: Int, height: Int) {
        super.setSize(width, height, 1)
    }

    override fun applySize(width: Int, height: Int, layers: Int) {
        require(layers == 1) { "OffscreenPass2d layers must be == 1" }
        super.applySize(width, height, layers)
        impl.applySize(width, height)
    }

    override fun release() {
        super.release()
        impl.release()
    }

    inner class ColorAttachment(config: TextureAttachmentConfig, i: Int) : RenderPassColorTextureAttachment<Texture2d> {
        override val texture: Texture2d = Texture2d(config.textureFormat, mipMode.mipMapping, config.samplerSettings, "${name}:color[$i]")
        override var clearColor: ClearColor = config.clearColor
    }

    inner class DepthAttachment(config: TextureAttachmentConfig) : RenderPassDepthTextureAttachment<Texture2d> {
        override val texture: Texture2d = Texture2d(config.textureFormat, mipMode.mipMapping, config.samplerSettings, "${name}:depth")
        override var clearDepth: ClearDepth = config.clearDepth
    }

    inner class TransientDepthAttachment(config: TextureAttachmentConfig) : RenderPassDepthAttachment {
        override val clearDepth: ClearDepth = config.clearDepth
    }
}

interface OffscreenPass2dImpl : Releasable {
    fun applySize(width: Int, height: Int)
}