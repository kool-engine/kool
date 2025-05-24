package de.fabmax.kool.pipeline.backend.wgpu

import io.ygdrasil.webgpu.GPUTexture
import io.ygdrasil.webgpu.GPUTextureFormat
import org.w3c.dom.HTMLCanvasElement

class WgpuCanvasRenderTarget(val canvas: HTMLCanvasElement) : WgpuRenderTarget {


    override val width: Int = canvas.width
    override val height: Int = canvas.height
    override val textureFormat: GPUTextureFormat = TODO()

    override fun getCurrentTexture(): GPUTexture {
        TODO("Not yet implemented")
    }

}