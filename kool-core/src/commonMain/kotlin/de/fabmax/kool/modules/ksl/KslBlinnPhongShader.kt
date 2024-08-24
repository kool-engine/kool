package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.copy

open class KslBlinnPhongShader(cfg: Config, model: KslProgram = Model(cfg)) : KslLitShader(cfg, model) {

    constructor(block: Config.Builder.() -> Unit) : this(Config.Builder().apply(block).build())

    var shininess: Float by propertyUniform(cfg.shininessCfg)
    var shininessMap: Texture2d? by propertyTexture(cfg.shininessCfg)
    var specularStrength: Float by propertyUniform(cfg.shininessCfg)
    var specularStrengthMap: Texture2d? by propertyTexture(cfg.specularStrengthCfg)

    var specularColor: Color by uniformColor("uSpecularColor", cfg.specularColor)

    val shininessCfg = PropertyBlockConfig(cfg.shininessCfg.propertyName, cfg.shininessCfg.propertySources.copy().toMutableList())
    val specularStrengthCfg = PropertyBlockConfig(cfg.specularStrengthCfg.propertyName, cfg.specularStrengthCfg.propertySources.copy().toMutableList())

    init {
        registerArrayTextures(cfg.shininessCfg)
        registerArrayTextures(cfg.specularStrengthCfg)
    }

    class Config(builder: Builder) : LitShaderConfig(builder) {
        val specularColor: Color = builder.specularColor
        val shininessCfg: PropertyBlockConfig = builder.shininessCfg.build()
        val specularStrengthCfg: PropertyBlockConfig = builder.specularStrengthCfg.build()

        class Builder : LitShaderConfig.Builder() {
            var specularColor: Color = Color.WHITE
            val shininessCfg = PropertyBlockConfig.Builder("shininess").constProperty(16f)
            val specularStrengthCfg = PropertyBlockConfig.Builder("specularStrength").constProperty(1f)

            fun shininess(block: PropertyBlockConfig.Builder.() -> Unit) {
                shininessCfg.propertySources.clear()
                shininessCfg.block()
            }

            fun shininess(value: Float): Builder {
                shininess { constProperty(value) }
                return this
            }

            fun specularStrength(block: PropertyBlockConfig.Builder.() -> Unit) {
                specularStrengthCfg.propertySources.clear()
                specularStrengthCfg.block()
            }

            fun specularStrength(value: Float): Builder {
                specularStrength { constProperty(value) }
                return this
            }

            override fun build(): Config {
                return Config(this)
            }
        }
    }

    class Model(cfg: Config) : LitShaderModel<Config>("Blinn-Phong Shader") {
        init {
            createModel(cfg)
        }

        override fun KslScopeBuilder.createMaterial(
            cfg: Config,
            camData: CameraData,
            irradiance: KslExprFloat3,
            lightData: SceneLightData,
            shadowFactors: KslExprFloat1Array,
            aoFactor: KslExprFloat1,
            normal: KslExprFloat3,
            fragmentWorldPos: KslExprFloat3,
            baseColor: KslExprFloat4,
            emissionColor: KslExprFloat4,
            ddx: KslExprFloat2?,
            ddy: KslExprFloat2?
        ): KslExprFloat4 {

            val uSpecularColor = uniformFloat4("uSpecularColor")
            val uShininess = fragmentPropertyBlock(cfg.shininessCfg, ddx, ddy).outProperty
            val uSpecularStrength = fragmentPropertyBlock(cfg.specularStrengthCfg, ddx, ddy).outProperty

            val material = blinnPhongMaterialBlock(cfg.lightingCfg.maxNumberOfLights) {
                inCamPos(camData.position)
                inNormal(normal)
                inFragmentPos(fragmentWorldPos)
                inBaseColor(baseColor)

                inAmbientColor(irradiance * aoFactor)
                inSpecularColor(uSpecularColor.rgb)
                inShininess(uShininess)
                inSpecularStrength(uSpecularStrength)

                setLightData(lightData, shadowFactors, cfg.lightingCfg.lightStrength.const)
            }
            return float4Value(material.outColor + emissionColor.rgb, baseColor.a)
        }
    }
}

fun KslBlinnPhongShaderConfig(block: KslBlinnPhongShader.Config.Builder.() -> Unit): KslBlinnPhongShader.Config {
    val builder = KslBlinnPhongShader.Config.Builder()
    builder.block()
    return builder.build()
}