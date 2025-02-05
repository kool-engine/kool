package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Releasable

/**
 * Copies the color and / or depth texture outputs of a render pass. [drawGroupId] can be used to capture
 * the render state after the given draw group is rendered. The draw group ID does not have to match an existing
 * draw group, it only determines the time when the render pass output is captured. E.g. setting [drawGroupId] to
 * -1 will capture the render output before the default draw group (ID = 0) is drawn.
 */
class FrameCopy(
    renderPass: RenderPass,
    val isCopyColor: Boolean,
    val isCopyDepth: Boolean,
    val drawGroupId: Int = 0,
    val isSingleShot: Boolean = false,
) : BaseReleasable() {

    val colorCopy2d: Texture2d get() = colorCopy[0] as Texture2d
    val depthCopy2d: Texture2d get() = depthCopy!! as Texture2d

    val colorCopyCube: TextureCube get() = colorCopy[0] as TextureCube
    val depthCopyCube: TextureCube get() = depthCopy!! as TextureCube

    val colorCopy: List<Texture<*>> = if (!isCopyColor) emptyList() else buildList {
        when (renderPass) {
            is Scene.ScreenPass -> {
                val tex = Texture2d(
                    TextureProps(
                        format = TexFormat.RGBA,
                        isMipMapped = false,
                        defaultSamplerSettings = SamplerSettings().nearest().clamped()
                    ),
                    "${renderPass.parentScene}:color-copy"
                )
                add(tex)
            }
            is OffscreenPass2d -> {
                renderPass.colors.forEach {
                    add(Texture2d(it.texture.props, "${it.texture.name}:copy"))
                }
            }
            is OffscreenPassCube -> {
                renderPass.colors.forEach {
                    add(TextureCube(it.texture.props, "${it.texture.name}:copy"))
                }
            }
            else -> error("Invalid render pass type: $renderPass")
        }
    }

    val depthCopy: Texture<*>? = if (!isCopyDepth) null else {
        when (renderPass) {
            is Scene.ScreenPass -> {
                Texture2d(
                    TextureProps(
                        format = TexFormat.R_F32,
                        isMipMapped = false,
                        defaultSamplerSettings = SamplerSettings().nearest().clamped()
                    ),
                    "${renderPass.parentScene}:depth-copy"
                )
            }
            is OffscreenPass2d -> {
                renderPass.depth?.let { Texture2d(it.texture.props, "${it.texture.name}:copy") }
            }
            is OffscreenPassCube -> {
                renderPass.depth?.let { TextureCube(it.texture.props, "${it.texture.name}:copy") }
            }
            else -> error("Invalid render pass type: $renderPass")
        }
    }

    internal var gpuFrameCopy: Releasable? = null

    override fun release() {
        super.release()
        gpuFrameCopy?.release()
        colorCopy.forEach { it.release() }
        depthCopy?.release()
    }
}