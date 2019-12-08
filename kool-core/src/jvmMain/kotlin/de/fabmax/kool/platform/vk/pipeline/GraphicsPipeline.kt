package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.platform.vk.util.bitValue
import de.fabmax.kool.shading.AttributeType
import de.fabmax.kool.util.logD
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkPushConstantRange

class GraphicsPipeline(val swapChain: SwapChain, val pipeline: Pipeline, val descriptorSetPoolSize: Int = 100) : VkResource() {

    val descriptorSetLayout: Long
    val descriptorPool: Long

    private val descriptorSetInstances = mutableMapOf<Long, DescriptorSet>()

    val vkGraphicsPipeline: Long
    val pipelineLayout: Long

    init {
        memStack {
            if (pipeline.descriptorSetLayouts.size != 1) {
                TODO("For now only one descriptor set layout is supported")
            }
            descriptorSetLayout = createDescriptorSetLayout(pipeline.descriptorSetLayouts[0])
            descriptorPool = createDescriptorPool(pipeline.descriptorSetLayouts[0])

            val shaderStages = pipeline.shader.shaderCode.stages
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
                // set negative viewport height -> flips viewport y-direction to be compatible with OpenGL, requires KHR_Maintenance1 extension
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
                lineWidth(pipeline.lineWidth)
                cullMode(when (pipeline.cullMethod) {
                    CullMethod.FRONT_FACE -> VK_CULL_MODE_FRONT_BIT
                    CullMethod.BACK_FACE -> VK_CULL_MODE_BACK_BIT
                    CullMethod.NO_CULL -> VK_CULL_MODE_NONE
                })
                frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE)
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
                // pre-multiplied alpha
                blendEnable(true)
                srcColorBlendFactor(VK_BLEND_FACTOR_ONE)
                dstColorBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
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

            val pushConstantRanges: VkPushConstantRange.Buffer? = if (pipeline.pushConstantRanges.isEmpty()) { null } else {
                callocVkPushConstantRangeN(pipeline.pushConstantRanges.size) {
                    pipeline.pushConstantRanges.forEachIndexed { i, pushConstantRange ->
                        this[i].stageFlags(pushConstantRange.stages.fold(0) { f, stage -> f or stage.bitValue() })
                                .size(pushConstantRange.size)
                    }
                }
            }

            val pipelineLayoutInfo = callocVkPipelineLayoutCreateInfo {
                sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                pSetLayouts(longs(descriptorSetLayout))
                pPushConstantRanges(pushConstantRanges)
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
        logD { "Created graphics pipeline, pipelineHash: ${pipeline.pipelineHash}" }
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

    private fun DescriptorType.intType() = when (this) {
        DescriptorType.IMAGE_SAMPLER -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        DescriptorType.UNIFORM_BUFFER -> VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER
    }

    private fun MemoryStack.createDescriptorSetLayout(descriptorSetLayout: DescriptorSetLayout): Long {
        val bindings = callocVkDescriptorSetLayoutBindingN(descriptorSetLayout.descriptors.size) {
            descriptorSetLayout.descriptors.forEachIndexed { i, b ->
                this[i].apply {
                    binding(i)
                    descriptorType(b.type.intType())
                    descriptorCount(1)
                    stageFlags(b.stages.fold(0) { flags, stage -> flags or stage.bitValue() })
                }
            }
        }

        val layoutInfo = callocVkDescriptorSetLayoutCreateInfo {
            sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
            pBindings(bindings)
        }

        return checkCreatePointer { vkCreateDescriptorSetLayout(swapChain.sys.device.vkDevice, layoutInfo, null, it) }
    }

    private fun createDescriptorPool(descriptorSetLayout: DescriptorSetLayout): Long {
        memStack {
            val poolSize = callocVkDescriptorPoolSizeN(descriptorSetLayout.descriptors.size) {
                descriptorSetLayout.descriptors.forEachIndexed { i, b ->
                    this[i].apply {
                        type(b.type.intType())
                        descriptorCount(swapChain.images.size * descriptorSetPoolSize)
                    }
                }
            }

            val poolInfo = callocVkDescriptorPoolCreateInfo {
                sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
                pPoolSizes(poolSize)
                maxSets(swapChain.images.size * descriptorSetPoolSize)
            }

            return checkCreatePointer { vkCreateDescriptorPool(swapChain.sys.device.vkDevice, poolInfo, null, it) }
        }
    }

    fun getDescriptorSetInstance(pipeline: Pipeline): DescriptorSet {
        return descriptorSetInstances.computeIfAbsent(pipeline.pipelineInstanceId) {
            logD { "Creating new descriptor set instance [${descriptorSetInstances.size+1} / $descriptorSetPoolSize, pipeline: ${pipeline.pipelineHash}]" }
            if (descriptorSetInstances.size == descriptorSetPoolSize - 1) {
                throw IllegalStateException("Descriptor set pool exhausted. Use larger descriptorSetPoolSize")
            }
            DescriptorSet(this, pipeline)
        }
    }

    override fun freeResources() {
        vkDestroyPipeline(swapChain.sys.device.vkDevice, vkGraphicsPipeline, null)
        vkDestroyPipelineLayout(swapChain.sys.device.vkDevice, pipelineLayout, null)
        vkDestroyDescriptorSetLayout(swapChain.sys.device.vkDevice, descriptorSetLayout, null)
        vkDestroyDescriptorPool(swapChain.sys.device.vkDevice, descriptorPool, null)

        descriptorSetInstances.clear()

        logD { "Destroyed graphics pipeline" }
    }
}