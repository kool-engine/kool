package de.fabmax.kool.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.animation.Armature

/**
 * @author fabmax
 */
open class GlslGenerator {
    companion object {
        const val U_MVP_MATRIX = "uMvpMatrix"
        const val U_MODEL_MATRIX = "uModelMatrix"
        const val U_VIEW_MATRIX = "uViewMatrix"
        const val U_LIGHT_DIRECTION = "uLightDirection"
        const val U_LIGHT_COLOR = "uLightColor"
        const val U_SHININESS = "uShininess"
        const val U_SPECULAR_INTENSITY = "uSpecularIntensity"
        const val U_CAMERA_POSITION = "uCameraPosition"
        const val U_FOG_COLOR = "uFogColor"
        const val U_FOG_RANGE = "uFogRange"
        const val U_TEXTURE_0 = "uTexture0"
        const val U_STATIC_COLOR = "uStaticColor"
        const val U_ALPHA = "uAlpha"
        const val U_SATURATION = "uSaturation"
        const val U_BONES = "uBones"
        const val U_SHADOW_MVP = "uShadowMvp"
        const val U_SHADOW_TEX = "uShadowTex"
        const val U_SHADOW_TEX_SZ = "uShadowTexSz"
        const val U_CLIP_SPACE_FAR_Z = "uClipSpaceFarZ"
        const val U_NORMAL_MAP_0 = "uNormalMap0"

        const val V_TEX_COORD = "vTexCoord"
        const val V_EYE_DIRECTION = "vEyeDirection_cameraspace"
        const val V_LIGHT_DIRECTION = "vLightDirection_cameraspace"
        const val V_NORMAL = "vNormal_cameraspace"
        const val V_COLOR = "vFragmentColor"
        const val V_DIFFUSE_LIGHT_COLOR = "vDiffuseLightColor"
        const val V_SPECULAR_LIGHT_COLOR = "vSpecularLightColor"
        const val V_POSITION_WORLDSPACE = "vPositionWorldspace"
        const val V_POSITION_LIGHTSPACE = "vPositionLightspace"
        const val V_POSITION_CLIPSPACE_Z = "vPositionClipspaceZ"
        const val V_TANGENT = "vTangent"

        const val L_TEX_COLOR = "texColor"
        const val L_VERTEX_COLOR = "vertColor"
        const val L_STATIC_COLOR = "staticColor"
    }

    interface GlslInjector {
        fun vsHeader(text: StringBuilder, ctx: KoolContext) { }
        fun vsStart(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }
        fun vsAfterInput(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }
        fun vsBeforeProj(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }
        fun vsAfterProj(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }
        fun vsEnd(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }

        fun fsHeader(text: StringBuilder, ctx: KoolContext) { }
        fun fsStart(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }
        fun fsAfterInput(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }
        fun fsBeforeSampling(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }
        fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }
        fun fsEnd(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) { }
    }

    val injectors = mutableListOf<GlslInjector>()
    val customUniforms = mutableListOf<Uniform<*>>()

    // keywords are set when shader is generated, before glCapabilites might not be initialized
    private var vsIn = ""
    private var vsOut = ""
    private var fsIn = ""
    private var fsOut = ""
    private var fsOutBody = ""
    private var texSampler = ""

    fun generate(shaderProps: ShaderProps, ctx: KoolContext): Shader.Source {
        vsIn = ctx.glCapabilities.glslDialect.vsIn
        vsOut = ctx.glCapabilities.glslDialect.vsOut
        fsIn = ctx.glCapabilities.glslDialect.fsIn
        fsOut = ctx.glCapabilities.glslDialect.fragColorHead
        fsOutBody = ctx.glCapabilities.glslDialect.fragColorBody
        texSampler = ctx.glCapabilities.glslDialect.texSampler

        return Shader.Source(generateVertShader(shaderProps, ctx), generateFragShader(shaderProps, ctx))
    }

    private fun generateVertShader(shaderProps: ShaderProps, ctx: KoolContext): String {
        val text = StringBuilder("${ctx.glCapabilities.glslDialect.version}\n")

        injectors.forEach { it.vsHeader(text, ctx) }

        injectors.forEach { it.vsStart(shaderProps, text, ctx) }
        generateVertInputCode(shaderProps, text, ctx)
        injectors.forEach { it.vsAfterInput(shaderProps, text, ctx) }
        generateVertBodyCode(shaderProps, text, ctx)

        return text.toString()
    }

    private fun generateFragShader(shaderProps: ShaderProps, ctx: KoolContext): String {
        val text = StringBuilder("${ctx.glCapabilities.glslDialect.version}\n")

        injectors.forEach { it.fsHeader(text, ctx) }

        injectors.forEach { it.fsStart(shaderProps, text, ctx) }
        generateFragInputCode(shaderProps, text, ctx)
        injectors.forEach { it.fsAfterInput(shaderProps, text, ctx) }
        generateFragBodyCode(shaderProps, text, ctx)

        return text.toString()
    }

    private fun generateVertInputCode(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
        // MVP matrices and vertex position attribute are always needed
        text.append("$vsIn vec3 ${Attribute.POSITIONS.name};\n")
        text.append("uniform mat4 $U_MVP_MATRIX;\n")
        text.append("uniform mat4 $U_MODEL_MATRIX;\n")
        text.append("uniform mat4 $U_VIEW_MATRIX;\n")

        // add light dependent uniforms and attributes
        if (shaderProps.lightModel != LightModel.NO_LIGHTING) {
            text.append("$vsIn vec3 ${Attribute.NORMALS.name};\n")
            text.append("uniform vec3 $U_LIGHT_DIRECTION;\n")

            if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
                // Phong model specific stuff
                text.append("$vsOut vec3 $V_EYE_DIRECTION;\n")
                text.append("$vsOut vec3 $V_LIGHT_DIRECTION;\n")
                text.append("$vsOut vec3 $V_NORMAL;\n")

                if (shaderProps.isNormalMapped) {
                    text.append("$vsIn vec3 ${Attribute.TANGENTS.name};\n")
                    text.append("$vsOut vec3 $V_TANGENT;\n")
                }

            } else {
                // Gouraud model specific stuff
                text.append("uniform vec3 $U_LIGHT_COLOR;\n")
                text.append("uniform float $U_SHININESS;\n")
                text.append("uniform float $U_SPECULAR_INTENSITY;\n")
                text.append("$vsOut vec3 $V_DIFFUSE_LIGHT_COLOR;\n")
                text.append("$vsOut vec3 $V_SPECULAR_LIGHT_COLOR;\n")
            }
        }

        // add color dependent attributes
        if (shaderProps.isTextureColor || shaderProps.isNormalMapped) {
            // texture color
            text.append("$vsIn vec2 ${Attribute.TEXTURE_COORDS.name};\n")
            text.append("$vsOut vec2 $V_TEX_COORD;\n")

        }
        if (shaderProps.isVertexColor) {
            // vertex color
            text.append("$vsIn vec4 ${Attribute.COLORS.name};\n")
            text.append("$vsOut vec4 $V_COLOR;\n")
        }

        if (shaderProps.numBones > 0 && ctx.glCapabilities.shaderIntAttribs) {
            text.append("$vsIn ivec4 ${Armature.BONE_INDICES.name};\n")
            text.append("$vsIn vec4 ${Armature.BONE_WEIGHTS.name};\n")
            text.append("uniform mat4 $U_BONES[${shaderProps.numBones}];\n")
        }

        val shadowMap = shaderProps.shadowMap
        if (shadowMap != null) {
            text.append("uniform mat4 $U_SHADOW_MVP[${shadowMap.numMaps}];\n")
            text.append("$vsOut vec4 $V_POSITION_LIGHTSPACE[${shadowMap.numMaps}];\n")
            text.append("$vsOut float $V_POSITION_CLIPSPACE_Z;\n")
        }

        // if fog is enabled, fragment shader needs to know the world position
        if (shaderProps.fogModel != FogModel.FOG_OFF) {
            text.append("$vsIn vec3 $V_POSITION_WORLDSPACE;\n")
        }

        for (uniform in customUniforms) {
            text.append("uniform ${uniform.type} ${uniform.name};\n")
        }
    }

    private fun generateVertBodyCode(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
        text.append("\nvoid main() {\n")

        text.append("vec4 position = vec4(${Attribute.POSITIONS}, 1.0);\n")
        if (shaderProps.lightModel != LightModel.NO_LIGHTING) {
            text.append("vec4 normal = vec4(${Attribute.NORMALS}, 0.0);\n")
            if (shaderProps.isNormalMapped) {
                text.append("vec4 tangent = vec4(${Attribute.TANGENTS}, 0.0);\n")
            }
        }

        injectors.forEach { it.vsBeforeProj(shaderProps, text, ctx) }

        if (shaderProps.numBones > 0 && ctx.glCapabilities.shaderIntAttribs) {
            text.append("mat4 boneT = $U_BONES[${Armature.BONE_INDICES}[0]] * ${Armature.BONE_WEIGHTS}[0];\n")
            text.append("boneT += $U_BONES[${Armature.BONE_INDICES}[1]] * ${Armature.BONE_WEIGHTS}[1];\n")
            text.append("boneT += $U_BONES[${Armature.BONE_INDICES}[2]] * ${Armature.BONE_WEIGHTS}[2];\n")
            text.append("boneT += $U_BONES[${Armature.BONE_INDICES}[3]] * ${Armature.BONE_WEIGHTS}[3];\n")
            text.append("position = boneT * position;\n")

            if (shaderProps.lightModel != LightModel.NO_LIGHTING) {
                text.append("normal = boneT * normal;\n")
                if (shaderProps.isNormalMapped) {
                    text.append("tangent = boneT * tangent;\n")
                }
            }
        }

        // output position of the vertex in clip space: MVP * position
        text.append("gl_Position = $U_MVP_MATRIX * position;\n")

        injectors.forEach { it.vsAfterProj(shaderProps, text, ctx) }

        val shadowMap = shaderProps.shadowMap
        if (shadowMap != null) {
            for (i in 0 until shadowMap.numMaps) {
                text.append("$V_POSITION_LIGHTSPACE[$i] = $U_SHADOW_MVP[$i] * ($U_MODEL_MATRIX * position);\n")
            }
            text.append("$V_POSITION_CLIPSPACE_Z = gl_Position.z;\n")
        }

        if (shaderProps.fogModel != FogModel.FOG_OFF) {
            text.append("$V_POSITION_WORLDSPACE = ($U_MODEL_MATRIX * position).xyz;\n")
        }

        if (shaderProps.isTextureColor || shaderProps.isNormalMapped) {
            // interpolate vertex texture coordinate for usage in fragment shader
            text.append("$V_TEX_COORD = ${Attribute.TEXTURE_COORDS.name};\n")

        }
        if (shaderProps.isVertexColor) {
            // interpolate vertex color for usage in fragment shader
            text.append("$V_COLOR = ${Attribute.COLORS.name};\n")
        }

        if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
            // vector from vertex to camera, in camera space. In camera space, the camera is at the origin (0, 0, 0).
            text.append("$V_EYE_DIRECTION = -($U_VIEW_MATRIX * ($U_MODEL_MATRIX * position)).xyz;\n")

            // light direction, in camera space. M is left out because light position is already in world space.
            text.append("$V_LIGHT_DIRECTION = ($U_VIEW_MATRIX * vec4($U_LIGHT_DIRECTION, 0.0)).xyz;\n")

            // normal of the the vertex, in camera space
            text.append("$V_NORMAL = ($U_VIEW_MATRIX * ($U_MODEL_MATRIX * normal)).xyz;\n")

            if (shaderProps.isNormalMapped) {
                text.append("$V_TANGENT = ($U_VIEW_MATRIX * ($U_MODEL_MATRIX * tangent)).xyz;\n")
            }

        } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
            // vector from vertex to camera, in camera space. In camera space, the camera is at the origin (0, 0, 0).
            text.append("vec3 e = normalize(-($U_VIEW_MATRIX * ($U_MODEL_MATRIX * position)).xyz);\n")

            // light direction, in camera space. M is left out because light position is already in world space.
            text.append("vec3 l = normalize(($U_VIEW_MATRIX * vec4($U_LIGHT_DIRECTION, 0.0)).xyz);\n")

            // normal of the the vertex, in camera space
            text.append("vec3 n = normalize(($U_VIEW_MATRIX * ($U_MODEL_MATRIX * normal)).xyz);\n")

            // cosine of angle between surface normal and light direction
            text.append("float cosTheta = clamp(dot(n, l), 0.0, 1.0);\n")
            // direction in which the light is reflected
            text.append("vec3 r = reflect(-l, n);\n")
            // cosine of the angle between the eye vector and the reflect vector
            text.append("float cosAlpha = clamp(dot(e, r), 0.0, 1.0);\n")

            // interpolate light colors for usage in fragment shader
            text.append("$V_DIFFUSE_LIGHT_COLOR = $U_LIGHT_COLOR * cosTheta;\n")
            text.append("$V_SPECULAR_LIGHT_COLOR = $U_LIGHT_COLOR * $U_SPECULAR_INTENSITY * pow(cosAlpha, $U_SHININESS);\n")
        }

        injectors.forEach { it.vsEnd(shaderProps, text, ctx) }
        text.append("}\n")
    }

    private fun generateFragInputCode(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
        text.append("precision highp float;\n")
        text.append("uniform mat4 $U_MODEL_MATRIX;\n")
        text.append("uniform mat4 $U_VIEW_MATRIX;\n")
        if (shaderProps.isAlpha) {
            text.append("uniform float $U_ALPHA;\n")
        }
        if (shaderProps.isSaturation) {
            text.append("uniform float $U_SATURATION;\n")
        }

        // add light dependent uniforms and varyings
        if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
            text.append("uniform vec3 $U_LIGHT_COLOR;\n")
            text.append("uniform float $U_SHININESS;\n")
            text.append("uniform float $U_SPECULAR_INTENSITY;\n")
            text.append("$fsIn vec3 $V_EYE_DIRECTION;\n")
            text.append("$fsIn vec3 $V_LIGHT_DIRECTION;\n")
            text.append("$fsIn vec3 $V_NORMAL;\n")

            if (shaderProps.isNormalMapped) {
                text.append("uniform sampler2D $U_NORMAL_MAP_0;\n")
                text.append("$fsIn vec3 $V_TANGENT;\n")
            }

        } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
            text.append("$fsIn vec3 $V_DIFFUSE_LIGHT_COLOR;\n")
            text.append("$fsIn vec3 $V_SPECULAR_LIGHT_COLOR;\n")
        }

        // add color dependent uniforms and varyings
        if (shaderProps.isTextureColor) {
            // texture color
            text.append("uniform sampler2D $U_TEXTURE_0;\n")
            text.append("$fsIn vec2 $V_TEX_COORD;\n")
        } else if (shaderProps.isNormalMapped) {
            text.append("$fsIn vec2 $V_TEX_COORD;\n")
        }
        if (shaderProps.isVertexColor) {
            // vertex color
            text.append("$fsIn vec4 $V_COLOR;\n")
        }
        if (shaderProps.isStaticColor) {
            // static color
            text.append("uniform vec4 $U_STATIC_COLOR;\n")
        }

        val shadowMap = shaderProps.shadowMap
        if (shadowMap != null) {
            text.append("$fsIn vec4 $V_POSITION_LIGHTSPACE[${shadowMap.numMaps}];\n")
            text.append("$fsIn float $V_POSITION_CLIPSPACE_Z;\n")

            // arrays of sampler2D uniforms are only supported with extensions in GL ES
            //text.append("uniform sampler2D $U_SHADOW_TEX[${shadowMap.subMaps.size}];\n")
            for (i in 0 until shadowMap.numMaps) {
                text.append("uniform sampler2D ${U_SHADOW_TEX}_$i;\n")
            }
            text.append("uniform int $U_SHADOW_TEX_SZ[${shadowMap.numMaps}];\n")
            text.append("uniform float $U_CLIP_SPACE_FAR_Z[${shadowMap.numMaps}];\n")
        }

        // add fog uniforms
        if (shaderProps.fogModel != FogModel.FOG_OFF) {
            text.append("uniform vec3 $U_CAMERA_POSITION;\n")
            text.append("uniform vec4 $U_FOG_COLOR;\n")
            text.append("uniform float $U_FOG_RANGE;\n")
            text.append("$fsIn vec3 $V_POSITION_WORLDSPACE;\n")
        }

        for (uniform in customUniforms) {
            text.append("uniform ${uniform.type} ${uniform.name};\n")
        }
        text.append(fsOut)
    }

    private fun generateFragBodyCode(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
        val shadowMap = shaderProps.shadowMap
        if (shadowMap != null) {
            fun addSample(x: Int, y: Int) {
                text.append("shadowMapDepth = $texSampler(shadowTex, projPos.xy + vec2(float($x) * off, float($y) * off)).x;\n")
                text.append("factor += step(projPos.z + accLvl, shadowMapDepth);\n")
            }

            text.append("float calcShadowFactor(sampler2D shadowTex, vec3 projPos, float off, float accLvl) {\n")
            text.append("  float factor = 0.0;\n")
            text.append("  float shadowMapDepth = 0.0;\n")
            for (y in -1..1) {
                for (x in -1..1) {
                    addSample(x, y)
                }
            }
            text.append("  return factor / 9.0;\n")

            text.append("}\n")
        }

        if (shaderProps.isNormalMapped) {
            text.append("vec3 calcBumpedNormal() {\n")
            text.append("  vec3 normal = normalize($V_NORMAL);\n")
            text.append("  vec3 tangent = normalize($V_TANGENT);\n")
            text.append("  tangent = normalize(tangent - dot(tangent, normal) * normal);\n")
            text.append("  vec3 bitangent = cross(tangent, normal);\n")
            text.append("  vec3 bumpMapNormal = $texSampler($U_NORMAL_MAP_0, $V_TEX_COORD).xyz;\n")
            text.append("  bumpMapNormal = 2.0 * bumpMapNormal - vec3(1.0, 1.0, 1.0);\n")
            text.append("  mat3 tbn = mat3(tangent, bitangent, normal);\n")
            text.append("  return normalize(tbn * bumpMapNormal);\n")
            text.append("}\n")
        }

        text.append("\nvoid main() {\n")
        text.append("float shadowFactor = 1.0;\n")

        injectors.forEach { it.fsBeforeSampling(shaderProps, text, ctx) }

        if (shaderProps.isTextureColor) {
            // get base fragment color from texture
            text.append("vec4 $L_TEX_COLOR = $texSampler($U_TEXTURE_0, $V_TEX_COORD);\n")
            text.append("$fsOutBody = $L_TEX_COLOR;\n")
        }
        if (shaderProps.isVertexColor) {
            // get base fragment color from vertex attribute
            text.append("vec4 $L_VERTEX_COLOR = $V_COLOR;\n")
            text.append("$L_VERTEX_COLOR.rgb *= $L_VERTEX_COLOR.a;\n")
            text.append("$fsOutBody = $L_VERTEX_COLOR;\n")
        }
        if (shaderProps.isStaticColor) {
            // get base fragment color from static color uniform
            text.append("vec4 $L_STATIC_COLOR = $U_STATIC_COLOR;\n")
            text.append("$L_STATIC_COLOR.rgb *= $L_STATIC_COLOR.a;\n")
            text.append("$fsOutBody = $L_STATIC_COLOR;\n")
        }

        injectors.forEach { it.fsAfterSampling(shaderProps, text, ctx) }

        if (shaderProps.isDiscardTranslucent) {
            text.append("if ($fsOutBody.a == 0.0) { discard; }")
        }

        if (shadowMap != null) {
            for (i in 0 until shadowMap.numMaps) {
                text.append("if ($V_POSITION_CLIPSPACE_Z <= $U_CLIP_SPACE_FAR_Z[$i]) {\n")
                text.append("  vec3 projPos = $V_POSITION_LIGHTSPACE[$i].xyz / $V_POSITION_LIGHTSPACE[$i].w;\n")
                text.append("  float off = 1.0 / float($U_SHADOW_TEX_SZ[$i]);\n")
                text.append("  shadowFactor = calcShadowFactor(${U_SHADOW_TEX}_$i, projPos, off, 0.0005);\n")
                text.append("}\n")
                if (i < shadowMap.numMaps - 1) {
                    text.append("else ")
                }
            }
            text.append("$fsOutBody.rgb *= shadowFactor / 2.0 + 0.5;\n")
        }

        if (shaderProps.lightModel != LightModel.NO_LIGHTING) {
            if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
                // normalize input vectors
                text.append("vec3 e = normalize($V_EYE_DIRECTION);\n")
                text.append("vec3 l = normalize($V_LIGHT_DIRECTION);\n")

                if (shaderProps.isNormalMapped) {
                    text.append("vec3 n = calcBumpedNormal();\n")
                } else {
                    text.append("vec3 n = normalize($V_NORMAL);\n")
                }

                // cosine of angle between surface normal and light direction
                text.append("float cosTheta = clamp(dot(n, l), 0.0, 1.0);\n")
                // direction in which the light is reflected
                text.append("vec3 r = reflect(-l, n);\n")
                // cosine of the angle between the eye vector and the reflect vector
                text.append("float cosAlpha = clamp(dot(e, r), 0.0, 1.0);\n")

                text.append("vec3 materialAmbientColor = $fsOutBody.rgb * vec3(0.42);\n")
                text.append("vec3 materialDiffuseColor = $fsOutBody.rgb * $U_LIGHT_COLOR * cosTheta;\n")
                text.append("vec3 materialSpecularColor = $U_LIGHT_COLOR * $U_SPECULAR_INTENSITY * pow(cosAlpha, $U_SHININESS) * $fsOutBody.a * shadowFactor;\n")

            } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
                text.append("vec3 materialAmbientColor = $fsOutBody.rgb * vec3(0.42);\n")
                text.append("vec3 materialDiffuseColor = $fsOutBody.rgb * $V_DIFFUSE_LIGHT_COLOR;\n")
                text.append("vec3 materialSpecularColor = $V_SPECULAR_LIGHT_COLOR * $fsOutBody.a * shadowFactor;\n")
            }

            // compute output color
            text.append("$fsOutBody = vec4(materialAmbientColor + materialDiffuseColor + materialSpecularColor, $fsOutBody.a);\n")

        }

        // add fog code
        if (shaderProps.fogModel != FogModel.FOG_OFF) {
            text.append("float d = 1.0 - clamp(length($U_CAMERA_POSITION - $V_POSITION_WORLDSPACE / $U_FOG_RANGE), 0.0, 1.0);\n")
            text.append("$fsOutBody.rgb = mix($U_FOG_COLOR.rgb, $fsOutBody.rgb, d * d * $U_FOG_COLOR.a);\n")
        }

        if (shaderProps.isAlpha) {
            text.append("$fsOutBody *= $U_ALPHA;\n")
        }
        if (shaderProps.isSaturation) {
            text.append("float avgColor = ($fsOutBody.r + $fsOutBody.g + $fsOutBody.b) * 0.333;\n")
            text.append("$fsOutBody.rgb = mix(vec3(avgColor), $fsOutBody.rgb, $U_SATURATION);\n")
        }

        injectors.forEach { it.fsEnd(shaderProps, text, ctx) }
        text.append("}\n")
    }
}
