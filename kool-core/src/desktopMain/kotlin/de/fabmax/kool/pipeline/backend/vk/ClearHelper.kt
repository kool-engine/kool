package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.VK10.*

class ClearHelper(val backend: RenderBackendVk) {
    private val device: Device get() = backend.device
    private val clearPipelines = mutableMapOf<RenderPass, ClearPipeline>()

    private val vertexModule: VkShaderModule = device.createShaderModule {
        val spirv = checkNotNull(Shaderc.compileVertexShader(VERT_SRC, "clear-shader.vert", "main").spirvData)
        pCode(spirv)
    }
    private val fragmentModule: VkShaderModule = device.createShaderModule {
        val spirv = checkNotNull(Shaderc.compileFragmentShader(FRAG_SRC, "clear-shader.frag", "main").spirvData)
        pCode(spirv)
    }

    init {
        backend.device.onRelease {
            backend.device.destroyShaderModule(vertexModule)
            backend.device.destroyShaderModule(fragmentModule)
        }
    }

    fun clear(passEncoderState: PassEncoderState) {
        val clearPipeline = clearPipelines.getOrPut(passEncoderState.renderPass) {
            ClearPipeline(passEncoderState)
        }
        clearPipeline.clear(passEncoderState)
    }

    private inner class ClearPipeline(passEncoderState: PassEncoderState) : BaseReleasable() {
        val renderPass: RenderPass = passEncoderState.renderPass
        val gpuRenderPass: RenderPassVk = passEncoderState.gpuRenderPass
        val descriptorSetLayout: VkDescriptorSetLayout
        val pipelineLayout: VkPipelineLayout
        val bindGroupData: BindGroupDataVk
        val clearValues: BindGroupData.UniformBufferBindingData<*>

        var prevColor: Color? = null
        var prevDepth = 0f

        val clearColorOnly: VkGraphicsPipeline by lazy { makeClearPipeline(true, false) }
        val clearDepthOnly: VkGraphicsPipeline by lazy { makeClearPipeline(false, true) }
        val clearColorAndDepth: VkGraphicsPipeline by lazy { makeClearPipeline(true, true) }

        init {
            val bindGrpLayout = BindGroupLayout.Builder(0, BindGroupScope.VIEW).apply {
                ubos += UniformBufferLayout(
                    name = "clearValues",
                    structProvider = {
                        DynamicStruct.Builder("clearVals", MemoryLayout.Std140).float4("color").float1("depth").build()
                    },
                    stages = setOf(ShaderStage.VERTEX_SHADER, ShaderStage.FRAGMENT_SHADER)
                )
            }.create()
            val bindGrpData = BindGroupData(bindGrpLayout)
            clearValues = bindGrpData.bindings[0] as BindGroupData.UniformBufferBindingData<*>

            memStack {
                val bindings = callocVkDescriptorSetLayoutBindingN(1) {
                    binding(0)
                    descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    stageFlags(VK_SHADER_STAGE_VERTEX_BIT or VK_SHADER_STAGE_FRAGMENT_BIT)
                    descriptorCount(1)
                }
                descriptorSetLayout = device.createDescriptorSetLayout(this) {
                    pBindings(bindings)
                }
                pipelineLayout = device.createPipelineLayout(this) {
                    pSetLayouts(longs(descriptorSetLayout.handle))
                }
                bindGroupData = BindGroupDataVk(bindGrpData, descriptorSetLayout, backend, passEncoderState.commandBuffer)
            }

            releaseWith(renderPass)
        }

        override fun release() {
            super.release()
            cancelReleaseWith(renderPass)
            bindGroupData.release()
            device.destroyPipelineLayout(pipelineLayout)
            device.destroyDescriptorSetLayout(descriptorSetLayout)
        }

        fun clear(passEncoderState: PassEncoderState) {
            val rp = passEncoderState.renderPass
            val clearColor = (rp.colorAttachments[0].clearColor as? ClearColorFill)?.clearColor ?: Color.BLACK
            val clearDepth = rp.depthMode.far

            passEncoderState.setViewport(0, 0, rp.width, rp.height)

            if (clearColor != prevColor || clearDepth != prevDepth) {
                prevColor = clearColor
                prevDepth = clearDepth
                clearValues.buffer.clear()
                clearColor.putTo(clearValues.buffer.buffer)
                clearValues.buffer.buffer.putFloat32(clearDepth)
                clearValues.markDirty()
            }

            val isClearColor = rp.colorAttachments[0].clearColor is ClearColorFill
            val isClearDepth = rp.depthAttachment?.clearDepth == ClearDepthFill
            val clearPipeline = when {
                isClearColor && isClearDepth -> clearColorAndDepth
                isClearColor -> clearColorOnly
                else -> clearDepthOnly
            }

            passEncoderState.setPipeline(clearPipeline)
            bindGroupData.prepareBind(passEncoderState)
            passEncoderState.setBindGroup(bindGroupData, pipelineLayout, BindPoint.Graphics)
            vkCmdDraw(passEncoderState.commandBuffer, 4, 1, 0, 0)
        }

        private fun makeClearPipeline(isClearColor: Boolean, isClearDepth: Boolean): VkGraphicsPipeline = memStack {
            val shaderStageInfos = callocVkPipelineShaderStageCreateInfoN(2) {
                this[0]
                    .stage(VK_SHADER_STAGE_VERTEX_BIT)
                    .module(vertexModule.handle)
                    .pName(ASCII("main"))
                this[1]
                    .stage(VK_SHADER_STAGE_FRAGMENT_BIT)
                    .module(fragmentModule.handle)
                    .pName(ASCII("main"))
            }
            val vertexInfo = callocVkPipelineVertexInputStateCreateInfo { }
            val inputAssembly = callocVkPipelineInputAssemblyStateCreateInfo {
                topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP)
                primitiveRestartEnable(true)
            }
            val rasterizer = callocVkPipelineRasterizationStateCreateInfo {
                lineWidth(1f)
                polygonMode(VK_POLYGON_MODE_FILL)
                cullMode(CullMethod.NO_CULLING.vk)
            }
            val depthStencil = callocVkPipelineDepthStencilStateCreateInfo {
                depthTestEnable(true)
                depthWriteEnable(isClearDepth)
                depthCompareOp(DepthCompareOp.ALWAYS.vk)
            }
            val multisampling = callocVkPipelineMultisampleStateCreateInfo {
                rasterizationSamples(gpuRenderPass.numSamples)
                minSampleShading(1f)
            }
            val viewportState = callocVkPipelineViewportStateCreateInfo {
                viewportCount(1)
                scissorCount(1)
            }
            val dynamicState = callocVkPipelineDynamicStateCreateInfo {
                pDynamicStates(ints(VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR))
            }
            val blendAttachments = callocVkPipelineColorBlendAttachmentStateN(1) {
                if (isClearColor) {
                    colorWriteMask(VK_COLOR_COMPONENT_R_BIT or VK_COLOR_COMPONENT_G_BIT or VK_COLOR_COMPONENT_B_BIT or VK_COLOR_COMPONENT_A_BIT)
                }
            }
            val blendInfo = callocVkPipelineColorBlendStateCreateInfo {
                pAttachments(blendAttachments)
            }

            val pipelineRenderingCreateInfo = callocVkPipelineRenderingCreateInfo {
                colorAttachmentCount(gpuRenderPass.numColorAttachments)
                val colorFormats = mallocInt(gpuRenderPass.numColorAttachments)
                for (i in 0 until gpuRenderPass.numColorAttachments) {
                    colorFormats.put(i, gpuRenderPass.colorTargetFormats[i])
                }
                pColorAttachmentFormats(colorFormats)
                if (gpuRenderPass.hasDepth) {
                    depthAttachmentFormat(backend.physicalDevice.depthFormat)
                }
            }

            device.createGraphicsPipeline {
                pStages(shaderStageInfos)
                pVertexInputState(vertexInfo)
                pInputAssemblyState(inputAssembly)
                pViewportState(viewportState)
                pDynamicState(dynamicState)
                pRasterizationState(rasterizer)
                pMultisampleState(multisampling)
                pDepthStencilState(depthStencil)
                pColorBlendState(blendInfo)
                layout(pipelineLayout.handle)
                renderPass(0L)
                subpass(0)
                basePipelineHandle(VK_NULL_HANDLE)
                basePipelineIndex(-1)
                pNext(pipelineRenderingCreateInfo)
            }.also { onRelease { device.destroyGraphicsPipeline(it) } }
        }
    }

    companion object {
        private val CLEAR_COLOR_EMPTY = Color.BLACK.withAlpha(0f)
        private val VERT_SRC = """
            #version 450

            vec2 positions[4] = vec2[](
                vec2(-1.0, 1.0), vec2(1.0, 1.0),
                vec2(-1.0, -1.0), vec2(1.0, -1.0)
            );
            
            layout(binding = 0) uniform ClearValues {
                vec4 color;
                float depth;
            } clearValues;
            
            void main() {
                gl_Position = vec4(positions[gl_VertexIndex], clearValues.depth, 1.0);
            }
        """.trimIndent()

        private val FRAG_SRC = """
            #version 450

            layout(binding = 0) uniform ClearValues {
                vec4 color;
                float depth;
            } clearValues;
            
            layout(location = 0) out vec4 outColor;

            void main() {
                outColor = clearValues.color;
            }
        """.trimIndent()
    }
}