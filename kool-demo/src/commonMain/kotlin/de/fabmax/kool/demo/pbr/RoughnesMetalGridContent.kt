package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Cycler
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
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
import kotlin.math.max

class RoughnesMetalGridContent(val sphereProto: PbrDemo.SphereProto) : PbrDemo.PbrContent("Roughness / Metal") {
    private val colors = Cycler(matColors)

    private val shaders = mutableListOf<PbrShader>()
    private var iblContent: Mesh? = null
    private var nonIblContent: Mesh? = null

    override fun createMenu(parent: UiContainer, smallFont: Font, yPos: Float) {
        parent.root.apply {
            ui = container("pbr-rough-metal-container") {
                isVisible = false
                layoutSpec.setOrigin(pcs(0f), dps(yPos - 60f), zero())
                layoutSpec.setSize(pcs(100f), dps(60f), full())

                // material map selection
                var y = -30f
                +label("color-lbl") {
                    layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                    layoutSpec.setSize(pcs(100f), dps(30f), full())
                    font.setCustom(smallFont)
                    text = "Color"
                    textColor.setCustom(theme.accentColor)
                    textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
                }
                y -= 30f
                val matLabel = button("selected-color") {
                    layoutSpec.setOrigin(pcs(15f), dps(y), zero())
                    layoutSpec.setSize(pcs(70f), dps(35f), full())
                    text = colors.current.name

                    onClick += { _, _, _ ->
                        text = colors.next().name
                        shaders.forEach { it.albedo(colors.current.linColor) }
                    }
                }
                +matLabel
                +button("color-left") {
                    layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                    layoutSpec.setSize(pcs(20f), dps(35f), full())
                    text = "<"

                    onClick += { _, _, _ ->
                        matLabel.text = colors.prev().name
                        shaders.forEach { it.albedo(colors.current.linColor) }
                    }
                }
                +button("color-right") {
                    layoutSpec.setOrigin(pcs(80f), dps(y), zero())
                    layoutSpec.setSize(pcs(20f), dps(35f), full())
                    text = ">"

                    onClick += { _, _, _ ->
                        matLabel.text = colors.next().name
                        shaders.forEach { it.albedo(colors.current.linColor) }
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

    private fun makeSpheres(withIbl: Boolean, envMaps: EnvironmentMaps): Mesh {
        val nRows = 7
        val nCols = 7
        val spacing = 2.5f

        return mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS)) {
            isFrustumChecked = false
            geometry.addGeometry(sphereProto.simpleSphere)
            shader = instancedPbrShader(withIbl, envMaps).also { shaders += it }

            instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, Attribute.COLORS), nRows * nCols) .apply {
                val mat = Mat4f()
                for (y in 0 until nRows) {
                    for (x in 0 until nCols) {
                        mat.setIdentity()
                        mat.translate((-(nCols - 1) * 0.5f + x) * spacing, ((nRows - 1) * 0.5f - y) * spacing, 0f)

                        addInstance {
                            put(mat.matrix)
                            val roughness = max(x / (nCols - 1).toFloat(), 0.05f)
                            val metallic = y / (nRows - 1).toFloat()
                            put(roughness)
                            put(metallic)
                            put(0f)
                            put(0f)
                        }
                    }
                }
            }
        }
    }

    private fun instancedPbrShader(withIbl: Boolean, envMaps: EnvironmentMaps): PbrShader {
        val pbrCfg = PbrMaterialConfig().apply {
            albedoSource = Albedo.STATIC_ALBEDO
            albedo = colors.current.linColor
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
                material.inRoughness = splitNode(ifInstColor.output, "r").output
                material.inMetallic = splitNode(ifInstColor.output, "g").output
            }
        }
        return PbrShader(pbrCfg, model)
    }

    private data class MatColor(val name: String, val linColor: Color)

    companion object {
        private val matColors = listOf(
                MatColor("Red", MdColor.RED.toLinear()),
                MatColor("Pink", MdColor.PINK.toLinear()),
                MatColor("Purple", MdColor.PURPLE.toLinear()),
                MatColor("Deep Purple", MdColor.DEEP_PURPLE.toLinear()),
                MatColor("Indigo", MdColor.INDIGO.toLinear()),
                MatColor("Blue", MdColor.BLUE.toLinear()),
                MatColor("Cyan", MdColor.CYAN.toLinear()),
                MatColor("Teal", MdColor.TEAL.toLinear()),
                MatColor("Green", MdColor.GREEN.toLinear()),
                MatColor("Light Green", MdColor.LIGHT_GREEN.toLinear()),
                MatColor("Lime", MdColor.LIME.toLinear()),
                MatColor("Yellow", MdColor.YELLOW.toLinear()),
                MatColor("Amber", MdColor.AMBER.toLinear()),
                MatColor("Orange", MdColor.ORANGE.toLinear()),
                MatColor("Deep Orange", MdColor.DEEP_ORANGE.toLinear()),
                MatColor("Brown", MdColor.BROWN.toLinear()),
                MatColor("White", Color.WHITE.toLinear()),
                MatColor("Grey", MdColor.GREY.toLinear()),
                MatColor("Blue Grey", MdColor.BLUE_GREY.toLinear()),
                MatColor("Almost Black", Color(0.1f, 0.1f, 0.1f).toLinear())
        )
    }
}