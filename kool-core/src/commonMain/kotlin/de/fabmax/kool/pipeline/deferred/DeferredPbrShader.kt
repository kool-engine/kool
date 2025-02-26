package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logE

inline fun deferredKslPbrShader(block: DeferredKslPbrShader.Config.Builder.() -> Unit): DeferredKslPbrShader {
    val cfg = DeferredKslPbrShader.Config.Builder()
    cfg.block()
    return DeferredKslPbrShader(cfg.build())
}

/**
 * 1st pass shader for deferred pbr shading: Renders view space position, normals, albedo, roughness, metallic and
 * texture-based AO into three separate texture outputs.
 */
open class DeferredKslPbrShader(cfg: Config) : KslShader(deferredPbrModel(cfg), cfg.pipelineCfg) {

    var color: Color by colorUniform(cfg.colorCfg)
    var colorMap: Texture2d? by colorTexture(cfg.colorCfg)

    var normalMap: Texture2d? by texture2d(cfg.normalMapCfg.textureName, cfg.normalMapCfg.defaultNormalMap)
    var normalMapStrength: Float by propertyUniform(cfg.normalMapCfg.strengthCfg)

    var displacement: Float by propertyUniform(cfg.vertexCfg.displacementCfg)
    var displacementMap: Texture2d? by propertyTexture(cfg.vertexCfg.displacementCfg)

    var emission: Color by colorUniform(cfg.emissionCfg)
    var emissionMap: Texture2d? by colorTexture(cfg.emissionCfg)

    var materialAo: Float by propertyUniform(cfg.aoCfg)
    var materialAoMap: Texture2d? by propertyTexture(cfg.aoCfg)

    var metallic: Float by propertyUniform(cfg.metallicCfg)
    var metallicMap: Texture2d? by propertyTexture(cfg.metallicCfg)

    var roughness: Float by propertyUniform(cfg.roughnessCfg)
    var roughnessMap: Texture2d? by propertyTexture(cfg.roughnessCfg)

    init {
        if (cfg.pipelineCfg.blendMode != BlendMode.DISABLED) {
            logE { "DeferredPbrShader is created with blendMode ${cfg.pipelineCfg.blendMode}, " +
                    "which results in undefined behavior (blendMode must be DISABLED)" }
        }
    }

    companion object {
        private fun deferredPbrModel(cfg: Config) = KslProgram("DeferredPbrShader").apply {
            val camData = cameraData()
            val positionViewSpace = interStageFloat3("positionWorldSpace")
            val normalViewSpace = interStageFloat3("normalWorldSpace")
            var tangentViewSpace: KslInterStageVector<KslFloat4, KslFloat1>? = null

            val texCoordBlock: TexCoordAttributeBlock

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

                    val viewPos = float4Var(camData.viewMat * float4Value(worldPos, 1f))
                    outPosition set camData.projMat * viewPos

                    positionViewSpace.input set viewPos.xyz
                    normalViewSpace.input set (camData.viewMat * float4Value(worldNormal, 0f)).xyz

                    if (cfg.normalMapCfg.isNormalMapped) {
                        tangentViewSpace = interStageFloat4().apply {
                            input.xyz set (camData.viewMat * float4Value(vertexBlock.outWorldTangent.xyz, 0f)).xyz
                            input.w set vertexBlock.outWorldTangent.w
                        }
                    }

                    // texCoordBlock is used by various other blocks to access texture coordinate vertex
                    // attributes (usually either none, or Attribute.TEXTURE_COORDS but there can be more)
                    texCoordBlock = texCoordAttributeBlock()
                }
            }
            fragmentStage {
                main {
                    // determine main color (albedo)
                    val colorBlock = fragmentColorBlock(cfg.colorCfg)
                    val baseColorPort = float4Port("baseColor", colorBlock.outColor)
                    (cfg.alphaMode as? AlphaMode.Mask)?.let {
                        `if`(baseColorPort.a lt it.cutOff.const) {
                            discard()
                        }
                    }

                    val emissionBlock = fragmentColorBlock(cfg.emissionCfg)
                    val emissionColorPort = float4Port("emissionColor", emissionBlock.outColor)

                    val vertexNormal = float3Var(normalize(normalViewSpace.output))
                    if (cfg.pipelineCfg.cullMethod.isBackVisible && cfg.vertexCfg.isFlipBacksideNormals) {
                        `if`(!inIsFrontFacing) {
                            vertexNormal *= (-1f).const3
                        }
                    }

                    // do normal map computations (if enabled) and adjust material block input normal accordingly
                    val bumpedNormal = if (cfg.normalMapCfg.isNormalMapped) {
                        val normalMapStrength = fragmentPropertyBlock(cfg.normalMapCfg.strengthCfg).outProperty
                        normalMapBlock(cfg.normalMapCfg) {
                            inTangentWorldSpace(tangentViewSpace!!.output)
                            inNormalWorldSpace(vertexNormal)
                            inStrength(normalMapStrength)
                            inTexCoords(texCoordBlock.getTextureCoords())
                        }.outBumpNormal
                    } else {
                        vertexNormal
                    }

                    val normal = float3Port("normal", bumpedNormal)
                    val roughness = float1Port("roughness", fragmentPropertyBlock(cfg.roughnessCfg).outProperty)
                    val metallic = float1Port("metallic", fragmentPropertyBlock(cfg.metallicCfg).outProperty)
                    val aoFactor = float1Port("aoFactor", fragmentPropertyBlock(cfg.aoCfg).outProperty)

                    colorOutput(positionViewSpace.output, cfg.materialFlags.toFloat().const, location = 0)
                    colorOutput(normal, roughness, location = 1)
                    colorOutput(baseColorPort.rgb, metallic, location = 2)
                    colorOutput(emissionColorPort.rgb, aoFactor, location = 3)
                }
            }
            cfg.modelCustomizer?.invoke(this)
        }
    }

    class Config(builder: Builder) : KslPbrShader.Config(builder) {
        val materialFlags = builder.materialFlags

        class Builder : KslPbrShader.Config.Builder() {
            var materialFlags = 0

            init {
                pipelineCfg.blendMode = BlendMode.DISABLED
            }

            override fun build() = Config(this)
        }

        companion object {
            const val MATERIAL_FLAG_ALWAYS_LIT = 1
            const val MATERIAL_FLAG_IS_MOVING = 2
        }
    }
}