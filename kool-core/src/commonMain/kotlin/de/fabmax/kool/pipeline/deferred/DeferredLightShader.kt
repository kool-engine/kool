package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.blocks.pbrLightBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Light

/**
 * 2nd pass shader for deferred pbr shading: Uses textures with view space position, normals, albedo, roughness,
 * metallic and texture-based AO and computes the final color output.
 */
class DeferredLightShader(encodedLightType: Float, model: Model = Model(encodedLightType)) :
    KslShader(
        model,
        PipelineConfig(
            blendMode = BlendMode.BLEND_ADDITIVE,
            cullMethod = CullMethod.CULL_FRONT_FACES,
            depthTest = DepthCompareOp.ALWAYS,
            isWriteDepth = false
        )
    )
{
    var positionFlags by texture2d("positionFlags")
    var normalRoughness by texture2d("normalRoughness")
    var colorMetallic by texture2d("colorMetallic")
    var emissiveAo by texture2d("emissiveAo")

    fun setMaterialInput(materialPass: MaterialPass) {
        createdPipeline?.swapPipelineData(materialPass)
        positionFlags = materialPass.positionFlags
        normalRoughness = materialPass.normalRoughness
        colorMetallic = materialPass.albedoMetal
        emissiveAo = materialPass.emissiveAo
    }

    class Model(encodedLightType: Float) : KslProgram("Defferred light shader") {
        init {
            val fragPos = interStageFloat4()

            val lightColor = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val lightPos = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val lightDir = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)

            val lightRadius = interStageFloat1(interpolation = KslInterStageInterpolation.Flat)

            vertexStage {
                main {
                    val instanceMvp = instanceAttribMat4(Attribute.INSTANCE_MODEL_MAT.name)
                    val mvp = mat4Var(mvpMatrix().matrix * instanceMvp)
                    outPosition set mvp * float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f.const)
                    fragPos.input set outPosition

                    lightRadius.input set length(instanceMvp * Vec4f.X_AXIS.const)

                    lightColor.input set instanceAttribFloat4(Attribute.COLORS.name)
                    lightPos.input set instanceAttribFloat4(LIGHT_POS.name)
                    if (encodedLightType != Light.Point.ENCODING) {
                        lightDir.input set instanceAttribFloat4(LIGHT_DIR.name)
                    } else {
                        lightDir.input set Vec4f.ZERO.const
                    }
                }
            }

            fragmentStage {
                main {
                    val uv = float2Var(fragPos.output.xy / fragPos.output.w * 0.5.const + 0.5.const)
                    if (KoolSystem.requireContext().backend.isInvertedNdcY) {
                        uv.y set 1f.const - uv.y
                    }

                    val posFlags = float4Var(sampleTexture(texture2d("positionFlags"), uv))
                    val normalRoughness = float4Var(sampleTexture(texture2d("normalRoughness"), uv))
                    val colorMetallic = float4Var(sampleTexture(texture2d("colorMetallic"), uv))
                    val emissiveAo = float4Var(sampleTexture(texture2d("emissiveAo"), uv))

                    val viewPos = posFlags.xyz
                    val viewNormal = normalRoughness.xyz
                    val roughness = normalRoughness.a
                    val color = colorMetallic.rgb
                    val metallic = colorMetallic.a
                    val ao = emissiveAo.a

                    // discard fragment if it contains background / clear color
                    `if`(viewPos.z gt 0f.const) {
                        discard()
                    }

                    // transform input positions from view space back to world space
                    val camData = deferredCameraData()
                    val worldPos = float3Var((camData.invViewMat * float4Value(viewPos, 1f.const)).xyz)
                    val worldNrm = float3Var((camData.invViewMat * float4Value(viewNormal, 0f.const)).xyz)

                    val viewDir = float3Var(normalize(camData.position - worldPos))
                    val f0 = mix(Vec3f(0.04f).const, color, metallic)

                    val lightBlock = pbrLightBlock(false) {
                        inViewDir(viewDir)
                        inNormalLight(worldNrm)
                        inFragmentPosLight(worldPos)
                        inBaseColorRgb(color)

                        inRoughnessLight(roughness)
                        inMetallicLight(metallic)
                        inF0(f0)

                        inEncodedLightPos(lightPos.output)
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

    companion object {
        val LIGHT_POS = Attribute("aLightPos", GpuType.Float4)
        val LIGHT_DIR = Attribute("aLightDir", GpuType.Float4)
    }
}