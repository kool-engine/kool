package de.fabmax.kool.platform.vk

import de.fabmax.kool.pipeline.OffscreenPass2dImpl
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.vk.util.vkFormat

class VkOffscreenPass2d(val parentPass: OffscreenPass2dImpl) : OffscreenPass2dImpl.BackendImpl {
    private var isCreated = false
    private var blockCreation = false

    var renderPass: VkOffscreenRenderPass? = null
        private set

    var colorAttachments: VkOffscreenRenderPass.ColorAttachments? = null
    var depthAttachment: VkOffscreenRenderPass.DepthAttachment? = null

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated && !blockCreation) {
            create(ctx)
        }
    }

    override fun dispose(ctx: Lwjgl3Context) {
        val rp = renderPass
        val loadedColorTex = parentPass.colorTexture.loadedTexture
        val loadedDepthTex = parentPass.depthTexture.loadedTexture

        isCreated = false
        renderPass = null

        if (!parentPass.isExtColorTexture) {
            parentPass.colorTexture.clear()
        }
        if (!parentPass.isExtDepthTexture) {
            parentPass.depthTexture.clear()
        }
        colorAttachments = null
        depthAttachment = null

        ctx.runDelayed(3) {
            rp?.destroyNow()
            if (!parentPass.isExtColorTexture) {
                loadedColorTex?.dispose()
            }
            if (!parentPass.isExtDepthTexture) {
                loadedDepthTex?.dispose()
            }
        }
    }

    private fun Texture.clear() {
        loadedTexture = null
        loadingState = Texture.LoadingState.NOT_LOADED
    }

    override fun resize(width: Int, height: Int, ctx: Lwjgl3Context) {
        dispose(ctx)

        if (!parentPass.isExtColorTexture && !parentPass.isExtDepthTexture) {
            create(ctx)

        } else {
            blockCreation = true
            ctx.runDelayed(3) {
                blockCreation = false
                create(ctx)
            }
        }
    }

    private fun create(ctx: Lwjgl3Context) {
        val sys = (ctx.renderBackend as VkRenderBackend).vkSystem
        parentPass.apply {
            if (colorAttachments == null) {
                colorAttachments = if (isExtColorTexture) {
                    val vkTex = colorTexture.loadedTexture as LoadedTextureVk?
                    if (vkTex != null) {
                        VkOffscreenRenderPass.ProvidedColorAttachments(false, listOf(vkTex.textureImage), listOf(vkTex.textureImageView), listOf(vkTex.sampler))
                    } else {
                        null
                    }
                } else {
                    VkOffscreenRenderPass.CreatedColorAttachments(sys, offscreenPass.texWidth, offscreenPass.texHeight, false, listOf(offscreenPass.colorFormat.vkFormat))
                }
            }

            if (depthAttachment == null) {
                depthAttachment = if (isExtDepthTexture) {
                    val vkTex = depthTexture.loadedTexture as LoadedTextureVk?
                    if (vkTex != null) {
                        VkOffscreenRenderPass.ProvidedDepthAttachment(false, vkTex.textureImage, vkTex.textureImageView, vkTex.sampler)
                    } else {
                        null
                    }
                } else {
                    VkOffscreenRenderPass.CreatedDepthAttachment(sys, offscreenPass.texWidth, offscreenPass.texHeight, false)
                }
            }

            val ca = colorAttachments
            val da = depthAttachment
            if (ca != null && da != null) {
                val rp = VkOffscreenRenderPass(sys, offscreenPass.texWidth, offscreenPass.texHeight, ca, isExtColorTexture, da, isExtDepthTexture)

                if (!isExtColorTexture) {
                    createTex(colorTexture, true, rp, sys)
                }
                if (!isExtDepthTexture) {
                    createTex(depthTexture, false, rp, sys)
                }
                renderPass = rp
                isCreated = true
            }
        }
    }

    private fun createTex(tex: Texture, isColor: Boolean, rp: VkOffscreenRenderPass, sys: VkSystem) {
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