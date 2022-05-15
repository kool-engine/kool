package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.armatureBlock
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.modelMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode

fun depthShader(cfgBlock: KslDepthShader.Config.() -> Unit): KslDepthShader {
    val cfg = KslDepthShader.Config().apply(cfgBlock)
    return KslDepthShader(cfg)
}

open class KslDepthShader(cfg: Config, model: KslProgram = Model(cfg)) : KslShader(model, cfg.pipelineCfg) {

    class Config {
        val pipelineCfg = PipelineConfig().apply { blendMode = BlendMode.DISABLED }
        val vertexCfg = BasicVertexConfig()
        var outputMode = OutputMode.DEFAULT

        var modelCustomizer: (KslProgram.() -> Unit)? = null

        fun pipeline(block: PipelineConfig.() -> Unit) {
            pipelineCfg.apply(block)
        }

        fun vertices(block: BasicVertexConfig.() -> Unit) {
            vertexCfg.block()
        }
    }

    class Model(cfg: Config) : KslProgram("Depth Shader") {
        init {
            var normalLinearDepth: KslInterStageVector<KslTypeFloat4, KslTypeFloat1>? = null

            vertexStage {
                main {
                    val uModelMat = modelMatrix()
                    val camData = cameraData()
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

                    val localPos = float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                    val worldPos = float3Port("worldPos", float3Var((modelMat * localPos).xyz))
                    outPosition set (viewProj * float4Value(worldPos.output, 1f))

                    if (cfg.outputMode == OutputMode.NORMAL_LINEAR) {
                        val localNormal = float4Value(vertexAttribFloat3(Attribute.NORMALS.name), 0f)
                        val viewNormal = float3Var(normalize((camData.viewMat * (modelMat * localNormal)).xyz))
                        val viewPos = float3Var((camData.viewMat * float4Value(worldPos, 1f)).xyz)
                        normalLinearDepth = interStageFloat4("normalLinearDepth").apply {
                            input set float4Value(viewNormal, viewPos.z)
                        }
                    }
                }
            }
            fragmentStage {
                main {
                    if (cfg.outputMode == OutputMode.NORMAL_LINEAR) {
                        val nrmLinDepth = normalLinearDepth!!.output

                        val normal = float3Var(nrmLinDepth.xyz)
                        if (cfg.pipelineCfg.cullMethod.isBackVisible && cfg.vertexCfg.isFlipBacksideNormals) {
                            `if`(!inIsFrontFacing) {
                                normal *= (-1f).const3
                            }
                        }
                        colorOutput(normal, nrmLinDepth.w)

                    } else {
                        colorOutput(float4Value(1f, 1f, 1f, 1f))
                    }
                }
            }
            cfg.modelCustomizer?.invoke(this)
        }
    }

    enum class OutputMode {
        DEFAULT,
        NORMAL_LINEAR
    }
}