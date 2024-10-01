package de.fabmax.kool.pipeline.mssao

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.BasicVertexConfig
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.VertexTransformBlock
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.vertexTransformBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.logE

class GbufferPass : OffscreenRenderPass2d(
    drawNode = Node(),
    attachmentConfig = AttachmentConfig(
        colorAttachments = ColorAttachmentTextures(
            listOf(TextureAttachmentConfig(TexFormat.RGBA_F16, SamplerSettings().clamped().nearest()))
        ),
        depthAttachment = DepthAttachmentTexture()
    ),
    initialSize = Vec2i(128),
    name = "mssao-gbuffer-pass"
) {

    private var sceneRenderCallback: SceneSizeRenderCallback? = null

    private val pipelines = mutableMapOf<NodeId, DrawPipeline?>()
    private val shaders = mutableMapOf<ShaderKey, NormalShader>()

    fun install(scene: Scene) {
        if (!scene.isInfiniteDepth) {
            logE { "Gbuffer pass requires reverse depth scene" }
            return
        }

        val proxyCam = PerspectiveProxyCam(scene.camera as PerspectiveCamera)

        isReverseDepth = true
        drawNode = scene
        camera = proxyCam
        sceneRenderCallback = SceneSizeRenderCallback(this, scene)
        scene.addOffscreenPass(this)

        onBeforeCollectDrawCommands += { ev ->
            proxyCam.sync(ev)
        }

        mirrorIfInvertedClipY()
        onAfterCollectDrawCommands += { ev ->
            // replace regular object shaders by gbuffer shaders
            val q = ev.view.drawQueue
            q.forEach {
                setupDrawCommand(it, ev)
            }
        }
    }

    private fun setupDrawCommand(cmd: DrawCommand, updateEvent: UpdateEvent) {
        val pipeline = getPipeline(cmd.mesh, updateEvent.ctx)
        if (pipeline == null || !cmd.mesh.isCastingShadow) {
            cmd.isActive = false
        } else {
            cmd.pipeline = pipeline
        }
    }

    private fun getPipeline(mesh: Mesh, ctx: KoolContext): DrawPipeline? {
        return pipelines.getOrPut(mesh.id) {
            defaultShader(mesh)?.getOrCreatePipeline(mesh, ctx)
        }
    }

    private fun defaultShader(mesh: Mesh): NormalShader? {
        if (!mesh.geometry.hasAttribute(Attribute.POSITIONS) || !mesh.geometry.hasAttribute(Attribute.NORMALS)) {
            return null
        }
        val key = ShaderKey(
            vertexLayout = mesh.geometry.vertexAttributes,
            instanceLayout = mesh.instances?.instanceAttributes ?: emptyList()
        )
        return shaders.getOrPut(key) { NormalShader(NormalShader.Config.forMesh(mesh)) }
    }

    private data class ShaderKey(
        val vertexLayout: List<Attribute>,
        val instanceLayout: List<Attribute>
    )

    private class NormalShader(val cfg: Config) : KslShader("gbuffer-normals") {
        init {
            pipelineConfig = PipelineConfig(blendMode = BlendMode.DISABLED)
            program.normalProgram()
        }

        private fun KslProgram.normalProgram() {
            val viewNormal = interStageFloat3()
            var alphaMaskUv: KslInterStageVector<KslFloat2, KslFloat1>? = null

            vertexStage {
                main {
                    val camData = cameraData()
                    val vertexBlock = vertexTransformBlock(cfg.vertexCfg) {
                        inLocalPos(vertexAttribFloat3(Attribute.POSITIONS.name))
                        inLocalNormal(vertexAttribFloat3(Attribute.NORMALS.name))
                    }

                    val viewPos = float4Var(camData.viewMat * float4Value(vertexBlock.outWorldPos, 1f.const))
                    outPosition set camData.projMat * viewPos
                    viewNormal.input set (camData.viewMat * float4Value(vertexBlock.outWorldNormal, 0f.const)).xyz

                    if (cfg.alphaMode is AlphaMode.Mask) {
                        alphaMaskUv = interStageFloat2("alphaMaskUv").apply {
                            input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
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

                    val normal = float3Var(normalize(viewNormal.output))
                    `if` (!inIsFrontFacing) {
                        normal set normal * (-1f).const
                    }
                    colorOutput(float4Value(normal, 1f.const))
                }
            }
        }

        data class Config(
            val pipelineCfg: PipelineConfig = PipelineConfig(),
            val vertexCfg: BasicVertexConfig = BasicVertexConfig.Builder().build(),
            val alphaMode: AlphaMode = AlphaMode.Opaque,
            val alphaMask: Texture2d? = null,
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
                    alphaMask = alphaMask
                )
            }
        }
    }
}