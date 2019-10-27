package de.fabmax.kool.platform.vk.scene

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.platform.vk.pipeline.GraphicsPipeline
import de.fabmax.kool.platform.vk.pipeline.ShaderStage
import de.fabmax.kool.util.Float32BufferImpl
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class VkTestScene() : VkScene {

    lateinit var sys: VkSystem
    lateinit var model: IndexedMesh
    lateinit var pipeline: Pipeline

    private val startTime = System.nanoTime()
    private lateinit var commandBuffers: CommandBuffers

    override fun onLoad(sys: VkSystem) {
        this.sys = sys

        val texture = VkTexture(sys, "docs/assets/reserve/vk/chalet.jpg")
        model = IndexedMesh.loadModel(sys, "docs/assets/reserve/vk/chalet.obj")

        sys.device.addDependingResource(texture)
        sys.device.addDependingResource(model)

        pipeline = Pipeline.Builder().apply {
            shaderCode = ShaderCode(
                    ShaderStage.fromSource("shader.vert", this::class.java.getResourceAsStream("/shader.vert"), VK_SHADER_STAGE_VERTEX_BIT),
                    ShaderStage.fromSource("shader.frag", this::class.java.getResourceAsStream("/shader.frag"), VK_SHADER_STAGE_FRAGMENT_BIT))

            descriptorLayout.apply {
                +UniformBuffer.Builder().apply {
                    stages += Stage.VERTEX_SHADER
                    +{ UniformMat4f("model") }
                    +{ UniformMat4f("view") }
                    +{ UniformMat4f("proj") }

                    onUpdate = { ubo, cmd ->
                        ubo.updateMvp(0, 1, 2, cmd)
                    }
                }
                +TextureSampler.Builder().apply {
                    name = "tex"
                    stages += Stage.FRAGMENT_SHADER
                }
            }
            vertexLayout.forVertices(model.data)

        }.build().apply {
            descriptorLayout.getTextureSampler("tex").texture = Texture { assets -> assets.loadImageData("") }
        }

        sys.pipelineManager.addPipelineConfig(pipeline)
    }

    override fun onSwapChainCreated(swapChain: SwapChain) {
        val graphicsPipeline = sys.pipelineManager.getPipeline(pipeline)

        for (i in 0 until swapChain.nImages) {
            TODO()
            //graphicsPipeline.descriptorObjects.getDescriptorObject(i, 1).texture = texture
        }
        graphicsPipeline.updateDescriptorSets()
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

        val ubo = pipeline.descriptorLayout.getUniformBuffer("ubo")
        val model = (ubo.uniforms[0] as UniformMat4f)
        val view = (ubo.uniforms[1] as UniformMat4f)
        val proj = (ubo.uniforms[2] as UniformMat4f)

        model.value.setIdentity()
        model.value.rotate(a, 0f, 0f, 1f)
        view.value.setLookAt(Vec3f(2f, 2f, 2f), Vec3f(0f, 0f, 0f), Vec3f(0f, 0f, 1f))
        proj.value.setPerspective(45f, ar, 0.1f, 10f)

        // compensate flipped y coordinate in clip space...
        // this also flips the triangle direction, therefore front-faces are counter-clockwise (-> rasterizer property, createGraphicsPipeline())
        proj.value[1, 1] *= -1f

        sys.pipelineManager.pipelines.values.forEach {
            val uboDesc = it.descriptorObjects.getDescriptorObject(currentImage, 0) as GraphicsPipeline.UboDescriptor
            uboDesc.buffer.mappedFloats {
                ubo.putTo(Float32BufferImpl(this))
            }
        }
        return commandBuffers.vkCommandBuffers[currentImage]
    }

}