package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.util.Color

interface GPUDictionary {
    @JsName("label")
    val label: String
}

class GPUBindGroupLayoutDescriptor(
    @JsName("entries")
    val entries: Array<GPUBindGroupLayoutEntry>,
    override val label: String = "GPUBindGroupLayoutDescriptor"
) : GPUDictionary

sealed interface GPUBindGroupLayoutEntry

data class GPUBindGroupLayoutEntryBuffer(
    @JsName("binding")
    val binding: Int,
    @JsName("visibility")
    val visibility: Int,
    @JsName("buffer")
    val buffer: GPUBufferBindingLayout,
) : GPUBindGroupLayoutEntry

data class GPUBindGroupLayoutEntrySampler(
    @JsName("binding")
    val binding: Int,
    @JsName("visibility")
    val visibility: Int,
    @JsName("sampler")
    val sampler: GPUSamplerBindingLayout,
) : GPUBindGroupLayoutEntry

data class GPUBindGroupLayoutEntryTexture(
    @JsName("binding")
    val binding: Int,
    @JsName("visibility")
    val visibility: Int,
    @JsName("texture")
    val texture: GPUTextureBindingLayout,
) : GPUBindGroupLayoutEntry

data class GPUBindGroupLayoutEntryStorageTexture(
    @JsName("binding")
    val binding: Int,
    @JsName("visibility")
    val visibility: Int,
    @JsName("storageTexture")
    val storageTexture: GPUStorageTextureBindingLayout,
) : GPUBindGroupLayoutEntry

data class GPUBindGroupLayoutEntryExternaleTexture(
    @JsName("binding")
    val binding: Int,
    @JsName("visibility")
    val visibility: Int,
    @JsName("externalTexture")
    val externalTexture: GPUExternalTextureBindingLayout,
) : GPUBindGroupLayoutEntry

data class GPUBufferBindingLayout(
    @JsName("type")
    val type: GPUBufferBindingType = GPUBufferBindingType.uniform,
    @JsName("hasDynamicOffset")
    val hasDynamicOffset: Boolean = false,
    @JsName("minBindingSize")
    val minBindingSize: Long = 0
)

class GPUSamplerBindingLayout

class GPUTextureBindingLayout

class GPUStorageTextureBindingLayout

class GPUExternalTextureBindingLayout

class GPUBindGroupDescriptor(
    @JsName("layout")
    val layout: GPUBindGroupLayout,
    @JsName("entries")
    val entries: Array<GPUBindGroupEntry>,
    override val label: String = "GPUBindGroupDescriptor"
) : GPUDictionary

class GPUBindGroupEntry(
    @JsName("binding")
    val binding: Int,
    @JsName("resource")
    val resource: GPUBindingResource,
)

class GPUBufferBinding(
    @JsName("buffer")
    val buffer: GPUBuffer,
    @JsName("offset")
    val offset: Long = 0,
//    @JsName("size")
//    val size: Long = 0
) : GPUBindingResource

data class GPUBufferDescriptor(
    @JsName("size")
    val size: Long,
    @JsName("usage")
    val usage: Int,
    @JsName("mappedAtCreation")
    val mappedAtCreation: Boolean = false,
    override val label: String = "GPUBufferDescriptor"
) : GPUDictionary

class GPUCanvasConfiguration(
    @JsName("device")
    val device: GPUDevice,
    @JsName("format")
    val format: GPUTextureFormat,
    @JsName("colorSpace")
    val colorSpace: GPUPredefinedColorSpace = GPUPredefinedColorSpace.srgb,
    @JsName("alphaMode")
    val alphaMode: GPUCanvasAlphaMode = GPUCanvasAlphaMode.opaque
)

class GPUColorDict(
    @JsName("r")
    val r: Float,
    @JsName("g")
    val g: Float,
    @JsName("b")
    val b: Float,
    @JsName("a")
    val a: Float,
)

fun GPUColorDict(color: Color): GPUColorDict = GPUColorDict(color.r, color.g, color.b, color.a)

class GPUColorTargetState(
    @JsName("format")
    val format: GPUTextureFormat
)

class GPUFragmentState(
    @JsName("module")
    val module: GPUShaderModule,
    @JsName("entryPoint")
    val entryPoint: String,
    @JsName("targets")
    val targets: Array<GPUColorTargetState>,
)

data class GPUMultisampleState(
    @JsName("count")
    val count: Int = 1,
//    @JsName("mask")
//    val mask: UInt = 0xffffffffu,
    @JsName("alphaToCoverageEnabled")
    val alphaToCoverageEnabled: Boolean = false
)

class GPUPipelineLayoutDescriptor(
    @JsName("bindGroupLayouts")
    val bindGroupLayouts: Array<GPUBindGroupLayout>,
    override val label: String = "GPUPipelineLayoutDescriptor"
) : GPURenderPassColorAttachment

data class GPUPrimitiveState(
    @JsName("topology")
    val topology: GPUPrimitiveTopology = GPUPrimitiveTopology.triangleList,
    //stripIndexFormat: GPUIndexFormat
    //frontFace: GPUFrontFace= 'ccw'
    //cullMode: GPUCullMode= 'none'
    //unclippedDepth: boolean= 'false'
)

sealed interface GPURenderPassColorAttachment : GPUDictionary

data class GPURenderPassColorAttachmentClear(
    @JsName("view")
    val view: GPUTextureView,
    @JsName("resolveTarget")
    val resolveTarget: GPUTextureView,
    @JsName("clearValue")
    val clearValue: GPUColorDict,
    @JsName("storeOp")
    val storeOp: GPUStoreOp = GPUStoreOp.store,
    override val label: String = "GPURenderPassColorAttachmentClear"
) : GPURenderPassColorAttachment {
    @JsName("loadOp")
    val loadOp: GPULoadOp = GPULoadOp.clear
}

data class GPURenderPassColorAttachmentLoad(
    @JsName("view")
    val view: GPUTextureView,
    @JsName("resolveTarget")
    val resolveTarget: GPUTextureView,
    @JsName("storeOp")
    val storeOp: GPUStoreOp = GPUStoreOp.store,
    override val label: String = "GPURenderPassColorAttachmentClear"
) : GPURenderPassColorAttachment {
    @JsName("loadOp")
    val loadOp: GPULoadOp = GPULoadOp.load
}

data class GPURenderPassDepthStencilAttachment(
    @JsName("view")
    val view: GPUTextureView,
    @JsName("depthLoadOp")
    val depthLoadOp: GPULoadOp,
    @JsName("depthStoreOp")
    val depthStoreOp: GPUStoreOp,
    @JsName("depthClearValue")
    val depthClearValue: Float = 1f,
)

class GPURenderPassDescriptor(
    @JsName("colorAttachments")
    val colorAttachments: Array<GPURenderPassColorAttachment>,
    @JsName("depthStencilAttachment")
    val depthStencilAttachment: GPURenderPassDepthStencilAttachment,
    override val label: String = "GPURenderPassDescriptor"
) : GPUDictionary

data class GPURenderPipelineDescriptor(
    @JsName("layout")
    val layout: GPUPipelineLayout,
    @JsName("vertex")
    val vertex: GPUVertexState,
    @JsName("fragment")
    val fragment: GPUFragmentState,
    @JsName("depthStencil")
    val depthStencil: GPUDepthStencilState,
    @JsName("primitive")
    val primitive: GPUPrimitiveState = GPUPrimitiveState(),
    @JsName("multisample")
    val multisample: GPUMultisampleState = GPUMultisampleState(),
    override val label: String = "GPURenderPipelineDescriptor"
) : GPUDictionary

data class GPUDepthStencilState(
    @JsName("format")
    val format: GPUTextureFormat,
    @JsName("depthWriteEnabled")
    val depthWriteEnabled: Boolean,
    @JsName("depthCompare")
    val depthCompare: GPUCompareFunction,
    @JsName("depthBias")
    val depthBias: Int = 0,
    @JsName("depthBiasSlopeScale")
    val depthBiasSlopeScale: Float = 0f,
    @JsName("depthBiasClamp")
    val depthBiasClamp: Float = 0f
)

data class GPUShaderModuleDescriptor(
    @JsName("code")
    val code: String,
    override val label: String = "GPUShaderModuleDescriptor"
) : GPUDictionary

class GPUTextureDescriptor(
    @JsName("size")
    val size: IntArray,
    @JsName("format")
    val format: GPUTextureFormat,
    @JsName("usage")
    val usage: Int,
    @JsName("sampleCount")
    val sampleCount: Int = 1,
)

class GPUVertexAttribute(
    @JsName("format")
    val format: GPUVertexFormat,
    @JsName("offset")
    val offset: Long,
    @JsName("shaderLocation")
    val shaderLocation: Int
)

class GPUVertexBufferLayout(
    @JsName("arrayStride")
    val arrayStride: Long,
    @JsName("attributes")
    val attributes: Array<GPUVertexAttribute>,
    @JsName("stepMode")
    val stepMode: GPUVertexStepMode = GPUVertexStepMode.vertex
)

class GPUVertexState(
    @JsName("module")
    val module: GPUShaderModule,
    @JsName("entryPoint")
    val entryPoint: String,
    @JsName("buffers")
    val buffers: Array<GPUVertexBufferLayout> = emptyArray(),
)

data class GPURequestAdapterOptions(
    @JsName("powerPreference")
    val powerPreference: GPUPowerPreference,
    @JsName("forceFallbackAdapter")
    val forceFallbackAdapter: Boolean = false
)
