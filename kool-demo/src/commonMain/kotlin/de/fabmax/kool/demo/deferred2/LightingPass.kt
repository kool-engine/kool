package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.NormalLightRange
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.util.ColorGradient

class LightingPass(
    size: Vec2i,
    private val pipeline: Deferred2Pipeline,
) : OffscreenPass2d(
    drawNode = Node(),
    attachmentConfig = AttachmentConfig {
        addColor(TexFormat.RG11B10_F, filterMethod = FilterMethod.NEAREST)   // metal, roughness, ao
        transientDepth()
    },
    initialSize = size,
    name = "deferred2-lighting-pass"
) {
    val lightingOutput: Texture2d get() = colorTexture!!

    private val lightingShader = DeferredLightingShader(pipeline.isScreenSpaceReflections)

    init {
        camera = pipeline.camera
        lighting = pipeline.lighting

        val outputMesh = Mesh(VertexLayouts.PositionTexCoord).apply {
            generateFullscreenQuad()
            shader = lightingShader
        }
        drawNode.addNode(outputMesh)
        drawNode.addNode(Skybox.cube(pipeline.ibl.reflectionMap, 2f, colorSpaceConversion = ColorSpaceConversion.AsIs))

        onAfterCollectDrawCommands += { viewData ->
            val ctx = KoolSystem.requireContext()
            val gbuffer = pipeline.gbuffers.newVal
            for (i in gbuffer.lightMeshes.indices) {
                val mesh = gbuffer.lightMeshes[i]
                mesh.getOrCreatePipeline(ctx)?.let { pipeline ->
                    viewData.drawQueue.addMesh(mesh, pipeline)
                }
            }
        }
    }

    fun swapBuffers() {
        val newGbuffer = pipeline.gbuffers.newVal
        lightingShader.swapPipelineData(newGbuffer) {
            depthTex = newGbuffer.depth
            scaledViewZ = pipeline.aoPass.scaledDists
            encodedNormals = newGbuffer.normals
            albedoEmissionTex = newGbuffer.albedoEmission
            metalRoughnessAoTex = newGbuffer.metalRoughnessAo
            irradianceMap = pipeline.ibl.irradianceMap
            reflectionMap = pipeline.ibl.reflectionMap
            aoMap = pipeline.aoPass.aoMap
            camData = pipeline.camData
            oldColor = pipeline.filterPass.filterOutput.oldVal
        }
    }
}

class DeferredLightingShader(isScreenSpaceReflections: Boolean) : KslShader("deferred2-lighting") {
    var depthTex by bindTexture2d("depth")
    var scaledViewZ by bindTexture2d("scaledViewZ")
    var encodedNormals by bindTexture2d("encodedNormals")
    var albedoEmissionTex by bindTexture2d("albedoEmission")
    var metalRoughnessAoTex by bindTexture2d("metalRoughnessAo")
    var irradianceMap by bindTextureCube("irradiance")
    var reflectionMap by bindTextureCube("reflection")
    var brdf by bindTexture2d("brdf", KoolSystem.requireContext().defaultPbrBrdfLut)
    var aoMap by bindTexture2d("aoMap")
    var camData by bindStorage("camData")

    var oldColor by bindTexture2d("oldColor")

    var ambientMapOrientation: Mat3f by bindUniformMat3("uAmbientTextureOri", Mat3f.IDENTITY)

    init {
        pipelineConfig = PipelineConfig(
            blendMode = BlendMode.DISABLED,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = DepthCompareOp.ALWAYS
        )
        program.program(isScreenSpaceReflections)

        bindTexture1d("tgradient", GradientTexture(ColorGradient.ROCKET))
    }

    private fun KslProgram.program(isScreenSpaceReflections: Boolean) {
        val uv = interStageFloat2()
        fullscreenQuadVertexStage(uv)
        fragmentStage {
            val depth = texture2d("depth", isUnfilterable = true)
            val scaledViewZ = texture2d("scaledViewZ", isUnfilterable = true)
            val encodedNormals = texture2dInt("encodedNormals")
            val albedoEmission = texture2d("albedoEmission")
            val metalRoughnessAo = texture2d("metalRoughnessAo")
            val reflection = textureCube("reflection")
            val irradiance = textureCube("irradiance")
            val brdf = texture2d("brdf")
            val aoMap = texture2d("aoMap")

            val ambientOri = uniformMat3("uAmbientTextureOri")
            val camDataLayout = struct(DeferredCamDataLayout)
            val camData = storage("camData", camDataLayout)

            main {
                val size by depth.size()
                val baseCoord by (uv.output * size.toFloat2()).toInt2()
                val depthSample by depth.load(baseCoord, lod = 0.const).x
                `if` (depthSample eq 0f.const) {
                    discard()
                }

                val camNear by camData.camNear
                val camPos by camData.camPosition
                val invView by camData.invView
                val invViewProj by camData.invViewProj
                val worldPos by unprojectBaseCoord(depthSample, baseCoord, size, camNear, invViewProj).xyz
                val ssao by aoMap.load(baseCoord, lod = 0.const).x

                val encodedNormal by encodedNormals.load(baseCoord, lod = 0.const).x
                val viewNormal by decodeNormalInt(encodedNormal)
                val worldNormal by normalize((invView * float4Value(viewNormal, 0f.const)).xyz)

                val albedoEmission by float4Var(albedoEmission.load(baseCoord, lod = 0.const))
                val albedo by albedoEmission.xyz
                val emissiveStrength by albedoEmission.w * 64f.const

                val metalRoughnessAo by metalRoughnessAo.load(baseCoord, lod = 0.const).xyz
                val metallic by metalRoughnessAo.x
                val roughness by metalRoughnessAo.y
                val ao by metalRoughnessAo.z

                val ambient by irradiance.sample(worldNormal).rgb * ssao

                // todo: config max lights
                val maxNumberOfLights = 4
                val normalLightRange = NormalLightRange.ZeroToOne
                val shadowFactors = float1Array(maxNumberOfLights, 1f.const)
                val lightData = sceneLightData(maxNumberOfLights)
                val material = pbrMaterialBlock(maxNumberOfLights, listOf(reflection), brdf, normalLightRange) {
                    inCamPos(camPos)
                    inNormal(worldNormal)
                    inFragmentPos(worldPos)
                    inBaseColor(float4Value(albedo, 1f.const))

                    inRoughness(roughness)
                    inMetallic(metallic)

                    inIrradiance(ambient)
                    inAoFactor(ao)
                    inAmbientOrientation(ambientOri)

                    setLightData(lightData, shadowFactors, 1f.const)
                }

                if (isScreenSpaceReflections) {
                    val oldColor = texture2d("oldColor")
                    val screenReflection by screenReflect(material, viewNormal, scaledViewZ, oldColor, camData)
                    val finalColor by material.outAmbient + material.outLight + screenReflection + albedo * emissiveStrength
                    colorOutput(finalColor)
//                    colorOutput(screenReflection)
                } else {
                    colorOutput(material.outColor)
                }

                outDepth set depthSample
            }
        }
    }
}

context(_: KslProgram, _: KslShaderStage)
fun KslScopeBuilder.screenReflect(
    material: PbrMaterialBlock,
    viewNormal: KslExprFloat3,
    viewZ: KslUniform<KslColorSampler2d>,
    oldColor: KslUniform<KslColorSampler2d>,
    camData: KslStructStorage<DeferredCamDataLayout>
): KslExprFloat3 {
    val fnProjViewPos = functionFloat2("fnProiViewPos") {
        val viewPos = paramFloat3("viewPos")
        body {
            val p by camData.proj * float4Value(viewPos, 1f)
            p.xy / p.w * float2Value(0.5f, -0.5f) + 0.5f.const
        }
    }

    val fnDepthDelta = functionFloat1("fnDepthDelta") {
        val uv = paramFloat2("uv")
        val refDepth = paramFloat1("refDepth")
        body {
            val texSz by viewZ.size().toFloat2()
            val uvi by (uv * texSz).toInt2()
            viewZ.load(uvi, lod = 0.const).x - refDepth
        }
    }

    val fnCastRay = functionFloat3("fnCastRay") {
        val origin = paramFloat3("origin")
        val rayDir = paramFloat3("rayDir")
        val noise = paramFloat3("noise")

        body {
            val baseDist by -origin.z
            val dError by 0f.const
            val stepUv by 0f.const2
            val isHit by false.const
            val step by baseDist * 0.025f.const + noise.x * 0.01f.const
            val prevStep by 0f.const
            val stepScale by 1f.const
            val maxIncrease by 4f.const
            val directionFac by abs(dot(rayDir, normalize(origin)))

            repeat(16.const) {
                val prevStepSize by abs(step - prevStep)
                val stepPos by origin + rayDir * step
                stepUv set fnProjViewPos(stepPos)
                dError set fnDepthDelta(stepUv, -stepPos.z) * stepScale
                `if`(abs(dError) lt step / 50f.const) {
                    isHit set true.const
                    `break`()
                }
                `if`((stepUv.x lt 0f.const) or (stepUv.x gt 1f.const) or (stepUv.y lt 0f.const) or (stepUv.y gt 1f.const)) {
                    `break`()
                }

                val nextStep by clamp(dError * (0.75f.const + noise.y * 0.5f.const), -prevStepSize * maxIncrease, prevStepSize * maxIncrease)
                val foregroundObjThresh by max(prevStepSize, baseDist * 0.05f.const / directionFac)
                `if`(-nextStep gt foregroundObjThresh) {
                    prevStep set step
                    step += prevStepSize
                }.elseIf(step + nextStep lt prevStep) {
                    stepScale *= 0.5f.const
                }.`else` {
                    prevStep set step
                    step += nextStep
                }
            }
            float3Value(stepUv, isHit.toFloat1())
        }
    }

    val specFactor by material.outSpecularFactor
    val roughFactor by material.inRoughness
    val envReflectionColor by material.outSpecular * specFactor * material.inAoFactor

    val viewPos by (camData.view * float4Value(material.inFragmentPos, 1f)).xyz
    val reflectionWeight by 0f.const
    val numRays by 0f.const
    val rayDir by reflect(normalize(viewPos), viewNormal)
    val noise by noise33(viewPos * (camData.frameIdx % 64.const + 1.const).toFloat1())

    val reflectionColorOut by 0f.const3
    val minColor by 1000f.const3
    val maxColor by 0f.const3
    val initialRays by clamp((roughFactor * length(specFactor) * 20f.const).toInt1(), 1.const, 4.const)
    repeat(initialRays) {
        numRays += 1f.const
        val scatterOffset by (noise - 0.5f.const) * roughFactor * 0.5f.const
        val scatteredRayDir by normalize(rayDir + scatterOffset)
        val rayResult by fnCastRay(viewPos, scatteredRayDir, noise)
        `if`(rayResult.z gt 0f.const) {
            val sampleColor by oldColor.sample(rayResult.xy).rgb * rayResult.z * specFactor
            reflectionColorOut += sampleColor
            reflectionWeight += rayResult.z
            minColor set min(minColor, sampleColor)
            maxColor set max(maxColor, sampleColor)
        }.`else` {
            reflectionColorOut += envReflectionColor
            reflectionWeight += 1f.const
            minColor set min(minColor, envReflectionColor)
            maxColor set max(maxColor, envReflectionColor)
        }
        noise set noise13(noise.x)
    }

    val thresh by length(maxColor - minColor)
    `while`((thresh gt 0.1f.const) and (numRays lt 6f.const)) {
        numRays += 1f.const
        val scatterOffset by (noise - 0.5f.const) * roughFactor * 0.5f.const
        val scatteredRayDir by normalize(rayDir + scatterOffset)
        val rayResult by fnCastRay(viewPos, scatteredRayDir, noise)
        `if`(rayResult.z gt 0f.const) {
            reflectionColorOut += oldColor.sample(rayResult.xy).rgb * rayResult.z * specFactor
            reflectionWeight += rayResult.z
            thresh -= 0.1f.const
        }.`else` {
            reflectionColorOut += envReflectionColor
            reflectionWeight += 1f.const
        }
        noise set noise13(noise.x)
    }

    reflectionColorOut set reflectionColorOut / reflectionWeight
    return reflectionColorOut
}
