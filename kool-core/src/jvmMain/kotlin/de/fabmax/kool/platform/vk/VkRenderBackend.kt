package de.fabmax.kool.platform.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Vec4d
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.RenderBackend
import de.fabmax.kool.platform.vk.util.bitValue
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MixedBufferImpl
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkClearValue
import org.lwjgl.vulkan.VkCommandBuffer
import java.util.*

class VkRenderBackend(props: Lwjgl3Context.InitProps, val ctx: Lwjgl3Context) : RenderBackend {
    override val apiName: String
    override val deviceName: String

    override var windowWidth = 0
        private set
    override var windowHeight = 0
        private set
    override var windowViewport = KoolContext.Viewport(0, 0, 0, 0)
        private set
    override val glfwWindowHandle: Long

    override val projCorrectionMatrixScreen = Mat4d()
    override val projCorrectionMatrixOffscreen = Mat4d()
    override val depthBiasMatrix = Mat4d()

    override val shaderGenerator = ShaderGeneratorImplVk()

    val vkSystem: VkSystem
    private val vkScene = KoolVkScene()

    init {
        windowWidth = props.width
        windowHeight = props.height
        windowViewport = KoolContext.Viewport(0, 0, windowWidth, windowHeight)

        val vkSetup = VkSetup().apply {
            isValidating = true
        }
        vkSystem = VkSystem(props, vkSetup, vkScene, this, ctx)
        apiName = "Vulkan ${vkSystem.physicalDevice.apiVersion}"
        deviceName = vkSystem.physicalDevice.deviceName

        glfwWindowHandle = vkSystem.window.glfwWindow

        vkSystem.window.onResize += object : GlfwWindow.OnWindowResizeListener {
            override fun onResize(window: GlfwWindow, newWidth: Int, newHeight: Int) {
                windowWidth = newWidth
                windowHeight = newHeight
                windowViewport = KoolContext.Viewport(0, 0, windowWidth, windowHeight)
            }
        }

        // maps camera projection matrices to Vulkan screen coordinates
        projCorrectionMatrixScreen.apply {
            setRow(0, Vec4d(1.0, 0.0, 0.0, 0.0))
            setRow(1, Vec4d(0.0, -1.0, 0.0, 0.0))
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

    override fun loadTex2d(tex: Texture, data: BufferedTextureData, recv: (Texture) -> Unit) {
        ctx.runOnMainThread {
            tex.loadedTexture = TextureLoader.loadTexture(vkSystem, tex.props, data)
            tex.loadingState = Texture.LoadingState.LOADED
            vkSystem.device.addDependingResource(tex.loadedTexture as LoadedTextureVk)
            recv(tex)
        }
    }

    override fun loadTexCube(tex: CubeMapTexture, data: CubeMapTextureData, recv: (CubeMapTexture) -> Unit) {
        ctx.runOnMainThread {
            tex.loadedTexture = TextureLoader.loadCubeMap(vkSystem, tex.props, data)
            tex.loadingState = Texture.LoadingState.LOADED
            vkSystem.device.addDependingResource(tex.loadedTexture as LoadedTextureVk)
            recv(tex)
        }
    }

    override fun createOffscreenPass2d(parentPass: OffscreenPass2dImpl): OffscreenPass2dImpl.BackendImpl {
        return OffscreenPass2dVk(parentPass)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenPassCubeImpl): OffscreenPassCubeImpl.BackendImpl {
        return OffscreenPassCubeVk(parentPass)
    }

    override fun drawFrame(ctx: Lwjgl3Context) {
        vkSystem.renderLoop.drawFrame()
    }

    override fun destroy(ctx: Lwjgl3Context) {
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

        override fun onDrawFrame(swapChain: SwapChain, imageIndex: Int): VkCommandBuffer {
            /*
             * From Vulkan Dos and don'ts:
             * Aim for 15-30 command buffers and 5-10 vkQueueSubmit() calls per frame, batch VkSubmitInfo() to a
             * single call as much as possible. Each vkQueueSubmit() has a performance cost on CPU, so lower is
             * generally better. Note that VkSemaphore-based synchronization can only be done across vkQueueSubmit()
             * calls, so you may be forced to split work up into multiple submits.
             */

            if (ctx.disposablePipelines.isNotEmpty()) {
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

                // fixme: submit individual render passes instead of merging command queues
                val mergeQueue = mutableListOf<DrawCommand>()
                var clearColor: Color? = null
                for (scene in ctx.scenes) {
                    for (i in scene.offscreenPasses.indices) {
                        renderOffscreen(commandBuffer, scene.offscreenPasses[i])
                    }

                    mergeQueue += scene.mainRenderPass.drawQueue.commands
                    if (scene.mainRenderPass.clearColor != null) {
                        clearColor = scene.mainRenderPass.clearColor
                    }
                }

                val renderPassInfo = renderPassBeginInfo(swapChain.renderPass, swapChain.framebuffers[imageIndex], true, clearColor)

                // fixme: optimize draw queue order (sort by distance, customizable draw order, etc.)

                vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
                renderDrawQueue(commandBuffer, mergeQueue, imageIndex, swapChain.renderPass, swapChain.nImages, false)
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
            ctx.disposablePipelines.forEach { pipeline ->
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
            ctx.disposablePipelines.clear()
        }

        private fun MemoryStack.renderDrawQueue(commandBuffer: VkCommandBuffer, drawQueue: List<DrawCommand>, imageIndex: Int,
                                                renderPass: VkRenderPass, nImages: Int, dynVp: Boolean) {
            var prevPipeline = 0UL
            drawQueue.forEach { cmd ->
                val pipelineCfg = cmd.pipeline
                if (!cmd.mesh.geometry.isEmpty() && pipelineCfg != null) {
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
                        pipelineCfg.pushConstantRanges.forEach {
                            it.onUpdate?.invoke(it, cmd)
                            val flags = it.stages.fold(0) { f, stage -> f or stage.bitValue() }
                            val pushBuffer = (it.toBuffer() as MixedBufferImpl).buffer
                            vkCmdPushConstants(commandBuffer, pipeline.pipelineLayout, flags, offset, pushBuffer)
                            offset += it.size
                        }

                        val instanceCnt: Int
                        val insts = cmd.mesh.instances
                        val instData = model.instanceBuffer
                        if (insts != null && instData != null) {
                            instanceCnt = insts.numInstances
                            vkCmdBindVertexBuffers(commandBuffer, 0, longs(model.vertexBuffer.vkBuffer, instData.vkBuffer), longs(0L, 0L))
                        } else {
                            instanceCnt = 1
                            vkCmdBindVertexBuffers(commandBuffer, 0, longs(model.vertexBuffer.vkBuffer), longs(0L))
                        }
                        vkCmdBindIndexBuffer(commandBuffer, model.indexBuffer.vkBuffer, 0L, VK_INDEX_TYPE_UINT32)
                        vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                                pipeline.pipelineLayout, 0, longs(descriptorSet.getDescriptorSet(imageIndex)), null)
                        vkCmdDrawIndexed(commandBuffer, model.numIndices, instanceCnt, 0, 0, 0)

                        ctx.engineStats.addDrawCommandCount(1)
                        ctx.engineStats.addPrimitiveCount(cmd.mesh.geometry.numPrimitives * instanceCnt)
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

        private fun MemoryStack.renderOffscreen(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenRenderPass) {
            when (offscreenPass) {
                is OffscreenRenderPass2D -> renderOffscreen2d(commandBuffer, offscreenPass)
                is OffscreenRenderPassCube -> renderOffscreenCube(commandBuffer, offscreenPass)
                else -> throw IllegalArgumentException("Not implemented: ${offscreenPass::class.java}")
            }
        }

        private fun MemoryStack.renderOffscreen2d(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenRenderPass2D) {
            offscreenPass.impl.draw(ctx)
            val backendImpl = offscreenPass.impl.backendImpl as OffscreenPass2dVk
            val rp = backendImpl.renderPass!!
            val renderPassInfo = renderPassBeginInfo(rp, rp.frameBuffer, offscreenPass.clearDepth, offscreenPass.clearColor)

            vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
            setViewport(commandBuffer, 0, 0, offscreenPass.mipWidth(offscreenPass.targetMipLevel), offscreenPass.mipHeight(offscreenPass.targetMipLevel))
            renderDrawQueue(commandBuffer, offscreenPass.drawQueue.commands, 0, rp, 1, true)
            vkCmdEndRenderPass(commandBuffer)
        }

        private fun MemoryStack.renderOffscreenCube(commandBuffer: VkCommandBuffer, offscreenPass: OffscreenRenderPassCube) {
            offscreenPass.impl.draw(ctx)
            val backendImpl = offscreenPass.impl.backendImpl as OffscreenPassCubeVk
            val rp = backendImpl.renderPass!!
            val renderPassInfo = renderPassBeginInfo(rp, rp.frameBuffer, offscreenPass.clearDepth, offscreenPass.clearColor)

            backendImpl.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
            // fixme: for some reason (timing / sync) last view is not copied sometimes? super duper fix: render last view twice
            for (view in cubeRenderPassViews) {
                vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE)
                setViewport(commandBuffer, 0, 0, offscreenPass.mipWidth(offscreenPass.targetMipLevel), offscreenPass.mipHeight(offscreenPass.targetMipLevel))
                renderDrawQueue(commandBuffer, offscreenPass.drawQueues[view.index].commands, view.index, rp, 6, true)
                vkCmdEndRenderPass(commandBuffer)
                backendImpl.copyView(commandBuffer, view)
            }
            if (offscreenPass.mipLevels > 1 && offscreenPass.targetMipLevel < 0) {
                backendImpl.generateMipmaps(commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
            } else {
                backendImpl.transitionTexLayout(commandBuffer, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
            }
        }
    }

    private fun MemoryStack.renderPassBeginInfo(renderPass: VkRenderPass, frameBuffer: Long, clearDepth: Boolean, clearColor: Color?) =
            callocVkRenderPassBeginInfo {
                sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                renderPass(renderPass.vkRenderPass)
                framebuffer(frameBuffer)
                renderArea { r ->
                    r.offset { it.x(0); it.y(0) }
                    r.extent { it.width(renderPass.maxWidth); it.height(renderPass.maxHeight) }
                }

                pClearValues(callocVkClearValueN(2) {
                    this[0].setColor(clearColor ?: Color.BLACK)
                    this[1].depthStencil { it.depth(1f); it.stencil(0) }
                })

                // fixme: make clear values optional
//                var clearCnt = 0
//                if (clearDepth) {
//                    clearCnt++
//                }
//                if (clearColor != null) {
//                    clearCnt++
//                }
//                pClearValues(callocVkClearValueN(clearCnt) {
//                    var clearI = 0
//                    if (clearColor != null) {
//                        this[clearI++].setColor(clearColor)
//                    }
//                    if (clearDepth) {
//                        this[clearI].depthStencil { it.depth(1f); it.stencil(0) }
//                    }
//                })
            }

    private fun VkClearValue.setColor(color: Color) {
        color {
            it.float32(0, color.r)
            it.float32(1, color.g)
            it.float32(2, color.b)
            it.float32(3, color.a)
        }
    }

    companion object {
        private val cubeRenderPassViews = Array(7) {
            i -> OffscreenRenderPassCube.ViewDirection.values()[i % OffscreenRenderPassCube.ViewDirection.values().size]
        }
    }
}