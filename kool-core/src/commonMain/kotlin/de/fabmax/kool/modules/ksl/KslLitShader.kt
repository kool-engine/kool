package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.util.Color

abstract class KslLitShader(val cfg: LitShaderConfig, model: KslProgram) : KslShader(model, cfg.pipelineCfg) {

    var color: Color by colorUniform(cfg.colorCfg)
    var colorMap: Texture2d? by colorTexture(cfg.colorCfg)

    var normalMap: Texture2d? by normalTexture(cfg.normalMapCfg)
    var normalMapStrength: Float by propertyUniform(cfg.normalMapCfg.strengthCfg)

    var emission: Color by colorUniform(cfg.emissionCfg)
    var emissionMap: Texture2d? by colorTexture(cfg.emissionCfg)

    var ao: Float by propertyUniform(cfg.aoCfg)
    var aoMap: Texture2d? by propertyTexture(cfg.aoCfg)

    var ssaoMap: Texture2d? by texture2d("tSsaoMap", cfg.lightingCfg.defaultSsaoMap)

    var parallaxMap: Texture2d? by texture2d(cfg.parallaxCfg.parallaxMapName, cfg.parallaxCfg.defaultParallaxMap)
    var parallaxStrength: Float by uniform1f("uParallaxStrength", cfg.parallaxCfg.strength)
    var parallaxMapSteps: Int by uniform1i("uParallaxMaxSteps", cfg.parallaxCfg.maxSteps)

    var vertexDisplacementMap: Texture2d? by propertyTexture(cfg.vertexCfg.displacementCfg)
    var vertexDisplacementStrength: Float by propertyUniform(cfg.vertexCfg.displacementCfg)

    var ambientFactor: Color by uniformColor("uAmbientColor")
    var ambientMapOrientation: Mat3f by uniformMat3f("uAmbientTextureOri")
    // if ambient color is image based
    var ambientMap: TextureCube? by textureCube("tAmbientTexture")
    // if ambient color is dual image based
    val ambientMaps = List(2) { textureCube("tAmbientTexture_$it") }
    var ambientMapWeights by uniform2f("tAmbientWeights", Vec2f.X_AXIS)

    val ambientCfg: AmbientLight get() = cfg.lightingCfg.ambientLight
    val colorCfg: ColorBlockConfig get() = cfg.colorCfg
    val emissionCfg: ColorBlockConfig get() = cfg.emissionCfg
    val aoCfg: PropertyBlockConfig get() = cfg.aoCfg
    val displacementCfg: PropertyBlockConfig get() = cfg.vertexCfg.displacementCfg
    val parallaxCfg: ParallaxMapConfig get() = cfg.parallaxCfg
    val isNormalMapped: Boolean get() = cfg.normalMapCfg.isNormalMapped
    val isParallaxMapped: Boolean get() = cfg.parallaxCfg.isParallaxMapped
    val isSsao: Boolean get() = cfg.lightingCfg.isSsao

    /**
     * Read-only list of shadow maps used by this shader. To modify the shadow maps, the shader has to be re-created.
     */
    val shadowMaps = cfg.lightingCfg.shadowMaps.map { it.shadowMap }

    init {
        if (cfg.normalMapCfg.isArrayNormalMap) {
            textureArrays[cfg.normalMapCfg.textureName] = texture2dArray(cfg.normalMapCfg.textureName, cfg.normalMapCfg.defaultArrayNormalMap)
        }
        registerArrayTextures(cfg.colorCfg)
        registerArrayTextures(cfg.emissionCfg)
        registerArrayTextures(cfg.aoCfg)
        registerArrayTextures(cfg.vertexCfg.displacementCfg)

        when (val ac = ambientCfg) {
            is AmbientLight.Uniform -> ambientFactor = ac.ambientFactor
            is AmbientLight.ImageBased -> {
                ambientMap = ac.ambientMap
                ambientFactor = ac.ambientFactor
            }
            is AmbientLight.DualImageBased -> {
                ambientFactor = ac.ambientFactor
            }
        }
    }

    sealed class AmbientLight {
        class Uniform(val ambientFactor: Color) : AmbientLight()
        class ImageBased(val ambientMap: TextureCube?, val ambientFactor: Color) : AmbientLight()
        class DualImageBased(val ambientFactor: Color) : AmbientLight()
    }

    open class LitShaderConfig(builder: Builder) {
        val pipelineCfg: PipelineConfig = builder.pipelineCfg.build()
        val vertexCfg: BasicVertexConfig = builder.vertexCfg.build()
        val colorCfg: ColorBlockConfig = builder.colorCfg.build()
        val normalMapCfg: NormalMapConfig = builder.normalMapCfg.build()
        val aoCfg: PropertyBlockConfig = builder.aoCfg.build()
        val parallaxCfg: ParallaxMapConfig = builder.parallaxCfg.build()
        val emissionCfg: ColorBlockConfig = builder.emissionCfg.build()
        val lightingCfg: LightingConfig = builder.lightingCfg.build()

        val colorSpaceConversion = builder.colorSpaceConversion
        val alphaMode: AlphaMode = builder.alphaMode

        val modelCustomizer: (KslProgram.() -> Unit)? = builder.modelCustomizer

        open fun requiresTextureCoords(): Boolean {
            if (vertexCfg.displacementCfg.primaryTexture != null) return true
            if (colorCfg.primaryTexture != null) return true
            if (normalMapCfg.isNormalMapped) return true
            if (aoCfg.primaryTexture != null) return true
            if (parallaxCfg.isParallaxMapped) return true
            if (emissionCfg.primaryTexture != null) return true
            return false
        }

        open class Builder {
            val pipelineCfg = PipelineConfig.Builder()
            val vertexCfg = BasicVertexConfig.Builder()
            val colorCfg = ColorBlockConfig.Builder("baseColor").constColor(Color.GRAY)
            val normalMapCfg = NormalMapConfig.Builder()
            val aoCfg = PropertyBlockConfig.Builder("ao").apply { constProperty(1f) }
            val parallaxCfg = ParallaxMapConfig.Builder()
            val emissionCfg = ColorBlockConfig.Builder("emissionColor").constColor(Color(0f, 0f, 0f, 0f))
            val lightingCfg = LightingConfig.Builder()

            var colorSpaceConversion: ColorSpaceConversion = ColorSpaceConversion.LinearToSrgbHdr()
            var alphaMode: AlphaMode = AlphaMode.Blend

            var modelCustomizer: (KslProgram.() -> Unit)? = null

            fun enableSsao(ssaoMap: Texture2d? = null): Builder {
                lightingCfg.enableSsao(ssaoMap)
                return this
            }

            inline fun ao(block: PropertyBlockConfig.Builder.() -> Unit) {
                aoCfg.block()
            }

            inline fun color(block: ColorBlockConfig.Builder.() -> Unit) {
                colorCfg.colorSources.clear()
                colorCfg.block()
            }

            inline fun emission(block: ColorBlockConfig.Builder.() -> Unit) {
                emissionCfg.colorSources.clear()
                emissionCfg.block()
            }

            inline fun lighting(block: LightingConfig.Builder.() -> Unit) {
                lightingCfg.block()
            }

            inline fun normalMapping(block: NormalMapConfig.Builder.() -> Unit) {
                normalMapCfg.block()
            }

            inline fun parallaxMapping(block: ParallaxMapConfig.Builder.() -> Unit) {
                parallaxCfg.block()
            }

            inline fun pipeline(block: PipelineConfig.Builder.() -> Unit) {
                pipelineCfg.block()
            }

            inline fun vertices(block: BasicVertexConfig.Builder.() -> Unit) {
                vertexCfg.block()
            }

            open fun build() = LitShaderConfig(this)
        }
    }

    abstract class LitShaderModel<T: LitShaderConfig>(name: String) : KslProgram(name) {

        open fun createModel(cfg: T) {
            val camData = cameraData()
            val positionWorldSpace = interStageFloat3("positionWorldSpace")
            val normalWorldSpace = interStageFloat3("normalWorldSpace")
            val projPosition = interStageFloat4("projPosition")
            var tangentWorldSpace: KslInterStageVector<KslFloat4, KslFloat1>? = null

            val texCoordBlock: TexCoordAttributeBlock
            val shadowMapVertexStage: ShadowBlockVertexStage?

            vertexStage {
                main {
                    val vertexBlock = vertexTransformBlock(cfg.vertexCfg) {
                        inLocalPos(vertexAttribFloat3(Attribute.POSITIONS.name))
                        inLocalNormal(vertexAttribFloat3(Attribute.NORMALS.name))

                        if (cfg.normalMapCfg.isNormalMapped) {
                            // if normal mapping is enabled, the input vertex data is expected to have a tangent attribute
                            inLocalTangent(vertexAttribFloat4(Attribute.TANGENTS.name))
                        }
                    }

                    // world position and normal are made available via ports for custom models to modify them
                    val worldPos = float3Port("worldPos", vertexBlock.outWorldPos)
                    val worldNormal = float3Port("worldNormal", vertexBlock.outWorldNormal)

                    positionWorldSpace.input set worldPos
                    normalWorldSpace.input set worldNormal
                    projPosition.input set (camData.viewProjMat * float4Value(worldPos, 1f))
                    outPosition set projPosition.input

                    if (cfg.normalMapCfg.isNormalMapped) {
                        tangentWorldSpace = interStageFloat4().apply { input set vertexBlock.outWorldTangent }
                    }

                    // texCoordBlock is used by various other blocks to access texture coordinate vertex
                    // attributes (usually either none, or Attribute.TEXTURE_COORDS but there can be more)
                    texCoordBlock = texCoordAttributeBlock()

                    // project coordinates into shadow map / light space
                    val perFragmentShadow = cfg.parallaxCfg.isParallaxMapped && cfg.parallaxCfg.isPreciseShadows
                    shadowMapVertexStage = if (perFragmentShadow || cfg.lightingCfg.shadowMaps.isEmpty()) null else {
                        vertexShadowBlock(cfg.lightingCfg) {
                            inPositionWorldSpace(worldPos)
                            inNormalWorldSpace(worldNormal)
                        }
                    }
                }
            }

            fragmentStage {
                val lightData = sceneLightData(cfg.lightingCfg.maxNumberOfLights)

                main {
                    val vertexWorldPos = float3Var(positionWorldSpace.output)
                    val vertexNormal = float3Var(normalize(normalWorldSpace.output))

                    var ddx: KslExprFloat2? = null
                    var ddy: KslExprFloat2? = null

                    // compute displaced texture coordinates if parallax mapping is enabled
                    if (cfg.parallaxCfg.isParallaxMapped) {
                        val parallaxMapping = parallaxMapBlock(cfg.parallaxCfg) {
                            inPositionClipSpace(projPosition.output)
                            inPositionWorldSpace(vertexWorldPos)
                            inNormalWorldSpace(vertexNormal)
                            inTexCoords(texCoordBlock.getTextureCoords())
                            inStrength(uniformFloat1("uParallaxStrength"))
                            inMaxSteps(uniformInt1("uParallaxMaxSteps"))
                        }
                        ddx = parallaxMapping.outDdx
                        ddy = parallaxMapping.outDdy

                        vertexWorldPos set parallaxMapping.outDisplacedWorldPos
                        texCoordBlock.texCoords[Attribute.TEXTURE_COORDS.name] = parallaxMapping.outDisplacedTexCoords

                        if (cfg.parallaxCfg.isAdjustFragmentDepth) {
                            val displacedPos = float4Var(camData.viewProjMat * float4Value(vertexWorldPos, 1f.const))
                            outDepth set displacedPos.z / displacedPos.w
                        }
                    }

                    // flip backside normal after parallax mapping, so that displacement always happens in front direction
                    if (cfg.pipelineCfg.cullMethod.isBackVisible && cfg.vertexCfg.isFlipBacksideNormals) {
                        `if`(!inIsFrontFacing) {
                            vertexNormal *= (-1f).const3
                        }
                    }

                    // determine main color (albedo)
                    val colorBlock = fragmentColorBlock(cfg.colorCfg, ddx, ddy)
                    val baseColorPort = float4Port("baseColor", colorBlock.outColor)

                    val baseColor = float4Var(baseColorPort)
                    when (val alphaMode = cfg.alphaMode) {
                        is AlphaMode.Blend -> { }
                        is AlphaMode.Opaque -> baseColor.a set 1f.const
                        is AlphaMode.Mask -> {
                            `if`(baseColorPort.a lt alphaMode.cutOff.const) {
                                discard()
                            }
                            baseColor.a set 1f.const
                        }
                    }

                    val emissionBlock = fragmentColorBlock(cfg.emissionCfg, ddx, ddy)
                    val emissionColorPort = float4Port("emissionColor", emissionBlock.outColor)

                    // do normal map computations (if enabled)
                    val bumpedNormal = if (cfg.normalMapCfg.isNormalMapped) {
                        val normalMapStrength = fragmentPropertyBlock(cfg.normalMapCfg.strengthCfg, ddx, ddy).outProperty
                        normalMapBlock(cfg.normalMapCfg, ddx, ddy) {
                            inTangentWorldSpace(tangentWorldSpace!!.output)
                            inNormalWorldSpace(vertexNormal)
                            inStrength(normalMapStrength)
                            inTexCoords(texCoordBlock.getTextureCoords())
                        }.outBumpNormal
                    } else {
                        vertexNormal
                    }
                    // make final normal value available to model customizer
                    val normal = float3Port("normal", bumpedNormal)
                    val worldPos = float3Port("worldPos", vertexWorldPos)

                    // create an array with light strength values per light source (1.0 = full strength)
                    val shadowFactors = float1Array(lightData.maxLightCount, 1f.const)
                    // adjust light strength values by shadow maps
                    if (shadowMapVertexStage != null) {
                        fragmentShadowBlock(shadowMapVertexStage, shadowFactors)
                    } else if (cfg.lightingCfg.shadowMaps.isNotEmpty()) {
                        fragmentOnlyShadowBlock(cfg.lightingCfg, worldPos, normal, shadowFactors)
                    }

                    val aoFactor = float1Var(fragmentPropertyBlock(cfg.aoCfg, ddx, ddy).outProperty)
                    if (cfg.lightingCfg.isSsao) {
                        val aoMap = texture2d("tSsaoMap")
                        val aoUv = float2Var(projPosition.output.xy / projPosition.output.w * 0.5f.const + 0.5f.const)
                        aoFactor *= sampleTexture(aoMap, aoUv, 0f.const).x
                    }

                    val irradiance = when (cfg.lightingCfg.ambientLight) {
                        is AmbientLight.Uniform -> uniformFloat4("uAmbientColor").rgb
                        is AmbientLight.ImageBased -> {
                            val ambientOri = uniformMat3("uAmbientTextureOri")
                            val ambientTex = textureCube("tAmbientTexture")
                            (sampleTexture(ambientTex, ambientOri * normal, 0f.const) * uniformFloat4("uAmbientColor")).rgb
                        }
                        is AmbientLight.DualImageBased -> {
                            val ambientOri = uniformMat3("uAmbientTextureOri")
                            val ambientTexs = List(2) { textureCube("tAmbientTexture_$it") }
                            val ambientWeights = uniformFloat2("tAmbientWeights")
                            val ambientColor = float4Var(sampleTexture(ambientTexs[0], ambientOri * normal, 0f.const) * ambientWeights.x)
                            `if`(ambientWeights.y gt 0f.const) {
                                ambientColor += float4Var(sampleTexture(ambientTexs[1], ambientOri * normal, 0f.const) * ambientWeights.y)
                            }
                            (ambientColor * uniformFloat4("uAmbientColor")).rgb
                        }
                    }

                    // main material block
                    val materialColor = createMaterial(
                        cfg = cfg,
                        camData = camData,
                        irradiance = irradiance,
                        lightData = lightData,
                        shadowFactors = shadowFactors,
                        aoFactor = aoFactor,
                        normal = normal,
                        fragmentWorldPos = worldPos,
                        baseColor = baseColor,
                        emissionColor = emissionColorPort,
                        ddx = ddx,
                        ddy = ddy
                    )

                    val materialColorPort = float4Port("materialColor", materialColor)

                    // set fragment stage output color
                    val outRgb = float3Var(materialColorPort.rgb)
                    if (cfg.pipelineCfg.blendMode == BlendMode.BLEND_PREMULTIPLIED_ALPHA) {
                        outRgb set outRgb * materialColorPort.a
                    }
                    outRgb set convertColorSpace(outRgb, cfg.colorSpaceConversion)

                    when (cfg.alphaMode) {
                        is AlphaMode.Blend -> colorOutput(outRgb, materialColorPort.a)
                        is AlphaMode.Mask -> colorOutput(outRgb, 1f.const)
                        is AlphaMode.Opaque -> colorOutput(outRgb, 1f.const)
                    }
                }
            }

            cfg.modelCustomizer?.invoke(this)
        }

        protected abstract fun KslScopeBuilder.createMaterial(
            cfg: T,
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
            ddy: KslExprFloat2?,
        ): KslExprFloat4
    }
}