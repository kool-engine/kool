package de.fabmax.kool.platform.vk.util

import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.util.logD
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*

class OffscreenRenderPass(sys: VkSystem, maxWidth: Int, maxHeight: Int, val isCopied: Boolean, texFormat: Int) :
        RenderPass(sys, maxWidth, maxHeight, texFormat) {

    override val vkRenderPass: Long

    val image: Image
    val imageView: ImageView
    val frameBuffer: Long
    val sampler: Long

    init {
        val fbImageCfg = Image.Config().apply {
            width = maxWidth
            height = maxHeight
            format = texFormat
            tiling = VK_IMAGE_TILING_OPTIMAL
            usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT or if (isCopied) { VK_IMAGE_USAGE_TRANSFER_SRC_BIT } else { VK_IMAGE_USAGE_SAMPLED_BIT }
            allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
        }

        image = Image(sys, fbImageCfg)
        imageView = ImageView(sys, image, VK_IMAGE_ASPECT_COLOR_BIT)
        sampler = createSampler()

        fbImageCfg.format = sys.physicalDevice.depthFormat
        fbImageCfg.usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT
        val depthImage = Image(sys, fbImageCfg)
        val depthStencilView = ImageView(sys, depthImage, VK_IMAGE_ASPECT_DEPTH_BIT)

        vkRenderPass = createRenderPass()
        frameBuffer = createFrameBuffer(vkRenderPass, imageView, depthStencilView)

        addDependingResource(image)
        addDependingResource(imageView)
        addDependingResource(depthImage)
        addDependingResource(depthStencilView)

        sys.device.addDependingResource(this)

        logD { "Created offscreen render pass" }
    }

    // fixme: duplicate from Image, add ImageSampler class?
    private fun createSampler(): Long {
        memStack {
            val samplerInfo = callocVkSamplerCreateInfo {
                sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                magFilter(VK_FILTER_LINEAR)
                minFilter(VK_FILTER_LINEAR)
                mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR)
                addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                anisotropyEnable(false)
                maxAnisotropy(1f)
                minLod(0f)
                maxLod(1f)
                borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)
            }
            return checkCreatePointer { vkCreateSampler(sys.device.vkDevice, samplerInfo, null, it) }
        }
    }

    override fun freeResources() {
        vkDestroySampler(sys.device.vkDevice, sampler, null)
        vkDestroyRenderPass(sys.device.vkDevice, vkRenderPass, null)
        vkDestroyFramebuffer(sys.device.vkDevice, frameBuffer, null)
        logD { "Destroyed offscreen render pass" }
    }

    private fun createFrameBuffer(renderPass: Long, imageView: ImageView, depthView: ImageView): Long {
        memStack {
            val framebufferInfo = callocVkFramebufferCreateInfo {
                sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                renderPass(renderPass)
                pAttachments(longs(imageView.vkImageView, depthView.vkImageView))
                width(maxWidth)
                height(maxHeight)
                layers(1)
            }
            return checkCreatePointer { vkCreateFramebuffer(sys.device.vkDevice, framebufferInfo, null, it) }
        }
    }

    private fun createRenderPass(): Long {
        // fixme: merge with RenderPass class (which currently is tailored to swap chain)
        memStack {
            val attachments = callocVkAttachmentDescriptionN(2) {
                this[0].apply {
                    format(colorFormat)
                    samples(VK_SAMPLE_COUNT_1_BIT)
                    loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)

                    if (isCopied) {
                        finalLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                    } else {
                        finalLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                    }
                }
                this[1]
                        .format(sys.physicalDevice.depthFormat)
                        .samples(VK_SAMPLE_COUNT_1_BIT)
                        .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                        .storeOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                        .finalLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
            }

            val colorAttachmentRef = callocVkAttachmentReferenceN(1) {
                attachment(0)
                layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            }
            val depthAttachmentRef = callocVkAttachmentReferenceN(1) {
                attachment(1)
                layout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
            }

            val subpass = callocVkSubpassDescriptionN(1) {
                pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                colorAttachmentCount(1)
                pColorAttachments(colorAttachmentRef)
                pDepthStencilAttachment(depthAttachmentRef[0])
            }

            val dependencies = callocVkSubpassDependencyN(2) {
                this[0]
                        .srcSubpass(VK_SUBPASS_EXTERNAL)
                        .dstSubpass(0)
                        .srcStageMask(VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT)
                        .srcAccessMask(VK_ACCESS_SHADER_READ_BIT)
                        .dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                        .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                        .dependencyFlags(VK_DEPENDENCY_BY_REGION_BIT)
                this[1]
                        .srcSubpass(0)
                        .dstSubpass(VK_SUBPASS_EXTERNAL)
                        .srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                        .srcAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                        .dstStageMask(VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT)
                        .dstAccessMask(VK_ACCESS_SHADER_READ_BIT)
                        .dependencyFlags(VK_DEPENDENCY_BY_REGION_BIT)
            }

            val renderPassInfo = callocVkRenderPassCreateInfo {
                sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                pAttachments(attachments)
                pSubpasses(subpass)
                pDependencies(dependencies)
            }
            return checkCreatePointer { vkCreateRenderPass(sys.device.vkDevice, renderPassInfo, null, it) }
        }
    }
}