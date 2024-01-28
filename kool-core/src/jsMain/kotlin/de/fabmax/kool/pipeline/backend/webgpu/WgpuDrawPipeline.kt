package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.MixedBufferImpl
import de.fabmax.kool.util.RenderLoop
import de.fabmax.kool.util.checkIsNotReleased
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WgpuDrawPipeline(
    drawPipeline: DrawPipeline,
    val vertexShaderModule: GPUShaderModule,
    val fragemntShaderModule: GPUShaderModule,
    val renderPass: WgpuRenderPass,
    val backend: RenderBackendWebGpu,
): BaseReleasable(), PipelineBackend {
    private val device: GPUDevice get() = backend.device

    private val locations = WgslLocations(drawPipeline.bindGroupLayouts, drawPipeline.vertexLayout)

    private val bindGroupLayouts: List<GPUBindGroupLayout> = createBindGroupLayouts(drawPipeline)
    private val pipelineLayout: GPUPipelineLayout = createPipelineLayout(drawPipeline)
    private val vertexBufferLayout: List<GPUVertexBufferLayout> = createVertexBufferLayout(drawPipeline)
    private val renderPipeline: GPURenderPipeline = createRenderPipeline(drawPipeline)

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

                    is Texture1dLayout -> listOf(
                        GPUBindGroupLayoutEntrySampler(
                            location.binding,
                            visibility,
                            GPUSamplerBindingLayout()
                        ),
                        GPUBindGroupLayoutEntryTexture(
                            location.binding + 1,
                            visibility,
                            GPUTextureBindingLayout(viewDimension = GPUTextureViewDimension.view1d)
                        )
                    )

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

                    is Texture3dLayout -> TODO("Texture3dLayout")
                    is TextureCubeLayout -> TODO("TextureCubeLayout")
                    is StorageTexture1dLayout -> TODO("StorageTexture1dLayout")
                    is StorageTexture2dLayout -> TODO("StorageTexture2dLayout")
                    is StorageTexture3dLayout -> TODO("StorageTexture3dLayout")
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
        return pipeline.vertexLayout.bindings
            .sortedBy { it.inputRate.name }     // INSTANCE first, VERTEX second
            .map { vertexBinding ->
                val attributes = vertexBinding.vertexAttributes.flatMap { attr ->
                    val (format, stride) = when (attr.type) {
                        GpuType.FLOAT1 -> GPUVertexFormat.float32 to 4
                        GpuType.FLOAT2 -> GPUVertexFormat.float32x2 to 8
                        GpuType.FLOAT3 -> GPUVertexFormat.float32x3 to 12
                        GpuType.FLOAT4 -> GPUVertexFormat.float32x4 to 16
                        GpuType.INT1 -> TODO("needs extra buffer") //GPUVertexFormat.sint32 to 4
                        GpuType.INT2 -> TODO("needs extra buffer") //GPUVertexFormat.sint32x2 to 8
                        GpuType.INT3 -> TODO("needs extra buffer") //GPUVertexFormat.sint32x3 to 12
                        GpuType.INT4 -> TODO("needs extra buffer") //GPUVertexFormat.sint32x4 to 16

                        GpuType.MAT2 -> GPUVertexFormat.float32x2 to 8
                        GpuType.MAT3 -> GPUVertexFormat.float32x3 to 12
                        GpuType.MAT4 -> GPUVertexFormat.float32x4 to 16
                    }

                    locations[attr].mapIndexed { i, loc ->
                        GPUVertexAttribute(
                            format = format,
                            offset = attr.bufferOffset.toLong() + stride * i,
                            shaderLocation = loc.location
                        )
                    }
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

        val blendMode = when (pipeline.pipelineConfig.blendMode) {
            BlendMode.DISABLED -> null
            BlendMode.BLEND_ADDITIVE -> GPUBlendState(
                color = GPUBlendComponent(srcFactor = GPUBlendFactor.one, dstFactor = GPUBlendFactor.one),
                alpha = GPUBlendComponent(),
            )
            BlendMode.BLEND_MULTIPLY_ALPHA -> GPUBlendState(
                color = GPUBlendComponent(srcFactor = GPUBlendFactor.srcAlpha, dstFactor = GPUBlendFactor.oneMinusSrcAlpha),
                alpha = GPUBlendComponent(),
            )
            BlendMode.BLEND_PREMULTIPLIED_ALPHA -> GPUBlendState(
                color = GPUBlendComponent(srcFactor = GPUBlendFactor.one, dstFactor = GPUBlendFactor.oneMinusSrcAlpha),
                alpha = GPUBlendComponent(),
            )
        }

        val fragmentState = GPUFragmentState(
            module = fragemntShaderModule,
            entryPoint = shaderCode.fragmentEntryPoint,
            targets = arrayOf(GPUColorTargetState(backend.canvasFormat, blendMode))
        )

        val primitiveState = GPUPrimitiveState(
            topology = pipeline.vertexLayout.primitiveType.wgpu,
            cullMode = pipeline.pipelineConfig.cullMethod.wgpu
        )

        val isWriteDepth = if (pipeline.pipelineConfig.depthTest == DepthCompareOp.DISABLED) false else pipeline.pipelineConfig.isWriteDepth
        val depthCompareOp = if (pipeline.pipelineConfig.depthTest == DepthCompareOp.DISABLED) GPUCompareFunction.always else pipeline.pipelineConfig.depthTest.wgpu
        val depthStencil = GPUDepthStencilState(
            format = renderPass.depthFormat,
            depthWriteEnabled = isWriteDepth,
            depthCompare = depthCompareOp
        )

        return device.createRenderPipeline(
            label = "${pipeline.name}-layout",
            layout = pipelineLayout,
            vertex = vertexState,
            fragment = fragmentState,
            depthStencil = depthStencil,
            primitive = primitiveState,
            multisample = GPUMultisampleState(renderPass.multiSamples)
        )
    }

    fun bind(cmd: DrawCommand, encoder: GPURenderPassEncoder): Boolean {
        val pipeline = cmd.pipeline!!
        val pipelineData = pipeline.pipelineData
        val viewData = cmd.queue.view.viewPipelineData.getPipelineData(pipeline)
        val meshData = cmd.mesh.meshPipelineData.getPipelineData(pipeline)

        if (!checkTextures(pipelineData) || !checkTextures(viewData) || !checkTextures(meshData)) {
            return false
        }

        encoder.setPipeline(renderPipeline)
        viewData.getOrCreateWgpuData().bind(encoder, viewData)
        pipelineData.getOrCreateWgpuData().bind(encoder, pipelineData)
        meshData.getOrCreateWgpuData().bind(encoder, meshData)

        bindVertexBuffers(encoder, cmd.mesh)
        return true
    }

    private fun checkTextures(bindGroupData: BindGroupData): Boolean {
        var isComplete = true
        bindGroupData.bindings
            .filterIsInstance<BindGroupData.TextureBindingData<*,*>>()
            .flatMap { it.textures }
            .filter { it?.loadingState != Texture.LoadingState.LOADED }
            .forEach {
                if (it == null || !checkLoadingState(it)) {
                    isComplete = false
                }
            }
        return isComplete
    }

    private fun checkLoadingState(texture: Texture): Boolean {
        texture.checkIsNotReleased()
        if (texture.loadingState == Texture.LoadingState.NOT_LOADED) {
            when (texture.loader) {
                is AsyncTextureLoader -> {
                    texture.loadingState = Texture.LoadingState.LOADING
                    CoroutineScope(Dispatchers.RenderLoop).launch {
                        val texData = texture.loader.loadTextureDataAsync().await()
                        backend.texLoader.loadTexture(texture, texData)
                    }
                }
                is SyncTextureLoader -> {
                    val texData = texture.loader.loadTextureDataSync()
                    backend.texLoader.loadTexture(texture, texData)
                }
                is BufferedTextureLoader -> {
                    backend.texLoader.loadTexture(texture, texture.loader.data)
                }
                else -> {
                    // loader is null
                    texture.loadingState = Texture.LoadingState.LOADING_FAILED
                }
            }
        }
        return texture.loadingState == Texture.LoadingState.LOADED
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
        gpuGeom.checkBuffers()

        var slot = 0
        gpuGeom.instanceBuffer?.let { encoder.setVertexBuffer(slot++, it) }
        encoder.setVertexBuffer(slot++, gpuGeom.floatBuffer)
        gpuGeom.intBuffer?.let { encoder.setVertexBuffer(slot, it) }
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

                    is BindGroupData.Texture1dBindingData -> {
                        val tex = checkNotNull(binding.texture) { "Cannot create texture binding from null texture" }
                        val loadedTex = checkNotNull(tex.loadedTexture as LoadedTextureWebGpu?) { "Cannot create texture binding from null texture" }
                        val samplerSettings = binding.sampler ?: tex.props.defaultSamplerSettings
                        val maxAnisotropy = if (tex.props.generateMipMaps && samplerSettings.minFilter == FilterMethod.LINEAR && samplerSettings.magFilter == FilterMethod.LINEAR) {
                            samplerSettings.maxAnisotropy
                        } else {
                            1
                        }

                        val sampler = device.createSampler(
                            GPUSamplerDescriptor(
                                addressModeU = samplerSettings.addressModeU.wgpu,
                                magFilter = samplerSettings.magFilter.wgpu,
                                minFilter = samplerSettings.minFilter.wgpu,
                                mipmapFilter = if (tex.props.generateMipMaps) GPUMipmapFilterMode.linear else GPUMipmapFilterMode.nearest,
                                maxAnisotropy = maxAnisotropy,
                            )
                        )

                        bindGroupEntries += GPUBindGroupEntry(location.binding, sampler)
                        bindGroupEntries += GPUBindGroupEntry(location.binding + 1, loadedTex.texture.createView(dimension = GPUTextureViewDimension.view1d))
                    }

                    is BindGroupData.Texture2dBindingData -> {
                        val tex = checkNotNull(binding.texture) { "Cannot create texture binding from null texture" }
                        val loadedTex = checkNotNull(tex.loadedTexture as LoadedTextureWebGpu?) { "Cannot create texture binding from null texture" }
                        val samplerSettings = binding.sampler ?: tex.props.defaultSamplerSettings
                        val maxAnisotropy = if (tex.props.generateMipMaps && samplerSettings.minFilter == FilterMethod.LINEAR && samplerSettings.magFilter == FilterMethod.LINEAR) {
                            samplerSettings.maxAnisotropy
                        } else {
                            1
                        }

                        val sampler = device.createSampler(
                            GPUSamplerDescriptor(
                                addressModeU = samplerSettings.addressModeU.wgpu,
                                addressModeV = samplerSettings.addressModeV.wgpu,
                                magFilter = samplerSettings.magFilter.wgpu,
                                minFilter = samplerSettings.minFilter.wgpu,
                                mipmapFilter = if (tex.props.generateMipMaps) GPUMipmapFilterMode.linear else GPUMipmapFilterMode.nearest,
                                maxAnisotropy = maxAnisotropy,
                            )
                        )
                        bindGroupEntries += GPUBindGroupEntry(location.binding, sampler)
                        bindGroupEntries += GPUBindGroupEntry(location.binding + 1, loadedTex.texture.createView())
                    }

                    is BindGroupData.Texture3dBindingData -> TODO("Texture3dBindingData")
                    is BindGroupData.TextureCubeBindingData -> TODO("TextureCubeBindingData")
                    is BindGroupData.StorageTexture1dBindingData -> TODO("StorageTexture1dBindingData")
                    is BindGroupData.StorageTexture2dBindingData -> TODO("StorageTexture2dBindingData")
                    is BindGroupData.StorageTexture3dBindingData -> TODO("StorageTexture3dBindingData")
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

        fun bind(encoder: GPURenderPassEncoder, bindGroupData: BindGroupData) {
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