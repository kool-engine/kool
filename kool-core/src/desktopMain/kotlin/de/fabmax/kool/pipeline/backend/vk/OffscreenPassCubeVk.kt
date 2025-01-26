package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.VK10.*

class OffscreenPassCubeVk(
    val parentPass: OffscreenRenderPassCube,
    numSamples: Int,
    backend: RenderBackendVk
) : RenderPassVk(0, numSamples, backend), OffscreenPassCubeImpl {

    override val colorTargetFormats: List<Int> = parentPass.colorTextures.map { it.props.format.vk }
    private val vkRenderPasses = Array<RenderPassWrapper?>(2) { null }

    private val colorAttachments = List(parentPass.colorTextures.size) {
        RenderAttachment(parentPass.colorTextures[it], false, "${parentPass.name}.color[$it]")
    }
    private val depthAttachment: RenderAttachment?

    private var copySrcFlag = 0
    private var copyDstFlag = 0

    init {
        val depthTex = when (parentPass.depthAttachment) {
            OffscreenRenderPass.DepthAttachmentRender -> TextureCube(
                TextureProps(generateMipMaps = false, defaultSamplerSettings = SamplerSettings().clamped()),
                "${parentPass.name}:render-depth"
            )
            else -> parentPass.depthTexture
        }
        depthAttachment = depthTex?.let { RenderAttachment(it, true,  it.name) }
    }

    override fun applySize(width: Int, height: Int) {
        logT { "Resize offscreen cube pass ${parentPass.name} to $width x $height" }
        colorAttachments.forEach { it.recreate(width, height) }
        depthAttachment?.recreate(width, height)
        vkRenderPasses.filterNotNull().forEach { it.recreateFramebuffers() }
    }

    override fun release() {
        val alreadyReleased = isReleased
        super.release()
        if (!alreadyReleased) {
            colorAttachments.forEach { it.release() }
            depthAttachment?.release()
        }
    }

    override fun toString(): String {
        return "OffscreenPassCubeVk:${parentPass.name}"
    }

    private fun getOrCreateRenderPass(forceLoad: Boolean): RenderPassWrapper {
        val idx = if (forceLoad) 1 else 0
        vkRenderPasses[idx]?.let { return it }

        val rp = RenderPassWrapper(forceLoad)
        vkRenderPasses[idx] = rp
        return rp
    }

    fun draw(passEncoderState: PassEncoderState) {
        val isCopySrc = parentPass.frameCopies.isNotEmpty() || parentPass.views.any { it.frameCopies.isNotEmpty() }
        val isCopyDst = parentPass.mipMode == RenderPass.MipMode.Generate
        if ((isCopySrc && copySrcFlag == 0) || (isCopyDst && copyDstFlag == 0)) {
            copySrcFlag = VK_IMAGE_USAGE_TRANSFER_SRC_BIT
            if (isCopyDst) {
                copyDstFlag = VK_IMAGE_USAGE_TRANSFER_DST_BIT
            }
            colorAttachments.forEach { it.recreate(parentPass.width, parentPass.height) }
            depthAttachment?.recreate(parentPass.width, parentPass.height)
        }
        render(parentPass, passEncoderState)
    }

    override fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean): VkRenderPass {
        val rp = getOrCreateRenderPass(forceLoad)
        rp.begin(passEncoderState)
        return rp.vkRenderPass
    }

    override fun generateMipLevels(passEncoderState: PassEncoderState) {
        TODO("Not yet implemented")
    }

    override fun copy(frameCopy: FrameCopy, passEncoderState: PassEncoderState) {
        if (frameCopy.isCopyColor) {
            for (i in frameCopy.colorCopy.indices) {
                colorAttachments[i].copyToTexture(frameCopy.colorCopy[i] as TextureCube, passEncoderState)
            }
        }
        if (frameCopy.isCopyDepth) {
            depthAttachment?.copyToTexture(frameCopy.depthCopyCube, passEncoderState)
        }
    }

    private inner class RenderAttachment(val texture: TextureCube, val isDepth: Boolean, val name: String) : BaseReleasable() {
        var descriptor: ImageInfo
        var gpuTexture: ImageVk
        val mipViews = mutableListOf<List<VkImageView>>()

        init {
            val attachmentUsage = if (isDepth) VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT else VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT
            val (desc, tex) = createTexture(
                width = parentPass.width,
                height = parentPass.height,
                usage = VK_IMAGE_USAGE_SAMPLED_BIT or attachmentUsage or copySrcFlag or copyDstFlag
            )
            descriptor = desc
            gpuTexture = tex

            texture.gpuTexture = gpuTexture
            texture.loadingState = Texture.LoadingState.LOADED
            createViews()
        }

        fun recreate(width: Int, height: Int) {
            val attachmentUsage = if (isDepth) VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT else VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT
            val (desc, tex) = createTexture(
                width = width,
                height = height,
                usage = VK_IMAGE_USAGE_SAMPLED_BIT or attachmentUsage or copySrcFlag or copyDstFlag
            )
            descriptor = desc
            gpuTexture.release()
            gpuTexture = tex
            texture.gpuTexture = gpuTexture
            createViews()
        }

        private fun createTexture(
            width: Int,
            height: Int,
            usage: Int,
            texture: Texture<*> = this.texture
        ): Pair<ImageInfo, ImageVk> {
            val descriptor = ImageInfo(
                imageType = VK_IMAGE_TYPE_2D,
                format = if (isDepth) backend.physicalDevice.depthFormat else texture.props.format.vk,
                width = width,
                height = height,
                depth = 1,
                arrayLayers = 6,
                mipLevels = parentPass.numTextureMipLevels,
                samples = numSamples,
                usage = usage,
                flags = VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT,
                label = texture.name
            )
            val tex = ImageVk(backend, descriptor, name)
            return descriptor to tex
        }

        private fun createViews() {
            mipViews.flatMap { it }.forEach { backend.device.destroyImageView(it) }
            mipViews.clear()
            for (i in 0 until parentPass.numRenderMipLevels) {
                mipViews += List<VkImageView>(6) { face ->
                    backend.device.createImageView(
                        image = gpuTexture.vkImage,
                        viewType = VK_IMAGE_VIEW_TYPE_2D,
                        format = gpuTexture.format,
                        aspectMask = if (isDepth) VK_IMAGE_ASPECT_DEPTH_BIT else VK_IMAGE_ASPECT_COLOR_BIT,
                        levelCount = 1,
                        baseArrayLayer = face,
                        baseMipLevel = i
                    )
                }
            }
        }

        fun copyToTexture(target: TextureCube, passEncoderState: PassEncoderState) {
            var copyDst = (target.gpuTexture as ImageVk?)
            if (copyDst == null || copyDst.width != parentPass.width || copyDst.height != parentPass.height) {
                copyDst?.release()
                val (_, gpuTex) = createTexture(
                    width = parentPass.width,
                    height = parentPass.height,
                    usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT,
                    texture = target
                )
                copyDst = gpuTex
                target.gpuTexture = copyDst
                target.loadingState = Texture.LoadingState.LOADED
            }
            copyDst.copyFromImage(gpuTexture, passEncoderState.commandBuffer)
        }

        override fun release() {
            super.release()
            gpuTexture.release()
            texture.gpuTexture = null
            mipViews.flatMap { it }.forEach { backend.device.destroyImageView(it) }
        }
    }

    private inner class RenderPassWrapper(forceLoad: Boolean) : BaseReleasable() {
        val vkRenderPass: VkRenderPass
        var framebuffers: List<List<VkFramebuffer>>

        init {
            if (forceLoad) {
                TODO()
            }

            val colorLoadOp = VK_ATTACHMENT_LOAD_OP_CLEAR
            val depthLoadOp = VK_ATTACHMENT_LOAD_OP_CLEAR

            memStack {
                val numAttachments = colorAttachments.size + if (depthAttachment != null) 1 else 0
                val attachments = callocVkAttachmentDescriptionN(numAttachments) {
                    for (i in colorTargetFormats.indices) {
                        this[i].apply {
                            format(colorTargetFormats[i])
                            samples(numSamples)
                            loadOp(colorLoadOp)
                            storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                            stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                            stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                            initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                            finalLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                        }
                    }
                    if (depthAttachment != null) {
                        this[numAttachments - 1].apply {
                            format(backend.physicalDevice.depthFormat)
                            samples(numSamples)
                            loadOp(depthLoadOp)
                            storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                            stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                            stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                            initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                            finalLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                        }
                    }
                }
                val colorAttachmentRefs = callocVkAttachmentReferenceN(numColorAttachments) {
                    for (i in colorTargetFormats.indices) {
                        this[i].apply {
                            attachment(i)
                            layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                        }
                    }
                }
                val subpass = callocVkSubpassDescriptionN(1) {
                    pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                    colorAttachmentCount(numColorAttachments)
                    pColorAttachments(colorAttachmentRefs)

                    if (depthAttachment != null) {
                        val depthAttachmentRef = callocVkAttachmentReferenceN(1) {
                            attachment(numAttachments - 1)
                            layout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                        }
                        pDepthStencilAttachment(depthAttachmentRef[0])
                    }
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

                vkRenderPass = device.createRenderPass {
                    pAttachments(attachments)
                    pSubpasses(subpass)
                    pDependencies(dependencies)
                }
            }
            framebuffers = createFramebuffers()

            releaseWith(this@OffscreenPassCubeVk)
            logD { "Created off-screen cube pass ${parentPass.name} (loading = $forceLoad)" }
        }

        fun begin(passEncoderState: PassEncoderState) = with(passEncoderState.stack) {
            val renderPass = passEncoderState.renderPass
            val mipLevel = passEncoderState.mipLevel
            val face = passEncoderState.layer

            val beginInfo = callocVkRenderPassBeginInfo {
                renderPass(vkRenderPass.handle)
                framebuffer(framebuffers[mipLevel][face].handle)
                renderArea().extent().set(
                    (parentPass.width shr mipLevel).coerceAtLeast(1),
                    (parentPass.height shr mipLevel).coerceAtLeast(1)
                )

                val numAttachments = colorAttachments.size + if (depthAttachment != null) 1 else 0
                val clearValues = callocVkClearValueN(numAttachments) {
                    for (i in 0 until numColorAttachments) {
                        this[i].setColor(renderPass.clearColors[i] ?: Color.BLACK)
                    }
                    if (depthAttachment != null) {
                        this[numAttachments - 1].depthStencil {
                            it.depth(if (renderPass.isReverseDepth) 0f else 1f)
                            it.stencil(0)
                        }
                    }
                }
                pClearValues(clearValues)
            }
            vkCmdBeginRenderPass(passEncoderState.commandBuffer, beginInfo, VK_SUBPASS_CONTENTS_INLINE)

            // when the renderpass is done, it transitions all attached textures into shader read only layout
            for (i in colorAttachments.indices) {
                colorAttachments[i].gpuTexture.layout = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL
            }
            depthAttachment?.gpuTexture?.layout = VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL
        }

        override fun release() {
            super.release()
            device.destroyRenderPass(vkRenderPass)
            framebuffers.flatMap { it }.forEach { device.destroyFramebuffer(it) }
        }

        fun recreateFramebuffers() {
            framebuffers.flatMap { it }.forEach { device.destroyFramebuffer(it) }
            framebuffers = createFramebuffers()
        }

        private fun createFramebuffers(): List<List<VkFramebuffer>> = memStack {
            val numAttachments = colorAttachments.size + if (depthAttachment != null) 1 else 0
            val attachments = mallocLong(numAttachments)

            List(parentPass.numRenderMipLevels) { level ->
                List<VkFramebuffer>(6) { face ->
                    colorAttachments.forEachIndexed { i, colorAttach ->
                        attachments.put(i, colorAttach.mipViews[level][face].handle)
                    }
                    depthAttachment?.let {
                        attachments.put(numAttachments - 1, it.mipViews[level][face].handle)
                    }
                    device.createFramebuffer(this@memStack) {
                        renderPass(vkRenderPass.handle)
                        pAttachments(attachments)
                        width((parentPass.width shr level).coerceAtLeast(1))
                        height((parentPass.height shr level).coerceAtLeast(1))
                        layers(1)
                    }
                }
            }
        }
    }
}