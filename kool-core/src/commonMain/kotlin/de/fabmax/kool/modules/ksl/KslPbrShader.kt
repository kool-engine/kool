package de.fabmax.kool.modules.ksl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureCube
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

open class KslPbrShader(cfg: Config, model: KslProgram = Model(cfg)) : KslLitShader(cfg, model) {

    constructor(block: Config.() -> Unit) : this(Config().apply(block))

    // basic material properties
    var roughness: Float by uniform1f(cfg.roughnessCfg.primaryUniform?.uniformName, cfg.roughnessCfg.primaryUniform?.defaultValue)
    var roughnessMap: Texture2d? by texture2d(cfg.roughnessCfg.primaryTexture?.textureName, cfg.roughnessCfg.primaryTexture?.defaultTexture)
    var metallic: Float by uniform1f(cfg.metallicCfg.primaryUniform?.uniformName, cfg.metallicCfg.primaryUniform?.defaultValue)
    var metallicMap: Texture2d? by texture2d(cfg.metallicCfg.primaryTexture?.textureName, cfg.metallicCfg.primaryTexture?.defaultTexture)

    val reflectionMaps: Array<TextureCube?> by textureCubeArray("tReflectionMaps", 2)
    var reflectionMapWeights: Vec2f by uniform2f("uReflectionWeights")
    var reflectionStrength: Vec4f by uniform4f("uReflectionStrength", cfg.reflectionStrength)

    var brdfLut: Texture2d? by texture2d("tBrdfLut")

    var reflectionMap: TextureCube?
        get() = reflectionMaps[0]
        set(value) {
            reflectionMaps[0] = value
            reflectionMaps[1] = value
            reflectionMapWeights = Vec2f.X_AXIS
        }

    init {
        reflectionMap = cfg.reflectionMap
    }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        super.onPipelineSetup(builder, mesh, ctx)
        if (brdfLut == null) {
            brdfLut = ctx.defaultPbrBrdfLut
        }
    }

    class Config : LitShaderConfig() {
        val metallicCfg = PropertyBlockConfig("metallic").apply { constProperty(0f) }
        val roughnessCfg = PropertyBlockConfig("roughness").apply { constProperty(0.5f) }

        var reflectionMap: TextureCube? = null

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
            irradiance: KslExprFloat3,
            lightData: SceneLightData,
            shadowFactors: KslExprFloat1Array,
            aoFactor: KslExprFloat1,
            normal: KslExprFloat3,
            fragmentWorldPos: KslExprFloat3,
            baseColor: KslExprFloat4
        ): KslExprFloat4 {

            val uRoughness = fragmentPropertyBlock(cfg.roughnessCfg).outProperty
            val uMetallic = fragmentPropertyBlock(cfg.metallicCfg).outProperty

            val ambientOri = uniformMat3("uAmbientTextureOri")
            val reflectionMaps = textureArrayCube("tReflectionMaps", 2).value
            val brdfLut = texture2d("tBrdfLut")
            val reflectionStrength = uniformFloat4("uReflectionStrength").rgb

            val material = pbrMaterialBlock(reflectionMaps, brdfLut) {
                inCamPos(camData.position)
                inNormal(normal)
                inFragmentPos(fragmentWorldPos)
                inBaseColor(baseColor.rgb)

                inRoughness(uRoughness)
                inMetallic(uMetallic)

                inIrradiance(irradiance)
                inAoFactor(aoFactor)
                inAmbientOrientation(ambientOri)

                inReflectionMapWeights(uniformFloat2("uReflectionWeights"))
                inReflectionStrength(reflectionStrength)

                setLightData(lightData, shadowFactors, cfg.lightStrength.const)
            }
            return float4Value(material.outColor, baseColor.a)
        }
    }
}