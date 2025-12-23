@file:Suppress("INLINE_CLASS_IN_EXTERNAL_DECLARATION_WARNING")

package de.fabmax.kool.pipeline.backend.webgpu

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.w3c.dom.RenderingContext
import kotlin.js.*

external class GPU : JsAny {
    fun getPreferredCanvasFormat(): String //GPUTextureFormat
    fun requestAdapter(options: GPURequestAdapterOptions = definedExternally): Promise<GPUAdapter?>
}

external class GPUAdapter : JsAny {
    val features: GPUSupportedFeatures
    val limits: GPUSupportedLimits
    fun requestDevice(descriptor: GPUDeviceDescriptor = definedExternally): Promise<GPUDevice>
}

external interface GPUSupportedFeatures : JsAny {
    fun forEach(block: (JsString) -> Unit)
}

external class GPUBindGroupLayout : JsAny

external class GPUBindGroup : JsAny

external interface GPUBindingResource : JsAny

external class GPUBuffer : JsAny {
    val label: String
    val size: JsNumber
    fun getMappedRange(offset: JsNumber = definedExternally, size: JsNumber = definedExternally): ArrayBuffer
    fun mapAsync(mode: Int, offset: JsNumber = definedExternally, size: JsNumber = definedExternally): Promise<*>
    fun unmap()
    fun destroy()
}

external object GPUMapMode : JsAny {
    val READ: Int
    val WRITE: Int
}

external object GPUBufferUsage : JsAny {
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

external class GPUCanvasContext : RenderingContext, JsAny {
    fun configure(configuration: GPUCanvasConfiguration)
    fun unconfigure()
    fun getCurrentTexture(): GPUTexture
}

external class GPUCommandBuffer : JsAny

external class GPUCommandEncoder : JsAny {
    fun beginComputePass(descriptor: GPUComputePassDescriptor = definedExternally): GPUComputePassEncoder
    fun beginRenderPass(descriptor: GPURenderPassDescriptor): GPURenderPassEncoder
    fun copyBufferToBuffer(source: GPUBuffer, sourceOffset: JsNumber, destination: GPUBuffer, destinationOffset: JsNumber, size: JsNumber)
    fun copyTextureToBuffer(source: GPUImageCopyTexture, destination: GPUImageCopyBuffer, copySize: JsArray<JsNumber>)
    fun copyTextureToTexture(source: GPUImageCopyTexture, destination: GPUImageCopyTexture, copySize: JsArray<JsNumber>)
    //fun writeTimestamp(querySet: GPUQuerySet, queryIndex: Int)
    fun resolveQuerySet(querySet: GPUQuerySet, firstQuery: Int, queryCount: Int, destination: GPUBuffer, destinationOffset: JsNumber)
    fun finish(): GPUCommandBuffer
}

external class GPUCompilationInfo : JsAny {
    val messages: JsArray<GPUCompilationMessage>
}

external class GPUCompilationMessage : JsAny {
    val type: String
    val message: String
}

external interface GPUDevice : JsAny {
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

external interface GPUPipelineLayout : JsAny

external class GPUQueue : JsAny {
    fun submit(commandBuffers: JsArray<GPUCommandBuffer>)
    fun writeBuffer(buffer: GPUBuffer, bufferOffset: JsNumber, data: ArrayBufferView, dataOffset: JsNumber = definedExternally, size: JsNumber = definedExternally)
    fun writeTexture(destination: GPUImageCopyTexture, data: ArrayBufferView, dataLayout: GPUImageDataLayout, size: JsArray<JsNumber>)
    fun copyExternalImageToTexture(source: GPUImageCopyExternalImage, destination: GPUImageCopyTextureTagged, copySize: JsArray<JsNumber>)
}

external class GPUComputePassEncoder : JsAny {
    fun setPipeline(pipeline: GPUComputePipeline)
    fun setBindGroup(index: Int, bindGroup: GPUBindGroup)
    fun dispatchWorkgroups(workgroupCountX: Int, workgroupCountY: Int, workgroupCountZ: Int)
    fun end()
}

external class GPURenderPassEncoder : JsAny {
    fun setPipeline(pipeline: GPURenderPipeline)
    fun setIndexBuffer(buffer: GPUBuffer, indexFormat: String /*GPUIndexFormat*/, offset: JsNumber = definedExternally, size: JsNumber = definedExternally)
    fun setVertexBuffer(slot: Int, buffer: GPUBuffer, offset: JsNumber = definedExternally, size: JsNumber = definedExternally)
    fun draw(vertexCount: Int, instanceCount: Int = definedExternally, firstVertex: Int = definedExternally, firstInstance: Int = definedExternally)
    fun drawIndexed(indexCount: Int, instanceCount: Int = definedExternally, firstIndex: Int = definedExternally, baseVertex: Int = definedExternally, firstInstance: Int = definedExternally)
    fun setBindGroup(index: Int, bindGroup: GPUBindGroup)
    fun setViewport(x: Float, y: Float, width: Float, height: Float, minDepth: Float, maxDepth: Float)
    fun setScissorRect(x: Int, y: Int, width: Int, height: Int)
    fun end()
}

external class GPUComputePipeline : JsAny {
    fun getBindGroupLayout(index: Int): GPUBindGroupLayout
}

external class GPUQuerySet : JsAny

external class GPURenderPipeline : JsAny {
    fun getBindGroupLayout(index: Int): GPUBindGroupLayout
}

external class GPUSampler : GPUBindingResource

external class GPUShaderModule : JsAny {
    fun getCompilationInfo(): Promise<GPUCompilationInfo>
}

external object GPUShaderStage : JsAny {
    val COMPUTE: Int
    val FRAGMENT: Int
    val VERTEX: Int
}

external interface GPUSupportedLimits : JsAny {
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

external class GPUTexture : JsAny {
    val label: String
    val width: Int
    val height: Int
    val depthOrArrayLayers: Int
    val mipLevelCount: Int
    val sampleCount: Int
    val format: String //GPUTextureFormat
    fun createView(): GPUTextureView
    fun createView(descriptor: GPUTextureViewDescriptor): GPUTextureView
    fun destroy()
}

external object GPUTextureUsage : JsAny {
    val COPY_SRC: Int
    val COPY_DST: Int
    val TEXTURE_BINDING: Int
    val STORAGE_BINDING: Int
    val RENDER_ATTACHMENT: Int
}

external class GPUTextureView : GPUBindingResource
