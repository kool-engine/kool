package de.fabmax.kool.demo.pathtracing

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
import de.fabmax.kool.util.*

class PathTracingDemo : DemoScene("Path-tracing") {

    val raytracerOutput = KoolSystem.requireContext().let { ctx ->
        StorageTexture2d(ctx.windowWidth, ctx.windowHeight, TexFormat.RGBA_F32, samplerSettings = SamplerSettings().nearest())
    }.also { it.releaseWith(mainScene) }

    fun raytracingShader() = KslComputeShader("raytracer") {
        computeStage(8, 8) {
            val sphere = struct { SphereStruct() }
            val ray = struct { KslRay() }
            val hitResult = struct { KslHitResult() }

            val frameId = uniformInt1("frameId")
            val imageSize = uniformInt2("imageSize")
            val camPos = uniformFloat3("camPos")
            val camMatrix = uniformMat4("camMatrix")
            val numObjects = uniformInt1("numObjects")
            val maxBounces = uniformInt1("maxBounces")

            val objects = storage("objects", sphere)
            val outputTex = storageTexture2d<KslFloat4>("outputTex", TexFormat.RGBA_F32)

            val fnClosestHit = functionStruct("closestHit", hitResult) {
                val ray = paramStruct(ray)
                val hitDistance = paramFloat1()
                val objectIndex = paramInt1()

                body {
                    val hitResult = structVar(hitResult)
                    val sphere = structVar(objects[objectIndex])
                    hitResult.struct.hitObject.ksl set objectIndex
                    hitResult.struct.hitDistance.ksl set hitDistance
                    hitResult.struct.worldPosition.ksl set ray.struct.origin.ksl + ray.struct.direction.ksl * hitDistance
                    hitResult.struct.worldNormal.ksl set normalize(hitResult.struct.worldPosition.ksl - sphere.struct.center.ksl)
                    hitResult
                }
            }

            val fnMiss = functionStruct("miss", hitResult) {
                paramStruct(ray)

                body {
                    val hitResult = structVar(hitResult)
                    hitResult.struct.hitObject.ksl set (-1).const
                    hitResult
                }
            }

            val fnTraceRay = functionStruct("traceRay", hitResult) {
                val ray = paramStruct(ray)

                body {
                    val hitResult = structVar(hitResult)
                    val hitDistance = float1Var((1e9f).const)
                    val hitObject = int1Var((-1).const)

                    repeat(numObjects) { i ->
                        val sphere = structVar(objects[i])
                        val hit = float3Var(raySphereIntersection(ray.struct.origin.ksl, ray.struct.direction.ksl, sphere.struct.center.ksl, sphere.struct.radius.ksl))
                        `if`((hit.z gt 0f.const) and (hit.x lt hitDistance)) {
                            hitDistance set hit.x
                            hitObject set i
                        }
                    }

                    `if`(hitObject lt 0.const) {
                        hitResult set fnMiss(ray)
                    }.`else` {
                        hitResult set fnClosestHit(ray, hitDistance, hitObject)
                    }
                    hitResult
                }
            }


            main {
                `if`(frameId gt 10000.const) { `return`() }

                val pixelCoord = int2Var(inGlobalInvocationId.xy.toInt2())
                val clipXy = float2Var((pixelCoord.toFloat2() + noise12(frameId.toFloat1())) / imageSize.toFloat2() * 2f.const - 1f.const)
                val lookAt = float4Var((camMatrix * float4Value(clipXy, 1f.const, 1f.const)))

                val ray = structVar(ray)
                ray.struct.origin.ksl set camPos
                ray.struct.direction.ksl set lookAt.xyz / lookAt.w - ray.struct.origin.ksl

                val lightDir = float3Var(-normalize(Vec3f.ONES.const))
                val color = float4Var(Color.BLACK.const)
                val colorMultiplier = float1Var(1f.const)

                repeat(maxBounces) { i ->
                    val hitResult = structVar(fnTraceRay(ray))

                    `if`(hitResult.struct.hitObject.ksl ge 0.const) {
                        val sphere = structVar(objects[hitResult.struct.hitObject.ksl])
                        val lightIntensity = float1Var(saturate(dot(-lightDir, hitResult.struct.worldNormal.ksl)))
                        color += sphere.struct.color.ksl * lightIntensity * colorMultiplier

                        ray.struct.origin.ksl set hitResult.struct.worldPosition.ksl + hitResult.struct.worldNormal.ksl * 0.001f.const
                        ray.struct.direction.ksl set normalize(reflect(ray.struct.direction.ksl, hitResult.struct.worldNormal.ksl))
                        colorMultiplier *= 0.7f.const
                    }.`else` {
                        `break`()
                    }
                }

                val oldColor = float4Var(outputTex[pixelCoord] * frameId.toFloat1())
                outputTex[pixelCoord] = (oldColor + color) / (frameId.toFloat1() + 1f.const)
            }
        }
    }.apply {
        storageTexture2d("outputTex", raytracerOutput)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val spheres = StructBuffer(100, SphereStruct())
        spheres.put {
            center.set(Vec3f.ZERO)
            radius.set(1.75f)
            color.set(MdColor.PINK)
        }
        spheres.put {
            center.set(Vec3f(3f, 0f, 0f))
            radius.set(1f)
            color.set(MdColor.LIGHT_BLUE)
        }
        val sphereBuffer = spheres.asStorageBuffer().also { it.releaseWith(this) }

        val raytracingPass = ComputePass("raytracingPass")
        val raytracingShader = raytracingShader()
        val task = raytracingPass.addTask(raytracingShader, Vec3i.ZERO)
        addComputePass(raytracingPass)

        raytracingShader.storage("objects", sphereBuffer)

        var frameId by raytracingShader.uniform1i("frameId")
        var imageSize by raytracingShader.uniform2i("imageSize")
        var camPos by raytracingShader.uniform3f("camPos")
        var camMatrix by raytracingShader.uniformMat4f("camMatrix")
        var numObjects by raytracingShader.uniform1i("numObjects", spheres.size)
        var maxBounces by raytracingShader.uniform1i("maxBounces", 2)

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

    class SphereStruct : Struct("Sphere", MemoryLayout.Std430) {
        val center = float3("center")
        val radius = float1("radius")
        val color = float4("color")
    }

    class KslHitResult : Struct("KslHitResult", MemoryLayout.DontCare) {
        val hitObject = int1("hitObject")
        val hitDistance = float1("hitDistance")
        val worldPosition = float3("worldPosition")
        val worldNormal = float3("worldNormal")
    }

    class KslRay : Struct("KslRay", MemoryLayout.DontCare) {
        val origin = float3("origin")
        val direction = float3("direction")
    }
}