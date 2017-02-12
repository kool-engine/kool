package de.fabmax.kool.util

import de.fabmax.kool.platform.ShaderGenerator
import de.fabmax.kool.shading.*

/**
 * @author fabmax
 */
open class GlslGenerator(private val customization: Customization? = null) : ShaderGenerator() {

    companion object {
        val ATTRIBUTE_NAME_POSITION = "aVertexPosition_modelspace"
        val ATTRIBUTE_NAME_NORMAL = "aVertexNormal_modelspace"
        val ATTRIBUTE_NAME_TEX_COORD = "aVertexTexCoord"
        val ATTRIBUTE_NAME_COLOR = "aVertexColor"

        val VARYING_NAME_TEX_COORD = "vTexCoord"
        val VARYING_NAME_EYE_DIRECTION = "vEyeDirection_cameraspace"
        val VARYING_NAME_LIGHT_DIRECTION = "vLightDirection_cameraspace"
        val VARYING_NAME_NORMAL = "vNormal_cameraspace"
        val VARYING_NAME_COLOR = "vFragmentColor"
        val VARYING_NAME_DIFFUSE_LIGHT_COLOR = "vDiffuseLightColor"
        val VARYING_NAME_SPECULAR_LIGHT_COLOR = "vSpecularLightColor"
        val VARYING_NAME_POSITION_WORLDSPACE = "vPositionWorldspace"
    }

    interface Customization {
        fun vertexShaderStart(shaderProps: ShaderProps, text: StringBuilder) { }
        fun vertexShaderAfterInput(shaderProps: ShaderProps, text: StringBuilder) { }
        fun vertexShaderEnd(shaderProps: ShaderProps, text: StringBuilder) { }

        fun fragmentShaderStart(shaderProps: ShaderProps, text: StringBuilder) { }
        fun fragmentShaderAfterInput(shaderProps: ShaderProps, text: StringBuilder) { }
        fun fragmentShaderEnd(shaderProps: ShaderProps, text: StringBuilder) { }
    }

    override fun onLoad(shader: BasicShader) {
        shader.enableAttribute(Shader.Attribute.POSITIONS, ATTRIBUTE_NAME_POSITION)
        shader.enableAttribute(Shader.Attribute.NORMALS, ATTRIBUTE_NAME_NORMAL)
        shader.enableAttribute(Shader.Attribute.TEXTURE_COORDS, ATTRIBUTE_NAME_TEX_COORD)
        shader.enableAttribute(Shader.Attribute.COLORS, ATTRIBUTE_NAME_COLOR)

        setUniformLocation(shader, uniformMvpMatrix)
        setUniformLocation(shader, uniformModelMatrix)
        setUniformLocation(shader, uniformViewMatrix)
        setUniformLocation(shader, uniformLightDirection)
        setUniformLocation(shader, uniformLightColor)
        setUniformLocation(shader, uniformShininess)
        setUniformLocation(shader, uniformSpecularIntensity)
        setUniformLocation(shader, uniformCameraPosition)
        setUniformLocation(shader, uniformFogColor)
        setUniformLocation(shader, uniformFogRange)
        setUniformLocation(shader, uniformTexture)
        setUniformLocation(shader, uniformStaticColor)
        setUniformLocation(shader, uniformAlpha)
        setUniformLocation(shader, uniformSaturation)
    }

    protected fun setUniformLocation(shader: BasicShader, uniform: Uniform<*>) {
        uniform.location = shader.findUniformLocation(uniform.name)
    }

    override fun generateSource(shaderProps: ShaderProps): Shader.Source {
        return Shader.Source(generateVertShader(shaderProps), generateFragShader(shaderProps))
    }

    private fun generateVertShader(shaderProps: ShaderProps): String {
        val text = StringBuilder("// Generated vertex shader code\n")

        customization?.vertexShaderStart(shaderProps, text)
        generateVertInputCode(shaderProps, text)
        customization?.vertexShaderAfterInput(shaderProps, text)
        generateVertBodyCode(shaderProps, text)
        customization?.vertexShaderEnd(shaderProps, text)

        return text.toString()
    }

    private fun generateFragShader(shaderProps: ShaderProps): String {
        val text = StringBuilder("// Generated fragment shader code\n")

        customization?.fragmentShaderStart(shaderProps, text)
        generateFragInputCode(shaderProps, text)
        customization?.fragmentShaderAfterInput(shaderProps, text)
        generateFragBodyCode(shaderProps, text)
        customization?.fragmentShaderEnd(shaderProps, text)

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
        if (shaderProps.colorModel == ColorModel.TEXTURE_COLOR) {
            // texture color
            text.append("attribute vec2 ").append(ATTRIBUTE_NAME_TEX_COORD).append(";\n")
            text.append("varying vec2 ").append(VARYING_NAME_TEX_COORD).append(";\n")

        } else if (shaderProps.colorModel == ColorModel.VERTEX_COLOR) {
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

        if (shaderProps.colorModel == ColorModel.TEXTURE_COLOR) {
            // interpolate vertex texture coordinate for usage in fragment shader
            // vTexCoord = aVertexTexCoord;
            text.append(VARYING_NAME_TEX_COORD).append(" = ").append(ATTRIBUTE_NAME_TEX_COORD).append(";\n")

        } else if (shaderProps.colorModel == ColorModel.VERTEX_COLOR) {
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
        /*if (shaderProps.isHighPrecision) {
            text.append("precision highp float;")
        } else {
            text.append("precision mediump float;")
        }*/

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
        if (shaderProps.colorModel == ColorModel.TEXTURE_COLOR) {
            // texture color
            text.append("uniform sampler2D ").append(UNIFORM_TEXTURE_0).append(";\n")
            text.append("varying vec2 ").append(VARYING_NAME_TEX_COORD).append(";\n")
        } else if (shaderProps.colorModel == ColorModel.VERTEX_COLOR) {
            // vertex color
            text.append("varying vec4 ").append(VARYING_NAME_COLOR).append(";\n")
        } else {
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

        if (shaderProps.colorModel == ColorModel.TEXTURE_COLOR) {
            // get base fragment color from texture, use varying color name as var name to make it
            // compatible with vertex color color model
            // vec4 fragmentColor = texture2D(uTextureSampler, vTexCoord);
            text.append("vec4 ").append(VARYING_NAME_COLOR).append(" = texture2D(")
                    .append(UNIFORM_TEXTURE_0).append(", ").append(VARYING_NAME_TEX_COORD).append(");\n")
        } else if (shaderProps.colorModel == ColorModel.STATIC_COLOR) {
            text.append("vec4 ").append(VARYING_NAME_COLOR).append(" = ").append(UNIFORM_STATIC_COLOR).append(";\n")
        }

        if (shaderProps.colorModel != ColorModel.TEXTURE_COLOR) {
            // pre-multiply alpha! Why? Because that's the right way...
            // https://limnu.com/webgl-blending-youre-probably-wrong/
            // http://www.realtimerendering.com/blog/gpus-prefer-premultiplication/

            // pre-multiplication is done for all color models except texture color,
            // because textures already are pre-multiplied
            text.append(VARYING_NAME_COLOR).append(".rgb *= ").append(VARYING_NAME_COLOR).append(".a;")
        }

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
                text.append("vec4 materialAmbientColor = ").append(VARYING_NAME_COLOR)
                        .append(" * vec4(0.4, 0.4, 0.4, 1.0);\n")

                // vec4 materialDiffuseColor = vFragmentColor * vec4(uLightColor, 1.0) * cosTheta;
                text.append("vec4 materialDiffuseColor = ").append(VARYING_NAME_COLOR)
                        .append(" * vec4(").append(UNIFORM_LIGHT_COLOR).append(", 1.0) * (cosTheta + 0.2);\n")

                // vec4 materialSpecularColor = vec4(uLightColor * uSpecular, 1.0) * pow(cosAlpha, uShininess);
                text.append("vec4 materialSpecularColor = vec4(").append(UNIFORM_LIGHT_COLOR)
                        .append(" * ").append(UNIFORM_SPECULAR_INTENSITY).append(", 0.0) * pow(cosAlpha, ")
                        .append(UNIFORM_SHININESS).append(");\n")

            } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
                // vec4 materialAmbientColor = vFragmentColor * vec4(0.4, 0.4, 0.4, 1.0);
                text.append("vec4 materialAmbientColor = ").append(VARYING_NAME_COLOR)
                        .append(" * vec4(0.4, 0.4, 0.4, 1.0);\n")

                // vec4 materialDiffuseColor = fragmentColor * vDiffuseLightColor;
                text.append("vec4 materialDiffuseColor = ").append(VARYING_NAME_COLOR)
                        .append(" * ").append(VARYING_NAME_DIFFUSE_LIGHT_COLOR).append(";\n")

                // vec4 materialSpecularColor = vSpecularLightColor;
                text.append("vec4 materialSpecularColor = ").append(VARYING_NAME_COLOR)
                        .append(" * ").append(VARYING_NAME_SPECULAR_LIGHT_COLOR).append(";\n")
            }

            // compute output color
            text.append("gl_FragColor = materialAmbientColor + materialDiffuseColor + materialSpecularColor;\n")

        } else {
            text.append("gl_FragColor = ").append(VARYING_NAME_COLOR).append(";\n")
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
            text.append("gl_FragColor.a = gl_FragColor.a * ").append(UNIFORM_ALPHA).append(";\n")
        }
        if (shaderProps.isSaturation) {
            text.append("float avgColor = (gl_FragColor.r + gl_FragColor.g + gl_FragColor.b) * 0.333;\n")
            text.append("gl_FragColor.rgb = mix(vec3(avgColor), gl_FragColor.rgb, ").append(UNIFORM_SATURATION).append(");\n")
        }

        text.append("}\n")
    }
}
