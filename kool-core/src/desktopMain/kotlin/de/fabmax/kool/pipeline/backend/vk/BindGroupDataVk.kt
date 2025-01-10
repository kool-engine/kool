package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.BindGroupData
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.pipeline.backend.wgsl.WgslLocations
import de.fabmax.kool.util.BaseReleasable

class BindGroupDataVk(
    private val data: BindGroupData,
    val gpuLayout: VkDescriptorSetLayout,
    private val locations: WgslLocations,
    private val backend: RenderBackendVk
) : BaseReleasable(), GpuBindGroupData {
    private val device: Device get() = backend.device

//    private val bufferBindings = mutableListOf<BufferBinding>()
//    private val storageBufferBindings = mutableListOf<StorageBufferBinding>()
//    private val textureBindings = mutableListOf<TextureBinding>()
//
//    var bindGroup: GPUBindGroup? = null
//        private set

    fun bind(passEncoderState: PassEncoderState, renderPass: RenderPass, group: Int = data.layout.group) {
//        val recreatedBindGroup = bindGroup == null || data.isDirty
//        if (recreatedBindGroup) {
//            data.isDirty = false
//            createBindGroup(renderPass)
//        }
//
//        for (i in bufferBindings.indices) {
//            val ubo = bufferBindings[i]
//            if (ubo.binding.getAndClearDirtyFlag() || recreatedBindGroup) {
//                device.queue.writeBuffer(
//                    buffer = ubo.gpuBuffer.buffer,
//                    bufferOffset = 0L,
//                    data = (ubo.binding.buffer as MixedBufferImpl).buffer
//                )
//            }
//        }

        passEncoderState.setBindGroup(group, this)
        TODO()
    }

    private fun createBindGroup(renderPass: RenderPass) {
//        bufferBindings.forEach { it.gpuBuffer.release() }
//        bufferBindings.clear()
//        textureBindings.clear()

//        val bindGroupEntries = mutableListOf<GPUBindGroupEntry>()

//        data.bindings.forEach { binding ->
//            when (binding) {
//                is BindGroupData.UniformBufferBindingData -> bindGroupEntries += binding.makeEntry(renderPass)
//                is BindGroupData.Texture1dBindingData -> bindGroupEntries += binding.makeTexture1dEntry()
//                is BindGroupData.Texture2dBindingData -> bindGroupEntries += binding.makeTexture2dEntry()
//                is BindGroupData.Texture3dBindingData -> bindGroupEntries += binding.makeTexture3dEntry()
//                is BindGroupData.TextureCubeBindingData -> bindGroupEntries += binding.makeTextureCubeEntry()
//                is BindGroupData.Texture2dArrayBindingData -> bindGroupEntries += binding.makeTexture2dArrayEntry()
//                is BindGroupData.TextureCubeArrayBindingData -> bindGroupEntries += binding.makeTextureCubeArrayEntry()
//
//                is BindGroupData.StorageBuffer1dBindingData -> bindGroupEntries += binding.makeEntry(renderPass)
//                is BindGroupData.StorageBuffer2dBindingData -> bindGroupEntries += binding.makeEntry(renderPass)
//                is BindGroupData.StorageBuffer3dBindingData -> bindGroupEntries += binding.makeEntry(renderPass)
//            }
//        }
//        bindGroup = backend.device.createBindGroup(
//            label = "bindGroup[${data.layout.scope}]",
//            layout = gpuLayout,
//            entries = bindGroupEntries.toTypedArray()
//        )
    }

//    private fun BindGroupData.UniformBufferBindingData.makeEntry(renderPass: RenderPass): GPUBindGroupEntry {
//        val location = locations[layout]
//        val bufferLayout = Std140BufferLayout(layout.uniforms)
//        val gpuBuffer = backend.memManager.createBuffer(
//            GPUBufferDescriptor(
//                label = "bindGroup[${data.layout.scope}]-ubo-${name}",
//                size = bufferLayout.size.toLong(),
//                usage = GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST
//            ),
//            "scene: ${renderPass.parentScene?.name}, render-pass: ${renderPass.name}"
//        )
//        bufferBindings += BufferBinding(this, gpuBuffer)
//        return GPUBindGroupEntry(location.binding, GPUBufferBinding(gpuBuffer.buffer))
//    }

}