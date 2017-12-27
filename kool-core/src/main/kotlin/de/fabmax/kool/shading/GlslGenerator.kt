package de.fabmax.kool.shading

import de.fabmax.kool.RenderContext
import de.fabmax.kool.defaultGlslInjector

/**
 * @author fabmax
 */
open class GlslGenerator {
    companion object {
        const val UNIFORM_MVP_MATRIX = "uMvpMatrix"
        const val UNIFORM_MODEL_MATRIX = "uModelMatrix"
        const val UNIFORM_VIEW_MATRIX = "uViewMatrix"
        const val UNIFORM_LIGHT_DIRECTION = "uLightDirection"
        const val UNIFORM_LIGHT_COLOR = "uLightColor"
        const val UNIFORM_SHININESS = "uShininess"
        const val UNIFORM_SPECULAR_INTENSITY = "uSpecularIntensity"
        const val UNIFORM_CAMERA_POSITION = "uCameraPosition"
        const val UNIFORM_FOG_COLOR = "uFogColor"
        const val UNIFORM_FOG_RANGE = "uFogRange"
        const val UNIFORM_TEXTURE_0 = "uTexture0"
        const val UNIFORM_STATIC_COLOR = "uStaticColor"
        const val UNIFORM_ALPHA = "uAlpha"
        const val UNIFORM_SATURATION = "uSaturation"

        const val ATTRIBUTE_NAME_POSITION = "aVertexPosition_modelspace"
        const val ATTRIBUTE_NAME_NORMAL = "aVertexNormal_modelspace"
        const val ATTRIBUTE_NAME_TEX_COORD = "aVertexTexCoord"
        const val ATTRIBUTE_NAME_COLOR = "aVertexColor"

        const val VARYING_NAME_TEX_COORD = "vTexCoord"
        const val VARYING_NAME_EYE_DIRECTION = "vEyeDirection_cameraspace"
        const val VARYING_NAME_LIGHT_DIRECTION = "vLightDirection_cameraspace"
        const val VARYING_NAME_NORMAL = "vNormal_cameraspace"
        const val VARYING_NAME_COLOR = "vFragmentColor"
        const val VARYING_NAME_DIFFUSE_LIGHT_COLOR = "vDiffuseLightColor"
        const val VARYING_NAME_SPECULAR_LIGHT_COLOR = "vSpecularLightColor"
        const val VARYING_NAME_POSITION_WORLDSPACE = "vPositionWorldspace"

        const val LOCAL_NAME_FRAG_COLOR = "fragColor"
        const val LOCAL_NAME_TEX_COLOR = "texColor"
        const val LOCAL_NAME_VERTEX_COLOR = "vertColor"
        const val LOCAL_NAME_STATIC_COLOR = "staticColor"
    }

    interface GlslInjector {
        fun vsHeader(text: StringBuilder) { }
        fun vsStart(shaderProps: ShaderProps, text: StringBuilder) { }
        fun vsAfterInput(shaderProps: ShaderProps, text: StringBuilder) { }
        fun vsBeforeProj(shaderProps: ShaderProps, text: StringBuilder) { }
        fun vsAfterProj(shaderProps: ShaderProps, text: StringBuilder) { }
        fun vsEnd(shaderProps: ShaderProps, text: StringBuilder) { }

        fun fsHeader(text: StringBuilder) { }
        fun fsStart(shaderProps: ShaderProps, text: StringBuilder) { }
        fun fsAfterInput(shaderProps: ShaderProps, text: StringBuilder) { }
        fun fsBeforeSampling(shaderProps: ShaderProps, text: StringBuilder) { }
        fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder) { }
        fun fsEnd(shaderProps: ShaderProps, text: StringBuilder) { }
    }

    val injectors: MutableList<GlslInjector> = mutableListOf(defaultGlslInjector())

    val uniformMvpMatrix: UniformMatrix4 = UniformMatrix4(UNIFORM_MVP_MATRIX)
    val uniformModelMatrix: UniformMatrix4 = UniformMatrix4(UNIFORM_MODEL_MATRIX)
    val uniformViewMatrix: UniformMatrix4 = UniformMatrix4(UNIFORM_VIEW_MATRIX)
    val uniformLightColor: Uniform3f = Uniform3f(UNIFORM_LIGHT_COLOR)
    val uniformLightDirection: Uniform3f = Uniform3f(UNIFORM_LIGHT_DIRECTION)
    val uniformCameraPosition: Uniform3f = Uniform3f(UNIFORM_CAMERA_POSITION)
    val uniformShininess: Uniform1f = Uniform1f(UNIFORM_SHININESS)
    val uniformSpecularIntensity: Uniform1f = Uniform1f(UNIFORM_SPECULAR_INTENSITY)
    val uniformStaticColor: Uniform4f = Uniform4f(UNIFORM_STATIC_COLOR)
    val uniformTexture: UniformTexture2D = UniformTexture2D(UNIFORM_TEXTURE_0)
    val uniformAlpha: Uniform1f = Uniform1f(UNIFORM_ALPHA)
    val uniformSaturation: Uniform1f = Uniform1f(UNIFORM_SATURATION)
    val uniformFogRange: Uniform1f = Uniform1f(UNIFORM_FOG_RANGE)
    val uniformFogColor: Uniform4f = Uniform4f(UNIFORM_FOG_COLOR)

    val customUnitforms: MutableMap<String, Uniform<*>> = mutableMapOf()

    fun addCustomUniform(uniform: Uniform<*>) {
        customUnitforms.put(uniform.name, uniform)
    }

    fun onLoad(shader: BasicShader, ctx: RenderContext) {
        shader.enableAttribute(Shader.Attribute.POSITIONS, ATTRIBUTE_NAME_POSITION, ctx)
        shader.enableAttribute(Shader.Attribute.NORMALS, ATTRIBUTE_NAME_NORMAL, ctx)
        shader.enableAttribute(Shader.Attribute.TEXTURE_COORDS, ATTRIBUTE_NAME_TEX_COORD, ctx)
        shader.enableAttribute(Shader.Attribute.COLORS, ATTRIBUTE_NAME_COLOR, ctx)

        shader.setUniformLocation(uniformMvpMatrix, ctx)
        shader.setUniformLocation(uniformModelMatrix, ctx)
        shader.setUniformLocation(uniformViewMatrix, ctx)
        shader.setUniformLocation(uniformLightDirection, ctx)
        shader.setUniformLocation(uniformLightColor, ctx)
        shader.setUniformLocation(uniformShininess, ctx)
        shader.setUniformLocation(uniformSpecularIntensity, ctx)
        shader.setUniformLocation(uniformCameraPosition, ctx)
        shader.setUniformLocation(uniformFogColor, ctx)
        shader.setUniformLocation(uniformFogRange, ctx)
        shader.setUniformLocation(uniformTexture, ctx)
        shader.setUniformLocation(uniformStaticColor, ctx)
        shader.setUniformLocation(uniformAlpha, ctx)
        shader.setUniformLocation(uniformSaturation, ctx)

        for (uniform in customUnitforms.values) {
            shader.setUniformLocation(uniform, ctx)
        }
    }

    fun generate(shaderProps: ShaderProps): Shader.Source {
        uniformMvpMatrix.location = null
        uniformModelMatrix.location = null
        uniformViewMatrix.location = null
        uniformLightColor.location = null
        uniformLightDirection.location = null
        uniformCameraPosition.location = null
        uniformShininess.location = null
        uniformSpecularIntensity.location = null
        uniformStaticColor.location = null
        uniformTexture.location = null
        uniformAlpha.location = null
        uniformSaturation.location = null
        uniformFogRange.location = null
        uniformFogColor.location = null

        return Shader.Source(generateVertShader(shaderProps), generateFragShader(shaderProps))
    }

    private fun generateVertShader(shaderProps: ShaderProps): String {
        val text = StringBuilder("// Generated vertex shader code\n")

        injectors.forEach { it.vsHeader(text) }

        injectors.forEach { it.vsStart(shaderProps, text) }
        generateVertInputCode(shaderProps, text)
        injectors.forEach { it.vsAfterInput(shaderProps, text) }
        generateVertBodyCode(shaderProps, text)
        injectors.forEach { it.vsEnd(shaderProps, text) }

        return text.toString()
    }

    private fun generateFragShader(shaderProps: ShaderProps): String {
        val text = StringBuilder("// Generated fragment shader code\n")

        injectors.forEach { it.fsHeader(text) }

        injectors.forEach { it.fsStart(shaderProps, text) }
        generateFragInputCode(shaderProps, text)
        injectors.forEach { it.fsAfterInput(shaderProps, text) }
        generateFragBodyCode(shaderProps, text)
        injectors.forEach { it.fsEnd(shaderProps, text) }

        return text.toString()
    }

    private fun generateVertInputCode(shaderProps: ShaderProps, text: StringBuilder) {
        // MVP matrices and vertex position attribute are always needed
        text.append("attribute vec3 ").append(ATTRIBUTE_NAME_POSITION).append(";\n")
        text.append("uniform mat4 ").append(UNIFORM_MVP_MATRIX).append(";\n")
        text.append("uniform mat4 ").append(UNIFORM_MODEL_MATRIX).append(";\n")
        text.append("uniform mat4 ").append(UNIFORM_VIEW_MATRIX).append(";\n")

        // add light dependent uniforms and attributes
        if (shaderProps.lightModel != LightModel.NO_LIGHTING) {
            text.append("attribute vec3 ").append(ATTRIBUTE_NAME_NORMAL).append(";\n")
            text.append("uniform vec3 ").append(UNIFORM_LIGHT_DIRECTION).append(";\n")

            if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
                // Phong model specific stuff
                text.append("varying vec3 ").append(VARYING_NAME_EYE_DIRECTION).append(";\n")
                text.append("varying vec3 ").append(VARYING_NAME_LIGHT_DIRECTION).append(";\n")
                text.append("varying vec3 ").append(VARYING_NAME_NORMAL).append(";\n")

            } else {
                // Gouraud model specific stuff
                text.append("uniform vec3 ").append(UNIFORM_LIGHT_COLOR).append(";\n")
                text.append("uniform float ").append(UNIFORM_SHININESS).append(";\n")
                text.append("uniform float ").append(UNIFORM_SPECULAR_INTENSITY).append(";\n")
                text.append("varying vec3 ").append(VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(";\n")
                text.append("varying vec3 ").append(VARYING_NAME_SPECULAR_LIGHT_COLOR).append(";\n")
            }
        }

        // add color dependent attributes
        if (shaderProps.isTextureColor) {
            // texture color
            text.append("attribute vec2 ").append(ATTRIBUTE_NAME_TEX_COORD).append(";\n")
            text.append("varying vec2 ").append(VARYING_NAME_TEX_COORD).append(";\n")

        }
        if (shaderProps.isVertexColor) {
            // vertex color
            text.append("attribute vec4 ").append(ATTRIBUTE_NAME_COLOR).append(";\n")
            text.append("varying vec4 ").append(VARYING_NAME_COLOR).append(";\n")
        }

        // if fog is enabled, fragment shader needs to know the world position
        if (shaderProps.fogModel != FogModel.FOG_OFF) {
            text.append("varying vec3 ").append(VARYING_NAME_POSITION_WORLDSPACE).append(";\n")
        }

        for (uniform in customUnitforms.values) {
            text.append("uniform ${uniform.type} ${uniform.name};\n")
        }
    }

    private fun generateVertBodyCode(shaderProps: ShaderProps, text: StringBuilder) {
        text.append("\nvoid main() {\n")

        injectors.forEach { it.vsBeforeProj(shaderProps, text) }

        // output position of the vertex in clip space: MVP * position
        // gl_Position = uMvpMatrix * vec4(aVertexPosition_modelspace, 1.0);
        text.append("gl_Position = ").append(UNIFORM_MVP_MATRIX).append(" * vec4(")
                .append(ATTRIBUTE_NAME_POSITION).append(", 1.0);\n")

        injectors.forEach { it.vsAfterProj(shaderProps, text) }

        if (shaderProps.fogModel != FogModel.FOG_OFF) {
            // vPositionWorldspace = (uModelMatrix * vec4(aVertexPosition_modelspace, 1.0)).xyz;
            text.append(VARYING_NAME_POSITION_WORLDSPACE).append(" = (").append(UNIFORM_MODEL_MATRIX)
                    .append(" * vec4(").append(ATTRIBUTE_NAME_POSITION).append(", 1.0)).xyz;\n")
        }

        if (shaderProps.isTextureColor) {
            // interpolate vertex texture coordinate for usage in fragment shader
            // vTexCoord = aVertexTexCoord;
            text.append(VARYING_NAME_TEX_COORD).append(" = ").append(ATTRIBUTE_NAME_TEX_COORD).append(";\n")

        }
        if (shaderProps.isVertexColor) {
            // interpolate vertex color for usage in fragment shader
            // vFragmentColor = aVertexColor;
            text.append(VARYING_NAME_COLOR).append(" = ").append(ATTRIBUTE_NAME_COLOR).append(";\n")
        }

        if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
            // vector from vertex to camera, in camera space. In camera space, the camera is at the origin (0, 0, 0).
            // vEyeDirection_cameraspace = -(uViewMatrix * uModelMatrix * vec4(aVertexPosition_modelspace, 1)).xyz;
            text.append(VARYING_NAME_EYE_DIRECTION).append(" = -(").append(UNIFORM_VIEW_MATRIX)
                    .append(" * ").append(UNIFORM_MODEL_MATRIX).append(" * vec4(")
                    .append(ATTRIBUTE_NAME_POSITION).append(", 1.0)).xyz;\n")

            // light direction, in camera space. M is left out because light position is already in world space.
            // vLightDirection_cameraspace = (uViewMatrix * vec4(uLightDirection_worldspace, 0)).xyz;
            text.append(VARYING_NAME_LIGHT_DIRECTION).append(" = (").append(UNIFORM_VIEW_MATRIX)
                    .append(" * vec4(").append(UNIFORM_LIGHT_DIRECTION).append(", 0.0)).xyz;\n")

            // normal of the the vertex, in camera space
            // vNormal_cameraspace = (uViewMatrix * uModelMatrix * vec4(aVertexNormal_modelspace, 0)).xyz;
            text.append(VARYING_NAME_NORMAL).append(" = (").append(UNIFORM_VIEW_MATRIX).append(" * ")
                    .append(UNIFORM_MODEL_MATRIX).append(" * vec4(").append(ATTRIBUTE_NAME_NORMAL)
                    .append(", 0.0)).xyz;\n")

        } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
            // vector from vertex to camera, in camera space. In camera space, the camera is at the origin (0, 0, 0).
            // vec3 e = normalize(-(uViewMatrix * uModelMatrix * vec4(aVertexPosition_modelspace, 1)).xyz);
            text.append("vec3 e = normalize(-(").append(UNIFORM_VIEW_MATRIX).append(" * ")
                    .append(UNIFORM_MODEL_MATRIX).append(" * vec4(").append(ATTRIBUTE_NAME_POSITION)
                    .append(", 1.0)).xyz);\n")

            // light direction, in camera space. M is left out because light position is already in world space.
            // vec3 l = normalize((uViewMatrix * vec4(uLightDirection_worldspace, 0)).xyz);
            text.append("vec3 l = normalize((").append(UNIFORM_VIEW_MATRIX)
                    .append(" * vec4(").append(UNIFORM_LIGHT_DIRECTION).append(", 0.0)).xyz);\n")

            // normal of the the vertex, in camera space
            // vec3 n = normalize((uViewMatrix * uModelMatrix * vec4(aVertexNormal_modelspace, 0)).xyz);
            text.append("vec3 n = normalize((").append(UNIFORM_VIEW_MATRIX).append(" * ")
                    .append(UNIFORM_MODEL_MATRIX).append(" * vec4(").append(ATTRIBUTE_NAME_NORMAL)
                    .append(", 0.0)).xyz);\n")

            // cosine of angle between surface normal and light direction
            text.append("float cosTheta = clamp(dot(n, l), 0.0, 1.0);\n")
            // direction in which the light is reflected
            text.append("vec3 r = reflect(-l, n);\n")
            // cosine of the angle between the eye vector and the reflect vector
            text.append("float cosAlpha = clamp(dot(e, r), 0.0, 1.0);\n")

            // interpolate light colors for usage in fragment shader
            // vDiffuseLightColor = uLightColor * cosTheta;
            text.append(VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(" = ")
                    .append(UNIFORM_LIGHT_COLOR).append(" * cosTheta;\n")
            // vSpecularLightColor = uLightColor * specularIntensity * pow(cosAlpha, uShininess);
            text.append(VARYING_NAME_SPECULAR_LIGHT_COLOR).append(" = ")
                    .append(UNIFORM_LIGHT_COLOR).append(" * ").append(UNIFORM_SPECULAR_INTENSITY)
                    .append(" * pow(cosAlpha, ").append(UNIFORM_SHININESS).append(");\n")
        }
        text.append("}\n")
    }

    private fun generateFragInputCode(shaderProps: ShaderProps, text: StringBuilder) {
        text.append("precision highp float;\n")
        text.append("uniform mat4 ").append(UNIFORM_MODEL_MATRIX).append(";\n")
        text.append("uniform mat4 ").append(UNIFORM_VIEW_MATRIX).append(";\n")
        if (shaderProps.isAlpha) {
            text.append("uniform float ").append(UNIFORM_ALPHA).append(";\n")
        }
        if (shaderProps.isSaturation) {
            text.append("uniform float ").append(UNIFORM_SATURATION).append(";\n")
        }

        // add light dependent uniforms and varyings
        if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
            text.append("uniform vec3 ").append(UNIFORM_LIGHT_COLOR).append(";\n")
            text.append("uniform float ").append(UNIFORM_SHININESS).append(";\n")
            text.append("uniform float ").append(UNIFORM_SPECULAR_INTENSITY).append(";\n")
            text.append("varying vec3 ").append(VARYING_NAME_EYE_DIRECTION).append(";\n")
            text.append("varying vec3 ").append(VARYING_NAME_LIGHT_DIRECTION).append(";\n")
            text.append("varying vec3 ").append(VARYING_NAME_NORMAL).append(";\n")
        } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
            text.append("varying vec3 ").append(VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(";\n")
            text.append("varying vec3 ").append(VARYING_NAME_SPECULAR_LIGHT_COLOR).append(";\n")
        }

        // add color dependent uniforms and varyings
        if (shaderProps.isTextureColor) {
            // texture color
            text.append("uniform sampler2D ").append(UNIFORM_TEXTURE_0).append(";\n")
            text.append("varying vec2 ").append(VARYING_NAME_TEX_COORD).append(";\n")
        }
        if (shaderProps.isVertexColor) {
            // vertex color
            text.append("varying vec4 ").append(VARYING_NAME_COLOR).append(";\n")
        }
        if (shaderProps.isStaticColor) {
            // static color
            text.append("uniform vec4 ").append(UNIFORM_STATIC_COLOR).append(";\n")
        }

        // add fog uniforms
        if (shaderProps.fogModel != FogModel.FOG_OFF) {
            text.append("uniform vec3 ").append(UNIFORM_CAMERA_POSITION).append(";\n")
            text.append("uniform vec4 ").append(UNIFORM_FOG_COLOR).append(";\n")
            text.append("uniform float ").append(UNIFORM_FOG_RANGE).append(";\n")
            text.append("varying vec3 ").append(VARYING_NAME_POSITION_WORLDSPACE).append(";\n")
        }

        for (uniform in customUnitforms.values) {
            text.append("uniform ${uniform.type} ${uniform.name};\n")
        }
    }

    private fun generateFragBodyCode(shaderProps: ShaderProps, text: StringBuilder) {
        text.append("\nvoid main() {\n")
        text.append("vec4 ").append(LOCAL_NAME_FRAG_COLOR).append(" = vec4(0.0);\n")

        injectors.forEach { it.fsBeforeSampling(shaderProps, text) }

        if (shaderProps.isTextureColor) {
            // get base fragment color from texture
            text.append("vec4 ").append(LOCAL_NAME_TEX_COLOR).append(" = texture2D(")
                    .append(UNIFORM_TEXTURE_0).append(", ").append(VARYING_NAME_TEX_COORD).append(");\n")
            text.append(LOCAL_NAME_FRAG_COLOR).append(" = ").append(LOCAL_NAME_TEX_COLOR).append(";\n")
        }
        if (shaderProps.isVertexColor) {
            // get base fragment color from vertex attribute
            text.append("vec4 ").append(LOCAL_NAME_VERTEX_COLOR).append(" = ").append(VARYING_NAME_COLOR).append(";\n")
            text.append(LOCAL_NAME_VERTEX_COLOR).append(".rgb *= ").append(LOCAL_NAME_VERTEX_COLOR).append(".a;\n")
            text.append(LOCAL_NAME_FRAG_COLOR).append(" = ").append(LOCAL_NAME_VERTEX_COLOR).append(";\n")
        }
        if (shaderProps.isStaticColor) {
            // get base fragment color from static color uniform
            text.append("vec4 ").append(LOCAL_NAME_STATIC_COLOR).append(" = ").append(UNIFORM_STATIC_COLOR).append(";\n")
            text.append(LOCAL_NAME_STATIC_COLOR).append(".rgb *= ").append(LOCAL_NAME_STATIC_COLOR).append(".a;\n")
            text.append(LOCAL_NAME_FRAG_COLOR).append(" = ").append(LOCAL_NAME_STATIC_COLOR).append(";\n")
        }

        injectors.forEach { it.fsAfterSampling(shaderProps, text) }

        if (shaderProps.lightModel != LightModel.NO_LIGHTING) {
            if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
                // normalize input vectors
                text.append("vec3 e = normalize(").append(VARYING_NAME_EYE_DIRECTION).append(");\n")
                text.append("vec3 l = normalize(").append(VARYING_NAME_LIGHT_DIRECTION).append(");\n")
                text.append("vec3 n = normalize(").append(VARYING_NAME_NORMAL).append(");\n")

                // cosine of angle between surface normal and light direction
                text.append("float cosTheta = clamp(dot(n, l), 0.0, 1.0);\n")
                // direction in which the light is reflected
                text.append("vec3 r = reflect(-l, n);\n")
                // cosine of the angle between the eye vector and the reflect vector
                text.append("float cosAlpha = clamp(dot(e, r), 0.0, 1.0);\n")

                // vec3 materialAmbientColor = vFragmentColor.rgb * vec3(0.4);
                text.append("vec3 materialAmbientColor = ").append(LOCAL_NAME_FRAG_COLOR).append(".rgb * vec3(0.42);\n")

                // vec3 materialDiffuseColor = vFragmentColor.rgb * uLightColor * cosTheta;
                text.append("vec3 materialDiffuseColor = ").append(LOCAL_NAME_FRAG_COLOR)
                        .append(".rgb * ").append(UNIFORM_LIGHT_COLOR).append(" * cosTheta;\n")

                // vec4 materialSpecularColor = vec4(uLightColor * uSpecular, 0.0) * pow(cosAlpha, uShininess) * alpha;
                text.append("vec3 materialSpecularColor = ").append(UNIFORM_LIGHT_COLOR)
                        .append(" * ").append(UNIFORM_SPECULAR_INTENSITY).append(" * pow(cosAlpha, ")
                        .append(UNIFORM_SHININESS).append(") * ").append(LOCAL_NAME_FRAG_COLOR).append(".a;\n")

            } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
                // vec3 materialAmbientColor = vFragmentColor.rgb * vec3(0.4);
                text.append("vec3 materialAmbientColor = ").append(LOCAL_NAME_FRAG_COLOR)
                        .append(".rgb * vec3(0.42);\n")

                // vec3 materialDiffuseColor = vFragmentColor.rgb * vDiffuseLightColor;
                text.append("vec3 materialDiffuseColor = ").append(LOCAL_NAME_FRAG_COLOR)
                        .append(".rgb * ").append(VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(";\n")

                // vec4 materialSpecularColor = vSpecularLightColor;
                text.append("vec3 materialSpecularColor = ").append(VARYING_NAME_SPECULAR_LIGHT_COLOR)
                        .append(" * ").append(LOCAL_NAME_FRAG_COLOR).append(".a;\n")
            }

            // compute output color
            text.append("gl_FragColor = vec4(materialAmbientColor + materialDiffuseColor + materialSpecularColor, ")
                    .append(LOCAL_NAME_FRAG_COLOR).append(".a);\n")

        } else {
            text.append("gl_FragColor = ").append(LOCAL_NAME_FRAG_COLOR).append(";\n")
        }

        // add fog code
        if (shaderProps.fogModel != FogModel.FOG_OFF) {
            text.append("float d = 1.0 - clamp(length(").append(UNIFORM_CAMERA_POSITION).append(" - ")
                    .append(VARYING_NAME_POSITION_WORLDSPACE).append(") / ")
                    .append(UNIFORM_FOG_RANGE).append(", 0.0, 1.0);\n")
            text.append("gl_FragColor.rgb = mix(").append(UNIFORM_FOG_COLOR).append(".rgb, gl_FragColor.rgb, d * d * ")
                    .append(UNIFORM_FOG_COLOR).append(".a);\n")
        }

        if (shaderProps.isAlpha) {
            text.append("gl_FragColor *= ").append(UNIFORM_ALPHA).append(";\n")
        }
        if (shaderProps.isSaturation) {
            text.append("float avgColor = (gl_FragColor.r + gl_FragColor.g + gl_FragColor.b) * 0.333;\n")
            text.append("gl_FragColor.rgb = mix(vec3(avgColor), gl_FragColor.rgb, ").append(UNIFORM_SATURATION).append(");\n")
        }

        text.append("}\n")
    }
}
