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

    var ssaoMap: Texture2d? by texture2d("tSsaoMap", cfg.defaultSsaoMap)

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
        val pipelineCfg = PipelineConfig()
        val shadowCfg = ShadowConfig()

        var ambientColor: AmbientColor = AmbientColor.Uniform(Color(0.2f, 0.2f, 0.2f).toLinear())
        var colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
        var maxNumberOfLights = 4
        var lightStrength = 1f
        var isSsao = false
        var defaultSsaoMap: Texture2d? = null
        var alphaMode: AlphaMode = AlphaMode.Blend()

        var modelCustomizer: (KslProgram.() -> Unit)? = null

        fun enableSsao(ssaoMap: Texture2d? = null) {
            isSsao = true
            defaultSsaoMap = ssaoMap
        }

        fun color(block: ColorBlockConfig.() -> Unit) {
            colorCfg.block()
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

        fun normalMapping(block: NormalMapConfig.() -> Unit) {
            normalMapCfg.block()
        }

        fun pipeline(block: PipelineConfig.() -> Unit) {
            pipelineCfg.block()
        }

        fun shadow(block: ShadowConfig.() -> Unit) {
            shadowCfg.block()
        }

        fun vertices(block: BasicVertexConfig.() -> Unit) {
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
                    val uModelMat = modelMatrix()
                    val viewProj = mat4Var(camData.viewProjMat)
                    val modelMat = mat4Var(uModelMat.matrix)
                    if (cfg.vertexCfg.isInstanced) {
                        val instanceModelMat = instanceAttribMat4(Attribute.INSTANCE_MODEL_MAT.name)
                        modelMat *= instanceModelMat
                    }
                    if (cfg.vertexCfg.isArmature) {
                        val armatureBlock = armatureBlock(cfg.vertexCfg.maxNumberOfBones)
                        armatureBlock.inBoneWeights(vertexAttribFloat4(Attribute.WEIGHTS.name))
                        armatureBlock.inBoneIndices(vertexAttribInt4(Attribute.JOINTS.name))
                        modelMat *= armatureBlock.outBoneTransform
                    }

                    // transform vertex attributes into world space and forward them to fragment stage
                    val localPos = float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                    val localNormal = float4Value(vertexAttribFloat3(Attribute.NORMALS.name), 0f)

                    // world position and normal are made available via ports for custom models to modify them
                    val worldPos = float3Port("worldPos", float3Var((modelMat * localPos).xyz))
                    val worldNormal = float3Port("worldNormal", float3Var(normalize((modelMat * localNormal).xyz)))

                    positionWorldSpace.input set worldPos
                    normalWorldSpace.input set worldNormal
                    projPosition.input set (viewProj * float4Value(worldPos, 1f))
                    outPosition set projPosition.input

                    // if normal mapping is enabled, the input vertex data is expected to have a tangent attribute
                    if (cfg.normalMapCfg.isNormalMapped) {
                        tangentWorldSpace = interStageFloat4().apply {
                            input set modelMat * float4Value(vertexAttribFloat4(Attribute.TANGENTS.name).xyz, 0f)
                        }
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

                    // discard fragment output if alpha mode is mask and fragment alpha value is below cutoff value
                    (cfg.alphaMode as? AlphaMode.Mask)?.let { mask ->
                        `if`(baseColorPort.a lt mask.cutOff.const) {
                            discard()
                        }
                    }

                    val vertexNormal = float3Var(normalize(normalWorldSpace.output))
                    if (cfg.pipelineCfg.cullMethod.isBackVisible && cfg.vertexCfg.isFlipBacksideNormals) {
                        `if`(!inIsFrontFacing) {
                            vertexNormal *= (-1f).const3
                        }
                    }

                    // do normal map computations (if enabled) and adjust material block input normal accordingly
                    val bumpedNormal = if (cfg.normalMapCfg.isNormalMapped) {
                        normalMapBlock(cfg.normalMapCfg) {
                            inTangentWorldSpace(normalize(tangentWorldSpace!!.output))
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

                    val aoFactor = float1Var(1f.const)
                    if (cfg.isSsao) {
                        val aoMap = texture2d("tSsaoMap")
                        val aoUv = float2Var(projPosition.output.xy / projPosition.output.w * 0.5f.const + 0.5f.const)
                        aoFactor set sampleTexture(aoMap, aoUv).x
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
                        baseColor = baseColorPort
                    )
                    val materialColorPort = float4Port("materialColor", materialColor)

                    // set fragment stage output color
                    val outRgb = float3Var(materialColorPort.rgb)
                    outRgb set convertColorSpace(outRgb, cfg.colorSpaceConversion)
                    if (cfg.pipelineCfg.blendMode == BlendMode.BLEND_PREMULTIPLIED_ALPHA) {
                        outRgb set outRgb * materialColorPort.a
                    }

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
        ): KslExprFloat4
    }
}