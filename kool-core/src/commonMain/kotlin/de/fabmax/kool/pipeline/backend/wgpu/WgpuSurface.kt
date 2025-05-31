package de.fabmax.kool.pipeline.backend.wgpu

import io.ygdrasil.webgpu.CompositeAlphaMode
import io.ygdrasil.webgpu.GPUTextureFormat
import io.ygdrasil.webgpu.GPUTextureView
import io.ygdrasil.webgpu.SurfaceConfiguration

expect class WgpuSurface: AutoCloseable {

    val width: UInt
    val height: UInt

    val supportedAlphaMode: Set<CompositeAlphaMode>
    val format: GPUTextureFormat

    fun getCurrentTextureView(): GPUTextureView

    fun present()

    fun configure(surfaceConfiguration: SurfaceConfiguration)

    override fun close()
}