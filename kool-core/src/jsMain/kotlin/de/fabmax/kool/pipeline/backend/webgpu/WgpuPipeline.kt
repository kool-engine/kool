package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.PipelineInfo
import de.fabmax.kool.pipeline.backend.wgsl.WgslLocations
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased

sealed class WgpuPipeline(
    private val pipeline: PipelineBase,
    protected val backend: RenderBackendWebGpu,
): BaseReleasable(), PipelineBackend {

    private val pipelineInfo = PipelineInfo(pipeline)

    protected val device: GPUDevice get() = backend.device

    protected val locations = WgslLocations(pipeline.bindGroupLayouts, (pipeline as? DrawPipeline)?.vertexLayout)
    private val bindGroupLayouts: List<GPUBindGroupLayout> = createBindGroupLayouts(pipeline)
    protected val pipelineLayout: GPUPipelineLayout = pipeline.createPipelineLayout()

    private fun createBindGroupLayouts(pipeline: PipelineBase): List<GPUBindGroupLayout> {
        val layouts = if (this is WgpuComputePipeline) listOf(pipeline.bindGroupLayouts.pipelineScope) else pipeline.bindGroupLayouts.asList

        return layouts.map { group ->
            val layoutEntries = buildList {
                group.bindings.forEach { binding ->
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
                        is UniformBufferLayout -> add(makeLayoutEntryBuffer(location, visibility, GPUBufferBindingType.uniform))
                        is Texture1dLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.view1d))
                        is Texture2dLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.view2d))
                        is Texture3dLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.view3d))
                        is TextureCubeLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.viewCube))
                        is StorageBufferLayout -> {
                            val bufferType = if (binding.accessType == StorageAccessType.READ_ONLY) {
                                GPUBufferBindingType.readOnlyStorage
                            } else {
                                GPUBufferBindingType.storage
                            }
                            add(makeLayoutEntryBuffer(location, visibility, bufferType))
                        }
                    }
                }
            }

            device.createBindGroupLayout(
                label = "${pipeline.name}-bindGroupLayout[${group.scope}]",
                entries = layoutEntries.toTypedArray()
            )
        }
    }

    private fun PipelineBase.createPipelineLayout(): GPUPipelineLayout {
        return device.createPipelineLayout(GPUPipelineLayoutDescriptor(
            label = "${name}-bindGroupLayout",
            bindGroupLayouts = this@WgpuPipeline.bindGroupLayouts.toTypedArray()
        ))
    }

    private fun makeLayoutEntryBuffer(location: WgslLocations.Location, visibility: Int, type: GPUBufferBindingType) = GPUBindGroupLayoutEntryBuffer(
        binding = location.binding,
        visibility = visibility,
        buffer = GPUBufferBindingLayout(type = type)
    )

    private fun makeLayoutEntriesTexture(
        binding: TextureLayout,
        location: WgslLocations.Location,
        visibility: Int,
        dimension: GPUTextureViewDimension
    ): List<GPUBindGroupLayoutEntry> {
        val texSampleType = binding.sampleType.wgpu
        val samplerType = when (texSampleType) {
            GPUTextureSampleType.float -> GPUSamplerBindingType.filtering
            GPUTextureSampleType.depth -> GPUSamplerBindingType.comparison
            GPUTextureSampleType.unfilterableFloat -> GPUSamplerBindingType.nonFiltering
            else -> error("unexpected: $texSampleType")
        }

        return listOf(
            GPUBindGroupLayoutEntrySampler(
                location.binding,
                visibility,
                GPUSamplerBindingLayout(samplerType)
            ),
            GPUBindGroupLayoutEntryTexture(
                location.binding + 1,
                visibility,
                GPUTextureBindingLayout(viewDimension = dimension, sampleType = texSampleType)
            )
        )
    }

    protected fun BindGroupData.checkBindings(backend: RenderBackendWebGpu): Boolean {
        return checkStorageBuffers() && checkTextures(backend)
    }

    protected fun BindGroupData.checkStorageBuffers(): Boolean {
        return bindings
            .filterIsInstance<BindGroupData.StorageBufferBindingData<*>>()
            .all { it.storageBuffer != null }
    }

    protected fun BindGroupData.checkTextures(backend: RenderBackendWebGpu): Boolean {
        var isComplete = true
        bindings
            .filterIsInstance<BindGroupData.TextureBindingData<*>>()
            .map { it.texture }
            .filter { it?.loadingState != Texture.LoadingState.LOADED }
            .forEach {
                if (it == null || !it.checkLoadingState(backend)) {
                    isComplete = false
                }
            }
        return isComplete
    }

    private fun <T: ImageData> Texture<T>.checkLoadingState(backend: RenderBackendWebGpu): Boolean {
        checkIsNotReleased()
        if (loadingState == Texture.LoadingState.NOT_LOADED) {
            uploadData?.let {
                uploadData = null
                backend.textureLoader.loadTexture(this, it)
            }
        }
        return loadingState == Texture.LoadingState.LOADED
    }

    protected fun BindGroupData.getOrCreateWgpuData(): WgpuBindGroupData {
        val group = if (this@WgpuPipeline is WgpuComputePipeline) 0 else layout.group
        if (gpuData == null) {
            gpuData = WgpuBindGroupData(this, bindGroupLayouts[group], locations, backend)
        }
        return gpuData as WgpuBindGroupData
    }

    override fun release() {
        if (!isReleased) {
            super.release()
            if (!pipeline.isReleased) {
                pipeline.release()
            }
            when (this) {
                is WgpuDrawPipeline -> backend.pipelineManager.removeDrawPipeline(this)
                is WgpuComputePipeline -> backend.pipelineManager.removeComputePipeline(this)
            }
            pipelineInfo.deleted()
        }
    }
}