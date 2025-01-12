package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.gl.getAttribLocations
import de.fabmax.kool.pipeline.backend.gl.locationSize
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.NodeId
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo

class VkDrawPipeline(
    val drawPipeline: DrawPipeline,
    private val vertexShaderModule: VkShaderModule,
    private val fragmentShaderModule: VkShaderModule,
    backend: RenderBackendVk,
): VkPipeline(drawPipeline, backend) {

    private val renderPipelines = mutableMapOf<RenderPassVk<*>, VkGraphicsPipeline>()
    private val users = mutableSetOf<NodeId>()

    private fun MemoryStack.createVertexBufferLayout(): VkPipelineVertexInputStateCreateInfo {
        val nVertexBindings = drawPipeline.vertexLayout.bindings.size
        val bindingDescription = callocVkVertexInputBindingDescriptionN(nVertexBindings) {
            var iBinding = 0
            drawPipeline.vertexLayout.bindings.forEach { binding ->
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

        val locations = drawPipeline.vertexLayout.getAttribLocations()
        val nVertexAttributes = drawPipeline.vertexLayout.bindings.sumOf { binding ->
            binding.vertexAttributes.sumOf { it.locationSize }
        }
        val attributeDescriptions = callocVkVertexInputAttributeDescriptionN(nVertexAttributes) {
            var iAttrib = 0
            drawPipeline.vertexLayout.bindings.forEach { binding ->
                binding.vertexAttributes.forEach { attrib ->
                    for (i in 0 until attrib.locationSize) {
                        val (slotOffset, slotType) = attrib.attribute.vkProps()
                        this[iAttrib++].apply {
                            binding(binding.binding)
                            location(locations[attrib]!! + i)
                            offset(attrib.bufferOffset + slotOffset * i)
                            format(slotType)
                        }
                    }
                }
            }
        }

        return callocVkPipelineVertexInputStateCreateInfo {
            pVertexBindingDescriptions(bindingDescription)
            pVertexAttributeDescriptions(attributeDescriptions)
        }
    }

    private fun MemoryStack.blendInfo(passEncoderState: RenderPassEncoderState<*>): VkPipelineColorBlendStateCreateInfo {
        val renderPass = passEncoderState.renderPass
        val renderPassVk = passEncoderState.renderPassVk

        val colorBlendAttachment = callocVkPipelineColorBlendAttachmentStateN(renderPassVk.numColorAttachments) {
            for (i in 0 until renderPass.clearColors.size) {
                this[i].apply {
                    colorWriteMask(VK_COLOR_COMPONENT_R_BIT or VK_COLOR_COMPONENT_G_BIT or VK_COLOR_COMPONENT_B_BIT or VK_COLOR_COMPONENT_A_BIT)
                    when (drawPipeline.blendMode) {
                        BlendMode.DISABLED -> blendEnable(false)
                        BlendMode.BLEND_ADDITIVE -> {
                            blendEnable(true)
                            srcColorBlendFactor(VK_BLEND_FACTOR_ONE)
                            dstColorBlendFactor(VK_BLEND_FACTOR_ONE)
                            colorBlendOp(VK_BLEND_OP_ADD)
                            srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                            dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO)
                            alphaBlendOp(VK_BLEND_OP_ADD)
                        }
                        BlendMode.BLEND_MULTIPLY_ALPHA -> {
                            blendEnable(true)
                            srcColorBlendFactor(VK_BLEND_FACTOR_SRC_ALPHA)
                            dstColorBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
                            colorBlendOp(VK_BLEND_OP_ADD)
                            srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                            dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO)
                            alphaBlendOp(VK_BLEND_OP_ADD)
                        }
                        BlendMode.BLEND_PREMULTIPLIED_ALPHA -> {
                            blendEnable(true)
                            srcColorBlendFactor(VK_BLEND_FACTOR_ONE)
                            dstColorBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
                            colorBlendOp(VK_BLEND_OP_ADD)
                            srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                            dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO)
                            alphaBlendOp(VK_BLEND_OP_ADD)
                        }
                    }
                }
            }
        }

        return callocVkPipelineColorBlendStateCreateInfo {
            logicOpEnable(false)
            logicOp(VK_LOGIC_OP_COPY)
            pAttachments(colorBlendAttachment)
            blendConstants(0, 0f)
            blendConstants(1, 0f)
            blendConstants(2, 0f)
            blendConstants(3, 0f)
        }
    }

    private fun createRenderPipeline(passEncoderState: RenderPassEncoderState<*>): VkGraphicsPipeline = memStack {
        val renderPass = passEncoderState.renderPass
        val renderPassVk = passEncoderState.renderPassVk

        val shaderCode = drawPipeline.shaderCode as ShaderCodeVk
        val shaderStages = listOf(
            shaderCode.vertexStage!! to vertexShaderModule,
            shaderCode.fragmentStage!! to fragmentShaderModule
        )
        val shaderStageInfos = callocVkPipelineShaderStageCreateInfoN(shaderStages.size) {
            shaderStages.forEachIndexed { i, (stage, module) ->
                this[i]
                    .stage(stage.stage)
                    .module(module.handle)
                    .pName(ASCII(stage.entryPoint))
            }
        }

        val inputAssembly = callocVkPipelineInputAssemblyStateCreateInfo {
            when (drawPipeline.vertexLayout.primitiveType) {
                PrimitiveType.LINES -> topology(VK_PRIMITIVE_TOPOLOGY_LINE_LIST)
                PrimitiveType.POINTS -> topology(VK_PRIMITIVE_TOPOLOGY_POINT_LIST)
                PrimitiveType.TRIANGLES -> topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
                PrimitiveType.TRIANGLE_STRIP -> topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP)
            }
            primitiveRestartEnable(false)
        }

        val rasterizer = callocVkPipelineRasterizationStateCreateInfo {
            depthClampEnable(false)
            rasterizerDiscardEnable(false)
            polygonMode(VK_POLYGON_MODE_FILL)
            lineWidth(drawPipeline.lineWidth)
            cullMode(drawPipeline.cullMethod.vk)
            frontFace(if (renderPass.isMirrorY) VK_FRONT_FACE_COUNTER_CLOCKWISE else VK_FRONT_FACE_CLOCKWISE)
            depthBiasEnable(false)
            depthBiasConstantFactor(0f)
            depthBiasClamp(0f)
            depthBiasSlopeFactor(0f)
        }

        val depthOp = when {
            renderPass.isReverseDepth && drawPipeline.autoReverseDepthFunc -> {
                when (drawPipeline.pipelineConfig.depthTest) {
                    DepthCompareOp.LESS -> DepthCompareOp.GREATER
                    DepthCompareOp.LESS_EQUAL -> DepthCompareOp.GREATER_EQUAL
                    DepthCompareOp.GREATER -> DepthCompareOp.LESS
                    DepthCompareOp.GREATER_EQUAL -> DepthCompareOp.LESS_EQUAL
                    else -> drawPipeline.pipelineConfig.depthTest
                }
            }
            else -> drawPipeline.pipelineConfig.depthTest
        }

        val hasDepthAttachment = renderPass !is OffscreenRenderPass ||
                renderPass.depthAttachment != OffscreenRenderPass.DepthAttachmentNone

        val depthStencil = if (!hasDepthAttachment) null else callocVkPipelineDepthStencilStateCreateInfo {
            depthTestEnable(true)
            depthWriteEnable(drawPipeline.isWriteDepth)
            depthCompareOp(depthOp.vk)
            depthBoundsTestEnable(false)
            stencilTestEnable(false)
        }

        val multisampling = callocVkPipelineMultisampleStateCreateInfo {
            sampleShadingEnable(false)
            rasterizationSamples(renderPassVk.numSamples)
            minSampleShading(1f)
            pSampleMask(null)
            alphaToCoverageEnable(false)
            alphaToOneEnable(false)
        }

        val viewportState = callocVkPipelineViewportStateCreateInfo {
            viewportCount(1)
            scissorCount(1)
        }

        val dynamicState = callocVkPipelineDynamicStateCreateInfo {
            pDynamicStates(ints(VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR))
        }

        return device.createGraphicsPipeline {
            pStages(shaderStageInfos)
            pVertexInputState(createVertexBufferLayout())
            pInputAssemblyState(inputAssembly)
            pViewportState(viewportState)
            pDynamicState(dynamicState)
            pRasterizationState(rasterizer)
            pMultisampleState(multisampling)
            pDepthStencilState(depthStencil)
            pColorBlendState(blendInfo(passEncoderState))
            layout(pipelineLayout.handle)
            renderPass(renderPassVk.vkRenderPass.handle)
            subpass(0)
            basePipelineHandle(VK_NULL_HANDLE)
            basePipelineIndex(-1)
        }
    }

    fun bind(cmd: DrawCommand, passEncoderState: RenderPassEncoderState<*>): Boolean {
        users.add(cmd.mesh.id)

        val pipelineData = drawPipeline.pipelineData
        val viewData = cmd.queue.view.viewPipelineData.getPipelineData(drawPipeline)
        val meshData = cmd.mesh.meshPipelineData.getPipelineData(drawPipeline)

        if (!pipelineData.checkBindings() || !viewData.checkBindings() || !meshData.checkBindings()) {
            return false
        }

        val renderPipeline = renderPipelines.getOrPut(passEncoderState.renderPassVk) {
            createRenderPipeline(passEncoderState)
        }

        passEncoderState.setPipeline(renderPipeline)
        viewData.getOrCreateVkData().bind(passEncoderState, this)
        pipelineData.getOrCreateVkData().bind(passEncoderState, this)
        meshData.getOrCreateVkData().bind(passEncoderState, this)
        bindVertexBuffers(passEncoderState, cmd)
        return true
    }

    private fun bindVertexBuffers(passEncoderState: RenderPassEncoderState<*>, cmd: DrawCommand) {
        if (cmd.mesh.geometry.gpuGeometry == null) {
            cmd.mesh.geometry.gpuGeometry = VkGeometry(cmd.mesh, backend)
        }
        val gpuGeom = cmd.mesh.geometry.gpuGeometry as VkGeometry
        gpuGeom.checkBuffers()

        var numBuffers = 1
        //if (cmd.instances != null) numSlots++
        if (gpuGeom.intBuffer != null) numBuffers++
        val buffers = passEncoderState.stack.mallocLong(numBuffers)
        val offsets = passEncoderState.stack.callocLong(numBuffers)

        var slot = 0
        cmd.instances?.let { insts ->
            TODO()
//            if (insts.gpuInstances == null) {
//                insts.gpuInstances = WgpuInstances(insts, backend, cmd.mesh)
//            }
//            val gpuInsts = insts.gpuInstances as WgpuInstances
//            gpuInsts.checkBuffers()
//            gpuInsts.instanceBuffer?.let { passEncoder.setVertexBuffer(slot++, it) }
        }

        buffers.put(slot++, gpuGeom.floatBuffer.handle)
        gpuGeom.intBuffer?.let { buffers.put(slot++, it.handle) }

        vkCmdBindVertexBuffers(passEncoderState.commandBuffer, 0, buffers, offsets)
        vkCmdBindIndexBuffer(passEncoderState.commandBuffer, gpuGeom.indexBuffer.handle, 0, VK_INDEX_TYPE_UINT32)
    }

    override fun removeUser(user: Any) {
        (user as? Mesh)?.let { users.remove(it.id) }
        if (users.isEmpty()) {
            release()
        }
    }

    override fun release() {
        super.release()
        renderPipelines.values.forEach { backend.device.destroyGraphicsPipeline(it) }
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