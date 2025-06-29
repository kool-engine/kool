package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.backend.stats.PipelineInfo
import de.fabmax.kool.pipeline.backend.wgsl.WgslLocations
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.checkIsNotReleased
import io.ygdrasil.webgpu.*

sealed class WgpuPipeline(
    private val pipeline: PipelineBase,
    protected val backend: RenderBackendWgpu4k,
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
                    val visibility = binding.stages.fold(emptySet<GPUShaderStage>()) { acc, stage ->
                        acc + when (stage) {
                            ShaderStage.VERTEX_SHADER -> GPUShaderStage.Vertex
                            ShaderStage.FRAGMENT_SHADER -> GPUShaderStage.Fragment
                            ShaderStage.COMPUTE_SHADER -> GPUShaderStage.Compute
                            else -> error("unsupported shader stage: $stage")
                        }
                    }
                    val location = locations[binding]

                    when (binding) {
                        is UniformBufferLayout<*> -> add(makeLayoutEntryBuffer(location, visibility))
                        is StorageBufferLayout -> add(makeLayoutEntryStorageBuffer(location, visibility, binding))

                        is Texture1dLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.OneD))
                        is Texture2dLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.TwoD))
                        is Texture3dLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.ThreeD))
                        is TextureCubeLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.Cube))
                        is Texture2dArrayLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.TwoDArray))
                        is TextureCubeArrayLayout -> addAll(makeLayoutEntriesTexture(binding, location, visibility, GPUTextureViewDimension.CubeArray))

                        is StorageTexture1dLayout -> add(makeLayoutStorageTexture(binding, location, visibility))
                        is StorageTexture2dLayout -> add(makeLayoutStorageTexture(binding, location, visibility))
                        is StorageTexture3dLayout -> add(makeLayoutStorageTexture(binding, location, visibility))
                    }
                }
            }

            device.createBindGroupLayout(
                label = "${pipeline.name}-bindGroupLayout[${group.scope}]",
                entries = layoutEntries
            )
        }
    }

    private fun createPipelineLayout(): GPUPipelineLayout {
        return device.createPipelineLayout(
            PipelineLayoutDescriptor(
                label = "${pipeline.name}-bindGroupLayout",
                bindGroupLayouts = bindGroupLayouts
            )
        )
    }

    private fun makeLayoutEntryBuffer(location: WgslLocations.Location, visibility: GPUShaderStageFlags) = BindGroupLayoutEntry(
        binding = location.binding.toUInt(),
        visibility = visibility,
        buffer = BufferBindingLayout(type = GPUBufferBindingType.Uniform)
    )

    private fun makeLayoutEntryStorageBuffer(location: WgslLocations.Location, visibility: GPUShaderStageFlags, binding: StorageBufferLayout): GPUBindGroupLayoutEntry {
        val type = if (binding.accessType == StorageAccessType.READ_ONLY) {
            GPUBufferBindingType.ReadOnlyStorage
        } else {
            GPUBufferBindingType.Storage
        }
        return BindGroupLayoutEntry(
            binding = location.binding.toUInt(),
            visibility = visibility,
            buffer = BufferBindingLayout(type = type)
        )
    }

    private fun makeLayoutEntriesTexture(
        binding: TextureLayout,
        location: WgslLocations.Location,
        visibility: GPUShaderStageFlags,
        dimension: GPUTextureViewDimension
    ): List<GPUBindGroupLayoutEntry> {
        val texSampleType = binding.sampleType.wgpu
        val samplerType = when (texSampleType) {
            GPUTextureSampleType.Float -> GPUSamplerBindingType.Filtering
            GPUTextureSampleType.Depth -> GPUSamplerBindingType.Comparison
            GPUTextureSampleType.UnfilterableFloat -> GPUSamplerBindingType.NonFiltering
            else -> error("unexpected: $texSampleType")
        }

        return listOf(
            BindGroupLayoutEntry(
                location.binding.toUInt(),
                visibility,
                sampler = SamplerBindingLayout(samplerType)
            ),
            BindGroupLayoutEntry(
                (location.binding + 1).toUInt(),
                visibility,
                texture = TextureBindingLayout(viewDimension = dimension, sampleType = texSampleType)
            )
        )
    }

    private fun makeLayoutStorageTexture(
        binding: StorageTextureLayout,
        location: WgslLocations.Location,
        visibility: GPUShaderStageFlags
    ): GPUBindGroupLayoutEntry {
        val dimension = when (binding) {
            is StorageTexture1dLayout -> GPUTextureViewDimension.OneD
            is StorageTexture2dLayout -> GPUTextureViewDimension.TwoD
            is StorageTexture3dLayout -> GPUTextureViewDimension.ThreeD
        }
        val access = when (binding.accessType) {
            StorageAccessType.READ_ONLY -> GPUStorageTextureAccess.ReadOnly
            StorageAccessType.WRITE_ONLY -> GPUStorageTextureAccess.WriteOnly
            StorageAccessType.READ_WRITE -> GPUStorageTextureAccess.ReadWrite
        }
        return BindGroupLayoutEntry(
            location.binding.toUInt(),
            visibility,
            storageTexture = StorageTextureBindingLayout(binding.texFormat.wgpuStorage, access, dimension)
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