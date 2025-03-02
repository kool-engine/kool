package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ksl.BasicVertexConfig
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.VertexTransformBlock
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.vertexTransformBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh

fun DepthShader(cfgBlock: DepthShader.Config.() -> Unit): DepthShader {
    val cfg = DepthShader.Config().apply(cfgBlock)
    return DepthShader(cfg)
}

open class DepthShader(val cfg: Config) : KslShader(depthShaderProg(cfg), cfg.pipelineCfg) {

    var alphaMask by texture2d("tAlphaMask", cfg.alphaMask)

    companion object {
        private fun depthShaderProg(cfg: Config) = KslProgram("Depth shader").apply {
            var alphaMaskUv: KslInterStageVector<KslFloat2, KslFloat1>? = null
            var viewNormal: KslInterStageVector<KslFloat3, KslFloat1>? = null
            var linearDepth: KslInterStageScalar<KslFloat1>? = null

            vertexStage {
                main {
                    val camData = cameraData()
                    val vertexBlock = vertexTransformBlock(cfg.vertexCfg) {
                        inLocalPos(vertexAttribFloat3(Attribute.POSITIONS.name))
                        if (cfg.outputNormals) {
                            inLocalNormal(vertexAttribFloat3(Attribute.NORMALS.name))
                        }
                    }
                    val worldPos = float3Port("worldPos", vertexBlock.outWorldPos)
                    val viewPos = float4Var(camData.viewMat * float4Value(worldPos, 1f.const))
                    outPosition set camData.projMat * viewPos

                    if (cfg.outputLinearDepth) {
                        linearDepth = interStageFloat1("linearDepth").apply {
                            input set viewPos.z
                        }
                    }
                    if (cfg.alphaMode is AlphaMode.Mask) {
                        alphaMaskUv = interStageFloat2("alphaMaskUv").apply {
                            input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
                        }
                    }
                    if (cfg.outputNormals) {
                        viewNormal = interStageFloat3("worldNormal").apply {
                            input set (camData.viewMat * float4Value(vertexBlock.outWorldNormal, 0f.const)).xyz
                        }
                    }
                }
            }
            fragmentStage {
                main {
                    (cfg.alphaMode as? AlphaMode.Mask)?.let { mask ->
                        val color = sampleTexture(texture2d("tAlphaMask"), alphaMaskUv!!.output)
                        `if`(color.a lt mask.cutOff.const) {
                            discard()
                        }
                    }

                    if (cfg.outputNormals) {
                        var w: KslExprFloat1 = (-1f).const
                        if (cfg.outputLinearDepth) {
                            w = linearDepth!!.output
                        }
                        val normal = float3Var(normalize(viewNormal!!.output))
                        `if` (!inIsFrontFacing) {
                            normal set normal * (-1f).const
                        }
                        colorOutput(float4Value(normal, w))
                    } else if (cfg.outputLinearDepth) {
                        val d = linearDepth!!.output
                        colorOutput(float4Value(d, 1f.const, 1f.const, 1f.const))
                    } else {
                        KoolSystem.requireContext().backend.features.depthOnlyShaderColorOutput?.let {
                            colorOutput(it.const)
                        }
                    }
                }
            }
            cfg.modelCustomizer?.invoke(this)
        }
    }

    data class Config(
        val pipelineCfg: PipelineConfig = PipelineConfig(),
        val vertexCfg: BasicVertexConfig = BasicVertexConfig.Builder().build(),
        val alphaMode: AlphaMode = AlphaMode.Opaque,
        val alphaMask: Texture2d? = null,
        val outputLinearDepth: Boolean = false,
        val outputNormals: Boolean = false,
        val modelCustomizer: (KslProgram.() -> Unit)? = null
    ) {
        companion object {
            fun forMesh(
                mesh: Mesh,
                cullMethod: CullMethod = CullMethod.CULL_BACK_FACES,
                alphaMode: AlphaMode? = null,
                alphaMask: Texture2d? = null
            ) = Builder().apply {
                pipeline {
                    this.cullMethod = cullMethod
                }
                vertices {
                    isInstanced = mesh.instances != null
                    mesh.skin?.let {
                        enableArmature(it.nodes.size)
                    }
                    morphAttributes += mesh.geometry.getMorphAttributes()

                    (mesh.shader as? KslShader)?.let { ksl ->
                        ksl.program.vertexStage?.findBlock<VertexTransformBlock>()?.cfg?.modelMatrixComposition
                    }?.let { modelMatrixComposition = it }
                }
                if (alphaMode is AlphaMode.Mask) {
                    this.alphaMode = alphaMode
                    this.alphaMask = alphaMask
                }
            }.build()
        }

        class Builder {
            val pipelineCfg: PipelineConfig.Builder = PipelineConfig.Builder()
            val vertexCfg: BasicVertexConfig.Builder = BasicVertexConfig.Builder()
            var alphaMode: AlphaMode = AlphaMode.Opaque
            var alphaMask: Texture2d? = null

            var outputLinearDepth: Boolean = false
            var outputNormals: Boolean = false

            var modelCustomizer: (KslProgram.() -> Unit)? = null

            init {
                pipelineCfg.blendMode = BlendMode.DISABLED
            }

            fun useAlphaMask(alphaMask: Texture2d, alphaCutOff: Float) {
                this.alphaMask = alphaMask
                alphaMode = AlphaMode.Mask(alphaCutOff)
            }

            inline fun pipeline(block: PipelineConfig.Builder.() -> Unit) {
                pipelineCfg.block()
            }

            inline fun vertices(block: BasicVertexConfig.Builder.() -> Unit) {
                vertexCfg.block()
            }

            fun build(): Config = Config(
                pipelineCfg = pipelineCfg.build(),
                vertexCfg = vertexCfg.build(),
                alphaMode = alphaMode,
                alphaMask = alphaMask,
                outputLinearDepth = outputLinearDepth,
                outputNormals = outputNormals,
                modelCustomizer = modelCustomizer
            )
        }
    }
}