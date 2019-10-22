package de.fabmax.kool.platform

import de.fabmax.kool.GlCapabilities
import de.fabmax.kool.KoolContext
import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.drawqueue.DrawCommandMesh
import de.fabmax.kool.pipeline.UniformBuffer
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.platform.vk.pipeline.GraphicsPipeline
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Float32BufferImpl
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import java.awt.Desktop
import java.net.URI


/**
 * @author fabmax
 */
class Lwjgl3VkContext(props: Lwjgl3Context.InitProps) : KoolContext() {

    override val glCapabilities = GlCapabilities.UNKNOWN_CAPABILITIES

    override val assetMgr = JvmAssetManager(props)

    override var windowWidth = 800
        private set
    override var windowHeight = 600
        private set

    private val vkScene = KoolVkScene()
    private val vkSystem: VkSystem

    init {
        viewport = Viewport(0, 0, windowWidth, windowHeight)
        vkSystem = VkSystem(VkSetup().apply { isValidating = true }, vkScene)
        vkSystem.window.onResize += object : GlfwWindow.OnWindowResizeListener {
            override fun onResize(window: GlfwWindow, newWidth: Int, newHeight: Int) {
                windowWidth = newWidth
                windowHeight = newHeight
                viewport = Viewport(0, 0, windowWidth, windowHeight)
            }
        }
    }

    override fun openUrl(url: String)  = Desktop.getDesktop().browse(URI(url))

    override fun run() {
        var prevTime = System.nanoTime()
        while (!glfwWindowShouldClose(vkSystem.window.glfwWindow)) {
            glfwPollEvents()

            // determine time delta
            val time = System.nanoTime()
            val dt = (time - prevTime) / 1e9
            prevTime = time

            // render engine content (fills the draw queue)
            render(dt)

            // fixme: this is way to complicated:
            // drawQueue now contains actual draw commands
            // command buffer will be set up in onDrawFrame, called as a call back from render loop

            vkSystem.renderLoop.drawFrame()
        }
        vkDeviceWaitIdle(vkSystem.device.vkDevice)
        destroy()
    }

    override fun destroy() {
        vkSystem.destroy()
    }

    override fun checkIsGlThread() { }

    private inner class KoolVkScene: VkScene {
        lateinit var sys: VkSystem
        val cmdPools = mutableListOf<CommandPool>()
        val cmdBuffers = mutableListOf<CommandBuffers>()

        val meshMap = mutableMapOf<Mesh, IndexedMesh>()

        override fun onLoad(sys: VkSystem) {
            this.sys = sys
        }

        override fun onSwapChainCreated(swapChain: SwapChain) {
            cmdBuffers.forEach { it.destroy() }
            cmdBuffers.clear()
            cmdPools.forEach {
                sys.device.removeDependingResource(it)
                it.destroy()
            }
            cmdPools.clear()

            for (i in swapChain.images.indices) {
                val pool = CommandPool(sys, sys.device.graphicsQueue)
                cmdPools += pool
                cmdBuffers += pool.createCommandBuffers(swapChain.images.size)
            }
        }

        override fun onDrawFrame(swapChain: SwapChain, imageIndex: Int): VkCommandBuffer {
            /*
             * From Vulkan Dos and don'ts:
             * Aim for 15-30 command buffers and 5-10 vkQueueSubmit() calls per frame, batch VkSubmitInfo() to a
             * single call as much as possible. Each vkQueueSubmit() has a performance cost on CPU, so lower is
             * generally better. Note that VkSemaphore-based synchronization can only be done across vkQueueSubmit()
             * calls, so you may be forced to split work up into multiple submits.
             */


            cmdBuffers[imageIndex].destroy()
            val pool = cmdPools[imageIndex]
            pool.reset()
            val cmdBufs = pool.createCommandBuffers(swapChain.images.size)
            cmdBuffers[imageIndex] = cmdBufs

            // record command list

            memStack {
                val commandBuffer = cmdBufs.vkCommandBuffers[0]
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
                    framebuffer(swapChain.framebuffers[imageIndex])
                    renderArea {
                        it.offset().apply { x(0); y(0) }
                        it.extent(swapChain.extent)
                    }
                    pClearValues(clearValues)
                }

                vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)

                var graphicsPipeline: GraphicsPipeline? = null
                drawQueue.commands.forEach { cmd ->
                    val pipelineCfg = cmd.pipelineConfig!!
                    if (!sys.pipelineManager.hasPipeline(pipelineCfg)) {
                        sys.pipelineManager.addPipelineConfig(pipelineCfg)
                        sys.pipelineManager.getPipeline(pipelineCfg).updateDescriptorSets()
                    }
                    val pipeline = sys.pipelineManager.getPipeline(pipelineCfg)
                    if (pipeline != graphicsPipeline) {
                        vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.vkGraphicsPipeline)
                        graphicsPipeline = pipeline
                    }

                    cmd as DrawCommandMesh
                    pipeline.updateUbo(cmd, imageIndex)

                    var model = meshMap[cmd.mesh]
                    if (cmd.mesh.meshData.isSyncRequired || model == null) {
                        cmd.mesh.meshData.isSyncRequired = false

                        // (re-)build buffer
                        // fixme: don't do this here, should have happened before (async?)
                        meshMap.remove(cmd.mesh)?.destroy()
                        model = IndexedMesh(sys, cmd.mesh.meshData.vertexList)
                        meshMap[cmd.mesh] = model
                        sys.device.addDependingResource(model)
                    }

                    vkCmdBindVertexBuffers(commandBuffer, 0, longs(model.vertexBuffer.vkBuffer), longs(0L))
                    vkCmdBindIndexBuffer(commandBuffer, model.indexBuffer.vkBuffer, 0L, VK_INDEX_TYPE_UINT32)

                    vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                            pipeline.pipelineLayout, 0, longs(pipeline.descriptorSets[imageIndex]), null)

                    vkCmdDrawIndexed(commandBuffer, model.numIndices, 1, 0, 0, 0)
                }

                vkCmdEndRenderPass(commandBuffer)

                check(vkEndCommandBuffer(commandBuffer) == VK_SUCCESS)
            }

            return cmdBufs.vkCommandBuffers[0]
        }

//        fun GraphicsPipeline.updateDescriptorSets(descriptorLayout: DescriptorLayout, imageIndex: Int) {
//            // fixme: support arbitrary descriptor layouts
//            val ubo = descriptorLayout.descriptors[0] as UniformBuffer
//
//            memStack {
//                val buffereInfo = callocVkDescriptorBufferInfoN(1) {
//                    buffer(uniformBuffers[imageIndex].vkBuffer)
//                    offset(0L)
//                    range(ubo.size.toLong())
//                }
//
//                val descriptorWrite = callocVkWriteDescriptorSetN(1) {
//                    sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
//                    dstSet(descriptorSets[imageIndex])
//                    dstBinding(0)
//                    dstArrayElement(0)
//                    descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
//                    descriptorCount(1)
//                    pBufferInfo(buffereInfo)
//                }
//
//                vkUpdateDescriptorSets(swapChain.sys.device.vkDevice, descriptorWrite, null)
//            }
//        }

        private fun GraphicsPipeline.updateUbo(cmd: DrawCommand, imageIndex: Int) {
            val pipelineCfg = cmd.pipelineConfig!!

            // fixme: support arbitrary descriptor layouts, this assumes UBO with MVP at location 0
            val descObj = descriptorObjects.getDescriptorObject(imageIndex, 0)
            val ubo = descObj.descriptor as UniformBuffer

            val model = (ubo.uniforms[0] as UniformMat4f)
            val view = (ubo.uniforms[1] as UniformMat4f)
            val proj = (ubo.uniforms[2] as UniformMat4f)

            model.value.set(cmd.modelMat)
            view.value.set(cmd.viewMat)
            proj.value.set(cmd.projMat)

            // compensate flipped y coordinate in clip space...
            // this also flips the triangle direction, therefore front-faces are counter-clockwise
            //   -> rasterizer property, createGraphicsPipeline()
            proj.value[1, 1] *= -1f

            descObj.buffer!!.mappedFloats {
                ubo.putTo(Float32BufferImpl(this))
            }
        }
    }
}

