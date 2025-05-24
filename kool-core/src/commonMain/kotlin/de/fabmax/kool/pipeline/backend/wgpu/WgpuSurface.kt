package de.fabmax.kool.pipeline.backend.wgpu

import io.ygdrasil.webgpu.CompositeAlphaMode
import io.ygdrasil.webgpu.GPUTextureFormat
import io.ygdrasil.webgpu.SurfaceConfiguration
import io.ygdrasil.webgpu.SurfaceTexture

expect class WgpuSurface: AutoCloseable {

    val width: UInt
    val height: UInt

    val supportedFormats: Set<GPUTextureFormat>
    val supportedAlphaMode: Set<CompositeAlphaMode>

    fun getCurrentTexture(): SurfaceTexture

    fun present()

    fun configure(surfaceConfiguration: SurfaceConfiguration)

    override fun close()
}