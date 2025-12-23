@file:Suppress("unused")

package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.toJsArray
import de.fabmax.kool.toJsNumber
import de.fabmax.kool.util.Color
import org.w3c.dom.ImageBitmap
import kotlin.js.*

external interface GPUBindGroupLayoutDescriptor : JsAny

fun GPUBindGroupLayoutDescriptor(
    entries: List<GPUBindGroupLayoutEntry>,
    label: String = ""
) = GPUBindGroupLayoutDescriptor(entries.toJsArray(), label)

private fun GPUBindGroupLayoutDescriptor(
    entries: JsArray<GPUBindGroupLayoutEntry>,
    label: String
): GPUBindGroupLayoutDescriptor = js("""({
    entries: entries,
    label: label
})""")

sealed external interface GPUBindGroupLayoutEntry : JsAny
external interface GPUBindGroupLayoutEntryBuffer : GPUBindGroupLayoutEntry
external interface GPUBindGroupLayoutEntrySampler : GPUBindGroupLayoutEntry
external interface GPUBindGroupLayoutEntryTexture : GPUBindGroupLayoutEntry
external interface GPUBindGroupLayoutEntryStorageTexture : GPUBindGroupLayoutEntry
external interface GPUBindGroupLayoutEntryExternalTexture : GPUBindGroupLayoutEntry

fun GPUBindGroupLayoutEntryBuffer(
    binding: Int,
    visibility: Int,
    buffer: GPUBufferBindingLayout,
): GPUBindGroupLayoutEntryBuffer = js("""({
    binding: binding,
    visibility: visibility,
    buffer: buffer
})""")

fun GPUBindGroupLayoutEntrySampler(
    binding: Int,
    visibility: Int,
    sampler: GPUSamplerBindingLayout,
): GPUBindGroupLayoutEntrySampler = js("""({
    binding: binding,
    visibility: visibility,
    sampler: sampler
})""")

fun GPUBindGroupLayoutEntryTexture(
    binding: Int,
    visibility: Int,
    texture: GPUTextureBindingLayout,
): GPUBindGroupLayoutEntryTexture = js("""({
    binding: binding,
    visibility: visibility,
    texture: texture
})""")

fun GPUBindGroupLayoutEntryStorageTexture(
    binding: Int,
    visibility: Int,
    storageTexture: GPUStorageTextureBindingLayout,
): GPUBindGroupLayoutEntryStorageTexture = js("""({
    binding: binding,
    visibility: visibility,
    storageTexture: storageTexture
})""")

fun GPUBindGroupLayoutEntryExternalTexture(
    binding: Int,
    visibility: Int,
    externalTexture: GPUExternalTextureBindingLayout,
): GPUBindGroupLayoutEntryExternalTexture = js("""({
    binding: binding,
    visibility: visibility,
    externalTexture: externalTexture
})""")

external interface GPUBufferBindingLayout : JsAny

fun GPUBufferBindingLayout(
    type: GPUBufferBindingType = GPUBufferBindingType.uniform,
    hasDynamicOffset: Boolean = false,
    minBindingSize: Long = 0
): GPUBufferBindingLayout = GPUBufferBindingLayout(type.value, hasDynamicOffset, minBindingSize.toJsNumber())

private fun GPUBufferBindingLayout(
    type: String,
    hasDynamicOffset: Boolean,
    minBindingSize: JsNumber
): GPUBufferBindingLayout = js("""({
    type: type,
    hasDynamicOffset: hasDynamicOffset,
    minBindingSize: minBindingSize
})""")

external interface GPUSamplerBindingLayout : JsAny

fun GPUSamplerBindingLayout(
    type: GPUSamplerBindingType = GPUSamplerBindingType.filtering
): GPUSamplerBindingLayout = GPUSamplerBindingLayout(type.value)

private fun GPUSamplerBindingLayout(
    type: String
): GPUSamplerBindingLayout = js("""({
    type: type
})""")

external interface GPUTextureBindingLayout : JsAny

fun GPUTextureBindingLayout(
    sampleType: GPUTextureSampleType = GPUTextureSampleType.float,
    viewDimension: GPUTextureViewDimension = GPUTextureViewDimension.view2d,
    multisampled: Boolean = false
): GPUTextureBindingLayout = GPUTextureBindingLayout(sampleType.value, viewDimension.value, multisampled)

private fun GPUTextureBindingLayout(
    sampleType: String,
    viewDimension: String,
    multisampled: Boolean
): GPUTextureBindingLayout = js("""({
    sampleType: sampleType,
    viewDimension: viewDimension,
    multisampled: multisampled
})""")

external interface GPUStorageTextureBindingLayout : JsAny

fun GPUStorageTextureBindingLayout(
    access: GPUStorageTextureAccess,
    format: GPUTextureFormat,
    viewDimension: GPUTextureViewDimension
): GPUStorageTextureBindingLayout = GPUStorageTextureBindingLayout(access.value, format.value, viewDimension.value)

private fun GPUStorageTextureBindingLayout(
    access: String,
    format: String,
    viewDimension: String
): GPUStorageTextureBindingLayout = js("""({
    access: access,
    format: format,
    viewDimension: viewDimension
})""")

external interface GPUExternalTextureBindingLayout : JsAny

external interface GPUBindGroupDescriptor : JsAny

fun GPUBindGroupDescriptor(
    layout: GPUBindGroupLayout,
    entries: List<GPUBindGroupEntry>,
    label: String = ""
) = GPUBindGroupDescriptor(layout, entries.toJsArray(), label)

private fun GPUBindGroupDescriptor(
    layout: GPUBindGroupLayout,
    entries: JsArray<GPUBindGroupEntry>,
    label: String
): GPUBindGroupDescriptor = js("""({
    layout: layout,
    entries: entries,
    label: label
})""")

external interface GPUBindGroupEntry : JsAny

fun GPUBindGroupEntry(
    binding: Int,
    resource: GPUBindingResource
): GPUBindGroupEntry = js("""({
    binding: binding,
    resource: resource
})""")

external interface GPUBlendState : JsAny

fun GPUBlendState(
    color: GPUBlendComponent,
    alpha: GPUBlendComponent,
): GPUBlendState = js("""({
    color: color,
    alpha: alpha
})""")

external interface GPUBlendComponent : JsAny

fun GPUBlendComponent(
    operation: GPUBlendOperation = GPUBlendOperation.add,
    srcFactor: GPUBlendFactor = GPUBlendFactor.one,
    dstFactor: GPUBlendFactor = GPUBlendFactor.zero,
) = GPUBlendComponent(operation.value, srcFactor.value, dstFactor.value)

private fun GPUBlendComponent(
    operation: String,
    srcFactor: String,
    dstFactor: String,
): GPUBlendComponent = js("""({
    operation: operation,
    srcFactor: srcFactor,
    dstFactor: dstFactor
})""")

external interface GPUBufferBinding : GPUBindingResource

fun GPUBufferBinding(
    buffer: GPUBuffer,
    offset: JsNumber = 0.toJsNumber(),
) : GPUBindingResource = js("""({
    buffer: buffer,
    offset: offset
})""")

external interface GPUBufferDescriptor {
    val size: JsNumber
}

fun GPUBufferDescriptor(
    size: JsNumber,
    usage: Int,
    mappedAtCreation: Boolean = false,
    label: String = "",
): GPUBufferDescriptor = js("""({
    size: size,
    usage: usage,
    mappedAtCreation: mappedAtCreation,
    label: label
})""")

external interface GPUCanvasConfiguration : JsAny

fun GPUCanvasConfiguration(
    device: GPUDevice,
    format: GPUTextureFormat,
    usage: Int? = null,
    colorSpace: GPUPredefinedColorSpace? = null,
    alphaMode: GPUCanvasAlphaMode? = null
): GPUCanvasConfiguration = GPUCanvasConfiguration(device, format.value, usage, colorSpace?.value, alphaMode?.value)

private fun GPUCanvasConfiguration(
    device: GPUDevice,
    format: String,
    usage: Int?,
    colorSpace: String?,
    alphaMode: String?,
): GPUCanvasConfiguration = js("""({
    device: device,
    format: format,
    usage: (usage != null ? usage : undefined),
    colorSpace: (colorSpace != null ? colorSpace : undefined),
    alphaMode: (alphaMode != null ? alphaMode : undefined)
})""")

external interface GPUColorDict

fun GPUColorDict(
    r: Float,
    g: Float,
    b: Float,
    a: Float,
): GPUColorDict = js("({r: r, g: g, b: b, a: a})")

fun GPUColorDict(color: Color): GPUColorDict = GPUColorDict(color.r, color.g, color.b, color.a)

external interface GPUColorTargetState : JsAny

fun GPUColorTargetState(
    format: GPUTextureFormat,
    blend: GPUBlendState? = null
) : GPUColorTargetState = GPUColorTargetState(format.value, blend)

fun GPUColorTargetState(
    format: String,
    blend: GPUBlendState? = null,
) : GPUColorTargetState = js("""({
    format: format,
    blend: (blend != null ? blend : undefined)
})""")

external interface GPUComputePassDescriptor : JsAny

fun GPUComputePassDescriptor(
    label: String = "",
    timestampWrites: GPUComputePassTimestampWrites? = null
): GPUComputePassDescriptor = js("""({
    label: label,
    timestampWrites: (timestampWrites != null ? timestampWrites : undefined)
})""")

external interface GPUComputePipelineDescriptor : JsAny

fun GPUComputePipelineDescriptor(
    layout: GPUPipelineLayout,
    compute: GPUProgrammableStage,
    label: String = "",
): GPUComputePipelineDescriptor = js("""({
    layout: layout,
    compute: compute,
    label: label
})""")

external interface GPUFragmentState : JsAny

fun GPUFragmentState(
    module: GPUShaderModule,
    entryPoint: String,
    targets: List<GPUColorTargetState>,
) = GPUFragmentState(module, entryPoint, targets.toJsArray())

private fun GPUFragmentState(
    module: GPUShaderModule,
    entryPoint: String,
    targets: JsArray<GPUColorTargetState>,
): GPUFragmentState = js("""({
    module: module,
    entryPoint: entryPoint,
    targets: targets
})""")

external interface GPUImageCopyExternalImage : JsAny

fun GPUImageCopyExternalImage(
    source: ImageBitmap
): GPUImageCopyExternalImage = js("({source: source})")

external interface GPUImageCopyBuffer : JsAny

fun GPUImageCopyBuffer(
    buffer: GPUBuffer,
    bytesPerRow: Int,
    rowsPerImage: Int,
    offset: JsNumber = 0.toJsNumber()
) : GPUImageCopyBuffer = js("""({
    buffer: buffer,
    bytesPerRow: bytesPerRow,
    rowsPerImage: rowsPerImage,
    offset: offset
})""")

external interface GPUImageCopyTexture : JsAny

fun GPUImageCopyTexture(
    texture: GPUTexture,
    mipLevel: Int = 0,
    origin: IntArray = intArrayOf(0, 0, 0),
): GPUImageCopyTexture = GPUImageCopyTexture(texture, mipLevel, origin.toJsArray())

private fun GPUImageCopyTexture(
    texture: GPUTexture,
    mipLevel: Int,
    origin: JsArray<JsNumber>,
): GPUImageCopyTexture = js("""({
    texture: texture,
    mipLevel: mipLevel,
    origin: origin
})""")

external interface GPUImageCopyTextureTagged : JsAny

fun GPUImageCopyTextureTagged(
    texture: GPUTexture,
    mipLevel: Int = 0,
    origin: IntArray = intArrayOf(0, 0, 0),
    //aspect: GPUTextureAspect = 'all'
    //colorSpace: PredefinedColorSpace = 'srgb'
    //premultipliedAlpha: boolean = 'false'
): GPUImageCopyTextureTagged = GPUImageCopyTextureTagged(texture, mipLevel, origin.toJsArray())

private fun GPUImageCopyTextureTagged(
    texture: GPUTexture,
    mipLevel: Int,
    origin: JsArray<JsNumber>,
): GPUImageCopyTextureTagged = js("""({
    texture: texture,
    mipLevel: mipLevel,
    origin: origin
})""")

external interface GPUImageDataLayout : JsAny

fun GPUImageDataLayout(
    bytesPerRow: Int,
    rowsPerImage: Int,
    offset: JsNumber = 0.toJsNumber()
): GPUImageDataLayout = js("""({
    bytesPerRow: bytesPerRow,
    rowsPerImage: rowsPerImage,
    offset: offset
})""")

external interface GPUMultisampleState : JsAny

fun GPUMultisampleState(
    count: Int = 1,
    alphaToCoverageEnabled: Boolean = false
) : GPUMultisampleState = js("""({
    count: count,
    alphaToCoverageEnabled: alphaToCoverageEnabled
})""")

external interface GPUPipelineLayoutDescriptor : JsAny

fun GPUPipelineLayoutDescriptor(
    bindGroupLayouts: List<GPUBindGroupLayout>,
    label: String = ""
): GPUPipelineLayoutDescriptor = GPUPipelineLayoutDescriptor(bindGroupLayouts.toJsArray(), label)

fun GPUPipelineLayoutDescriptor(
    bindGroupLayouts: JsArray<GPUBindGroupLayout>,
    label: String
): GPUPipelineLayoutDescriptor = js("""({
    bindGroupLayouts: bindGroupLayouts,
    label: label
})""")

external interface GPUPrimitiveState : JsAny

fun GPUPrimitiveState(
    topology: GPUPrimitiveTopology,
    stripIndexFormat: GPUIndexFormat = GPUIndexFormat.uint32,
    frontFace: GPUFrontFace = GPUFrontFace.ccw,
    cullMode: GPUCullMode = GPUCullMode.none,
    unclippedDepth: Boolean = false
): GPUPrimitiveState {
    val stripFmt = if (topology == GPUPrimitiveTopology.triangleStrip || topology == GPUPrimitiveTopology.lineStrip) stripIndexFormat.value else null
    return GPUPrimitiveState(topology.value, stripFmt, frontFace.value, cullMode.value, unclippedDepth)
}

private fun GPUPrimitiveState(
    topology: String,
    stripIndexFormat: String?,
    frontFace: String,
    cullMode: String,
    unclippedDepth: Boolean
): GPUPrimitiveState = js("""({
    topology: topology,
    stripIndexFormat: (stripIndexFormat != null ? stripIndexFormat : undefined),
    frontFace: frontFace,
    cullMode: cullMode,
    unclippedDepth: unclippedDepth
})""")

external interface GPURenderPassColorAttachment : JsAny

fun GPURenderPassColorAttachment(
    view: GPUTextureView,
    clearValue: GPUColorDict? = null,
    resolveTarget: GPUTextureView? = null,
    loadOp: GPULoadOp = if (clearValue != null) GPULoadOp.clear else GPULoadOp.load,
    storeOp: GPUStoreOp = GPUStoreOp.store,
    label: String = ""
) : GPURenderPassColorAttachment = GPURenderPassColorAttachment(view, clearValue, resolveTarget, loadOp.value, storeOp.value, label)

private fun GPURenderPassColorAttachment(
    view: GPUTextureView,
    clearValue: GPUColorDict? = null,
    resolveTarget: GPUTextureView? = null,
    loadOp: String,
    storeOp: String,
    label: String = ""
) : GPURenderPassColorAttachment= js("""({
    view: view,
    loadOp: loadOp,
    storeOp: storeOp,
    label: label,
    clearValue: (clearValue != null ? clearValue : undefined),
    resolveTarget: (resolveTarget != null ? resolveTarget : undefined)
})""")

external interface GPURenderPassDepthStencilAttachment : JsAny

fun GPURenderPassDepthStencilAttachment(
    view: GPUTextureView,
    depthLoadOp: GPULoadOp,
    depthStoreOp: GPUStoreOp,
    depthClearValue: Float = 1f,
): GPURenderPassDepthStencilAttachment = GPURenderPassDepthStencilAttachment(view, depthLoadOp.value, depthStoreOp.value, depthClearValue)

private fun GPURenderPassDepthStencilAttachment(
    view: GPUTextureView,
    depthLoadOp: String,
    depthStoreOp: String,
    depthClearValue: Float,
): GPURenderPassDepthStencilAttachment = js("""({
    view: view,
    depthLoadOp: depthLoadOp,
    depthStoreOp: depthStoreOp,
    depthClearValue: depthClearValue
})""")

external interface GPURenderPassDescriptor : JsAny

fun GPURenderPassDescriptor(
    colorAttachments: List<GPURenderPassColorAttachment>,
    depthStencilAttachment: GPURenderPassDepthStencilAttachment? = null,
    timestampWrites: GPURenderPassTimestampWrites? = null,
    label: String = ""
): GPURenderPassDescriptor = GPURenderPassDescriptor(colorAttachments.toJsArray(), depthStencilAttachment, timestampWrites, label)

private fun GPURenderPassDescriptor(
    colorAttachments: JsArray<GPURenderPassColorAttachment>,
    depthStencilAttachment: GPURenderPassDepthStencilAttachment?,
    timestampWrites: GPURenderPassTimestampWrites?,
    label: String
): GPURenderPassDescriptor = js("""({
    colorAttachments: colorAttachments,
    depthStencilAttachment: (depthStencilAttachment != null ? depthStencilAttachment : undefined),
    timestampWrites: (timestampWrites != null ? timestampWrites : undefined),
    label: label
})""")

external interface GPUComputePassTimestampWrites : JsAny

fun GPUComputePassTimestampWrites(
    querySet: GPUQuerySet,
    beginningOfPassWriteIndex: Int,
    endOfPassWriteIndex: Int
): GPUComputePassTimestampWrites = js("""({
    querySet: querySet,
    beginningOfPassWriteIndex: beginningOfPassWriteIndex,
    endOfPassWriteIndex: endOfPassWriteIndex
})""")

external interface GPURenderPassTimestampWrites : JsAny

fun GPURenderPassTimestampWrites(
    querySet: GPUQuerySet,
    beginningOfPassWriteIndex: Int,
    endOfPassWriteIndex: Int
): GPURenderPassTimestampWrites = js("""({
    querySet: querySet,
    beginningOfPassWriteIndex: beginningOfPassWriteIndex,
    endOfPassWriteIndex: endOfPassWriteIndex
})""")

external interface GPURenderPipelineDescriptor : JsAny

fun GPURenderPipelineDescriptor(
    layout: JsAny, //GPUPipelineLayout
    vertex: GPUVertexState,
    fragment: GPUFragmentState? = null,
    depthStencil: GPUDepthStencilState? = null,
    primitive: GPUPrimitiveState? = null,
    multisample: GPUMultisampleState? = null,
    label: String = "",
): GPURenderPipelineDescriptor = js("""({
    layout: layout,
    vertex: vertex,
    fragment: (fragment != null ? fragment : undefined),
    depthStencil: (depthStencil != null ? depthStencil : undefined),
    primitive: (primitive != null ? primitive : undefined),
    multisample: (multisample != null ? multisample : undefined),
    label: label
})""")

external interface GPUDepthStencilState : JsAny

fun GPUDepthStencilState(
    format: GPUTextureFormat,
    depthWriteEnabled: Boolean,
    depthCompare: GPUCompareFunction,
    depthBias: Int = 0,
    depthBiasSlopeScale: Float = 0f,
    depthBiasClamp: Float = 0f
): GPUDepthStencilState = GPUDepthStencilState(format.value, depthWriteEnabled, depthCompare.value, depthBias, depthBiasSlopeScale, depthBiasClamp)

private fun GPUDepthStencilState(
    format: String,
    depthWriteEnabled: Boolean,
    depthCompare: String,
    depthBias: Int,
    depthBiasSlopeScale: Float,
    depthBiasClamp: Float
): GPUDepthStencilState = js("""({
    format: format,
    depthWriteEnabled: depthWriteEnabled,
    depthCompare: depthCompare,
    depthBias: depthBias,
    depthBiasSlopeScale: depthBiasSlopeScale,
    depthBiasClamp: depthBiasClamp
})""")

external interface GPUDeviceDescriptor : JsAny

fun GPUDeviceDescriptor(
    requiredFeatures: List<String>,
    label: String = "",
): GPUDeviceDescriptor = GPUDeviceDescriptor(requiredFeatures.toJsArray(), label)

private fun GPUDeviceDescriptor(
    requiredFeatures: JsArray<JsString>,
    label: String,
): GPUDeviceDescriptor = js("""({
    requiredFeatures: requiredFeatures,
    label: label
})""")

external interface GPUProgrammableStage : JsAny

fun GPUProgrammableStage(
    module: GPUShaderModule,
    entryPoint: String,
    // constants: Map<String, GPUPipelineConstantValue>
): GPUProgrammableStage = js("""({
    module: module,
    entryPoint: entryPoint
})""")

external interface GPUQuerySetDescriptor : JsAny

fun GPUQuerySetDescriptor(
    type: GPUQueryType,
    count: Int,
    label: String = "",
): GPUQuerySetDescriptor = GPUQuerySetDescriptor(type.value, count, label)

fun GPUQuerySetDescriptor(
    type: String,
    count: Int,
    label: String = "",
): GPUQuerySetDescriptor = js("""({
    type: type,
    count: count,
    label: label
})""")

external interface GPUShaderModuleDescriptor : JsAny

fun GPUShaderModuleDescriptor(
    code: String,
    label: String = ""
): GPUShaderModuleDescriptor = js("""({
    code: code,
    label: label
})""")

external interface GPUSamplerDescriptor : JsAny

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
): GPUSamplerDescriptor = GPUSamplerDescriptor(label, addressModeU.value, addressModeV.value, addressModeW.value, magFilter.value, minFilter.value, mipmapFilter.value, lodMinClamp, lodMaxClamp, maxAnisotropy, compare?.value)

fun GPUSamplerDescriptor(
    label: String,
    addressModeU: String,
    addressModeV: String,
    addressModeW: String,
    magFilter: String,
    minFilter: String,
    mipmapFilter: String,
    lodMinClamp: Float = 0f,
    lodMaxClamp: Float = 32f,
    maxAnisotropy: Int = 1,
    compare: String?,
): GPUSamplerDescriptor = js("""({
    label: label,
    addressModeU: addressModeU,
    addressModeV: addressModeV,
    addressModeW: addressModeW,
    magFilter: magFilter,
    minFilter: minFilter,
    mipmapFilter: mipmapFilter,
    lodMinClamp: lodMinClamp,
    lodMaxClamp: lodMaxClamp,
    maxAnisotropy: maxAnisotropy,
    compare: (compare != null ? compare : undefined)
})""")

external interface GPUTextureDescriptor : JsAny {
    val size: JsArray<JsNumber>
    val format: String
    val usage: Int
    val label: String
    val mipLevelCount: Int
    val sampleCount: Int
    val dimension: String
    val viewFormats: JsArray<JsString>
}

fun GPUTextureDescriptor(
    size: IntArray,
    format: GPUTextureFormat,
    usage: Int,
    label: String = "",
    mipLevelCount: Int = 1,
    sampleCount: Int = 1,
    dimension: GPUTextureDimension = GPUTextureDimension.texture2d,
    viewFormats: List<GPUTextureFormat> = emptyList(),
): GPUTextureDescriptor {
    val jsSize: JsArray<JsNumber> = size.toJsArray()
    val jsFormats: JsArray<JsString> = viewFormats.map { it.value }.toJsArray()
    return GPUTextureDescriptor(jsSize, format.value, usage, label, mipLevelCount, sampleCount, dimension.value, jsFormats)
}

private fun GPUTextureDescriptor(
    size: JsArray<JsNumber>,
    format: String,
    usage: Int,
    label: String,
    mipLevelCount: Int,
    sampleCount: Int,
    dimension: String,
    viewFormats: JsArray<JsString>,
): GPUTextureDescriptor = js("""({
    size: size,
    format: format,
    usage: usage,
    label: label,
    mipLevelCount: mipLevelCount,
    sampleCount: sampleCount,
    dimension: dimension,
    viewFormats: viewFormats
})""")

external interface GPUTextureViewDescriptor : JsAny

fun GPUTextureViewDescriptor(
    label: String = "",
    format: GPUTextureFormat? = null,
    dimension: GPUTextureViewDimension? = null,
    //val aspect: GPUTextureAspect = 'all'
    baseMipLevel: Int = 0,
    mipLevelCount: Int? = null,
    baseArrayLayer: Int = 0,
    arrayLayerCount: Int? = null
): GPUTextureViewDescriptor = GPUTextureViewDescriptor(label, format?.value, dimension?.value, baseMipLevel, mipLevelCount, baseArrayLayer, arrayLayerCount)

private fun GPUTextureViewDescriptor(
    label: String,
    format: String?,
    dimension: String?,
    baseMipLevel: Int,
    mipLevelCount: Int?,
    baseArrayLayer: Int,
    arrayLayerCount: Int?
): GPUTextureViewDescriptor = js("""({
    label: label,
    format: (format != null ? format : undefined),
    dimension: (dimension != null ? dimension : undefined),
    baseMipLevel: baseMipLevel,
    mipLevelCount: (mipLevelCount != null ? mipLevelCount : undefined),
    baseArrayLayer: baseArrayLayer,
    arrayLayerCount: (arrayLayerCount != null ? arrayLayerCount : undefined)
})""")

external interface GPUVertexAttribute : JsAny

fun GPUVertexAttribute(
    format: GPUVertexFormat,
    offset: Long,
    shaderLocation: Int
) = GPUVertexAttribute(format.value, offset.toJsNumber(), shaderLocation)

private fun GPUVertexAttribute(
    format: String,
    offset: JsNumber,
    shaderLocation: Int
): GPUVertexAttribute = js("""({
    format: format,
    offset: offset,
    shaderLocation: shaderLocation
})""")

external interface GPUVertexBufferLayout : JsAny

fun GPUVertexBufferLayout(
    arrayStride: Long,
    attributes: Array<GPUVertexAttribute>,
    stepMode: GPUVertexStepMode = GPUVertexStepMode.vertex
) = GPUVertexBufferLayout(arrayStride.toJsNumber(), attributes.toJsArray(), stepMode.value)

private fun GPUVertexBufferLayout(
    arrayStride: JsNumber,
    attributes: JsArray<GPUVertexAttribute>,
    stepMode: String
): GPUVertexBufferLayout = js("""({
    arrayStride: arrayStride,
    attributes: attributes,
    stepMode: stepMode
})""")

external interface GPUVertexState : JsAny

fun GPUVertexState(
    module: GPUShaderModule,
    entryPoint: String,
    buffers: List<GPUVertexBufferLayout> = emptyList(),
) = GPUVertexState(module, entryPoint, buffers.toJsArray())

private fun GPUVertexState(
    module: GPUShaderModule,
    entryPoint: String,
    buffers: JsArray<GPUVertexBufferLayout>,
): GPUVertexState = js("""({
    module: module,
    entryPoint: entryPoint,
    buffers: buffers
})""")

external interface GPURequestAdapterOptions : JsAny

fun GPURequestAdapterOptions(
    powerPreference: GPUPowerPreference,
    forceFallbackAdapter: Boolean = false,
): GPURequestAdapterOptions = GPURequestAdapterOptions(powerPreference.value, forceFallbackAdapter)

private fun GPURequestAdapterOptions(
    powerPreference: String,
    forceFallbackAdapter: Boolean,
): GPURequestAdapterOptions = js("""({
    powerPreference: powerPreference,
    forceFallbackAdapter: forceFallbackAdapter
})""")