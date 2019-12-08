package de.fabmax.kool.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.InstancedMesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.animation.Armature

/**
 * @author fabmax
 */
open class GlslGenerator {
    companion object {
        const val U_MVP_MATRIX = "uMvpMatrix"
        const val U_MODEL_MATRIX = "uModelMatrix"
        const val U_VIEW_MATRIX = "uViewMatrix"
        const val U_PROJ_MATRIX = "uProjMatrix"
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
        const val U_ENVIRONMENT_MAP = "uEnvironmentMap"
        const val U_REFLECTIVENESS = "uReflectivity"

        const val V_TEX_COORD = "vTexCoord"
        const val V_EYE_DIRECTION = "vEyeDirection_cameraspace"
        const val V_LIGHT_DIRECTION = "vLightDirection_cameraspace"
        const val V_NORMAL = "vNormal_cameraspace"
        const val V_NORMAL_WORLDSPACE = "vNormalWorldspace"
        const val V_COLOR = "vFragmentColor"
        const val V_DIFFUSE_LIGHT_COLOR = "vDiffuseLightColor"
        const val V_SPECULAR_LIGHT_COLOR = "vSpecularLightColor"
        const val V_POSITION_WORLDSPACE = "vPositionWorldspace"
        const val V_POSITION_LIGHTSPACE = "vPositionLightspace"
        const val V_POSITION_CLIPSPACE_Z = "vPositionClipspaceZ"
        const val V_TANGENT = "vTangent"

        const val L_VS_POSITION = "position"

        const val L_FS_TEX_COORD = "texUV"
        const val L_FS_TEX_COLOR = "texColor"
        const val L_FS_VERTEX_COLOR = "vertColor"
        const val L_FS_STATIC_COLOR = "staticColor"
        const val L_FS_REFLECTIVITY = "reflectivity"

        private val shadowOffsets = listOf(
                -0.9420f, -0.3990f,
                +0.9456f, -0.7689f,
                -0.0942f, -0.9294f,
                +0.3450f, +0.2939f,
                -0.9159f, +0.4577f,
                -0.8154f, -0.8791f,
                -0.3828f, +0.2768f,
                +0.9748f, +0.7565f,
                +0.4432f, -0.9751f,
                +0.5374f, -0.4737f,
                -0.2650f, -0.4189f,
                +0.7920f, +0.1909f,
                -0.2419f, +0.9971f,
                -0.8141f, +0.9144f,
                +0.1998f, +0.7864f,
                +0.1438f, -0.1410f
        )
    }

    interface GlslInjector {
        fun vsHeader(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
        fun vsAfterInput(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
        fun vsBeforeProj(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
        fun vsAfterProj(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
        fun vsEnd(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }

        fun fsHeader(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
        fun fsAfterInput(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
        fun fsBeforeSampling(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
        fun fsAfterSampling(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
        fun fsAfterLighting(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
        fun fsEnd(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }

        fun geomShader(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) { }
    }

    val injectors = mutableListOf<GlslInjector>()
    val customUniforms = mutableListOf<Uniform<*>>()
    val customAttributes = mutableListOf<Attribute>()

    // keywords are set when shader is generated, before glCapabilites might not be initialized
    lateinit var vsIn: String
    lateinit var vsOut: String
    lateinit var fsIn: String
    lateinit var fsOut: String
    lateinit var fsOutBody: String
    lateinit var texSampler: String

    fun generate(shaderProps: ShaderProps, node: Node, ctx: KoolContext): Shader.Source {
        vsIn = ctx.glCapabilities.glslDialect.vsIn
        vsOut = ctx.glCapabilities.glslDialect.vsOut
        fsIn = ctx.glCapabilities.glslDialect.fsIn
        fsOut = ctx.glCapabilities.glslDialect.fragColorHead
        fsOutBody = ctx.glCapabilities.glslDialect.fragColorBody
        texSampler = ctx.glCapabilities.glslDialect.texSampler

        return Shader.Source(generateVertShader(shaderProps, node, ctx),
                generateGeomShader(shaderProps, node, ctx),
                generateFragShader(shaderProps, node, ctx))
    }

    private fun generateVertShader(shaderProps: ShaderProps, node: Node, ctx: KoolContext): String {
        val text = StringBuilder("${ctx.glCapabilities.glslDialect.version}\n")

        injectors.forEach { it.vsHeader(shaderProps, node, text, ctx) }
        generateVertInputCode(shaderProps, node, text, ctx)
        generateVertBodyCode(shaderProps, node, text, ctx)

        return text.toString()
    }

    private fun generateFragShader(shaderProps: ShaderProps, node: Node, ctx: KoolContext): String {
        val text = StringBuilder("${ctx.glCapabilities.glslDialect.version}\n")

        injectors.forEach { it.fsHeader(shaderProps, node, text, ctx) }
        generateFragInputCode(shaderProps, node, text, ctx)
        generateFragBodyCode(shaderProps, node, text, ctx)

        return text.toString()
    }

    private fun generateGeomShader(shaderProps: ShaderProps, node: Node, ctx: KoolContext): String {
        val text = StringBuilder()
        injectors.forEach { it.geomShader(shaderProps, node, text, ctx) }
        var txt = text.toString()
        if (txt.isNotEmpty() && !txt.startsWith("#version")) {
            txt = "${ctx.glCapabilities.glslDialect.version}\n" + txt
        }
        return txt
    }

    private fun generateVertInputCode(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
        text.append("$vsIn vec3 ${Attribute.POSITIONS.glslSrcName};\n")
        text.append("uniform mat4 $U_MODEL_MATRIX;\n")
        text.append("uniform mat4 $U_VIEW_MATRIX;\n")
        text.append("uniform mat4 $U_MVP_MATRIX;\n")

        if (shaderProps.isInstanced) {
            // for instanced meshes there is an additional instance model matrix attribute
            text.append("$vsIn mat4 ${InstancedMesh.MODEL_INSTANCES_0.glslSrcName};\n")
        }

        // add light dependent uniforms and attributes
        if (shaderProps.lightModel != LightModel.NO_LIGHTING) {
            text.append("$vsIn vec3 ${Attribute.NORMALS.glslSrcName};\n")
            text.append("uniform vec3 $U_LIGHT_DIRECTION;\n")

            if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
                // Phong model specific stuff
                text.append("$vsOut vec3 $V_EYE_DIRECTION;\n")
                text.append("$vsOut vec3 $V_LIGHT_DIRECTION;\n")
                text.append("$vsOut vec3 $V_NORMAL;\n")

                if (shaderProps.isNormalMapped) {
                    text.append("$vsIn vec3 ${Attribute.TANGENTS.glslSrcName};\n")
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
            text.append("$vsIn vec2 ${Attribute.TEXTURE_COORDS.glslSrcName};\n")
            text.append("$vsOut vec2 $V_TEX_COORD;\n")

        }
        if (shaderProps.isVertexColor) {
            // vertex color
            text.append("$vsIn vec4 ${Attribute.COLORS.glslSrcName};\n")
            text.append("$vsOut vec4 $V_COLOR;\n")
        }

        if (shaderProps.numBones > 0 && ctx.glCapabilities.shaderIntAttribs) {
            text.append("$vsIn ivec4 ${Armature.BONE_INDICES.glslSrcName};\n")
            text.append("$vsIn vec4 ${Armature.BONE_WEIGHTS.glslSrcName};\n")
            text.append("uniform mat4 $U_BONES[${shaderProps.numBones}];\n")
        }

        val shadowMap = node.scene?.lighting?.shadowMap
        if (shaderProps.isReceivingShadows && shadowMap != null) {
            text.append("uniform mat4 $U_SHADOW_MVP[${shadowMap.numMaps}];\n")
            text.append("$vsOut vec4 $V_POSITION_LIGHTSPACE[${shadowMap.numMaps}];\n")
            text.append("$vsOut float $V_POSITION_CLIPSPACE_Z;\n")
        }

        // forward vertex position and / or normal in world space to fragment shader if needed
        val isFsNeedsWorldPos = shaderProps.fogModel != FogModel.FOG_OFF || shaderProps.isEnvironmentMapped
        if (isFsNeedsWorldPos) {
            text.append("$vsOut vec3 $V_POSITION_WORLDSPACE;\n")
        }
        val isFsNeedsWorldNormal = shaderProps.isEnvironmentMapped
        if (isFsNeedsWorldNormal) {
            text.append("$vsOut vec3 $V_NORMAL_WORLDSPACE;\n")
        }

        for (uniform in customUniforms) {
            text.append("uniform ${uniform.type} ${uniform.name};\n")
        }
        for (attrib in customAttributes) {
            TODO()
            //text.append("$vsIn ${attrib.type.glslTypeName} ${attrib.glslSrcName};\n")
        }

        injectors.forEach { it.vsAfterInput(shaderProps, node, text, ctx) }
    }

    private fun generateVertBodyCode(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
        text.append("\nvoid main() {\n")

        var mvpMat = U_MVP_MATRIX
        var modelMat = U_MODEL_MATRIX
        if (shaderProps.isInstanced) {
            mvpMat = "mvpMatInst"
            text.append("mat4 $mvpMat = $U_MVP_MATRIX * ${InstancedMesh.MODEL_INSTANCES_0.glslSrcName};\n")
            modelMat = "modelMatInst"
            text.append("mat4 $modelMat = $U_MODEL_MATRIX * ${InstancedMesh.MODEL_INSTANCES_0.glslSrcName};\n")
        }

        text.append("vec4 $L_VS_POSITION = vec4(${Attribute.POSITIONS}, 1.0);\n")
        if (shaderProps.lightModel != LightModel.NO_LIGHTING) {
            text.append("vec4 normal = vec4(${Attribute.NORMALS}, 0.0);\n")
            if (shaderProps.isNormalMapped) {
                text.append("vec4 tangent = vec4(${Attribute.TANGENTS}, 0.0);\n")
            }
        }

        injectors.forEach { it.vsBeforeProj(shaderProps, node, text, ctx) }

        if (shaderProps.numBones > 0 && ctx.glCapabilities.shaderIntAttribs) {
            text.append("mat4 boneT = $U_BONES[${Armature.BONE_INDICES}[0]] * ${Armature.BONE_WEIGHTS}[0];\n")
            text.append("boneT += $U_BONES[${Armature.BONE_INDICES}[1]] * ${Armature.BONE_WEIGHTS}[1];\n")
            text.append("boneT += $U_BONES[${Armature.BONE_INDICES}[2]] * ${Armature.BONE_WEIGHTS}[2];\n")
            text.append("boneT += $U_BONES[${Armature.BONE_INDICES}[3]] * ${Armature.BONE_WEIGHTS}[3];\n")
            text.append("$L_VS_POSITION = boneT * $L_VS_POSITION;\n")

            if (shaderProps.lightModel != LightModel.NO_LIGHTING) {
                text.append("normal = boneT * normal;\n")
                if (shaderProps.isNormalMapped) {
                    text.append("tangent = boneT * tangent;\n")
                }
            }
        }

        // output position of the vertex in clip space: MVP * position
        text.append("gl_Position = $mvpMat * $L_VS_POSITION;\n")

        injectors.forEach { it.vsAfterProj(shaderProps, node, text, ctx) }

        val shadowMap = node.scene?.lighting?.shadowMap
        if (shaderProps.isReceivingShadows && shadowMap != null) {
            for (i in 0 until shadowMap.numMaps) {
                text.append("$V_POSITION_LIGHTSPACE[$i] = $U_SHADOW_MVP[$i] * ($modelMat * $L_VS_POSITION);\n")
            }
            text.append("$V_POSITION_CLIPSPACE_Z = gl_Position.z;\n")
        }

        // forward vertex position and / or normal in world space to fragment shader if needed
        val isFsNeedsWorldPos = shaderProps.fogModel != FogModel.FOG_OFF || shaderProps.isEnvironmentMapped
        if (isFsNeedsWorldPos) {
            text.append("$V_POSITION_WORLDSPACE = ($modelMat * $L_VS_POSITION).xyz;\n")
        }
        val isFsNeedsWorldNormal = shaderProps.isEnvironmentMapped
        if (isFsNeedsWorldNormal) {
            text.append("$V_NORMAL_WORLDSPACE = ($modelMat * normal).xyz;\n")
        }

        if (shaderProps.isTextureColor || shaderProps.isNormalMapped) {
            // interpolate vertex texture coordinate for usage in fragment shader
            text.append("$V_TEX_COORD = ${Attribute.TEXTURE_COORDS.glslSrcName};\n")

        }
        if (shaderProps.isVertexColor) {
            // interpolate vertex color for usage in fragment shader
            text.append("$V_COLOR = ${Attribute.COLORS.glslSrcName};\n")
        }

        if (shaderProps.lightModel == LightModel.PHONG_LIGHTING) {
            // vector from vertex to camera, in camera space. In camera space, the camera is at the origin (0, 0, 0).
            text.append("$V_EYE_DIRECTION = -($U_VIEW_MATRIX * ($modelMat * $L_VS_POSITION)).xyz;\n")

            // light direction, in camera space. M is left out because light position is already in world space.
            text.append("$V_LIGHT_DIRECTION = ($U_VIEW_MATRIX * vec4($U_LIGHT_DIRECTION, 0.0)).xyz;\n")

            // normal of the the vertex, in camera space
            text.append("$V_NORMAL = ($U_VIEW_MATRIX * ($modelMat * normal)).xyz;\n")

            if (shaderProps.isNormalMapped) {
                text.append("$V_TANGENT = ($U_VIEW_MATRIX * ($modelMat * tangent)).xyz;\n")
            }

        } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
            // vector from vertex to camera, in camera space. In camera space, the camera is at the origin (0, 0, 0).
            text.append("vec3 e = normalize(-($U_VIEW_MATRIX * ($modelMat * $L_VS_POSITION)).xyz);\n")

            // light direction, in camera space. M is left out because light position is already in world space.
            text.append("vec3 l = normalize(($U_VIEW_MATRIX * vec4($U_LIGHT_DIRECTION, 0.0)).xyz);\n")

            // normal of the the vertex, in camera space
            text.append("vec3 n = normalize(($U_VIEW_MATRIX * ($modelMat * normal)).xyz);\n")

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

        injectors.forEach { it.vsEnd(shaderProps, node, text, ctx) }
        text.append("}\n")
    }

    private fun generateFragInputCode(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
        text.append("precision highp float;\n")
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

        val shadowMap = node.scene?.lighting?.shadowMap
        if (shaderProps.isReceivingShadows && shadowMap != null) {
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

        val isFsNeedsWorldPos = shaderProps.fogModel != FogModel.FOG_OFF || shaderProps.isEnvironmentMapped
        if (isFsNeedsWorldPos) {
            text.append("$fsIn vec3 $V_POSITION_WORLDSPACE;\n")
        }
        val isFsNeedsWorldNormal = shaderProps.isEnvironmentMapped
        if (isFsNeedsWorldNormal) {
            text.append("$fsIn vec3 $V_NORMAL_WORLDSPACE;\n\n")
        }
        val isFsNeedsCamPos = shaderProps.fogModel != FogModel.FOG_OFF || shaderProps.isEnvironmentMapped
        if (isFsNeedsCamPos) {
            text.append("uniform vec3 $U_CAMERA_POSITION;\n")
        }
        if (shaderProps.isEnvironmentMapped) {
            text.append("uniform float $U_REFLECTIVENESS;\n")
            text.append("uniform samplerCube $U_ENVIRONMENT_MAP;\n")
        }

        // add fog uniforms
        if (shaderProps.fogModel != FogModel.FOG_OFF) {
            text.append("uniform vec4 $U_FOG_COLOR;\n")
            text.append("uniform float $U_FOG_RANGE;\n")
        }

        for (uniform in customUniforms) {
            text.append("uniform ${uniform.type} ${uniform.name};\n")
        }
        text.append("$fsOut\n")

        injectors.forEach { it.fsAfterInput(shaderProps, node, text, ctx) }
    }

    private fun generateFragBodyCode(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
        val shadowMap = node.scene?.lighting?.shadowMap
        if (shaderProps.isReceivingShadows && shadowMap != null) {
            text.append("float calcShadowFactor(sampler2D shadowTex, vec3 projPos, float pxSz, float accLvl) {\n")

            fun addSample(fac: String, x: Float, y: Float) {
                text.append("shadowMapDepth = $texSampler(shadowTex, projPos.xy + vec2($x * pxSz, $y * pxSz)).x;\n")
                text.append("$fac += step(projPos.z + accLvl, shadowMapDepth);\n")
            }
            text.append("  float factor = 0.0;\n")
            text.append("  float shadowMapDepth = 0.0;\n")
            for (i in 0..8) {
                addSample("factor", shadowOffsets[i*2], shadowOffsets[i*2+1])
            }
            text.append("  factor /= 9.0;\n")
            text.append("  return factor;\n")

//            fun quadSample(fac: String, tx: String, ty: String) {
//                for (i in 0..3) {
//                    val ox = shadowOffsets[i*2]
//                    val oy = shadowOffsets[i*2+1]
//                    text.append("d = $texSampler(shadowTex, vec2($tx + $ox * pxSz, $ty + $oy * pxSz)).x;\n")
//                    text.append("$fac += step(projPos.z + accLvl, d);\n")
//                }
//                text.append("$fac /= 4.0;\n")
//            }
//            text.append("float su = 0.5 * pxSz;\n")
//            text.append("float tx0 = float(int(projPos.x / su)) * su;\n")
//            text.append("float ty0 = float(int(projPos.y / su)) * su;\n")
//            text.append("float tx1 = float(int(projPos.x / su) + 1) * su;\n")
//            text.append("float ty1 = float(int(projPos.y / su) + 1) * su;\n")
//            text.append("float wx = (tx1 - projPos.x) / su;\n")
//            text.append("float wy = (ty1 - projPos.y) / su;\n")
//            text.append("float d = 0.0;\n")
//            text.append("float s00 = 0.0;\n")
//            text.append("float s01 = 0.0;\n")
//            text.append("float s10 = 0.0;\n")
//            text.append("float s11 = 0.0;\n")
//            quadSample("s00", "tx0", "ty0")
//            quadSample("s01", "tx0", "ty1")
//            quadSample("s10", "tx1", "ty0")
//            quadSample("s11", "tx1", "ty1")
//            text.append("return (s00 * wy + s01 * (1.0-wy)) * wx + (s10 * wy + s11 * (1.0-wy)) * (1.0-wx);\n")

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

        if (shaderProps.isTextureColor) {
            text.append("vec2 $L_FS_TEX_COORD = $V_TEX_COORD;\n")
        }

        if (shaderProps.isEnvironmentMapped) {
            text.append("float $L_FS_REFLECTIVITY = $U_REFLECTIVENESS;\n")
        }

        injectors.forEach { it.fsBeforeSampling(shaderProps, node, text, ctx) }

        if (shaderProps.isTextureColor) {
            // get base fragment color from texture
            text.append("vec4 $L_FS_TEX_COLOR = $texSampler($U_TEXTURE_0, $L_FS_TEX_COORD);\n")
            if (!ctx.glCapabilities.premultipliedAlphaTextures) {
                text.append("$L_FS_TEX_COLOR.rgb *= $L_FS_TEX_COLOR.a;\n")
            }
            text.append("$fsOutBody = $L_FS_TEX_COLOR;\n")
        }
        if (shaderProps.isVertexColor) {
            // get base fragment color from vertex attribute
            text.append("vec4 $L_FS_VERTEX_COLOR = $V_COLOR;\n")
            text.append("$L_FS_VERTEX_COLOR.rgb *= $L_FS_VERTEX_COLOR.a;\n")
            text.append("$fsOutBody = $L_FS_VERTEX_COLOR;\n")
        }
        if (shaderProps.isStaticColor) {
            // get base fragment color from static color uniform
            text.append("vec4 $L_FS_STATIC_COLOR = $U_STATIC_COLOR;\n")
            text.append("$L_FS_STATIC_COLOR.rgb *= $L_FS_STATIC_COLOR.a;\n")
            text.append("$fsOutBody = $L_FS_STATIC_COLOR;\n")
        }

        injectors.forEach { it.fsAfterSampling(shaderProps, node, text, ctx) }

        if (shaderProps.isDiscardTranslucent) {
            text.append("if ($fsOutBody.a == 0.0) { discard; }")
        }

        if (shaderProps.isReceivingShadows && shadowMap != null) {
            for (i in 0 until shadowMap.numMaps) {
                text.append("if ($V_POSITION_CLIPSPACE_Z <= $U_CLIP_SPACE_FAR_Z[$i]) {\n")
                text.append("  vec3 projPos = $V_POSITION_LIGHTSPACE[$i].xyz / $V_POSITION_LIGHTSPACE[$i].w;\n")
                text.append("  float pxSz = 1.0 / float($U_SHADOW_TEX_SZ[$i]);\n")
                text.append("  shadowFactor = calcShadowFactor(${U_SHADOW_TEX}_$i, projPos, pxSz, ${shaderProps.shadowDepthOffset});\n")
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
                if (!shaderProps.isTwoSidedLighting) {
                    text.append("float cosTheta = clamp(dot(n, l), 0.0, 1.0);\n")
                } else {
                    text.append("float cosTheta = abs(dot(n, l));\n")
                }
                // direction in which the light is reflected
                text.append("vec3 r = reflect(-l, n);\n")
                // cosine of the angle between the eye vector and the reflect vector
                text.append("float cosAlpha = clamp(dot(e, r), 0.0, 1.0);\n")

                text.append("vec3 materialAmbientColor = $fsOutBody.rgb * ${shaderProps.ambientColorBrightness};\n")
                text.append("vec3 materialDiffuseColor = $fsOutBody.rgb * $U_LIGHT_COLOR * cosTheta;\n")
                text.append("vec3 materialSpecularColor = $U_LIGHT_COLOR * $U_SPECULAR_INTENSITY * pow(cosAlpha, $U_SHININESS) * $fsOutBody.a * shadowFactor;\n")

            } else if (shaderProps.lightModel == LightModel.GOURAUD_LIGHTING) {
                text.append("vec3 materialAmbientColor = $fsOutBody.rgb * ${shaderProps.ambientColorBrightness};\n")
                text.append("vec3 materialDiffuseColor = $fsOutBody.rgb * $V_DIFFUSE_LIGHT_COLOR;\n")
                text.append("vec3 materialSpecularColor = $V_SPECULAR_LIGHT_COLOR * $fsOutBody.a * shadowFactor;\n")
            }

            // compute output color
            text.append("$fsOutBody.rgb = materialAmbientColor + materialDiffuseColor + materialSpecularColor;\n")
        }

        injectors.forEach { it.fsAfterLighting(shaderProps, node, text, ctx) }

        if (shaderProps.isEnvironmentMapped) {
            text.append("vec3 eyeDir = normalize($V_POSITION_WORLDSPACE - $U_CAMERA_POSITION);\n")
            text.append("vec3 reflectedDir = reflect(eyeDir, normalize($V_NORMAL_WORLDSPACE));\n")
            text.append("$fsOutBody.rgb = mix($fsOutBody.rgb , texture($U_ENVIRONMENT_MAP, reflectedDir).rgb, $L_FS_REFLECTIVITY);\n")
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

        injectors.forEach { it.fsEnd(shaderProps, node, text, ctx) }
        text.append("}\n")
    }
}
