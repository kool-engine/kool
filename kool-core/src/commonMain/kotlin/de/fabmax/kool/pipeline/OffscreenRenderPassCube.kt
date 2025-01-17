package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.math.deg
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Releasable
import de.fabmax.kool.util.launchDelayed

open class OffscreenRenderPassCube(drawNode: Node, attachmentConfig: AttachmentConfig, initialSize: Vec2i, name: String) :
    OffscreenRenderPass(attachmentConfig, Vec3i(initialSize.x, initialSize.y, 6), name)
{
    override val views: List<View> = ViewDirection.entries.mapIndexed { i, dir ->
        val cam = PerspectiveCamera()
        cam.fovY = 90f.deg
        cam.clipNear = 0.01f
        cam.clipFar = 10f
        cam.setupCamera(position = Vec3f.ZERO, up = dir.up, lookAt = dir.lookAt)
        View(dir.toString(), drawNode, cam).apply { setFullscreenViewport() }.apply {
            isReleaseDrawNode = i == 0
            isUpdateDrawNode = i == 0
        }
    }

    var drawNode: Node = drawNode
        set(value) {
            field = value
            views.forEach { it.drawNode = value }
        }

    val depthTexture = makeDepthAttachment()
    val colorTextures = makeColorAttachments()
    val colorTexture: TextureCube?
        get() = colorTextures.getOrNull(0)

    internal val impl = KoolSystem.requireContext().backend.createOffscreenPassCube(this)

    init {
        if (attachmentConfig.depthAttachment is DepthAttachmentTexture) {
            throw RuntimeException("CubeMapDepthTexture not yet implemented")
        }
        if (numColorAttachments > 1) {
            throw RuntimeException("CubeMap multiple render targets not yet implemented")
        }
    }

    /**
     * Convenience function: Create a single shot FrameCopy of the color attachment.
     */
    fun copyColor(): TextureCube {
        val copy = FrameCopy(this, isCopyColor = true, isCopyDepth = false, isSingleShot = true)
        frameCopies += copy
        return copy.colorCopyCube
    }

    override fun setSize(width: Int, height: Int, depth: Int) {
        super.setSize(width, height, 6)
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
        check(depth == 6) { "OffscreenRenderPassCube depth must be == 6" }
        super.applySize(width, height, depth)
        impl.applySize(width, height)
    }

    private fun makeColorAttachments(): List<TextureCube> {
        return if (colorAttachments is ColorAttachmentTextures) {
            (0 until numColorAttachments).map { TextureCube(createColorTextureProps(it), "${name}_color[$it]") }
        } else {
            emptyList()
        }
    }

    private fun makeDepthAttachment(): TextureCube? {
        return if (depthAttachment is DepthAttachmentTexture) {
            TextureCube(createDepthTextureProps(), "${name}_depth")
        } else {
            null
        }
    }

    enum class ViewDirection(val index: Int, val lookAt: Vec3f, val up: Vec3f) {
        POS_X(0, Vec3f( 1f,  0f,  0f), Vec3f.NEG_Y_AXIS),
        NEG_X(1, Vec3f(-1f,  0f,  0f), Vec3f.NEG_Y_AXIS),
        POS_Y(2, Vec3f( 0f,  1f,  0f), Vec3f.Z_AXIS),
        NEG_Y(3, Vec3f( 0f, -1f,  0f), Vec3f.NEG_Z_AXIS),
        POS_Z(4, Vec3f( 0f,  0f,  1f), Vec3f.NEG_Y_AXIS),
        NEG_Z(5, Vec3f( 0f,  0f, -1f), Vec3f.NEG_Y_AXIS),
    }
}

interface OffscreenPassCubeImpl : Releasable {
    fun applySize(width: Int, height: Int)
}