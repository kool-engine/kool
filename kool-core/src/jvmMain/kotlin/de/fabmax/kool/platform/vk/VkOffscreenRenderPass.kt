package de.fabmax.kool.platform.vk

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*

class VkOffscreenRenderPass(sys: VkSystem, maxWidth: Int, maxHeight: Int,
                            val colorAttachments: ColorAttachments, val isExtColorAttachments: Boolean,
                            val depthAttachment: DepthAttachment, val isExtDepthAttachments: Boolean) :
        VkRenderPass(sys, maxWidth, maxHeight, colorAttachments.colorFormats) {

    constructor(sys: VkSystem, maxWidth: Int, maxHeight: Int, isCopied: Boolean, texFormat: Int,
                colorFilterMethod: Int = VK_FILTER_LINEAR, depthFilterMethod: Int = VK_FILTER_NEAREST, depthCopmpareOp: Int = VK_COMPARE_OP_NEVER) :
            this(sys, maxWidth, maxHeight, isCopied, listOf(texFormat), colorFilterMethod, depthFilterMethod, depthCopmpareOp)

    constructor(sys: VkSystem, maxWidth: Int, maxHeight: Int, isCopied: Boolean, texFormats: List<Int>,
                colorFilterMethod: Int = VK_FILTER_LINEAR, depthFilterMethod: Int = VK_FILTER_NEAREST, depthCopmpareOp: Int = VK_COMPARE_OP_NEVER) :
            this(sys, maxWidth, maxHeight,
                    CreatedColorAttachments(sys, maxWidth, maxHeight, isCopied, texFormats, colorFilterMethod), false,
                    CreatedDepthAttachment(sys, maxWidth, maxHeight, isCopied, depthFilterMethod, depthCopmpareOp), false)

    override val vkRenderPass: Long

    val images: List<Image>
        get() = colorAttachments.colorImages
    val imageViews: List<ImageView>
        get() = colorAttachments.colorImageViews
    val samplers: List<Long>
        get() = colorAttachments.colorSamplers

    val image: Image
        get() = images[0]
    val imageView: ImageView
        get() = imageViews[0]
    val sampler: Long
        get() = samplers[0]

    val depthImage: Image
        get() = depthAttachment.depthImage
    val depthImageView: ImageView
        get() = depthAttachment.depthImageView
    val depthSampler: Long
        get() = depthAttachment.depthSampler

    val frameBuffer: Long

    init {
        triFrontDirection = VK_FRONT_FACE_CLOCKWISE

        vkRenderPass = createRenderPass()
        frameBuffer = createFrameBuffer(vkRenderPass, imageViews, depthImageView)

        if (!isExtColorAttachments) {
            addDependingResource(colorAttachments)
        }
        if (!isExtDepthAttachments) {
            addDependingResource(depthAttachment)
        }
        sys.device.addDependingResource(this)

        logD { "Created offscreen render pass" }
    }

    fun destroyNow() {
        sys.device.removeDependingResource(this)
        destroy()
    }

    override fun freeResources() {
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
                val colorLoadOp = if (isExtColorAttachments) {
                    VK_ATTACHMENT_LOAD_OP_LOAD
                } else {
                    VK_ATTACHMENT_LOAD_OP_CLEAR
                }
                val depthLoadOp = if (isExtDepthAttachments) {
                    VK_ATTACHMENT_LOAD_OP_LOAD
                } else {
                    VK_ATTACHMENT_LOAD_OP_CLEAR
                }

                for (i in colorFormats.indices) {
                    this[i].apply {
                        format(colorFormats[i])
                        samples(VK_SAMPLE_COUNT_1_BIT)
                        loadOp(colorLoadOp)
                        storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                        stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                        stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                        initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)

                        if (colorAttachments.isCopied) {
                            finalLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                        } else {
                            finalLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                        }
                    }
                }
                this[nColorAttachments].apply {
                    format(sys.physicalDevice.depthFormat)
                    samples(VK_SAMPLE_COUNT_1_BIT)
                    loadOp(depthLoadOp)
                    storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)

                    if (depthAttachment.isCopied) {
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

    abstract class ColorAttachments(val isCopied: Boolean, val colorFormats: List<Int>) : VkResource() {
        abstract val colorImages: List<Image>
        abstract val colorImageViews: List<ImageView>
        abstract val colorSamplers: List<Long>
    }

    abstract class DepthAttachment(val isCopied: Boolean) : VkResource() {
        abstract val depthImage: Image
        abstract val depthImageView: ImageView
        abstract val depthSampler: Long
    }

    class ProvidedColorAttachments(isCopied: Boolean, images: List<Image>, imageViews: List<ImageView>, samplers: List<Long>) : ColorAttachments(isCopied, images.map { it.format }) {
        override val colorImages: List<Image> = images
        override val colorImageViews: List<ImageView> = imageViews
        override val colorSamplers: List<Long> = samplers

        override fun freeResources() { }
    }

    class CreatedColorAttachments(val sys: VkSystem, maxWidth: Int, maxHeight: Int, isCopied: Boolean,
                                  colorFormats: List<Int>, filterMethod: Int) :
            ColorAttachments(isCopied, colorFormats) {
        override val colorImages: List<Image>
        override val colorImageViews: List<ImageView>
        override val colorSamplers: List<Long>

        init {
            val mImages = mutableListOf<Image>()
            val mImageViews = mutableListOf<ImageView>()
            val mSamplers = mutableListOf<Long>()
            for (i in colorFormats.indices) {
                val fbImageCfg = Image.Config().apply {
                    width = maxWidth
                    height = maxHeight
                    format = colorFormats[i]
                    tiling = VK_IMAGE_TILING_OPTIMAL
                    usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT or VK_IMAGE_USAGE_TRANSFER_SRC_BIT or if (!isCopied) VK_IMAGE_USAGE_SAMPLED_BIT else 0
                    allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
                }

                val img = Image(sys, fbImageCfg)
                mImages += img
                mImageViews += ImageView(sys, img, VK_IMAGE_ASPECT_COLOR_BIT)
                mSamplers += createSampler(sys, filterMethod, false, VK_COMPARE_OP_NEVER)
            }
            colorImages = mImages
            colorImageViews = mImageViews
            colorSamplers = mSamplers

            colorImages.forEach { addDependingResource(it) }
            colorImageViews.forEach { addDependingResource(it) }
        }

        override fun freeResources() {
            colorSamplers.forEach { vkDestroySampler(sys.device.vkDevice, it, null) }
        }
    }

    class ProvidedDepthAttachment(isCopied: Boolean, image: Image, imageView: ImageView, sampler: Long) : DepthAttachment(isCopied) {
        override val depthImage: Image = image
        override val depthImageView: ImageView = imageView
        override val depthSampler: Long = sampler

        override fun freeResources() { }
    }

    class CreatedDepthAttachment(val sys: VkSystem, maxWidth: Int, maxHeight: Int, isCopied: Boolean,
                                 filterMethod: Int, depthCompareOp: Int) :
            DepthAttachment(isCopied) {
        override val depthImage: Image
        override val depthImageView: ImageView
        override val depthSampler: Long

        init {
            val depthImageCfg = Image.Config().apply {
                width = maxWidth
                height = maxHeight
                format = sys.physicalDevice.depthFormat
                tiling = VK_IMAGE_TILING_OPTIMAL
                usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT or if (isCopied) VK_IMAGE_USAGE_TRANSFER_SRC_BIT else VK_IMAGE_USAGE_SAMPLED_BIT
                allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
            }
            depthImage = Image(sys, depthImageCfg)
            depthImageView = ImageView(sys, depthImage, VK_IMAGE_ASPECT_DEPTH_BIT)
            depthSampler = createSampler(sys, filterMethod, true, depthCompareOp)

            addDependingResource(depthImage)
            addDependingResource(depthImageView)
        }

        override fun freeResources() {
            vkDestroySampler(sys.device.vkDevice, depthSampler, null)
        }
    }

    companion object {
        private fun createSampler(sys: VkSystem, filterMethod: Int, isDepth: Boolean, depthCompareOp: Int): Long {
            memStack {
                val samplerInfo = callocVkSamplerCreateInfo {
                    sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                    magFilter(filterMethod)
                    minFilter(filterMethod)
                    mipmapMode(if (filterMethod == VK_FILTER_NEAREST) VK_SAMPLER_MIPMAP_MODE_NEAREST else VK_SAMPLER_MIPMAP_MODE_LINEAR)
                    addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                    addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                    addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                    anisotropyEnable(false)
                    maxAnisotropy(1f)
                    minLod(0f)
                    maxLod(1f)
                    borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)

                    if (isDepth && filterMethod == VK_FILTER_LINEAR) {
                        compareEnable(true)
                        compareOp(depthCompareOp)
                    }
                }
                val lp = mallocLong(1)
                check(vkCreateSampler(sys.device.vkDevice, samplerInfo, null, lp) == VK_SUCCESS)
                return lp[0]
            }
        }
    }
}