package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.BaseReleasable

/**
 * Copies the color and / or depth texture outputs of a renderpass / view. [drawGroupId] can be used to capture
 * the render state after the given draw group is rendered. The draw group ID does not have to match an existing
 * draw group, it only determines the time when the render pass output is captured. E.g. setting [drawGroupId] to
 * -1 will capture the render output before the default draw group (ID = 0) is drawn.
 *
 * @see RenderPass.View.copyOutput
 * @see OffscreenPass2d.copyColor
 * @see OffscreenPassCube.copyColor
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
                    format = TexFormat.RGBA,
                    mipMapping = MipMapping.Off,
                    samplerSettings = SamplerSettings().nearest().clamped(),
                    name = "${renderPass.parentScene}:color-copy"
                )
                add(tex)
            }
            is OffscreenPass2d -> {
                renderPass.colorTextures.forEach {
                    add(Texture2d(it.format, it.mipMapping, it.samplerSettings, "${it.name}:color-copy"))
                }
            }
            is OffscreenPassCube -> {
                renderPass.colorTextures.forEach {
                    add(TextureCube(it.format, it.mipMapping, it.samplerSettings, "${it.name}:color-copy"))
                }
            }
            else -> error("Invalid render pass type: $renderPass")
        }
    }

    val depthCopy: Texture<*>? = if (!isCopyDepth) null else {
        when (renderPass) {
            is Scene.ScreenPass -> {
                Texture2d(
                    format = TexFormat.R_F32,
                    mipMapping = MipMapping.Off,
                    samplerSettings = SamplerSettings().nearest().clamped(),
                    name = "${renderPass.parentScene}:depth-copy"
                )
            }
            is OffscreenPass2d -> {
                renderPass.depthTexture?.let {
                    Texture2d(it.format, it.mipMapping, it.samplerSettings, "${it.name}:depth-copy")
                }
            }
            is OffscreenPassCube -> {
                renderPass.depthTexture?.let {
                    TextureCube(it.format, it.mipMapping, it.samplerSettings, "${it.name}:depth-copy")
                }
            }
            else -> error("Invalid render pass type: $renderPass")
        }
    }

    override fun release() {
        super.release()
        colorCopy.forEach { it.release() }
        depthCopy?.release()
    }
}