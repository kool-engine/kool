package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.BindGroupData
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.GpuPass
import de.fabmax.kool.pipeline.TextureSampleType
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.pipeline.backend.wgsl.WgslLocations
import de.fabmax.kool.util.BaseReleasable
import io.ygdrasil.webgpu.*

class WgpuBindGroupData(
    private val data: BindGroupData,
    private val gpuLayout: GPUBindGroupLayout,
    private val locations: WgslLocations,
    private val backend: RenderBackendWgpu4k
) : BaseReleasable(), GpuBindGroupData {
    private val device: GPUDevice get() = backend.device

    private val bufferBindings = mutableListOf<BufferBinding>()
    private val storageBufferBindings = mutableListOf<StorageBufferBinding>()
    private val textureBindings = mutableListOf<TextureBinding>()
    private val storageTextureBindings = mutableListOf<StorageTextureBinding>()

    var bindGroup: GPUBindGroup? = null
        private set

    fun bind(passEncoderState: PassEncoderState, group: Int = data.layout.group) {
        for (i in textureBindings.indices) {
            val tex = textureBindings[i]
            if (tex.binding.texture?.gpuTexture !== tex.loadedTex) {
                // underlying gpu texture has changed, e.g. because render attachment of a render pass was recreated
                data.isDirty = true
            }
        }
        for (i in storageTextureBindings.indices) {
            val tex = storageTextureBindings[i]
            if (tex.binding.storageTexture?.asTexture?.gpuTexture !== tex.loadedTex) {
                // underlying gpu texture has changed, e.g. because render attachment of a render pass was recreated
                data.isDirty = true
            }
        }

        val recreatedBindGroup = bindGroup == null || data.isDirty
        if (recreatedBindGroup) {
            data.isDirty = false
            createBindGroup(passEncoderState.renderPass)
        }

        for (i in bufferBindings.indices) {
            val ubo = bufferBindings[i]
            ubo.binding.buffer.buffer.asArrayBuffer { arrayBuffer ->
                if (ubo.modCount != ubo.binding.modCount || recreatedBindGroup) {
                    device.queue.writeBuffer(
                        buffer = ubo.gpuBuffer.buffer,
                        bufferOffset = 0uL,
                        data = arrayBuffer
                    )
                }
            }
        }

        for (i in storageBufferBindings.indices) {
            val storage = storageBufferBindings[i]
            storage.binding.storageBuffer?.uploadData?.let { upload ->
                storage.binding.storageBuffer?.uploadData = null
                upload.asArrayBuffer { arrayBuffer ->
                    device.queue.writeBuffer(
                        buffer = storage.gpuBuffer.buffer,
                        bufferOffset = 0uL,
                        data = arrayBuffer
                    )
                }
            }
        }
        passEncoderState.setBindGroup(group, this)
    }

    private fun createBindGroup(pass: GpuPass) {
        bufferBindings.forEach { it.gpuBuffer.release() }
        bufferBindings.clear()
        textureBindings.clear()
        storageTextureBindings.clear()

        val bindGroupEntries: List<GPUBindGroupEntry> = buildList {
            data.bindings.map { binding ->
                when (binding) {
                    is BindGroupData.UniformBufferBindingData<*> -> add(binding.makeEntry(pass))
                    is BindGroupData.StorageBufferBindingData -> add(binding.makeEntry(pass))

                    is BindGroupData.Texture1dBindingData -> addAll(binding.makeTexture1dEntry())
                    is BindGroupData.Texture2dBindingData -> addAll(binding.makeTexture2dEntry())
                    is BindGroupData.Texture3dBindingData -> addAll(binding.makeTexture3dEntry())
                    is BindGroupData.TextureCubeBindingData -> addAll(binding.makeTextureCubeEntry())
                    is BindGroupData.Texture2dArrayBindingData -> addAll(binding.makeTexture2dArrayEntry())
                    is BindGroupData.TextureCubeArrayBindingData -> addAll(binding.makeTextureCubeArrayEntry())

                    is BindGroupData.StorageTexture1dBindingData -> add(binding.makeStorageTextureEntry())
                    is BindGroupData.StorageTexture2dBindingData -> add(binding.makeStorageTextureEntry())
                    is BindGroupData.StorageTexture3dBindingData -> add(binding.makeStorageTextureEntry())
                }
            }
        }
        bindGroup = backend.device.createBindGroup(
            label = "bindGroup[${data.layout.scope}]",
            layout = gpuLayout,
            entries = bindGroupEntries
        )
    }

    private fun BindGroupData.UniformBufferBindingData<*>.makeEntry(pass: GpuPass): GPUBindGroupEntry {
        val location = locations[layout]
        val struct = buffer.struct
        val gpuBuffer = backend.createBuffer(
            BufferDescriptor(
                label = "bindGroup[${data.layout.scope}]-ubo-${name}",
                size = struct.structSize.toULong(),
                usage = setOf(GPUBufferUsage.Uniform, GPUBufferUsage.CopyDst)
            ),
            "scene: ${pass.parentScene?.name}, render-pass: ${pass.name}"
        )
        bufferBindings += BufferBinding(this, gpuBuffer)
        return BindGroupEntry(location.binding.toUInt(), BufferBinding(gpuBuffer.buffer))
    }

    private fun BindGroupData.StorageBufferBindingData.makeEntry(pass: GpuPass): GPUBindGroupEntry {
        val location = locations[layout]
        val storage = checkNotNull(storageBuffer) { "Cannot create storage buffer binding from null buffer" }
        var gpuBuffer = storage.gpuBuffer as GpuBufferWgpu?
        if (gpuBuffer == null) {
            gpuBuffer = backend.createBuffer(
                BufferDescriptor(
                    label = "bindGroup[${data.layout.scope}]-storage-${name}",
                    size = (storage.size * storage.type.byteSize).toULong(),
                    usage = setOf(GPUBufferUsage.Storage, GPUBufferUsage.CopySrc, GPUBufferUsage.CopyDst)
                ),
                "scene: ${pass.parentScene?.name}, render-pass: ${pass.name}"
            )
            storage.gpuBuffer = gpuBuffer
        }
        storageBufferBindings += StorageBufferBinding(this, gpuBuffer)
        return BindGroupEntry(location.binding.toUInt(), BufferBinding(gpuBuffer.buffer))
    }

    private fun BindGroupData.Texture1dBindingData.makeTexture1dEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.gpuTexture as WgpuTextureResource?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.samplerSettings

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
        )

        textureBindings += TextureBinding(this, loadedTex)
        return listOf(
            BindGroupEntry(location.binding.toUInt(), sampler),
            BindGroupEntry(location.binding.toUInt() + 1u, loadedTex.gpuTexture.createView(dimension = GPUTextureViewDimension.OneD))
        )
    }

    private fun BindGroupData.Texture2dBindingData.makeTexture2dEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.gpuTexture as WgpuTextureResource?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.samplerSettings
        val maxAnisotropy = if (tex.mipMapping.isMipMapped &&
            samplerSettings.minFilter == FilterMethod.LINEAR &&
            samplerSettings.magFilter == FilterMethod.LINEAR
        ) samplerSettings.maxAnisotropy else 1
        val compare = if (layout.sampleType == TextureSampleType.DEPTH) samplerSettings.compareOp.wgpu else null

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            addressModeV = samplerSettings.addressModeV.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
            mipmapFilter = if (tex.mipMapping.isMipMapped) GPUMipmapFilterMode.Linear else GPUMipmapFilterMode.Nearest,
            maxAnisotropy = maxAnisotropy,
            compare = compare,
        )

        textureBindings += TextureBinding(this, loadedTex)

        val baseLevel = samplerSettings.baseMipLevel.coerceAtMost(loadedTex.imageInfo.mipLevelCount.toInt() - 1)
        val numLevels = if (samplerSettings.numMipLevels > 0) samplerSettings.numMipLevels else loadedTex.imageInfo.mipLevelCount.toInt()
        val numLevelsSafe = numLevels.coerceAtMost(loadedTex.imageInfo.mipLevelCount.toInt() - baseLevel)

        val texView = loadedTex.gpuTexture.createView(baseMipLevel = baseLevel, mipLevelCount = numLevelsSafe)
        return listOf(
            BindGroupEntry(location.binding.toUInt(), sampler),
            BindGroupEntry(location.binding.toUInt() + 1u, texView)
        )
    }

    private fun BindGroupData.Texture3dBindingData.makeTexture3dEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.gpuTexture as WgpuTextureResource?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.samplerSettings

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            addressModeV = samplerSettings.addressModeV.wgpu,
            addressModeW = samplerSettings.addressModeW.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
            mipmapFilter = if (tex.mipMapping.isMipMapped) GPUMipmapFilterMode.Linear else GPUMipmapFilterMode.Nearest,
        )

        textureBindings += TextureBinding(this, loadedTex)
        return listOf(
            BindGroupEntry(location.binding.toUInt(), sampler),
            BindGroupEntry((location.binding + 1).toUInt(), loadedTex.gpuTexture.createView())
        )
    }

    private fun BindGroupData.TextureCubeBindingData.makeTextureCubeEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.gpuTexture as WgpuTextureResource?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.samplerSettings
        val compare = if (layout.sampleType == TextureSampleType.DEPTH) samplerSettings.compareOp.wgpu else null

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            addressModeV = samplerSettings.addressModeV.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
            mipmapFilter = if (tex.mipMapping.isMipMapped) GPUMipmapFilterMode.Linear else GPUMipmapFilterMode.Nearest,
            compare = compare,
        )

        textureBindings += TextureBinding(this, loadedTex)
        return listOf(
            BindGroupEntry(location.binding.toUInt(), sampler),
            BindGroupEntry((location.binding + 1).toUInt(), loadedTex.gpuTexture.createView(dimension = GPUTextureViewDimension.Cube))
        )
    }

    private fun BindGroupData.Texture2dArrayBindingData.makeTexture2dArrayEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.gpuTexture as WgpuTextureResource?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.samplerSettings
        val maxAnisotropy = if (tex.mipMapping.isMipMapped &&
            samplerSettings.minFilter == FilterMethod.LINEAR &&
            samplerSettings.magFilter == FilterMethod.LINEAR
        ) samplerSettings.maxAnisotropy else 1
        val compare = if (layout.sampleType == TextureSampleType.DEPTH) samplerSettings.compareOp.wgpu else null

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            addressModeV = samplerSettings.addressModeV.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
            mipmapFilter = if (tex.mipMapping.isMipMapped) GPUMipmapFilterMode.Linear else GPUMipmapFilterMode.Nearest,
            maxAnisotropy = maxAnisotropy,
            compare = compare,
        )

        textureBindings += TextureBinding(this, loadedTex)
        return listOf(
            BindGroupEntry(location.binding.toUInt(), sampler),
            BindGroupEntry(location.binding.toUInt() + 1u, loadedTex.gpuTexture.createView(dimension = GPUTextureViewDimension.TwoDArray))
        )
    }

    private fun BindGroupData.TextureCubeArrayBindingData.makeTextureCubeArrayEntry(): List<GPUBindGroupEntry> {
        val location = locations[layout]
        val tex = checkNotNull(texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.gpuTexture as WgpuTextureResource?) { "Cannot create texture binding from null texture" }
        val samplerSettings = sampler ?: tex.samplerSettings
        val compare = if (layout.sampleType == TextureSampleType.DEPTH) samplerSettings.compareOp.wgpu else null

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            addressModeV = samplerSettings.addressModeV.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
            mipmapFilter = if (tex.mipMapping.isMipMapped) GPUMipmapFilterMode.Linear else GPUMipmapFilterMode.Nearest,
            compare = compare,
        )

        textureBindings += TextureBinding(this, loadedTex)
        return listOf(
            BindGroupEntry(location.binding.toUInt(), sampler),
            BindGroupEntry(location.binding.toUInt() + 1u, loadedTex.gpuTexture.createView(dimension = GPUTextureViewDimension.CubeArray))
        )
    }

    private fun BindGroupData.StorageTextureBindingData<*>.makeStorageTextureEntry(): GPUBindGroupEntry {
        val location = locations[layout]
        val storageTex = checkNotNull(storageTexture) { "Cannot create storage texture binding from null texture" }
        val loadedTex = checkNotNull(storageTex.asTexture.gpuTexture as WgpuTextureResource?) { "Cannot create storage texture binding from null texture" }

        storageTextureBindings += StorageTextureBinding(this, loadedTex)
        val texView = loadedTex.gpuTexture.createView(
            baseMipLevel = mipLevel.coerceAtMost(loadedTex.imageInfo.mipLevelCount.toInt() - 1),
            mipLevelCount = 1
        )
        return BindGroupEntry(location.binding.toUInt(), texView)
    }

    override fun release() {
        super.release()
        textureBindings.clear()
        bufferBindings.forEach { it.gpuBuffer.release() }
        bufferBindings.clear()
        storageBufferBindings.clear()
        storageTextureBindings.clear()
    }

    private data class BufferBinding(
        val binding: BindGroupData.UniformBufferBindingData<*>,
        val gpuBuffer: GpuBufferWgpu
    ) {
        var modCount = -1
    }

    private data class StorageBufferBinding(
        val binding: BindGroupData.StorageBufferBindingData,
        val gpuBuffer: GpuBufferWgpu
    )

    private data class TextureBinding(
        val binding: BindGroupData.TextureBindingData<*>,
        val loadedTex: WgpuTextureResource
    )

    private data class StorageTextureBinding(
        val binding: BindGroupData.StorageTextureBindingData<*>,
        val loadedTex: WgpuTextureResource
    )
}