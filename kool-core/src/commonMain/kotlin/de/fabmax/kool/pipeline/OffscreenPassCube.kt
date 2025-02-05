package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.math.deg
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Releasable

open class OffscreenPassCube(
    drawNode: Node,
    attachmentConfig: AttachmentConfig,
    initialSize: Vec2i,
    name: String,
    numSamples: Int = 1,
    mipMode: MipMode = MipMode.Single,
) : OffscreenPass(numSamples, mipMode, Vec3i(initialSize, 6), name) {

    override val colors: List<ColorAttachment> = attachmentConfig.colors.mapIndexed { i, cfg -> ColorAttachment(cfg, i) }
    override val depth: DepthAttachment? = attachmentConfig.depth?.let { DepthAttachment(it) }

    val colorTexture: TextureCube? get() = colors.getOrNull(0)?.texture
    val depthTexture: TextureCube? get() = depth?.texture

    override val views: List<View> = ViewDirection.entries.mapIndexed { i, dir ->
        val cam = PerspectiveCamera()
        cam.fovY = 90f.deg
        cam.clipNear = 0.01f
        cam.clipFar = 10f
        cam.setupCamera(position = Vec3f.ZERO, up = dir.up, lookAt = dir.lookAt)
        View(dir.toString(), drawNode, cam).apply {
            viewport.set(0, 0, width, height)
            isReleaseDrawNode = i == 0
            isUpdateDrawNode = i == 0
        }
    }

    var drawNode: Node = drawNode
        set(value) {
            field = value
            views.forEach { it.drawNode = value }
        }

    internal val impl = KoolSystem.requireContext().backend.createOffscreenPassCube(this)

    /**
     * Convenience function: Create a single shot FrameCopy of the color attachment.
     */
    fun copyColor(): TextureCube {
        val copy = FrameCopy(this, isCopyColor = true, isCopyDepth = false, isSingleShot = true)
        frameCopies += copy
        return copy.colorCopyCube
    }

    fun setSize(width: Int, height: Int) {
        super.setSize(width, height, 6)
    }

    override fun release() {
        super.release()
        impl.release()
        depth?.texture?.release()
        colors.forEach { it.texture.release() }
    }

    override fun applySize(width: Int, height: Int, layers: Int) {
        require(layers == 6) { "OffscreenRenderPassCube layers must be == 6" }
        super.applySize(width, height, layers)
        impl.applySize(width, height)
    }

    inner class ColorAttachment(config: TextureAttachmentConfig, i: Int) : RenderPassColorTextureAttachment<TextureCube> {
        override val texture: TextureCube = TextureCube(config.createTextureProps(mipMode.hasMipLevels), "${name}:color[$i]")
        override var clearColor: ClearColor = config.clearColor
    }

    inner class DepthAttachment(config: TextureAttachmentConfig) : RenderPassDepthTextureAttachment<TextureCube> {
        override val texture: TextureCube = TextureCube(config.createTextureProps(mipMode.hasMipLevels), "${name}:depth")
        override var clearDepth: ClearDepth = config.clearDepth
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