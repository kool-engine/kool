package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.*
import org.lwjgl.vulkan.VK10.*

class ClearHelper(val backend: RenderBackendVk) {
    private val device: Device get() = backend.device
    private val clearPipelines = mutableMapOf<RenderPassVk, ClearPipeline>()

    private val vertexModule: VkShaderModule by lazy {
        val spirv = checkNotNull(Shaderc.compileVertexShader(VERT_SRC, "clear-shader.vert", "main").spirvData)
        device.createShaderModule { pCode(spirv) }.also {
            device.onRelease { device.destroyShaderModule(it) }
        }
    }
    private val fragmentModule: VkShaderModule by lazy {
        val spirv = checkNotNull(Shaderc.compileFragmentShader(FRAG_SRC, "clear-shader.frag", "main").spirvData)
        device.createShaderModule { pCode(spirv) }.also {
            device.onRelease { device.destroyShaderModule(it) }
        }
    }

    fun clear(passEncoderState: RenderPassEncoderState) {
        val clearPipeline = clearPipelines.getOrPut(passEncoderState.gpuRenderPass) {
            ClearPipeline(passEncoderState.gpuRenderPass)
        }
        clearPipeline.clear(passEncoderState)
    }

    private inner class ClearPipeline(val gpuRenderPass: RenderPassVk) : BaseReleasable() {
        val pipelineLayout: VkPipelineLayout
        val bindGroupData: BindGroupDataVk
        val clearValues: BindGroupData.UniformBufferBindingData

        var prevColor: Color? = null
        var prevDepth = 0f

        val clearColorOnly: VkGraphicsPipeline by lazy { makeClearPipeline(true, false) }
        val clearDepthOnly: VkGraphicsPipeline by lazy { makeClearPipeline(false, true) }
        val clearColorAndDepth: VkGraphicsPipeline by lazy { makeClearPipeline(true, true) }

        init {
            val bindGrpLayout = BindGroupLayout.Builder(0, BindGroupScope.VIEW).apply {
                ubos += UniformBufferLayout(
                    name = "clearValues",
                    uniforms = listOf(Uniform.float4("color"), Uniform.float1("depth")),
                    stages = setOf(ShaderStage.VERTEX_SHADER, ShaderStage.FRAGMENT_SHADER)
                )
            }.create()
            val bindGrpData = BindGroupData(bindGrpLayout)
            clearValues = bindGrpData.bindings[0] as BindGroupData.UniformBufferBindingData

            memStack {
                val bindings = callocVkDescriptorSetLayoutBindingN(1) {
                    binding(0)
                    descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    stageFlags(VK_SHADER_STAGE_VERTEX_BIT or VK_SHADER_STAGE_FRAGMENT_BIT)
                    descriptorCount(1)
                }
                val gpuLayout = device.createDescriptorSetLayout(this) {
                    pBindings(bindings)
                }
                pipelineLayout = device.createPipelineLayout(this) {
                    pSetLayouts(longs(gpuLayout.handle))
                }
                bindGroupData = BindGroupDataVk(bindGrpData, gpuLayout, backend)
            }

            releaseWith(gpuRenderPass)
            bindGroupData.releaseWith(this)
        }

        override fun release() {
            super.release()
            cancelReleaseWith(gpuRenderPass)
            device.destroyPipelineLayout(pipelineLayout)
        }

        fun clear(passEncoderState: RenderPassEncoderState) {
            val rp = passEncoderState.renderPass
            val clearColor = rp.clearColor
            val clearDepth = if (rp.isReverseDepth) 0f else 1f

            if (clearColor != prevColor || clearDepth != prevDepth) {
                prevColor = clearColor
                prevDepth = clearDepth
                clearValues.buffer.clear()
                clearColor?.putTo(clearValues.buffer)
                clearValues.buffer.putFloat32(clearDepth)
                clearValues.markDirty()
            }

            val clearPipeline = when {
                clearColor == null -> clearDepthOnly
                !rp.clearDepth -> clearColorOnly
                else -> clearColorAndDepth
            }

            passEncoderState.setPipeline(clearPipeline)
            bindGroupData.bind(passEncoderState, pipelineLayout)
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
                blendEnable(false)
            }
            val blendInfo = callocVkPipelineColorBlendStateCreateInfo {
                pAttachments(blendAttachments)
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
                renderPass(gpuRenderPass.vkRenderPass.handle)
                subpass(0)
                basePipelineHandle(VK_NULL_HANDLE)
                basePipelineIndex(-1)
            }.also { onRelease { device.destroyGraphicsPipeline(it) } }
        }
    }

    companion object {
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