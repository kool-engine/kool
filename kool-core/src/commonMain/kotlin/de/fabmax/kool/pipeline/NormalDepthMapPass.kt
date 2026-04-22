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
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.UniqueId
import de.fabmax.kool.util.releaseDelayed

class NormalDepthMapPass(
    drawNode: Node,
    attachmentConfig: AttachmentConfig = AttachmentConfig.singleColorDefaultDepth(TexFormat.R_I32),
    initialSize: Vec2i = Vec2i(128, 128),
    name: String = UniqueId.nextId("normal-depth-map-pass")
) : OffscreenPass2d(drawNode, attachmentConfig, initialSize, name) {

    val encodedNormalMap: Texture2d get() = colorTexture!!

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
                    val alpha by sampleTexture(texture2d("tAlphaMask"), alphaMaskUv!!.output).a
                    `if`(alpha lt mask.cutOff.const) {
                        discard()
                    }
                }
                val normal by normalize(viewNormal.output)
                val encoded = encodeNormal(normal)
                intOutput(int4Value(encoded, 0.const, 0.const, 0.const))
            }
        }
    }
}

fun interface VertexTransformBlockBuilder {
    context(vs: KslVertexStage)
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

fun KslScopeBuilder.encodeNormal(normal: KslVarFloat3): KslVarInt1 {
    val sign by step(0f.const, dot(normal, float3Value(0f.const, 0f.const, 1f.const))).toInt1()
    val x by clamp(((normal.x + 1f.const) * 16383f.const).toInt1(), 0.const, 32766.const)
    val y by clamp(((normal.y + 1f.const) * 16383f.const).toInt1(), 0.const, 32766.const)
    val encoded by 0x80000000.toInt().const or (sign shl 15.const) or (y shl 16.const) or x
    return encoded
}

fun KslScopeBuilder.decodeNormal(encoded: KslVarInt1): KslVarFloat3 {
    val decodedNormal by 0f.const3
    `if`(encoded and 0x80000000.toInt().const ne 0.const) {
        val y by ((encoded shr 16.const) and 0x7fff.const).toFloat1() / 16383f.const - 1f.const
        val x by (encoded and 0x7fff.const).toFloat1() / 16383f.const - 1f.const
        val sign by (((encoded shr 15.const) and 1.const) * 2.const - 1.const).toFloat1()
        val z by sqrt(clamp(1f.const - x*x - y*y, 0f.const, 1f.const)) * sign
        decodedNormal.set(float3Value(x, y, z))
    }
    return decodedNormal
}

fun KslScopeBuilder.isValidEncodedNormal(encoded: KslVarInt1): KslVarBool1 {
    val isValidEncodedNormal by encoded and 0x80000000.toInt().const ne 0.const
    return isValidEncodedNormal
}