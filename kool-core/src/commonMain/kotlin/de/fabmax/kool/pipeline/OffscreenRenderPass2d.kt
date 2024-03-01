package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.launchDelayed

open class OffscreenRenderPass2d(drawNode: Node, attachmentConfig: AttachmentConfig, initialSize: Vec2i, name: String) :
    OffscreenRenderPass(attachmentConfig, Vec3i(initialSize.x, initialSize.y, 1), name)
{
    override val views = mutableListOf(
        View("default", drawNode, PerspectiveCamera()).apply {
            setFullscreenViewport()
        }
    )

    val mainView: View get() = views[0]
    var drawNode: Node by mainView::drawNode
    var camera: Camera by mainView::camera
    val viewport: Viewport by mainView::viewport

    var isUpdateDrawNode: Boolean by mainView::isUpdateDrawNode
    var isReleaseDrawNode: Boolean by mainView::isReleaseDrawNode

    val depthTexture = makeDepthAttachment()
    val colorTextures = makeColorAttachments()
    val colorTexture: Texture2d?
        get() = colorTextures.getOrNull(0)

    internal val impl = KoolSystem.requireContext().backend.createOffscreenPass2d(this)

    fun addView(name: String, camera: Camera): View {
        val view = View(name, mainView.drawNode, camera)
        views.add(view)
        return view
    }

    /**
     * Convenience function: Create a single shot FrameCopy of the color attachment.
     */
    fun copyColor(): Texture2d {
        val copy = FrameCopy(this, isCopyColor = true, isCopyDepth = false, isSingleShot = true)
        frameCopies += copy
        return copy.colorCopy2d
    }

    override fun setSize(width: Int, height: Int, depth: Int) {
        super.setSize(width, height, 1)
    }

    override fun release() {
        super.release()
        impl.release()

        launchDelayed(3) {
            depthTexture?.release()
            colorTextures.forEach { it.release() }
        }
    }

    override fun applySize(width: Int, height: Int, depth: Int) {
        check(depth == 1) { "OffscreenRenderPass2d depth must be == 1" }
        super.applySize(width, height, depth)
        impl.applySize(width, height)
    }

    private fun makeColorAttachments(): List<Texture2d> {
        return if (colorAttachments is ColorAttachmentTextures) {
            (0 until numColorAttachments).map { Texture2d(createColorTextureProps(it), "${name}:color[$it]") }
        } else {
            emptyList()
        }
    }

    private fun makeDepthAttachment(): Texture2d? {
        return if (depthAttachment is DepthAttachmentTexture) {
            Texture2d(createDepthTextureProps(), "${name}:depth")
        } else {
            null
        }
    }
}

interface OffscreenPass2dImpl : Releasable {
    fun applySize(width: Int, height: Int)
}