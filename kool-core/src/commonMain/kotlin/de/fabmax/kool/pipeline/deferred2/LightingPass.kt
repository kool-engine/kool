package de.fabmax.kool.pipeline.deferred2

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.NormalLightRange
import de.fabmax.kool.modules.ksl.ShadowMapConfig
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
import kotlin.math.abs

class LightingPass(
    size: Vec2i,
    private val pipeline: Deferred2Pipeline,
) : OffscreenPass2d(
    drawNode = Node(),
    attachmentConfig = AttachmentConfig {
        addColor(TexFormat.RG11B10_F, filterMethod = FilterMethod.NEAREST)   // metal, roughness, ao
        defaultDepth()
    },
    initialSize = size,
    name = "deferred2-lighting-pass"
) {
    val lightingOutput: Texture2d get() = colorTexture!!

    private val lightingShader = DeferredLightingShader(pipeline.maxGlobalLights, pipeline.shadowMapConfig)
    var numReflectionRays: Int = lightingShader.numReflectionRays
    var reflectionRayStepIncrease: Float = lightingShader.reflectionRayStepIncrease
    var ambientShadowFactor: Float = lightingShader.ambientShadowFactor

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
            for (i in gbuffer.alphaMeshes.indices) {
                val mesh = gbuffer.alphaMeshes[i]
                mesh.getOrCreatePipeline(ctx)?.let { pipeline ->
                    viewData.drawQueue.addMesh(mesh, pipeline)
                }
            }
        }
    }

    fun swapBuffers() {
        val newGbuffer = pipeline.gbuffers.newVal
        // swap pipeline data for next frame. copy bindings from previous data because it contains the updated
        // light space matrices for shadows
        lightingShader.swapPipelineData(newGbuffer, copyBindings = true) {
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
            numReflectionRays = this@LightingPass.numReflectionRays
            reflectionRayStepIncrease = this@LightingPass.reflectionRayStepIncrease
            ambientShadowFactor = this@LightingPass.ambientShadowFactor
        }
    }
}

class DeferredLightingShader(
    maxGlobalLights: Int,
    shadowMapConfig: List<ShadowMapConfig>,
) : KslShader("deferred2-lighting") {
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

    var numReflectionRays by bindUniformInt1("numReflectionRays")
    var reflectionRayStepIncrease by bindUniformFloat1("reflectionRayStepIncrease", 1.5f)
    var ambientShadowFactor by bindUniformFloat1("ambientShadowFactor", 0f)

    var oldColor by bindTexture2d("oldColor")

    var ambientMapOrientation: Mat3f by bindUniformMat3("uAmbientTextureOri", Mat3f.IDENTITY)

    init {
        pipelineConfig = PipelineConfig(
            blendMode = BlendMode.DISABLED,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = DepthCompareOp.ALWAYS
        )
        program.program(maxGlobalLights, shadowMapConfig)

        bindTexture1d("tgradient", GradientTexture(ColorGradient.ROCKET))
    }

    private fun KslProgram.program(
        maxGlobalLights: Int,
        shadowMapConfig: List<ShadowMapConfig>,
    ) {
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

            val numReflectionRays = uniformInt1("numReflectionRays")
            val reflectionRayStepIncrease = uniformFloat1("reflectionRayStepIncrease")
            val ambientShadowFactor = uniformFloat1("uAmbientShadowFactor")
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

                val lightData = sceneLightData(maxGlobalLights)
                val shadowData = shadowData(shadowMapConfig)
                val shadowFactors = float1Array(maxGlobalLights, 1f.const)
                val avgShadow = float1Var(0f.const)
                if (shadowData.numSubMaps > 0) {
                    val lightSpacePositions = List(shadowData.numSubMaps) { float4Var(Vec4f.ZERO.const) }
                    val lightSpaceNormalZs = List(shadowData.numSubMaps) { float1Var(0f.const) }

                    // transform positions to light space
                    shadowData.shadowMapInfos.forEach { mapInfo ->
                        mapInfo.subMaps.forEachIndexed { i, subMap ->
                            val subMapIdx = mapInfo.fromIndexIncl + i
                            val viewProj = shadowData.shadowMapViewProjMats[subMapIdx]
                            val normalLightSpace = float3Var(normalize((viewProj * float4Value(worldNormal, 0f.const)).xyz))
                            lightSpaceNormalZs[subMapIdx] set normalLightSpace.z
                            lightSpacePositions[subMapIdx] set viewProj * float4Value(worldPos, 1f.const)
                            lightSpacePositions[subMapIdx].xyz += normalLightSpace * abs(subMap.shaderDepthOffset).const
                        }
                    }
                    // adjust light strength values by shadow maps
                    fragmentShadowBlock(lightSpacePositions, lightSpaceNormalZs, shadowData, shadowFactors)
                    fori(0.const, lightData.lightCount) { i ->
                        avgShadow += shadowFactors[i]
                    }
                    avgShadow /= max(1f.const, lightData.lightCount.toFloat1())
                }
                ambient set ambient * (1f.const - (1f.const - avgShadow) * ambientShadowFactor)

                val normalLightRange = NormalLightRange.ZeroToOne
                val material = pbrMaterialBlock(maxGlobalLights, listOf(reflection), brdf, normalLightRange) {
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

                val outColor by material.outColor + albedo * emissiveStrength
                `if`(numReflectionRays gt 0.const) {
                    val oldColor = texture2d("oldColor")
                    val screenReflection by screenReflect(material, viewNormal, scaledViewZ, oldColor, camData, numReflectionRays, reflectionRayStepIncrease)
                    outColor set material.outAmbient + material.outLight + screenReflection + albedo * emissiveStrength
                }
                colorOutput(outColor)
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
    camData: KslStructStorage<DeferredCamDataLayout>,
    numReflectionRays: KslExprInt1,
    reflectionRayStepIncrease: KslExprFloat1,
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
        val maxIncrease = paramFloat1("maxIncrease")

        body {
            val baseDist by -origin.z
            val dError by 0f.const
            val stepUv by 0f.const2
            val isHit by false.const
            val step by baseDist * 0.025f.const + noise.x * 0.01f.const
            val prevStep by 0f.const
            val stepScale by 1f.const
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
                val foregroundObjThresh by max(prevStepSize * reflectionRayStepIncrease, baseDist * 0.05f.const) * 0.5f.const / directionFac
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
    val numRays by 0.const
    val rayDir by reflect(normalize(viewPos), viewNormal)
    val noise by noise33(viewPos * (camData.frameIdx % 64.const + 1.const).toFloat1())

    val ddx by Vec2f.X_AXIS.const
    val ddy by Vec2f.Y_AXIS.const
    val scatteringCoeff by 0.4f.const
    val reflectionColorOut by 0f.const3
    val minColor by 1000f.const3
    val maxColor by 0f.const3
    val initialRays by clamp((roughFactor * length(specFactor) * 20f.const).toInt1(), 1.const, numReflectionRays)
    val hit by false.const
    repeat(initialRays) {
        numRays += 1.const
        val scatterOffset by (noise - 0.5f.const) * roughFactor * scatteringCoeff
        val scatteredRayDir by normalize(rayDir + scatterOffset)
        val rayResult by fnCastRay(viewPos, scatteredRayDir, noise, reflectionRayStepIncrease)
        `if`(rayResult.z gt 0f.const) {
            val sampleColor by oldColor.sample(rayResult.xy, ddx, ddy).rgb * rayResult.z * specFactor
            reflectionColorOut += sampleColor
            reflectionWeight += rayResult.z
            minColor set min(minColor, sampleColor)
            maxColor set max(maxColor, sampleColor)
            hit set true.const
        }.`else` {
            reflectionColorOut += envReflectionColor
            reflectionWeight += 1f.const
            minColor set min(minColor, envReflectionColor)
            maxColor set max(maxColor, envReflectionColor)
        }
        noise set noise13(noise.x)
    }

    val thresh by length(maxColor - minColor)
    `while`((thresh gt 0.1f.const) and (numRays lt numReflectionRays * 2.const)) {
        numRays += 1.const
        val scatterOffset by (noise - 0.5f.const) * roughFactor * scatteringCoeff
        val scatteredRayDir by normalize(rayDir + scatterOffset)
        val rayResult by fnCastRay(viewPos, scatteredRayDir, noise, reflectionRayStepIncrease)
        `if`(rayResult.z gt 0f.const) {
            reflectionColorOut += oldColor.sample(rayResult.xy, ddx, ddy).rgb * rayResult.z * specFactor
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
