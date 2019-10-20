package de.fabmax.kool.platform.vk.scene

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.platform.vk.pipeline.GraphicsPipeline
import de.fabmax.kool.platform.vk.pipeline.ShaderStage
import de.fabmax.kool.platform.vk.pipeline.SpirvShaderCode
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class VkTestScene() : VkScene {

    lateinit var sys: VkSystem
    lateinit var texture: Texture
    lateinit var model: IndexedMesh
    lateinit var pipelineConfig: PipelineConfig

    private val startTime = System.nanoTime()
    private val ubo = UniformBufferObject()
    private lateinit var commandBuffers: CommandBuffers

    override fun onLoad(sys: VkSystem) {
        this.sys = sys
        texture = Texture(sys, "docs/assets/reserve/vk/chalet.jpg")
        model = IndexedMesh.loadModel(sys, "docs/assets/reserve/vk/chalet.obj")

        sys.device.addDependingResource(texture)
        sys.device.addDependingResource(model)

        pipelineConfig = PipelineConfig.Builder().apply {
            shaderCode = SpirvShaderCode(mutableListOf(
                    ShaderStage.fromSource("shader.vert", this::class.java.getResourceAsStream("/shader.vert"), VK_SHADER_STAGE_VERTEX_BIT ),
                    ShaderStage.fromSource("shader.frag", this::class.java.getResourceAsStream("/shader.frag"), VK_SHADER_STAGE_FRAGMENT_BIT )
            ))

            uniformLayout = UniformLayoutDescription(listOf(
                    UniformLayoutDescription.Binding("ubo", 0, UniformType.UNIFORM_BUFFER, setOf(Stage.VERTEX_SHADER), 1),
                    UniformLayoutDescription.Binding("texture", 1, UniformType.IMAGE_SAMPLER, setOf(Stage.FRAGMENT_SHADER), 1)
            ))

            vertexLayout = VertexLayoutDescription.forVertices(model.data)
        }.build()
        sys.pipelineManager.addPipelineConfig(pipelineConfig)
    }

    override fun onSwapChainCreated(swapChain: SwapChain) {
        val graphicsPipeline = sys.pipelineManager.getPipeline(pipelineConfig)

        graphicsPipeline.updateDescriptorSets(texture)
        commandBuffers = sys.commandPool.createCommandBuffers(swapChain.images.size)
        swapChain.addDependingResource(commandBuffers)

        memStack {
            commandBuffers.vkCommandBuffers.forEachIndexed { i, commandBuffer ->
                val beginInfo = callocVkCommandBufferBeginInfo { sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO) }
                check(vkBeginCommandBuffer(commandBuffer, beginInfo) == VK_SUCCESS)

                val clearValues = callocVkClearValueN(2) {
                    this[0].color {
                        it.float32(0, 0.05f)
                        it.float32(1, 0.1f)
                        it.float32(2, 0.2f)
                        it.float32(3, 1f)
                    }
                    this[1].depthStencil {
                        it.depth(1f)
                        it.stencil(0)
                    }
                }
                val renderPassInfo = callocVkRenderPassBeginInfo {
                    sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                    renderPass(swapChain.renderPass.vkRenderPass)
                    framebuffer(swapChain.framebuffers[i])
                    renderArea {
                        it.offset().apply { x(0); y(0) }
                        it.extent(swapChain.extent)
                    }
                    pClearValues(clearValues)
                }

                vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
                vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, graphicsPipeline.vkGraphicsPipeline)

                vkCmdBindVertexBuffers(commandBuffer, 0, longs(model.vertexBuffer.vkBuffer), longs(0L))
                vkCmdBindIndexBuffer(commandBuffer, model.indexBuffer.vkBuffer, 0L, VK_INDEX_TYPE_UINT32)

                vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                    graphicsPipeline.pipelineLayout, 0, longs(graphicsPipeline.descriptorSets[i]), null)

                vkCmdDrawIndexed(commandBuffer, model.numIndices, 1, 0, 0, 0)
                vkCmdEndRenderPass(commandBuffer)

                check(vkEndCommandBuffer(commandBuffer) == VK_SUCCESS)
            }
        }
    }

    override fun onDrawFrame(swapChain: SwapChain, currentImage: Int): VkCommandBuffer {
        val t = System.nanoTime() - startTime
        val a = (t / 1e9 * 30).toFloat()
        val ar = swapChain.extent.width() / swapChain.extent.height().toFloat()

        ubo.reset()
        ubo.model.rotate(a, 0f, 0f, 1f)
        ubo.view.setLookAt(Vec3f(2f, 2f, 2f), Vec3f(0f, 0f, 0f), Vec3f(0f, 0f, 1f))
        ubo.proj.setPerspective(45f, ar, 0.1f, 10f)

        // compensate flipped y coordinate in clip space...
        // this also flips the triangle direction, therefore front-faces are counter-clockwise (-> rasterizer property, createGraphicsPipeline())
        ubo.proj[1, 1] *= -1f

        sys.pipelineManager.pipelines.values.forEach {
            it.uniformBuffers[currentImage].mappedFloats {
                put(ubo.model.matrix)
                put(ubo.view.matrix)
                put(ubo.proj.matrix)
            }
        }

        return commandBuffers.vkCommandBuffers[currentImage]
    }

    fun GraphicsPipeline.updateDescriptorSets(bindTexture: Texture) {
        memStack {
            for (i in swapChain.images.indices) {
                val buffereInfo = callocVkDescriptorBufferInfoN(1) {
                    buffer(uniformBuffers[i].vkBuffer)
                    offset(0L)
                    range(UniformBufferObject.SIZE.toLong())
                }

                val imageInfo = callocVkDescriptorImageInfoN(1) {
                    imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                    imageView(bindTexture.textureImageView.vkImageView)
                    sampler(bindTexture.sampler)
                }

                val descriptorWrite = callocVkWriteDescriptorSetN(2) {
                    this[0]
                            .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                            .dstSet(descriptorSets[i])
                            .dstBinding(0)
                            .dstArrayElement(0)
                            .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                            .descriptorCount(1)
                            .pBufferInfo(buffereInfo)
                    this[1]
                            .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                            .dstSet(descriptorSets[i])
                            .dstBinding(1)
                            .dstArrayElement(0)
                            .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                            .descriptorCount(1)
                            .pImageInfo(imageInfo)
                }

                vkUpdateDescriptorSets(swapChain.sys.device.vkDevice, descriptorWrite, null)
            }
        }
    }

}