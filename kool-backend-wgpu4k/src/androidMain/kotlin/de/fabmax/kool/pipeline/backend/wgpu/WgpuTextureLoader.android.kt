package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.ImageData
import io.ygdrasil.webgpu.Extent3D
import io.ygdrasil.webgpu.GPUDevice
import io.ygdrasil.webgpu.GPUOrigin3D
import io.ygdrasil.webgpu.GPUTexture

internal actual fun copyNativeTextureData(
    src: ImageData,
    dst: GPUTexture,
    size: Extent3D,
    dstOrigin: GPUOrigin3D,
    device: GPUDevice
) {
    // Not yet supported on Android
    error("Not implemented: ${src::class.simpleName}")
}