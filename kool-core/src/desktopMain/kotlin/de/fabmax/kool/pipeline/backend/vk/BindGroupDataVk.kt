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
        passEncoderState.setBindGroup(group, this)
        TODO()
    }
}