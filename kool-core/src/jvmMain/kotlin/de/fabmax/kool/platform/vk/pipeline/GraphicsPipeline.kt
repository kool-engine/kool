package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.platform.vk.util.bitValue
import de.fabmax.kool.scene.CullMethod
import de.fabmax.kool.util.logD
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo
import org.lwjgl.vulkan.VkPushConstantRange
import java.nio.ByteBuffer

class GraphicsPipeline(val sys: VkSystem, val renderPass: RenderPass, val msaaSamples: Int, val dynamicViewPort: Boolean,
                       val pipeline: Pipeline, val nImages: Int, val descriptorSetPoolSize: Int = 100) : VkResource() {

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
                var iAttrib = 0
                pipeline.vertexLayout.bindings.forEach { binding ->
                    binding.attributes.forEach { attrib ->
                        this[iAttrib++].apply {
                            binding(binding.binding)
                            location(attrib.location)
                            offset(attrib.offset)
                            format(when (attrib.type) {
                                GlslType.FLOAT -> VK_FORMAT_R32_SFLOAT
                                GlslType.VEC_2F -> VK_FORMAT_R32G32_SFLOAT
                                GlslType.VEC_3F -> VK_FORMAT_R32G32B32_SFLOAT
                                GlslType.VEC_4F -> VK_FORMAT_R32G32B32A32_SFLOAT
                                else -> throw IllegalStateException("Attribute is not a float type")
                            })
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
                topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
                primitiveRestartEnable(false)
            }

            val viewportState: VkPipelineViewportStateCreateInfo?
            val viewport = callocVkViewportN(1) {
                // actual viewport size is set on render
                x(0f)
                y(0f)
                width(renderPass.maxWidth.toFloat())
                height(renderPass.maxHeight.toFloat())
                minDepth(0f)
                maxDepth(1f)
            }

            val scissor = callocVkRect2DN(1) {
                offset { it.set(0, 0) }
                extent { it.width(renderPass.maxWidth); it.height(renderPass.maxHeight) }
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
                //polygonMode(VK_POLYGON_MODE_LINE)
                lineWidth(pipeline.lineWidth)
                cullMode(when (pipeline.cullMethod) {
                    CullMethod.DEFAULT -> VK_CULL_MODE_BACK_BIT
                    CullMethod.CULL_BACK_FACES -> VK_CULL_MODE_BACK_BIT
                    CullMethod.CULL_FRONT_FACES -> VK_CULL_MODE_FRONT_BIT
                    CullMethod.NO_CULLING -> VK_CULL_MODE_NONE
                })
                frontFace(renderPass.triFrontDirection)
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
                depthTestEnable(pipeline.depthCompareOp != DepthCompareOp.DISABLED)
                depthWriteEnable(pipeline.isWriteDepth)
                depthCompareOp(when (pipeline.depthCompareOp) {
                    DepthCompareOp.DISABLED -> 0
                    DepthCompareOp.ALWAYS -> VK_COMPARE_OP_ALWAYS
                    DepthCompareOp.LESS -> VK_COMPARE_OP_LESS
                    DepthCompareOp.LESS_EQUAL -> VK_COMPARE_OP_LESS_OR_EQUAL
                    DepthCompareOp.GREATER -> VK_COMPARE_OP_GREATER
                    DepthCompareOp.GREATER_EQUAL -> VK_COMPARE_OP_GREATER_OR_EQUAL
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
                renderPass(renderPass.vkRenderPass)
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

    private fun DescriptorType.intType() = when (this) {
        DescriptorType.IMAGE_SAMPLER -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        DescriptorType.CUBE_IMAGE_SAMPLER -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        DescriptorType.UNIFORM_BUFFER -> VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER
    }

    private fun MemoryStack.createDescriptorSetLayout(descriptorSetLayout: DescriptorSetLayout): Long {
        val bindings = callocVkDescriptorSetLayoutBindingN(descriptorSetLayout.descriptors.size) {
            descriptorSetLayout.descriptors.forEachIndexed { i, b ->
                this[i].apply {
                    binding(b.binding)
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

        return checkCreatePointer { vkCreateDescriptorSetLayout(sys.device.vkDevice, layoutInfo, null, it) }
    }

    private fun createDescriptorPool(descriptorSetLayout: DescriptorSetLayout): Long {
        memStack {
            val poolSize = callocVkDescriptorPoolSizeN(descriptorSetLayout.descriptors.size) {
                descriptorSetLayout.descriptors.forEachIndexed { i, b ->
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

    fun getDescriptorSetInstance(pipeline: Pipeline): DescriptorSet {
        return descriptorSetInstances.computeIfAbsent(pipeline.pipelineInstanceId) {
            logD { "Creating new descriptor set instance [${descriptorSetInstances.size+1} / $descriptorSetPoolSize, pipeline: ${pipeline.pipelineHash}]" }
            if (descriptorSetInstances.size == descriptorSetPoolSize) {
                throw IllegalStateException("Descriptor set pool exhausted. Use larger descriptorSetPoolSize")
            }
            DescriptorSet(this, pipeline)
        }
    }

    override fun freeResources() {
        vkDestroyPipeline(sys.device.vkDevice, vkGraphicsPipeline, null)
        vkDestroyPipelineLayout(sys.device.vkDevice, pipelineLayout, null)
        vkDestroyDescriptorSetLayout(sys.device.vkDevice, descriptorSetLayout, null)
        vkDestroyDescriptorPool(sys.device.vkDevice, descriptorPool, null)

        descriptorSetInstances.clear()

        logD { "Destroyed graphics pipeline" }
    }
}