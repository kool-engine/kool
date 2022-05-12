package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Texture2d

class KslPbrShader(cfg: Config, model: KslProgram = Model(cfg)) : KslLitShader(cfg, model) {

    constructor(block: Config.() -> Unit) : this(Config().apply(block))

    var roughness: Float by uniform1f(cfg.roughnessCfg.primaryUniform?.uniformName, cfg.roughnessCfg.primaryUniform?.defaultValue)
    var roughnessMap: Texture2d? by texture2d(cfg.roughnessCfg.primaryTexture?.textureName, cfg.roughnessCfg.primaryTexture?.defaultTexture)
    var metallic: Float by uniform1f(cfg.metallicCfg.primaryUniform?.uniformName, cfg.metallicCfg.primaryUniform?.defaultValue)
    var metallicMap: Texture2d? by texture2d(cfg.metallicCfg.primaryTexture?.textureName, cfg.metallicCfg.primaryTexture?.defaultTexture)

    class Config : LitShaderConfig() {
        val metallicCfg = PropertyBlockConfig("metallic").apply { constProperty(0f) }
        val roughnessCfg = PropertyBlockConfig("roughness").apply { constProperty(0.5f) }
    }

    class Model(cfg: Config) : LitShaderModel<Config>("PBR Shader") {
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

            val uRoughness = fragmentPropertyBlock(cfg.roughnessCfg).outProperty
            val uMetallic = fragmentPropertyBlock(cfg.metallicCfg).outProperty

            val material = pbrMaterialBlock {
                inCamPos(camData.position)
                inNormal(normal)
                inFragmentPos(fragmentWorldPos)
                inBaseColor(baseColor.rgb)

                inRoughness(uRoughness)
                inMetallic(uMetallic)

                setLightData(lightData, shadowFactors)
            }
            return float4Value(material.outColor, baseColor.a)
        }
    }
}