package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.Color

fun blinnPhongShader(cfgBlock: KslBlinnPhongShader.Config.() -> Unit): KslBlinnPhongShader {
    val cfg = KslBlinnPhongShader.Config().apply(cfgBlock)
    return KslBlinnPhongShader(cfg)
}

class KslBlinnPhongShader(cfg: Config, model: KslProgram = Model(cfg)) : KslShader(model, cfg.pipelineCfg) {

    var uniformDiffuseColor: Vec4f by uniform4f(cfg.colorCfg.primaryUniformColor?.uniformName, cfg.colorCfg.primaryUniformColor?.defaultColor)
    var colorTexture: Texture2d? by texture2d(cfg.colorCfg.primaryTextureColor?.textureName, cfg.colorCfg.primaryTextureColor?.defaultTexture)

    var specularColor: Vec4f by uniform4f("uSpecularColor", cfg.specularColor)
    var ambientColor: Vec4f by uniform4f("uAmbientColor", cfg.ambientColor)
    var shininess: Float by uniform1f("uShininess", cfg.shininess)

    class Config {
        val colorCfg = ColorBlockConfig()
        val pipelineCfg = PipelineConfig()
        val shadowCfg = ShadowConfig()

        var isInstanced = false
        var isOutputToSrgbColorSpace = true
        var isFlipBacksideNormals = true

        var maxNumberOfLights = 4

        var specularColor: Color = Color.WHITE
        var ambientColor: Color = Color(0.2f, 0.2f, 0.2f).toLinear()
        var shininess = 16f

        var modelCustomizer: (KslProgram.() -> Unit)? = null

        fun color(block: ColorBlockConfig.() -> Unit) {
            colorCfg.apply(block)
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

            val positionWorldSpace = interStageFloat3()
            val normalWorldSpace = interStageFloat3()

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

                    val localPos = constFloat4(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                    val localNormal = constFloat4(vertexAttribFloat3(Attribute.NORMALS.name), 0f)
                    val worldPos = float3Var((modelMat * localPos).xyz)
                    val worldNormal = float3Var((modelMat * localNormal).xyz)

                    positionWorldSpace.input set worldPos
                    normalWorldSpace.input set worldNormal
                    outPosition set mvp * localPos

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

                    val shadowFactors = floatArray(lightData.maxLightCount, 1f.const)
                    fragmentShadowBlock(shadowMapVertexStage, shadowFactors)

                    val material = blinnPhongMaterialBlock {
                        inCamPos = camData.position
                        inNormal = normal
                        inFragmentPos = positionWorldSpace.output
                        inFragmentColor = fragmentColorBlock(cfg.colorCfg).outColor.rgb

                        inAmbientColor = uAmbientColor.rgb
                        inSpecularColor = uSpecularColor.rgb
                        inShininess = uShininess

                        setLightData(lightData, shadowFactors)
                    }

                    val outColor = float3Var(material.outColor)
                    if (cfg.isOutputToSrgbColorSpace) {
                        outColor set pow(outColor, Vec3f(Color.GAMMA_LINEAR_TO_sRGB).const)
                    }
                    colorOutput(outColor * fragmentColor.a, fragmentColor.a)
                }
            }

            cfg.modelCustomizer?.invoke(this)

//            fragmentStage.main.updateModel()
//            fragmentStage.hierarchy.printHierarchy()
        }
    }
}