package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.logD
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*

class VkOffscreenRenderPass(sys: VkSystem, maxWidth: Int, maxHeight: Int, val isCopied: Boolean, texFormats: List<Int>, private val depthCopmpareOp: Int = VK_COMPARE_OP_LESS) :
        VkRenderPass(sys, maxWidth, maxHeight, texFormats) {

    constructor(sys: VkSystem, maxWidth: Int, maxHeight: Int, isCopied: Boolean, texFormat: Int, depthCopmpareOp: Int = VK_COMPARE_OP_LESS) :
            this(sys, maxWidth, maxHeight, isCopied, listOf(texFormat), depthCopmpareOp)

    override val vkRenderPass: Long

    val images: List<Image>
    val imageViews: List<ImageView>
    val samplers: List<Long>

    val image: Image
        get() = images[0]
    val imageView: ImageView
        get() = imageViews[0]
    val sampler: Long
        get() = samplers[0]

    val depthImage: Image
    val depthImageView: ImageView
    val depthSampler: Long

    val frameBuffer: Long

    init {
        val mImages = mutableListOf<Image>()
        val mImageViews = mutableListOf<ImageView>()
        val mSamplers = mutableListOf<Long>()
        for (i in texFormats.indices) {
            val fbImageCfg = Image.Config().apply {
                width = maxWidth
                height = maxHeight
                format = texFormats[i]
                tiling = VK_IMAGE_TILING_OPTIMAL
                usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT or if (isCopied) { VK_IMAGE_USAGE_TRANSFER_SRC_BIT } else { VK_IMAGE_USAGE_SAMPLED_BIT }
                allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
            }
            triFrontDirection = VK_FRONT_FACE_CLOCKWISE

            val img = Image(sys, fbImageCfg)
            mImages += img
            mImageViews += ImageView(sys, img, VK_IMAGE_ASPECT_COLOR_BIT)
            mSamplers += createSampler(false)
        }
        images = mImages
        imageViews = mImageViews
        samplers = mSamplers

        val depthImageCfg = Image.Config().apply {
            width = maxWidth
            height = maxHeight
            format = sys.physicalDevice.depthFormat
            tiling = VK_IMAGE_TILING_OPTIMAL
            usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT or if (isCopied) { VK_IMAGE_USAGE_TRANSFER_SRC_BIT } else { VK_IMAGE_USAGE_SAMPLED_BIT }
            allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
        }
        depthImage = Image(sys, depthImageCfg)
        depthImageView = ImageView(sys, depthImage, VK_IMAGE_ASPECT_DEPTH_BIT)
        depthSampler = createSampler(true)

        vkRenderPass = createRenderPass()
        frameBuffer = createFrameBuffer(vkRenderPass, imageViews, depthImageView)

        images.forEach { addDependingResource(it) }
        imageViews.forEach { addDependingResource(it) }
        addDependingResource(depthImage)
        addDependingResource(depthImageView)

        sys.device.addDependingResource(this)

        logD { "Created offscreen render pass" }
    }

    // fixme: somewhat duplicate from Image, add ImageSampler class?
    private fun createSampler(isDepth: Boolean): Long {
        memStack {
            val samplerInfo = callocVkSamplerCreateInfo {
                sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                magFilter(VK_FILTER_LINEAR)
                minFilter(VK_FILTER_LINEAR)
                mipmapMode(VK_SAMPLER_MIPMAP_MODE_NEAREST)
                addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                anisotropyEnable(false)
                maxAnisotropy(1f)
                minLod(0f)
                maxLod(1f)
                borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)

                if (isDepth) {
                    compareEnable(true)
                    compareOp(depthCopmpareOp)
                }
            }
            return checkCreatePointer { vkCreateSampler(sys.device.vkDevice, samplerInfo, null, it) }
        }
    }

    fun destroyNow() {
        sys.device.removeDependingResource(this)
        destroy()
    }

    override fun freeResources() {
        samplers.forEach { vkDestroySampler(sys.device.vkDevice, it, null) }
        vkDestroySampler(sys.device.vkDevice, depthSampler, null)
        vkDestroyRenderPass(sys.device.vkDevice, vkRenderPass, null)
        vkDestroyFramebuffer(sys.device.vkDevice, frameBuffer, null)
        logD { "Destroyed offscreen render pass" }
    }

    private fun createFrameBuffer(renderPass: Long, imageViews: List<ImageView>, depthView: ImageView): Long {
        memStack {
            val attachments = mallocLong(imageViews.size + 1)
            imageViews.forEachIndexed { i, imageView -> attachments.put(i, imageView.vkImageView) }
            attachments.put(imageViews.size, depthView.vkImageView)

            val framebufferInfo = callocVkFramebufferCreateInfo {
                sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                renderPass(renderPass)
                pAttachments(attachments)
                width(maxWidth)
                height(maxHeight)
                layers(1)
            }
            return checkCreatePointer { vkCreateFramebuffer(sys.device.vkDevice, framebufferInfo, null, it) }
        }
    }

    private fun createRenderPass(): Long {
        memStack {
            val attachments = callocVkAttachmentDescriptionN(nColorAttachments + 1) {
                for (i in colorFormats.indices) {
                    this[i].apply {
                        format(colorFormats[i])
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
                }
                this[nColorAttachments].apply {
                    format(sys.physicalDevice.depthFormat)
                    samples(VK_SAMPLE_COUNT_1_BIT)
                    loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)

                    if (isCopied) {
                        finalLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                    } else {
                        finalLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                    }
                }
            }

            val colorAttachmentRefs = callocVkAttachmentReferenceN(nColorAttachments) {
                for (i in 0 until nColorAttachments) {
                    this[i].apply {
                        attachment(i)
                        layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                    }
                }
            }
            val depthAttachmentRef = callocVkAttachmentReferenceN(1) {
                attachment(nColorAttachments)
                layout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
            }

            val subpass = callocVkSubpassDescriptionN(1) {
                pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                colorAttachmentCount(nColorAttachments)
                pColorAttachments(colorAttachmentRefs)
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