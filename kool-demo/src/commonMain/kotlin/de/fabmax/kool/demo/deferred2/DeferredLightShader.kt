package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.NormalLightRange
import de.fabmax.kool.modules.ksl.blocks.modelMatrix
import de.fabmax.kool.modules.ksl.blocks.pbrLightBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.InstanceLayouts
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.scene.instanceAttrib
import de.fabmax.kool.scene.vertexAttrib

class DeferredLightShader : KslShader("deferred-light-shader") {
    var depth by bindTexture2d("depth")
    var normals by bindTexture2d("normals")
    var albedoEmission by bindTexture2d("albedoEmission")
    var metalRoughAo by bindTexture2d("metalRoughAo")
    var camData by bindStorage("camData")

    init {
        pipelineConfig = PipelineConfig(
            blendMode = BlendMode.BLEND_ADDITIVE,
            cullMethod = CullMethod.CULL_FRONT_FACES,
            depthTest = DepthCompareOp.ALWAYS,
            isWriteDepth = false
        )
        program.program()
    }

    private fun KslProgram.program() {
        val projPos = interStageFloat4()

        val lightPosType = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
        val lightDir = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
        val lightColor = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
        val lightRadius = interStageFloat1(interpolation = KslInterStageInterpolation.Flat)

        val camDataLayout = struct(DeferredCamDataLayout)
        val camData = storage("camData", camDataLayout)

        vertexStage {
            main {
                val model by modelMatrix().matrix * instanceAttrib(InstanceLayouts.ModelMat.modelMat)
                val mvp by camData.viewProj * model
                outPosition set mvp * float4Value(vertexAttrib(VertexLayouts.Position.position), 1f.const)
                projPos.input set outPosition

                val lightType by instanceAttrib(DeferredLightInstanceLayout.lightType)
                val lightPos by (model * float4Value(0f, 0f, 0f, 1f)).xyz
                val spotAngle by instanceAttrib(DeferredLightInstanceLayout.encodedSpotAngle)
                val dir by normalize((model * float4Value(1f, 0f, 0f, 0f)).xyz)

                lightRadius.input set length(model * Vec4f.X_AXIS.const)
                lightColor.input set instanceAttrib(DeferredLightInstanceLayout.lightColor)
                lightPosType.input set float4Value(lightPos, lightType)
                lightDir.input set float4Value(dir, spotAngle)
            }
        }

        fragmentStage {
            val depth = texture2d("depth", isUnfilterable = true)
            val metalRoughAo = texture2d("metalRoughAo")
            val normals = texture2dInt("normals")
            val albedoEmission = texture2d("albedoEmission")

            main {
                val uv = float2Var(projPos.output.xy / projPos.output.w * 0.5.const + 0.5.const)
                if (KoolSystem.requireContext().backend.isInvertedNdcY) {
                    uv.y set 1f.const - uv.y
                }
                val size by depth.size()
                val baseCoord by (uv * size.toFloat2()).toInt2()
                val camNear by camData.camNear
                val invViewProj by camData.invViewProj
                val depthSample by depth.load(baseCoord, lod = 0.const).x
                val worldPos by unprojectBaseCoord(depthSample, baseCoord, size, camNear, invViewProj).xyz
                val lightOrigin by lightPosType.output.xyz
                val lightToFrag by lightOrigin - worldPos
                val lightDist by length(lightToFrag)
                val dotDir by dot(lightDir.output.xyz, -lightToFrag / lightDist)
                val spotAngle by lightDir.output.w

                `if`((lightDist gt lightRadius.output) or (dotDir lt spotAngle)) {
                    discard()
                }

                val encodedNormal by normals.load(baseCoord).x
                val viewNormal by decodeNormalInt(encodedNormal)
                val baseColor by albedoEmission.load(baseCoord).rgb
                val mra by metalRoughAo.load(baseCoord).xyz
                val (metallic, roughness, ao) = mra
                val worldNrm by (camData.invView * float4Value(viewNormal, 0f.const)).xyz
                val viewDir by normalize(camData.camPosition - worldPos)
                val f0 = mix(0.04f.const3, baseColor, metallic)
                val lightBlock = pbrLightBlock(false, normalLightRange = NormalLightRange.ZeroToOne) {
                    inViewDir(viewDir)
                    inNormalLight(worldNrm)
                    inFragmentPosLight(worldPos)
                    inBaseColorRgb(baseColor)

                    inRoughnessLight(roughness)
                    inMetallicLight(metallic)
                    inF0(f0)

                    inEncodedLightPos(lightPosType.output)
                    inEncodedLightDir(lightDir.output)
                    inEncodedLightColor(lightColor.output)
                    inLightRadius(lightRadius.output)
                    inLightStr(1f.const)
                    inShadowFac(ao)
                }
                colorOutput(lightBlock.outRadiance)
            }
        }
    }
}