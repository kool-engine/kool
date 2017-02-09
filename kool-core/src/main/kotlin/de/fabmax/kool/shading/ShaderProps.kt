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
    var colorModel = ColorModel.VERTEX_COLOR
    var fogModel = FogModel.FOG_OFF

    var isAlpha = false
    var isSaturation = false

    init {
        this.init()
    }
}
