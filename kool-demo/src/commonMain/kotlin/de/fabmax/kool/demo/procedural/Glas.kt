package de.fabmax.kool.demo.procedural

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.pipeline.deferred.DeferredPassSwapListener
import de.fabmax.kool.pipeline.deferred.DeferredPasses
import de.fabmax.kool.pipeline.deferred.deferredKslPbrShader
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap

class Glas(val ibl: EnvironmentMaps, shadowMap: SimpleShadowMap) : Node(), DeferredPassSwapListener {

    private val glasShader: GlassShader = GlassShader(ibl, shadowMap)

    init {
        makeWine()
        makeBody()
        makeShaft()

        transform.translate(7.5f, 0f, 2.5f)
        transform.scale(0.9f)
    }

    override fun onSwap(previousPasses: DeferredPasses, currentPasses: DeferredPasses) {
        glasShader.refractionColorMap = currentPasses.lightingPass.colorTexture
    }

    private fun makeBody() = addColorMesh {
        isOpaque = false
        generate {
            makeBodyGeometry()
            geometry.removeDegeneratedTriangles()
            geometry.generateNormals()
        }
        shader = KslPbrShader {
            color { vertexColor() }
            imageBasedAmbientColor(ibl.irradianceMap)
            reflectionMap = ibl.reflectionMap
            roughness(0f)
            alphaMode = AlphaMode.Blend
            reflectionStrength = Vec3f(0.3f, 0.3f, 0.3f)
        }
    }

    private fun makeShaft() = addMesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, THICKNESS) {
        generate {
            makeShaftGeometry()
            geometry.removeDegeneratedTriangles()
            geometry.generateNormals()
        }

        isOpaque = false
        shader = glasShader
    }

    private fun makeWine() = addColorMesh {
        generate {
            makeWineGeometry()
            geometry.removeDegeneratedTriangles()
            geometry.generateNormals()
        }

        shader = deferredKslPbrShader {
            roughness(0.0f)
            color {
                constColor(Color(0.3f, 0f, 0.1f).mix(Color.BLACK, 0.2f).toLinear())
            }
            emission {
                constColor(Color(0.3f, 0f, 0.1f).toLinear().withAlpha(0.8f))
            }
        }
    }

    private fun MeshBuilder.makeBodyGeometry() {
        rotate(90f.deg, Vec3f.NEG_X_AXIS)
        color = Color.BLACK.withAlpha(0.1f).toLinear()

        profile {
            circleShape()

            val bodyExtrude = listOf(
                    ExtrudeProps(1.3f, 9.1f, 1.5f),
                    ExtrudeProps(1.5f, 9.3f, 1f),
                    ExtrudeProps(3.97f, 11.8f, 1f),
                    ExtrudeProps(4.2f, 12.1f, 1f),
                    ExtrudeProps(4.45f, 12.6f, 1f),
                    ExtrudeProps(4.6f, 13.1f, 1f),
                    ExtrudeProps(4.65f, 13.5f, 1f),
                    ExtrudeProps(4.65f, 14.5f, 1f),
                    ExtrudeProps(4.58f, 15.5f, 1f),
                    ExtrudeProps(4.42f, 17f, 1f),
                    ExtrudeProps(4.0f, 20.0f, 1f),

                    ExtrudeProps(3.5f, 23.0f, 0.5f),
                    ExtrudeProps(3.475f, 23.05f, 0.5f),
                    ExtrudeProps(3.45f, 23.0f, 0.5f),

                    ExtrudeProps(3.95f, 20.0f, 0.5f),
                    ExtrudeProps(4.37f, 17f, 0.5f),
                    ExtrudeProps(4.6f, 14.5f, 0.5f),
                    ExtrudeProps(4.6f, 13.5f, 0.5f)
            ).reversed()
            bodyExtrude.forEach { ep ->
                withTransform {
                    scale(ep.r, ep.r, 1f)
                    translate(0f, 0f, ep.h)
                    sample(inverseOrientation = true)
                }
            }
        }
    }

    private fun MeshBuilder.makeShaftGeometry() {
        rotate(90f.deg, Vec3f.NEG_X_AXIS)
        color = Color.DARK_GRAY.withAlpha(0.1f).toLinear()

        profile {
            circleShape()

            var thickness = 0.5f
            vertexModFun = {
                getFloatAttribute(THICKNESS)?.f = thickness
            }

            withTransform {
                scale(4f, 4f, 1f)
                sampleAndFillBottom()
            }

            val shaftExtrude = listOf(
                    ExtrudeProps(4.1f, 0.1f, 0.5f),
                    ExtrudeProps(4.0f, 0.2f, 0.5f),
                    ExtrudeProps(2.0f, 0.5f, 0.75f),
                    ExtrudeProps(1.5f, 0.65f, 1f),
                    ExtrudeProps(1.0f, 0.95f, 1.5f),
                    ExtrudeProps(0.75f, 1.25f, 2f),
                    ExtrudeProps(0.5f, 2.0f, 5f),
                    ExtrudeProps(0.4f, 3.0f, 20f),
                    ExtrudeProps(0.4f, 7.0f, 20f),
                    ExtrudeProps(0.5f, 8.0f, 20f),
                    ExtrudeProps(0.6f, 8.25f, 2.5f),
                    ExtrudeProps(0.9f, 8.65f, 1f),
                    ExtrudeProps(1.3f, 9.1f, 1f)
            )
            shaftExtrude.forEach { ep ->
                withTransform {
                    scale(ep.r, ep.r, 1f)
                    translate(0f, 0f, ep.h)
                    thickness = ep.t
                    sample()
                }
            }
        }
    }

    private fun MeshBuilder.makeWineGeometry() {
        rotate(90f.deg, Vec3f.NEG_X_AXIS)

        profile {
            circleShape()

            withTransform {
                translate(0f, 0f, 10f)
                sampleAndFillBottom()
            }
            val wineExtrude = listOf(
                    ExtrudeProps(1.28f, 9.1f, 1.5f),
                    ExtrudeProps(1.48f, 9.3f, 3f),
                    ExtrudeProps(3.95f, 11.8f, 7f),
                    ExtrudeProps(4.18f, 12.1f, 7f),
                    ExtrudeProps(4.43f, 12.6f, 7f),
                    ExtrudeProps(4.58f, 13.1f, 7f),
                    ExtrudeProps(4.63f, 13.5f, 7f),
                    ExtrudeProps(4.63f, 13.5f, 7f),
                    ExtrudeProps(4.57f, 13.4f, 7f),
                    ExtrudeProps(3.0f, 13.4f, 7f),
                    ExtrudeProps(1.5f, 13.4f, 7f),
                    ExtrudeProps(0.5f, 13.4f, 7f)
            )
            wineExtrude.forEach { ep ->
                withTransform {
                    scale(ep.r, ep.r, 1f)
                    translate(0f, 0f, ep.h)
                    sample()
                }
            }
            fillTop()
        }
    }

    private class GlassShader(ibl: EnvironmentMaps, shadowMap: SimpleShadowMap, cfg: Config = glassShaderConfig(ibl, shadowMap))
        : KslPbrShader(cfg, glassShaderModel(cfg))
    {
        var refractionColorMap by texture2d("tRefractionColor")

        companion object {
            fun glassShaderConfig(ibl: EnvironmentMaps, shadowMap: SimpleShadowMap) = Config.Builder().apply {
                color { vertexColor() }
                shadow { addShadowMap(shadowMap) }
                roughness(0f)
                enableImageBasedLighting(ibl)
            }.build()

            fun glassShaderModel(cfg: Config) = Model(cfg).apply {
                val matThickness = interStageFloat1()

                vertexStage {
                    main {
                        matThickness.input set vertexAttribFloat1(THICKNESS.name)
                    }
                }

                fragmentStage {
                    val camData = cameraData()
                    val refractionColorMap = texture2d("tRefractionColor")

                    main {
                        val materialColorPort = getFloat4Port("materialColor")
                        val materialColorInput = float4Var(materialColorPort.input.input)

                        val normal = getFloat3Port("normal")
                        val worldPos = getFloat3Port("worldPos")
                        val viewDir = float3Var(normalize(camData.position - worldPos))

                        val refractionIor = 1.4f
                        val refractionDir = float3Var(refract(viewDir, normalize(normal), (1f / refractionIor).const))
                        val refractionPos = float3Var(worldPos + refractionDir * matThickness.output)
                        val clipPos = float4Var(camData.viewProjMat * float4Value(refractionPos, 1f.const))
                        val samplePos = float2Var(clipPos.xy / clipPos.w * 0.5f.const + 0.5f.const)

                        val refractionColor = float4Var(Vec4f.ZERO.const)
                        `if`((samplePos.x gt 0f.const) and (samplePos.x lt 1f.const) and
                                (samplePos.y gt 0f.const) and (samplePos.y lt 1f.const)) {
//                            if (KoolSystem.requireContext().backend.isInvertedNdcY) {
//                                samplePos.y set 1f.const - samplePos.y
//                            }
                            refractionColor set sampleTexture(refractionColorMap, samplePos, 0f.const)
                        }
                        `if`(refractionColor.a eq 0f.const) {
                            // refraction sample pos out of screen bounds -> use first reflection map instead
                            refractionColor set sampleTexture(textureCube("tReflectionMap_0"), refractionDir, 0f.const)
                        }

                        val weight = 1f.const - materialColorInput.a
                        val resultColor = float3Var(materialColorInput.rgb + refractionColor.rgb * weight)
                        materialColorPort.input(float4Value(resultColor, 1f.const))
                    }
                }
            }
        }
    }

    private class ExtrudeProps(val r: Float, val h: Float, val t: Float)

    companion object {
        private val THICKNESS = Attribute("aMatThickness", GpuType.FLOAT1)
    }
}