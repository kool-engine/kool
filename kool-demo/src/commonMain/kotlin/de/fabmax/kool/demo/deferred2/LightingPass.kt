package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.NormalLightRange
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.pbrMaterialBlock
import de.fabmax.kool.modules.ksl.blocks.sceneLightData
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

class LightingPass(
    val gbuffers: AlternatingPair<GbufferPass>,
    camera: Camera,
    lighting: Lighting?,
    size: Vec2i,
    var ssaoMap: Texture2d,
    private val ibl: EnvironmentMap,
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

    private val lightingShader = DeferredLightingShader()

    init {
        this.camera = camera
        this.lighting = lighting

        val outputMesh = Mesh(VertexLayouts.PositionTexCoord).apply {
            generateFullscreenQuad()
            shader = lightingShader
        }
        drawNode.addNode(outputMesh)
        drawNode.addNode(Skybox.cube(ibl.reflectionMap, 2f, colorSpaceConversion = ColorSpaceConversion.AsIs))
    }

    fun swapBuffers() {
        val newGbuffer = gbuffers.newVal
        lightingShader.swapPipelineDataCapturing(newGbuffer) {
            depthTex = newGbuffer.depth
            encodedNormals = newGbuffer.normals
            albedoEmissionTex = newGbuffer.albedoEmission
            metalRoughnessAoTex = newGbuffer.metalRoughnessAo
            irradianceMap = ibl.irradianceMap
            reflectionMap = ibl.reflectionMap
            aoMap = ssaoMap

            camData.set {
                set(it.proj, camera.proj)
                set(it.invView, camera.invView)
                set(it.invViewProj, camera.invViewProj)
                set(it.viewport, Vec4f(0f, 0f, width.toFloat(), height.toFloat()))
                set(it.position, camera.globalPos)
                set(it.camNear, camera.clipNear)
            }
        }
    }
}

class DeferredLightingShader : KslShader("deferred2-lighting") {
    var depthTex by bindTexture2d("depth")
    var encodedNormals by bindTexture2d("encodedNormals")
    var albedoEmissionTex by bindTexture2d("albedoEmission")
    var metalRoughnessAoTex by bindTexture2d("metalRoughnessAo")
    var irradianceMap by bindTextureCube("irradiance")
    var reflectionMap by bindTextureCube("reflection")
    var brdf by bindTexture2d("brdf", KoolSystem.requireContext().defaultPbrBrdfLut)
    var aoMap by bindTexture2d("aoMap")

    val camData = bindUniformStruct("deferredCamData", DeferredCamDataStruct)
    var ambientMapOrientation: Mat3f by bindUniformMat3("uAmbientTextureOri", Mat3f.IDENTITY)

    init {
        pipelineConfig = PipelineConfig(
            blendMode = BlendMode.DISABLED,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = DepthCompareOp.ALWAYS
        )
        program.program()
    }

    private fun KslProgram.program() {
        val uv = interStageFloat2()
        fullscreenQuadVertexStage(uv)
        fragmentStage {
            val depth = texture2d("depth", isUnfilterable = true)
            val encodedNormals = texture2dInt("encodedNormals")
            val albedoEmission = texture2d("albedoEmission")
            val metalRoughnessAo = texture2d("metalRoughnessAo")
            val reflection = textureCube("reflection")
            val irradiance = textureCube("irradiance")
            val brdf = texture2d("brdf")
            val aoMap = texture2d("aoMap")

            val ambientOri = uniformMat3("uAmbientTextureOri")
            val camData = uniformStruct("deferredCamData", DeferredCamDataStruct)

            main {
                val baseCoord by (uv.output * depth.size().toFloat2()).toInt2()
                val depthSample by depth.load(baseCoord, lod = 0.const).x
                `if` (depthSample eq 0f.const) {
                    discard()
                }

                val camNear by camData[DeferredCamDataStruct.camNear]
                val camPos by camData[DeferredCamDataStruct.position]
                val invView by camData[DeferredCamDataStruct.invView]
                val invViewProj by camData[DeferredCamDataStruct.invViewProj]
                val size by depth.size()
                val worldPos by unprojectBaseCoord(depthSample, baseCoord, size, camNear, invViewProj).xyz
                val ssao by aoMap.load(baseCoord, lod = 0.const).x

                val encodedNormal by encodedNormals.load(baseCoord, lod = 0.const).x
                val viewNormal by decodeNormalInt(encodedNormal)
                val worldNormal by normalize((invView * float4Value(viewNormal, 0f.const)).xyz)

                val albedoEmission by float4Var(albedoEmission.load(baseCoord, lod = 0.const))
                val albedo by albedoEmission.xyz
                val emission by albedoEmission.w

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

                    inReflectionMapWeights(float2Value(1f, 0f))
                    inReflectionStrength(1f.const3)

                    setLightData(lightData, shadowFactors, 1f.const)
                }
                colorOutput(material.outColor)
                outDepth set depthSample
//                colorOutput(viewNormal * 0.5f.const + 0.5f.const)
//                colorOutput(float3Value(ssao, ssao, ssao))
            }
        }
    }
}

object DeferredCamDataStruct : Struct("DeferredCameraData", MemoryLayout.Std140) {
    val proj = mat4("projMat")
    val invView = mat4("invView")
    val invViewProj = mat4("invViewProjMat")
    val viewport = float4("viewport")
    val position = float3("position")
    val camNear = float1("camClipNear")
}
