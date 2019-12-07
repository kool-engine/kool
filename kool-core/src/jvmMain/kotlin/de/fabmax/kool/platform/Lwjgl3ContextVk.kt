package de.fabmax.kool.platform

import de.fabmax.kool.DesktopImpl
import de.fabmax.kool.GlCapabilities
import de.fabmax.kool.KoolContext
import de.fabmax.kool.drawqueue.DrawCommandMesh
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.scene.Mesh
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import java.awt.Desktop
import java.net.URI
import java.util.*


/**
 * @author fabmax
 */
class Lwjgl3ContextVk(props: Lwjgl3ContextGL.InitProps) : KoolContext() {
    override val glCapabilities = GlCapabilities.UNKNOWN_CAPABILITIES

    override val assetMgr = JvmAssetManager(props)

    override val shaderGenerator = ShaderGeneratorImplVk()

    override var windowWidth = 1024
        private set
    override var windowHeight = 768
        private set

    private val vkScene = KoolVkScene()
    private val vkSystem: VkSystem

    private object SysInfo : ArrayList<String>() {
        fun set(api: String, dev: String) {
            clear()
            add(api)
            add(dev)
            add("")
            update()
        }

        fun update() {
            val rt = Runtime.getRuntime()
            val freeMem = rt.freeMemory()
            val totalMem = rt.totalMemory()
            set(2, "Heap: ${(totalMem - freeMem) / 1024 / 1024} / ${totalMem / 1024 / 1024} MB")
        }
    }

    init {
        viewport = Viewport(0, 0, windowWidth, windowHeight)
        vkSystem = VkSystem(VkSetup().apply { isValidating = true }, vkScene, this)
        SysInfo.set("Vulkan ${vkSystem.physicalDevice.apiVersion}", vkSystem.physicalDevice.deviceName)

        vkSystem.window.onResize += object : GlfwWindow.OnWindowResizeListener {
            override fun onResize(window: GlfwWindow, newWidth: Int, newHeight: Int) {
                windowWidth = newWidth
                windowHeight = newHeight
                viewport = Viewport(0, 0, windowWidth, windowHeight)
            }
        }

        screenDpi = DesktopImpl.primaryMonitor.dpi

        // maps camera projection matrices to Vulkan coordinates
        projCorrectionMatrix.apply {
            setRow(0, Vec4f(1f, 0f, 0f, 0f))
            setRow(1, Vec4f(0f, -1f, 0f, 0f))
            setRow(2, Vec4f(0f, 0f, 0.5f, 0.5f))
            setRow(3, Vec4f(0f, 0f, 0f, 1f))
        }
    }

    override fun openUrl(url: String)  = Desktop.getDesktop().browse(URI(url))

    override fun run() {
        var prevTime = System.nanoTime()
        while (!glfwWindowShouldClose(vkSystem.window.glfwWindow)) {
            SysInfo.update()
            glfwPollEvents()

            // determine time delta
            val time = System.nanoTime()
            val dt = (time - prevTime) / 1e9
            prevTime = time

            // render engine content (fills the draw queue)
            render(dt)

            // fixme: this is way to complicated, tighter integration of Vulkan stuff needed
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

    override fun getSysInfos(): List<String> = SysInfo

    private inner class KoolVkScene: VkScene {
        lateinit var sys: VkSystem
        val cmdPools = mutableListOf<CommandPool>()
        val cmdBuffers = mutableListOf<CommandBuffers>()

        val meshMap = mutableMapOf<Mesh, IndexedMesh>()

        inner class DeleteMesh(val mesh: IndexedMesh, var deleteDelay: Int)
        private val meshDeleteQueue = ArrayDeque<DeleteMesh>()

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
                        it.float32(0, clearColor.r)
                        it.float32(1, clearColor.g)
                        it.float32(2, clearColor.b)
                        it.float32(3, clearColor.a)
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

                // optimize draw queue order
                // fixme: more sophisticated sorting, customizable draw order, etc.
                //drawQueue.commands.sortBy { it.pipeline!!.pipelineHash }

                var prevPipeline = 0L
                drawQueue.commands.forEach { cmd ->
                    val pipelineCfg = cmd.pipeline!!
                    if (!sys.pipelineManager.hasPipeline(pipelineCfg)) {
                        sys.pipelineManager.addPipelineConfig(pipelineCfg)
                    }
                    val pipeline = sys.pipelineManager.getPipeline(pipelineCfg)
                    if (pipelineCfg.pipelineHash != prevPipeline) {
                        vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.vkGraphicsPipeline)
                        prevPipeline = pipelineCfg.pipelineHash
                    }
                    val descriptorSet = pipeline.getDescriptorSetInstance(pipelineCfg)

                    cmd as DrawCommandMesh
                    if (descriptorSet.updateDescriptors(cmd, imageIndex, sys)) {
                        descriptorSet.updateDescriptorSets()

                        var model = meshMap[cmd.mesh]
                        if (cmd.mesh.meshData.isSyncRequired || model == null) {
                            cmd.mesh.meshData.isSyncRequired = false

                            // (re-)build buffer
                            // fixme: don't do this here, should have happened before (async?)
                            meshMap.remove(cmd.mesh)?.let {
                                meshDeleteQueue.addLast(DeleteMesh(it, swapChain.nImages))
                            }
                            model = IndexedMesh(sys, cmd.mesh.meshData.vertexList)
                            meshMap[cmd.mesh] = model
                            sys.device.addDependingResource(model)
                        }

                        vkCmdBindVertexBuffers(commandBuffer, 0, longs(model.vertexBuffer.vkBuffer), longs(0L))
                        vkCmdBindIndexBuffer(commandBuffer, model.indexBuffer.vkBuffer, 0L, VK_INDEX_TYPE_UINT32)

                        vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                                pipeline.pipelineLayout, 0, longs(descriptorSet.getDescriptorSet(imageIndex)), null)

                        vkCmdDrawIndexed(commandBuffer, model.numIndices, 1, 0, 0, 0)
                    }
                }

                vkCmdEndRenderPass(commandBuffer)
                check(vkEndCommandBuffer(commandBuffer) == VK_SUCCESS)

                // delete discarded meshes
                val delIt = meshDeleteQueue.iterator()
                for (delMesh in delIt) {
                    if (delMesh.deleteDelay-- <= 0) {
                        sys.device.removeDependingResource(delMesh.mesh)
                        delMesh.mesh.destroy()
                    }
                    delIt.remove()
                }
            }

            return cmdBufs.vkCommandBuffers[0]
        }
    }
}

