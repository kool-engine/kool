package de.fabmax.kool.demo.procedural

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.shadermodel.RefractionSamplerNode
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.AlphaModeBlend
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.deferred.DeferredPassSwapListener
import de.fabmax.kool.util.deferred.DeferredPasses
import de.fabmax.kool.util.deferred.deferredPbrShader
import de.fabmax.kool.util.ibl.EnvironmentMaps

class Glas(val ibl: EnvironmentMaps) : Group(), DeferredPassSwapListener {

    private val glasShader: PbrShader

    init {
        glasShader = glasShader()

        +makeWine()
        +makeBody()
        +makeShaft()

        translate(7.5f, 0f, 2.5f)
        scale(0.9f)
    }

    override fun onSwap(previousPasses: DeferredPasses, currentPasses: DeferredPasses) {
        glasShader.refractionColorMap(currentPasses.lightingPass.colorTexture)
    }

    private fun makeBody() = colorMesh {
        isOpaque = false
        generate {
            makeBodyGeometry()
            geometry.removeDegeneratedTriangles()
            geometry.generateNormals()
        }
        shader = pbrShader {
            useImageBasedLighting(ibl)
            roughness = 0f
            alphaMode = AlphaModeBlend()
            reflectionStrength = 0.3f
        }
    }

    private fun makeShaft() = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, THICKNESS)) {
        generate {
            makeShaftGeometry()
            geometry.removeDegeneratedTriangles()
            geometry.generateNormals()
        }

        isOpaque = false
        shader = glasShader
    }

    private fun makeWine() = colorMesh {
        generate {
            makeWineGeometry()
            geometry.removeDegeneratedTriangles()
            geometry.generateNormals()
        }

        shader = deferredPbrShader {
            roughness = 0.0f
            emissive = Color(0.3f, 0f, 0.1f).toLinear().withAlpha(0.8f)
            useStaticAlbedo(Color(0.3f, 0f, 0.1f).mix(Color.BLACK, 0.2f).toLinear())
        }
    }

    private fun MeshBuilder.makeBodyGeometry() {
        rotate(90f, Vec3f.NEG_X_AXIS)
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
        rotate(90f, Vec3f.NEG_X_AXIS)
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
        rotate(90f, Vec3f.NEG_X_AXIS)

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

    private fun glasShader(): PbrShader {
        val glasCfg = PbrMaterialConfig().apply {
            useImageBasedLighting(ibl)
            roughness = 0f
            alphaMode = AlphaModeBlend()
            isRefraction = true
        }
        val glasModel = PbrShader.defaultPbrModel(glasCfg).apply {
            val ifThickness: StageInterfaceNode
            vertexStage {
                ifThickness = stageInterfaceNode("ifThickness", attributeNode(THICKNESS).output)
            }
            fragmentStage {
                val refrSampler = findNodeByType<RefractionSamplerNode>()
                refrSampler?.inMaterialThickness = ifThickness.output
            }
        }
        return PbrShader(glasCfg, glasModel)
    }

    private class ExtrudeProps(val r: Float, val h: Float, val t: Float)

    companion object {
        private val THICKNESS = Attribute("aMatThickness", GlslType.FLOAT)
    }
}