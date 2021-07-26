package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.shadermodel.PbrMaterialNode
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MeshInstanceList
import de.fabmax.kool.util.ibl.EnvironmentMaps

class ColorGridContent(val sphereProto: PbrDemo.SphereProto) : PbrDemo.PbrContent("Color Grid") {
    private val shaders = mutableListOf<PbrShader>()
    private var iblContent: Mesh? = null
    private var nonIblContent: Mesh? = null

    override fun createMenu(parent: UiContainer, smallFont: Font, yPos: Float) {
        parent.root.apply {
            ui = container("pbr-color-container") {
                isVisible = false
                layoutSpec.setOrigin(pcs(0f), dps(yPos - 100f), zero())
                layoutSpec.setSize(pcs(100f), dps(100f), full())

                // material map selection
                var y = -30f
                +label("mat-lbl") {
                    layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                    layoutSpec.setSize(pcs(100f), dps(30f), full())
                    font.setCustom(smallFont)
                    text = "Material"
                    textColor.setCustom(theme.accentColor)
                    textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
                }
                y -= 35f
                +label("mat-roughness-lbl") {
                    layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                    layoutSpec.setSize(pcs(10f), dps(35f), full())
                    text = "R:"
                }
                +slider("mat-roughness-slider") {
                    layoutSpec.setOrigin(pcs(15f), dps(y), zero())
                    layoutSpec.setSize(pcs(85f), dps(35f), full())
                    value = 10f

                    onValueChanged += {
                        shaders.forEach { it.roughness(value / 100f) }
                    }
                }
                y -= 35f
                +label("mat-metallic-lbl") {
                    layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                    layoutSpec.setSize(pcs(10f), dps(35f), full())
                    text = "M:"
                }
                +slider("mat-metallic-slider") {
                    layoutSpec.setOrigin(pcs(15f), dps(y), zero())
                    layoutSpec.setSize(pcs(85f), dps(35f), full())
                    value = 0f

                    onValueChanged += {
                        shaders.forEach { it.metallic(value / 100f) }
                    }
                }
            }
            parent += ui!!
        }
    }

    override fun setUseImageBasedLighting(enabled: Boolean) {
        iblContent?.isVisible = enabled
        nonIblContent?.isVisible = !enabled
    }

    override fun createContent(scene: Scene, envMaps: EnvironmentMaps, ctx: KoolContext): Group {
        content = group {
            isVisible = false
            isFrustumChecked = false

            val ibl = makeSpheres(true, envMaps)
            val nonIbl = makeSpheres(false, envMaps).apply { isVisible = false }

            +ibl
            +nonIbl

            iblContent = ibl
            nonIblContent = nonIbl
        }
        return content!!
    }

    override fun updateEnvironmentMap(envMaps: EnvironmentMaps) {
        iblContent?.let {
            val pbrShader = it.shader as PbrShader
            pbrShader.irradianceMap(envMaps.irradianceMap)
            pbrShader.reflectionMap(envMaps.reflectionMap)
            pbrShader.brdfLut(envMaps.brdfLut)
        }
    }

    private fun makeSpheres(withIbl: Boolean, environmentMaps: EnvironmentMaps): Mesh {
        val nRows = 4
        val nCols = 5
        val spacing = 4.5f

        val colors = mutableListOf<Color>()
        colors += MdColor.PALETTE
        colors.remove(MdColor.LIGHT_BLUE)
        colors.remove(MdColor.GREY)
        colors.remove(MdColor.BLUE_GREY)
        colors += Color.WHITE
        colors += MdColor.GREY
        colors += MdColor.BLUE_GREY
        colors += Color(0.1f, 0.1f, 0.1f)

        return mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS)) {
            isFrustumChecked = false
            geometry.addGeometry(sphereProto.simpleSphere)
            shader = instancedPbrShader(withIbl, environmentMaps).also { shaders += it }

            instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS), nRows * nCols) .apply {
                val mat = Mat4f()
                for (y in 0 until nRows) {
                    for (x in 0 until nCols) {
                        mat.setIdentity()
                        mat.translate((-(nCols - 1) * 0.5f + x) * spacing, ((nRows - 1) * 0.5f - y) * spacing, 0f)
                        mat.scale(1.5f)

                        addInstance {
                            put(mat.matrix)
                            put(colors[(y * nCols + x) % colors.size].toLinear().array)
                        }
                    }
                }
            }
        }
    }

    private fun instancedPbrShader(withIbl: Boolean, envMaps: EnvironmentMaps): PbrShader {
        val pbrCfg = PbrMaterialConfig().apply {
            albedoSource = Albedo.STATIC_ALBEDO
            roughness = 0.1f
            metallic = 0f
            isInstanced = true
            if (withIbl) {
                useImageBasedLighting(envMaps)
            }
        }

        // use default PBR shader model and replace color input by instance attribute
        val model = PbrShader.defaultPbrModel(pbrCfg).apply {
            val ifInstColor: StageInterfaceNode
            vertexStage {
                ifInstColor = stageInterfaceNode("ifInstColors", instanceAttributeNode(Attribute.COLORS).output)
            }
            fragmentStage {
                val material = findNode<PbrMaterialNode>("pbrMaterial")!!
                material.inAlbedo = ifInstColor.output
            }
        }
        return PbrShader(pbrCfg, model)
    }
}