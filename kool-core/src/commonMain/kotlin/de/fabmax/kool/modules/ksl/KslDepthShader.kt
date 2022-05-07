package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.armatureBlock
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.float3Port
import de.fabmax.kool.modules.ksl.blocks.modelMatrix
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.modules.ksl.lang.xyz
import de.fabmax.kool.pipeline.Attribute

fun depthShader(cfgBlock: KslDepthShader.Config.() -> Unit): KslDepthShader {
    val cfg = KslDepthShader.Config().apply(cfgBlock)
    return KslDepthShader(cfg)
}

open class KslDepthShader(cfg: Config, model: KslProgram = Model(cfg)) : KslShader(model, cfg.pipelineCfg) {

    class Config {
        val pipelineCfg = PipelineConfig()

        var isInstanced = false
        val isArmature: Boolean
            get() = maxNumberOfBones > 0
        var maxNumberOfBones = 0

        var modelCustomizer: (KslProgram.() -> Unit)? = null

        fun pipeline(block: PipelineConfig.() -> Unit) {
            pipelineCfg.apply(block)
        }
    }

    class Model(cfg: Config) : KslProgram("Depth Shader") {
        init {
            vertexStage {
                main {
                    val uModelMat = modelMatrix()
                    val camData = cameraData()
                    val viewProj = mat4Var(camData.viewProjMat)
                    val modelMat = mat4Var(uModelMat.matrix)

                    if (cfg.isInstanced) {
                        val instanceModelMat = instanceAttribMat4(Attribute.INSTANCE_MODEL_MAT.name)
                        modelMat *= instanceModelMat
                    }
                    if (cfg.isArmature) {
                        val armatureBlock = armatureBlock(cfg.maxNumberOfBones)
                        armatureBlock.inBoneWeights(vertexAttribFloat4(Attribute.WEIGHTS.name))
                        armatureBlock.inBoneIndices(vertexAttribInt4(Attribute.JOINTS.name))
                        modelMat *= armatureBlock.outBoneTransform
                    }

                    val localPos = constFloat4(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                    val worldPos = float3Port("worldPos", float3Var((modelMat * localPos).xyz))
                    outPosition set (viewProj * constFloat4(worldPos.output, 1f))
                }
            }
            fragmentStage {
                main {
                    colorOutput(constFloat4(1f, 1f, 1f, 1f))
                }
            }
            cfg.modelCustomizer?.invoke(this)
        }
    }
}