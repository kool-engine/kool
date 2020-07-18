package de.fabmax.kool.platform.vk

import de.fabmax.kool.pipeline.OffscreenPass2dMrtImpl
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.vk.util.vkFormat
import org.lwjgl.vulkan.VK10.VK_FILTER_NEAREST

class VkOffscreenPass2dMrt(val parentPass: OffscreenPass2dMrtImpl) : OffscreenPass2dMrtImpl.BackendImpl {
    private var isCreated = false

    var renderPass: VkOffscreenRenderPass? = null
        private set

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated) {
            create(ctx)
            isCreated = true
        }
    }

    override fun dispose(ctx: Lwjgl3Context) {
        val rp = renderPass
        val loadedColorTexs = parentPass.colorTextures.map { it.loadedTexture }
        val loadedDepthTex = parentPass.depthTexture.loadedTexture

        isCreated = false
        renderPass = null
        parentPass.colorTextures.forEach { it.clear() }
        parentPass.depthTexture.clear()

        ctx.runDelayed(3) {
            rp?.destroyNow()
            loadedColorTexs.forEach { it?.dispose() }
            loadedDepthTex?.dispose()
        }
    }

    private fun Texture.clear() {
        loadedTexture = null
        loadingState = Texture.LoadingState.NOT_LOADED
    }

    override fun resize(width: Int, height: Int, ctx: Lwjgl3Context) {
        dispose(ctx)
        create(ctx)
    }

    private fun create(ctx: Lwjgl3Context) {
        val sys = (ctx.renderBackend as VkRenderBackend).vkSystem
        parentPass.apply {
            val colorFormats = offscreenPass.texFormats.map { it.vkFormat }
            val rp = VkOffscreenRenderPass(sys, offscreenPass.texWidth, offscreenPass.texHeight, false, colorFormats, colorFilterMethod = VK_FILTER_NEAREST)
            for (i in colorTextures.indices) {
                createTex(colorTextures[i], i, true, rp, sys)
            }
            createTex(depthTexture, 0, false, rp, sys)
            renderPass = rp
        }
    }

    private fun createTex(tex: Texture, iAttachment: Int, isColor: Boolean, rp: VkOffscreenRenderPass, sys: VkSystem) {
        tex.apply {
            loadedTexture = if (isColor) {
                LoadedTextureVk(sys, rp.getTexFormat(iAttachment), rp.images[iAttachment], rp.imageViews[iAttachment], rp.samplers[iAttachment], true)
            } else {
                LoadedTextureVk(sys, rp.texFormat, rp.depthImage, rp.depthImageView, rp.depthSampler, true)
            }
            loadingState = Texture.LoadingState.LOADED
        }
    }
}