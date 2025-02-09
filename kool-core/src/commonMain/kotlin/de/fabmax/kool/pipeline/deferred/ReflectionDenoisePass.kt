package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh

class ReflectionDenoisePass(reflectionPass: OffscreenPass2d) :
    OffscreenPass2d(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RGBA),
        initialSize = reflectionPass.size.xy,
        name = "reflection-denoise"
    )
{

    private lateinit var denoiseShader: DenoiseShader

    init {
        drawNode.apply {
            addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS) {
                generateFullscreenQuad()

                denoiseShader = DenoiseShader(reflectionPass.colorTexture)
                shader = denoiseShader
            }
        }

        dependsOn(reflectionPass)
    }

    fun setPositionInput(materialPass: MaterialPass) {
        denoiseShader.createdPipeline?.swapPipelineData(materialPass)
        denoiseShader.depthTex = materialPass.positionFlags
    }

    private fun denoiseProg() = KslProgram("Reflection Denoise Pass").apply {
        val uv = interStageFloat2("uv")

        fullscreenQuadVertexStage(uv)

        fragmentStage {
            val noisyReflectionTex = texture2d("noisyReflectionTex")
            val positionTex = texture2d("positionFlags")

            main {
                val blurSize = ReflectionPass.NOISE_SIZE.const
                val texelSize = float2Var(1f.const / textureSize2d(noisyReflectionTex).toFloat2())
                val depthOri = float1Var(sampleTexture(positionTex, uv.output).z)
                val depthThreshold = float1Var(max(0.3f.const, depthOri * 0.05f.const))
                val output = float4Var(Vec4f.ZERO.const)

                val weight = float1Var(0f.const)
                val hlim = float2Var(Vec2f(-ReflectionPass.NOISE_SIZE.toFloat()).const * 0.5f.const + 0.5f.const)
                fori(0.const, blurSize) { x ->
                    fori(0.const, blurSize) { y ->
                        val sampleUv = float2Var(uv.output + (hlim + float2Value(x.toFloat1(), y.toFloat1())) * texelSize)
                        val sampleDepth = abs(sampleTexture(positionTex, sampleUv).z - depthOri)
                        val w = 1f.const - step(depthThreshold, sampleDepth) * 0.99f.const

                        output += sampleTexture(noisyReflectionTex, sampleUv) * w
                        weight += w
                    }
                }
                colorOutput(output / weight)
            }
        }
    }

    private inner class DenoiseShader(noisyReflection: Texture2d?)
        : KslShader(denoiseProg(), FullscreenShaderUtil.fullscreenShaderPipelineCfg) {
        var noisyReflectionTex by texture2d("noisyReflectionTex", noisyReflection)
        var depthTex by texture2d("positionFlags")
    }
}