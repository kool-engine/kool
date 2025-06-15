@file:Suppress("INLINE_CLASS_IN_EXTERNAL_DECLARATION_WARNING")

package de.fabmax.kool.pipeline.backend.webgpu

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.w3c.dom.RenderingContext
import kotlin.js.Promise

external class GPU {
    fun getPreferredCanvasFormat(): GPUTextureFormat
    fun requestAdapter(options: GPURequestAdapterOptions = definedExternally): Promise<GPUAdapter?>
}

external class GPUAdapter {
    val features: dynamic
    val limits: GPUSupportedLimits
    fun requestDevice(descriptor: GPUDeviceDescriptor = definedExternally): Promise<GPUDevice>
}

external class GPUBindGroupLayout

external class GPUBindGroup

external interface GPUBindingResource

external class GPUBuffer {
    val label: String
    val size: dynamic
    fun getMappedRange(offset: Long = definedExternally, size: Long = definedExternally): ArrayBuffer
    fun mapAsync(mode: Int, offset: Long = definedExternally, size: Long = definedExternally): Promise<Unit>
    fun unmap()
    fun destroy()
}

external object GPUMapMode {
    val READ: Int
    val WRITE: Int
}

external object GPUBufferUsage {
    val MAP_READ: Int
    val MAP_WRITE: Int
    val COPY_SRC: Int
    val COPY_DST: Int
    val INDEX: Int
    val VERTEX: Int
    val UNIFORM: Int
    val STORAGE: Int
    val INDIRECT: Int
    val QUERY_RESOLVE: Int
}

external class GPUCanvasContext : RenderingContext {
    fun configure(configuration: GPUCanvasConfiguration)
    fun unconfigure()
    fun getCurrentTexture(): GPUTexture
}

external class GPUCommandBuffer

external class GPUCommandEncoder {
    fun beginComputePass(descriptor: GPUComputePassDescriptor = definedExternally): GPUComputePassEncoder
    fun beginRenderPass(descriptor: GPURenderPassDescriptor): GPURenderPassEncoder
    fun copyBufferToBuffer(source: GPUBuffer, sourceOffset: Long, destination: GPUBuffer, destinationOffset: Long, size: Long)
    fun copyTextureToBuffer(source: GPUImageCopyTexture, destination: GPUImageCopyBuffer, copySize: IntArray)
    fun copyTextureToTexture(source: GPUImageCopyTexture, destination: GPUImageCopyTexture, copySize: IntArray)
    //fun writeTimestamp(querySet: GPUQuerySet, queryIndex: Int)
    fun resolveQuerySet(querySet: GPUQuerySet, firstQuery: Int, queryCount: Int, destination: GPUBuffer, destinationOffset: Long)
    fun finish(): GPUCommandBuffer
}

external class GPUCompilationInfo {
    val messages: Array<GPUCompilationMessage>
}

external class GPUCompilationMessage {
    val type: String
    val message: String
}

external class GPUDevice {
    val limits: GPUSupportedLimits
    val queue: GPUQueue

    fun createBuffer(descriptor: GPUBufferDescriptor): GPUBuffer
    fun createBindGroupLayout(descriptor: GPUBindGroupLayoutDescriptor): GPUBindGroupLayout
    fun createPipelineLayout(descriptor: GPUPipelineLayoutDescriptor): GPUPipelineLayout
    fun createBindGroup(descriptor: GPUBindGroupDescriptor): GPUBindGroup
    fun createCommandEncoder(): GPUCommandEncoder
    fun createComputePipeline(descriptor: GPUComputePipelineDescriptor): GPUComputePipeline
    fun createQuerySet(descriptor: GPUQuerySetDescriptor): GPUQuerySet
    fun createRenderPipeline(descriptor: GPURenderPipelineDescriptor): GPURenderPipeline
    fun createShaderModule(descriptor: GPUShaderModuleDescriptor): GPUShaderModule
    fun createSampler(descriptor: GPUSamplerDescriptor): GPUSampler
    fun createTexture(descriptor: GPUTextureDescriptor): GPUTexture
}

external class GPUExternalTexture : GPUBindingResource

external interface GPUPipelineLayout

external class GPUQueue {
    fun submit(commandBuffers: Array<GPUCommandBuffer>)
    fun writeBuffer(buffer: GPUBuffer, bufferOffset: Long, data: ArrayBufferView, dataOffset: Long = definedExternally, size: Long = definedExternally)
    fun writeTexture(destination: GPUImageCopyTexture, data: ArrayBufferView, dataLayout: GPUImageDataLayout, size: IntArray)
    fun copyExternalImageToTexture(source: GPUImageCopyExternalImage, destination: GPUImageCopyTextureTagged, copySize: IntArray)
}

external class GPUComputePassEncoder {
    fun setPipeline(pipeline: GPUComputePipeline)
    fun setBindGroup(index: Int, bindGroup: GPUBindGroup, dynamicOffsets: Array<Int> = definedExternally)
    fun dispatchWorkgroups(workgroupCountX: Int, workgroupCountY: Int, workgroupCountZ: Int)
    fun end()
}

external class GPURenderPassEncoder {
    fun setPipeline(pipeline: GPURenderPipeline)
    fun setIndexBuffer(buffer: GPUBuffer, indexFormat: GPUIndexFormat, offset: Long = definedExternally, size: Long = definedExternally)
    fun setVertexBuffer(slot: Int, buffer: GPUBuffer, offset: Long = definedExternally, size: Long = definedExternally)
    fun draw(vertexCount: Int, instanceCount: Int = definedExternally, firstVertex: Int = definedExternally, firstInstance: Int = definedExternally)
    fun drawIndexed(indexCount: Int, instanceCount: Int = definedExternally, firstIndex: Int = definedExternally, baseVertex: Int = definedExternally, firstInstance: Int = definedExternally)
    fun setBindGroup(index: Int, bindGroup: GPUBindGroup, dynamicOffsets: Array<Int> = definedExternally)
    fun setViewport(x: Float, y: Float, width: Float, height: Float, minDepth: Float, maxDepth: Float)
    fun setScissorRect(x: Int, y: Int, width: Int, height: Int)
    fun end()
}

external class GPUComputePipeline {
    fun getBindGroupLayout(index: Int): GPUBindGroupLayout
}

external class GPUQuerySet

external class GPURenderPipeline {
    fun getBindGroupLayout(index: Int): GPUBindGroupLayout
}

external class GPUSampler : GPUBindingResource

external class GPUShaderModule {
    fun getCompilationInfo(): Promise<GPUCompilationInfo>
}

external object GPUShaderStage {
    val COMPUTE: Int
    val FRAGMENT: Int
    val VERTEX: Int
}

external class GPUSupportedLimits {
    val maxTextureDimension1D: Int
    val maxTextureDimension2D: Int
    val maxTextureDimension3D: Int
    val maxTextureArrayLayers: Int
    val maxBindGroups: Int
    val maxBindGroupsPlusVertexBuffers: Int
    val maxBindingsPerBindGroup: Int
    val maxDynamicUniformBuffersPerPipelineLayout: Int
    val maxDynamicStorageBuffersPerPipelineLayout: Int
    val maxSampledTexturesPerShaderStage: Int
    val maxSamplersPerShaderStage: Int
    val maxStorageBuffersPerShaderStage: Int
    val maxStorageTexturesPerShaderStage: Int
    val maxUniformBuffersPerShaderStage: Int
    val maxUniformBufferBindingSize: Double
    val maxStorageBufferBindingSize: Double
    val minUniformBufferOffsetAlignment: Int
    val minStorageBufferOffsetAlignment: Int
    val maxVertexBuffers: Int
    val maxBufferSize: Double
    val maxVertexAttributes: Int
    val maxVertexBufferArrayStride: Int
    val maxInterStageShaderComponents: Int
    val maxInterStageShaderVariables: Int
    val maxColorAttachments: Int
    val maxColorAttachmentBytesPerSample: Int
    val maxComputeWorkgroupStorageSize: Int
    val maxComputeInvocationsPerWorkgroup: Int
    val maxComputeWorkgroupSizeX: Int
    val maxComputeWorkgroupSizeY: Int
    val maxComputeWorkgroupSizeZ: Int
    val maxComputeWorkgroupsPerDimension: Int
}

external class GPUTexture {
    val label: String
    val width: Int
    val height: Int
    val depthOrArrayLayers: Int
    val mipLevelCount: Int
    val sampleCount: Int
    val format: GPUTextureFormat
    fun createView(): GPUTextureView
    fun createView(descriptor: GPUTextureViewDescriptor): GPUTextureView
    fun destroy()
}

external object GPUTextureUsage {
    val COPY_SRC: Int
    val COPY_DST: Int
    val TEXTURE_BINDING: Int
    val STORAGE_BINDING: Int
    val RENDER_ATTACHMENT: Int
}

external class GPUTextureView : GPUBindingResource
