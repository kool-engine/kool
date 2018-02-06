package de.fabmax.kool

import de.fabmax.kool.gl.GL_DEPTH_COMPONENT
import de.fabmax.kool.gl.GL_DEPTH_COMPONENT24
import de.fabmax.kool.gl.GL_LINEAR
import de.fabmax.kool.gl.GL_NEAREST

class GlCapabilities {
    var uint32Indices = false
    var shaderIntAttribs = false
    var depthTextures = false
    var depthComponentIntFormat = GL_DEPTH_COMPONENT
    var depthFilterMethod = GL_NEAREST
    var framebufferWithoutColor = false

    var glslDialect = GlslDialect.GLSL_DIALECT_100

    companion object {
        val GL_330: GlCapabilities = GlCapabilities().apply {
            uint32Indices = true
            shaderIntAttribs = true
            depthTextures = true
            depthComponentIntFormat = GL_DEPTH_COMPONENT
            depthFilterMethod = GL_LINEAR
            framebufferWithoutColor = true

            glslDialect = GlslDialect.GLSL_DIALECT_330
        }

        val GL_ES_300: GlCapabilities = GlCapabilities().apply {
            uint32Indices = true
            shaderIntAttribs = true
            depthTextures = true
            depthComponentIntFormat = GL_DEPTH_COMPONENT24
            depthFilterMethod = GL_NEAREST
            framebufferWithoutColor = true

            glslDialect = GlslDialect.GLSL_DIALECT_300_ES
        }

        val GL_ES_200: GlCapabilities = GlCapabilities().apply {
            // most capabilities can be true depending on available extensions, set by implementation accordingly
            uint32Indices = false
            shaderIntAttribs = false
            depthTextures = false
            depthComponentIntFormat = GL_DEPTH_COMPONENT
            depthFilterMethod = GL_NEAREST
            framebufferWithoutColor = false

            glslDialect = GlslDialect.GLSL_DIALECT_100
        }
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
