package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.VK10.*

@Deprecated("to be removed")
class OffscreenRenderPassVk(
    backend: RenderBackendVk,
    val maxWidth: Int,
    val maxHeight: Int,
    val colorAttachments: ColorAttachments,
    val isExtColorAttachments: Boolean,
    val depthAttachment: DepthAttachment,
    val isExtDepthAttachments: Boolean,
    val isMultiSampled: Boolean
) : RenderPassVk(0, 0, backend) {

    constructor(backend: RenderBackendVk, maxWidth: Int, maxHeight: Int, isCopied: Boolean, texFormat: Int,
                colorFilterMethod: Int = VK_FILTER_LINEAR, depthFilterMethod: Int = VK_FILTER_NEAREST, depthCopmpareOp: Int = VK_COMPARE_OP_NEVER) :
            this(backend, maxWidth, maxHeight, isCopied, listOf(texFormat), colorFilterMethod, depthFilterMethod, depthCopmpareOp)

    constructor(backend: RenderBackendVk, maxWidth: Int, maxHeight: Int, isCopied: Boolean, texFormats: List<Int>,
                colorFilterMethod: Int = VK_FILTER_LINEAR, depthFilterMethod: Int = VK_FILTER_NEAREST, depthCopmpareOp: Int = VK_COMPARE_OP_NEVER) :
            this(
                backend,
                maxWidth,
                maxHeight,
                CreatedColorAttachments(backend, maxWidth, maxHeight, isCopied, texFormats, colorFilterMethod, false),
                isExtColorAttachments = false,
                CreatedDepthAttachment(backend, maxWidth, maxHeight, isCopied, depthFilterMethod, depthCopmpareOp, false),
                isExtDepthAttachments = false,
                isMultiSampled = false
            )

    override val colorTargetFormats: List<Int>
        get() = TODO("Not yet implemented")

    val vkRenderPass: VkRenderPass
    //val numSamples: Int = 1

    override fun beginRenderPass(passEncoderState: RenderPassEncoderState, forceLoad: Boolean): VkRenderPass {
        TODO("Not yet implemented")
    }

    val images: List<Image>
        get() = colorAttachments.colorImages
    val imageViews: List<VkImageView>
        get() = colorAttachments.colorImageViews
    val samplers: List<Long>
        get() = colorAttachments.colorSamplers

    val image: Image
        get() = images[0]
    val imageView: VkImageView
        get() = imageViews[0]
    val sampler: Long
        get() = samplers[0]

    val depthImage: Image
        get() = depthAttachment.depthImage
    val depthImageView: VkImageView
        get() = depthAttachment.depthImageView
    val depthSampler: Long
        get() = depthAttachment.depthSampler

    val frameBuffer: Long

    init {
        //triFrontDirection = VK_FRONT_FACE_CLOCKWISE

        vkRenderPass = createRenderPass()
        frameBuffer = createFrameBuffer(vkRenderPass, imageViews, depthImageView)

        if (!isExtColorAttachments) {
            colorAttachments.releaseWith(this)
        }
        if (!isExtDepthAttachments) {
            depthAttachment.releaseWith(this)
        }
        releaseWith(backend.device)

        logD { "Created offscreen render pass" }
    }

    fun destroyNow() {
        cancelReleaseWith(backend.device)
        release()
    }

    override fun release() {
        super.release()
        vkDestroyRenderPass(device.vkDevice, vkRenderPass.handle, null)
        vkDestroyFramebuffer(device.vkDevice, frameBuffer, null)
        logD { "Destroyed offscreen render pass" }
    }

    private fun createFrameBuffer(renderPass: VkRenderPass, imageViews: List<VkImageView>, depthView: VkImageView): Long {
        memStack {
            val attachments = mallocLong(imageViews.size + 1)
            imageViews.forEachIndexed { i, imageView -> attachments.put(i, imageView.handle) }
            attachments.put(imageViews.size, depthView.handle)

            val framebufferInfo = callocVkFramebufferCreateInfo {
                sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                renderPass(renderPass.handle)
                pAttachments(attachments)
                width(maxWidth)
                height(maxHeight)
                layers(1)
            }
            return checkCreateLongPtr { vkCreateFramebuffer(device.vkDevice, framebufferInfo, null, it) }
        }
    }

    private fun createRenderPass(): VkRenderPass {
        memStack {
            val physicalDevice = backend.physicalDevice
            val colorFormats = emptyList<Int>()
            val attachments = callocVkAttachmentDescriptionN(numColorAttachments + 1) {
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

                val samples = if (isMultiSampled) physicalDevice.maxSamples else VK_SAMPLE_COUNT_1_BIT

                for (i in colorFormats.indices) {
                    this[i].apply {
                        format(colorFormats[i])
                        samples(samples)
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
                this[numColorAttachments].apply {
                    format(physicalDevice.depthFormat)
                    samples(samples)
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

            val colorAttachmentRefs = callocVkAttachmentReferenceN(numColorAttachments) {
                for (i in 0 until numColorAttachments) {
                    this[i].apply {
                        attachment(i)
                        layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                    }
                }
            }
            val depthAttachmentRef = callocVkAttachmentReferenceN(1) {
                attachment(numColorAttachments)
                layout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
            }

            val subpass = callocVkSubpassDescriptionN(1) {
                pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                colorAttachmentCount(numColorAttachments)
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

            return device.createRenderPass {
                sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                pAttachments(attachments)
                pSubpasses(subpass)
                pDependencies(dependencies)
            }
        }
    }

    override fun copy(frameCopy: FrameCopy, encoder: RenderPassEncoderState) {
        TODO("Not yet implemented")
    }

    abstract class ColorAttachments(val isCopied: Boolean, val colorFormats: List<Int>) : BaseReleasable() {
        abstract val colorImages: List<Image>
        abstract val colorImageViews: List<VkImageView>
        abstract val colorSamplers: List<Long>
    }

    abstract class DepthAttachment(val isCopied: Boolean) : BaseReleasable() {
        abstract val depthImage: Image
        abstract val depthImageView: VkImageView
        abstract val depthSampler: Long
    }

    class ProvidedColorAttachments(isCopied: Boolean, images: List<Image>, imageViews: List<VkImageView>, samplers: List<Long>) : ColorAttachments(isCopied, images.map { it.format }) {
        override val colorImages: List<Image> = images
        override val colorImageViews: List<VkImageView> = imageViews
        override val colorSamplers: List<Long> = samplers
    }

    class CreatedColorAttachments(val backend: RenderBackendVk, maxWidth: Int, maxHeight: Int, isCopied: Boolean,
                                  colorFormats: List<Int>, filterMethod: Int, multiSampling: Boolean) :
            ColorAttachments(isCopied, colorFormats) {
        override val colorImages: List<Image>
        override val colorImageViews: List<VkImageView>
        override val colorSamplers: List<Long>

        init {
            val mImages = mutableListOf<Image>()
            val mImageViews = mutableListOf<VkImageView>()
            val mSamplers = mutableListOf<Long>()
            for (i in colorFormats.indices) {
                TODO()
//                val fbImageCfg = Image.Config().apply {
//                    width = maxWidth
//                    height = maxHeight
//                    format = colorFormats[i]
//                    tiling = VK_IMAGE_TILING_OPTIMAL
//                    usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT or VK_IMAGE_USAGE_TRANSFER_SRC_BIT or if (!isCopied) VK_IMAGE_USAGE_SAMPLED_BIT else 0
//                    allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
//                    if (multiSampling) {
//                        numSamples = backend.physicalDevice.msaaSamples
//                    }
//                }
//
//                val img = Image(backend, fbImageCfg)
//                mImages += img
//                mImageViews += ImageView.imageView2d(backend.device, img, VK_IMAGE_ASPECT_COLOR_BIT)
//                mSamplers += createSampler(backend, filterMethod, false, VK_COMPARE_OP_NEVER)
            }
            colorImages = mImages
            colorImageViews = mImageViews
            colorSamplers = mSamplers

            colorImages.forEach { it.releaseWith(this) }
//            colorImageViews.forEach { it.releaseWith(this) }
        }

        override fun release() {
            super.release()
            colorSamplers.forEach { vkDestroySampler(backend.device.vkDevice, it, null) }
        }
    }

    class ProvidedDepthAttachment(isCopied: Boolean, image: Image, imageView: VkImageView, sampler: Long) : DepthAttachment(isCopied) {
        override val depthImage: Image = image
        override val depthImageView: VkImageView = imageView
        override val depthSampler: Long = sampler
    }

    class CreatedDepthAttachment(val backend: RenderBackendVk, maxWidth: Int, maxHeight: Int, isCopied: Boolean,
                                 filterMethod: Int, depthCompareOp: Int, multiSampling: Boolean) :
            DepthAttachment(isCopied) {
        override val depthImage: Image = TODO()
        override val depthImageView: VkImageView
        override val depthSampler: Long

        init {
//            val depthImageCfg = Image.Config().apply {
//                width = maxWidth
//                height = maxHeight
//                format = backend.physicalDevice.depthFormat
//                tiling = VK_IMAGE_TILING_OPTIMAL
//                usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT or if (isCopied) VK_IMAGE_USAGE_TRANSFER_SRC_BIT else VK_IMAGE_USAGE_SAMPLED_BIT
//                allocUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
//                if (multiSampling) {
//                    numSamples = backend.physicalDevice.msaaSamples
//                }
//            }
//            depthImage = Image(backend, depthImageCfg)
//            depthImageView = ImageView.imageView2d(backend.device, depthImage, VK_IMAGE_ASPECT_DEPTH_BIT)
//            depthSampler = createSampler(backend, filterMethod, true, depthCompareOp)
//
//            depthImage.releaseWith(this)
//            depthImageView.releaseWith(this)
        }

        override fun release() {
            super.release()
            vkDestroySampler(backend.device.vkDevice, depthSampler, null)
        }
    }

    companion object {
        private fun createSampler(backend: RenderBackendVk, filterMethod: Int, isDepth: Boolean, depthCompareOp: Int): Long {
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
                check(vkCreateSampler(backend.device.vkDevice, samplerInfo, null, lp) == VK_SUCCESS)
                return lp[0]
            }
        }
    }
}