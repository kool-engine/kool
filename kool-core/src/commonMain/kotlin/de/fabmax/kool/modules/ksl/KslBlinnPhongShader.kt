package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.Color

fun blinnPhongShader(cfgBlock: KslBlinnPhongShader.Config.() -> Unit): KslBlinnPhongShader {
    val cfg = KslBlinnPhongShader.Config().apply(cfgBlock)
    return KslBlinnPhongShader(cfg)
}

class KslBlinnPhongShader(cfg: Config, model: KslProgram = Model(cfg)) : KslShader(model, cfg.pipelineCfg) {

    var uniformDiffuseColor: Vec4f by uniform4f(cfg.colorCfg.primaryUniformColor?.uniformName, cfg.colorCfg.primaryUniformColor?.defaultColor)
    var colorTexture: Texture2d? by texture2d(cfg.colorCfg.primaryTextureColor?.textureName, cfg.colorCfg.primaryTextureColor?.defaultTexture)
    var normalMap: Texture2d? by texture2d(cfg.normalMapCfg.normalMapName, cfg.normalMapCfg.defaultNormalMap)

    var specularColor: Vec4f by uniform4f("uSpecularColor", cfg.specularColor)
    var ambientColor: Vec4f by uniform4f("uAmbientColor", cfg.ambientColor)
    var shininess: Float by uniform1f("uShininess", cfg.shininess)
    var specularGain: Float by uniform1f("uSpecularGain", cfg.specularGain)
    var normalMapStrength: Float by uniform1f("uNormalMapStrength", cfg.normalMapCfg.defaultStrength)

    class Config {
        val colorCfg = ColorBlockConfig()
        val normalMapCfg = NormalMapConfig()
        val pipelineCfg = PipelineConfig()
        val shadowCfg = ShadowConfig()

        var isInstanced = false
        var isOutputToSrgbColorSpace = true
        var isFlipBacksideNormals = true

        var maxNumberOfLights = 4

        var specularColor: Color = Color.WHITE
        var ambientColor: Color = Color(0.2f, 0.2f, 0.2f).toLinear()
        var shininess = 16f
        var specularGain = 1f

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
    }

    class Model(cfg: Config) : KslProgram("Blinn-Phong Shader") {
        init {
            val uMvp = mvpMatrix()
            val uModelMat = modelMatrix()

            val uSpecularColor = uniformFloat4("uSpecularColor")
            val uAmbientColor = uniformFloat4("uAmbientColor")
            val uShininess = uniformFloat1("uShininess")
            val uSpecularGain = uniformFloat1("uSpecularGain")
            val uNormalMapStrength = uniformFloat1("uNormalMapStrength")

            val positionWorldSpace = interStageFloat3()
            val normalWorldSpace = interStageFloat3()
            var tangentWorldSpace: KslInterStageVector<KslTypeFloat4, KslTypeFloat1>? = null

            val texCoordBlock: TexCoordAttributeBlock
            val shadowMapVertexStage: ShadowBlockVertexStage

            vertexStage {
                main {
                    val mvp = mat4Var(uMvp.matrix)
                    val modelMat = mat4Var(uModelMat.matrix)
                    if (cfg.isInstanced) {
                        val instanceModelMat = instanceAttribMat4(Attribute.INSTANCE_MODEL_MAT.name)
                        mvp *= instanceModelMat
                        modelMat *= instanceModelMat
                    }

                    // transform vertex attributes into world space and forward them to fragment stage
                    val localPos = constFloat4(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                    val localNormal = constFloat4(vertexAttribFloat3(Attribute.NORMALS.name), 0f)
                    val worldPos = float3Var((modelMat * localPos).xyz)
                    val worldNormal = float3Var((modelMat * localNormal).xyz)
                    positionWorldSpace.input set worldPos
                    normalWorldSpace.input set worldNormal
                    outPosition set mvp * localPos

                    // if normal mapping is enabled, the input vertex data is expected to have a tangent attribute
                    if (cfg.normalMapCfg.isNormalMapped) {
                        tangentWorldSpace = interStageFloat4().apply {
                            input set modelMat * constFloat4(vertexAttribFloat4(Attribute.TANGENTS.name).xyz, 0f)
                        }
                    }

                    // texCoordBlock is used by various other blocks to access texture coordinate vertex
                    // attributes (usually either none, or Attribute.TEXTURE_COORDS but there can be more)
                    texCoordBlock = texCoordAttributeBlock()

                    // project coordinates into shadow map / light space
                    shadowMapVertexStage = vertexShadowBlock(cfg.shadowCfg) {
                        inPositionWorldSpace = worldPos
                        inNormalWorldSpace = worldNormal
                    }
                }
            }

            fragmentStage {
                val camData = cameraData()
                val lightData = sceneLightData(cfg.maxNumberOfLights)

                main {
                    val normal = float3Var(normalize(normalWorldSpace.output))
                    val fragmentColor = fragmentColorBlock(cfg.colorCfg).outColor

                    if (cfg.pipelineCfg.cullMethod.isBackVisible && cfg.isFlipBacksideNormals) {
                        `if` (!inIsFrontFacing) {
                            normal *= (-1f).const3
                        }
                    }

                    // create an array with light strength values per light source (1.0 = full strength)
                    val shadowFactors = floatArray(lightData.maxLightCount, 1f.const)
                    // adjust light strength values by shadow maps
                    fragmentShadowBlock(shadowMapVertexStage, shadowFactors)

                    // determine main color (albedo)
                    val colorBlock = fragmentColorBlock(cfg.colorCfg)

                    // main material block
                    val material = blinnPhongMaterialBlock {
                        inCamPos = camData.position
                        inNormal = normal
                        inFragmentPos = positionWorldSpace.output
                        inFragmentColor = colorBlock.outColor.rgb

                        inAmbientColor = uAmbientColor.rgb
                        inSpecularColor = uSpecularColor.rgb
                        inShininess = uShininess
                        inSpecularGain = uSpecularGain

                        setLightData(lightData, shadowFactors)
                    }

                    // do normal map computations (if enabled) and adjust material block input normal accordingly
                    if (cfg.normalMapCfg.isNormalMapped) {
                        normalMapBlock(cfg.normalMapCfg) {
                            inTangentWorldSpace = normalize(tangentWorldSpace!!.output)
                            inNormalWorldSpace = normal
                            inStrength = uNormalMapStrength
                            inTexCoords = texCoordBlock.getAttributeCoords(cfg.normalMapCfg.coordAttribute)

                            material.inNormal = outBumpNormal
                        }
                    }

                    // set fragment stage output color
                    val outColor = float3Var(material.outColor)
                    if (cfg.isOutputToSrgbColorSpace) {
                        outColor set pow(outColor, Vec3f(Color.GAMMA_LINEAR_TO_sRGB).const)
                    }
                    if (cfg.pipelineCfg.blendMode == BlendMode.BLEND_PREMULTIPLIED_ALPHA) {
                        outColor set outColor * fragmentColor.a
                    }
                    colorOutput(outColor, fragmentColor.a)
                }
            }

            cfg.modelCustomizer?.invoke(this)

//            fragmentStage.main.updateModel()
//            fragmentStage.hierarchy.printHierarchy()
        }
    }
}