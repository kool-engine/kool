package de.fabmax.kool.pipeline.backend.wgpu

import io.ygdrasil.webgpu.GPUTexture
import io.ygdrasil.webgpu.GPUTextureFormat

interface WgpuRenderTarget {
    val width: Int
    val height: Int
    val textureFormat: GPUTextureFormat

    fun getCurrentTexture(): GPUTexture
}