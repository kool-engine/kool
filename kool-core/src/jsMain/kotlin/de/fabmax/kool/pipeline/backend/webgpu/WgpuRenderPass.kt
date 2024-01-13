package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*

class WgpuRenderPass(val backend: RenderBackendWebGpu, val multiSamples: Int = 4) {

    private val device: GPUDevice
        get() = backend.device
    private val gpuContext: GPUCanvasContext
        get() = backend.gpuContext

    private var colorAttachment: GPUTexture? = null
    private var colorAttachmentView: GPUTextureView? = null

    private val depthFormat = GPUTextureFormat.depth32float
    private var depthAttachment: GPUTexture? = null
    private var depthAttachmentView: GPUTextureView? = null

    fun createRenderAttachments(
        width: Int = backend.canvas.width,
        height: Int = backend.canvas.height,
    ) {
        colorAttachment?.destroy()
        depthAttachment?.destroy()

        val depthDescriptor = GPUTextureDescriptor(
            size = intArrayOf(width, height),
            format = depthFormat,
            usage = GPUTextureUsage.RENDER_ATTACHMENT,
            sampleCount = multiSamples
        )
        depthAttachment = device.createTexture(depthDescriptor).also {
            depthAttachmentView = it.createView()
        }

        val colorDescriptor = GPUTextureDescriptor(
            size = intArrayOf(width, height),
            format = backend.canvasFormat,
            usage = GPUTextureUsage.RENDER_ATTACHMENT,
            sampleCount = multiSamples
        )
        colorAttachment = device.createTexture(colorDescriptor).also {
            colorAttachmentView = it.createView()
        }
    }

    fun doForegroundPass(scene: Scene) {
        if (depthAttachment == null || colorAttachment == null) {
            createRenderAttachments()
        }

        val scenePass = when (val pass = scene.mainRenderPass) {
            is Scene.OnscreenSceneRenderPass -> pass
            else -> TODO()
        }

        device.queue.submit(scenePass.views.map { it.encodeQueue() }.toTypedArray())
    }

    private fun RenderPass.View.encodeQueue(): GPUCommandBuffer {
        val encoder = device.createCommandEncoder()

        val colorAttachments = clearColors.map { clearColor ->
            val resolveTarget = gpuContext.getCurrentTexture().createView()
            clearColor?.let {
                GPURenderPassColorAttachmentClear(colorAttachmentView!!, resolveTarget, GPUColorDict(it))
            } ?: GPURenderPassColorAttachmentLoad(colorAttachmentView!!, resolveTarget)
        }.toTypedArray()

        val depthAttachment = GPURenderPassDepthStencilAttachment(
            view = depthAttachmentView!!,
            depthLoadOp = GPULoadOp.clear,
            depthStoreOp = GPUStoreOp.store,
            depthClearValue = 1f
        )

        val pass = encoder.beginRenderPass(GPURenderPassDescriptor(colorAttachments, depthAttachment))
        //pass.setViewport()
        //pass.setScissor()

        for (cmd in drawQueue.commands) {
            val t = Time.precisionTime

            if (cmd.geometry.numIndices == 0) continue
            val pipeline = cmd.pipeline ?: continue
            if (!pipeline.setup(pass, cmd)) continue

//            val insts = cmd.mesh.instances
//            if (insts == null) {
                pass.drawIndexed(cmd.geometry.numIndices)
//            } else {
//                pass.drawIndexed(cmd.geometry.numIndices, insts.numInstances)
//            }

            cmd.mesh.drawTime = Time.precisionTime - t
        }

        pass.end()
        return encoder.finish()
    }

    private fun Pipeline.setup(pass: GPURenderPassEncoder, drawCmd: DrawCommand): Boolean {
        // call onUpdate callbacks
        for (i in onUpdate.indices) {
            onUpdate[i].invoke(drawCmd)
        }

        val gpuPipeline = getOrCreateWgpuPipeline()
        pass.setPipeline(gpuPipeline.pipeline)
        gpuPipeline.bindVertexBuffers(pass, drawCmd.mesh)
        gpuPipeline.bindBindGroups(pass, this)
        return true
    }

    private val pipelines = mutableMapOf<Long, WgpuPipeline>()
    private fun Pipeline.getOrCreateWgpuPipeline(): WgpuPipeline {
        return pipelines.getOrPut(pipelineHash) {
            logD { "create pipeline: $name (hash=$pipelineHash)" }
            WgpuPipeline(this)
        }
    }

    inner class WgpuPipeline(pipeline: Pipeline) {
        val bindGroupLayout: GPUBindGroupLayout = device.createBindGroupLayout(bindGroupLayoutDescriptor(pipeline))
        val pipelineLayout: GPUPipelineLayout = device.createPipelineLayout(pipelineLayoutDescriptor(pipeline))
        val vertexBufferLayout: Array<GPUVertexBufferLayout> = vertexBufferLayout(pipeline)
        val vertexShaderModule: GPUShaderModule = device.createShaderModule(vertexShaderModuleDescriptor(pipeline))
        val fragemntShaderModule: GPUShaderModule = device.createShaderModule(fragmentShaderModuleDescriptor(pipeline))
        val pipeline: GPURenderPipeline = device.createRenderPipeline(renderPipelineDescriptor(pipeline))

        fun bindGroupLayoutDescriptor(pipeline: Pipeline): GPUBindGroupLayoutDescriptor {
            val layoutEntries = pipeline.bindGroupLayout.bindings.map { binding ->
                val visibility = binding.stages.fold(0) { acc, stage ->
                    acc or when (stage) {
                        ShaderStage.VERTEX_SHADER -> GPUShaderStage.VERTEX
                        ShaderStage.FRAGMENT_SHADER -> GPUShaderStage.FRAGMENT
                        ShaderStage.COMPUTE_SHADER -> GPUShaderStage.COMPUTE
                        else -> error("unsupported shader stage: $stage")
                    }
                }

                when (binding) {
                    is UniformBufferLayout -> GPUBindGroupLayoutEntryBuffer(binding.bindingIndex, visibility, GPUBufferBindingLayout())
                    is Texture1dLayout -> TODO()
                    is Texture2dLayout -> TODO()
                    is Texture3dLayout -> TODO()
                    is TextureCubeLayout -> TODO()
                    is StorageTexture1dLayout -> TODO()
                    is StorageTexture2dLayout -> TODO()
                    is StorageTexture3dLayout -> TODO()
                }
            }

            return GPUBindGroupLayoutDescriptor(
                label = "${pipeline.name}-bindGroupLayout",
                entries = layoutEntries.toTypedArray()
            )
        }

        fun pipelineLayoutDescriptor(pipeline: Pipeline) = GPUPipelineLayoutDescriptor(
            label = "${pipeline.name}-bindGroupLayout",
            bindGroupLayouts = arrayOf(bindGroupLayout)
        )

        fun renderPipelineDescriptor(pipeline: Pipeline): GPURenderPipelineDescriptor {
            val shaderCode = pipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode
            val vertexState = GPUVertexState(
                module = vertexShaderModule,
                entryPoint = shaderCode.vertexEntryPoint,
                buffers = vertexBufferLayout
            )
            val fragmentState = GPUFragmentState(
                module = fragemntShaderModule,
                entryPoint = shaderCode.fragmentEntryPoint,
                targets = arrayOf(GPUColorTargetState(backend.canvasFormat))
            )

            val depthStencil = GPUDepthStencilState(
                format = depthFormat,
                depthWriteEnabled = true,
                depthCompare = GPUCompareFunction.less
            )

            return GPURenderPipelineDescriptor(
                label = "${pipeline.name}-layout",
                layout = pipelineLayout,
                vertex = vertexState,
                fragment = fragmentState,
                depthStencil = depthStencil,
                multisample = GPUMultisampleState(multiSamples)
            )
        }

        fun vertexBufferLayout(pipeline: Pipeline): Array<GPUVertexBufferLayout> {
            return pipeline.vertexLayout.bindings.map { vertexBinding ->
                val attributes = vertexBinding.vertexAttributes.map { attr ->
                    val format = when (attr.type) {
                        GpuType.FLOAT1 -> GPUVertexFormat.float32
                        GpuType.FLOAT2 -> GPUVertexFormat.float32x2
                        GpuType.FLOAT3 -> GPUVertexFormat.float32x3
                        GpuType.FLOAT4 -> GPUVertexFormat.float32x4
                        GpuType.INT1 -> GPUVertexFormat.sint32
                        GpuType.INT2 -> GPUVertexFormat.sint32x2
                        GpuType.INT3 -> GPUVertexFormat.sint32x3
                        GpuType.INT4 -> GPUVertexFormat.sint32x4
                        else -> error("Invalid vertex attribute type: ${attr.type}")
                    }
                    GPUVertexAttribute(
                        format = format,
                        offset = attr.offset.toLong(),
                        shaderLocation = attr.location
                    )
                }.toTypedArray()

                GPUVertexBufferLayout(
                    arrayStride = vertexBinding.strideBytes.toLong(),
                    attributes = attributes,
                    stepMode = when (vertexBinding.inputRate) {
                        InputRate.VERTEX -> GPUVertexStepMode.vertex
                        InputRate.INSTANCE -> GPUVertexStepMode.instance
                    }
                )
            }.toTypedArray()
        }

        fun vertexShaderModuleDescriptor(pipeline: Pipeline) = GPUShaderModuleDescriptor(
            label = "${pipeline.name} vertex shader",
            code = (pipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode).vertexSrc
        )

        fun fragmentShaderModuleDescriptor(pipeline: Pipeline) = GPUShaderModuleDescriptor(
            label = "${pipeline.name} fragment shader",
            code = (pipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode).fragmentSrc
        )

        fun bindVertexBuffers(pass: GPURenderPassEncoder, mesh: Mesh) {
            val gpuGeom = (mesh.geometry.gpuGeometry as WgpuGeometry?) ?: WgpuGeometry(mesh).also { mesh.geometry.gpuGeometry = it }
            pass.setVertexBuffer(0, gpuGeom.floatBuffer)
            gpuGeom.intBuffer?.let { pass.setVertexBuffer(1, it) }
            pass.setIndexBuffer(gpuGeom.indexBuffer, GPUIndexFormat.uint32)
        }

        // fixme: this needs to be associated with mesh / pipeline instance
        var bindGroup: GPUBindGroup? = null
        val ubos = mutableListOf<UboBinding>()

        fun bindBindGroups(pass: GPURenderPassEncoder, pipeline: Pipeline) {
            // fixme: support multiple bind groups
            if (bindGroup == null) {
                val bindGroupEntries = mutableListOf<GPUBindGroupEntry>()
                pipeline.bindGroupLayouts.indices.forEach { group ->
                    pipeline.bindGroupLayouts[group].bindings
                        .filterIsInstance<UniformBufferLayout>()
                        .forEach { ubo ->
                            val layout = Std140BufferLayout(ubo.uniforms)
                            val gpuBuffer = device.createBuffer(GPUBufferDescriptor(
                                label = "${pipeline.name} uniforms",
                                size = layout.size.toLong(),
                                usage = GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST
                            ))
                            ubos += UboBinding(group, ubo, layout, gpuBuffer)
                            bindGroupEntries += GPUBindGroupEntry(ubo.bindingIndex, GPUBufferBinding(gpuBuffer))
                        }
                }

                bindGroup = device.createBindGroup(GPUBindGroupDescriptor(
                    label = "${pipeline.name} bind group",
                    layout = bindGroupLayout,
                    entries = bindGroupEntries.toTypedArray()
                ))
            }

            ubos.forEach { ubo ->
                val data = pipeline.bindGroupData[ubo.group].uniformBufferBindingData(ubo.binding.bindingIndex)
                if (data.getAndClearDirtyFlag()) {
                    device.queue.writeBuffer(
                        buffer = ubo.gpuBuffer,
                        bufferOffset = 0L,
                        data = (data.buffer as MixedBufferImpl).buffer
                    )
                }
            }
            pass.setBindGroup(0, bindGroup!!)
        }
    }

    data class UboBinding(
        val group: Int,
        val binding: UniformBufferLayout,
        val layout: Std140BufferLayout,
        val gpuBuffer: GPUBuffer
    )

    inner class WgpuGeometry(mesh: Mesh) : GpuGeometry {
        val indexBuffer: GPUBuffer
        val floatBuffer: GPUBuffer
        val intBuffer: GPUBuffer?

        override var isReleased: Boolean = false

        init {
            val geom = mesh.geometry
            indexBuffer = device.createBuffer(GPUBufferDescriptor(
                label = "${mesh.name} index data",
                size = 4 * geom.numIndices.toLong(),
                usage = GPUBufferUsage.INDEX or GPUBufferUsage.COPY_DST
            ))

            floatBuffer = device.createBuffer(GPUBufferDescriptor(
                label = "${mesh.name} vertex float data",
                size = geom.byteStrideF.toLong() * geom.numVertices,
                usage = GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST
            ))

            intBuffer = if (geom.byteStrideI > 0) {
                device.createBuffer(GPUBufferDescriptor(
                    label = "${mesh.name} vertex int data",
                    size = geom.byteStrideI.toLong() * geom.numVertices,
                    usage = GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST
                ))
            } else null

            device.queue.writeBuffer(
                buffer = indexBuffer,
                bufferOffset = 0L,
                data = (geom.indices as Int32BufferImpl).buffer,
                dataOffset = 0L,
                size = geom.numIndices.toLong()
            )

            device.queue.writeBuffer(
                buffer = floatBuffer,
                bufferOffset = 0L,
                data = (geom.dataF as Float32BufferImpl).buffer,
                dataOffset = 0L,
                size = geom.vertexSizeF * geom.numVertices.toLong()
            )

            intBuffer?.let {
                device.queue.writeBuffer(
                    buffer = it,
                    bufferOffset = 0L,
                    data = (geom.dataI as Int32BufferImpl).buffer,
                    dataOffset = 0L,
                    size = geom.vertexSizeI * geom.numVertices.toLong()
                )
            }
        }

        override fun release() {
            indexBuffer.destroy()
            floatBuffer.destroy()
            intBuffer?.destroy()
            isReleased = true
        }

    }
}