package de.fabmax.kool

data class GlCapabilities(
        val uint32Indices: Boolean,
        val shaderIntAttribs: Boolean,
        val maxTexUnits: Int,
        val depthTextures: Boolean,
        val depthComponentIntFormat: Int,
        val depthFilterMethod: Int,
        val anisotropicTexFilterInfo: AnisotropicTexFilterInfo,
        val glslDialect: GlslDialect,
        val glVersion: GlVersion
) {
    companion object {
        val UNKNOWN_CAPABILITIES = GlCapabilities(
                uint32Indices = false,
                shaderIntAttribs = false,
                maxTexUnits = 16,
                depthTextures = false,
                depthComponentIntFormat = 0,
                depthFilterMethod = 0,
                anisotropicTexFilterInfo = AnisotropicTexFilterInfo.NOT_SUPPORTED,
                glslDialect = GlslDialect.GLSL_DIALECT_100,
                glVersion = GlVersion("Unknown", 0, 0))
    }
}

data class GlVersion(
        val glDialect: String,
        val versionMajor: Int,
        val versionMinor: Int
) {
    override fun toString(): String = "$glDialect $versionMajor.$versionMinor"
}

data class AnisotropicTexFilterInfo(val maxAnisotropy: Float, val TEXTURE_MAX_ANISOTROPY_EXT: Int) {
    val isSupported get() = TEXTURE_MAX_ANISOTROPY_EXT != 0

    companion object {
        val NOT_SUPPORTED = AnisotropicTexFilterInfo(0f, 0)
    }
}

/**
 * This is a pretty shitty approach to generating shader code for different GLSL targets.
 * fixme: use a real builder with configurable target version to generate shader code
 */
data class GlslDialect(
        val version: String,
        val vsIn: String,
        val vsOut: String,
        val fsIn: String,
        val fragColorHead: String,
        val fragColorBody: String,
        val texSampler: String
) {
    companion object {
        val GLSL_DIALECT_100 = GlslDialect("#version 100", "attribute", "varying",
                "varying", "", "gl_FragColor", "texture2D")
        val GLSL_DIALECT_330 = GlslDialect("#version 330", "in", "out",
                "in", "out vec4 fragColor;", "fragColor", "texture")
        val GLSL_DIALECT_300_ES = GlslDialect("#version 300 es", "in", "out",
                "in", "out vec4 fragColor;", "fragColor", "texture")
    }
}
