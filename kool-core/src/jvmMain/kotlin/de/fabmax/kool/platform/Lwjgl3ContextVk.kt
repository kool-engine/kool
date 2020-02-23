package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.platform.vk.RenderPass
import de.fabmax.kool.platform.vk.util.bitValue
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MixedBufferImpl
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkClearValue
import org.lwjgl.vulkan.VkCommandBuffer
import java.awt.Desktop
import java.net.URI
import java.util.*
import java.util.concurrent.CompletableFuture


/**
 * @author fabmax
 */
class Lwjgl3ContextVk(props: InitProps) : KoolContext() {
    override val assetMgr = JvmAssetManager(props, this)

    override val shaderGenerator = ShaderGeneratorImplVk()

    override var windowWidth = props.width
        private set
    override var windowHeight = props.height
        private set

    private val vkScene = KoolVkScene()
    internal val vkSystem: VkSystem

    private val gpuThreadRunnables = mutableListOf<GpuThreadRunnable>()

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
        setupInput(vkSystem.window.glfwWindow)

        screenDpi = DesktopImpl.primaryMonitor.dpi

        // maps camera projection matrices to Vulkan coordinates
        projCorrectionMatrix.apply {
            setRow(0, Vec4f(1f, 0f, 0f, 0f))
            setRow(1, Vec4f(0f, -1f, 0f, 0f))
            setRow(2, Vec4f(0f, 0f, 0.5f, 0.5f))
            setRow(3, Vec4f(0f, 0f, 0f, 1f))
        }

        depthBiasMatrix.apply {
            setRow(0, Vec4f(0.5f, 0.0f, 0.0f, 0.5f))
            setRow(1, Vec4f(0.0f, 0.5f, 0.0f, 0.5f))
            setRow(2, Vec4f(0.0f, 0.0f, 1.0f, 0.0f))
            setRow(3, Vec4f(0.0f, 0.0f, 0.0f, 1.0f))
        }
    }

    override fun openUrl(url: String)  = Desktop.getDesktop().browse(URI(url))

    override fun run() {
        var prevTime = System.nanoTime()
        while (!glfwWindowShouldClose(vkSystem.window.glfwWindow)) {
            SysInfo.update()
            glfwPollEvents()

            synchronized(gpuThreadRunnables) {
                if (gpuThreadRunnables.isNotEmpty()) {
                    for (r in gpuThreadRunnables) {
                        r.r()
                        r.future.complete(null)
                    }
                    gpuThreadRunnables.clear()
                }
            }

            // determine time delta
            val time = System.nanoTime()
            val dt = (time - prevTime) / 1e9
            prevTime = time

            // render engine content (fills the draw queue)
            render(dt)

            // fixme: this is way to complicated, tighter integration of Vulkan stuff needed
            // drawQueue now contains actual draw commands
            // command buffer will be set up in onDrawFrame, called as a call back from render loop

            engineStats.resetPrimitveCount()
            vkSystem.renderLoop.drawFrame()
        }
        vkDeviceWaitIdle(vkSystem.device.vkDevice)
        destroy()
    }

    override fun destroy() {
        vkSystem.destroy()
    }

    override fun getSysInfos(): List<String> = SysInfo

    // fixme: use Coroutine stuff instead
    fun runOnGpuThread(action: () -> Unit): CompletableFuture<Void> {
        synchronized(gpuThreadRunnables) {
            val r = GpuThreadRunnable(action)
            gpuThreadRunnables += r
            return r.future
        }
    }

    private fun setupInput(window: Long) {
        // install window callbacks
        glfwSetWindowPosCallback(window) { _, x, y ->
            screenDpi = getResolutionAt(x, y)
        }

        // install mouse callbacks
        glfwSetMouseButtonCallback(window) { _, btn, act, _ ->
            inputMgr.handleMouseButtonState(btn, act == GLFW_PRESS)
        }
        glfwSetCursorPosCallback(window) { _, x, y ->
            inputMgr.handleMouseMove(x.toFloat(), y.toFloat())
        }
        glfwSetCursorEnterCallback(window) { _, entered ->
            if (!entered) {
                inputMgr.handleMouseExit()
            }
        }
        glfwSetScrollCallback(window) { _, _, yOff ->
            inputMgr.handleMouseScroll(yOff.toFloat())
        }

        // install keyboard callbacks
        glfwSetKeyCallback(window) { _, key, _, action, mods ->
            val event = when (action) {
                GLFW_PRESS -> InputManager.KEY_EV_DOWN
                GLFW_REPEAT -> InputManager.KEY_EV_DOWN or InputManager.KEY_EV_REPEATED
                GLFW_RELEASE -> InputManager.KEY_EV_UP
                else -> -1
            }
            if (event != -1) {
                val keyCode = KEY_CODE_MAP[key] ?: key
                var keyMod = 0
                if (mods and GLFW_MOD_ALT != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_ALT
                }
                if (mods and GLFW_MOD_CONTROL != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_CTRL
                }
                if (mods and GLFW_MOD_SHIFT != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_SHIFT
                }
                if (mods and GLFW_MOD_SUPER != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_SUPER
                }
                inputMgr.keyEvent(keyCode, keyMod, event)
            }
        }
        glfwSetCharCallback(window) { _, codepoint ->
            inputMgr.charTyped(codepoint.toChar())
        }
    }

    private inner class KoolVkScene: VkScene {
        lateinit var sys: VkSystem
        val cmdPools = mutableListOf<CommandPool>()
        val cmdBuffers = mutableListOf<CommandBuffers>()

        val meshMap = mutableMapOf<Long, IndexedMesh>()

        inner class DelayAction(var delay: Int = sys.swapChain?.nImages ?: 3, val action: () -> Unit)
        private val actionQueue = ArrayDeque<DelayAction>()

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

            if (disposablePipelines.isNotEmpty()) {
                disposePipelines()
            }

            cmdBuffers[imageIndex].destroy()
            val pool = cmdPools[imageIndex]
            pool.reset()
            val cmdBufs = pool.createCommandBuffers(1)
            cmdBuffers[imageIndex] = cmdBufs

            // record command list

            memStack {
                val commandBuffer = cmdBufs.vkCommandBuffers[0]
                val beginInfo = callocVkCommandBufferBeginInfo { sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO) }
                check(vkBeginCommandBuffer(commandBuffer, beginInfo) == VK_SUCCESS)

                for (i in 0 until offscreenPasses.size) {
                    renderOffscreen(commandBuffer, offscreenPasses[i])
                }

                val renderPassInfo = renderPassBeginInfo(swapChain.renderPass, swapChain.framebuffers[imageIndex], clearColor)

                // optimize draw queue order
                // fixme: more sophisticated sorting, customizable draw order, etc.
                //drawQueue.commands.sortBy { it.pipeline!!.pipelineHash }

                vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
                renderDrawQueue(commandBuffer, drawQueue, imageIndex, swapChain.renderPass, swapChain.nImages, false)
                vkCmdEndRenderPass(commandBuffer)

                check(vkEndCommandBuffer(commandBuffer) == VK_SUCCESS)

                // delete discarded meshes
                val delIt = actionQueue.iterator()
                for (action in delIt) {
                    if (action.delay-- <= 0) {
                        //sys.device.removeDependingResource(delMesh.mesh)
                        //delMesh.mesh.destroy()
                        action.action()
                        delIt.remove()
                    }
                }
            }

            return cmdBufs.vkCommandBuffers[0]
        }

        private fun disposePipelines() {
            disposablePipelines.forEach { pipeline ->
                val delMesh = meshMap.remove(pipeline.pipelineInstanceId)
                val delPipeline = sys.pipelineManager.getPipeline(pipeline)

                actionQueue += DelayAction {
                    delMesh?.let {
                        sys.device.removeDependingResource(it)
                        it.destroy()
                    }
                    delPipeline?.freeDescriptorSetInstance(pipeline)
                    // todo: dispose entire pipeline if no instances left
                }
            }
            disposablePipelines.clear()
        }

        private fun MemoryStack.renderDrawQueue(commandBuffer: VkCommandBuffer, drawQueue: DrawQueue, imageIndex: Int,
                                                renderPass: RenderPass, nImages: Int, dynVp: Boolean) {
            var prevPipeline = 0UL
            drawQueue.commands.forEach { cmd ->
                if (!cmd.mesh.geometry.isEmpty()) {
                    val pipelineCfg = cmd.pipeline ?: throw KoolException("Mesh pipeline not set")
                    if (!sys.pipelineManager.hasPipeline(pipelineCfg, renderPass.vkRenderPass)) {
                        sys.pipelineManager.addPipelineConfig(pipelineCfg, nImages, renderPass, dynVp)
                    }
                    val pipeline = sys.pipelineManager.getPipeline(pipelineCfg, renderPass.vkRenderPass)
                    if (pipelineCfg.pipelineHash != prevPipeline) {
                        vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.vkGraphicsPipeline)
                        prevPipeline = pipelineCfg.pipelineHash
                    }
                    val descriptorSet = pipeline.getDescriptorSetInstance(pipelineCfg)

                    if (descriptorSet.updateDescriptors(cmd, imageIndex, sys)) {
                        descriptorSet.updateDescriptorSets(imageIndex)

                        var model = meshMap[pipelineCfg.pipelineInstanceId]
                        if ((cmd.mesh.geometry.hasChanged && !cmd.mesh.geometry.isBatchUpdate) || model == null) {
                            // fixme: currently there is one IndexedMesh per pipeline, not per mesh
                            // if mesh is rendered multiple times (e.g. by additional shadow passes), clearing
                            // hasChanged flag early results in IndexedMeshes not being updated
                            actionQueue += DelayAction(0) { cmd.mesh.geometry.hasChanged = false }

                            // (re-)build buffer
                            // fixme: don't do this here, should have happened before (async?)
                            meshMap.remove(pipelineCfg.pipelineInstanceId)?.let {
                                actionQueue += DelayAction {
                                    sys.device.removeDependingResource(it)
                                    it.destroy()
                                }
                            }
                            model = IndexedMesh(sys, cmd.mesh.geometry)
                            meshMap[pipelineCfg.pipelineInstanceId] = model
                            sys.device.addDependingResource(model)
                        }

                        pipelineCfg.pushConstantRanges.forEach {
                            val flags = it.stages.fold(0) { f, stage -> f or stage.bitValue() }
                            vkCmdPushConstants(commandBuffer, pipeline.pipelineLayout, flags, 0, (it.toBuffer() as MixedBufferImpl).buffer)
                        }

                        vkCmdBindVertexBuffers(commandBuffer, 0, longs(model.vertexBuffer.vkBuffer), longs(0L))
                        vkCmdBindIndexBuffer(commandBuffer, model.indexBuffer.vkBuffer, 0L, VK_INDEX_TYPE_UINT32)
                        vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                                pipeline.pipelineLayout, 0, longs(descriptorSet.getDescriptorSet(imageIndex)), null)
                        vkCmdDrawIndexed(commandBuffer, model.numIndices, 1, 0, 0, 0)

                        engineStats.addPrimitiveCount(cmd.mesh.geometry.numPrimitives)
                    }
                }
            }
        }

        private fun MemoryStack.setViewport(commandBuffer: VkCommandBuffer, x: Int, y: Int, width: Int, height: Int) {
            val viewport = callocVkViewportN(1) {
                x(x.toFloat())
                y(y.toFloat())
                width(width.toFloat())
                height(height.toFloat())
                minDepth(0f)
                maxDepth(1f)
            }
            vkCmdSetViewport(commandBuffer, 0, viewport)

            val scissor = callocVkRect2DN(1) {
                offset { it.set(x, y) }
                extent { it.width(width); it.height(height) }
            }
            vkCmdSetScissor(commandBuffer, 0, scissor)
        }

        private fun MemoryStack.renderOffscreen(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenPass) {
            when (offscreenPass) {
                is OffscreenPass2d -> renderOffscreen2d(commandBuffer, offscreenPass)
                is OffscreenPassCube -> renderOffscreenCube(commandBuffer, offscreenPass)
                else -> throw IllegalArgumentException("Not implemented: ${offscreenPass::class.java}")
            }
        }

        private fun MemoryStack.renderOffscreen2d(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenPass2d) {
            val rp = offscreenPass.impl.getRenderPass(sys)
            val renderPassInfo = renderPassBeginInfo(rp, rp.frameBuffer, offscreenPass.clearColor)

            vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
            setViewport(commandBuffer, 0, 0, offscreenPass.mipWidth(offscreenPass.targetMipLevel), offscreenPass.mipHeight(offscreenPass.targetMipLevel))
            renderDrawQueue(commandBuffer, offscreenPass.drawQueues[0], 0, rp, 1, true)
            vkCmdEndRenderPass(commandBuffer)
        }

        private fun MemoryStack.renderOffscreenCube(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenPassCube) {
            val rp = offscreenPass.impl.getRenderPass(sys)
            val renderPassInfo = renderPassBeginInfo(rp, rp.frameBuffer, offscreenPass.clearColor)

            offscreenPass.impl.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
            // fixme: for some reason (timing / sync) last view is not copied sometimes? super duper fix: render last view twice
            for (view in cubeRenderPassViews) {
                vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
                setViewport(commandBuffer, 0, 0, offscreenPass.mipWidth(offscreenPass.targetMipLevel), offscreenPass.mipHeight(offscreenPass.targetMipLevel))
                renderDrawQueue(commandBuffer, offscreenPass.drawQueues[view.index], view.index, rp, 6, true)
                vkCmdEndRenderPass(commandBuffer)
                offscreenPass.impl.copyView(commandBuffer, view)
            }
            if (offscreenPass.mipLevels > 1 && offscreenPass.targetMipLevel < 0) {
                offscreenPass.impl.generateMipmaps(commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
            } else {
                offscreenPass.impl.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
            }
        }
    }

    private fun MemoryStack.renderPassBeginInfo(renderPass: RenderPass, frameBuffer: Long, clearColor: Color) =
            callocVkRenderPassBeginInfo {
                sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                renderPass(renderPass.vkRenderPass)
                framebuffer(frameBuffer)
                renderArea { r ->
                    r.offset { it.x(0); it.y(0) }
                    r.extent { it.width(renderPass.maxWidth); it.height(renderPass.maxHeight) }
                }
                pClearValues(callocVkClearValueN(2) {
                    this[0].setColor(clearColor)
                    this[1].depthStencil { it.depth(1f); it.stencil(0) }
                })
            }

    private fun VkClearValue.setColor(color: Color) {
        color {
            it.float32(0, color.r)
            it.float32(1, color.g)
            it.float32(2, color.b)
            it.float32(3, color.a)
        }
    }

    private class GpuThreadRunnable(val r: () -> Unit) {
        val future = CompletableFuture<Void>()
    }

    companion object {
        private val cubeRenderPassViews = Array(7) {
            i -> OffscreenPassCube.ViewDirection.values()[i % OffscreenPassCube.ViewDirection.values().size]
        }

        val KEY_CODE_MAP: Map<Int, Int> = mutableMapOf(
                GLFW_KEY_LEFT_CONTROL to InputManager.KEY_CTRL_LEFT,
                GLFW_KEY_RIGHT_CONTROL to InputManager.KEY_CTRL_RIGHT,
                GLFW_KEY_LEFT_SHIFT to InputManager.KEY_SHIFT_LEFT,
                GLFW_KEY_RIGHT_SHIFT to InputManager.KEY_SHIFT_RIGHT,
                GLFW_KEY_LEFT_ALT to InputManager.KEY_ALT_LEFT,
                GLFW_KEY_RIGHT_ALT to InputManager.KEY_ALT_RIGHT,
                GLFW_KEY_LEFT_SUPER to InputManager.KEY_SUPER_LEFT,
                GLFW_KEY_RIGHT_SUPER to InputManager.KEY_SUPER_RIGHT,
                GLFW_KEY_ESCAPE to InputManager.KEY_ESC,
                GLFW_KEY_MENU to InputManager.KEY_MENU,
                GLFW_KEY_ENTER to InputManager.KEY_ENTER,
                GLFW_KEY_KP_ENTER to InputManager.KEY_NP_ENTER,
                GLFW_KEY_KP_DIVIDE to InputManager.KEY_NP_DIV,
                GLFW_KEY_KP_MULTIPLY to InputManager.KEY_NP_MUL,
                GLFW_KEY_KP_ADD to InputManager.KEY_NP_PLUS,
                GLFW_KEY_KP_SUBTRACT to InputManager.KEY_NP_MINUS,
                GLFW_KEY_BACKSPACE to InputManager.KEY_BACKSPACE,
                GLFW_KEY_TAB to InputManager.KEY_TAB,
                GLFW_KEY_DELETE to InputManager.KEY_DEL,
                GLFW_KEY_INSERT to InputManager.KEY_INSERT,
                GLFW_KEY_HOME to InputManager.KEY_HOME,
                GLFW_KEY_END to InputManager.KEY_END,
                GLFW_KEY_PAGE_UP to InputManager.KEY_PAGE_UP,
                GLFW_KEY_PAGE_DOWN to InputManager.KEY_PAGE_DOWN,
                GLFW_KEY_LEFT to InputManager.KEY_CURSOR_LEFT,
                GLFW_KEY_RIGHT to InputManager.KEY_CURSOR_RIGHT,
                GLFW_KEY_UP to InputManager.KEY_CURSOR_UP,
                GLFW_KEY_DOWN to InputManager.KEY_CURSOR_DOWN,
                GLFW_KEY_F1 to InputManager.KEY_F1,
                GLFW_KEY_F2 to InputManager.KEY_F2,
                GLFW_KEY_F3 to InputManager.KEY_F3,
                GLFW_KEY_F4 to InputManager.KEY_F4,
                GLFW_KEY_F5 to InputManager.KEY_F5,
                GLFW_KEY_F6 to InputManager.KEY_F6,
                GLFW_KEY_F7 to InputManager.KEY_F7,
                GLFW_KEY_F8 to InputManager.KEY_F8,
                GLFW_KEY_F9 to InputManager.KEY_F9,
                GLFW_KEY_F10 to InputManager.KEY_F10,
                GLFW_KEY_F11 to InputManager.KEY_F11,
                GLFW_KEY_F12 to InputManager.KEY_F12
        )
    }

    class InitProps(init: InitProps.() -> Unit = {}) {
        var width = 1200
        var height = 800
        var title = "Kool"
        var monitor = 0L
        var share = 0L

        var msaaSamples = 8

        var assetsBaseDir = "./assets"

        val extraFonts = mutableListOf<String>()

        init {
            init()
        }
    }
}

