package de.fabmax.kool

import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture

actual class OffscreenPass2dImpl actual constructor(val offscreenPass: OffscreenPass2d) {
    actual val texture: Texture
        get() = TODO()
}

actual class OffscreenPassCubeImpl actual constructor(val offscreenPass: OffscreenPassCube) {
    actual val texture: CubeMapTexture
        get() = TODO()
}