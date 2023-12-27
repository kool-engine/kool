package de.fabmax.kool.pipeline.backend.webgpu

import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.RenderingContext
import kotlin.js.Promise


external val navigator: Navigator

external class Navigator {
    val gpu: GPU
}

external class GPU {
    fun getPreferredCanvasFormat(): GPUTextureFormat
    fun requestAdapter(): Promise<GPUAdapter?>
}

external class GPUAdapter {
    fun requestDevice(): Promise<GPUDevice>
}

external class GPUBindGroupLayout

external class GPUBindGroup

external interface GPUBindingResource

external class GPUBuffer {
    fun getMappedRange(offset: Long = definedExternally, size: Long = definedExternally): ArrayBuffer
    fun unmap()
    fun destroy()
}

external object GPUBufferUsage {
    val INDEX: Int
    val VERTEX: Int
    val UNIFORM: Int
    val COPY_DST: Int
}

external class GPUCanvasContext : RenderingContext {
    fun configure(configuration: GPUCanvasConfiguration)
    fun unconfigure()
    fun getCurrentTexture(): GPUTexture
}

external class GPUCommandBuffer

external class GPUCommandEncoder {
    fun beginRenderPass(descriptor: GPURenderPassDescriptor): GPURenderPassEncoder
    fun finish(): GPUCommandBuffer
}

external class GPUDevice {
    val queue: GPUQueue

    fun createBuffer(descriptor: GPUBufferDescriptor): GPUBuffer
    fun createBindGroupLayout(descriptor: GPUBindGroupLayoutDescriptor): GPUBindGroupLayout
    fun createPipelineLayout(descriptor: GPUPipelineLayoutDescriptor): GPUPipelineLayout
    fun createBindGroup(descriptor: GPUBindGroupDescriptor): GPUBindGroup
    fun createCommandEncoder(): GPUCommandEncoder
    fun createRenderPipeline(descriptor: GPURenderPipelineDescriptor): GPURenderPipeline
    fun createShaderModule(descriptor: GPUShaderModuleDescriptor): GPUShaderModule
    fun createTexture(descriptor: GPUTextureDescriptor): GPUTexture
}

external class GPUExternalTexture : GPUBindingResource

external class GPUPipelineLayout

external class GPUQueue {
    fun submit(commandBuffers: Array<GPUCommandBuffer>)
}

external class GPURenderPassEncoder {
    fun setPipeline(pipeline: GPURenderPipeline)
    fun setIndexBuffer(buffer: GPUBuffer, indexFormat: GPUIndexFormat, offset: Long = definedExternally, size: Long = definedExternally)
    fun setVertexBuffer(slot: Int, buffer: GPUBuffer, offset: Long = definedExternally, size: Long = definedExternally)
    fun draw(vertexCount: Int, instanceCount: Int = definedExternally, firstVertex: Int = definedExternally, firstInstance: Int = definedExternally)
    fun drawIndexed(indexCount: Int, instanceCount: Int = definedExternally, firstIndex: Int = definedExternally, baseVertex: Int = definedExternally, firstInstance: Int = definedExternally)
    fun setBindGroup(index: Int, bindGroup: GPUBindGroup, dynamicOffsets: Array<Int> = definedExternally)
    fun setViewport(x: Float, y: Float, width: Float, height: Float, minDepth: Float, maxDepth: Float)
    fun end()
}

external class GPURenderPipeline

external class GPUSampler : GPUBindingResource

external class GPUShaderModule

external object GPUShaderStage {
    val FRAGMENT: Int
    val VERTEX: Int
}

external class GPUTexture {
    fun createView(): GPUTextureView
    fun destroy()
}

external object GPUTextureUsage {
    val RENDER_ATTACHMENT: Int
}

external class GPUTextureView : GPUBindingResource
