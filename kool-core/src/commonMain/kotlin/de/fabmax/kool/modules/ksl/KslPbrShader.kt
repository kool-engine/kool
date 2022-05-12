package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.CameraData
import de.fabmax.kool.modules.ksl.blocks.SceneLightData
import de.fabmax.kool.modules.ksl.blocks.pbrMaterialBlock
import de.fabmax.kool.modules.ksl.lang.*

class KslPbrShader(cfg: Config, model: KslProgram = Model(cfg)) : KslLitShader(cfg, model) {

    constructor(block: Config.() -> Unit) : this(Config().apply(block))

    var roughness: Float by uniform1f("uRoughness", cfg.roughness)
    var metallic: Float by uniform1f("uMetallic", cfg.metallic)

    class Config : LitShaderConfig() {
        var metallic = 0f
        var roughness = 0.5f
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

            val uRoughness = uniformFloat1("uRoughness")
            val uMetallic = uniformFloat1("uMetallic")

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