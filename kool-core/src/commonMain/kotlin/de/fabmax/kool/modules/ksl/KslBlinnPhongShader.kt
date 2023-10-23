package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.copy

open class KslBlinnPhongShader(cfg: Config, model: KslProgram = Model(cfg)) : KslLitShader(cfg, model) {

    constructor(block: Config.() -> Unit) : this(Config().apply(block))

    var shininess: Float by propertyUniform(cfg.shininessCfg)
    var shininessMap: Texture2d? by propertyTexture(cfg.shininessCfg)
    var specularStrength: Float by propertyUniform(cfg.shininessCfg)
    var specularStrengthMap: Texture2d? by propertyTexture(cfg.specularStrengthCfg)

    var specularColor: Color by uniformColor("uSpecularColor", cfg.specularColor)

    val shininessCfg = PropertyBlockConfig(cfg.shininessCfg.propertyName, cfg.shininessCfg.propertySources.copy().toMutableList())
    val specularStrengthCfg = PropertyBlockConfig(cfg.specularStrengthCfg.propertyName, cfg.specularStrengthCfg.propertySources.copy().toMutableList())

    class Config : LitShaderConfig() {
        var specularColor: Color = Color.WHITE
        val shininessCfg = PropertyBlockConfig("shininess").apply { constProperty(16f) }
        val specularStrengthCfg = PropertyBlockConfig("specularStrength").apply { constProperty(1f) }

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
            emissionColor: KslExprFloat4
        ): KslExprFloat4 {

            val uSpecularColor = uniformFloat4("uSpecularColor")
            val uShininess = fragmentPropertyBlock(cfg.shininessCfg).outProperty
            val uSpecularStrength = fragmentPropertyBlock(cfg.specularStrengthCfg).outProperty

            val material = blinnPhongMaterialBlock(cfg.maxNumberOfLights) {
                inCamPos(camData.position)
                inNormal(normal)
                inFragmentPos(fragmentWorldPos)
                inBaseColor(baseColor)

                inAmbientColor(irradiance * aoFactor)
                inSpecularColor(uSpecularColor.rgb)
                inShininess(uShininess)
                inSpecularStrength(uSpecularStrength)

                setLightData(lightData, shadowFactors, cfg.lightStrength.const)
            }
            return float4Value(material.outColor + emissionColor.rgb, baseColor.a)
        }
    }
}