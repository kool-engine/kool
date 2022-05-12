package de.fabmax.kool.modules.ksl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureCube
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

class KslPbrShader(cfg: Config, model: KslProgram = Model(cfg)) : KslLitShader(cfg, model) {

    constructor(block: Config.() -> Unit) : this(Config().apply(block))

    // basic material properties
    var roughness: Float by uniform1f(cfg.roughnessCfg.primaryUniform?.uniformName, cfg.roughnessCfg.primaryUniform?.defaultValue)
    var roughnessMap: Texture2d? by texture2d(cfg.roughnessCfg.primaryTexture?.textureName, cfg.roughnessCfg.primaryTexture?.defaultTexture)
    var metallic: Float by uniform1f(cfg.metallicCfg.primaryUniform?.uniformName, cfg.metallicCfg.primaryUniform?.defaultValue)
    var metallicMap: Texture2d? by texture2d(cfg.metallicCfg.primaryTexture?.textureName, cfg.metallicCfg.primaryTexture?.defaultTexture)

    // image based lighting maps
    var irradianceMap: TextureCube? by textureCube("tIrradianceMap", cfg.irradianceMap)
    var reflectionMap: TextureCube? by textureCube("tReflectionMap", cfg.reflectionMap)
    var brdfLut: Texture2d? by texture2d("tBrdfLut")
    var irradianceStrength: Vec4f by uniform4f("uIrradianceStrength", cfg.irradianceStrength)
    var reflectionStrength: Vec4f by uniform4f("uReflectionStrength", cfg.reflectionStrength)

    var ambientTextureOrientation: Mat3f by uniformMat3f("uAmbientTextureOri", Mat3f().setIdentity())

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        super.onPipelineSetup(builder, mesh, ctx)
        if (brdfLut == null) {
            brdfLut = ctx.defaultPbrBrdfLut
        }
    }

    class Config : LitShaderConfig() {
        val metallicCfg = PropertyBlockConfig("metallic").apply { constProperty(0f) }
        val roughnessCfg = PropertyBlockConfig("roughness").apply { constProperty(0.5f) }

        var irradianceMap: TextureCube? = null
        var reflectionMap: TextureCube? = null

        var irradianceStrength = Color.WHITE
        var reflectionStrength = Color.WHITE

        fun metallic(block: PropertyBlockConfig.() -> Unit) {
            metallicCfg.propertySources.clear()
            metallicCfg.block()
        }

        fun metallic(value: Float) = metallic { constProperty(value) }

        fun roughness(block: PropertyBlockConfig.() -> Unit) {
            roughnessCfg.propertySources.clear()
            roughnessCfg.block()
        }

        fun roughness(value: Float) = roughness { constProperty(value) }
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

            val ambientOri = uniformMat3("uAmbientTextureOri")
            val irradianceMap = textureCube("tIrradianceMap")
            val reflectionMap = textureCube("tReflectionMap")
            val brdfLut = texture2d("tBrdfLut")
            val irradianceStrength = uniformFloat4("uIrradianceStrength").rgb
            val reflectionStrength = uniformFloat4("uReflectionStrength").rgb

            val irradiance = float3Var(sampleTexture(irradianceMap, ambientOri * normal).rgb) * irradianceStrength

            val material = pbrMaterialBlock(reflectionMap, brdfLut) {
                inCamPos(camData.position)
                inNormal(normal)
                inFragmentPos(fragmentWorldPos)
                inBaseColor(baseColor.rgb)

                inRoughness(uRoughness)
                inMetallic(uMetallic)

                inIrradiance(irradiance)
                inAmbientOrientation(ambientOri)

                inReflectionStrength(reflectionStrength)

                setLightData(lightData, shadowFactors, cfg.lightStrength.const)
            }
            return float4Value(material.outColor, baseColor.a)
        }
    }
}