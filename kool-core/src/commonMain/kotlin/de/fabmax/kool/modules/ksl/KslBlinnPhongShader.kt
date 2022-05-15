package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureCube
import de.fabmax.kool.util.Color

open class KslBlinnPhongShader(cfg: Config, model: KslProgram = Model(cfg)) : KslLitShader(cfg, model) {

    constructor(block: Config.() -> Unit) : this(Config().apply(block))

    var shininess: Float by uniform1f(cfg.shininessCfg.primaryUniform?.uniformName, cfg.shininessCfg.primaryUniform?.defaultValue)
    var shininessMap: Texture2d? by texture2d(cfg.shininessCfg.primaryTexture?.textureName, cfg.shininessCfg.primaryTexture?.defaultTexture)
    var specularStrength: Float by uniform1f(cfg.shininessCfg.primaryUniform?.uniformName, cfg.shininessCfg.primaryUniform?.defaultValue)
    var specularStrengthMap: Texture2d? by texture2d(cfg.specularStrengthCfg.primaryTexture?.textureName, cfg.specularStrengthCfg.primaryTexture?.defaultTexture)

    var specularColor: Vec4f by uniform4f("uSpecularColor", cfg.specularColor)
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
        val shininessCfg = PropertyBlockConfig("shininess").apply { constProperty(16f) }
        val specularStrengthCfg = PropertyBlockConfig("specularStrength").apply { constProperty(1f) }

        fun uniformAmbientColor(color: Color = Color(0.2f, 0.2f, 0.2f).toLinear()) {
            ambientColor = AmbientColor.Uniform(color)
        }

        fun imageBasedAmbientColor(ambientTexture: TextureCube? = null, colorFactor: Color = Color.WHITE) {
            ambientColor = AmbientColor.ImageBased(ambientTexture, colorFactor)
        }

        fun shininess(block: PropertyBlockConfig.() -> Unit) {
            shininessCfg.propertySources.clear()
            shininessCfg.block()
        }

        fun shininess(value: Float) = shininess { constProperty(value) }

        fun specularStrength(block: PropertyBlockConfig.() -> Unit) {
            specularStrengthCfg.propertySources.clear()
            specularStrengthCfg.block()
        }

        fun specularStrength(value: Float) = specularStrength { constProperty(value) }

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
            shadowFactors: KslExprFloat1Array,
            aoFactor: KslExprFloat1,
            normal: KslExprFloat3,
            fragmentWorldPos: KslExprFloat3,
            baseColor: KslExprFloat4
        ): KslExprFloat4 {

            val uSpecularColor = uniformFloat4("uSpecularColor")
            val uShininess = fragmentPropertyBlock(cfg.shininessCfg).outProperty
            val uSpecularStrength = fragmentPropertyBlock(cfg.specularStrengthCfg).outProperty

            val ambientColor = when (cfg.ambientColor) {
                is Config.AmbientColor.Uniform -> uniformFloat4("uAmbientColor")
                is Config.AmbientColor.ImageBased -> {
                    val ambientTex = textureCube("tAmbientTexture")
                    val ambientOri = uniformMat3("uAmbientTextureOri")
                    sampleTexture(ambientTex, ambientOri * normal) * uniformFloat4("uAmbientColor")
                }
            }

            val material = blinnPhongMaterialBlock {
                inCamPos(camData.position)
                inNormal(normal)
                inFragmentPos(fragmentWorldPos)
                inBaseColor(baseColor.rgb)

                inAmbientColor(ambientColor.rgb * aoFactor)
                inSpecularColor(uSpecularColor.rgb)
                inShininess(uShininess)
                inSpecularStrength(uSpecularStrength)

                setLightData(lightData, shadowFactors, cfg.lightStrength.const)
            }
            return float4Value(material.outColor, baseColor.a)
        }
    }
}