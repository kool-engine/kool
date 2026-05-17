package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.BasicVertexConfig
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.VertexTransformBlock
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.vertexTransformBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.pipeline.shading.DepthShader.Config
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.UniqueId
import de.fabmax.kool.util.releaseDelayed

class NormalDepthMapPass(
    drawNode: Node,
    attachmentConfig: AttachmentConfig = AttachmentConfig.singleColorDefaultDepth(
        texFormat = TexFormat.RGBA,
        clearColor = ClearColorFill(Color.ZERO)
    ),
    initialSize: Vec2i = Vec2i(128, 128),
    name: String = UniqueId.nextId("normal-depth-map-pass")
) : OffscreenPass2d(drawNode, attachmentConfig, initialSize, name) {

    val depth: Texture2d get() = depthTexture!!
    val viewSpaceNormals: Texture2d get() = colorTexture!!

    var cullMethod: CullMethod? = null

    private val pipelines = mutableMapOf<NodeId, DrawPipeline?>()
    private val shaders = mutableMapOf<ShaderKey, NormalDepthShader>()

    init {
        mirrorIfInvertedClipY()
        onAfterCollectDrawCommands += { viewData ->
            val ctx = KoolSystem.requireContext()
            viewData.drawQueue.forEach { setupDrawCommand(it, ctx) }
        }
    }

    private fun setupDrawCommand(cmd: DrawCommand, ctx: KoolContext) {
        val pipeline = getPipeline(cmd.mesh, ctx)
        if (pipeline == null) {
            cmd.isActive = false
        } else {
            cmd.pipeline = pipeline
        }
    }

    private fun getPipeline(mesh: Mesh<*>, ctx: KoolContext): DrawPipeline? {
        return pipelines.getOrPut(mesh.id) {
            val shader = mesh.normalLinearDepthShader
                ?: mesh.depthShaderConfig?.let { cfg -> NormalDepthShader(cfg) }
                ?: defaultShader(mesh, ctx)
            shader?.getOrCreatePipeline(mesh, ctx)
        }
    }

    private fun defaultShader(mesh: Mesh<*>, ctx: KoolContext): NormalDepthShader? {
        if (!mesh.geometry.hasAttribute(VertexLayouts.Position.position) ||
            !mesh.geometry.hasAttribute(VertexLayouts.Normal.normal)
        ) {
            return null
        }
        val cfg = Config.forMesh(mesh, getMeshCullMethod(mesh, ctx))
        val key = ShaderKey(
            vertexLayout = mesh.geometry.layout,
            instanceLayout = mesh.instances?.layout,
            shaderCfg = cfg
        )
        return shaders.getOrPut(key) { NormalDepthShader(cfg) }
    }

    private fun getMeshCullMethod(mesh: Mesh<*>, ctx: KoolContext): CullMethod {
        return this.cullMethod ?: mesh.getOrCreatePipeline(ctx)?.cullMethod ?: CullMethod.CULL_BACK_FACES
    }

    override fun doRelease() {
        super.doRelease()
        pipelines.values
            .filterNotNull()
            .distinct()
            .filter { !it.isReleased }
            .forEach { it.releaseDelayed(1) }
    }

    private data class ShaderKey(
        val vertexLayout: Struct,
        val instanceLayout: Struct?,
        val shaderCfg: Config
    )
}

class NormalDepthShader(
    val cfg: Config,
    vertexTransformBuilder: VertexTransformBlockBuilder = VertexTransformBlockBuilder.default
) : KslShader("NormalDepth") {
    init {
        pipelineConfig = cfg.pipelineCfg
        program.program(vertexTransformBuilder)
    }

    private fun KslProgram.program(vertexTransformBuilder: VertexTransformBlockBuilder) {
        var alphaMaskUv: KslInterStageVector<KslFloat2, KslFloat1>? = null
        val viewNormal = interStageFloat3("viewNormal")

        vertexStage {
            main {
                val vertexBlock = with(vertexTransformBuilder) {
                    vertexTransformBlock(cfg.vertexCfg)
                }
                val camData = cameraData()
                val viewPos by camData.viewMat * float4Value(vertexBlock.outWorldPos, 1f.const)
                outPosition set camData.projMat * viewPos

                viewNormal.input set (camData.viewMat * float4Value(vertexBlock.outWorldNormal, 0f.const)).xyz
                if (cfg.alphaMode is AlphaMode.Mask) {
                    alphaMaskUv = interStageFloat2("alphaMaskUv").apply {
                        input set vertexAttrib(VertexLayouts.TexCoord.texCoord)
                    }
                }
            }
        }
        fragmentStage {
            main {
                (cfg.alphaMode as? AlphaMode.Mask)?.let { mask ->
                    val alpha by texture2d("tAlphaMask").sample(alphaMaskUv!!.output).a
                    `if`(alpha lt mask.cutOff.const) {
                        discard()
                    }
                }
                val encoded by encodeNormalRgb(normalize(viewNormal.output))
                colorOutput(encoded, 1f.const)
            }
        }
    }
}

fun interface VertexTransformBlockBuilder {
    context(program: KslProgram, vs: KslVertexStage)
    fun KslScopeBuilder.vertexTransformBlock(cfg: BasicVertexConfig): VertexTransformBlock

    companion object {
        val default = VertexTransformBlockBuilder { cfg ->
            vertexTransformBlock(cfg) {
                inLocalPos(contextOf<KslVertexStage>().vertexAttrib(VertexLayouts.Position.position))
                inLocalNormal(contextOf<KslVertexStage>().vertexAttrib(VertexLayouts.Normal.normal))
            }
        }
    }
}

context(scope: KslScopeBuilder)
fun encodeNormalRgb(normal: KslExprFloat3): KslExprFloat3 = float3Var(normal * 0.5f.const + 0.5f.const)

context(scope: KslScopeBuilder)
fun decodeNormalRgb(encoded: KslExprFloat3): KslExprFloat3 = float3Var(encoded * 2f.const - 1f.const)

fun KslScopeBuilder.isValidEncodedNormal(encoded: KslExprInt1): KslExprBool1 =
    encoded and 0x80000000.toInt().const ne 0.const