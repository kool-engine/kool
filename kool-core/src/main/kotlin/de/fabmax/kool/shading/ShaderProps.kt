package de.fabmax.kool.shading

import de.fabmax.kool.Texture
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap

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

class ShaderProps {
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
    var isDiscardTranslucent = false

    var numBones = 0

    var shadowMap: ShadowMap? = null

    var isNormalMapped = false

    // init values for newly created BasicShader
    var shininess = 20.0f
    var specularIntensity = 0.75f
    var staticColor = Color.BLACK
    var alpha = 1.0f
    var saturation = 1.0f
    var texture: Texture? = null
    var normalMap: Texture? = null
}
