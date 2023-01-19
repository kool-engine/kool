package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureCube
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.util.Color

abstract class KslLitShader(cfg: LitShaderConfig, model: KslProgram) : KslShader(model, cfg.pipelineCfg) {

    var color: Vec4f by uniform4f(cfg.colorCfg.primaryUniform?.uniformName, cfg.colorCfg.primaryUniform?.defaultColor)
    var colorMap: Texture2d? by texture2d(cfg.colorCfg.primaryTexture?.textureName, cfg.colorCfg.primaryTexture?.defaultTexture)

    var normalMap: Texture2d? by texture2d(cfg.normalMapCfg.normalMapName, cfg.normalMapCfg.defaultNormalMap)
    var normalMapStrength: Float by uniform1f("uNormalMapStrength", cfg.normalMapCfg.defaultStrength)

    var emission: Vec4f by uniform4f(cfg.emissionCfg.primaryUniform?.uniformName, cfg.emissionCfg.primaryUniform?.defaultColor)
    var emissionMap: Texture2d? by texture2d(cfg.emissionCfg.primaryTexture?.textureName, cfg.emissionCfg.primaryTexture?.defaultTexture)

    var ssaoMap: Texture2d? by texture2d("tSsaoMap", cfg.aoCfg.defaultSsaoMap)
    var materialAo: Float by uniform1f(cfg.aoCfg.materialAo.primaryUniform?.uniformName, cfg.aoCfg.materialAo.primaryUniform?.defaultValue)
    var materialAoMap: Texture2d? by texture2d(cfg.aoCfg.materialAo.primaryTexture?.textureName, cfg.aoCfg.materialAo.primaryTexture?.defaultTexture)

    var displacement: Float by uniform1f(cfg.vertexCfg.displacementCfg.primaryUniform?.uniformName, cfg.vertexCfg.displacementCfg.primaryUniform?.defaultValue)
    var displacementMap: Texture2d? by texture2d(cfg.vertexCfg.displacementCfg.primaryTexture?.textureName, cfg.vertexCfg.displacementCfg.primaryTexture?.defaultTexture)

    var ambientFactor: Vec4f by uniform4f("uAmbientColor")
    var ambientTextureOrientation: Mat3f by uniformMat3f("uAmbientTextureOri", Mat3f().setIdentity())
    // if ambient color is image based
    var ambientTexture: TextureCube? by textureCube("tAmbientTexture")
    // if ambient color is dual image based
    val ambientTextures: Array<TextureCube?> by textureCubeArray("tAmbientTextures", 2)
    var ambientTextureWeights by uniform2f("tAmbientWeights", Vec2f.X_AXIS)

    init {
        when (val ambient = cfg.ambientColor) {
            is AmbientColor.Uniform -> ambientFactor = ambient.color
            is AmbientColor.ImageBased -> {
                ambientTexture = ambient.ambientTexture
                ambientFactor = ambient.colorFactor
            }
            is AmbientColor.DualImageBased -> {
                ambientFactor = ambient.colorFactor
            }
        }
    }

    sealed class AmbientColor {
        class Uniform(val color: Color) : AmbientColor()
        class ImageBased(val ambientTexture: TextureCube?, val colorFactor: Color) : AmbientColor()
        class DualImageBased(val colorFactor: Color) : AmbientColor()
    }

    open class LitShaderConfig {
        val vertexCfg = BasicVertexConfig()
        val colorCfg = ColorBlockConfig("baseColor")
        val normalMapCfg = NormalMapConfig()
        val aoCfg = AmbientOcclusionConfig()
        val pipelineCfg = PipelineConfig()
        val shadowCfg = ShadowConfig()
        val emissionCfg = ColorBlockConfig("emissionColor").apply { constColor(Color(0f, 0f, 0f, 0f)) }

        var ambientColor: AmbientColor = AmbientColor.Uniform(Color(0.2f, 0.2f, 0.2f).toLinear())
        var colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        var maxNumberOfLights = 4
        var lightStrength = 1f
        var alphaMode: AlphaMode = AlphaMode.Blend()

        var modelCustomizer: (KslProgram.() -> Unit)? = null

        fun enableSsao(ssaoMap: Texture2d? = null) = aoCfg.enableSsao(ssaoMap)

        inline fun ao(block: AmbientOcclusionConfig.() -> Unit) {
            aoCfg.block()
        }

        inline fun color(block: ColorBlockConfig.() -> Unit) {
            colorCfg.block()
        }

        inline fun emission(block: ColorBlockConfig.() -> Unit) {
            emissionCfg.block()
        }

        fun uniformAmbientColor(color: Color = Color(0.2f, 0.2f, 0.2f).toLinear()) {
            ambientColor = AmbientColor.Uniform(color)
        }

        fun imageBasedAmbientColor(ambientTexture: TextureCube? = null, colorFactor: Color = Color.WHITE) {
            ambientColor = AmbientColor.ImageBased(ambientTexture, colorFactor)
        }

        fun dualImageBasedAmbientColor(colorFactor: Color = Color.WHITE) {
            ambientColor = AmbientColor.DualImageBased(colorFactor)
        }

        inline fun normalMapping(block: NormalMapConfig.() -> Unit) {
            normalMapCfg.block()
        }

        inline fun pipeline(block: PipelineConfig.() -> Unit) {
            pipelineCfg.block()
        }

        inline fun shadow(block: ShadowConfig.() -> Unit) {
            shadowCfg.block()
        }

        inline fun vertices(block: BasicVertexConfig.() -> Unit) {
            vertexCfg.block()
        }
    }

    abstract class LitShaderModel<T: LitShaderConfig>(name: String) : KslProgram(name) {

        open fun createModel(cfg: T) {
            val camData = cameraData()
            val positionWorldSpace = interStageFloat3("positionWorldSpace")
            val normalWorldSpace = interStageFloat3("normalWorldSpace")
            val projPosition = interStageFloat4("screenUv")
            var tangentWorldSpace: KslInterStageVector<KslTypeFloat4, KslTypeFloat1>? = null

            val texCoordBlock: TexCoordAttributeBlock
            val shadowMapVertexStage: ShadowBlockVertexStage

            vertexStage {
                main {
                    val vertexBlock = vertexTransformBlock(cfg.vertexCfg) {
                        inModelMat(modelMatrix().matrix)
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
                    shadowMapVertexStage = vertexShadowBlock(cfg.shadowCfg) {
                        inPositionWorldSpace(worldPos)
                        inNormalWorldSpace(worldNormal)
                    }
                }
            }

            fragmentStage {
                val uNormalMapStrength = uniformFloat1("uNormalMapStrength")
                val lightData = sceneLightData(cfg.maxNumberOfLights)

                main {
                    // determine main color (albedo)
                    val colorBlock = fragmentColorBlock(cfg.colorCfg)
                    val baseColorPort = float4Port("baseColor", colorBlock.outColor)

                    val baseColor = float4Var(baseColorPort)
                    when (val alphaMode = cfg.alphaMode) {
                        is AlphaMode.Blend -> { }
                        is AlphaMode.Opaque -> baseColor.a set 1f.const
                        is AlphaMode.Mask -> {
                            `if`(baseColorPort.a lt alphaMode.cutOff.const) {
                                discard()
                            }
                        }
                    }

                    val emissionBlock = fragmentColorBlock(cfg.emissionCfg)
                    val emissionColorPort = float4Port("emissionColor", emissionBlock.outColor)

                    val vertexNormal = float3Var(normalize(normalWorldSpace.output))
                    if (cfg.pipelineCfg.cullMethod.isBackVisible && cfg.vertexCfg.isFlipBacksideNormals) {
                        `if`(!inIsFrontFacing) {
                            vertexNormal *= (-1f).const3
                        }
                    }

                    // do normal map computations (if enabled) and adjust material block input normal accordingly
                    val bumpedNormal = if (cfg.normalMapCfg.isNormalMapped) {
                        normalMapBlock(cfg.normalMapCfg) {
                            inTangentWorldSpace(tangentWorldSpace!!.output)
                            inNormalWorldSpace(vertexNormal)
                            inStrength(uNormalMapStrength)
                            inTexCoords(texCoordBlock.getAttributeCoords(cfg.normalMapCfg.coordAttribute))
                        }.outBumpNormal
                    } else {
                        vertexNormal
                    }

                    // make final normal value available to model customizer
                    val normal = float3Port("normal", bumpedNormal)

                    // create an array with light strength values per light source (1.0 = full strength)
                    val shadowFactors = float1Array(lightData.maxLightCount, 1f.const)
                    // adjust light strength values by shadow maps
                    fragmentShadowBlock(shadowMapVertexStage, shadowFactors)

                    val aoFactor = float1Var(fragmentPropertyBlock(cfg.aoCfg.materialAo).outProperty)
                    if (cfg.aoCfg.isSsao) {
                        val aoMap = texture2d("tSsaoMap")
                        val aoUv = float2Var(projPosition.output.xy / projPosition.output.w * 0.5f.const + 0.5f.const)
                        aoFactor *= sampleTexture(aoMap, aoUv).x
                    }

                    val irradiance = when (cfg.ambientColor) {
                        is AmbientColor.Uniform -> uniformFloat4("uAmbientColor").rgb
                        is AmbientColor.ImageBased -> {
                            val ambientOri = uniformMat3("uAmbientTextureOri")
                            val ambientTex = textureCube("tAmbientTexture")
                            (sampleTexture(ambientTex, ambientOri * normal) * uniformFloat4("uAmbientColor")).rgb
                        }
                        is AmbientColor.DualImageBased -> {
                            val ambientOri = uniformMat3("uAmbientTextureOri")
                            val ambientTexs = textureArrayCube("tAmbientTextures", 2)
                            val ambientWeights = uniformFloat2("tAmbientWeights")
                            val ambientColor = float4Var(sampleTexture(ambientTexs[0], ambientOri * normal) * ambientWeights.x)
                            `if`(ambientWeights.y gt 0f.const) {
                                ambientColor += float4Var(sampleTexture(ambientTexs[1], ambientOri * normal) * ambientWeights.y)
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
                        fragmentWorldPos = positionWorldSpace.output,
                        baseColor = baseColor,
                        emissionColor = emissionColorPort
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
        ): KslExprFloat4
    }
}