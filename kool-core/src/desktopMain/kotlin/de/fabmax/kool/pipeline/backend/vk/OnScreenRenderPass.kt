package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR
import org.lwjgl.vulkan.VK10.*

class OnScreenRenderPass(val swapChain: SwapChain) :
    VkRenderPass(swapChain.backend, swapChain.extent.width(), swapChain.extent.height(), listOf(swapChain.imageFormat))
{

    override val vkRenderPass: Long

    init {
        memStack {
            val attachments = callocVkAttachmentDescriptionN(3) {
                this[0]
                    .format(swapChain.imageFormat)
                    .samples(physicalDevice.msaaSamples)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .finalLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                this[1]
                    .format(physicalDevice.depthFormat)
                    .samples(physicalDevice.msaaSamples)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    .storeOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .finalLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                this[2]
                    .format(swapChain.imageFormat)
                    .samples(VK_SAMPLE_COUNT_1_BIT)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR)
            }

            val colorAttachmentRef = callocVkAttachmentReferenceN(1) {
                attachment(0)
                layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            }
            val depthAttachmentRef = callocVkAttachmentReferenceN(1) {
                attachment(1)
                layout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
            }
            val colorAttachmentResolveRef  = callocVkAttachmentReferenceN(1) {
                attachment(2)
                layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            }

            val subpass = callocVkSubpassDescriptionN(1) {
                pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                colorAttachmentCount(1)
                pColorAttachments(colorAttachmentRef)
                pDepthStencilAttachment(depthAttachmentRef[0])
                pResolveAttachments(colorAttachmentResolveRef)
            }

            val dependency = callocVkSubpassDependencyN(1) {
                srcSubpass(VK_SUBPASS_EXTERNAL)
                dstSubpass(0)
                srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                srcAccessMask(0)
                dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                //dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT or VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
            }

            val renderPassInfo = callocVkRenderPassCreateInfo {
                pAttachments(attachments)
                pSubpasses(subpass)
                pDependencies(dependency)
            }

            vkRenderPass = checkCreateLongPtr { vkCreateRenderPass(logicalDevice.vkDevice, renderPassInfo, null, it) }
        }

        swapChain.addDependingResource(this)
        logD { "Created render pass" }
    }

//    fun blitFrom(src: VkOffscreenPass2d, commandBuffer: VkCommandBuffer, mipLevel: Int) {
//        logE { "Blitting render passes is not yet implemented on Vulkan backend" }
//
//        val rp = src.renderPass ?: return
//        val srcWidth = src.parentPass.width shr mipLevel
//        val srcHeight = src.parentPass.height shr mipLevel
//        val width = maxWidth
//        val height = maxHeight
//
//        if (srcWidth != width || srcHeight != height) {
//            logE { "Render pass blitting requires source and destination pass to have the same size" }
//        }
//
//        memStack {
//            val srcImage = rp.images[0]
//            swapChain.colorImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
//            srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
//            val imageCopy = callocVkImageCopyN(1) {
//                srcSubresource {
//                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//                    it.mipLevel(mipLevel)
//                    it.baseArrayLayer(0)
//                    it.layerCount(1)
//                }
//                srcOffset { it.set(0, 0, 0) }
//                dstSubresource {
//                    it.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
//                    it.mipLevel(mipLevel)
//                    it.baseArrayLayer(0)
//                    it.layerCount(1)
//                }
//                dstOffset { it.set(0, 0, 0) }
//                extent { it.set(width, height, 1) }
//            }
//            vkCmdCopyImage(commandBuffer, srcImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL, swapChain.colorImage.vkImage, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, imageCopy)
//            srcImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
//            swapChain.colorImage.transitionLayout(this, commandBuffer, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
//        }
//    }

    override fun freeResources() {
        vkDestroyRenderPass(logicalDevice.vkDevice, vkRenderPass, null)
        logD { "Destroyed render pass" }
    }
}