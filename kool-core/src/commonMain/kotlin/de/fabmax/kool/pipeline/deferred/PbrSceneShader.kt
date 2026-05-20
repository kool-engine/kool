package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.LightingConfig
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.Color

/**
 * 2nd pass shader for deferred pbr shading: Uses textures with view space position, normals, albedo, roughness,
 * metallic and texture-based AO and computes the final color output.
 */
open class PbrSceneShader(cfg: DeferredPbrConfig, model: Model = Model(cfg)) :
    KslShader(
        model,
        PipelineConfig(
            blendMode = BlendMode.DISABLED,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = DepthCompareOp.ALWAYS
        )
    )
{

    var depth by bindTexture2d("depth")
    var positionFlags by bindTexture2d("positionFlags")
    var normalRoughness by bindTexture2d("normalRoughness")
    var colorMetallic by bindTexture2d("colorMetallic")
    var emissiveAo by bindTexture2d("emissiveAo")

    var ambientFactor: Color by bindUniformColor("uAmbientColor")
    var ambientMapOrientation: Mat3f by bindUniformMat3("uAmbientTextureOri")
    // if ambient color is image based
    var ambientMap: TextureCube? by bindTextureCube("tAmbientTexture")
    // if ambient color is dual image based
    val ambientMaps = List(2) { bindTextureCube("tAmbientTexture_$it") }
    var ambientMapWeights by bindUniformFloat2("tAmbientWeights", Vec2f.X_AXIS)
    var ambientShadowFactor by bindUniformFloat1("uAmbientShadowFactor", cfg.ambientShadowFactor)

    var scrSpcAmbientOcclusionMap: Texture2d? by bindTexture2d("tSsaoMap")
    var scrSpcReflectionMap: Texture2d? by bindTexture2d("tSsrMap")

    val reflectionMaps = List(2) { bindTextureCube("tReflectionMap_$it") }
    var reflectionMapWeights: Vec2f by bindUniformFloat2("uReflectionWeights")
    var reflectionStrength: Vec4f by bindUniformFloat4("uReflectionStrength", Vec4f(cfg.reflectionStrength, 0f))
    var brdfLut: Texture2d? by bindTexture2d("tBrdfLut")

    var reflectionMap: TextureCube?
        get() = reflectionMaps[0].get()
        set(value) {
            reflectionMaps[0].set(value)
            reflectionMaps[1].set(value)
            reflectionMapWeights = Vec2f.X_AXIS
        }

    init {
        reflectionMap = cfg.reflectionMap
        when (val ambient = cfg.ambientLight) {
            is KslLitShader.AmbientLight.Uniform -> ambientFactor = ambient.ambientFactor
            is KslLitShader.AmbientLight.ImageBased -> {
                ambientMap = ambient.ambientMap
                ambientFactor = ambient.ambientFactor
            }
            is KslLitShader.AmbientLight.DualImageBased -> {
                ambientFactor = ambient.ambientFactor
            }
        }
    }

    override fun createPipeline(mesh: Mesh<*>, instances: MeshInstanceList<*>?, ctx: KoolContext): DrawPipeline {
        return super.createPipeline(mesh, instances, ctx).also {
            if (brdfLut == null) {
                brdfLut = ctx.defaultPbrBrdfLut
            }
        }
    }

    fun setMaterialInput(materialPass: MaterialPass) {
        createdPipeline?.swapPipelineData(materialPass)
        depth = materialPass.depthTexture
        positionFlags = materialPass.positionFlags
        normalRoughness = materialPass.normalRoughness
        colorMetallic = materialPass.albedoMetal
        emissiveAo = materialPass.emissiveAo
    }

    class Model(cfg: DeferredPbrConfig) : KslProgram("Deferred PBR compositing shader") {
        init {
            val texCoord = interStageFloat2("uv")
            fullscreenQuadVertexStage(texCoord)

            fragmentStage {
                main {
                    val uv = texCoord.output

                    val posFlags by texture2d("positionFlags").sample(uv)
                    val normalRoughness by texture2d("normalRoughness").sample(uv)
                    val colorMetallic by texture2d("colorMetallic").sample(uv)
                    val emissiveAo by texture2d("emissiveAo").sample(uv)

                    val viewPos = posFlags.xyz
                    //val flags = posFlags.a
                    val viewNormal = normalRoughness.xyz
                    val roughness = normalRoughness.a
                    val color = colorMetallic.rgb
                    val metallic = colorMetallic.a
                    val emissive = emissiveAo.rgb
                    val ao = emissiveAo.a

                    `if`(viewPos.z gt 0f.const) {
                        discard()
                    }

                    val camData = deferredCameraData()
                    val lightData = sceneLightData(cfg.lightingConfig.maxNumberOfLights)
                    val shadowData = shadowData(cfg.lightingConfig.build())

                    // transform input positions from view space back to world space
                    val worldPos = float3Var((camData.invViewMat * float4Value(viewPos, 1f.const)).xyz)
                    val worldNrm = float3Var((camData.invViewMat * float4Value(viewNormal, 0f.const)).xyz)

                    // compute ambient lighting properties
                    val ambientOri = uniformMat3("uAmbientTextureOri")
                    val irradiance = when (cfg.ambientLight) {
                        is KslLitShader.AmbientLight.Uniform -> float3Var(uniformFloat4("uAmbientColor").rgb)
                        is KslLitShader.AmbientLight.ImageBased -> {
                            val ambientTex = textureCube("tAmbientTexture")
                            float3Var((ambientTex.sample(ambientOri * worldNrm) * uniformFloat4("uAmbientColor")).rgb)
                        }
                        is KslLitShader.AmbientLight.DualImageBased -> {
                            val ambientTexs = List(2) { textureCube("tAmbientTexture_$it") }
                            val ambientWeights = uniformFloat2("tAmbientWeights")
                            val ambientColor = float4Var(ambientTexs[0].sample(ambientOri * worldNrm) * ambientWeights.x)
                            `if`(ambientWeights.y gt 0f.const) {
                                ambientColor += float4Var(ambientTexs[1].sample(ambientOri * worldNrm) * ambientWeights.y)
                            }
                            float3Var((ambientColor * uniformFloat4("uAmbientColor")).rgb)
                        }
                    }

                    // create an array with light strength values per light source (1.0 = full strength)
                    val shadowFactors = float1Array(lightData.maxLightCount, 1f.const)
                    val avgShadow = float1Var(0f.const)
                    if (shadowData.numSubMaps > 0) {
                        val lightSpacePositions = List(shadowData.numSubMaps) { float4Var(Vec4f.ZERO.const) }
                        val lightSpaceNormalZs = List(shadowData.numSubMaps) { float1Var(0f.const) }

                        // transform positions to light space
                        shadowData.shadowMapInfos.forEach { mapInfo ->
                            mapInfo.subMaps.forEachIndexed { i, subMap ->
                                val subMapIdx = mapInfo.fromIndexIncl + i
                                val viewProj = shadowData.shadowMapViewProjMats[subMapIdx]
                                val normalLightSpace = float3Var(normalize((viewProj * float4Value(worldNrm, 0f.const)).xyz))
                                lightSpaceNormalZs[subMapIdx] set normalLightSpace.z
                                lightSpacePositions[subMapIdx] set viewProj * float4Value(worldPos, 1f.const)
                                lightSpacePositions[subMapIdx].xyz += normalLightSpace * kotlin.math.abs(subMap.shaderDepthOffset).const
                            }
                        }

                        // adjust light strength values by shadow maps
                        fragmentShadowBlock(lightSpacePositions, lightSpaceNormalZs, shadowData, shadowFactors)
                        fori(0.const, lightData.lightCount) { i ->
                            avgShadow += shadowFactors[i]
                        }
                        avgShadow /= max(1f.const, lightData.lightCount.toFloat1())
                    }
                    val ambientShadowFac = uniformFloat1("uAmbientShadowFactor")
                    val shadowStr = float1Var((1f.const - avgShadow) * ambientShadowFac)
                    irradiance set irradiance * (1f.const - shadowStr)

                    // screen-space ao (if enabled)
                    if (cfg.isScrSpcAmbientOcclusion) {
                        val aoMap = texture2d("tSsaoMap")
                        ao *= aoMap.sample(uv).x
                    }

                    val reflectionColor = float3Var(Vec3f.ZERO.const)
                    val reflectionWeight = float1Var(0f.const)
                    if (cfg.isScrSpcReflections) {
                        val ssrMap = texture2d("tSsrMap")
                        val ssr = float4Var(ssrMap.sample(uv))
                        reflectionColor set convertColorSpace(ssr.rgb, ColorSpaceConversion.SrgbToLinear())
                        reflectionWeight set ssr.a
                    }

                    // reflection input textures
                    val brdfLut = texture2d("tBrdfLut")
                    val reflectionStrength = uniformFloat4("uReflectionStrength").rgb
                    val reflectionMaps = if (cfg.isTextureReflection) {
                        List(2) { textureCube("tReflectionMap_$it") }
                    } else {
                        emptyList()
                    }

                    val material = pbrMaterialBlock(cfg.lightingConfig.maxNumberOfLights, reflectionMaps, brdfLut, cfg.lightingConfig.normalLightRange) {
                        inCamPos(camData.position)
                        inNormal(worldNrm)
                        inFragmentPos(worldPos)
                        inBaseColor(float4Value(color, 1f.const))

                        inRoughness(roughness)
                        inMetallic(metallic)

                        inIrradiance(irradiance)
                        inAoFactor(ao)
                        inAmbientOrientation(ambientOri)

                        inReflectionMapWeights(uniformFloat2("uReflectionWeights"))
                        inReflectionStrength(reflectionStrength)

                        setLightData(lightData, shadowFactors, cfg.lightingConfig.lightStrength.const)
                    }
                    val finalReflectionColor by mix(material.outSpecular, clamp(reflectionColor, Vec3f.ZERO.const, Vec3f(5f).const), reflectionWeight)
                    val finalColor by material.outAmbient + material.outLight + finalReflectionColor * material.outSpecularFactor + emissive
                    colorOutput(finalColor)
                    outDepth set texture2d("depth", isUnfilterable = true).sample(uv).r
                }
            }
        }
    }

    class DeferredPbrConfig {
        var isImageBasedLighting = false
        var isScrSpcAmbientOcclusion = false
        var isScrSpcReflections = false

        var environmentMap: EnvironmentMap? = null
        var ambientShadowFactor = 0f

        val lightingConfig = LightingConfig.Builder()

        var ambientLight: KslLitShader.AmbientLight = KslLitShader.AmbientLight.Uniform(Color(0.2f, 0.2f, 0.2f).toLinear())
        var isTextureReflection = false
        var reflectionStrength = Vec3f.ONES
        var reflectionMap: TextureCube? = null
            set(value) {
                field = value
                isTextureReflection = value != null
            }

        fun useImageBasedLighting(environmentMap: EnvironmentMap?) {
            this.environmentMap = environmentMap
            isImageBasedLighting = environmentMap != null

            if (environmentMap != null) {
                ambientLight = KslLitShader.AmbientLight.ImageBased(environmentMap.irradianceMap, Color.WHITE)
                isTextureReflection = true
                reflectionMap = environmentMap.reflectionMap
            } else {
                ambientLight = KslLitShader.AmbientLight.Uniform(Color(0.2f, 0.2f, 0.2f).toLinear())
                isTextureReflection = false
            }
        }
    }
}