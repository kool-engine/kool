package de.fabmax.kool.demo.pathtracing

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.MenuRow
import de.fabmax.kool.demo.MenuSlider1
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class PathTracingDemo : DemoScene("Path-tracing") {

    private val numSamples = mutableStateOf(0)
    private val defocusAngle = mutableStateOf(0.6f).onChange { _, _ -> numSamples.set(0) }
    private val focusDistance = mutableStateOf(10f).onChange { _, _ -> numSamples.set(0) }
    private val cameraFovy = mutableStateOf(20f).onChange { _, _ -> numSamples.set(0) }
    private val resolution = mutableStateOf(0.25f).onChange { _, _ -> numSamples.set(0) }

    fun raytracingShader() = KslComputeShader("raytracer") {
        computeStage(8, 8) {
            val sphere = struct { SphereStruct() }
            val material = struct { MaterialStruct() }
            val ray = struct { KslRay() }
            val hitResult = struct { KslHitResult() }

            val numObjects = uniformInt1("numObjects")
            val maxBounces = uniformInt1("maxBounces")

            val camMatrix = uniformMat4("camMatrix")
            val frameId = uniformInt1("frameId")
            val imageSize = uniformInt2("imageSize")
            val camPos = uniformFloat3("camPos")
            val camUp = uniformFloat3("camUp")
            val camRight = uniformFloat3("camRight")
            val camFront = uniformFloat3("camFront")
            val defocusAngle = uniformFloat1("defocusAngle")
            val focusDist = uniformFloat1("focusDist")

            val objects = storage("objects", sphere)
            val materials = storage("materials", material)
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

            val fnRandomVecInUnitDisc = functionFloat2("randomVecInUnitDisc") {
                body {
                    val vec = float2Var(Vec2f.ONES.const)
                    `while`(dot(vec, vec) gt 1f.const) {
                        vec set noise12(random()) * 2f.const - 1f.const
                    }
                    vec
                }
            }

            val fnScatterLambertian = functionStruct("scatterLambertian", ray) {
                val hit = paramStruct(hitResult)
                body {
                    val scatterLambertian = float3Var(normalize(hit.struct.worldNormal.ksl + fnRandomUnitVector() * 0.9999f.const))
                    val outRay = structVar(ray)
                    outRay.struct.origin.ksl set hit.struct.worldPosition.ksl + hit.struct.worldNormal.ksl * 0.0001f.const
                    outRay.struct.direction.ksl set scatterLambertian
                    outRay
                }
            }

            val fnScatterMetal = functionStruct("scatterMetal", ray) {
                val inRay = paramStruct(ray)
                val hit = paramStruct(hitResult)
                val roughness = paramFloat1()
                body {
                    val scatterReflective = float3Var(
                        normalize(reflect(inRay.struct.direction.ksl, hit.struct.worldNormal.ksl)) +
                        fnRandomUnitVector() * clamp(roughness, 0f.const, 0.9999f.const)
                    )
                    val outRay = structVar(ray)
                    outRay.struct.direction.ksl set scatterReflective
                    outRay.struct.origin.ksl set hit.struct.worldPosition.ksl + outRay.struct.direction.ksl * 0.0001f.const
                    outRay
                }
            }

            val fnReflectance = functionFloat1("reflectance") {
                val cos = paramFloat1()
                val ior = paramFloat1()
                body {
                    val r0 = float1Var((1f.const - ior) / (1f.const + ior))
                    r0 set r0 * r0
                    r0 + (1f.const - r0) * pow(1f.const - cos, 5f.const)
                }
            }

            val fnScatterGlas = functionStruct("scatterGlas", ray) {
                val inRay = paramStruct(ray)
                val hit = paramStruct(hitResult)
                val refractionIndex = paramFloat1()
                body {
                    val ri = float1Var(refractionIndex)
                    `if`(hit.struct.isFrontFace.ksl) {
                        ri set 1f.const / ri
                    }

                    val unitDir = float3Var(normalize(inRay.struct.direction.ksl))
                    val cosTheta = float1Var(min(dot(-unitDir, hit.struct.worldNormal.ksl), 1f.const))
                    val sinTheta = float1Var(sqrt(1f.const - (cosTheta * cosTheta)))
                    val cannotRefract = bool1Var((ri * sinTheta) gt 1f.const)

                    val outRay = structVar(ray)
                    `if`(cannotRefract or (fnReflectance(cosTheta, ri) gt randomF())) {
                        outRay.struct.direction.ksl set reflect(unitDir, hit.struct.worldNormal.ksl)
                    }.`else` {
                        outRay.struct.direction.ksl set refract(unitDir, hit.struct.worldNormal.ksl, ri)
                    }

                    outRay.struct.origin.ksl set hit.struct.worldPosition.ksl + outRay.struct.direction.ksl * 0.0001f.const
                    outRay
                }
            }

            val fnClosestHit = functionStruct("closestHit", hitResult) {
                val ray = paramStruct(ray)
                val hitDistance = paramFloat1()
                val objectIndex = paramInt1()

                body {
                    val hit = structVar(hitResult)
                    val sphere = structVar(objects[objectIndex])
                    hit.struct.hitObject.ksl set objectIndex
                    hit.struct.hitDistance.ksl set hitDistance
                    hit.struct.worldPosition.ksl set ray.struct.origin.ksl + ray.struct.direction.ksl * hitDistance
                    hit.struct.worldNormal.ksl set normalize(hit.struct.worldPosition.ksl - sphere.struct.center.ksl)
                    hit.struct.isFrontFace.ksl set (dot(ray.struct.direction.ksl, hit.struct.worldNormal.ksl) lt 0f.const)
                    `if`(!hit.struct.isFrontFace.ksl) {
                        hit.struct.worldNormal.ksl set hit.struct.worldNormal.ksl * (-1f).const
                    }
                    hit
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
                        `if`(hit.z gt 0f.const) {
                            `if`((hit.x gt 0f.const) and (hit.x lt hitDistance)) {
                                hitDistance set hit.x
                                hitObject set i
                            }.elseIf((hit.y gt 0f.const) and (hit.y lt hitDistance)) {
                                hitDistance set hit.y
                                hitObject set i
                            }
                        }
                    }

                    `if`(hitObject ge 0.const) {
                        hitResult set fnClosestHit(ray, hitDistance, hitObject)
                    }.`else` {
                        hitResult set fnMiss(ray)
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
                val color = float4Var(Color.BLACK.const)

                val clipXy = float2Var((pixelCoord.toFloat2() + noise12(random())) / imageSize.toFloat2() * 2f.const - 1f.const)
                val proj = float4Var((camMatrix * float4Value(clipXy, 1f.const, 1f.const)))
                val lookAt = float3Var(proj.xyz / proj.w)
                val lookDir = float3Var(normalize(lookAt - camPos))
                val lookDist = float3Var(lookDir * focusDist / dot(lookDir, camFront))
                lookAt set camPos + lookDist

                val ray = structVar(ray)
                ray.struct.origin.ksl set camPos

                `if`(defocusAngle gt 0f.const) {
                    val defocusRadius = float1Var(focusDist * tan(defocusAngle / 2f.const))
                    val sample = fnRandomVecInUnitDisc()
                    ray.struct.origin.ksl += camRight * sample.x * defocusRadius
                    ray.struct.origin.ksl += camUp * sample.y * defocusRadius
                }
                ray.struct.direction.ksl set lookAt - ray.struct.origin.ksl

                val colorAttenuation = float3Var(Vec3f.ONES.const)
                repeat(maxBounces) { bounce ->
                    val hit = structVar(fnTraceRay(ray))

                    `if`(hit.struct.hitObject.ksl ge 0.const) {
                        val hitObject = structVar(objects[hit.struct.hitObject.ksl])
                        val hitMaterial = structVar(materials[hitObject.struct.material.ksl])
                        colorAttenuation *= hitMaterial.struct.albedo.ksl.rgb

                        `if`(hitMaterial.struct.materialType.ksl eq MATERIAL_LAMBERTIAN.const) {
                            ray set fnScatterLambertian(hit)
                        }.elseIf(hitMaterial.struct.materialType.ksl eq MATERIAL_METAL.const) {
                            ray set fnScatterMetal(ray, hit, hitMaterial.struct.roughness.ksl)
                        }.elseIf(hitMaterial.struct.materialType.ksl eq MATERIAL_GLASS.const) {
                            ray set fnScatterGlas(ray, hit, hitMaterial.struct.refractionIndex.ksl)
                        }

                    }.`else` {
                        val unitDir = float3Var(normalize(ray.struct.direction.ksl))
                        val a = float1Var((unitDir.y + 1f.const) * 0.5f.const)
                        val skyColor = float4Var(mix(Color.WHITE.const, Color(0.5f, 0.65f, 1f).const, saturate(a)))
                        color += skyColor * float4Value(colorAttenuation, 1f.const)
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
        val materials = StructBuffer(1024, MaterialStruct())
        val spheres = StructBuffer(1024, SphereStruct())
        makeManySpheres(spheres, materials)

        val materialBuffer = materials.asStorageBuffer().also { it.releaseWith(this) }
        val sphereBuffer = spheres.asStorageBuffer().also { it.releaseWith(this) }

        val raytracingPass = ComputePass("raytracingPass")
        val raytracingShader = raytracingShader()
        val task = raytracingPass.addTask(raytracingShader, Vec3i.ZERO)
        addComputePass(raytracingPass)

        var frameId by raytracingShader.uniform1i("frameId")
        var imageSize by raytracingShader.uniform2i("imageSize")
        var camPos by raytracingShader.uniform3f("camPos")
        var camUp by raytracingShader.uniform3f("camUp")
        var camRight by raytracingShader.uniform3f("camRight")
        var camFront by raytracingShader.uniform3f("camFront")
        var camMatrix by raytracingShader.uniformMat4f("camMatrix")
        var outputTex by raytracingShader.storage("outputTex")

        var defocusAngle by raytracingShader.uniform1f("defocusAngle", defocusAngle.value.deg.rad)
        var focusDist by raytracingShader.uniform1f("focusDist", focusDistance.value)

        raytracingShader.uniform1i("numObjects", spheres.size)
        raytracingShader.uniform1i("maxBounces", 10)
        raytracingShader.storage("materials", materialBuffer)
        raytracingShader.storage("objects", sphereBuffer)

        defaultOrbitCamera(yaw = 78f, pitch = -9f).apply {
            smoothingDecay = 0.0
            zoom = 13.5
            maxZoom = 1000.0
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
        onUpdate {
            val imgW = (ctx.windowWidth * resolution.value).roundToInt()
            val imgH = (ctx.windowHeight * resolution.value).roundToInt()

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
            camUp = camera.globalUp
            camRight = camera.globalRight
            camFront = camera.globalLookDir
            camMatrix = camera.invViewProj

            if (prevCamMatrix != camMatrix) {
                prevCamMatrix.set(camMatrix)
                numSamples.set(0)
            }
            frameId = numSamples.value
            defocusAngle = this@PathTracingDemo.defocusAngle.value.deg.rad
            focusDist = this@PathTracingDemo.focusDistance.value
            (camera as PerspectiveCamera).fovY = cameraFovy.value.deg

            raytracingPass.isEnabled = numSamples.value < 10000
            if (raytracingPass.isEnabled) {
                numSamples.set(numSamples.value + 1)
            }
        }

        onRelease {
            outputTex?.release()
        }
    }

    private fun makeManySpheres(spheres: StructBuffer<SphereStruct>, materials: StructBuffer<MaterialStruct>) {
        val groundMaterial = materials.addLambertian(MdColor.GREY toneLin 500)
        val material1 = materials.addGlas(1.5f)
        val material2 = materials.addLambertian(Color(0.4f, 0.2f, 0.1f))
        val material3 = materials.addMetal(Color(0.7f, 0.6f, 0.5f), 0f)

        spheres.addSphere(Vec3f(0f, -1000f, 0f), 1000f, groundMaterial)
        spheres.addSphere(Vec3f(0f, 1f, 0f), 1f, material1)
        spheres.addSphere(Vec3f(-4f, 1f, 0f), 1f, material2)
        spheres.addSphere(Vec3f(4f, 1f, 0f), 1f, material3)

        val rand = Random(17)
        for (y in -7 .. 7) {
            for (x in -11 .. 11) {
                val mat = rand.randomF()
                val pos = Vec3f(x + 0.8f * rand.randomF(), 0.2f, y + 0.8f * rand.randomF())

                if (pos.distance(Vec3f(0f, 0.2f, 0f)) > 0.9f &&
                    pos.distance(Vec3f(4f, 0.2f, 0f)) > 0.9f &&
                    pos.distance(Vec3f(-4f, 0.2f, 0f)) > 0.9f) {
                    val matI = when {
                        mat < 0.75f -> materials.addLambertian(rand.randomColor())
                        mat < 0.93f -> materials.addMetal(rand.randomColor(), rand.randomF(0f, 0.5f))
                        else -> materials.addGlas(1.5f)
                    }
                    spheres.addSphere(pos, 0.2f, matI)
                }
            }
        }
    }

    private fun Random.randomColor(): Color {
        return Color.Hsv(randomF(0f, 360f), randomF(0.3f, 0.9f).pow(2), sqrt(randomF(0.3f, 1f))).toLinearRgb()
    }

    private fun StructBuffer<SphereStruct>.addSphere(center: Vec3f, radius: Float, material: Int) = put {
        this.center.set(center)
        this.radius.set(radius)
        this.material.set(material)
    }

    private fun StructBuffer<MaterialStruct>.addLambertian(color: Color): Int = put {
        materialType.set(MATERIAL_LAMBERTIAN)
        albedo.set(color)
    }

    private fun StructBuffer<MaterialStruct>.addMetal(color: Color, roughness: Float): Int = put {
        materialType.set(MATERIAL_METAL)
        albedo.set(color)
        this.roughness.set(roughness)
    }

    private fun StructBuffer<MaterialStruct>.addGlas(refractionIndex: Float): Int = put {
        materialType.set(MATERIAL_GLASS)
        albedo.set(Color.WHITE)
        this.refractionIndex.set(refractionIndex)
    }

    class MaterialStruct : Struct("Material", MemoryLayout.Std430) {
        val albedo = float4("albedo")
        val roughness = float1("roughness")
        val refractionIndex = float1("refractionIndex")
        val materialType = int1("materialType")
    }

    class SphereStruct : Struct("Sphere", MemoryLayout.Std430) {
        val center = float3("center")
        val radius = float1("radius")
        val material = int1("materialIndex")
    }

    class KslHitResult : Struct("KslHitResult", MemoryLayout.DontCare) {
        val hitObject = int1("hitObject")
        val hitDistance = float1("hitDistance")
        val worldPosition = float3("worldPosition")
        val worldNormal = float3("worldNormal")
        val isFrontFace = bool1("isFrontFace")
    }

    class KslRay : Struct("KslRay", MemoryLayout.DontCare) {
        val origin = float3("origin")
        val direction = float3("direction")
    }

    companion object {
        const val MATERIAL_LAMBERTIAN = 1
        const val MATERIAL_METAL = 2
        const val MATERIAL_GLASS = 3    // the raytracing in one weekend book calls this dielectric
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        MenuSlider1("Focus:", defocusAngle.use(), 0f, 5f) { defocusAngle.set(it) }
        MenuSlider1("Distance:", sqrt(focusDistance.use()), 1f, sqrt(1000f), { (it*it).toString(2) }) { focusDistance.set(it * it) }
        MenuSlider1("Zoom:", cameraFovy.use(), 10f, 60f) { cameraFovy.set(it) }
        MenuSlider1("Resolution:", resolution.use(), 0.25f, 1f) { resolution.set(it) }
        MenuRow {
            Text("Samples:") { modifier.width(Grow.Std).alignY(AlignmentY.Center) }
            Text("${numSamples.use()}") { modifier.alignY(AlignmentY.Center) }
        }
    }
}