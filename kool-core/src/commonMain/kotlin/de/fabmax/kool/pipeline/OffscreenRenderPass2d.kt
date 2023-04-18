package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.launchDelayed

open class OffscreenRenderPass2d(drawNode: Node, config: Config) : OffscreenRenderPass(drawNode, config) {

    internal val impl = OffscreenPass2dImpl(this)

    val depthTexture = makeDepthAttachmentTex()
    val colorTextures = makeColorAttachmentTexs()
    val colorTexture: Texture2d?
        get() = if (colorTextures.isNotEmpty()) colorTextures[0] else null

    val copyTargetsColor = mutableListOf<Texture2d>()

    fun copyColor(): Texture2d {
        val tex = Texture2d(getColorTexProps(), "$name-copy-${copyTargetsColor.size}")
        copyTargetsColor += tex
        return tex
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        impl.dispose(ctx)

        launchDelayed(3) {
            if (config.depthAttachment?.providedTexture == null) {
                depthTexture?.dispose()
            }
            colorTextures.forEachIndexed { i, tex ->
                if (!config.colorAttachments[i].isProvided) {
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
        return config.colorAttachments.mapIndexed { i, texCfg ->
            if (texCfg.isProvided) {
                texCfg.providedTexture!! as Texture2d
            } else {
                val name = "${name}_color[$i]"
                val props = texCfg.getTextureProps(config.mipLevels > 1)
                Texture2d(props, name)
            }
        }
    }

    private fun makeDepthAttachmentTex(): Texture2d? {
        return config.depthAttachment?.let { texCfg ->
            if (texCfg.isProvided) {
                texCfg.providedTexture!! as Texture2d
            } else {
                val name = "${name}_depth"
                val props = texCfg.getTextureProps(config.mipLevels > 1)
                Texture2d(props, name)
            }
        }
    }
}

expect class OffscreenPass2dImpl(offscreenPass: OffscreenRenderPass2d) {
    fun applySize(width: Int, height: Int, ctx: KoolContext)

    fun dispose(ctx: KoolContext)
}