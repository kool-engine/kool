package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.PipelineInfo
import de.fabmax.kool.pipeline.backend.wgsl.WgslLocations
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.checkIsNotReleased

sealed class WgpuPipeline(
    private val pipeline: PipelineBase,
    protected val backend: RenderBackendWebGpu,
): BaseReleasable(), PipelineBackend {

    private val pipelineInfo = PipelineInfo(pipeline)

    protected val device: GPUDevice get() = backend.device

    protected val locations = WgslLocations(pipeline.bindGroupLayouts, (pipeline as? DrawPipeline)?.vertexLayout)
    private val bindGroupLayouts: List<GPUBindGroupLayout> = createBindGroupLayouts()
    protected val pipelineLayout: GPUPipelineLayout = createPipelineLayout()

    private fun createBindGroupLayouts(): List<GPUBindGroupLayout> {
        val layouts = if (this is WgpuComputePipeline) {
            listOf(pipeline.bindGroupLayouts.pipelineScope)
        } else {
            pipeline.bindGroupLayouts.asList
        }

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
                        is UniformBufferLayout<*> -> add(makeLayoutEntryBuffer(location, visibility))
                        is StorageBufferLayout -> add(makeLayoutEntryStorageBuffer(location, visibility, binding))

                        is Texture1dLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.view1d))
                        is Texture2dLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.view2d))
                        is Texture3dLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.view3d))
                        is TextureCubeLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.viewCube))
                        is Texture2dArrayLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.view2dArray))
                        is TextureCubeArrayLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.viewCubeArray))

                        is StorageTexture1dLayout -> add(makeLayoutStorageTexture(binding, location, visibility))
                        is StorageTexture2dLayout -> add(makeLayoutStorageTexture(binding, location, visibility))
                        is StorageTexture3dLayout -> add(makeLayoutStorageTexture(binding, location, visibility))
                    }
                }
            }

            device.createBindGroupLayout(
                label = "${pipeline.name}-bindGroupLayout[${group.scope}]",
                entries = layoutEntries.toTypedArray()
            )
        }
    }

    private fun createPipelineLayout(): GPUPipelineLayout {
        return device.createPipelineLayout(GPUPipelineLayoutDescriptor(
            label = "${pipeline.name}-bindGroupLayout",
            bindGroupLayouts = bindGroupLayouts.toTypedArray()
        ))
    }

    private fun makeLayoutEntryBuffer(location: WgslLocations.Location, visibility: Int) = GPUBindGroupLayoutEntryBuffer(
        binding = location.binding,
        visibility = visibility,
        buffer = GPUBufferBindingLayout(type = GPUBufferBindingType.uniform)
    )

    private fun makeLayoutEntryStorageBuffer(location: WgslLocations.Location, visibility: Int, binding: StorageBufferLayout): GPUBindGroupLayoutEntryBuffer {
        val type = if (binding.accessType == StorageAccessType.READ_ONLY) {
            GPUBufferBindingType.readOnlyStorage
        } else {
            GPUBufferBindingType.storage
        }
        return GPUBindGroupLayoutEntryBuffer(
            binding = location.binding,
            visibility = visibility,
            buffer = GPUBufferBindingLayout(type = type)
        )
    }

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

    private fun makeLayoutStorageTexture(
        binding: StorageTextureLayout,
        location: WgslLocations.Location,
        visibility: Int
    ): GPUBindGroupLayoutEntryStorageTexture {
        val dimension = when (binding) {
            is StorageTexture1dLayout -> GPUTextureViewDimension.view1d
            is StorageTexture2dLayout -> GPUTextureViewDimension.view2d
            is StorageTexture3dLayout -> GPUTextureViewDimension.view3d
        }
        val access = when (binding.accessType) {
            StorageAccessType.READ_ONLY -> GPUStorageTextureAccess.readOnly
            StorageAccessType.WRITE_ONLY -> GPUStorageTextureAccess.writeOnly
            StorageAccessType.READ_WRITE -> GPUStorageTextureAccess.readWrite
        }
        return GPUBindGroupLayoutEntryStorageTexture(
            location.binding,
            visibility,
            GPUStorageTextureBindingLayout(access, binding.texFormat.wgpuStorage, dimension)
        )
    }

    protected fun BindGroupData.checkBindings(): Boolean {
        if (Time.frameCount == checkFrame) return isCheckOk
        checkFrame = Time.frameCount
        isCheckOk = true

        for (i in bindings.indices) {
            val binding = bindings[i]
            when (binding) {
                is BindGroupData.StorageBufferBindingData -> {
                    isCheckOk = isCheckOk && binding.storageBuffer != null
                }
                is BindGroupData.TextureBindingData<*> -> {
                    val tex = binding.texture
                    if (tex == null || !tex.checkLoadingState()) {
                        isCheckOk = false
                    }
                }
                else -> { }
            }
        }
        return isCheckOk
    }

    private fun <T: ImageData> Texture<T>.checkLoadingState(): Boolean {
        checkIsNotReleased()
        uploadData?.let { backend.textureLoader.loadTexture(this) }
        return isLoaded
    }

    protected fun BindGroupData.getOrCreateWgpuData(): WgpuBindGroupData {
        if (gpuData == null) {
            val group = if (this@WgpuPipeline is WgpuComputePipeline) 0 else layout.group
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