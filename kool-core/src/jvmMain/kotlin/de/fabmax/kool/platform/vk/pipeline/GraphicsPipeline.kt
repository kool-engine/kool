package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.logD
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkWriteDescriptorSet
import java.nio.ByteBuffer

class GraphicsPipeline(val swapChain: SwapChain, val pipeline: Pipeline) : VkResource() {

    val descriptorSetLayout: Long
    val descriptorPool: Long
    val descriptorSets = mutableListOf<Long>()

    // fixme: make descriptorObjects private and access buffers / textures via pipelineConfig
    val descriptorObjects = DescriptorObjects(swapChain.nImages)

    val vkGraphicsPipeline: Long
    val pipelineLayout: Long

    var requiresDescriptorSetUpdate = true

    init {
        memStack {
            descriptorSetLayout = createDescriptorSetLayout(pipeline.descriptorLayout)
            descriptorPool = createDescriptorPool(pipeline.descriptorLayout)
            createDescriptorSets()
            createDescriptorObjects(pipeline.descriptorLayout)

            val shaderStages = pipeline.shaderCode.stages
            val shaderStageModules = shaderStages.map { createShaderModule(it) }
            val shaderStageInfos = callocVkPipelineShaderStageCreateInfoN(shaderStages.size) {
                for (i in shaderStages.indices) {
                    this[i]
                        .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                        .stage(shaderStages[i].stage)
                        .module(shaderStageModules[i])
                        .pName(ASCII(shaderStages[i].entryPoint))
                }
            }

            val bindingDescription = callocVkVertexInputBindingDescriptionN(1) {
                binding(pipeline.vertexLayout.bindings[0].binding)
                stride(pipeline.vertexLayout.bindings[0].strideBytes)
                inputRate(VK_VERTEX_INPUT_RATE_VERTEX)
            }
            val nAttributes = pipeline.vertexLayout.bindings[0].attributes.size
            val attributeDescriptions = callocVkVertexInputAttributeDescriptionN(nAttributes) {
                pipeline.vertexLayout.bindings[0].attributes.forEachIndexed { i, attrib ->
                    this[i].apply {
                        binding(attrib.binding)
                        location(attrib.location)
                        offset(attrib.offset)
                        format(when (attrib.type) {
                            AttributeType.FLOAT -> VK_FORMAT_R32_SFLOAT
                            AttributeType.VEC_2F -> VK_FORMAT_R32G32_SFLOAT
                            AttributeType.VEC_3F -> VK_FORMAT_R32G32B32_SFLOAT
                            AttributeType.VEC_4F -> VK_FORMAT_R32G32B32A32_SFLOAT
                            AttributeType.COLOR_4F -> VK_FORMAT_R32G32B32A32_SFLOAT
                            else -> throw IllegalStateException("Attribute is not a float type")
                        })
                    }
                }
            }
            val vertexInputInfo = callocVkPipelineVertexInputStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                pVertexBindingDescriptions(bindingDescription)
                pVertexAttributeDescriptions(attributeDescriptions)
            }

            val inputAssembly = callocVkPipelineInputAssemblyStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
                primitiveRestartEnable(false)
            }

            val viewport = callocVkViewportN(1) {
                x(0f)
                y(0f)
                width(swapChain.extent.width().toFloat())
                height(swapChain.extent.height().toFloat())
                minDepth(0f)
                maxDepth(1f)
            }

            val scissor = callocVkRect2DN(1) {
                offset { it.set(0, 0) }
                extent(swapChain.extent)
            }

            val viewportState = callocVkPipelineViewportStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                viewportCount(1)
                pViewports(viewport)
                scissorCount(1)
                pScissors(scissor)
            }

            val rasterizer = callocVkPipelineRasterizationStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                depthClampEnable(false)
                rasterizerDiscardEnable(false)
                polygonMode(VK_POLYGON_MODE_FILL)
                //polygonMode(VK_POLYGON_MODE_LINE)
                lineWidth(1f)
                cullMode(when (pipeline.cullMethod) {
                    CullMethod.FRONT_FACE -> VK_CULL_MODE_FRONT_BIT
                    CullMethod.BACK_FACE -> VK_CULL_MODE_BACK_BIT
                    CullMethod.NO_CULL -> VK_CULL_MODE_NONE
                })
                //frontFace(VK_FRONT_FACE_CLOCKWISE)
                frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE)     // use counter-clockwise as front to compensate y-flip in projection
                depthBiasEnable(false)
                depthBiasConstantFactor(0f)
                depthBiasClamp(0f)
                depthBiasSlopeFactor(0f)
            }

            val multisampling = callocVkPipelineMultisampleStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                sampleShadingEnable(false)
                rasterizationSamples(swapChain.sys.physicalDevice.msaaSamples)
                minSampleShading(1f)
                pSampleMask(null)
                alphaToCoverageEnable(false)
                alphaToOneEnable(false)
                //sampleShadingEnable(true)
                //minSampleShading(0.2f)
            }

            val colorBlendAttachment = callocVkPipelineColorBlendAttachmentStateN(1) {
                colorWriteMask(VK_COLOR_COMPONENT_R_BIT or VK_COLOR_COMPONENT_G_BIT or VK_COLOR_COMPONENT_B_BIT or VK_COLOR_COMPONENT_A_BIT)
                blendEnable(false)
                srcColorBlendFactor(VK_BLEND_FACTOR_ONE)
                dstColorBlendFactor(VK_BLEND_FACTOR_ZERO)
                colorBlendOp(VK_BLEND_OP_ADD)
                srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO)
                alphaBlendOp(VK_BLEND_OP_ADD)
            }

            val colorBlending = callocVkPipelineColorBlendStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                logicOpEnable(false)
                logicOp(VK_LOGIC_OP_COPY)
                pAttachments(colorBlendAttachment)
                blendConstants(0, 0f)
                blendConstants(1, 0f)
                blendConstants(2, 0f)
                blendConstants(3, 0f)
            }

            val depthStencil = callocVkPipelineDepthStencilStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
                depthTestEnable(pipeline.depthTest != DepthTest.DISABLED)
                depthWriteEnable(pipeline.isWriteDepth)
                depthCompareOp(when (pipeline.depthTest) {
                    DepthTest.DISABLED -> 0
                    DepthTest.ALWAYS -> VK_COMPARE_OP_ALWAYS
                    DepthTest.LESS -> VK_COMPARE_OP_LESS
                    DepthTest.LESS_EQUAL -> VK_COMPARE_OP_LESS_OR_EQUAL
                    DepthTest.GREATER -> VK_COMPARE_OP_GREATER
                    DepthTest.GREATER_EQUAL -> VK_COMPARE_OP_GREATER_OR_EQUAL
                })
                depthBoundsTestEnable(false)
                stencilTestEnable(false)
            }

            val pipelineLayoutInfo = callocVkPipelineLayoutCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                pSetLayouts(longs(descriptorSetLayout))
            }
            pipelineLayout = checkCreatePointer {
                vkCreatePipelineLayout(swapChain.sys.device.vkDevice, pipelineLayoutInfo, null, it )
            }

            val pipelineInfo = callocVkGraphicsPipelineCreateInfoN(1) {
                sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                pStages(shaderStageInfos)
                pVertexInputState(vertexInputInfo)
                pInputAssemblyState(inputAssembly)
                pViewportState(viewportState)
                pRasterizationState(rasterizer)
                pMultisampleState(multisampling)
                pDepthStencilState(depthStencil)
                pColorBlendState(colorBlending)
                pDynamicState(null)
                layout(pipelineLayout)
                renderPass(swapChain.renderPass.vkRenderPass)
                subpass(0)
                basePipelineHandle(VK_NULL_HANDLE)
                basePipelineIndex(-1)
            }
            vkGraphicsPipeline = checkCreatePointer {
                vkCreateGraphicsPipelines(
                    swapChain.sys.device.vkDevice,
                    VK_NULL_HANDLE,
                    pipelineInfo,
                    null,
                    it
                )
            }

            for (module in shaderStageModules) {
                vkDestroyShaderModule(swapChain.sys.device.vkDevice, module, null)
            }
        }

        swapChain.addDependingResource(this)
        logD { "Created graphics pipeline" }
    }

    fun updateDescriptorSets() {
        if (requiresDescriptorSetUpdate) {
            requiresDescriptorSetUpdate = false
            for (imageIndex in 0 until swapChain.nImages) {
                memStack {
                    val descriptorWrite = callocVkWriteDescriptorSetN(pipeline.descriptorLayout.descriptors.size) {
                        pipeline.descriptorLayout.descriptors.forEachIndexed { descIdx, desc ->
                            val descObj = descriptorObjects.getDescriptorObject(imageIndex, descIdx)
                            descObj.setDescriptorSet(this@memStack, this[descIdx], descriptorSets[imageIndex])
                        }
                    }
                    vkUpdateDescriptorSets(swapChain.sys.device.vkDevice, descriptorWrite, null)
                }
            }
        }
    }

    private fun createShaderModule(shaderStage: ShaderStage): Long {
        return memStack {
            val code = malloc(shaderStage.code.size).put(shaderStage.code).flip()
            val createInfo = callocVkShaderModuleCreateInfo {
                sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                pCode(code)
            }
            checkCreatePointer { vkCreateShaderModule(swapChain.sys.device.vkDevice, createInfo, null, it) }
        }
    }

    private fun createShaderModule(code: ByteBuffer): Long {
        return memStack {
            val createInfo = callocVkShaderModuleCreateInfo {
                sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                pCode(code)
            }
            checkCreatePointer { vkCreateShaderModule(swapChain.sys.device.vkDevice, createInfo, null, it) }
        }
    }

    private fun DescriptorType.intType() = when (this) {
        DescriptorType.IMAGE_SAMPLER -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        DescriptorType.UNIFORM_BUFFER -> VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER
    }

    private fun MemoryStack.createDescriptorSetLayout(descriptorLayout: DescriptorLayout): Long {
        val bindings = callocVkDescriptorSetLayoutBindingN(descriptorLayout.descriptors.size) {
            descriptorLayout.descriptors.forEachIndexed { i, b ->
                this[i].apply {
                    binding(i)
                    descriptorType(b.type.intType())
                    descriptorCount(1)
                    var flags = 0
                    b.stages.forEach { stage ->
                        flags = when (stage) {
                            Stage.VERTEX_SHADER -> flags or VK_SHADER_STAGE_VERTEX_BIT
                            Stage.GEOMETRY_SHADER -> flags or VK_SHADER_STAGE_GEOMETRY_BIT
                            Stage.FRAGMENT_SHADER -> flags or VK_SHADER_STAGE_FRAGMENT_BIT
                        }
                    }
                    stageFlags(flags)
                }
            }
        }

        val layoutInfo = callocVkDescriptorSetLayoutCreateInfo {
            sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
            pBindings(bindings)
        }

        return checkCreatePointer { vkCreateDescriptorSetLayout(swapChain.sys.device.vkDevice, layoutInfo, null, it) }
    }

    private fun createDescriptorObjects(descriptorLayout: DescriptorLayout) {
        descriptorLayout.descriptors.forEachIndexed { idx, desc ->
            descriptorObjects.addDescriptor {
                when (desc.type) {
                    // fixme: more reasonable binding index needed?
                    DescriptorType.UNIFORM_BUFFER -> {
                        desc as UniformBuffer
                        val usage = VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT
                        val allocUsage = VMA_MEMORY_USAGE_CPU_TO_GPU
                        val buffer = Buffer(swapChain.sys, desc.size.toLong(), usage, allocUsage).also { addDependingResource(it) }
                        UboDescriptor(idx, desc, buffer)
                    }
                    DescriptorType.IMAGE_SAMPLER -> {
                        desc as TextureSampler
                        SamplerDescriptor(idx, desc)
                    }
                }
            }
        }
    }

    private fun createDescriptorPool(descriptorLayout: DescriptorLayout): Long {
        memStack {
            val poolSize = callocVkDescriptorPoolSizeN(descriptorLayout.descriptors.size) {
                descriptorLayout.descriptors.forEachIndexed { i, b ->
                    this[i].apply {
                        type(b.type.intType())
                        descriptorCount(swapChain.images.size)
                    }
                }
            }

            val poolInfo = callocVkDescriptorPoolCreateInfo {
                sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
                pPoolSizes(poolSize)
                maxSets(swapChain.images.size)
            }

            return checkCreatePointer { vkCreateDescriptorPool(swapChain.sys.device.vkDevice, poolInfo, null, it) }
        }
    }

    private fun createDescriptorSets() {
        memStack {
            val layouts = mallocLong(swapChain.images.size)
            for (i in swapChain.images.indices) {
                layouts.put(i, descriptorSetLayout)
            }
            val allocInfo = callocVkDescriptorSetAllocateInfo {
                sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                descriptorPool(descriptorPool)
                pSetLayouts(layouts)
            }

            val sets = mallocLong(swapChain.images.size)
            checkVk(vkAllocateDescriptorSets(swapChain.sys.device.vkDevice, allocInfo, sets))
            for (i in swapChain.images.indices) {
                descriptorSets += sets[i]
            }
        }
    }

    fun updateDescriptors(cmd: DrawCommand, imageIndex: Int): Boolean {
        return descriptorObjects.updateDescriptors(cmd, imageIndex)
    }

    override fun freeResources() {
        vkDestroyPipeline(swapChain.sys.device.vkDevice, vkGraphicsPipeline, null)
        vkDestroyPipelineLayout(swapChain.sys.device.vkDevice, pipelineLayout, null)
        vkDestroyDescriptorSetLayout(swapChain.sys.device.vkDevice, descriptorSetLayout, null)
        vkDestroyDescriptorPool(swapChain.sys.device.vkDevice, descriptorPool, null)

        descriptorObjects.clear()
        descriptorSets.clear()

        logD { "Destroyed graphics pipeline" }
    }

    class DescriptorObjects(val nImages: Int) {
        private val objects = Array<MutableList<DescriptorObject>>(nImages) { mutableListOf() }

        fun addDescriptor(block: () -> DescriptorObject): Int {
            for (i in 0 until nImages) {
                objects[i].add(block())
            }
            return objects[0].size - 1
        }

        fun clear() {
            objects.forEach { it.clear() }
        }

        fun getDescriptorObject(imageIndex: Int, descriptorIndex: Int): DescriptorObject {
            return objects[imageIndex][descriptorIndex]
        }

        fun updateDescriptors(cmd: DrawCommand, imageIndex: Int): Boolean {
            val descs = objects[imageIndex]
            var valid = true
            for (i in descs.indices) {
                valid = descs[i].update(cmd) && valid
            }
            return valid
        }
    }

    abstract class DescriptorObject(val binding: Int, val descriptor: Descriptor) {
        abstract fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long)

        abstract fun update(cmd: DrawCommand): Boolean
    }

    class UboDescriptor(binding: Int, val ubo: UniformBuffer, val buffer: Buffer) : DescriptorObject(binding, ubo) {
        override fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long) {
            stack.apply {
                val buffereInfo = callocVkDescriptorBufferInfoN(1) {
                    buffer(buffer.vkBuffer)
                    offset(0L)
                    range(ubo.size.toLong())
                }
                vkWriteDescriptorSet
                    .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                    .dstSet(dstSet)
                    .dstBinding(binding)
                    .dstArrayElement(0)
                    .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    .descriptorCount(1)
                    .pBufferInfo(buffereInfo)
            }
        }

        override fun update(cmd: DrawCommand): Boolean {
            ubo.onUpdate?.invoke(ubo, cmd)
            buffer.mappedFloats { ubo.putTo(Float32BufferImpl(this)) }
            return true
        }
    }

    inner class SamplerDescriptor(binding: Int, val sampler: TextureSampler) : DescriptorObject(binding, sampler) {
        override fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long) {
            stack.apply {
                // fixme: way to many repeating words and exclamation marks...
                val vkTex = sampler.texture!!.sampler!!.texture!!

                val imageInfo = callocVkDescriptorImageInfoN(1) {
                    imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                    imageView(vkTex.textureImageView.vkImageView)
                    sampler(vkTex.sampler)
                }
                vkWriteDescriptorSet
                        .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                        .dstSet(dstSet)
                        .dstBinding(binding)
                        .dstArrayElement(0)
                        .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                        .descriptorCount(1)
                        .pImageInfo(imageInfo)
            }
        }

        override fun update(cmd: DrawCommand): Boolean {
            sampler.texture?.let {
                if (it.sampler == null) {
                    it.sampler = ImageSampler(swapChain.sys.ctx.assetMgr.deferredTexLoad(it.loader), swapChain.sys)
                }
                if (it.sampler?.texture == null) {
                    it.sampler?.checkComplete()
                    if (it.sampler?.texture != null) {
                        requiresDescriptorSetUpdate = true
                    }
                }
            }
            sampler.onUpdate?.invoke(sampler, cmd)
            return sampler.texture?.sampler?.texture != null
        }
    }
}