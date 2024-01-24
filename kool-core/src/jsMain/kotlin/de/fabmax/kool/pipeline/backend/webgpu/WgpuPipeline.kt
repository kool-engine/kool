package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.MixedBufferImpl

class WgpuPipeline(
    val drawPipeline: DrawPipeline,
    val vertexShaderModule: GPUShaderModule,
    val fragemntShaderModule: GPUShaderModule,
    val renderPass: WgpuRenderPass,
    val backend: RenderBackendWebGpu,
): BaseReleasable(), PipelineBackend {
    private val device: GPUDevice get() = backend.device

    private val locations = WgslLocations(drawPipeline.bindGroupLayouts)

    private val bindGroupLayouts: List<GPUBindGroupLayout> = createBindGroupLayouts(drawPipeline)
    private val pipelineLayout: GPUPipelineLayout = createPipelineLayout(drawPipeline)
    private val vertexBufferLayout: List<GPUVertexBufferLayout> = createVertexBufferLayout(drawPipeline)
    private val renderPipeline: GPURenderPipeline = createRenderPipeline(drawPipeline)

    init {
        drawPipeline.pipelineBackend = this
    }

    private fun createBindGroupLayouts(pipeline: DrawPipeline): List<GPUBindGroupLayout> {

        return pipeline.bindGroupLayouts.asList.map { group ->
            val layoutEntries = group.bindings.flatMap { binding ->
                val visibility = binding.stages.fold(0) { acc, stage ->
                    acc or when (stage) {
                        ShaderStage.VERTEX_SHADER -> GPUShaderStage.VERTEX
                        ShaderStage.FRAGMENT_SHADER -> GPUShaderStage.FRAGMENT
                        ShaderStage.COMPUTE_SHADER -> GPUShaderStage.COMPUTE
                        else -> error("unsupported shader stage: $stage")
                    }
                }
                val location = locations[binding]

                when (binding) {
                    is UniformBufferLayout -> listOf(
                        GPUBindGroupLayoutEntryBuffer(
                            binding.bindingIndex,
                            visibility,
                            GPUBufferBindingLayout()
                        )
                    )

                    is Texture1dLayout -> TODO()

                    is Texture2dLayout -> listOf(
                        GPUBindGroupLayoutEntrySampler(
                            location.binding,
                            visibility,
                            GPUSamplerBindingLayout()
                        ),
                        GPUBindGroupLayoutEntryTexture(
                            location.binding + 1,
                            visibility,
                            GPUTextureBindingLayout()
                        )
                    )

                    is Texture3dLayout -> TODO()
                    is TextureCubeLayout -> TODO()
                    is StorageTexture1dLayout -> TODO()
                    is StorageTexture2dLayout -> TODO()
                    is StorageTexture3dLayout -> TODO()
                }
            }

            device.createBindGroupLayout(
                GPUBindGroupLayoutDescriptor(
                    label = "${pipeline.name}-bindGroupLayout[${group.scope}]",
                    entries = layoutEntries.toTypedArray()
                )
            )
        }
    }

    private fun createPipelineLayout(pipeline: DrawPipeline): GPUPipelineLayout {
        return device.createPipelineLayout(
            GPUPipelineLayoutDescriptor(
                label = "${pipeline.name}-bindGroupLayout",
                bindGroupLayouts = bindGroupLayouts.toTypedArray()
            )
        )
    }

    private fun createVertexBufferLayout(pipeline: DrawPipeline): List<GPUVertexBufferLayout> {
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
                    offset = attr.bufferOffset.toLong(),
                    shaderLocation = attr.index
                )
            }

            GPUVertexBufferLayout(
                arrayStride = vertexBinding.strideBytes.toLong(),
                attributes = attributes.toTypedArray(),
                stepMode = when (vertexBinding.inputRate) {
                    InputRate.VERTEX -> GPUVertexStepMode.vertex
                    InputRate.INSTANCE -> GPUVertexStepMode.instance
                }
            )
        }
    }

    private fun createRenderPipeline(pipeline: DrawPipeline): GPURenderPipeline {
        val shaderCode = pipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode
        val vertexState = GPUVertexState(
            module = vertexShaderModule,
            entryPoint = shaderCode.vertexEntryPoint,
            buffers = vertexBufferLayout.toTypedArray()
        )
        val fragmentState = GPUFragmentState(
            module = fragemntShaderModule,
            entryPoint = shaderCode.fragmentEntryPoint,
            targets = arrayOf(GPUColorTargetState(backend.canvasFormat))
        )

        val depthStencil = GPUDepthStencilState(
            format = renderPass.depthFormat,
            depthWriteEnabled = true,
            depthCompare = GPUCompareFunction.less
        )

        return device.createRenderPipeline(
            GPURenderPipelineDescriptor(
                label = "${pipeline.name}-layout",
                layout = pipelineLayout,
                vertex = vertexState,
                fragment = fragmentState,
                depthStencil = depthStencil,
                multisample = GPUMultisampleState(renderPass.multiSamples)
            )
        )
    }

    fun bind(cmd: DrawCommand, encoder: GPURenderPassEncoder): Boolean {
        encoder.setPipeline(renderPipeline)

        val pipeline = cmd.pipeline!!
        val pipelineData = pipeline.pipelineData
        val viewData = cmd.queue.view.viewPipelineData.getPipelineData(pipeline)
        val meshData = cmd.mesh.meshPipelineData.getPipelineData(pipeline)

        val bindingsComplete =
            viewData.getOrCreateWgpuData().bind(encoder, viewData) &&
            pipelineData.getOrCreateWgpuData().bind(encoder, pipelineData) &&
            meshData.getOrCreateWgpuData().bind(encoder, meshData)

        if (bindingsComplete) {
            bindVertexBuffers(encoder, cmd.mesh)
        }
        return bindingsComplete
    }

    private fun BindGroupData.getOrCreateWgpuData(): WgpuBindGroupData {
        if (gpuData == null) {
            gpuData = WgpuBindGroupData(this, bindGroupLayouts[layout.group], backend)
        }
        return gpuData as WgpuBindGroupData
    }

    private fun bindVertexBuffers(encoder: GPURenderPassEncoder, mesh: Mesh) {
        if (mesh.geometry.gpuGeometry == null) {
            mesh.geometry.gpuGeometry = WgpuGeometry(mesh, backend)
        }
        val gpuGeom = mesh.geometry.gpuGeometry as WgpuGeometry

        // todo: mesh.instances

        encoder.setVertexBuffer(0, gpuGeom.floatBuffer)
        gpuGeom.intBuffer?.let { encoder.setVertexBuffer(1, it) }
        encoder.setIndexBuffer(gpuGeom.indexBuffer, GPUIndexFormat.uint32)
    }

    inner class WgpuBindGroupData(
        private val data: BindGroupData,
        private val gpuLayout: GPUBindGroupLayout,
        private val backend: RenderBackendWebGpu
    ) :
        BaseReleasable(), GpuBindGroupData
    {
        private val device: GPUDevice get() = backend.device

        private val group = data.layout.group
        private val mappedLocations = IntArray(data.bindings.size)
        private val ubos = mutableListOf<UboBinding>()
        private var bindGroup: GPUBindGroup? = null

        private fun createBindGroup() {
            val bindGroupEntries = mutableListOf<GPUBindGroupEntry>()
            data.bindings.forEachIndexed { i, binding ->
                val location = locations[binding.layout]
                mappedLocations[i] = location.binding

                when (binding) {
                    is BindGroupData.UniformBufferBindingData -> {
                        val bufferLayout = Std140BufferLayout(binding.layout.uniforms)
                        val gpuBuffer = device.createBuffer(
                            GPUBufferDescriptor(
                                label = "bindGroup[${data.layout.scope}]-ubo-${binding.name}",
                                size = bufferLayout.size.toLong(),
                                usage = GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST
                            )
                        )
                        ubos += UboBinding(binding, bufferLayout, gpuBuffer)
                        bindGroupEntries += GPUBindGroupEntry(location.binding, GPUBufferBinding(gpuBuffer))
                    }
                    is BindGroupData.Texture1dBindingData -> TODO()
                    is BindGroupData.Texture2dBindingData -> {
                        val tex = checkNotNull(binding.texture) {
                            "Nullable textures are not yet supported"
                        }
                        val loadedTex = checkNotNull(tex.loadedTexture as LoadedTextureWebGpu?) {
                            "Lazy loaded textures are not yet supported"
                        }

                        val samplerSettings = binding.sampler ?: tex.props.defaultSamplerSettings
                        val sampler = device.createSampler(
                            GPUSamplerDescriptor(
                                addressModeU = samplerSettings.addressModeU.wgpu,
                                addressModeV = samplerSettings.addressModeV.wgpu,
                                addressModeW = samplerSettings.addressModeW.wgpu,
                                magFilter = samplerSettings.magFilter.wgpu,
                                minFilter = samplerSettings.minFilter.wgpu,
                                mipmapFilter = if (tex.props.generateMipMaps) GPUMipmapFilterMode.linear else GPUMipmapFilterMode.nearest,
                                maxAnisotropy = samplerSettings.maxAnisotropy,
                            )
                        )
                        bindGroupEntries += GPUBindGroupEntry(location.binding, sampler)
                        bindGroupEntries += GPUBindGroupEntry(location.binding+1, loadedTex.texture.createView())
                    }
                    is BindGroupData.Texture3dBindingData -> TODO()
                    is BindGroupData.TextureCubeBindingData -> TODO()
                    is BindGroupData.StorageTexture1dBindingData -> TODO()
                    is BindGroupData.StorageTexture2dBindingData -> TODO()
                    is BindGroupData.StorageTexture3dBindingData -> TODO()
                }
            }
            bindGroup = backend.device.createBindGroup(
                GPUBindGroupDescriptor(
                    label = "bindGroup[${data.layout.scope}]",
                    layout = gpuLayout,
                    entries = bindGroupEntries.toTypedArray()
                )
            )
        }

        fun bind(encoder: GPURenderPassEncoder, bindGroupData: BindGroupData): Boolean {
            if (bindGroup == null || bindGroupData.isDirty) {
                bindGroupData.isDirty = false
                createBindGroup()
            }

            ubos.forEach { ubo ->
                if (ubo.binding.getAndClearDirtyFlag()) {
                    device.queue.writeBuffer(
                        buffer = ubo.gpuBuffer,
                        bufferOffset = 0L,
                        data = (ubo.binding.buffer as MixedBufferImpl).buffer
                    )
                }
            }
            encoder.setBindGroup(group, bindGroup!!)
            return true
        }
    }

    data class UboBinding(
        val binding: BindGroupData.UniformBufferBindingData,
        val layout: Std140BufferLayout,
        val gpuBuffer: GPUBuffer
    )

    override fun removeUser(user: Any) {
        // todo
    }
}