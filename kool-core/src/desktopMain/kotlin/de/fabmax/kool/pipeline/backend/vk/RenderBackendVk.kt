package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackendJvm
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.pipeline.backend.vk.trash.VkOffscreenPassCube
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.memStack
import kotlinx.coroutines.CompletableDeferred
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.vulkan.VkCommandBuffer

class RenderBackendVk(val ctx: Lwjgl3Context) : RenderBackendJvm {
    override val name = "Vulkan backend"
    override val apiName: String
    override val deviceName: String

    override val glfwWindow: GlfwVkWindow

    override val deviceCoordinates: DeviceCoordinates = DeviceCoordinates.VULKAN
    override val features = BackendFeatures(
        computeShaders = true,
        cubeMapArrays = false,
        reversedDepth = true
    )

    val setup = KoolSystem.configJvm.vkSetup ?: VkSetup()

    val instance: Instance
    val physicalDevice: PhysicalDevice
    val device: Device
    val memManager: MemoryManager
    val commandPool: CommandPool
    val commandBuffers: List<VkCommandBuffer>
    var swapchain: Swapchain; private set
    val textureLoader: TextureLoaderVk
    val pipelineManager: PipelineManager
    val screenRenderPass: ScreenRenderPassVk
    val clearHelper = ClearHelper(this)
    private val passEncoderState = RenderPassEncoderState(this)

    private var windowResized = false

    override var frameGpuTime: Double = 0.0

    init {
        // tell GLFW to not initialize default OpenGL API before we create the window
        check(GLFWVulkan.glfwVulkanSupported()) { "Cannot find a compatible Vulkan installable client driver (ICD)" }
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API)

        glfwWindow = GlfwVkWindow(this, ctx)
        glfwWindow.isFullscreen = KoolSystem.configJvm.isFullscreen
        instance = Instance(this, KoolSystem.configJvm.windowTitle)
        glfwWindow.createSurface()

        physicalDevice = PhysicalDevice(this)
        device = Device(this)
        apiName = "Vulkan ${physicalDevice.apiVersion}"
        deviceName = physicalDevice.deviceName

        memManager = MemoryManager(this)
        commandPool = CommandPool(this, device.graphicsQueue)
        commandBuffers = commandPool.allocateCommandBuffers(Swapchain.MAX_FRAMES_IN_FLIGHT)
        swapchain = Swapchain(this)
        textureLoader = TextureLoaderVk(this)
        pipelineManager = PipelineManager(this)
        screenRenderPass = ScreenRenderPassVk(this)

        glfwWindow.onResize += GlfwVkWindow.OnWindowResizeListener { _, _ -> windowResized = true }
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        memStack {
            var imgOk = swapchain.acquireNextImage()
            if (imgOk) {
                passEncoderState.beginFrame(this)

                ctx.backgroundScene.renderOffscreenPasses(passEncoderState)
                if (ctx.scenes.isEmpty()) {
                    screenRenderPass.renderScene(ctx.backgroundScene.mainRenderPass, passEncoderState)
                }

                for (i in ctx.scenes.indices) {
                    val scene = ctx.scenes[i]
                    if (scene.isVisible) {
                        //val t = Time.precisionTime
                        scene.renderOffscreenPasses(passEncoderState)
                        screenRenderPass.renderScene(scene.mainRenderPass, passEncoderState)
                        //scene.sceneDrawTime = Time.precisionTime - t
                    }
                }

//        if (gpuReadbacks.isNotEmpty()) {
//            // copy all buffers requested for readback to temporary buffers using the current command encoder
//            copyReadbacks(encoder)
//        }
//        timestampQuery.resolve(encoder)
//        device.queue.submit(arrayOf(encoder.finish()))

                passEncoderState.endFrame()

//        timestampQuery.readTimestamps()
//        if (gpuReadbacks.isNotEmpty()) {
//            // after encoder is finished and submitted, temp buffers can be mapped for readback
//            mapReadbacks()
//        }
                imgOk = swapchain.presentNextImage(this)
            }
            if (!imgOk || windowResized) {
                windowResized = false
                recreateSwapchain()
            }
        }
    }

    private fun Scene.renderOffscreenPasses(passEncoderState: RenderPassEncoderState) {
        for (i in sortedOffscreenPasses.indices) {
            val pass = sortedOffscreenPasses[i]
            if (pass.isEnabled) {
                pass.render(passEncoderState)
            }
        }
    }

    private fun OffscreenRenderPass.render(passEncoderState: RenderPassEncoderState) {
        when (this) {
            is OffscreenRenderPass2d -> draw(passEncoderState)
            is OffscreenRenderPassCube -> draw(passEncoderState)
            is OffscreenRenderPass2dPingPong -> draw(passEncoderState)
            is ComputeRenderPass -> dispatch(passEncoderState)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $this")
        }
    }

    private fun OffscreenRenderPass2dPingPong.draw(passEncoderState: RenderPassEncoderState) {
        for (i in 0 until pingPongPasses) {
            onDrawPing?.invoke(i)
            ping.draw(passEncoderState)
            onDrawPong?.invoke(i)
            pong.draw(passEncoderState)
        }
    }

    private fun OffscreenRenderPass2d.draw(passEncoderState: RenderPassEncoderState) {
        (impl as OffscreenPass2dVk).draw(passEncoderState)
    }

    private fun OffscreenRenderPassCube.draw(passEncoderState: RenderPassEncoderState) {
        TODO()
    }

    private fun ComputeRenderPass.dispatch(passEncoderState: RenderPassEncoderState) {
        TODO()
    }

    override fun cleanup(ctx: KoolContext) {
        device.waitForIdle()
        instance.release()
    }

    override fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode {
        val src = KslGlslGeneratorVk().generateProgram(shader.program, pipeline)
        return ShaderCodeVk.drawShaderCode(src.vertexSrc, src.fragmentSrc)
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        val src = KslGlslGeneratorVk().generateComputeProgram(shader.program, pipeline)
        return ShaderCodeVk.computeShaderCode(src.computeSrc)
    }

    override fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl {
        return OffscreenPass2dVk(parentPass, 1, this)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl {
        return VkOffscreenPassCube(parentPass)
    }

    override fun createComputePass(parentPass: ComputeRenderPass): ComputePassImpl {
        TODO("Not yet implemented")
    }

    override fun <T : ImageData> uploadTextureData(tex: Texture<T>) = textureLoader.loadTexture(tex)

    override fun downloadStorageBuffer(storage: StorageBuffer, deferred: CompletableDeferred<Unit>) {
        TODO("Not yet implemented")
    }

    override fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>) {
        TODO("Not yet implemented")
    }

    private fun recreateSwapchain() {
        // Theoretically it might be possible for the swapchain image format to change (e.g. because the window
        // is moved to another monitor with HDR) in that case the screen render pass would also need to be
        // recreated.
        // However, currently, image format is more or less hardcoded to 8-bit SRGB in PhysicalDevice, so no need
        // for all the fuzz.

        device.waitForIdle()
        swapchain.release()
        swapchain = Swapchain(this)
        screenRenderPass.onSwapchainRecreated()
    }

//    private inner class KoolVkScene: VkScene {
//        lateinit var sys: VkSystem
//        val cmdPools = mutableListOf<CommandPool>()
//        val cmdBuffers = mutableListOf<CommandBuffers>()
//
//        val meshMap = mutableMapOf<Long, IndexedMesh>()
//
//        inner class DelayAction(var delay: Int = sys.swapChain?.nImages ?: 3, val action: () -> Unit)
//        private val actionQueue = ArrayDeque<DelayAction>()
//
//        override fun onLoad(sys: VkSystem) {
//            this.sys = sys
//        }
//
//        override fun onSwapChainCreated(swapChain: SwapChain) {
//            cmdBuffers.forEach { it.destroy() }
//            cmdBuffers.clear()
//            cmdPools.forEach {
//                sys.logicalDevice.removeDependingResource(it)
//                it.destroy()
//            }
//            cmdPools.clear()
//
//            for (i in swapChain.images.indices) {
//                val pool = CommandPool(this@RenderBackendVk, logicalDevice.graphicsQueue)
//                cmdPools += pool
//                cmdBuffers += pool.createCommandBuffers(swapChain.images.size)
//            }
//        }
//
//        override fun onDestroy(sys: VkSystem) { }
//
//        override fun onDrawFrame(swapChain: SwapChain, imageIndex: Int, fence: LongBuffer, waitSema: LongBuffer, signalSema: LongBuffer) {
//            // todo: delete discarded pipelines, if there are any
//            //if (ctx.disposablePipelines.isNotEmpty()) {
//            //    disposePipelines()
//            //}
//
//            renderAll(swapChain, imageIndex, fence, waitSema[0], signalSema[0])
//
//            // perform post render actions, if there are any
//            if (actionQueue.isNotEmpty()) {
//                val delIt = actionQueue.iterator()
//                for (action in delIt) {
//                    if (action.delay-- <= 0) {
//                        //sys.device.removeDependingResource(delMesh.mesh)
//                        //delMesh.mesh.destroy()
//                        action.action()
//                        delIt.remove()
//                    }
//                }
//            }
//        }
//
//        private fun renderAll(swapChain: SwapChain, imageIndex: Int, fence: LongBuffer, waitSema: Long, signalSema: Long) {
//            /*
//             * From Vulkan Dos and don'ts:
//             * Aim for 15-30 command buffers and 5-10 vkQueueSubmit() calls per frame, batch VkSubmitInfo() to a
//             * single call as much as possible. Each vkQueueSubmit() has a performance cost on CPU, so lower is
//             * generally better. Note that VkSemaphore-based synchronization can only be done across vkQueueSubmit()
//             * calls, so you may be forced to split work up into multiple submits.
//             */
////            renderPassGraph.updateGraph(ctx)
////            semaPool.reclaimAll(imageIndex)
////
////            for (iGrp in renderPassGraph.groups.indices) {
////                val grp = renderPassGraph.groups[iGrp]
////                if (grp.isOnScreen) {
////                    grp.signalSemaphore = signalSema
////                } else {
////                    grp.signalSemaphore = semaPool.getSemaphore(imageIndex)
////                }
////            }
//
////            renderPassGraph.apply {
////                System.err.println("graph update: ${groups.size} groups, frame wait sema: $waitSema, frame signal sema: $signalSema")
////            }
//
//            memStack {
//                cmdBuffers[imageIndex].destroy()
//                val pool = cmdPools[imageIndex]
//                pool.reset()
//                val cmdBufs = pool.createCommandBuffers(renderPassGraph.requiredCommandBuffers)
//                cmdBuffers[imageIndex] = cmdBufs
//
//                for (iGrp in renderPassGraph.groups.indices) {
//                    val group = renderPassGraph.groups[iGrp]
//
//                    val submitCmdBufs = makeCommandBuffers(cmdBufs, group, swapChain, imageIndex)
//
//                    var nWaitSemas = group.dependencies.size
//                    if (group.isOnScreen) {
//                        nWaitSemas++
//                    }
//                    val waitSemas = if (nWaitSemas > 0) {
//                        val longs = mallocLong(nWaitSemas)
//                        group.dependencies.forEachIndexed { i, dep ->
//                            longs.put(i, dep.signalSemaphore)
//                        }
//                        if (group.isOnScreen) {
//                            longs.put(nWaitSemas - 1, waitSema)
//                        }
//                        longs
//                    } else {
//                        null
//                    }
//
////                    val deps = (0 until nWaitSemas).map { String.format("0x%x", waitSemas!!.get(it)) }.joinToString(", ")
////                    System.err.printf("  grp $iGrp [onScr: ${group.isOnScreen}]: ${group.renderPasses.size} passes, deps: [$deps], sig: 0x%x\n", group.signalSemaphore)
//
//                    val submitInfo = callocVkSubmitInfo {
//                        sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
//                        waitSemaphoreCount(nWaitSemas)
//                        pWaitSemaphores(waitSemas)
//                        pWaitDstStageMask(ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
//                        pCommandBuffers(submitCmdBufs)
//                        pSignalSemaphores(longs(group.signalSemaphore))
//                    }
//                    if (group.isOnScreen) {
//                        vkResetFences(sys.logicalDevice.vkDevice, fence)
//                        check(vkQueueSubmit(sys.logicalDevice.graphicsQueue, submitInfo, fence[0]) == VK_SUCCESS)
//                    } else {
//                        check(vkQueueSubmit(sys.logicalDevice.graphicsQueue, submitInfo, 0L) == VK_SUCCESS)
//                    }
//
//                    for (i in group.renderPasses.indices) {
//                        group.renderPasses[i].afterDraw()
//                    }
//                }
//            }
//        }
//
//        private fun MemoryStack.makeCommandBuffers(cmdBuffers: CommandBuffers, group: RenderPassGraph.RenderPassGroup, swapChain: SwapChain, imageIndex: Int): PointerBuffer {
//            val nCmdBufs = if (group.isOnScreen) { 1 } else { group.numRequiredCmdBuffers }
//            val submitCmdBufs = mallocPointer(nCmdBufs)
//
//            if (group.isOnScreen) {
//                val commandBuffer = cmdBuffers.nextCommandBuffer()
//                submitCmdBufs.put(0, commandBuffer)
//                makeCommandBufferOnScreen(commandBuffer, group, swapChain, imageIndex)
//
//            } else {
//                var iCmdBuffer = 0
//                for (iPass in 0 until group.renderPasses.size) {
//                    val offscreenPass = group.renderPasses[iPass] as OffscreenRenderPass
//                    if (offscreenPass is OffscreenRenderPass2dPingPong) {
//                        makeCommandBuffersOffScreenPingPong(cmdBuffers, submitCmdBufs, iCmdBuffer, offscreenPass)
//                        iCmdBuffer += offscreenPass.pingPongPasses
//
//                    } else {
//                        val commandBuffer = cmdBuffers.nextCommandBuffer()
//                        submitCmdBufs.put(iCmdBuffer++, commandBuffer)
//                        makeCommandBufferOffScreen(commandBuffer, offscreenPass)
//                    }
//                }
//            }
//            return submitCmdBufs
//        }
//
//        private fun MemoryStack.makeCommandBufferOnScreen(cmdBuffer: VkCommandBuffer, group: RenderPassGraph.RenderPassGroup, swapChain: SwapChain, imageIndex: Int) {
//            val mergeQueue = mutableListOf<DrawCommand>()
//
//            // fixme: this assumes all render passes / views use the same fullscreen viewport
//            val viewport = group.renderPasses[0].views[0].viewport
//
//            val beginInfo = callocVkCommandBufferBeginInfo { sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO) }
//            check(vkBeginCommandBuffer(cmdBuffer, beginInfo) == VK_SUCCESS)
//
//            // on screen render passes of all scenes are merged into a single command buffer
//            for (i in group.renderPasses.indices) {
//                val onScreenPass = group.renderPasses[i]
//                for (view in onScreenPass.views) {
//                    view.drawQueue.forEach { mergeQueue += it }
//                }
//            }
//
//            val renderPassInfo = renderPassBeginInfo(swapChain.renderPass, swapChain.framebuffers[imageIndex], group.renderPasses[0])
//            vkCmdBeginRenderPass(cmdBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
//            setViewport(cmdBuffer, viewport, true)
//            renderDrawQueue(cmdBuffer, mergeQueue, imageIndex, swapChain.renderPass, swapChain.nImages, false)
//            vkCmdEndRenderPass(cmdBuffer)
//
//            check(vkEndCommandBuffer(cmdBuffer) == VK_SUCCESS)
//        }
//
//        private fun MemoryStack.makeCommandBufferOffScreen(cmdBuffer: VkCommandBuffer, offscreenPass: OffscreenRenderPass) {
//            val beginInfo = callocVkCommandBufferBeginInfo { sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO) }
//            check(vkBeginCommandBuffer(cmdBuffer, beginInfo) == VK_SUCCESS)
//            when (offscreenPass) {
//                is OffscreenRenderPass2d -> renderOffscreen2d(cmdBuffer, offscreenPass)
//                is OffscreenRenderPassCube -> renderOffscreenCube(cmdBuffer, offscreenPass)
//                else -> throw IllegalArgumentException("Not implemented: ${offscreenPass::class.java}")
//            }
//            check(vkEndCommandBuffer(cmdBuffer) == VK_SUCCESS)
//        }
//
//        private fun MemoryStack.makeCommandBuffersOffScreenPingPong(cmdBuffers: CommandBuffers, submitCmdBufs: PointerBuffer, iCmdBuffer: Int, offscreenPass: OffscreenRenderPass2dPingPong) {
//            for (i in 0 until offscreenPass.pingPongPasses) {
//                val commandBuffer = cmdBuffers.nextCommandBuffer()
//                submitCmdBufs.put(iCmdBuffer + i, commandBuffer)
//
//                val beginInfo = callocVkCommandBufferBeginInfo { sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO) }
//                check(vkBeginCommandBuffer(commandBuffer, beginInfo) == VK_SUCCESS)
//                offscreenPass.onDrawPing?.invoke(i)
//                renderOffscreen2d(commandBuffer, offscreenPass.ping)
//                offscreenPass.onDrawPong?.invoke(i)
//                renderOffscreen2d(commandBuffer, offscreenPass.pong)
//                check(vkEndCommandBuffer(commandBuffer) == VK_SUCCESS)
//            }
//        }
//
//        private fun disposePipelines() {
//            TODO()
////            ctx.disposablePipelines.forEach { pipeline ->
////                val delMesh = meshMap.remove(pipeline.pipelineInstanceId)
////                val delPipeline = sys.pipelineManager.getPipeline(pipeline)
////
////                actionQueue += DelayAction {
////                    delMesh?.let {
////                        sys.device.removeDependingResource(it)
////                        it.destroy()
////                    }
////                    delPipeline?.freeDescriptorSetInstance(pipeline)
////                }
////            }
////            ctx.disposablePipelines.clear()
//        }
//
//        private fun PipelineBase.instanceId(mesh: Mesh): Long {
//            return pipelineHash.hash * mesh.id.value
//        }
//
//        private fun MemoryStack.renderDrawQueue(commandBuffer: VkCommandBuffer, drawQueue: List<DrawCommand>, imageIndex: Int,
//                                                renderPass: VkRenderPass, nImages: Int, dynVp: Boolean) {
//            var prevPipeline = 0L
//            drawQueue.forEach { cmd ->
//                val pipelineCfg = cmd.pipeline
//                if (!cmd.mesh.geometry.isEmpty()) {
//                    pipelineCfg.update(cmd)
//
//                    if (!sys.pipelineManager.hasPipeline(pipelineCfg, renderPass.vkRenderPass)) {
//                        sys.pipelineManager.addPipelineConfig(pipelineCfg, nImages, cmd.queue.renderPass, renderPass, dynVp)
//                    }
//                    val pipeline = sys.pipelineManager.getPipeline(pipelineCfg, renderPass.vkRenderPass)
//                    if (pipelineCfg.pipelineHash.hash != prevPipeline) {
//                        vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.vkGraphicsPipeline)
//                        prevPipeline = pipelineCfg.pipelineHash.hash
//                    }
//                    val descriptorSet = pipeline.getDescriptorSetInstance(pipelineCfg)
//
//                    if (descriptorSet.updateDescriptors(cmd, imageIndex, sys)) {
//                        descriptorSet.updateDescriptorSets(imageIndex, cmd)
//
//                        var model = meshMap[pipelineCfg.instanceId(cmd.mesh)]
//                        if ((cmd.mesh.geometry.hasChanged && !cmd.mesh.geometry.isBatchUpdate) || model == null) {
//                            // fixme: currently there is one IndexedMesh per pipeline, not per mesh
//                            // if mesh is rendered multiple times (e.g. by additional shadow passes), clearing
//                            // hasChanged flag early results in IndexedMeshes not being updated
//                            actionQueue += DelayAction(0) { cmd.mesh.geometry.hasChanged = false }
//
//                            // (re-)build buffer
//                            // fixme: don't do this here, should have happened before (async?)
//                            meshMap.remove(pipelineCfg.instanceId(cmd.mesh))?.let {
//                                actionQueue += DelayAction {
//                                    sys.logicalDevice.removeDependingResource(it)
//                                    it.destroy()
//                                }
//                            }
//                            model = IndexedMesh(sys, cmd.mesh)
//                            meshMap[pipelineCfg.instanceId(cmd.mesh)] = model
//                            sys.logicalDevice.addDependingResource(model)
//                        }
//
//                        if (cmd.mesh.instances?.hasChanged == true) {
//                            model.updateInstanceBuffer()
//                            actionQueue += DelayAction(0) { cmd.mesh.instances?.hasChanged = false }
//                        }
//
////                        var offset = 0
////                        pipelineCfg.layout.pushConstantRanges.forEach {
////                            it.onUpdate?.invoke(it, cmd)
////                            val flags = it.stages.fold(0) { f, stage -> f or stage.bitValue() }
////                            it.toBuffer().useRaw { buf ->
////                                vkCmdPushConstants(commandBuffer, pipeline.pipelineLayout, flags, offset, buf)
////                            }
////                            offset += it.size
////                        }
//
//                        val instanceCnt: Int
//                        val insts = cmd.mesh.instances
//                        val instData = model.instanceBuffer
//                        val intData = model.vertexBufferI?.vkBuffer
//
//                        val pBuffers: LongBuffer
//                        val pOffsets: LongBuffer
//                        if (insts != null && instData != null) {
//                            instanceCnt = insts.numInstances
//                            if (intData != null) {
//                                pBuffers = longs(model.vertexBuffer.vkBuffer, intData, instData.buffer.vkBuffer)
//                                pOffsets = longs(0L, 0L, 0L)
//                            } else {
//                                pBuffers = longs(model.vertexBuffer.vkBuffer, instData.buffer.vkBuffer)
//                                pOffsets = longs(0L, 0L)
//                            }
//
//                        } else {
//                            instanceCnt = 1
//                            if (intData != null) {
//                                pBuffers = longs(model.vertexBuffer.vkBuffer, intData)
//                                pOffsets = longs(0L, 0L)
//                            } else {
//                                pBuffers = longs(model.vertexBuffer.vkBuffer)
//                                pOffsets = longs(0L)
//                            }
//                        }
//
//                        if (instanceCnt > 0) {
//                            vkCmdBindVertexBuffers(commandBuffer, 0, pBuffers, pOffsets)
//                            vkCmdBindIndexBuffer(commandBuffer, model.indexBuffer.vkBuffer, 0L, VK_INDEX_TYPE_UINT32)
//                            val descriptorSetPtr = descriptorSet.getDescriptorSet(imageIndex)
//                            if (descriptorSetPtr != 0L) {
//                                vkCmdBindDescriptorSets(
//                                    commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
//                                    pipeline.pipelineLayout, 0, longs(descriptorSetPtr), null
//                                )
//                            }
//                            vkCmdDrawIndexed(commandBuffer, model.numIndices, instanceCnt, 0, 0, 0)
//
//                            BackendStats.addDrawCommands(1, cmd.mesh.geometry.numPrimitives * instanceCnt)
//                        }
//                    }
//                }
//            }
//        }
//
//        private fun MemoryStack.setViewport(commandBuffer: VkCommandBuffer, viewport: Viewport, isOnscreen: Boolean) {
//            val vkViewport = callocVkViewportN(1) {
//                x(viewport.x.toFloat())
//                width(viewport.width.toFloat())
//
//                if (isOnscreen) {
//                    y(viewport.height.toFloat() + viewport.y.toFloat())
//                    height(-viewport.height.toFloat())
//                } else {
//                    y(viewport.y.toFloat())
//                    height(viewport.height.toFloat())
//                }
//
//                minDepth(0f)
//                maxDepth(1f)
//            }
//            vkCmdSetViewport(commandBuffer, 0, vkViewport)
//
//            val scissor = callocVkRect2DN(1) {
//                offset { it.set(viewport.x, viewport.y) }
//                extent { it.width(viewport.width); it.height(viewport.height) }
//            }
//            vkCmdSetScissor(commandBuffer, 0, scissor)
//        }
//
//        private fun MemoryStack.renderOffscreen2d(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenRenderPass2d) {
//            val vkPass2d = offscreenPass.impl as VkOffscreenPass2d
//
//            // fixme: blitting render passes does not work yet
//            //offscreenPass.blitRenderPass?.let {
//            //    for (mipLevel in 0 until vkPass2d.renderMipLevels) {
//            //        val srcPass2d = it.impl as VkOffscreenPass2d
//            //        vkPass2d.blitFrom(srcPass2d, commandBuffer, mipLevel)
//            //    }
//            //}
//
//            offscreenPass.impl.draw()
//            vkPass2d.renderPass?.let { rp ->
//                val renderPassInfo = renderPassBeginInfo(rp, rp.frameBuffer, offscreenPass)
//
//                vkPass2d.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
//                val mipLevels = offscreenPass.numRenderMipLevels
//                for (mipLevel in 0 until vkPass2d.parentPass.numRenderMipLevels) {
//                    offscreenPass.setupMipLevel(mipLevel)
//                    vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
//
//                    for (view in offscreenPass.views) {
//                        view.viewport.set(0, 0, offscreenPass.width shr mipLevel, offscreenPass.height shr mipLevel)
//                        setViewport(commandBuffer, view.viewport, false)
//
//                        val commands = mutableListOf<DrawCommand>()
//                        view.drawQueue.forEach { commands += it }
//                        renderDrawQueue(commandBuffer, commands, mipLevel, rp, mipLevels, true)
//                    }
//
//                    vkCmdEndRenderPass(commandBuffer)
//                    vkPass2d.copyMipView(commandBuffer, mipLevel)
//                }
//                vkPass2d.generateMipLevels(commandBuffer)
//                vkPass2d.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
//                vkPass2d.copyToTextures(commandBuffer, ctx)
//            }
//        }
//
//        private fun MemoryStack.renderOffscreenCube(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenRenderPassCube) {
//            val vkPassCube = offscreenPass.impl as VkOffscreenPassCube
//            vkPassCube.draw()
//            vkPassCube.renderPass?.let { rp ->
//                val renderPassInfo = renderPassBeginInfo(rp, rp.frameBuffer, offscreenPass)
//
//                vkPassCube.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
//                val mipLevels = offscreenPass.numRenderMipLevels
//                for (mipLevel in 0 until mipLevels) {
//                    offscreenPass.setupMipLevel(mipLevel)
//                    //offscreenPass.applyMipViewport(mipLevel)
//                    for (cubeView in cubeRenderPassViews) {
//                        val imageI = mipLevel * 6 + cubeView.index
//                        val view = offscreenPass.views[cubeView.index]
//                        view.viewport.set(0, 0, offscreenPass.width shr mipLevel, offscreenPass.height shr mipLevel)
//
//                        vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
//                        setViewport(commandBuffer, view.viewport, false)
//
//                        val commands = mutableListOf<DrawCommand>()
//                        view.drawQueue.forEach { commands += it }
//                        renderDrawQueue(commandBuffer, commands, imageI, rp, 6 * mipLevels, true)
//                        vkCmdEndRenderPass(commandBuffer)
//                        vkPassCube.copyView(commandBuffer, cubeView, mipLevel)
//                    }
//                }
//                vkPassCube.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
//            }
//            vkPassCube.copyToTextures(commandBuffer, ctx)
//        }
//    }

//    private fun MemoryStack.renderPassBeginInfo(vkRenderPass: VkRenderPass, frameBuffer: Long, renderPass: RenderPass) =
//            callocVkRenderPassBeginInfo {
//                sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
//                renderPass(vkRenderPass.vkRenderPass)
//                framebuffer(frameBuffer)
//                renderArea { r ->
//                    r.offset { it.x(0); it.y(0) }
//                    r.extent { it.width(vkRenderPass.maxWidth); it.height(vkRenderPass.maxHeight) }
//                }
//
//                // fixme: make clear values optional (if clear color is null or clearDepth = false)
//                val clearColors = renderPass.clearColors
//                val colorAttachments = max(1, clearColors.size)
//                pClearValues(callocVkClearValueN(colorAttachments + 1) {
//                    for (i in 0 until colorAttachments) {
//                        val clearColor = if (i < clearColors.size) {
//                            clearColors[i] ?: Color.BLACK
//                        } else {
//                            Color.BLACK
//                        }
//                        this[i].setColor(clearColor)
//                    }
//                    this[colorAttachments].depthStencil {
//                        if (renderPass.isReverseDepth) {
//                            it.depth(0f)
//                        } else {
//                            it.depth(1f)
//                        }
//                        it.stencil(0)
//                    }
//                })
//            }

//    private class SemaphorePool(val sys: VkSystem) : VkResource() {
//        private val pools = Array(sys.swapChain?.nImages ?: 3) { mutableListOf<Long>() }
//        private val available = Array(sys.swapChain?.nImages ?: 3) { mutableListOf<Long>() }
//
//        fun reclaimAll(imageIndex: Int) {
//            available[imageIndex].addAll(pools[imageIndex])
//        }
//
//        fun getSemaphore(imageIndex: Int): Long {
//            val sema: Long
//            if (available[imageIndex].isNotEmpty()) {
//                sema = available[imageIndex].removeAt(available[imageIndex].lastIndex)
//            } else {
//                memStack {
//                    val semaphoreInfo = callocVkSemaphoreCreateInfo { sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO) }
//                    sema = checkCreateLongPtr { vkCreateSemaphore(sys.logicalDevice.vkDevice, semaphoreInfo, null, it) }
//                    pools[imageIndex].add(sema)
//                }
//            }
//            return sema
//        }
//
//        override fun freeResources() {
//            pools.forEach { pool ->
//                pool.forEach { sema ->
//                    vkDestroySemaphore(sys.logicalDevice.vkDevice, sema, null)
//                }
//                pool.clear()
//            }
//            available.forEach { it.clear() }
//        }
//
//    }

//    companion object {
//        private val cubeRenderPassViews = Array(6) {
//            i -> OffscreenRenderPassCube.ViewDirection.entries[i]
//        }
//    }
}