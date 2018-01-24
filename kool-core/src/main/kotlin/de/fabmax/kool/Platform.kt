package de.fabmax.kool

import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps

/**
 * @author fabmax
 */

expect val supportsMultiContext: Boolean

expect val supportsUint32Indices: Boolean

expect fun createContext(props: RenderContext.InitProps): RenderContext

expect fun createCharMap(fontProps: FontProps): CharMap

expect fun currentTimeMillis(): Long

expect fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit)

expect fun loadTextureAsset(assetPath: String): TextureData

expect fun openUrl(url: String)

expect val glslVersion: GlslVersion

enum class GlslVersion(val versionStr: String, val dialect: GlslDialect) {
    GLSL_330("#version 330", GlslDialect.GLSL_DIALECT_300),
    GLSL_200_ES("#version 100", GlslDialect.GLSL_DIALECT_100),
    GLSL_300_ES("#version 300 es", GlslDialect.GLSL_DIALECT_300),
}

/**
 * This is a very shitty approach to generating shader code for different GLSL targets.
 * fixme: use a real builder with configurable target version to generate shader code
 */
data class GlslDialect(
        val vsIn: String,
        val vsOut: String,
        val fsIn: String,
        val fragColorHead: String,
        val fragColorBody: String,
        val texSampler: String
) {
    companion object {
        val GLSL_DIALECT_100 = GlslDialect("attribute", "varying",
                "varying", "", "gl_FragColor", "texture2D")
        val GLSL_DIALECT_300 = GlslDialect("in", "out",
                "in", "out vec4 fragColor;", "fragColor", "texture")
    }
}
