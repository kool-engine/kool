package de.fabmax.kool.platform.vk

import de.fabmax.kool.pipeline.OffscreenPass2dImpl
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.vk.util.OffscreenRenderPass
import de.fabmax.kool.platform.vk.util.vkFormat

class OffscreenPass2dVk(val parentPass: OffscreenPass2dImpl) : OffscreenPass2dImpl.BackendImpl {
    private var isCreated = false

    var renderPass: OffscreenRenderPass? = null
        private set

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated) {
            create(ctx)
            isCreated = true
        }
    }

    override fun dispose(ctx: Lwjgl3Context) {
        ctx.runDelayed(3) {
            renderPass?.destroyNow()
            parentPass.texture.dispose()
            parentPass.depthTexture.dispose()
        }
    }

    private fun create(ctx: Lwjgl3Context) {
        val sys = (ctx.renderBackend as VkRenderBackend).vkSystem
        parentPass.apply {
            val rp = OffscreenRenderPass(sys, offscreenPass.texWidth, offscreenPass.texHeight, false, offscreenPass.colorFormat.vkFormat)
            createTex(texture, true, rp, sys)
            createTex(depthTexture, false, rp, sys)
            renderPass = rp
        }
    }

    private fun createTex(tex: Texture, isColor: Boolean, rp: OffscreenRenderPass, sys: VkSystem) {
        tex.apply {
            loadedTexture = if (isColor) {
                LoadedTextureVk(sys, rp.texFormat, rp.image, rp.imageView, rp.sampler, true)
            } else {
                LoadedTextureVk(sys, rp.texFormat, rp.depthImage, rp.depthImageView, rp.depthSampler, true)
            }
            loadingState = Texture.LoadingState.LOADED
        }
    }
}