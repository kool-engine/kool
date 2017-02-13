package de.fabmax.kool.shading

enum class LightModel {
    PHONG_LIGHTING,
    GOURAUD_LIGHTING,
    NO_LIGHTING
}

enum class ColorModel {
    VERTEX_COLOR,
    TEXTURE_COLOR,
    STATIC_COLOR
}

enum class FogModel {
    FOG_OFF,
    FOG_ON
}

class ShaderProps(init: ShaderProps.() -> Unit = {}) {
    var lightModel = LightModel.PHONG_LIGHTING
    var colorModel = ColorModel.STATIC_COLOR
        set(value) {
            when (value) {
                ColorModel.VERTEX_COLOR -> { isVertexColor = true; isTextureColor = false; isStaticColor = false }
                ColorModel.TEXTURE_COLOR -> { isVertexColor = false; isTextureColor = true; isStaticColor = false }
                ColorModel.STATIC_COLOR -> { isVertexColor = false; isTextureColor = false; isStaticColor = true }
            }
        }
    var fogModel = FogModel.FOG_OFF

    var isVertexColor = false
    var isTextureColor = false
    var isStaticColor = true

    var isAlpha = false
    var isSaturation = false

    init {
        this.init()
    }
}
