package de.fabmax.kool.demo.pathtracing

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.*

class PathTracingDemo : DemoScene("Path-tracing") {

    fun raytracingShader() = KslComputeShader("raytracer") {
        dumpCode = true

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
            val outputTex = storage<KslFloat4>("outputTex")

            val fnRandomUnitVector = functionFloat3("randomUnitVec") {
                body {
                    val vec = float3Var(Vec3f.ZERO.const)
                    `while`((dot(vec, vec) lt 1e-10f.const) or (dot(vec, vec) gt 1f.const)) {
                        vec set noise13(random()) * 2f.const - 1f.const
                    }
                    normalize(vec)
                }
            }

            val fnRandomUnitVectorHemi = functionFloat3("randomUnitVecHemi") {
                val normal = paramFloat3()
                body {
                    val vec = float3Var(fnRandomUnitVector())
                    vec * sign(dot(vec, normal))
                }
            }

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
                `if`((frameId gt 10000.const) or (inGlobalInvocationId.x.toInt1() ge imageSize.x) or (inGlobalInvocationId.y.toInt1() ge imageSize.y)) {
                    `return`()
                }

                randomSeed(frameId.toUint1() + noise21(inGlobalInvocationId.xy.toFloat2()).toUintBits())

                val pixelCoord = int2Var(inGlobalInvocationId.xy.toInt2())
                val clipXy = float2Var((pixelCoord.toFloat2() + noise12(frameId.toFloat1())) / imageSize.toFloat2() * 2f.const - 1f.const)
                val lookAt = float4Var((camMatrix * float4Value(clipXy, 1f.const, 1f.const)))

                val ray = structVar(ray)
                ray.struct.origin.ksl set camPos
                ray.struct.direction.ksl set lookAt.xyz / lookAt.w - ray.struct.origin.ksl

                val color = float4Var(Color.BLACK.const)
                val colorMultiplier = float1Var(1f.const)

                repeat(maxBounces) { i ->
                    val hitResult = structVar(fnTraceRay(ray))

                    `if`(hitResult.struct.hitObject.ksl ge 0.const) {
                        ray.struct.origin.ksl set hitResult.struct.worldPosition.ksl + hitResult.struct.worldNormal.ksl * 0.001f.const
                        //ray.struct.direction.ksl set fnRandomUnitVectorHemi(hitResult.struct.worldNormal.ksl)
                        ray.struct.direction.ksl set hitResult.struct.worldNormal.ksl + fnRandomUnitVector()
                        colorMultiplier *= 0.5f.const

                    }.`else` {
                        val unitDir = float3Var(normalize(ray.struct.direction.ksl))
                        val a = float1Var((unitDir.y + 1f.const) * 0.5f.const)
                        val skyColor = float4Var(mix(Color.WHITE.const, Color(0.5f, 0.7f, 1f).toLinear().const, saturate(a)))
                        color += skyColor * colorMultiplier

                        `break`()
                    }
                }

                val bufferCoord = int1Var(pixelCoord.y * imageSize.x + pixelCoord.x)
                val oldColor = float4Var(outputTex[bufferCoord] * frameId.toFloat1())
                outputTex[bufferCoord] = (oldColor + color) / (frameId.toFloat1() + 1f.const)
            }
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        val spheres = StructBuffer(100, SphereStruct())
        spheres.put {
            center.set(Vec3f.ZERO)
            radius.set(0.5f)
            color.set(MdColor.PINK)
        }
        spheres.put {
            center.set(Vec3f(0f, -100.5f, 0f))
            radius.set(100f)
            color.set(MdColor.LIGHT_BLUE)
        }
        val sphereBuffer = spheres.asStorageBuffer().also { it.releaseWith(this) }

        val raytracingPass = ComputePass("raytracingPass")
        val raytracingShader = raytracingShader()
        val task = raytracingPass.addTask(raytracingShader, Vec3i.ZERO)
        addComputePass(raytracingPass)

        var frameId by raytracingShader.uniform1i("frameId")
        var imageSize by raytracingShader.uniform2i("imageSize")
        var camPos by raytracingShader.uniform3f("camPos")
        var camMatrix by raytracingShader.uniformMat4f("camMatrix")
        var outputTex by raytracingShader.storage("outputTex")

        raytracingShader.uniform1i("numObjects", spheres.size)
        raytracingShader.uniform1i("maxBounces", 5)
        raytracingShader.storage("objects", sphereBuffer)

        defaultOrbitCamera(yaw = 0f, pitch = -10f).apply {
            smoothingDecay = 0.0
            zoom = 1.5
        }

        val outputShader = KslShader("output shader", PipelineConfig(depthTest = DepthCompareOp.ALWAYS)) {
            val uv = interStageFloat2()
            fullscreenQuadVertexStage(uv)
            fragmentStage {
                main {
                    val imageSize = uniformInt2("imageSize")
                    val imageBuf = storage<KslFloat4>("outputTex")
                    val coordX = int1Var(clamp((uv.output.x * imageSize.x.toFloat1()).toInt1(), 0.const, imageSize.x))
                    val coordY = int1Var(clamp((uv.output.y * imageSize.y.toFloat1()).toInt1(), 0.const, imageSize.y))
                    colorOutput(convertColorSpace(imageBuf[coordY * imageSize.x + coordX].rgb, ColorSpaceConversion.LinearToSrgbHdr(ToneMapping.Aces)))
                }
            }
        }
        var outImageBuffer by outputShader.storage("outputTex")
        var outImageSize by outputShader.uniform2i("imageSize")

        addTextureMesh {
            generateFullscreenQuad(mirrorTexCoordsY = true)
            shader = outputShader
        }

        val prevCamMatrix = MutableMat4f()
        var frameCnt = 0
        onUpdate {
            val imgW = ctx.windowWidth / 4
            val imgH = ctx.windowHeight / 4

            val bufferSz = imgW * imgH
            val outBuffer = outputTex
            if (outBuffer == null || outBuffer.size != bufferSz) {
                outBuffer?.release()
                val gpuBuffer = GpuBuffer(
                    type = GpuType.Float4,
                    usage = BufferUsage.makeUsage(storage = true),
                    size = bufferSz
                )
                outputTex = gpuBuffer
                outImageBuffer = gpuBuffer
            }

            task.numGroups.set(
                x = (imgW + 7) / 8,
                y = (imgH + 7) / 8,
                z = 1
            )

            imageSize = Vec2i(imgW, imgH)
            outImageSize = Vec2i(imgW, imgH)
            camPos = camera.globalPos
            camMatrix = camera.invViewProj

            if (prevCamMatrix != camMatrix) {
                prevCamMatrix.set(camMatrix)
                frameCnt = 0
            }
            frameId = frameCnt
            frameCnt++
        }

        onRelease {
            outputTex?.release()
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