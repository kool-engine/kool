package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.AlphaMode

fun pbrShaderKsl(cfgBlock: KslPbrShader.Config.() -> Unit): KslPbrShader {
    val cfg = KslPbrShader.Config().apply(cfgBlock)
    return KslPbrShader(cfg)
}

class KslPbrShader(cfg: Config, model: KslProgram = Model(cfg)) : KslShader(model, cfg.pipelineCfg) {

    var uniformDiffuseColor: Vec4f by uniform4f(cfg.colorCfg.primaryUniformColor?.uniformName, cfg.colorCfg.primaryUniformColor?.defaultColor)
    var colorTexture: Texture2d? by texture2d(cfg.colorCfg.primaryTextureColor?.textureName, cfg.colorCfg.primaryTextureColor?.defaultTexture)
    var normalMap: Texture2d? by texture2d(cfg.normalMapCfg.normalMapName, cfg.normalMapCfg.defaultNormalMap)

    var roughness: Float by uniform1f("uRoughness", cfg.roughness)
    var metallic: Float by uniform1f("uMetallic", cfg.metallic)

    class Config {
        val vertexConfig = BasicVertexConfig()
        val colorCfg = ColorBlockConfig()
        val normalMapCfg = NormalMapConfig()
        val pipelineCfg = PipelineConfig()
        val shadowCfg = ShadowConfig()

        var colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        var maxNumberOfLights = 4
        var alphaMode: AlphaMode = AlphaMode.Blend()

        var metallic = 0f
        var roughness = 0.5f

        var modelCustomizer: (KslProgram.() -> Unit)? = null

        fun color(block: ColorBlockConfig.() -> Unit) {
            colorCfg.apply(block)
        }

        fun normalMapping(block: NormalMapConfig.() -> Unit) {
            normalMapCfg.apply(block)
        }

        fun pipeline(block: PipelineConfig.() -> Unit) {
            pipelineCfg.apply(block)
        }

        fun shadow(block: ShadowConfig.() -> Unit) {
            shadowCfg.apply(block)
        }

        fun vertices(block: BasicVertexConfig.() -> Unit) {
            vertexConfig.apply(block)
        }
    }

    class Model(cfg: Config) : KslProgram("PBR Shader") {
        init {
            val camData = cameraData()
            val positionWorldSpace = interStageFloat3("positionWorldSpace")
            val normalWorldSpace = interStageFloat3("normalWorldSpace")
            var tangentWorldSpace: KslInterStageVector<KslTypeFloat4, KslTypeFloat1>? = null

            val texCoordBlock: TexCoordAttributeBlock
            val shadowMapVertexStage: ShadowBlockVertexStage

            vertexStage {
                main {
                    val uModelMat = modelMatrix()
                    val viewProj = mat4Var(camData.viewProjMat)
                    val modelMat = mat4Var(uModelMat.matrix)
                    if (cfg.vertexConfig.isInstanced) {
                        val instanceModelMat = instanceAttribMat4(Attribute.INSTANCE_MODEL_MAT.name)
                        modelMat *= instanceModelMat
                    }
                    if (cfg.vertexConfig.isArmature) {
                        val armatureBlock = armatureBlock(cfg.vertexConfig.maxNumberOfBones)
                        armatureBlock.inBoneWeights(vertexAttribFloat4(Attribute.WEIGHTS.name))
                        armatureBlock.inBoneIndices(vertexAttribInt4(Attribute.JOINTS.name))
                        modelMat *= armatureBlock.outBoneTransform
                    }

                    // transform vertex attributes into world space and forward them to fragment stage
                    val localPos = float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                    val localNormal = float4Value(vertexAttribFloat3(Attribute.NORMALS.name), 0f)

                    // world position and normal are made available via ports for custom models to modify them
                    val worldPos = float3Port("worldPos", float3Var((modelMat * localPos).xyz))
                    val worldNormal = float3Port("worldNormal", float3Var(normalize((modelMat * localNormal).xyz)))

                    positionWorldSpace.input set worldPos
                    normalWorldSpace.input set worldNormal
                    outPosition set (viewProj * float4Value(worldPos, 1f))

                    // if normal mapping is enabled, the input vertex data is expected to have a tangent attribute
                    if (cfg.normalMapCfg.isNormalMapped) {
                        tangentWorldSpace = interStageFloat4().apply {
                            input set modelMat * float4Value(vertexAttribFloat4(Attribute.TANGENTS.name).xyz, 0f)
                        }
                    }

                    // texCoordBlock is used by various other blocks to access texture coordinate vertex
                    // attributes (usually either none, or Attribute.TEXTURE_COORDS but there can be more)
                    texCoordBlock = texCoordAttributeBlock()

                    // project coordinates into shadow map / light space
                    shadowMapVertexStage = vertexShadowBlock(cfg.shadowCfg) {
                        inPositionWorldSpace(worldPos)
                        inNormalWorldSpace(worldNormal)
                    }
                }
            }

            fragmentStage {
                val uRoughness = uniformFloat1("uRoughness")
                val uMetallic = uniformFloat1("uMetallic")
                val uNormalMapStrength = uniformFloat1("uNormalMapStrength")

                val lightData = sceneLightData(cfg.maxNumberOfLights)

                main {
                    // determine main color (albedo)
                    val colorBlock = fragmentColorBlock(cfg.colorCfg)
                    val fragmentColor = float4Port("fragmentColor", colorBlock.outColor)

                    // discard fragment output if alpha mode is mask and fragment alpha value is below cutoff value
                    (cfg.alphaMode as? AlphaMode.Mask)?.let { mask ->
                        `if` (fragmentColor.a lt mask.cutOff.const) {
                            discard()
                        }
                    }

                    val vertexNormal = float3Var(normalize(normalWorldSpace.output))
                    if (cfg.pipelineCfg.cullMethod.isBackVisible && cfg.vertexConfig.isFlipBacksideNormals) {
                        `if` (!inIsFrontFacing) {
                            vertexNormal *= (-1f).const3
                        }
                    }

                    // do normal map computations (if enabled) and adjust material block input normal accordingly
                    val bumpedNormal = if (cfg.normalMapCfg.isNormalMapped) {
                        normalMapBlock(cfg.normalMapCfg) {
                            inTangentWorldSpace(normalize(tangentWorldSpace!!.output))
                            inNormalWorldSpace(vertexNormal)
                            inStrength(uNormalMapStrength)
                            inTexCoords(texCoordBlock.getAttributeCoords(cfg.normalMapCfg.coordAttribute))
                        }.outBumpNormal
                    } else {
                        vertexNormal
                    }

                    // make final normal value available to model customizer
                    val normal = float3Port("normal", bumpedNormal)

                    // create an array with light strength values per light source (1.0 = full strength)
                    val shadowFactors = floatArray(lightData.maxLightCount, 1f.const)
                    // adjust light strength values by shadow maps
                    fragmentShadowBlock(shadowMapVertexStage, shadowFactors)

                    // main material block
                    val material = pbrMaterialBlock {
                        inCamPos(camData.position)
                        inNormal(normal)
                        inFragmentPos(positionWorldSpace.output)
                        inBaseColor(fragmentColor.rgb)

                        inRoughness(uRoughness)
                        inMetallic(uMetallic)

                        setLightData(lightData, shadowFactors)
                    }

                    // set fragment stage output color
                    val outRgb = float3Var(material.outColor)
                    outRgb set convertColorSpace(outRgb, cfg.colorSpaceConversion)
                    if (cfg.pipelineCfg.blendMode == BlendMode.BLEND_PREMULTIPLIED_ALPHA) {
                        outRgb set outRgb * fragmentColor.a
                    }

                    when (cfg.alphaMode) {
                        is AlphaMode.Blend -> colorOutput(outRgb, fragmentColor.a)
                        is AlphaMode.Mask -> colorOutput(outRgb, 1f.const)
                        is AlphaMode.Opaque -> colorOutput(outRgb, 1f.const)
                    }
                }
            }

            cfg.modelCustomizer?.invoke(this)
        }
    }
}