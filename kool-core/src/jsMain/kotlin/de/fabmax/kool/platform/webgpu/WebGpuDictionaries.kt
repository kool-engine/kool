package de.fabmax.kool.platform.webgpu

class GPUBindGroupLayoutDescriptor(
    @JsName("entries")
    val entries: Array<GPUBindGroupLayoutEntry>,
)

class GPUBindGroupLayoutEntry(
    @JsName("binding")
    val binding: Int,
    @JsName("visibility")
    val visibility: Int,
    @JsName("buffer")
    val buffer: GPUBufferBindingLayout,
//    @JsName("sampler")
//    val sampler: GPUSamplerBindingLayout? = null,
//    @JsName("texture")
//    val texture: GPUTextureBindingLayout? = null,
//    @JsName("storageTexture")
//    val storageTexture: GPUStorageTextureBindingLayout? = null,
//    @JsName("externalTexture")
//    val externalTexture: GPUExternalTextureBindingLayout? = null,
)

class GPUBufferBindingLayout(
    type: GPUBufferBindingType,
    @JsName("hasDynamicOffset")
    val hasDynamicOffset: Boolean = false,
    @JsName("minBindingSize")
    val minBindingSize: Long = 0
) {
    @JsName("type")
    val type: String = type.typeName
}

class GPUSamplerBindingLayout

class GPUTextureBindingLayout

class GPUStorageTextureBindingLayout

class GPUExternalTextureBindingLayout

class GPUBindGroupDescriptor(
    @JsName("layout")
    val layout: GPUBindGroupLayout,
    @JsName("entries")
    val entries: Array<GPUBindGroupEntry>
)

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

class GPUBufferDescriptor(
    @JsName("size")
    val size: Long,
    @JsName("usage")
    val usage: Int,
    @JsName("mappedAtCreation")
    val mappedAtCreation: Boolean = true,
)

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
    val r: Double,
    @JsName("g")
    val g: Double,
    @JsName("b")
    val b: Double,
    @JsName("a")
    val a: Double,
)

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

class GPUMultisampleState(
    @JsName("count")
    val count: Int,
)

class GPUPipelineLayoutDescriptor(
    @JsName("bindGroupLayouts")
    val bindGroupLayouts: Array<GPUBindGroupLayout>,
)

class GPUPrimitiveState(topology: GPUPrimitiveTopology) {
    @JsName("topology")
    val topology: String = topology.topoName
}

class GPURenderPassColorAttachment(
    @JsName("view")
    val view: GPUTextureView,
    @JsName("resolveTarget")
    val resolveTarget: GPUTextureView,
    @JsName("clearValue")
    val clearValue: GPUColorDict,
    @JsName("loadOp")
    val loadOp: GPULoadOp,
    @JsName("storeOp")
    val storeOp: GPUStoreOp,
)

class GPURenderPassDescriptor(
    @JsName("colorAttachments")
    val colorAttachments: Array<GPURenderPassColorAttachment>,
)

class GPURenderPipelineDescriptor private constructor(
    @JsName("layout")
    val layout: Any,
    @JsName("fragment")
    val fragment: GPUFragmentState,
    @JsName("vertex")
    val vertex: GPUVertexState,
    @JsName("primitive")
    val primitive: GPUPrimitiveState,
    @JsName("multisample")
    val multisample: GPUMultisampleState
) {
    constructor(layout: GPUAutoLayoutMode,
                vertex: GPUVertexState,
                primitive: GPUPrimitiveState,
                multisample: GPUMultisampleState,
                fragment: GPUFragmentState
    ) : this(layout, fragment, vertex, primitive, multisample)

    constructor(layout: GPUPipelineLayout,
                vertex: GPUVertexState,
                primitive: GPUPrimitiveState,
                multisample: GPUMultisampleState,
                fragment: GPUFragmentState
    ) : this(layout, fragment, vertex, primitive, multisample)
}

class GPUShaderModuleDescriptor(
    @JsName("code")
    val code: String
)

class GPUTextureDescriptor(
    @JsName("size")
    val size: IntArray,
    @JsName("sampleCount")
    val sampleCount: Int,
    @JsName("format")
    val format: GPUTextureFormat,
    @JsName("usage")
    val usage: Int,
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
