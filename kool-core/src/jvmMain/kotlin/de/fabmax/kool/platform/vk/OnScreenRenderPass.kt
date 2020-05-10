package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.logD
import org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR
import org.lwjgl.vulkan.VK10.*

class OnScreenRenderPass(swapChain: SwapChain) :
        VkRenderPass(swapChain.sys, swapChain.extent.width(), swapChain.extent.height(), swapChain.imageFormat) {

    override val vkRenderPass: Long

    init {
        memStack {
            val attachments = callocVkAttachmentDescriptionN(3) {
                this[0]
                    .format(swapChain.imageFormat)
                    .samples(swapChain.sys.physicalDevice.msaaSamples)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .finalLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                this[1]
                    .format(swapChain.sys.physicalDevice.depthFormat)
                    .samples(swapChain.sys.physicalDevice.msaaSamples)
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
                dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT or VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
            }

            val renderPassInfo = callocVkRenderPassCreateInfo {
                sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                pAttachments(attachments)
                pSubpasses(subpass)
                pDependencies(dependency)
            }

            vkRenderPass = checkCreatePointer { vkCreateRenderPass(swapChain.sys.device.vkDevice, renderPassInfo, null, it) }
        }

        swapChain.addDependingResource(this)
        logD { "Created render pass" }
    }

    override fun freeResources() {
        vkDestroyRenderPass(sys.device.vkDevice, vkRenderPass, null)
        logD { "Destroyed render pass" }
    }
}