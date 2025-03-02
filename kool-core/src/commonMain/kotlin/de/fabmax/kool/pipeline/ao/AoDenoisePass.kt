package de.fabmax.kool.pipeline.ao

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenShaderPipelineCfg
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchDelayed

class AoDenoisePass(aoPass: OffscreenPass2d, depthComponent: String) :
    OffscreenPass2d(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.R),
        initialSize = aoPass.size.xy,
        name = "ambient-occlusion-denoise"
    )
{
    val denoiseShader = DenoiseShader(aoPass, depthComponent)

    var radius: Float by denoiseShader::uRadius
    var noisyAo: Texture2d? by denoiseShader::noisyAoTex
    var linearDepth: Texture2d? by denoiseShader::viewSpaceTex

    var clearAndDisable = false

    private val denoiseMesh: Mesh = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, name = "AoDenoiseMesh").apply {
        generateFullscreenQuad()
        shader = denoiseShader
    }
    private val clearMesh: Mesh = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, name = "AoClearMesh").apply {
        isVisible = false
        generateFullscreenQuad()
        shader = KslShader("AO Clear", fullscreenShaderPipelineCfg) {
            fullscreenQuadVertexStage(null)
            fragmentStage {
                main {
                    colorOutput(Color.WHITE.const)
                }
            }
        }
    }

    init {
        drawNode.apply {
            addNode(denoiseMesh)
            addNode(clearMesh)
        }
    }

    override fun update(ctx: KoolContext) {
        if (clearAndDisable) {
            setSize(1, 1)
            clearAndDisable = false
            denoiseMesh.isVisible = false
            clearMesh.isVisible = true

            launchDelayed(5) {
                isEnabled = false
                denoiseMesh.isVisible = true
                clearMesh.isVisible = false
            }
        }

        super.update(ctx)
    }

    inner class DenoiseShader(aoPass: OffscreenPass2d, depthComponent: String) :
        KslShader("Ambient Occlusion Denoise Pass")
    {
        var noisyAoTex by texture2d("noisyAoTex", aoPass.colorTexture)
        var viewSpaceTex by texture2d("viewSpaceTex")
        var uRadius by uniform1f("uRadius", 1f)

        init {
            pipelineConfig = fullscreenShaderPipelineCfg
            program.denoiseProg(depthComponent)
        }

        private fun KslProgram.denoiseProg(depthComponent: String) {
            val uv = interStageFloat2("uv")

            fullscreenQuadVertexStage(uv)

            fragmentStage {
                val noisyAoTex = texture2d("noisyAoTex")
                val viewSpaceTex = texture2d("viewSpaceTex")
                val uRadius = uniformFloat1("uRadius")

                main {
                    val texelSize = float2Var(1f.const / textureSize2d(noisyAoTex).toFloat2())
                    val baseDepth = float1Var(sampleTexture(viewSpaceTex, uv.output).float1(depthComponent))
                    val depthThresh = float1Var(uRadius * 0.1f.const)

                    val result = float1Var(0f.const)
                    val weight = float1Var(0f.const)
                    val hlim = float2Var(Vec2f(-AmbientOcclusionPass.NOISE_TEX_SIZE.toFloat()).const * 0.5f.const + 0.5f.const)
                    fori(0.const, AmbientOcclusionPass.NOISE_TEX_SIZE.const) { y ->
                        fori(0.const, AmbientOcclusionPass.NOISE_TEX_SIZE.const) { x ->
                            val sampleUv = float2Var(uv.output + (hlim + float2Value(x.toFloat1(), y.toFloat1())) * texelSize)
                            val sampleDepth = abs(sampleTexture(viewSpaceTex, sampleUv).float1(depthComponent) - baseDepth)
                            val w = 1f.const - step(depthThresh, sampleDepth) * 0.99f.const
                            result += sampleTexture(noisyAoTex, sampleUv).r * w
                            weight += w
                        }
                    }
                    result /= weight
                    colorOutput(float4Value(result, result, result, 1f.const))
                }
            }
        }
    }
}