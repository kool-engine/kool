package de.fabmax.kool.shading

data class ShadingHints(
        var preferredLightModel: PreferredLightModel,
        var preferredShadowMethod: PreferredShadowMethod
)

enum class PreferredLightModel(val level: Int) {
    NO_LIGHTING(0),
    GOURAUD(1),
    PHONG(2)
}

enum class PreferredShadowMethod(val level: Int) {
    NO_SHADOW(0),
    SINGLE_SHADOW_MAP(1),
    CASCADED_SHADOW_MAP(2)
}
