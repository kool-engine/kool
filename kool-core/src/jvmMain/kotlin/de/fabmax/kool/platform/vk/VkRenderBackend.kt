package de.fabmax.kool.platform.vk

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Vec4d
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.platform.GlfwWindow
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.RenderBackend
import de.fabmax.kool.platform.vk.util.bitValue
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MixedBufferImpl
import de.fabmax.kool.util.Viewport
import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkClearValue
import org.lwjgl.vulkan.VkCommandBuffer
import java.nio.LongBuffer
import java.util.*
import kotlin.math.max

class VkRenderBackend(val ctx: Lwjgl3Context) : RenderBackend {
    override val apiName: String
    override val deviceName: String

    override val glfwWindow: GlfwWindow
        get() = vkSystem.window

    override val projCorrectionMatrixScreen = Mat4d()
    override val projCorrectionMatrixOffscreen = Mat4d()
    override val depthBiasMatrix = Mat4d()

    private val shaderCodes = mutableMapOf<String, ShaderCode>()

    val vkSystem: VkSystem
    private val vkScene = KoolVkScene()

    private val semaPool: SemaphorePool
    private val renderPassGraph = RenderPassGraph()

    init {
        val vkSetup = VkSetup().apply {
            isValidating = true
        }
        vkSystem = VkSystem(vkSetup, vkScene, ctx)
        semaPool = SemaphorePool(vkSystem)
        vkSystem.addDependingResource(semaPool)
        apiName = "Vulkan ${vkSystem.physicalDevice.apiVersion}"
        deviceName = vkSystem.physicalDevice.deviceName

        // maps camera projection matrices to Vulkan screen coordinates
        projCorrectionMatrixScreen.apply {
            setRow(0, Vec4d(1.0, 0.0, 0.0, 0.0))
            setRow(1, Vec4d(0.0, 1.0, 0.0, 0.0))
            setRow(2, Vec4d(0.0, 0.0, 0.5, 0.5))
            setRow(3, Vec4d(0.0, 0.0, 0.0, 1.0))
        }

        projCorrectionMatrixOffscreen.apply {
            setRow(0, Vec4d(1.0, 0.0, 0.0, 0.0))
            setRow(1, Vec4d(0.0, 1.0, 0.0, 0.0))
            setRow(2, Vec4d(0.0, 0.0, 0.5, 0.5))
            setRow(3, Vec4d(0.0, 0.0, 0.0, 1.0))
        }

        depthBiasMatrix.apply {
            setRow(0, Vec4d(0.5, 0.0, 0.0, 0.5))
            setRow(1, Vec4d(0.0, 0.5, 0.0, 0.5))
            setRow(2, Vec4d(0.0, 0.0, 1.0, 0.0))
            setRow(3, Vec4d(0.0, 0.0, 0.0, 1.0))
        }
    }

    override fun getWindowViewport(result: Viewport) {
        result.set(0, glfwWindow.framebufferHeight, glfwWindow.framebufferWidth, -glfwWindow.framebufferHeight)
    }

    override fun uploadTextureToGpu(tex: Texture, data: TextureData) {
        tex.loadedTexture = when (tex) {
            is Texture1d -> TextureLoader.loadTexture1d(vkSystem, tex.props, data)
            is Texture2d -> TextureLoader.loadTexture2d(vkSystem, tex.props, data)
            is Texture3d -> TextureLoader.loadTexture3d(vkSystem, tex.props, data)
            is TextureCube -> TextureLoader.loadTextureCube(vkSystem, tex.props, data)
            else -> throw IllegalArgumentException("Unsupported texture type: $tex")
        }
        tex.loadingState = Texture.LoadingState.LOADED
        vkSystem.device.addDependingResource(tex.loadedTexture as LoadedTextureVk)
    }

    override fun createOffscreenPass2d(parentPass: OffscreenPass2dImpl): OffscreenPass2dImpl.BackendImpl {
        return VkOffscreenPass2d(parentPass)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenPassCubeImpl): OffscreenPassCubeImpl.BackendImpl {
        return VkOffscreenPassCube(parentPass)
    }

    override fun generateKslShader(shader: KslShader, pipelineLayout: Pipeline.Layout): ShaderCode {
        val src = KslGlslGeneratorVk(pipelineLayout).generateProgram(shader.program)
        if (shader.program.dumpCode) {
            src.dump()
        }
        val codeKey = src.vertexSrc + src.fragmentSrc
        return shaderCodes.getOrPut(codeKey) {
            ShaderCode.vkCodeFromSource(src.vertexSrc, src.fragmentSrc)
        }
    }

    override fun drawFrame(ctx: Lwjgl3Context) {
        vkSystem.renderLoop.drawFrame()
    }

    override fun close(ctx: Lwjgl3Context) {
        glfwSetWindowShouldClose(vkSystem.window.windowPtr, true)
    }

    override fun cleanup(ctx: Lwjgl3Context) {
        vkDeviceWaitIdle(vkSystem.device.vkDevice)
        vkSystem.destroy()
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

        override fun onDestroy(sys: VkSystem) { }

        override fun onDrawFrame(swapChain: SwapChain, imageIndex: Int, fence: LongBuffer, waitSema: LongBuffer, signalSema: LongBuffer) {
            // delete discarded pipelines, if there are any
            if (ctx.disposablePipelines.isNotEmpty()) {
                disposePipelines()
            }

            renderAll(swapChain, imageIndex, fence, waitSema[0], signalSema[0])

            // perform post render actions, if there are any
            if (actionQueue.isNotEmpty()) {
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
        }

        private fun renderAll(swapChain: SwapChain, imageIndex: Int, fence: LongBuffer, waitSema: Long, signalSema: Long) {
            /*
             * From Vulkan Dos and don'ts:
             * Aim for 15-30 command buffers and 5-10 vkQueueSubmit() calls per frame, batch VkSubmitInfo() to a
             * single call as much as possible. Each vkQueueSubmit() has a performance cost on CPU, so lower is
             * generally better. Note that VkSemaphore-based synchronization can only be done across vkQueueSubmit()
             * calls, so you may be forced to split work up into multiple submits.
             */
            renderPassGraph.updateGraph(ctx)
            semaPool.reclaimAll(imageIndex)

            for (iGrp in renderPassGraph.groups.indices) {
                val grp = renderPassGraph.groups[iGrp]
                if (grp.isOnScreen) {
                    grp.signalSemaphore = signalSema
                } else {
                    grp.signalSemaphore = semaPool.getSemaphore(imageIndex)
                }
            }

//            renderPassGraph.apply {
//                System.err.println("graph update: ${groups.size} groups, frame wait sema: $waitSema, frame signal sema: $signalSema")
//            }

            memStack {
                cmdBuffers[imageIndex].destroy()
                val pool = cmdPools[imageIndex]
                pool.reset()
                val cmdBufs = pool.createCommandBuffers(renderPassGraph.requiredCommandBuffers)
                cmdBuffers[imageIndex] = cmdBufs

                for (iGrp in renderPassGraph.groups.indices) {
                    val group = renderPassGraph.groups[iGrp]

                    val submitCmdBufs = makeCommandBuffers(cmdBufs, group, swapChain, imageIndex)

                    var nWaitSemas = group.dependencies.size
                    if (group.isOnScreen) {
                        nWaitSemas++
                    }
                    val waitSemas = if (nWaitSemas > 0) {
                        val longs = mallocLong(nWaitSemas)
                        group.dependencies.forEachIndexed { i, dep ->
                            longs.put(i, dep.signalSemaphore)
                        }
                        if (group.isOnScreen) {
                            longs.put(nWaitSemas - 1, waitSema)
                        }
                        longs
                    } else {
                        null
                    }

//                    val deps = (0 until nWaitSemas).map { String.format("0x%x", waitSemas!!.get(it)) }.joinToString(", ")
//                    System.err.printf("  grp $iGrp [onScr: ${group.isOnScreen}]: ${group.renderPasses.size} passes, deps: [$deps], sig: 0x%x\n", group.signalSemaphore)

                    val submitInfo = callocVkSubmitInfo {
                        sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                        waitSemaphoreCount(nWaitSemas)
                        pWaitSemaphores(waitSemas)
                        pWaitDstStageMask(ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
                        pCommandBuffers(submitCmdBufs)
                        pSignalSemaphores(longs(group.signalSemaphore))
                    }
                    if (group.isOnScreen) {
                        vkResetFences(sys.device.vkDevice, fence)
                        check(vkQueueSubmit(sys.device.graphicsQueue, submitInfo, fence[0]) == VK_SUCCESS)
                    } else {
                        check(vkQueueSubmit(sys.device.graphicsQueue, submitInfo, 0L) == VK_SUCCESS)
                    }

                    for (i in group.renderPasses.indices) {
                        group.renderPasses[i].afterDraw(ctx)
                    }
                }
            }
        }

        private fun MemoryStack.makeCommandBuffers(cmdBuffers: CommandBuffers, group: RenderPassGraph.RenderPassGroup, swapChain: SwapChain, imageIndex: Int): PointerBuffer {
            val nCmdBufs = if (group.isOnScreen) { 1 } else { group.numRequiredCmdBuffers }
            val submitCmdBufs = mallocPointer(nCmdBufs)

            if (group.isOnScreen) {
                val commandBuffer = cmdBuffers.nextCommandBuffer()
                submitCmdBufs.put(0, commandBuffer)
                makeCommandBufferOnScreen(commandBuffer, group, swapChain, imageIndex)

            } else {
                var iCmdBuffer = 0
                for (iPass in 0 until group.renderPasses.size) {
                    val offscreenPass = group.renderPasses[iPass] as OffscreenRenderPass
                    if (offscreenPass is OffscreenRenderPass2dPingPong) {
                        makeCommandBuffersOffScreenPingPong(cmdBuffers, submitCmdBufs, iCmdBuffer, offscreenPass)
                        iCmdBuffer += offscreenPass.pingPongPasses

                    } else {
                        val commandBuffer = cmdBuffers.nextCommandBuffer()
                        submitCmdBufs.put(iCmdBuffer++, commandBuffer)
                        makeCommandBufferOffScreen(commandBuffer, offscreenPass)
                    }
                }
            }
            return submitCmdBufs
        }

        private fun MemoryStack.makeCommandBufferOnScreen(cmdBuffer: VkCommandBuffer, group: RenderPassGraph.RenderPassGroup, swapChain: SwapChain, imageIndex: Int) {
            val mergeQueue = mutableListOf<DrawCommand>()
            val viewport = group.renderPasses[0].viewport

            // on screen render passes of all scenes are merged into a single command buffer
            for (i in group.renderPasses.indices) {
                val onScreenPass = group.renderPasses[i]
                mergeQueue += onScreenPass.drawQueue.commands
            }

            // fixme: optimize draw queue order (sort by distance, customizable draw order, etc.)

            val beginInfo = callocVkCommandBufferBeginInfo { sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO) }
            check(vkBeginCommandBuffer(cmdBuffer, beginInfo) == VK_SUCCESS)

            val renderPassInfo = renderPassBeginInfo(swapChain.renderPass, swapChain.framebuffers[imageIndex], group.renderPasses[0])
            vkCmdBeginRenderPass(cmdBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
            setViewport(cmdBuffer, viewport)
            renderDrawQueue(cmdBuffer, mergeQueue, imageIndex, swapChain.renderPass, swapChain.nImages, false)
            vkCmdEndRenderPass(cmdBuffer)

            check(vkEndCommandBuffer(cmdBuffer) == VK_SUCCESS)
        }

        private fun MemoryStack.makeCommandBufferOffScreen(cmdBuffer: VkCommandBuffer, offscreenPass: OffscreenRenderPass) {
            val beginInfo = callocVkCommandBufferBeginInfo { sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO) }
            check(vkBeginCommandBuffer(cmdBuffer, beginInfo) == VK_SUCCESS)
            when (offscreenPass) {
                is OffscreenRenderPass2d -> renderOffscreen2d(cmdBuffer, offscreenPass)
                is OffscreenRenderPassCube -> renderOffscreenCube(cmdBuffer, offscreenPass)
                else -> throw IllegalArgumentException("Not implemented: ${offscreenPass::class.java}")
            }
            check(vkEndCommandBuffer(cmdBuffer) == VK_SUCCESS)
        }

        private fun MemoryStack.makeCommandBuffersOffScreenPingPong(cmdBuffers: CommandBuffers, submitCmdBufs: PointerBuffer, iCmdBuffer: Int, offscreenPass: OffscreenRenderPass2dPingPong) {
            for (i in 0 until offscreenPass.pingPongPasses) {
                val commandBuffer = cmdBuffers.nextCommandBuffer()
                submitCmdBufs.put(iCmdBuffer + i, commandBuffer)

                val beginInfo = callocVkCommandBufferBeginInfo { sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO) }
                check(vkBeginCommandBuffer(commandBuffer, beginInfo) == VK_SUCCESS)
                offscreenPass.onDrawPing?.invoke(i)
                renderOffscreen2d(commandBuffer, offscreenPass.ping)
                offscreenPass.onDrawPong?.invoke(i)
                renderOffscreen2d(commandBuffer, offscreenPass.pong)
                check(vkEndCommandBuffer(commandBuffer) == VK_SUCCESS)
            }
        }

        private fun disposePipelines() {
            ctx.disposablePipelines.forEach { pipeline ->
                val delMesh = meshMap.remove(pipeline.pipelineInstanceId)
                val delPipeline = sys.pipelineManager.getPipeline(pipeline)

                actionQueue += DelayAction {
                    delMesh?.let {
                        sys.device.removeDependingResource(it)
                        it.destroy()
                    }
                    delPipeline?.freeDescriptorSetInstance(pipeline)
                }
            }
            ctx.disposablePipelines.clear()
        }

        private fun MemoryStack.renderDrawQueue(commandBuffer: VkCommandBuffer, drawQueue: List<DrawCommand>, imageIndex: Int,
                                                renderPass: VkRenderPass, nImages: Int, dynVp: Boolean) {
            var prevPipeline = 0UL
            drawQueue.forEach { cmd ->
                val pipelineCfg = cmd.pipeline
                if (!cmd.mesh.geometry.isEmpty() && pipelineCfg != null) {
                    pipelineCfg.onUpdate.forEach { it(cmd) }

                    if (!sys.pipelineManager.hasPipeline(pipelineCfg, renderPass.vkRenderPass)) {
                        sys.pipelineManager.addPipelineConfig(pipelineCfg, nImages, cmd.renderPass, renderPass, dynVp)
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
                            model = IndexedMesh(sys, cmd.mesh)
                            meshMap[pipelineCfg.pipelineInstanceId] = model
                            sys.device.addDependingResource(model)
                        }

                        if (cmd.mesh.instances?.hasChanged == true) {
                            model.updateInstanceBuffer()
                            actionQueue += DelayAction(0) { cmd.mesh.instances?.hasChanged = false }
                        }

                        var offset = 0
                        pipelineCfg.layout.pushConstantRanges.forEach {
                            it.onUpdate?.invoke(it, cmd)
                            val flags = it.stages.fold(0) { f, stage -> f or stage.bitValue() }
                            val pushBuffer = (it.toBuffer() as MixedBufferImpl).buffer
                            vkCmdPushConstants(commandBuffer, pipeline.pipelineLayout, flags, offset, pushBuffer)
                            offset += it.size
                        }

                        val instanceCnt: Int
                        val insts = cmd.mesh.instances
                        val instData = model.instanceBuffer
                        val intData = model.vertexBufferI?.vkBuffer

                        val pBuffers: LongBuffer
                        val pOffsets: LongBuffer
                        if (insts != null && instData != null) {
                            instanceCnt = insts.numInstances
                            if (intData != null) {
                                pBuffers = longs(model.vertexBuffer.vkBuffer, intData, instData.buffer.vkBuffer)
                                pOffsets = longs(0L, 0L, 0L)
                            } else {
                                pBuffers = longs(model.vertexBuffer.vkBuffer, instData.buffer.vkBuffer)
                                pOffsets = longs(0L, 0L)
                            }

                        } else {
                            instanceCnt = 1
                            if (intData != null) {
                                pBuffers = longs(model.vertexBuffer.vkBuffer, intData)
                                pOffsets = longs(0L, 0L)
                            } else {
                                pBuffers = longs(model.vertexBuffer.vkBuffer)
                                pOffsets = longs(0L)
                            }
                        }

                        if (instanceCnt > 0) {
                            vkCmdBindVertexBuffers(commandBuffer, 0, pBuffers, pOffsets)
                            vkCmdBindIndexBuffer(commandBuffer, model.indexBuffer.vkBuffer, 0L, VK_INDEX_TYPE_UINT32)
                            val descriptorSetPtr = descriptorSet.getDescriptorSet(imageIndex)
                            if (descriptorSetPtr != 0L) {
                                vkCmdBindDescriptorSets(
                                    commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                                    pipeline.pipelineLayout, 0, longs(descriptorSetPtr), null
                                )
                            }
                            vkCmdDrawIndexed(commandBuffer, model.numIndices, instanceCnt, 0, 0, 0)

                            ctx.engineStats.addDrawCommandCount(1)
                            ctx.engineStats.addPrimitiveCount(cmd.mesh.geometry.numPrimitives * instanceCnt)
                        }
                    }
                }
            }
        }

        private fun MemoryStack.setViewport(commandBuffer: VkCommandBuffer, viewport: Viewport) {
            val vkViewport = callocVkViewportN(1) {
                x(viewport.x.toFloat())
                y(viewport.ySigned.toFloat())
                width(viewport.width.toFloat())
                height(viewport.heightSigned.toFloat())
                minDepth(0f)
                maxDepth(1f)
            }
            vkCmdSetViewport(commandBuffer, 0, vkViewport)

            val scissor = callocVkRect2DN(1) {
                offset { it.set(viewport.x, viewport.y) }
                extent { it.width(viewport.width); it.height(viewport.height) }
            }
            vkCmdSetScissor(commandBuffer, 0, scissor)
        }

        private fun MemoryStack.renderOffscreen2d(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenRenderPass2d) {
            offscreenPass.impl.draw(ctx)
            val vkPass2d = offscreenPass.impl.backendImpl as VkOffscreenPass2d
            vkPass2d.renderPass?.let { rp ->
                val renderPassInfo = renderPassBeginInfo(rp, rp.frameBuffer, offscreenPass)

                vkPass2d.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
                for (mipLevel in 0 until vkPass2d.renderMipLevels) {
                    offscreenPass.onSetupMipLevel?.invoke(mipLevel, ctx)
                    offscreenPass.applyMipViewport(mipLevel)
                    vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
                    setViewport(commandBuffer, offscreenPass.viewport)
                    renderDrawQueue(commandBuffer, offscreenPass.drawQueue.commands, 0, rp, 1, true)
                    vkCmdEndRenderPass(commandBuffer)
                    vkPass2d.copyMipView(commandBuffer, mipLevel)
                }
                vkPass2d.generateMipLevels(commandBuffer)
                vkPass2d.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                vkPass2d.copyToTextures(commandBuffer, ctx)
            }
        }

        private fun MemoryStack.renderOffscreenCube(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenRenderPassCube) {
            offscreenPass.impl.draw(ctx)
            val vkPassCube = offscreenPass.impl.backendImpl as VkOffscreenPassCube
            vkPassCube.renderPass?.let { rp ->
                val renderPassInfo = renderPassBeginInfo(rp, rp.frameBuffer, offscreenPass)

                vkPassCube.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
                for (mipLevel in 0 until offscreenPass.config.mipLevels) {
                    offscreenPass.onSetupMipLevel?.invoke(mipLevel, ctx)
                    offscreenPass.applyMipViewport(mipLevel)
                    for (view in cubeRenderPassViews) {
                        vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
                        setViewport(commandBuffer, offscreenPass.viewport)
                        renderDrawQueue(commandBuffer, offscreenPass.drawQueues[view.index].commands, view.index, rp, 6, true)
                        vkCmdEndRenderPass(commandBuffer)
                        vkPassCube.copyView(commandBuffer, view, mipLevel)
                    }
                }
                vkPassCube.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
            }
            vkPassCube.copyToTextures(commandBuffer, ctx)
        }
    }

    private fun MemoryStack.renderPassBeginInfo(vkRenderPass: VkRenderPass, frameBuffer: Long, renderPass: RenderPass) =
            callocVkRenderPassBeginInfo {
                sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                renderPass(vkRenderPass.vkRenderPass)
                framebuffer(frameBuffer)
                renderArea { r ->
                    r.offset { it.x(0); it.y(0) }
                    r.extent { it.width(vkRenderPass.maxWidth); it.height(vkRenderPass.maxHeight) }
                }

                // fixme: make clear values optional (if clear color is null or clearDepth = false)
                val colorAttachments = max(1, renderPass.clearColors.size)
                pClearValues(callocVkClearValueN(colorAttachments + 1) {
                    for (i in 0 until colorAttachments) {
                        val clearColor = if (i < renderPass.clearColors.size) {
                            renderPass.clearColors[i] ?: Color.BLACK
                        } else {
                            Color.BLACK
                        }
                        this[i].setColor(clearColor)
                    }
                    this[colorAttachments].depthStencil { it.depth(1f); it.stencil(0) }
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

    private class SemaphorePool(val sys: VkSystem) : VkResource() {
        private val pools = Array(sys.swapChain?.nImages ?: 3) { mutableListOf<Long>() }
        private val available = Array(sys.swapChain?.nImages ?: 3) { mutableListOf<Long>() }

        fun reclaimAll(imageIndex: Int) {
            available[imageIndex].addAll(pools[imageIndex])
        }

        fun getSemaphore(imageIndex: Int): Long {
            val sema: Long
            if (available[imageIndex].isNotEmpty()) {
                sema = available[imageIndex].removeAt(available[imageIndex].lastIndex)
            } else {
                memStack {
                    val semaphoreInfo = callocVkSemaphoreCreateInfo { sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO) }
                    sema = checkCreatePointer { vkCreateSemaphore(sys.device.vkDevice, semaphoreInfo, null, it) }
                    pools[imageIndex].add(sema)
                }
            }
            return sema
        }

        override fun freeResources() {
            pools.forEach { pool ->
                pool.forEach { sema ->
                    vkDestroySemaphore(sys.device.vkDevice, sema, null)
                }
                pool.clear()
            }
            available.forEach { it.clear() }
        }

    }

    companion object {
        // fixme: for some reason (timing / sync) last view is not copied sometimes? super duper fix: render last view twice
        private val cubeRenderPassViews = Array(7) {
            i -> OffscreenRenderPassCube.ViewDirection.values()[i % OffscreenRenderPassCube.ViewDirection.values().size]
        }
    }
}