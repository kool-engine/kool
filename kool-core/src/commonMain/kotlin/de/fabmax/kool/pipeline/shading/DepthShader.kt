package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.modules.ksl.BasicVertexConfig
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.modelMatrix
import de.fabmax.kool.modules.ksl.blocks.vertexTransformBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import kotlin.math.max

class DepthShader(val cfg: Config) : KslShader(depthShaderProg(cfg), cfg.pipelineCfg) {

    var alphaMask by texture2d("tAlphaMask", cfg.alphaMask)

    constructor(block: Config.() -> Unit) : this(Config().apply(block))

    companion object {
        private fun depthShaderProg(cfg: Config) = KslProgram("Depth shader").apply {
            var alphaMaskUv: KslInterStageVector<KslTypeFloat2, KslTypeFloat1>? = null
            var viewNormal: KslInterStageVector<KslTypeFloat3, KslTypeFloat1>? = null

            vertexStage {
                main {
                    val camData = cameraData()
                    val viewProj = mat4Var(camData.viewProjMat)
                    val vertexBlock = vertexTransformBlock(cfg.vertexCfg) {
                        inModelMat(modelMatrix().matrix)
                        inLocalPos(vertexAttribFloat3(Attribute.POSITIONS.name))

                        if (cfg.outputNormals) {
                            inLocalNormal(vertexAttribFloat3(Attribute.NORMALS.name))
                        }
                    }
                    outPosition set viewProj * float4Value(vertexBlock.outWorldPos, 1f)

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
                        var w: KslExprFloat1 = 1f.const
                        if (cfg.outputLinearDepth) {
                            w = inFragPosition.z / inFragPosition.w
                        }
                        colorOutput(float4Value(viewNormal!!.output, -w))
                    } else if (cfg.outputLinearDepth) {
                        val d = inFragPosition.z / inFragPosition.w
                        colorOutput(float4Value(-d, 1f.const, 1f.const, 1f.const))
                    } else {
                        colorOutput(float4Value(1f, 1f, 1f, 1f))
                    }
                }
            }
            cfg.modelCustomizer?.invoke(this)
        }
    }

    data class Config(
        val pipelineCfg: PipelineConfig = PipelineConfig(),
        val vertexCfg: BasicVertexConfig = BasicVertexConfig(),
        var alphaMode: AlphaMode = AlphaMode.Opaque(),
        var alphaMask: Texture2d? = null,

        var outputLinearDepth: Boolean = false,
        var outputNormals: Boolean = false,

        var modelCustomizer: (KslProgram.() -> Unit)? = null
    ) {
        init {
            pipelineCfg.blendMode = BlendMode.DISABLED
        }

        fun useAlphaMask(alphaMask: Texture2d, alphaCutOff: Float) {
            this.alphaMask = alphaMask
            alphaMode = AlphaMode.Mask(alphaCutOff)
        }

        inline fun pipeline(block: PipelineConfig.() -> Unit) {
            pipelineCfg.block()
        }

        inline fun vertices(block: BasicVertexConfig.() -> Unit) {
            vertexCfg.block()
        }

        companion object {
            fun forMesh(
                mesh: Mesh,
                cullMethod: CullMethod = CullMethod.CULL_BACK_FACES,
                alphaMode: AlphaMode? = null,
                alphaMask: Texture2d? = null
            ) = Config().apply {
                pipeline {
                    this.cullMethod = cullMethod
                }
                vertices {
                    isInstanced = mesh.instances != null
                    mesh.skin?.let {
                        enableArmature(max(DepthMapPass.defaultMaxNumberOfJoints, it.nodes.size))
                    }
                    morphAttributes += mesh.geometry.getMorphAttributes()
                }
                if (alphaMode is AlphaMode.Mask) {
                    this.alphaMode = alphaMode
                    this.alphaMask = alphaMask
                }
            }
        }
    }
}