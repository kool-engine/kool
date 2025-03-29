package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.gl.getAttribLocations
import de.fabmax.kool.pipeline.backend.gl.locationSize
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.NodeId
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.memStack
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo

class DrawPipelineVk(
    val drawPipeline: DrawPipeline,
    private val vertexShaderModule: VkShaderModule,
    private val fragmentShaderModule: VkShaderModule,
    backend: RenderBackendVk,
): PipelineVk(drawPipeline, backend) {

    private val pipelines = mutableMapOf<RenderPassVk, VkGraphicsPipeline>()
    private val users = mutableSetOf<NodeId>()

    fun updateGeometry(cmd: DrawCommand, passEncoderState: PassEncoderState) {
        if (cmd.geometry.numIndices == 0) return
        users.add(cmd.mesh.id)

        if (cmd.geometry.gpuGeometry == null) {
            cmd.geometry.gpuGeometry = GeometryVk(cmd.mesh, backend)
        }
        val gpuGeom = cmd.geometry.gpuGeometry as GeometryVk
        gpuGeom.checkBuffers(passEncoderState.commandBuffer)

        cmd.instances?.let { insts ->
            if (insts.gpuInstances == null) {
                insts.gpuInstances = InstancesVk(insts, backend, cmd.mesh)
            }
            val gpuInsts = insts.gpuInstances as InstancesVk
            gpuInsts.checkBuffers(passEncoderState.commandBuffer)
        }

        drawPipeline.pipelineData.getOrCreateVkData(passEncoderState.commandBuffer).updateBuffers(passEncoderState)
    }

    fun bind(cmd: DrawCommand, passEncoderState: PassEncoderState): Boolean {
        val pipelineData = drawPipeline.pipelineData
        val viewData = cmd.queue.view.viewPipelineData.getPipelineData(drawPipeline)
        val meshData = cmd.mesh.meshPipelineData.getPipelineData(drawPipeline)

        val pipelineDataOk = pipelineData.checkBindings()
        val viewDataOk = viewData.checkBindings()
        val meshDataOk = meshData.checkBindings()
        if (!viewDataOk || !pipelineDataOk || !meshDataOk) {
            return false
        }

        val pipeline = pipelines.getOrPut(passEncoderState.gpuRenderPass) { createPipeline(passEncoderState) }
        passEncoderState.setPipeline(pipeline)

        val pipelineGroup = pipelineData.getOrCreateVkData(passEncoderState.commandBuffer)
        val viewGroup = viewData.getOrCreateVkData(passEncoderState.commandBuffer)
        val meshGroup = meshData.getOrCreateVkData(passEncoderState.commandBuffer)

        pipelineGroup.prepareBind(passEncoderState)
        viewGroup.prepareBind(passEncoderState)
        meshGroup.prepareBind(passEncoderState)
        passEncoderState.setBindGroups(viewGroup, pipelineGroup, meshGroup, pipelineLayout, BindPoint.Graphics)

        return bindVertexBuffers(cmd, passEncoderState)
    }

    private fun bindVertexBuffers(cmd: DrawCommand, passEncoderState: PassEncoderState): Boolean {
        val gpuGeom = cmd.mesh.geometry.gpuGeometry as GeometryVk? ?: return false
        val gpuInsts = cmd.instances?.gpuInstances as InstancesVk?

        var numBuffers = 0
        if (gpuInsts?.instanceBuffer != null) numBuffers++
        if (gpuGeom.floatBuffer != null) numBuffers++
        if (gpuGeom.intBuffer != null) numBuffers++
        if (numBuffers > 0) {
            bufferHandles.limit(numBuffers)
            bufferOffsets.limit(numBuffers)

            var slot = 0
            gpuGeom.floatBuffer?.let { bufferHandles.put(slot++, it.handle) }
            gpuGeom.intBuffer?.let { bufferHandles.put(slot++, it.handle) }
            gpuInsts?.instanceBuffer?.let { bufferHandles.put(slot++, it.handle) }

            vkCmdBindVertexBuffers(passEncoderState.commandBuffer, 0, bufferHandles, bufferOffsets)
        }
        vkCmdBindIndexBuffer(passEncoderState.commandBuffer, gpuGeom.indexBuffer.handle, 0, VK_INDEX_TYPE_UINT32)
        return true
    }

    private fun createPipeline(passEncoderState: PassEncoderState): VkGraphicsPipeline = memStack {
        val renderPass = passEncoderState.renderPass
        val renderPassVk = passEncoderState.gpuRenderPass

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
                PrimitiveType.TRIANGLE_STRIP -> {
                    topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP)
                    primitiveRestartEnable(true)
                }
            }
        }

        val rasterizer = callocVkPipelineRasterizationStateCreateInfo {
            depthClampEnable(false)
            rasterizerDiscardEnable(false)
            polygonMode(VK_POLYGON_MODE_FILL)
            lineWidth(drawPipeline.lineWidth.clamp(backend.physicalDevice.minLineWidth, backend.physicalDevice.maxLineWidth))
            cullMode(drawPipeline.cullMethod.vk)
            frontFace(if (renderPass.isMirrorY) VK_FRONT_FACE_CLOCKWISE else VK_FRONT_FACE_COUNTER_CLOCKWISE)
            depthBiasEnable(false)
            depthBiasConstantFactor(0f)
            depthBiasClamp(0f)
            depthBiasSlopeFactor(0f)
        }

        val depthOp = when {
            renderPass.depthMode == DepthMode.Reversed && drawPipeline.autoReverseDepthFunc -> {
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

        val depthStencil = if (!renderPass.hasDepth) null else callocVkPipelineDepthStencilStateCreateInfo {
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

        val pipelineRenderingCreateInfo = callocVkPipelineRenderingCreateInfo {
            colorAttachmentCount(renderPassVk.numColorAttachments)
            val colorFormats = mallocInt(renderPassVk.numColorAttachments)
            for (i in 0 until renderPassVk.numColorAttachments) {
                colorFormats.put(i, renderPassVk.colorTargetFormats[i])
            }
            pColorAttachmentFormats(colorFormats)
            if (passEncoderState.gpuRenderPass.hasDepth) {
                depthAttachmentFormat(backend.physicalDevice.depthFormat)
            }
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
            renderPass(0L)
            subpass(0)
            basePipelineHandle(VK_NULL_HANDLE)
            basePipelineIndex(-1)
            pNext(pipelineRenderingCreateInfo)
        }
    }

    private fun MemoryStack.createVertexBufferLayout(): VkPipelineVertexInputStateCreateInfo {
        val bindings = drawPipeline.vertexLayout.bindings.filter { it.vertexAttributes.isNotEmpty() }
        val nVertexBindings = bindings.size
        val bindingDescription = callocVkVertexInputBindingDescriptionN(nVertexBindings) {
            bindings
                .sortedBy { it.inputRate.name }     // INSTANCE first, VERTEX second
                .forEachIndexed { i, binding ->
                    this[i].apply {
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
        val nVertexAttributes = bindings.sumOf { binding ->
            binding.vertexAttributes.sumOf { it.locationSize }
        }
        val attributeDescriptions = callocVkVertexInputAttributeDescriptionN(nVertexAttributes) {
            var iAttrib = 0
            bindings.forEach { binding ->
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

    private fun MemoryStack.blendInfo(passEncoderState: PassEncoderState): VkPipelineColorBlendStateCreateInfo? {
        val renderPass = passEncoderState.renderPass
        val renderPassVk = passEncoderState.gpuRenderPass
        if (renderPassVk.numColorAttachments == 0) {
            return null
        }

        val colorBlendAttachment = callocVkPipelineColorBlendAttachmentStateN(renderPassVk.numColorAttachments) {
            for (i in 0 until renderPass.colorAttachments.size) {
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

    override fun removeUser(user: Any) {
        (user as? Mesh)?.let { users.remove(it.id) }
        if (users.isEmpty()) {
            release()
        }
    }

    override fun release() {
        super.release()
        backend.pipelineManager.removeDrawPipeline(this)
        pipelines.values.forEach { backend.device.destroyGraphicsPipeline(it) }
    }

    private data class AttributeVkProps(val slotOffset: Int, val slotType: Int)

    private fun Attribute.vkProps() = when (type) {
        GpuType.Float1 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32_SFLOAT)
        GpuType.Float2 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32_SFLOAT)
        GpuType.Float3 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32_SFLOAT)
        GpuType.Float4 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32A32_SFLOAT)

        GpuType.Mat2 -> AttributeVkProps(slotOffset = GpuType.Float2.byteSize, slotType = VK_FORMAT_R32G32_SFLOAT)
        GpuType.Mat3 -> AttributeVkProps(slotOffset = GpuType.Float3.byteSize, slotType = VK_FORMAT_R32G32B32_SFLOAT)
        GpuType.Mat4 -> AttributeVkProps(slotOffset = GpuType.Float4.byteSize, slotType = VK_FORMAT_R32G32B32A32_SFLOAT)

        GpuType.Int1 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32_SINT)
        GpuType.Int2 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32_SINT)
        GpuType.Int3 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32_SINT)
        GpuType.Int4 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32A32_SINT)

        GpuType.Uint1 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32_UINT)
        GpuType.Uint2 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32_UINT)
        GpuType.Uint3 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32_UINT)
        GpuType.Uint4 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32A32_UINT)

        GpuType.Bool1 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32_UINT)
        GpuType.Bool2 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32_UINT)
        GpuType.Bool3 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32_UINT)
        GpuType.Bool4 -> AttributeVkProps(slotOffset = 0, slotType = VK_FORMAT_R32G32B32A32_UINT)

        is GpuType.Struct -> TODO("GpuType.STRUCT not implemented")
    }

    companion object {
        private val bufferHandles = MemoryUtil.memCallocLong(3)
        private val bufferOffsets = MemoryUtil.memCallocLong(3)
    }
}