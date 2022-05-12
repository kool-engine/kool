package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.CameraData
import de.fabmax.kool.modules.ksl.blocks.SceneLightData
import de.fabmax.kool.modules.ksl.blocks.blinnPhongMaterialBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.TextureCube
import de.fabmax.kool.util.Color

open class KslBlinnPhongShader(cfg: Config, model: KslProgram = Model(cfg)) : KslLitShader(cfg, model) {

    constructor(block: Config.() -> Unit) : this(Config().apply(block))

    var specularColor: Vec4f by uniform4f("uSpecularColor", cfg.specularColor)
    var shininess: Float by uniform1f("uShininess", cfg.shininess)
    var specularStrength: Float by uniform1f("uSpecularStrength", cfg.specularStrength)
    var normalMapStrength: Float by uniform1f("uNormalMapStrength", cfg.normalMapCfg.defaultStrength)

    var ambientColor: Vec4f by uniform4f("uAmbientColor")
    var ambientTexture: TextureCube? by textureCube("tAmbientTexture")
    var ambientTextureOrientation: Mat3f by uniformMat3f("uAmbientTextureOri", Mat3f().setIdentity())

    init {
        when (val ambient = cfg.ambientColor) {
            is Config.AmbientColor.Uniform -> ambientColor = ambient.color
            is Config.AmbientColor.ImageBased -> {
                ambientTexture = ambient.ambientTexture
                ambientColor = ambient.colorFactor
            }
        }
    }

    class Config : LitShaderConfig() {
        var specularColor: Color = Color.WHITE
        var ambientColor: AmbientColor = AmbientColor.Uniform(Color(0.2f, 0.2f, 0.2f).toLinear())
        var shininess = 16f
        var specularStrength = 1f

        fun uniformAmbientColor(color: Color = Color(0.2f, 0.2f, 0.2f).toLinear()) {
            ambientColor = AmbientColor.Uniform(color)
        }

        fun imageBasedAmbientColor(ambientTexture: TextureCube? = null, colorFactor: Color = Color.WHITE) {
            ambientColor = AmbientColor.ImageBased(ambientTexture, colorFactor)
        }

        sealed class AmbientColor {
            class Uniform(val color: Color) : AmbientColor()
            class ImageBased(val ambientTexture: TextureCube?, val colorFactor: Color) : AmbientColor()
        }
    }

    class Model(cfg: Config) : LitShaderModel<Config>("Blinn-Phong Shader") {
        init {
            createModel(cfg)
        }

        override fun KslScopeBuilder.createMaterial(
            cfg: Config,
            camData: CameraData,
            lightData: SceneLightData,
            shadowFactors: KslScalarArrayExpression<KslTypeFloat1>,
            normal: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
            fragmentWorldPos: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
            baseColor: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>
        ): KslVectorExpression<KslTypeFloat4, KslTypeFloat1> {

            val uSpecularColor = uniformFloat4("uSpecularColor")
            val uShininess = uniformFloat1("uShininess")
            val uSpecularStrength = uniformFloat1("uSpecularStrength")
            val uAmbientColor = uniformFloat4("uAmbientColor")

            val ambientColor = when (cfg.ambientColor) {
                is Config.AmbientColor.Uniform -> uAmbientColor
                is Config.AmbientColor.ImageBased -> {
                    val ambientTex = textureCube("tAmbientTexture")
                    val ambientOri = uniformMat3("uAmbientTextureOri")
                    sampleTexture(ambientTex, ambientOri * normal) * uAmbientColor
                }
            }

            val material = blinnPhongMaterialBlock {
                inCamPos(camData.position)
                inNormal(normal)
                inFragmentPos(fragmentWorldPos)
                inBaseColor(baseColor.rgb)

                inAmbientColor(ambientColor.rgb)
                inSpecularColor(uSpecularColor.rgb)
                inShininess(uShininess)
                inSpecularStrength(uSpecularStrength)

                setLightData(lightData, shadowFactors)
            }
            return float4Value(material.outColor, baseColor.a)
        }
    }
}