package de.fabmax.kool.util

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.platform.ShaderGenerator
import de.fabmax.kool.shading.*

/**
 * @author fabmax
 */
open class GlslGenerator : ShaderGenerator() {

    override fun onLoad(shader: BasicShader, ctx: RenderContext) {
        shader.enableAttribute(Shader.Attribute.POSITIONS, ATTRIBUTE_NAME_POSITION, ctx)
        shader.enableAttribute(Shader.Attribute.NORMALS, ATTRIBUTE_NAME_NORMAL, ctx)
        shader.enableAttribute(Shader.Attribute.TEXTURE_COORDS, ATTRIBUTE_NAME_TEX_COORD, ctx)
        shader.enableAttribute(Shader.Attribute.COLORS, ATTRIBUTE_NAME_COLOR, ctx)

        setUniformLocation(shader, uniformMvpMatrix, ctx)
        setUniformLocation(shader, uniformModelMatrix, ctx)
        setUniformLocation(shader, uniformViewMatrix, ctx)
        setUniformLocation(shader, uniformLightDirection, ctx)
        setUniformLocation(shader, uniformLightColor, ctx)
        setUniformLocation(shader, uniformShininess, ctx)
        setUniformLocation(shader, uniformSpecularIntensity, ctx)
        setUniformLocation(shader, uniformCameraPosition, ctx)
        setUniformLocation(shader, uniformFogColor, ctx)
        setUniformLocation(shader, uniformFogRange, ctx)
        setUniformLocation(shader, uniformTexture, ctx)
        setUniformLocation(shader, uniformStaticColor, ctx)
        setUniformLocation(shader, uniformAlpha, ctx)
        setUniformLocation(shader, uniformSaturation, ctx)
    }

    protected fun setUniformLocation(shader: BasicShader, uniform: Uniform<*>, ctx: RenderContext) {
        uniform.location = shader.findUniformLocation(uniform.name, ctx)
    }

    override fun generateSource(shaderProps: ShaderProps): Shader.Source {
        return Shader.Source(generateVertShader(shaderProps), generateFragShader(shaderProps))
    }

    private fun generateVertShader(shaderProps: ShaderProps): String {
        val text = StringBuilder("// Generated vertex shader code\n")

        injectors.forEach { it.vsStart(shaderProps, text) }
        generateVertInputCode(shaderProps, text)
        injectors.forEach { it.vsAfterInput(shaderProps, text) }
        generateVertBodyCode(shaderProps, text)
        injectors.forEach { it.vsEnd(shaderProps, text) }

        return text.toString()
    }

    private fun generateFragShader(shaderProps: ShaderProps): String {
        val text = StringBuilder("// Generated fragment shader code\n")

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
                text.append("varying vec4 ").append(VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(";\n")
                text.append("varying vec4 ").append(VARYING_NAME_SPECULAR_LIGHT_COLOR).append(";\n")
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
    }

    private fun generateVertBodyCode(shaderProps: ShaderProps, text: StringBuilder) {
        text.append("\nvoid main() {\n")

        // output position of the vertex in clip space: MVP * position
        // gl_Position = uMvpMatrix * vec4(aVertexPosition_modelspace, 1.0);
        text.append("gl_Position = ").append(UNIFORM_MVP_MATRIX).append(" * vec4(")
                .append(ATTRIBUTE_NAME_POSITION).append(", 1.0);\n")

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
            // vDiffuseLightColor = vec4(uLightColor, 1.0) * cosTheta;
            text.append(VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(" = vec4(")
                    .append(UNIFORM_LIGHT_COLOR).append(", 1.0) * cosTheta;\n")
            // vSpecularLightColor = vec4(uLightColor * specularIntensity, 1.0) * pow(cosAlpha, uShininess);
            text.append(VARYING_NAME_SPECULAR_LIGHT_COLOR).append(" = vec4(")
                    .append(UNIFORM_LIGHT_COLOR).append(" * ").append(UNIFORM_SPECULAR_INTENSITY)
                    .append(", 0.0) * pow(cosAlpha, ").append(UNIFORM_SHININESS)
                    .append(");\n")
        }
        text.append("}\n")
    }

    private fun generateFragInputCode(shaderProps: ShaderProps, text: StringBuilder) {
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
            text.append("varying vec4 ").append(VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(";\n")
            text.append("varying vec4 ").append(VARYING_NAME_SPECULAR_LIGHT_COLOR).append(";\n")
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
    }

    private fun generateFragBodyCode(shaderProps: ShaderProps, text: StringBuilder) {
        text.append("\nvoid main() {\n")
        text.append("vec4 ").append(LOCAL_NAME_FRAG_COLOR).append(" = vec4(0.0);\n")

        injectors.forEach { it.fsBeforeSampling(shaderProps, text) }

        if (shaderProps.isTextureColor) {
            // get base fragment color from texture
            // vec4 fragmentColor = texture2D(uTextureSampler, vTexCoord);
            text.append("vec4 ").append(LOCAL_NAME_TEX_COLOR).append(" = texture2D(")
                    .append(UNIFORM_TEXTURE_0).append(", ").append(VARYING_NAME_TEX_COORD).append(");\n")
            text.append(LOCAL_NAME_FRAG_COLOR).append(" = ").append(LOCAL_NAME_TEX_COLOR).append(";\n")
        }
        if (shaderProps.isVertexColor) {
            text.append("vec4 ").append(LOCAL_NAME_VERTEX_COLOR).append(" = ").append(VARYING_NAME_COLOR).append(";\n")
            text.append(LOCAL_NAME_VERTEX_COLOR).append(".rgb *= ").append(LOCAL_NAME_VERTEX_COLOR).append(".a;\n")
            text.append(LOCAL_NAME_FRAG_COLOR).append(" = ").append(LOCAL_NAME_VERTEX_COLOR).append(";\n")
        }
        if (shaderProps.isStaticColor) {
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

                // vec4 materialAmbientColor = vFragmentColor * vec4(0.4, 0.4, 0.4, 1.0);
                text.append("vec4 materialAmbientColor = ").append(LOCAL_NAME_FRAG_COLOR)
                        .append(" * vec4(0.4, 0.4, 0.4, 1.0);\n")

                // vec4 materialDiffuseColor = vFragmentColor * vec4(uLightColor, 1.0) * cosTheta;
                text.append("vec4 materialDiffuseColor = ").append(LOCAL_NAME_FRAG_COLOR)
                        .append(" * vec4(").append(UNIFORM_LIGHT_COLOR).append(", 1.0) * cosTheta;\n")

                // vec4 materialSpecularColor = vec4(uLightColor * uSpecular, 0.0) * pow(cosAlpha, uShininess);
                text.append("vec4 materialSpecularColor = vec4(").append(UNIFORM_LIGHT_COLOR)
                        .append(" * ").append(UNIFORM_SPECULAR_INTENSITY).append(", 0.0) * pow(cosAlpha, ")
                        .append(UNIFORM_SHININESS).append(") * clamp(").append(LOCAL_NAME_FRAG_COLOR).append(".a * 2.0, 0.0, 1.0);\n")

            } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
                // vec4 materialAmbientColor = vFragmentColor * vec4(0.4, 0.4, 0.4, 1.0);
                text.append("vec4 materialAmbientColor = ").append(LOCAL_NAME_FRAG_COLOR)
                        .append(" * vec4(0.4, 0.4, 0.4, 1.0);\n")

                // vec4 materialDiffuseColor = vFragmentColor * vDiffuseLightColor;
                text.append("vec4 materialDiffuseColor = ").append(LOCAL_NAME_FRAG_COLOR)
                        .append(" * ").append(VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(";\n")

                // vec4 materialSpecularColor = vSpecularLightColor;
                text.append("vec4 materialSpecularColor = ").append(VARYING_NAME_SPECULAR_LIGHT_COLOR).append(";\n")
                        .append(" * clamp(").append(LOCAL_NAME_FRAG_COLOR).append(".a * 2.0, 0.0, 1.0);\n")
            }

            // compute output color
            text.append("gl_FragColor = materialAmbientColor + materialDiffuseColor + materialSpecularColor;\n")

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
