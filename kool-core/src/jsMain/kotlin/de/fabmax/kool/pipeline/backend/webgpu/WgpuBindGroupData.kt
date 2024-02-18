package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.pipeline.backend.wgsl.WgslLocations
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.MixedBufferImpl
import de.fabmax.kool.util.Uint8BufferImpl

class WgpuBindGroupData(
    private val data: BindGroupData,
    private val gpuLayout: GPUBindGroupLayout,
    private val locations: WgslLocations,
    private val backend: RenderBackendWebGpu
) : BaseReleasable(), GpuBindGroupData {
    private val device: GPUDevice get() = backend.device

    private val bufferBindings = mutableListOf<BufferBinding>()
    private val storageBufferBindings = mutableListOf<StorageBufferBinding>()
    private val textureBindings = mutableListOf<TextureBinding>()

    var bindGroup: GPUBindGroup? = null
        private set

    fun bind(passEncoderState: PassEncoderState, renderPass: RenderPass, group: Int = data.layout.group) {
        for (i in textureBindings.indices) {
            val tex = textureBindings[i]
            if (tex.binding.texture?.loadedTexture !== tex.loadedTex) {
                // underlying gpu texture has changed, e.g. because render attachment of a render pass was recreated
                data.isDirty = true
            }
        }

        val recreatedBindGroup = bindGroup == null || data.isDirty
        if (recreatedBindGroup) {
            data.isDirty = false
            createBindGroup(renderPass)
        }

        for (i in bufferBindings.indices) {
            val ubo = bufferBindings[i]
            if (ubo.binding.getAndClearDirtyFlag() || recreatedBindGroup) {
                device.queue.writeBuffer(
                    buffer = ubo.gpuBuffer.buffer,
                    bufferOffset = 0L,
                    data = (ubo.binding.buffer as MixedBufferImpl).buffer
                )
            }
        }

        for (i in storageBufferBindings.indices) {
            val storage = storageBufferBindings[i]
            if (storage.binding.getAndClearDirtyFlag() || recreatedBindGroup) {
                val hostBuffer = when (val buf = checkNotNull(storage.binding.storageBuffer?.buffer)) {
                    is Uint8BufferImpl -> buf.buffer
                    is Float32BufferImpl -> buf.buffer
                    else -> error("unexpected buffer type: ${buf::class.simpleName}")
                }
                device.queue.writeBuffer(
                    buffer = storage.gpuBuffer.buffer,
                    bufferOffset = 0L,
                    data = hostBuffer
                )
            }
        }
        passEncoderState.setBindGroup(group, this)
    }

    private fun createBindGroup(renderPass: RenderPass) {
        bufferBindings.forEach { it.gpuBuffer.release() }
        bufferBindings.clear()
        textureBindings.clear()

        val bindGroupEntries = mutableListOf<GPUBindGroupEntry>()

        data.bindings.forEach { binding ->
            when (binding) {
                is BindGroupData.UniformBufferBindingData -> bindGroupEntries += binding.makeEntry(renderPass)
                is BindGroupData.Texture1dBindingData -> bindGroupEntries += binding.makeTexture1dEntry()
                is BindGroupData.Texture2dBindingData -> bindGroupEntries += binding.makeTexture2dEntry()
                is BindGroupData.Texture3dBindingData -> bindGroupEntries += binding.makeTexture3dEntry()
                is BindGroupData.TextureCubeBindingData -> bindGroupEntries += binding.makeTextureCubeEntry()

                is BindGroupData.StorageBuffer1dBindingData -> bindGroupEntries += binding.makeEntry(renderPass)
                is BindGroupData.StorageBuffer2dBindingData -> bindGroupEntries += binding.makeEntry(renderPass)
                is BindGroupData.StorageBuffer3dBindingData -> bindGroupEntries += binding.makeEntry(renderPass)
            }
        }
        bindGroup = backend.device.createBindGroup(
            label = "bindGroup[${data.layout.scope}]",
            layout = gpuLayout,
            entries = bindGroupEntries.toTypedArray()
        )
    }

    private fun BindGroupData.UniformBufferBindingData.makeEntry(renderPass: RenderPass): GPUBindGroupEntry {
        val location = locations[layout]
        val bufferLayout = Std140BufferLayout(layout.uniforms)
        val gpuBuffer = backend.createBuffer(
            GPUBufferDescriptor(
                label = "bindGroup[${data.layout.scope}]-ubo-${name}",
                size = bufferLayout.size.toLong(),
                usage = GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST
            ),
            "scene: ${renderPass.parentScene?.name}, render-pass: ${renderPass.name}"
        )
        bufferBindings += BufferBinding(this, gpuBuffer)
        return GPUBindGroupEntry(location.binding, GPUBufferBinding(gpuBuffer.buffer))
    }

    private fun BindGroupData.StorageBufferBindingData<*>.makeEntry(renderPass: RenderPass): GPUBindGroupEntry {
        val (name, location) = when (this) {
            is BindGroupData.StorageBuffer1dBindingData -> name to locations[layout]
            is BindGroupData.StorageBuffer2dBindingData -> name to locations[layout]
            is BindGroupData.StorageBuffer3dBindingData -> name to locations[layout]
            else -> error("unreachable")
        }

        val storage = checkNotNull(storageBuffer) { "Cannot create storage buffer binding from null buffer" }
        var gpuBuffer = storage.gpuBuffer as WgpuBufferResource?
        if (gpuBuffer == null) {
            gpuBuffer = backend.createBuffer(
                GPUBufferDescriptor(
                    label = "bindGroup[${data.layout.scope}]-storage-${name}",
                    size = storage.buffer.limit.toLong() * 4,
                    usage = GPUBufferUsage.STORAGE or GPUBufferUsage.COPY_SRC or GPUBufferUsage.COPY_DST
                ),
                "scene: ${renderPass.parentScene?.name}, render-pass: ${renderPass.name}"
            )
            storage.gpuBuffer = gpuBuffer
        }
        storageBufferBindings += StorageBufferBinding(this, gpuBuffer)
        return GPUBindGroupEntry(location.binding, GPUBufferBinding(gpuBuffer.buffer))
    }

    private fun BindGroupData.Texture1dBindingData.makeTexture1dEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.loadedTexture as WgpuLoadedTexture?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.props.defaultSamplerSettings

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
        )

        textureBindings += TextureBinding(this, loadedTex)
        return listOf(
            GPUBindGroupEntry(location.binding, sampler),
            GPUBindGroupEntry(location.binding + 1, loadedTex.texture.gpuTexture.createView(dimension = GPUTextureViewDimension.view1d))
        )
    }

    private fun BindGroupData.Texture2dBindingData.makeTexture2dEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.loadedTexture as WgpuLoadedTexture?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.props.defaultSamplerSettings
        val maxAnisotropy = if (tex.props.generateMipMaps &&
            samplerSettings.minFilter == FilterMethod.LINEAR &&
            samplerSettings.magFilter == FilterMethod.LINEAR
        ) samplerSettings.maxAnisotropy else 1
        val compare = if (layout.sampleType == TextureSampleType.DEPTH) samplerSettings.compareOp.wgpu else null

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            addressModeV = samplerSettings.addressModeV.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
            mipmapFilter = if (tex.props.generateMipMaps) GPUMipmapFilterMode.linear else GPUMipmapFilterMode.nearest,
            maxAnisotropy = maxAnisotropy,
            compare = compare,
        )

        textureBindings += TextureBinding(this, loadedTex)
        return listOf(
            GPUBindGroupEntry(location.binding, sampler),
            GPUBindGroupEntry(location.binding + 1, loadedTex.texture.gpuTexture.createView())
        )
    }

    private fun BindGroupData.Texture3dBindingData.makeTexture3dEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.loadedTexture as WgpuLoadedTexture?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.props.defaultSamplerSettings

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            addressModeV = samplerSettings.addressModeV.wgpu,
            addressModeW = samplerSettings.addressModeW.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
            mipmapFilter = if (tex.props.generateMipMaps) GPUMipmapFilterMode.linear else GPUMipmapFilterMode.nearest,
        )

        textureBindings += TextureBinding(this, loadedTex)
        return listOf(
            GPUBindGroupEntry(location.binding, sampler),
            GPUBindGroupEntry(location.binding + 1, loadedTex.texture.gpuTexture.createView())
        )
    }

    private fun BindGroupData.TextureCubeBindingData.makeTextureCubeEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.loadedTexture as WgpuLoadedTexture?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.props.defaultSamplerSettings
        val compare = if (layout.sampleType == TextureSampleType.DEPTH) samplerSettings.compareOp.wgpu else null

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            addressModeV = samplerSettings.addressModeV.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
            mipmapFilter = if (tex.props.generateMipMaps) GPUMipmapFilterMode.linear else GPUMipmapFilterMode.nearest,
            compare = compare,
        )

        textureBindings += TextureBinding(this, loadedTex)
        return listOf(
            GPUBindGroupEntry(location.binding, sampler),
            GPUBindGroupEntry(location.binding + 1, loadedTex.texture.gpuTexture.createView(dimension = GPUTextureViewDimension.viewCube))
        )
    }

    override fun release() {
        super.release()
        bufferBindings.forEach { it.gpuBuffer.release() }
        bufferBindings.clear()
        storageBufferBindings.forEach { it.gpuBuffer.release() }
        storageBufferBindings.clear()
    }

    private data class BufferBinding(
        val binding: BindGroupData.UniformBufferBindingData,
        val gpuBuffer: WgpuBufferResource
    )

    private data class StorageBufferBinding(
        val binding: BindGroupData.StorageBufferBindingData<*>,
        val gpuBuffer: WgpuBufferResource
    )

    private data class TextureBinding(
        val binding: BindGroupData.TextureBindingData<*>,
        val loadedTex: WgpuLoadedTexture
    )
}