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

class AoDenoisePass(aoPass: OffscreenRenderPass2d, depthComponent: String) :
    OffscreenRenderPass2d(Node(), renderPassConfig {
        name = "AoDenoisePass"
        size.set(aoPass.size)
        clearDepthTexture()
        addColorTexture(TexFormat.R)
    }) {

    private val denoiseShader = DenoiseShader(aoPass, depthComponent)

    var radius: Float by denoiseShader::uRadius
    var noisyAo: Texture2d? by denoiseShader::noisyAoTex
    var depth: Texture2d? by denoiseShader::depthTex

    var clearAndDisable = false

    private val denoiseMesh: Mesh
    private val clearMesh: Mesh

    init {
        clearColor = null

        denoiseMesh = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, name = "AoDenoiseMesh").apply {
            generateFullscreenQuad()
            shader = denoiseShader
        }

        clearMesh = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, name = "AoClearMesh").apply {
            isVisible = false
            generateFullscreenQuad()
            shader = KslShader(KslProgram("AO Clear").apply {
                fullscreenQuadVertexStage(null)
                fragmentStage {
                    main {
                        colorOutput(Color.WHITE.const)
                    }
                }
            }, fullscreenShaderPipelineCfg)
        }

        drawNode.apply {
            addNode(denoiseMesh)
            addNode(clearMesh)
        }
    }

    override fun update(ctx: KoolContext) {
        if (clearAndDisable) {
            resize(1, 1, ctx)
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

    override fun release() {
        drawNode.release()
        super.release()
    }

    private fun denoiseProg(depthComponent: String) = KslProgram("Ambient Occlusion Denoise Pass").apply {
        val uv = interStageFloat2("uv")

        fullscreenQuadVertexStage(uv)

        fragmentStage {
            val noisyAoTex = texture2d("noisyAoTex")
            val depthTex = texture2d("depthTex")

            val uRadius = uniformFloat1("uRadius")

            main {
                val texelSize = float2Var(1f.const / textureSize2d(noisyAoTex).toFloat2())
                val baseDepth = float1Var(sampleTexture(depthTex, uv.output).float1(depthComponent))
                val depthThresh = float1Var(uRadius * 0.1f.const)

                val result = float1Var(0f.const)
                val weight = float1Var(0f.const)
                val hlim = float2Var(Vec2f(-AmbientOcclusionPass.NOISE_TEX_SIZE.toFloat()).const * 0.5f.const + 0.5f.const)
                fori(0.const, AmbientOcclusionPass.NOISE_TEX_SIZE.const) { y ->
                    fori(0.const, AmbientOcclusionPass.NOISE_TEX_SIZE.const) { x ->
                        val sampleUv = float2Var(uv.output + (hlim + float2Value(x.toFloat1(), y.toFloat1())) * texelSize)
                        val sampleDepth = abs(sampleTexture(depthTex, sampleUv).float1(depthComponent) - baseDepth)
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

    private inner class DenoiseShader(aoPass: OffscreenRenderPass2d, depthComponent: String)
        : KslShader(denoiseProg(depthComponent), fullscreenShaderPipelineCfg) {
        var noisyAoTex by texture2d("noisyAoTex", aoPass.colorTexture)
        var depthTex by texture2d("depthTex")
        var uRadius by uniform1f("uRadius", 1f)
    }
}