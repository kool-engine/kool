package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.LightDataStruct
import de.fabmax.kool.modules.ksl.blocks.getLightDirectionFromFragPos
import de.fabmax.kool.modules.ksl.blocks.getLightRadiance
import de.fabmax.kool.modules.ksl.blocks.setLightData
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.Time

class LightingPass(
    val gbuffers: AlternatingPair<GbufferPass>,
    camera: Camera,
    lighting: Lighting?,
    size: Vec2i,
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
    }

    fun swapBuffers() {
        val newGbuffer = gbuffers.newVal
        lightingShader.swapPipelineDataCapturing(newGbuffer) {
            depthTex = newGbuffer.depth
            encodedNormals = newGbuffer.encodedNormals
            albedoEmissionTex = newGbuffer.albedoEmission
            metalRoughnessAoTex = newGbuffer.metalRoughnessAo
            frameI = Time.frameCount

            camData.set {
                set(it.proj, camera.proj)
                set(it.invView, camera.invView)
                set(it.invViewProj, camera.invViewProj)
                set(it.viewport, Vec4f(0f, 0f, width.toFloat(), height.toFloat()))
                set(it.position, camera.globalPos)
                set(it.camNear, camera.clipNear)
            }
            lightData.set {
                setLightData(lighting, maxLightCount = 4, it)
            }
        }
    }
}

class DeferredLightingShader : KslShader("deferred2-lighting") {
    var depthTex by bindTexture2d("depth")
    var encodedNormals by bindTexture2d("encodedNormals")
    var albedoEmissionTex by bindTexture2d("albedoEmission")
    var metalRoughnessAoTex by bindTexture2d("metalRoughnessAo")

    val camData = bindUniformStruct("deferredCamData", DeferredCamDataStruct)
    private val lightDataStruct = LightDataStruct(4)
    val lightData = bindUniformStruct("lightData", lightDataStruct)

    var frameI by bindUniformInt1("frameI")

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
            val encodedNormals = texture2d("encodedNormals")
            val albedoEmission = texture2d("albedoEmission")
            val metalRoughnessAo = texture2d("metalRoughnessAo")

            val camData = uniformStruct("deferredCamData", DeferredCamDataStruct)
            val lightData = uniformStruct("lightData", lightDataStruct)

            val frameI = uniformInt1("frameI")

            main {
                val baseCoord = (uv.output * depth.size().toFloat2()).toInt2()
                val camNear = camData[DeferredCamDataStruct.camNear]
                val invView = camData[DeferredCamDataStruct.invView]
                val invViewProj = camData[DeferredCamDataStruct.invViewProj]
                val worldPos by unprojectBaseCoord(depth, baseCoord, camNear, invViewProj).xyz

                val viewNormal by decodeNormalRgb(encodedNormals.load(baseCoord, lod = 0.const).xyz)
                val worldNormal by (invView * float4Value(viewNormal, 0f.const)).xyz

                val albedoEmission = float4Var(albedoEmission.load(baseCoord, lod = 0.const))
                val albedo = albedoEmission.xyz
                val emission = albedoEmission.w

                val metalRoughnessAo by metalRoughnessAo.load(baseCoord, lod = 0.const).xyz
                val metallic = metalRoughnessAo.x
                val roughness = metalRoughnessAo.y
                val ao = metalRoughnessAo.z

                val lightPos by lightData[lightDataStruct.encodedPositions][0.const]
                val lightDir by lightData[lightDataStruct.encodedDirections][0.const]
                val lightColor by lightData[lightDataStruct.encodedColors][0.const]
                val dirToLight by normalize(getLightDirectionFromFragPos(worldPos, lightPos))
                val radiance by getLightRadiance(worldPos, lightPos, lightDir, lightColor)

                val ambient by 0.04f.const
                val diffuse by albedo * ambient + albedo * radiance * saturate(dot(dirToLight, worldNormal))
                colorOutput(diffuse)

//                val filterNoise by noise31(float3Value(uv.output, frameI.toFloat1())) * 0.7f.const + 0.3f.const
//                colorOutput(diffuse * filterNoise)
            }
        }
    }
}

object DeferredCamDataStruct : Struct("DeferredCameraData", MemoryLayout.Std140) {
    val proj = mat4("projMat")
//    val invProj = mat4("invProjMat")
    val invView = mat4("invView")
    val invViewProj = mat4("invViewProjMat")
    val viewport = float4("viewport")
    val position = float3("position")
    val camNear = float1("camClipNear")
}
