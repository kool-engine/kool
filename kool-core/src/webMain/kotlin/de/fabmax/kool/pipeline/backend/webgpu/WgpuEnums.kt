package de.fabmax.kool.pipeline.backend.webgpu

interface JsValueEnum {
    val value: String
}

enum class GPUAddressMode(override val value: String) : JsValueEnum {
    clampToEdge("clamp-to-edge"),
    repeat("repeat"),
    mirrorRepeat("mirror-repeat"),
}

enum class GPUAutoLayoutMode(override val value: String) : JsValueEnum {
    auto("auto"),
}

enum class GPUBlendFactor(override val value: String) : JsValueEnum {
    zero("zero"),
    one("one"),
    src("src"),
    oneMinusSrc("one-minus-src"),
    srcAlpha("src-alpha"),
    oneMinusSrcAlpha("one-minus-src-alpha"),
    dst("dst"),
    oneMinusDst("one-minus-dst"),
    dstAlpha("dst-alpha"),
    oneMinusDstAlpha("one-minus-dst-alpha"),
    srcAlphaSaturated("src-alpha-saturated"),
    constant("constant"),
    oneMinusConstant("one-minus-constant"),
}

enum class GPUBlendOperation(override val value: String) : JsValueEnum {
    add("add"),
    subtract("subtract"),
    reverseSubtract("reverse-subtract"),
    min("min"),
    max("max"),
}

enum class GPUBufferBindingType(override val value: String) : JsValueEnum {
    uniform("uniform"),
    storage("storage"),
    readOnlyStorage("read-only-storage"),
}

enum class GPUCanvasAlphaMode(override val value: String) : JsValueEnum {
    opaque("opaque"),
    premultiplied("premultiplied"),
}

enum class GPUCompareFunction(override val value: String) : JsValueEnum {
    never("never"),
    less("less"),
    equal("equal"),
    lessEqual("less-equal"),
    greater("greater"),
    notEqual("not-equal"),
    greaterEqual("greater-equal"),
    always("always"),
}

enum class GPUCullMode(override val value: String) : JsValueEnum {
    none("none"),
    front("front"),
    back("back"),
}

enum class GPUFilterMode(override val value: String) : JsValueEnum {
    nearest("nearest"),
    linear("linear"),
}

enum class GPUFrontFace(override val value: String) : JsValueEnum {
    ccw("ccw"),
    cw("cw"),
}

enum class GPUMipmapFilterMode(override val value: String) : JsValueEnum {
    nearest("nearest"),
    linear("linear"),
}

enum class GPUIndexFormat(override val value: String) : JsValueEnum {
    uint16("uint16"),
    uint32("uint32"),
}

enum class GPULoadOp(override val value: String) : JsValueEnum {
    load("load"),
    clear("clear"),
}

enum class GPUPredefinedColorSpace(override val value: String) : JsValueEnum {
    srgb("srgb"),
}

enum class GPUPrimitiveTopology(override val value: String) : JsValueEnum {
    pointList("point-list"),
    lineList("line-list"),
    lineStrip("line-strip"),
    triangleList("triangle-list"),
    triangleStrip("triangle-strip"),
}

enum class GPUPowerPreference(override val value: String) : JsValueEnum {
    lowPower("low-power"),
    highPerformance("high-performance"),
}

enum class GPUQueryType(override val value: String) : JsValueEnum {
    timestamp("timestamp"),
    occlusion("occlusion"),
}

enum class GPUSamplerBindingType(override val value: String) : JsValueEnum {
    filtering("filtering"),
    nonFiltering("non-filtering"),
    comparison("comparison"),
}

enum class GPUStoreOp(override val value: String) : JsValueEnum {
    store("store"),
    discard("discard"),
}

enum class GPUTextureDimension(override val value: String) : JsValueEnum {
    texture1d("1d"),
    texture2d("2d"),
    texture3d("3d"),
}

enum class GPUTextureFormat(override val value: String) : JsValueEnum {
    r8unorm("r8unorm"),
    r8snorm("r8snorm"),
    r8uint("r8uint"),
    r8sint("r8sint"),
    r16uint("r16uint"),
    r16sint("r16sint"),
    r16float("r16float"),
    rg8unorm("rg8unorm"),
    rg8snorm("rg8snorm"),
    rg8uint("rg8uint"),
    rg8sint("rg8sint"),
    r32uint("r32uint"),
    r32sint("r32sint"),
    r32float("r32float"),
    rg16uint("rg16uint"),
    rg16sint("rg16sint"),
    rg16float("rg16float"),
    rgba8unorm("rgba8unorm"),
    rgba8unormSrgb("rgba8unorm-srgb"),
    rgba8snorm("rgba8snorm"),
    rgba8uint("rgba8uint"),
    rgba8sint("rgba8sint"),
    bgra8unorm("bgra8unorm"),
    bgra8unormSrgb("bgra8unorm-srgb"),
    rgb9e5ufloat("rgb9e5ufloat"),
    rgb10a2uint("rgb10a2uint"),
    rgb10a2unorm("rgb10a2unorm"),
    rg11b10ufloat("rg11b10ufloat"),
    rg32uint("rg32uint"),
    rg32sint("rg32sint"),
    rg32float("rg32float"),
    rgba16uint("rgba16uint"),
    rgba16sint("rgba16sint"),
    rgba16float("rgba16float"),
    rgba32uint("rgba32uint"),
    rgba32sint("rgba32sint"),
    rgba32float("rgba32float"),
    stencil8("stencil8"),
    depth16unorm("depth16unorm"),
    depth24plus("depth24plus"),
    depth24plusStencil8("depth24plus-stencil8"),
    depth32float("depth32float"),
    depth32floatStencil8("depth32float-stencil8");
    // todo: many more compressed formats...

    companion object {
        fun forValue(value: String) = entries.first { it.value == value }
    }
}

enum class GPUTextureSampleType(override val value: String) : JsValueEnum {
    float("float"),
    unfilterableFloat("unfilterable-float"),
    depth("depth"),
    sint("sint"),
    uint("uint"),
}

enum class GPUStorageTextureAccess(override val value: String) : JsValueEnum {
    writeOnly("write-only"),
    readOnly("read-only"),
    readWrite("read-write"),
}

enum class GPUTextureViewDimension(override val value: String) : JsValueEnum {
    view1d("1d"),
    view2d("2d"),
    view2dArray("2d-array"),
    viewCube("cube"),
    viewCubeArray("cube-array"),
    view3d("3d"),
}

enum class GPUVertexFormat(override val value: String) : JsValueEnum {
    uint8x2("uint8x2"),
    uint8x4("uint8x4"),
    sint8x2("sint8x2"),
    sint8x4("sint8x4"),
    unorm8x2("unorm8x2"),
    unorm8x4("unorm8x4"),
    snorm8x2("snorm8x2"),
    snorm8x4("snorm8x4"),
    uint16x2("uint16x2"),
    uint16x4("uint16x4"),
    sint16x2("sint16x2"),
    sint16x4("sint16x4"),
    unorm16x2("unorm16x2"),
    unorm16x4("unorm16x4"),
    snorm16x2("snorm16x2"),
    snorm16x4("snorm16x4"),
    float16x2("float16x2"),
    float16x4("float16x4"),
    float32("float32"),
    float32x2("float32x2"),
    float32x3("float32x3"),
    float32x4("float32x4"),
    uint32("uint32"),
    uint32x2("uint32x2"),
    uint32x3("uint32x3"),
    uint32x4("uint32x4"),
    sint32("sint32"),
    sint32x2("sint32x2"),
    sint32x3("sint32x3"),
    sint32x4("sint32x4"),
}

enum class GPUVertexStepMode(override val value: String) : JsValueEnum {
    vertex("vertex"),
    instance("instance"),
}
