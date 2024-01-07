package de.fabmax.kool.pipeline.backend.webgpu

value class GPUBufferBindingType(val enumValue: String) {
    companion object {
        val uniform = GPUBufferBindingType("uniform")
        val storage = GPUBufferBindingType("storage")
        val readOnlyStorage = GPUBufferBindingType("read-only-storage")
    }
}

value class GPUCanvasAlphaMode(val enumValue: String) {
    companion object {
        val opaque = GPUCanvasAlphaMode("opaque")
        val premultiplied = GPUCanvasAlphaMode("premultiplied")
    }
}

value class GPUCompareFunction(val funcName: String) {
    companion object {
        val never = GPUCompareFunction("never")
        val less = GPUCompareFunction("less")
        val equal = GPUCompareFunction("equal")
        val lessEqual = GPUCompareFunction("less-equal")
        val greater = GPUCompareFunction("greater")
        val notEqual = GPUCompareFunction("not-equal")
        val greaterEqual = GPUCompareFunction("greater-equal")
        val always = GPUCompareFunction("always")
    }
}

value class GPUIndexFormat(val enumValue: String) {
    companion object {
        val uint16 = GPUIndexFormat("uint16")
        val uint32 = GPUIndexFormat("uint32")
    }
}

value class GPULoadOp(val enumValue: String) {
    companion object {
        val load = GPULoadOp("load")
        val clear = GPULoadOp("clear")
    }
}

value class GPUPredefinedColorSpace(val enumValue: String) {
    companion object {
        val srgb = GPUPredefinedColorSpace("srgb")
    }
}

value class GPUPrimitiveTopology(val enumValue: String) {
    companion object {
        val pointList = GPUPrimitiveTopology("point-list")
        val lineList = GPUPrimitiveTopology("line-list")
        val lineStrip = GPUPrimitiveTopology("line-strip")
        val triangleList = GPUPrimitiveTopology("triangle-list")
        val triangleStrip = GPUPrimitiveTopology("triangle-strip")
    }
}

value class GPUStoreOp(val enumValue: String) {
    companion object {
        val store = GPUStoreOp("store")
        val discard = GPUStoreOp("discard")
    }
}

value class GPUTextureFormat(val enumValue: String) {
    companion object {
        val r8unorm = GPUTextureFormat("r8unorm")
        val r8snorm = GPUTextureFormat("r8snorm")
        val r8uint = GPUTextureFormat("r8uint")
        val r8sint = GPUTextureFormat("r8sint")
        val r16uint = GPUTextureFormat("r16uint")
        val r16sint = GPUTextureFormat("r16sint")
        val r16float = GPUTextureFormat("r16float")
        val rg8unorm = GPUTextureFormat("rg8unorm")
        val rg8snorm = GPUTextureFormat("rg8snorm")
        val rg8uint = GPUTextureFormat("rg8uint")
        val rg8sint = GPUTextureFormat("rg8sint")
        val r32uint = GPUTextureFormat("r32uint")
        val r32sint = GPUTextureFormat("r32sint")
        val r32float = GPUTextureFormat("r32float")
        val rg16uint = GPUTextureFormat("rg16uint")
        val rg16sint = GPUTextureFormat("rg16sint")
        val rg16float = GPUTextureFormat("rg16float")
        val rgba8unorm = GPUTextureFormat("rgba8unorm")
        val rgba8unormSrgb = GPUTextureFormat("rgba8unorm-srgb")
        val rgba8snorm = GPUTextureFormat("rgba8snorm")
        val rgba8uint = GPUTextureFormat("rgba8uint")
        val rgba8sint = GPUTextureFormat("rgba8sint")
        val bgra8unorm = GPUTextureFormat("bgra8unorm")
        val bgra8unormSrgb = GPUTextureFormat("bgra8unorm-srgb")
        val rgb9e5ufloat = GPUTextureFormat("rgb9e5ufloat")
        val rgb10a2uint = GPUTextureFormat("rgb10a2uint")
        val rgb10a2unorm = GPUTextureFormat("rgb10a2unorm")
        val rg11b10ufloat = GPUTextureFormat("rg11b10ufloat")
        val rg32uint = GPUTextureFormat("rg32uint")
        val rg32sint = GPUTextureFormat("rg32sint")
        val rg32float = GPUTextureFormat("rg32float")
        val rgba16uint = GPUTextureFormat("rgba16uint")
        val rgba16sint = GPUTextureFormat("rgba16sint")
        val rgba16float = GPUTextureFormat("rgba16float")
        val rgba32uint = GPUTextureFormat("rgba32uint")
        val rgba32sint = GPUTextureFormat("rgba32sint")
        val rgba32float = GPUTextureFormat("rgba32float")
        val stencil8 = GPUTextureFormat("stencil8")
        val depth16unorm = GPUTextureFormat("depth16unorm")
        val depth24plus = GPUTextureFormat("depth24plus")
        val depth24plusStencil8 = GPUTextureFormat("depth24plus-stencil8")
        val depth32float = GPUTextureFormat("depth32float")
        val depth32floatStencil8 = GPUTextureFormat("depth32float-stencil8")
        // todo: many more compressed formats...
    }
}

value class GPUVertexFormat(val enumValue: String) {
    companion object {
        val uint8x2 = GPUVertexFormat("uint8x2")
        val uint8x4 = GPUVertexFormat("uint8x4")
        val sint8x2 = GPUVertexFormat("sint8x2")
        val sint8x4 = GPUVertexFormat("sint8x4")
        val unorm8x2 = GPUVertexFormat("unorm8x2")
        val unorm8x4 = GPUVertexFormat("unorm8x4")
        val snorm8x2 = GPUVertexFormat("snorm8x2")
        val snorm8x4 = GPUVertexFormat("snorm8x4")
        val uint16x2 = GPUVertexFormat("uint16x2")
        val uint16x4 = GPUVertexFormat("uint16x4")
        val sint16x2 = GPUVertexFormat("sint16x2")
        val sint16x4 = GPUVertexFormat("sint16x4")
        val unorm16x2 = GPUVertexFormat("unorm16x2")
        val unorm16x4 = GPUVertexFormat("unorm16x4")
        val snorm16x2 = GPUVertexFormat("snorm16x2")
        val snorm16x4 = GPUVertexFormat("snorm16x4")
        val float16x2 = GPUVertexFormat("float16x2")
        val float16x4 = GPUVertexFormat("float16x4")
        val float32 = GPUVertexFormat("float32")
        val float32x2 = GPUVertexFormat("float32x2")
        val float32x3 = GPUVertexFormat("float32x3")
        val float32x4 = GPUVertexFormat("float32x4")
        val uint32 = GPUVertexFormat("uint32")
        val uint32x2 = GPUVertexFormat("uint32x2")
        val uint32x3 = GPUVertexFormat("uint32x3")
        val uint32x4 = GPUVertexFormat("uint32x4")
        val sint32 = GPUVertexFormat("sint32")
        val sint32x2 = GPUVertexFormat("sint32x2")
        val sint32x3 = GPUVertexFormat("sint32x3")
        val sint32x4 = GPUVertexFormat("sint32x4")
    }
}

value class GPUVertexStepMode(val enumValue: String) {
    companion object {
        val vertex = GPUVertexStepMode("vertex")
        val instance = GPUVertexStepMode("instance")
    }
}
