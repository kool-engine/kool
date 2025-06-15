package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.AddressMode
import de.fabmax.kool.pipeline.TextureSampleType
import de.fabmax.kool.scene.geometry.PrimitiveType
import io.ygdrasil.webgpu.*

fun GPUCommandEncoder.beginRenderPass(
    colorAttachments: List<GPURenderPassColorAttachment>,
    depthStencilAttachment: GPURenderPassDepthStencilAttachment? = null,
    timestampWrites: GPURenderPassTimestampWrites? = null,
    label: String = ""
) = beginRenderPass(RenderPassDescriptor(colorAttachments, depthStencilAttachment, timestampWrites = timestampWrites, label = label))

fun GPUDevice.createBindGroup(
    layout: GPUBindGroupLayout,
    entries: List<GPUBindGroupEntry>,
    label: String = ""
) = createBindGroup(BindGroupDescriptor(layout, entries, label))

fun GPUDevice.createBindGroupLayout(
    entries: List<GPUBindGroupLayoutEntry>,
    label: String = ""
) = createBindGroupLayout(
    BindGroupLayoutDescriptor(
        entries = entries,
        label = label
    )
)

fun GPUDevice.createSampler(
    label: String = "",
    addressModeU: GPUAddressMode = GPUAddressMode.ClampToEdge,
    addressModeV: GPUAddressMode = GPUAddressMode.ClampToEdge,
    addressModeW: GPUAddressMode = GPUAddressMode.ClampToEdge,
    magFilter: GPUFilterMode = GPUFilterMode.Nearest,
    minFilter: GPUFilterMode = GPUFilterMode.Nearest,
    mipmapFilter: GPUMipmapFilterMode = GPUMipmapFilterMode.Nearest,
    lodMinClamp: Float = 0f,
    lodMaxClamp: Float = 32f,
    maxAnisotropy: Int = 1,
    compare: GPUCompareFunction? = null
): GPUSampler = createSampler(
    SamplerDescriptor(
        label = label,
        addressModeU = addressModeU,
        addressModeV = addressModeV,
        addressModeW = addressModeW,
        magFilter = magFilter,
        minFilter = minFilter,
        mipmapFilter = mipmapFilter,
        lodMinClamp = lodMinClamp,
        lodMaxClamp = lodMaxClamp,
        maxAnisotropy = maxAnisotropy.toUShort(),
        compare = compare
    )
)

fun GPUDevice.createComputePipeline(
    layout: GPUPipelineLayout,
    compute: GPUProgrammableStage,
    label: String = ""
) = createComputePipeline(
    ComputePipelineDescriptor(
        layout = layout,
        compute = compute,
        label = label
    )
)

fun GPUDevice.createRenderPipeline(
    layout: GPUPipelineLayout,
    vertex: GPUVertexState,
    fragment: GPUFragmentState? = null,
    depthStencil: GPUDepthStencilState? = null,
    primitive: GPUPrimitiveState? = null,
    multisample: GPUMultisampleState? = null,
    label: String = ""
) = createRenderPipeline(
    RenderPipelineDescriptor(
        layout = layout,
        vertex = vertex,
        fragment = fragment,
        depthStencil = depthStencil,
        primitive = primitive ?: PrimitiveState(),
        multisample = multisample ?: MultisampleState(),
        label = label
    )
)

fun GPUDevice.createShaderModule(code: String) = createShaderModule(ShaderModuleDescriptor(code))

fun GPUTexture.createView(
    label: String = "",
    format: GPUTextureFormat? = null,
    dimension: GPUTextureViewDimension? = null,
    baseMipLevel: Int = 0,
    mipLevelCount: Int? = null,
    baseArrayLayer: Int = 0,
    arrayLayerCount: Int? = null
) = createView(
    TextureViewDescriptor(
        label = label,
        format = format,
        dimension = dimension,
        baseMipLevel = baseMipLevel.toUInt(),
        mipLevelCount = mipLevelCount?.toUInt(),
        baseArrayLayer = baseArrayLayer.toUInt(),
        arrayLayerCount = arrayLayerCount?.toUInt() ?: dimension.let {
            when (it) {
                GPUTextureViewDimension.Cube -> 6u
                else -> null
            }
        }
    )
)

val AddressMode.wgpu: GPUAddressMode
    get() = when (this) {
        AddressMode.CLAMP_TO_EDGE -> GPUAddressMode.ClampToEdge
        AddressMode.MIRRORED_REPEAT -> GPUAddressMode.MirrorRepeat
        AddressMode.REPEAT -> GPUAddressMode.Repeat
    }

val CullMethod.wgpu: GPUCullMode
    get() = when (this) {
        CullMethod.CULL_BACK_FACES -> GPUCullMode.Back
        CullMethod.CULL_FRONT_FACES -> GPUCullMode.Front
        CullMethod.NO_CULLING -> GPUCullMode.None
    }

val DepthCompareOp.wgpu: GPUCompareFunction
    get() = when (this) {
        DepthCompareOp.ALWAYS -> GPUCompareFunction.Always
        DepthCompareOp.NEVER -> GPUCompareFunction.Never
        DepthCompareOp.LESS -> GPUCompareFunction.Less
        DepthCompareOp.LESS_EQUAL -> GPUCompareFunction.LessEqual
        DepthCompareOp.GREATER -> GPUCompareFunction.Greater
        DepthCompareOp.GREATER_EQUAL -> GPUCompareFunction.GreaterEqual
        DepthCompareOp.EQUAL -> GPUCompareFunction.Equal
        DepthCompareOp.NOT_EQUAL -> GPUCompareFunction.NotEqual
    }

val FilterMethod.wgpu: GPUFilterMode
    get() = when (this) {
        FilterMethod.NEAREST -> GPUFilterMode.Nearest
        FilterMethod.LINEAR -> GPUFilterMode.Linear
    }

val PrimitiveType.wgpu: GPUPrimitiveTopology
    get() = when (this) {
        PrimitiveType.LINES -> GPUPrimitiveTopology.LineList
        PrimitiveType.POINTS -> GPUPrimitiveTopology.PointList
        PrimitiveType.TRIANGLES -> GPUPrimitiveTopology.TriangleList
        PrimitiveType.TRIANGLE_STRIP -> GPUPrimitiveTopology.TriangleStrip
    }

val TexFormat.wgpu: GPUTextureFormat
    get() = when (this) {
        TexFormat.R -> GPUTextureFormat.R8Unorm
        TexFormat.RG -> GPUTextureFormat.RG8Unorm
        TexFormat.RGBA -> GPUTextureFormat.RGBA8Unorm
        TexFormat.R_F16 -> GPUTextureFormat.R16Float
        TexFormat.RG_F16 -> GPUTextureFormat.RG16Float
        TexFormat.RGBA_F16 -> GPUTextureFormat.RGBA16Float
        TexFormat.R_F32 -> GPUTextureFormat.R32Float
        TexFormat.RG_F32 -> GPUTextureFormat.RG32Float
        TexFormat.RGBA_F32 -> GPUTextureFormat.RGBA32Float
        TexFormat.R_I32 -> GPUTextureFormat.R32Sint
        TexFormat.RG_I32 -> GPUTextureFormat.RG32Sint
        TexFormat.RGBA_I32 -> GPUTextureFormat.RGBA32Sint
        TexFormat.R_U32 -> GPUTextureFormat.R32Uint
        TexFormat.RG_U32 -> GPUTextureFormat.RG32Uint
        TexFormat.RGBA_U32 -> GPUTextureFormat.RGBA32Uint
        TexFormat.RG11B10_F -> GPUTextureFormat.RG11B10Ufloat
    }

val TexFormat.wgpuStorage: GPUTextureFormat
    get() = when (this) {
        TexFormat.R -> GPUTextureFormat.R8Unorm
        TexFormat.RG -> GPUTextureFormat.RG8Unorm
        TexFormat.RGBA -> GPUTextureFormat.RGBA8Unorm
        TexFormat.R_F16 -> GPUTextureFormat.R16Float
        TexFormat.RG_F16 -> GPUTextureFormat.RG16Float
        TexFormat.RGBA_F16 -> GPUTextureFormat.RGBA16Float
        TexFormat.R_F32 -> GPUTextureFormat.R32Float
        TexFormat.RG_F32 -> GPUTextureFormat.RG32Float
        TexFormat.RGBA_F32 -> GPUTextureFormat.RGBA32Float
        TexFormat.R_I32 -> GPUTextureFormat.R32Sint
        TexFormat.RG_I32 -> GPUTextureFormat.RG32Sint
        TexFormat.RGBA_I32 -> GPUTextureFormat.RGBA32Sint
        TexFormat.R_U32 -> GPUTextureFormat.R32Uint
        TexFormat.RG_U32 -> GPUTextureFormat.RG32Uint
        TexFormat.RGBA_U32 -> GPUTextureFormat.RGBA32Uint
        TexFormat.RG11B10_F -> GPUTextureFormat.RGBA16Float
    }

val TextureSampleType.wgpu: GPUTextureSampleType
    get() = when (this) {
        TextureSampleType.FLOAT -> GPUTextureSampleType.Float
        TextureSampleType.UNFILTERABLE_FLOAT -> GPUTextureSampleType.UnfilterableFloat
        TextureSampleType.DEPTH -> GPUTextureSampleType.Depth
    }