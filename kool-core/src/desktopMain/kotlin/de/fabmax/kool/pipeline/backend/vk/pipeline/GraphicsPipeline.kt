package de.fabmax.kool.pipeline.backend.vk.pipeline

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.gl.getAttribLocations
import de.fabmax.kool.pipeline.backend.gl.locationSize
import de.fabmax.kool.pipeline.backend.vk.*
import de.fabmax.kool.pipeline.backend.vk.util.bitValue
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo
import org.lwjgl.vulkan.VkPushConstantRange
import java.nio.ByteBuffer

class GraphicsPipeline(val sys: VkSystem, val koolRenderPass: RenderPass, val vkRenderPass: VkRenderPass, val msaaSamples: Int, val dynamicViewPort: Boolean,
                       val pipeline: DrawPipeline, val nImages: Int, val descriptorSetPoolSize: Int = 500) : VkResource() {

    val descriptorSetLayout: Long
    val descriptorPool: Long

    private val descriptorSetInstances = mutableMapOf<Long, DescriptorSet>()
    private val reusableDescriptorSets = mutableListOf<DescriptorSet>()

    val vkGraphicsPipeline: Long
    val pipelineLayout: Long

    init {
        memStack {
            descriptorSetLayout = createDescriptorSetLayout(pipeline.bindGroupLayouts[BindGroupScope.PIPELINE])
            descriptorPool = createDescriptorPool(pipeline.bindGroupLayouts[BindGroupScope.PIPELINE])

            val shaderStages = (pipeline.shaderCode as ShaderCodeImplVk).vkStages
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

            val nBindings = pipeline.vertexLayout.bindings.size
            val bindingDescription = callocVkVertexInputBindingDescriptionN(nBindings) {
                var iBinding = 0
                pipeline.vertexLayout.bindings.forEach { binding ->
                    this[iBinding++].apply {
                        binding(binding.binding)
                        stride(binding.strideBytes)
                        when (binding.inputRate) {
                            InputRate.VERTEX -> inputRate(VK_VERTEX_INPUT_RATE_VERTEX)
                            InputRate.INSTANCE -> inputRate(VK_VERTEX_INPUT_RATE_INSTANCE)
                        }
                    }
                }
            }

            val locations = pipeline.vertexLayout.getAttribLocations()
            val nAttributes = pipeline.vertexLayout.bindings.sumOf { binding ->
                binding.vertexAttributes.sumOf { it.locationSize }
            }
            val attributeDescriptions = callocVkVertexInputAttributeDescriptionN(nAttributes) {
                var iAttrib = 0
                pipeline.vertexLayout.bindings.forEach { binding ->
                    binding.vertexAttributes.forEach { attrib ->
                        for (i in 0 until attrib.locationSize) {
                            val attrVkProps = attrib.attribute.vkProps()
                            this[iAttrib++].apply {
                                binding(binding.binding)
                                location(locations[attrib]!! + i)
                                offset(attrib.bufferOffset + attrVkProps.slotOffset * i)
                                format(attrVkProps.slotType)
                            }
                        }
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
                when (pipeline.vertexLayout.primitiveType) {
                    PrimitiveType.LINES -> topology(VK_PRIMITIVE_TOPOLOGY_LINE_LIST)
                    PrimitiveType.POINTS -> topology(VK_PRIMITIVE_TOPOLOGY_POINT_LIST)
                    PrimitiveType.TRIANGLES -> topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
                }
                primitiveRestartEnable(false)
            }

            val viewportState: VkPipelineViewportStateCreateInfo?
            val viewport = callocVkViewportN(1) {
                // actual viewport size is set on render
                x(0f)
                y(0f)
                width(vkRenderPass.maxWidth.toFloat())
                height(vkRenderPass.maxHeight.toFloat())
                minDepth(0f)
                maxDepth(1f)
            }

            val scissor = callocVkRect2DN(1) {
                offset { it.set(0, 0) }
                extent { it.width(vkRenderPass.maxWidth); it.height(vkRenderPass.maxHeight) }
            }

            viewportState = callocVkPipelineViewportStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                viewportCount(1)
                pViewports(viewport)
                scissorCount(1)
                pScissors(scissor)
            }

            val dynamicState = if (dynamicViewPort) {
                callocVkPipelineDynamicStateCreateInfo {
                    sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                    pDynamicStates(ints(VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR))
                }
            } else {
                null
            }

            val rasterizer = callocVkPipelineRasterizationStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                depthClampEnable(false)
                rasterizerDiscardEnable(false)
                polygonMode(VK_POLYGON_MODE_FILL)
                lineWidth(pipeline.lineWidth)
                cullMode(when (pipeline.cullMethod) {
                    CullMethod.CULL_BACK_FACES -> VK_CULL_MODE_BACK_BIT
                    CullMethod.CULL_FRONT_FACES -> VK_CULL_MODE_FRONT_BIT
                    CullMethod.NO_CULLING -> VK_CULL_MODE_NONE
                })
                frontFace(vkRenderPass.triFrontDirection)
                depthBiasEnable(false)
                depthBiasConstantFactor(0f)
                depthBiasClamp(0f)
                depthBiasSlopeFactor(0f)
            }

            val multisampling = callocVkPipelineMultisampleStateCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                sampleShadingEnable(false)
                rasterizationSamples(msaaSamples)
                minSampleShading(1f)
                pSampleMask(null)
                alphaToCoverageEnable(false)
                alphaToOneEnable(false)
                //sampleShadingEnable(true)
                //minSampleShading(0.2f)
            }

            val colorBlendAttachment = callocVkPipelineColorBlendAttachmentStateN(vkRenderPass.nColorAttachments) {
                for (i in 0 until vkRenderPass.nColorAttachments) {
                    this[i].apply {
                        colorWriteMask(VK_COLOR_COMPONENT_R_BIT or VK_COLOR_COMPONENT_G_BIT or VK_COLOR_COMPONENT_B_BIT or VK_COLOR_COMPONENT_A_BIT)
                        // pre-multiplied alpha
                        when (pipeline.blendMode) {
                            BlendMode.DISABLED -> blendEnable(false)
                            BlendMode.BLEND_ADDITIVE -> {
                                blendEnable(true)
                                srcColorBlendFactor(VK_BLEND_FACTOR_ONE)
                                dstColorBlendFactor(VK_BLEND_FACTOR_ONE)
                                colorBlendOp(VK_BLEND_OP_ADD)
                                srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                                dstAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                                alphaBlendOp(VK_BLEND_OP_ADD)
                            }
                            BlendMode.BLEND_MULTIPLY_ALPHA -> {
                                blendEnable(true)
                                srcColorBlendFactor(VK_BLEND_FACTOR_SRC_ALPHA)
                                dstColorBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
                                colorBlendOp(VK_BLEND_OP_ADD)
                                srcAlphaBlendFactor(VK_BLEND_FACTOR_SRC_ALPHA)
                                dstAlphaBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
                                alphaBlendOp(VK_BLEND_OP_ADD)
                            }
                            BlendMode.BLEND_PREMULTIPLIED_ALPHA -> {
                                blendEnable(true)
                                srcColorBlendFactor(VK_BLEND_FACTOR_ONE)
                                dstColorBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
                                colorBlendOp(VK_BLEND_OP_ADD)
                                srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                                dstAlphaBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
                                alphaBlendOp(VK_BLEND_OP_ADD)
                            }
                        }
                    }
                }
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
                depthTestEnable(pipeline.isWriteDepth || pipeline.depthCompareOp != DepthCompareOp.ALWAYS)
                depthWriteEnable(pipeline.isWriteDepth)

                if (koolRenderPass.isReverseDepth && pipeline.autoReverseDepthFunc) {
                    depthCompareOp(when (pipeline.depthCompareOp) {
                        DepthCompareOp.ALWAYS -> VK_COMPARE_OP_ALWAYS
                        DepthCompareOp.NEVER -> VK_COMPARE_OP_NEVER
                        DepthCompareOp.LESS -> VK_COMPARE_OP_GREATER
                        DepthCompareOp.LESS_EQUAL -> VK_COMPARE_OP_GREATER_OR_EQUAL
                        DepthCompareOp.GREATER -> VK_COMPARE_OP_LESS
                        DepthCompareOp.GREATER_EQUAL -> VK_COMPARE_OP_LESS_OR_EQUAL
                        DepthCompareOp.EQUAL -> VK_COMPARE_OP_EQUAL
                        DepthCompareOp.NOT_EQUAL -> VK_COMPARE_OP_NOT_EQUAL
                    })
                } else {
                    depthCompareOp(when (pipeline.depthCompareOp) {
                        DepthCompareOp.ALWAYS -> VK_COMPARE_OP_ALWAYS
                        DepthCompareOp.NEVER -> VK_COMPARE_OP_NEVER
                        DepthCompareOp.LESS -> VK_COMPARE_OP_LESS
                        DepthCompareOp.LESS_EQUAL -> VK_COMPARE_OP_LESS_OR_EQUAL
                        DepthCompareOp.GREATER -> VK_COMPARE_OP_GREATER
                        DepthCompareOp.GREATER_EQUAL -> VK_COMPARE_OP_GREATER_OR_EQUAL
                        DepthCompareOp.EQUAL -> VK_COMPARE_OP_EQUAL
                        DepthCompareOp.NOT_EQUAL -> VK_COMPARE_OP_NOT_EQUAL
                    })
                }

                depthBoundsTestEnable(false)
                stencilTestEnable(false)
            }

            val pushConstantRanges: VkPushConstantRange.Buffer? = null
//                if (pipeline.layout.pushConstantRanges.isEmpty()) { null } else {
//                    callocVkPushConstantRangeN(pipeline.layout.pushConstantRanges.size) {
//                        var offset = 0
//                        pipeline.layout.pushConstantRanges.forEachIndexed { i, pushConstantRange ->
//                            this[i].stageFlags(pushConstantRange.stages.fold(0) { f, stage -> f or stage.bitValue() })
//                                    .offset(offset)
//                                    .size(pushConstantRange.size)
//                            offset += pushConstantRange.size
//                        }
//                    }
//                }

            val pipelineLayoutInfo = callocVkPipelineLayoutCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                pSetLayouts(longs(descriptorSetLayout))
                pPushConstantRanges(pushConstantRanges)
            }
            pipelineLayout = checkCreatePointer {
                vkCreatePipelineLayout(sys.device.vkDevice, pipelineLayoutInfo, null, it )
            }

            val pipelineInfo = callocVkGraphicsPipelineCreateInfoN(1) {
                sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                pStages(shaderStageInfos)
                pVertexInputState(vertexInputInfo)
                pInputAssemblyState(inputAssembly)
                pViewportState(viewportState)
                pDynamicState(dynamicState)
                pRasterizationState(rasterizer)
                pMultisampleState(multisampling)
                pDepthStencilState(depthStencil)
                pColorBlendState(colorBlending)
                layout(pipelineLayout)
                renderPass(vkRenderPass.vkRenderPass)
                subpass(0)
                basePipelineHandle(VK_NULL_HANDLE)
                basePipelineIndex(-1)
            }
            vkGraphicsPipeline = checkCreatePointer {
                vkCreateGraphicsPipelines(
                    sys.device.vkDevice,
                    VK_NULL_HANDLE,
                    pipelineInfo,
                    null,
                    it
                )
            }

            for (module in shaderStageModules) {
                vkDestroyShaderModule(sys.device.vkDevice, module, null)
            }
        }

        //swapChain.addDependingResource(this)
        logD { "Created graphics pipeline, pipelineHash: ${pipeline.pipelineHash}" }
    }

    private fun createShaderModule(shaderStage: ShaderStage): Long {
        return memStack {
            val code = malloc(shaderStage.code.size).put(shaderStage.code).flip() as ByteBuffer
            val createInfo = callocVkShaderModuleCreateInfo {
                sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                pCode(code)
            }
            checkCreatePointer { vkCreateShaderModule(sys.device.vkDevice, createInfo, null, it) }
        }
    }

    private fun BindingType.intType() = when (this) {
        BindingType.TEXTURE_1D -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.TEXTURE_2D -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.TEXTURE_3D -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.TEXTURE_CUBE -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.UNIFORM_BUFFER -> VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER
        BindingType.STORAGE_TEXTURE_1D -> TODO()
        BindingType.STORAGE_TEXTURE_2D -> TODO()
        BindingType.STORAGE_TEXTURE_3D -> TODO()
    }

    private fun MemoryStack.createDescriptorSetLayout(bindGroupLayout: BindGroupLayout): Long {
        val bindings = callocVkDescriptorSetLayoutBindingN(bindGroupLayout.bindings.size) {
            bindGroupLayout.bindings.forEachIndexed { i, b ->
                this[i].apply {
                    binding(b.bindingIndex)
                    descriptorType(b.type.intType())
                    stageFlags(b.stages.fold(0) { flags, stage -> flags or stage.bitValue() })
                    descriptorCount(1)
                }
            }
        }

        val layoutInfo = callocVkDescriptorSetLayoutCreateInfo {
            sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
            pBindings(bindings)
        }

        return checkCreatePointer { vkCreateDescriptorSetLayout(sys.device.vkDevice, layoutInfo, null, it) }
    }

    private fun createDescriptorPool(bindGroupLayout: BindGroupLayout): Long {
        if (bindGroupLayout.bindings.isNotEmpty()) {
            memStack {
                val poolSize = callocVkDescriptorPoolSizeN(bindGroupLayout.bindings.size) {
                    bindGroupLayout.bindings.forEachIndexed { i, b ->
                        this[i].apply {
                            type(b.type.intType())
                            descriptorCount(nImages * descriptorSetPoolSize)
                        }
                    }
                }

                val poolInfo = callocVkDescriptorPoolCreateInfo {
                    sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
                    pPoolSizes(poolSize)
                    maxSets(nImages * descriptorSetPoolSize)
                }

                return checkCreatePointer { vkCreateDescriptorPool(sys.device.vkDevice, poolInfo, null, it) }
            }
        }
        return 0L
    }

    fun getDescriptorSetInstance(pipeline: DrawPipeline): DescriptorSet {
        TODO()
//        return descriptorSetInstances.getOrPut(pipeline.pipelineInstanceId) {
//            val setInstance = if (reusableDescriptorSets.isNotEmpty()) {
//                logD { "Reusing recycled descriptor set instance, pipeline: ${pipeline.name} [${pipeline.pipelineHash}]" }
//                reusableDescriptorSets.removeAt(reusableDescriptorSets.lastIndex)
//            } else {
//                logD { "Creating new descriptor set instance ${descriptorSetInstances.size + 1} / $descriptorSetPoolSize, pipeline: ${pipeline.name} [${pipeline.pipelineHash}]" }
//                if (descriptorSetInstances.size == descriptorSetPoolSize) {
//                    throw IllegalStateException("Descriptor set pool exhausted. Use larger descriptorSetPoolSize")
//                }
//                DescriptorSet(this)
//            }
//            // todo BackendStats / PipelineInfo.numInstances++
//            setInstance.createDescriptorObjects(pipeline)
//            setInstance
//        }
    }

    fun freeDescriptorSetInstance(pipeline: DrawPipeline): Boolean {
        TODO()
//        val freeSet = descriptorSetInstances.remove(pipeline.pipelineInstanceId)
//        if (freeSet != null) {
//            // todo BackendStats / PipelineInfo.numInstances--
//            freeSet.clearDescriptorObjects()
//            reusableDescriptorSets += freeSet
//        }
//        return freeSet != null
    }

    fun isEmpty(): Boolean = descriptorSetInstances.isEmpty()

    override fun freeResources() {
        vkDestroyPipeline(sys.device.vkDevice, vkGraphicsPipeline, null)
        vkDestroyPipelineLayout(sys.device.vkDevice, pipelineLayout, null)
        vkDestroyDescriptorSetLayout(sys.device.vkDevice, descriptorSetLayout, null)
        if (descriptorPool != 0L) {
            vkDestroyDescriptorPool(sys.device.vkDevice, descriptorPool, null)
        }

        descriptorSetInstances.clear()
        // todo BackendStats / PipelineInfo.deleted()

        logD { "Destroyed graphics pipeline" }
    }

    private data class AttributeVkProps(val slotOffset: Int, val slotType: Int)

    private fun Attribute.vkProps() = when (type) {
            GpuType.FLOAT1 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32_SFLOAT)
            GpuType.FLOAT2 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32_SFLOAT)
            GpuType.FLOAT3 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32_SFLOAT)
            GpuType.FLOAT4 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32A32_SFLOAT)
            GpuType.MAT2 -> AttributeVkProps(slotOffset = GpuType.FLOAT2.byteSize, slotType = VK_FORMAT_R32G32_SFLOAT)
            GpuType.MAT3 -> AttributeVkProps(slotOffset = GpuType.FLOAT3.byteSize, slotType = VK_FORMAT_R32G32B32_SFLOAT)
            GpuType.MAT4 -> AttributeVkProps(slotOffset = GpuType.FLOAT4.byteSize, slotType = VK_FORMAT_R32G32B32A32_SFLOAT)
            GpuType.INT1 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32_SINT)
            GpuType.INT2 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32_SINT)
            GpuType.INT3 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32_SINT)
            GpuType.INT4 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32A32_SINT)
        }
}