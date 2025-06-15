package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.util.Color
import org.w3c.dom.ImageBitmap

class GPUBindGroupLayoutDescriptor(
    @JsName("entries")
    val entries: Array<GPUBindGroupLayoutEntry>,
    @JsName("label")
    val label: String = ""
)

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

class GPUSamplerBindingLayout(
    @JsName("type")
    val type: GPUSamplerBindingType = GPUSamplerBindingType.filtering
)

class GPUTextureBindingLayout(
    @JsName("sampleType")
    val sampleType: GPUTextureSampleType = GPUTextureSampleType.float,
    @JsName("viewDimension")
    val viewDimension: GPUTextureViewDimension = GPUTextureViewDimension.view2d,
    @JsName("multisampled")
    val multisampled: Boolean = false
)

class GPUStorageTextureBindingLayout(
    @JsName("access")
    val access: GPUStorageTextureAccess,
    @JsName("format")
    val format: GPUTextureFormat,
    @JsName("viewDimension")
    val viewDimension: GPUTextureViewDimension,
)

class GPUExternalTextureBindingLayout

class GPUBindGroupDescriptor(
    @JsName("layout")
    val layout: GPUBindGroupLayout,
    @JsName("entries")
    val entries: Array<GPUBindGroupEntry>,
    @JsName("label")
    val label: String = ""
)

class GPUBindGroupEntry(
    @JsName("binding")
    val binding: Int,
    @JsName("resource")
    val resource: GPUBindingResource,
)

class GPUBlendState(
    @JsName("color")
    val color: GPUBlendComponent,
    @JsName("alpha")
    val alpha: GPUBlendComponent,
)

class GPUBlendComponent(
    @JsName("operation")
    val operation: GPUBlendOperation = GPUBlendOperation.add,
    @JsName("srcFactor")
    val srcFactor: GPUBlendFactor = GPUBlendFactor.one,
    @JsName("dstFactor")
    val dstFactor: GPUBlendFactor = GPUBlendFactor.zero
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
    @JsName("label")
    val label: String = ""
)

interface GPUCanvasConfiguration

fun GPUCanvasConfiguration(
    device: GPUDevice,
    format: GPUTextureFormat,
    usage: Int? = null,
    colorSpace: GPUPredefinedColorSpace? = null,
    alphaMode: GPUCanvasAlphaMode? = null
): GPUCanvasConfiguration {
    val o = js("({})")
    o["device"] = device
    o["format"] = format
    usage?.let { o["usage"] = it }
    colorSpace?.let { o["colorSpace"] = it }
    alphaMode?.let { o["alphaMode"] = it }
    return o
}

data class GPUColorDict(
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

interface GPUColorTargetState

fun GPUColorTargetState(
    format: GPUTextureFormat,
    blend: GPUBlendState? = null
) : GPUColorTargetState {
    val o = js("({})")
    o["format"] = format
    blend?.let { o["blend"] = it }
    return o
}

interface GPUComputePassDescriptor

fun GPUComputePassDescriptor(
    label: String = "",
    timestampWrites: GPUComputePassTimestampWrites? = null
): GPUComputePassDescriptor {
    val o = js("({})")
    timestampWrites?.let { o["timestampWrites"] = it }
    o["label"] = label
    return o
}

interface GPUComputePipelineDescriptor

fun GPUComputePipelineDescriptor(
    layout: GPUPipelineLayout,
    compute: GPUProgrammableStage,
    label: String = "",
): GPUComputePipelineDescriptor {
    val o = js("({})")
    o["layout"] = layout
    o["compute"] = compute
    o["label"] = label
    return o
}

class GPUFragmentState(
    @JsName("module")
    val module: GPUShaderModule,
    @JsName("entryPoint")
    val entryPoint: String,
    @JsName("targets")
    val targets: Array<GPUColorTargetState>,
)

class GPUImageCopyExternalImage(
    @JsName("source")
    val source: ImageBitmap
)

class GPUImageCopyBuffer(
    @JsName("buffer")
    val buffer: GPUBuffer,
    @JsName("bytesPerRow")
    val bytesPerRow: Int,
    @JsName("rowsPerImage")
    val rowsPerImage: Int,
    @JsName("offset")
    val offset: Long = 0
)

class GPUImageCopyTexture(
    @JsName("texture")
    val texture: GPUTexture,
    @JsName("mipLevel")
    val mipLevel: Int = 0,
    @JsName("origin")
    val origin: IntArray = intArrayOf(0, 0, 0),
    //aspect: GPUTextureAspect = 'all'
)

class GPUImageCopyTextureTagged(
    @JsName("texture")
    val texture: GPUTexture,
    @JsName("mipLevel")
    val mipLevel: Int = 0,
    @JsName("origin")
    val origin: IntArray = intArrayOf(0, 0, 0),
    //aspect: GPUTextureAspect = 'all'
    //colorSpace: PredefinedColorSpace = 'srgb'
    //premultipliedAlpha: boolean = 'false'
)

class GPUImageDataLayout(
    @JsName("bytesPerRow")
    val bytesPerRow: Int,
    @JsName("rowsPerImage")
    val rowsPerImage: Int,
    @JsName("offset")
    val offset: Long = 0
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
    @JsName("label")
    val label: String = ""
)

interface GPUPrimitiveState

fun GPUPrimitiveState(
    topology: GPUPrimitiveTopology,
    stripIndexFormat: GPUIndexFormat = GPUIndexFormat.uint32,
    frontFace: GPUFrontFace = GPUFrontFace.ccw,
    cullMode: GPUCullMode = GPUCullMode.none,
    unclippedDepth: Boolean = false
): GPUPrimitiveState {
    val o = js("({})")
    o["frontFace"] = frontFace
    o["cullMode"] = cullMode
    o["unclippedDepth"] = unclippedDepth
    o["topology"] = topology
    if (topology == GPUPrimitiveTopology.triangleStrip || topology == GPUPrimitiveTopology.lineStrip) {
        o["stripIndexFormat"] = stripIndexFormat
    }
    return o
}

interface GPURenderPassColorAttachment

fun GPURenderPassColorAttachment(
    view: GPUTextureView,
    clearValue: GPUColorDict? = null,
    resolveTarget: GPUTextureView? = null,
    loadOp: GPULoadOp = if (clearValue != null) GPULoadOp.clear else GPULoadOp.load,
    storeOp: GPUStoreOp = GPUStoreOp.store,
    label: String = ""
) : GPURenderPassColorAttachment {
    val o = js("({})")
    o["label"] = label
    clearValue?.let { o["clearValue"] = it }
    o["loadOp"] = loadOp
    o["view"] = view
    o["storeOp"] = storeOp
    resolveTarget?.let { o["resolveTarget"] = it }
    return o
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

interface GPURenderPassDescriptor

fun GPURenderPassDescriptor(
    colorAttachments: Array<GPURenderPassColorAttachment>,
    depthStencilAttachment: GPURenderPassDepthStencilAttachment? = null,
    timestampWrites: GPURenderPassTimestampWrites? = null,
    label: String = ""
): GPURenderPassDescriptor {
    val o = js("({})")
    o["colorAttachments"] = colorAttachments
    depthStencilAttachment?.let { o["depthStencilAttachment"] = it }
    timestampWrites?.let { o["timestampWrites"] = it }
    o["label"] = label
    return o
}

data class GPUComputePassTimestampWrites(
    @JsName("querySet")
    val querySet: GPUQuerySet,
    @JsName("beginningOfPassWriteIndex")
    val beginningOfPassWriteIndex: Int,
    @JsName("endOfPassWriteIndex")
    val endOfPassWriteIndex: Int
)

data class GPURenderPassTimestampWrites(
    @JsName("querySet")
    val querySet: GPUQuerySet,
    @JsName("beginningOfPassWriteIndex")
    val beginningOfPassWriteIndex: Int,
    @JsName("endOfPassWriteIndex")
    val endOfPassWriteIndex: Int
)

interface GPURenderPipelineDescriptor

fun GPURenderPipelineDescriptor(
    layout: GPUPipelineLayout,
    vertex: GPUVertexState,
    fragment: GPUFragmentState? = null,
    depthStencil: GPUDepthStencilState? = null,
    primitive: GPUPrimitiveState? = null,
    multisample: GPUMultisampleState? = null,
    label: String = "",
): GPURenderPipelineDescriptor {
    val o = js("({})")
    o["layout"] = layout
    o["vertex"] = vertex
    fragment?.let { o["fragment"] = it }
    depthStencil?.let { o["depthStencil"] = it }
    primitive?.let { o["primitive"] = it }
    multisample?.let { o["multisample"] = it }
    o["label"] = label
    return o
}

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

interface GPUDeviceDescriptor

fun GPUDeviceDescriptor(
    requiredFeatures: Array<String> = emptyArray(),
    label: String = "",
): GPUDeviceDescriptor {
    val o = js("({})")
    o["requiredFeatures"] = requiredFeatures
    o["label"] = label
    return o
}

interface GPUProgrammableStage

fun GPUProgrammableStage(
    module: GPUShaderModule,
    entryPoint: String,
    // constants: Map<String, GPUPipelineConstantValue>
): GPUProgrammableStage {
    val o = js("({})")
    o["module"] = module
    o["entryPoint"] = entryPoint
    return o
}

interface GPUQuerySetDescriptor

fun GPUQuerySetDescriptor(
    type: GPUQueryType,
    count: Int,
    label: String = "",
): GPUQuerySetDescriptor {
    val o = js("({})")
    o["type"] = type
    o["count"] = count
    o["label"] = label
    return o
}

data class GPUShaderModuleDescriptor(
    @JsName("code")
    val code: String,
    @JsName("label")
    val label: String = ""
)

interface GPUSamplerDescriptor
fun GPUSamplerDescriptor(
    label: String = "",
    addressModeU: GPUAddressMode = GPUAddressMode.clampToEdge,
    addressModeV: GPUAddressMode = GPUAddressMode.clampToEdge,
    addressModeW: GPUAddressMode = GPUAddressMode.clampToEdge,
    magFilter: GPUFilterMode = GPUFilterMode.nearest,
    minFilter: GPUFilterMode = GPUFilterMode.nearest,
    mipmapFilter: GPUMipmapFilterMode = GPUMipmapFilterMode.nearest,
    lodMinClamp: Float = 0f,
    lodMaxClamp: Float = 32f,
    maxAnisotropy: Int = 1,
    compare: GPUCompareFunction? = null,
): GPUSamplerDescriptor {
    val o = js("({})")
    o["label"] = label
    o["addressModeU"] = addressModeU
    o["addressModeV"] = addressModeV
    o["addressModeW"] = addressModeW
    o["magFilter"] = magFilter
    o["minFilter"] = minFilter
    o["mipmapFilter"] = mipmapFilter
    o["lodMinClamp"] = lodMinClamp
    o["lodMaxClamp"] = lodMaxClamp
    o["maxAnisotropy"] = maxAnisotropy
    compare?.let { o["compare"] = it }
    return o
}

class GPUTextureDescriptor(
    @JsName("size")
    val size: IntArray,
    @JsName("format")
    val format: GPUTextureFormat,
    @JsName("usage")
    val usage: Int,
    @JsName("label")
    val label: String = "",
    @JsName("mipLevelCount")
    val mipLevelCount: Int = 1,
    @JsName("sampleCount")
    val sampleCount: Int = 1,
    @JsName("dimension")
    val dimension: GPUTextureDimension = GPUTextureDimension.texture2d,
    @JsName("viewFormats")
    val viewFormats: Array<GPUTextureFormat> = emptyArray(),
)

interface GPUTextureViewDescriptor
fun GPUTextureViewDescriptor(
    label: String = "",
    format: GPUTextureFormat? = null,
    dimension: GPUTextureViewDimension? = null,
    //val aspect: GPUTextureAspect = 'all'
    baseMipLevel: Int = 0,
    mipLevelCount: Int? = null,
    baseArrayLayer: Int = 0,
    arrayLayerCount: Int? = null
): GPUTextureViewDescriptor {
    val o = js("({})")
    o["label"] = label
    format?.let { o["format"] = it }
    dimension?.let { o["dimension"] = it }
    o["baseMipLevel"] = baseMipLevel
    mipLevelCount?.let { o["mipLevelCount"] = it }
    o["baseArrayLayer"] = baseArrayLayer
    arrayLayerCount?.let { o["arrayLayerCount"] = it }
    return o
}

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
