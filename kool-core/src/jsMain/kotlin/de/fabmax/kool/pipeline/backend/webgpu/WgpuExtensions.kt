package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.geometry.PrimitiveType

fun GPUCommandEncoder.beginRenderPass(
    colorAttachments: Array<GPURenderPassColorAttachment>,
    depthStencilAttachment: GPURenderPassDepthStencilAttachment? = null,
    timestampWrites: GPURenderPassTimestampWrites? = null,
    label: String = ""
) = beginRenderPass(GPURenderPassDescriptor(colorAttachments, depthStencilAttachment, timestampWrites, label))

fun GPUDevice.createBindGroup(
    layout: GPUBindGroupLayout,
    entries: Array<GPUBindGroupEntry>,
    label: String = ""
) = createBindGroup(GPUBindGroupDescriptor(layout, entries, label))

fun GPUDevice.createBindGroupLayout(
    entries: Array<GPUBindGroupLayoutEntry>,
    label: String = ""
) = createBindGroupLayout(GPUBindGroupLayoutDescriptor(
    entries = entries,
    label = label
))

fun GPUDevice.createSampler(
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
    compare: GPUCompareFunction? = null
): GPUSampler = createSampler(GPUSamplerDescriptor(
    label = label,
    addressModeU = addressModeU,
    addressModeV = addressModeV,
    addressModeW = addressModeW,
    magFilter = magFilter,
    minFilter = minFilter,
    mipmapFilter = mipmapFilter,
    lodMinClamp = lodMinClamp,
    lodMaxClamp = lodMaxClamp,
    maxAnisotropy = maxAnisotropy,
    compare = compare
))

fun GPUDevice.createComputePipeline(
    layout: GPUPipelineLayout,
    compute: GPUProgrammableStage,
    label: String = ""
) = createComputePipeline(GPUComputePipelineDescriptor(
    layout = layout,
    compute = compute,
    label = label
))

fun GPUDevice.createRenderPipeline(
    layout: GPUPipelineLayout,
    vertex: GPUVertexState,
    fragment: GPUFragmentState? = null,
    depthStencil: GPUDepthStencilState? = null,
    primitive: GPUPrimitiveState? = null,
    multisample: GPUMultisampleState? = null,
    label: String = ""
) = createRenderPipeline(GPURenderPipelineDescriptor(
    layout = layout,
    vertex = vertex,
    fragment = fragment,
    depthStencil = depthStencil,
    primitive = primitive,
    multisample = multisample,
    label = label
))

fun GPUDevice.createShaderModule(code: String) = createShaderModule(GPUShaderModuleDescriptor(code))

fun GPUTexture.createView(
    label: String = "",
    format: GPUTextureFormat? = null,
    dimension: GPUTextureViewDimension? = null,
    baseMipLevel: Int = 0,
    mipLevelCount: Int? = null,
    baseArrayLayer: Int = 0,
    arrayLayerCount: Int? = null
) = createView(GPUTextureViewDescriptor(
    label = label,
    format = format,
    dimension = dimension,
    baseMipLevel = baseMipLevel,
    mipLevelCount = mipLevelCount,
    baseArrayLayer = baseArrayLayer,
    arrayLayerCount = arrayLayerCount
))

val AddressMode.wgpu: GPUAddressMode
    get() = when (this) {
        AddressMode.CLAMP_TO_EDGE -> GPUAddressMode.clampToEdge
        AddressMode.MIRRORED_REPEAT -> GPUAddressMode.mirrorRepeat
        AddressMode.REPEAT -> GPUAddressMode.repeat
    }

val CullMethod.wgpu: GPUCullMode
    get() = when (this) {
        CullMethod.CULL_BACK_FACES -> GPUCullMode.back
        CullMethod.CULL_FRONT_FACES -> GPUCullMode.front
        CullMethod.NO_CULLING -> GPUCullMode.none
    }

val DepthCompareOp.wgpu: GPUCompareFunction
    get() = when (this) {
        DepthCompareOp.ALWAYS -> GPUCompareFunction.always
        DepthCompareOp.NEVER -> GPUCompareFunction.never
        DepthCompareOp.LESS -> GPUCompareFunction.less
        DepthCompareOp.LESS_EQUAL -> GPUCompareFunction.lessEqual
        DepthCompareOp.GREATER -> GPUCompareFunction.greater
        DepthCompareOp.GREATER_EQUAL -> GPUCompareFunction.greaterEqual
        DepthCompareOp.EQUAL -> GPUCompareFunction.equal
        DepthCompareOp.NOT_EQUAL -> GPUCompareFunction.notEqual
    }

val FilterMethod.wgpu: GPUFilterMode
    get() = when (this) {
        FilterMethod.NEAREST -> GPUFilterMode.nearest
        FilterMethod.LINEAR -> GPUFilterMode.linear
    }

val PrimitiveType.wgpu: GPUPrimitiveTopology
    get() = when (this) {
        PrimitiveType.LINES -> GPUPrimitiveTopology.lineList
        PrimitiveType.POINTS -> GPUPrimitiveTopology.pointList
        PrimitiveType.TRIANGLES -> GPUPrimitiveTopology.triangleList
        PrimitiveType.TRIANGLE_STRIP -> GPUPrimitiveTopology.triangleStrip
    }

val TexFormat.wgpu: GPUTextureFormat
    get() = when (this) {
        TexFormat.R -> GPUTextureFormat.r8unorm
        TexFormat.RG -> GPUTextureFormat.rg8unorm
        TexFormat.RGBA -> GPUTextureFormat.rgba8unorm
        TexFormat.R_F16 -> GPUTextureFormat.r16float
        TexFormat.RG_F16 -> GPUTextureFormat.rg16float
        TexFormat.RGBA_F16 -> GPUTextureFormat.rgba16float
        TexFormat.R_F32 -> GPUTextureFormat.r32float
        TexFormat.RG_F32 -> GPUTextureFormat.rg32float
        TexFormat.RGBA_F32 -> GPUTextureFormat.rgba32float
        TexFormat.R_I32 -> GPUTextureFormat.r32sint
        TexFormat.RG_I32 -> GPUTextureFormat.rg32sint
        TexFormat.RGBA_I32 -> GPUTextureFormat.rgba32sint
        TexFormat.R_U32 -> GPUTextureFormat.r32uint
        TexFormat.RG_U32 -> GPUTextureFormat.rg32uint
        TexFormat.RGBA_U32 -> GPUTextureFormat.rgba32uint
        TexFormat.RG11B10_F -> GPUTextureFormat.rg11b10ufloat
    }

val TexFormat.wgpuStorage: GPUTextureFormat
    get() = when (this) {
        TexFormat.R -> GPUTextureFormat.r8unorm
        TexFormat.RG -> GPUTextureFormat.rg8unorm
        TexFormat.RGBA -> GPUTextureFormat.rgba8unorm
        TexFormat.R_F16 -> GPUTextureFormat.r16float
        TexFormat.RG_F16 -> GPUTextureFormat.rg16float
        TexFormat.RGBA_F16 -> GPUTextureFormat.rgba16float
        TexFormat.R_F32 -> GPUTextureFormat.r32float
        TexFormat.RG_F32 -> GPUTextureFormat.rg32float
        TexFormat.RGBA_F32 -> GPUTextureFormat.rgba32float
        TexFormat.R_I32 -> GPUTextureFormat.r32sint
        TexFormat.RG_I32 -> GPUTextureFormat.rg32sint
        TexFormat.RGBA_I32 -> GPUTextureFormat.rgba32sint
        TexFormat.R_U32 -> GPUTextureFormat.r32uint
        TexFormat.RG_U32 -> GPUTextureFormat.rg32uint
        TexFormat.RGBA_U32 -> GPUTextureFormat.rgba32uint
        TexFormat.RG11B10_F -> GPUTextureFormat.rgba16float
    }

val TextureSampleType.wgpu: GPUTextureSampleType
    get() = when (this) {
        TextureSampleType.FLOAT -> GPUTextureSampleType.float
        TextureSampleType.UNFILTERABLE_FLOAT -> GPUTextureSampleType.unfilterableFloat
        TextureSampleType.DEPTH -> GPUTextureSampleType.depth
    }