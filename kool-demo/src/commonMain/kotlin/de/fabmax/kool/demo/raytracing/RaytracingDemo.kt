package de.fabmax.kool.demo.raytracing

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.noise12
import de.fabmax.kool.modules.ksl.blocks.raySphereIntersection
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.releaseWith

class RaytracingDemo : DemoScene("Raytracing") {

    val raytracerOutput = KoolSystem.requireContext().let { ctx ->
        StorageTexture2d(ctx.windowWidth, ctx.windowHeight, TexFormat.RGBA_F32, samplerSettings = SamplerSettings().nearest())
    }.also { it.releaseWith(mainScene) }

    fun raytracingShader() = KslComputeShader("raytracer") {
        computeStage(8, 8) {
            val outputTex = storageTexture2d<KslFloat4>("outputTex", TexFormat.RGBA_F32)

            val frameId = uniformInt1("frameId")
            val imageSize = uniformInt2("imageSize")
            val camPos = uniformFloat3("camPos")
            val camMatrix = uniformMat4("camMatrix")

            main {
                `if`(frameId gt 10000.const) { `return`() }

                val pixelCoord = int2Var(inGlobalInvocationId.xy.toInt2())

                val rayOri = float3Var(camPos)
                val clipXy = float2Var((pixelCoord.toFloat2() + noise12(frameId.toFloat1())) / imageSize.toFloat2() * 2f.const - 1f.const)
                val lookAt = float4Var((camMatrix * float4Value(clipXy, 1f.const, 1f.const)))
                val rayDir = float3Var(lookAt.xyz / lookAt.w - rayOri)

                val lightDir = float3Var(normalize(Vec3f.ONES.const))
                val sphereCenter = float3Var(Vec3f.ZERO.const)
                val hit = float3Var(raySphereIntersection(rayOri, rayDir, sphereCenter, 1f.const))
                val color = float4Var(Color.BLACK.const)
                `if`(hit.z gt 0f.const) {
                    val hitPos = float3Var(rayOri + rayDir * hit.x)
                    val hitNormal = float3Var(normalize(hitPos - sphereCenter))
                    val light = float1Var(saturate(dot(lightDir, hitNormal)) * 0.95f.const + 0.05f.const)

                    color += MdColor.BLUE.const * light
                }

                val oldColor = float4Var(outputTex[pixelCoord] * frameId.toFloat1())
                outputTex[pixelCoord] = (oldColor + color) / (frameId.toFloat1() + 1f.const)
            }
        }
    }.apply {
        storageTexture2d("outputTex", raytracerOutput)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val raytracingShader = raytracingShader()
        val raytracingPass = ComputePass("raytracingPass")
        val task = raytracingPass.addTask(raytracingShader, Vec3i.ZERO)
        addComputePass(raytracingPass)

        var frameId by raytracingShader.uniform1i("frameId")
        var imageSize by raytracingShader.uniform2i("imageSize")
        var camPos by raytracingShader.uniform3f("camPos")
        var camMatrix by raytracingShader.uniformMat4f("camMatrix")

        val prevCamMatrix = MutableMat4f()
        var frameCnt = 0
        onUpdate {
            val imgW = ctx.windowWidth / 4
            val imgH = ctx.windowHeight / 4
            raytracerOutput.resize(imgW, imgH)
            task.numGroups.set(
                x = (imgW + 7) / 8,
                y = (imgH + 7) / 8,
                z = 1
            )

            imageSize = Vec2i(imgW, imgH)
            camPos = camera.globalPos
            camMatrix = camera.invViewProj

            if (prevCamMatrix != camMatrix) {
                prevCamMatrix.set(camMatrix)
                frameCnt = 0
            }
            frameId = frameCnt
            frameCnt++

        }

        defaultOrbitCamera(yaw = 0f, pitch = 0f).apply {
            smoothingDecay = 0.0
        }

        addTextureMesh {
            generateFullscreenQuad(mirrorTexCoordsY = true)
            shader = KslShader("output shader", PipelineConfig(depthTest = DepthCompareOp.ALWAYS)) {
                val uv = interStageFloat2()
                fullscreenQuadVertexStage(uv)
                fragmentStage {
                    main {
                        val tex = texture2d("outputTex", sampleType = TextureSampleType.UNFILTERABLE_FLOAT)
                        colorOutput(tex.sample(uv.output).rgb, 1f.const)
                    }
                }
            }.apply {
                texture2d("outputTex", raytracerOutput)
            }
        }
    }
}