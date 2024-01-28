package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.BindGroupData
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.Std140BufferLayout
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.MixedBufferImpl

class WgpuBindGroupData(
    private val data: BindGroupData,
    private val gpuLayout: GPUBindGroupLayout,
    private val locations: WgslLocations,
    private val backend: RenderBackendWebGpu
) : BaseReleasable(), GpuBindGroupData {
    private val device: GPUDevice get() = backend.device

    private val bufferBindings = mutableListOf<BufferBinding>()
    private var bindGroup: GPUBindGroup? = null

    fun bind(encoder: GPURenderPassEncoder, bindGroupData: BindGroupData, renderPass: RenderPass) {
        if (bindGroup == null || bindGroupData.isDirty) {
            bindGroupData.isDirty = false
            createBindGroup(renderPass)
        }

        bufferBindings.forEach { ubo ->
            if (ubo.binding.getAndClearDirtyFlag()) {
                device.queue.writeBuffer(
                    buffer = ubo.gpuBuffer.buffer,
                    bufferOffset = 0L,
                    data = (ubo.binding.buffer as MixedBufferImpl).buffer
                )
            }
        }
        encoder.setBindGroup(data.layout.group, bindGroup!!)
    }

    private fun createBindGroup(renderPass: RenderPass) {
        bufferBindings.forEach { it.gpuBuffer.release() }
        bufferBindings.clear()
        val bindGroupEntries = mutableListOf<GPUBindGroupEntry>()

        data.bindings.forEach { binding ->
            when (binding) {
                is BindGroupData.UniformBufferBindingData -> {
                    val (bufferBinding, entry) = binding.makeEntry(renderPass)
                    bufferBindings += bufferBinding
                    bindGroupEntries += entry
                }
                is BindGroupData.Texture1dBindingData -> bindGroupEntries += binding.makeTexture1dEntry()
                is BindGroupData.Texture2dBindingData -> bindGroupEntries += binding.makeTexture2dEntry()

                is BindGroupData.Texture3dBindingData -> TODO("Texture3dBindingData")
                is BindGroupData.TextureCubeBindingData -> TODO("TextureCubeBindingData")
                is BindGroupData.StorageTexture1dBindingData -> TODO("StorageTexture1dBindingData")
                is BindGroupData.StorageTexture2dBindingData -> TODO("StorageTexture2dBindingData")
                is BindGroupData.StorageTexture3dBindingData -> TODO("StorageTexture3dBindingData")
            }
        }
        bindGroup = backend.device.createBindGroup(
            label = "bindGroup[${data.layout.scope}]",
            layout = gpuLayout,
            entries = bindGroupEntries.toTypedArray()
        )
    }

    private fun BindGroupData.UniformBufferBindingData.makeEntry(renderPass: RenderPass): Pair<BufferBinding, GPUBindGroupEntry> {
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
        return BufferBinding(this, bufferLayout, gpuBuffer) to GPUBindGroupEntry(location.binding, GPUBufferBinding(gpuBuffer.buffer))
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
        ) {
            samplerSettings.maxAnisotropy
        } else {
            1
        }

        val sampler = device.createSampler(
            addressModeU = samplerSettings.addressModeU.wgpu,
            addressModeV = samplerSettings.addressModeV.wgpu,
            magFilter = samplerSettings.magFilter.wgpu,
            minFilter = samplerSettings.minFilter.wgpu,
            mipmapFilter = if (tex.props.generateMipMaps) GPUMipmapFilterMode.linear else GPUMipmapFilterMode.nearest,
            maxAnisotropy = maxAnisotropy,
        )

        return listOf(
            GPUBindGroupEntry(location.binding, sampler),
            GPUBindGroupEntry(location.binding + 1, loadedTex.texture.gpuTexture.createView())
        )
    }

    override fun release() {
        super.release()
        bufferBindings.forEach { it.gpuBuffer.release() }
        bufferBindings.clear()
    }

    private data class BufferBinding(
        val binding: BindGroupData.UniformBufferBindingData,
        val layout: Std140BufferLayout,
        val gpuBuffer: WgpuBufferResource
    )
}