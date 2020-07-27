package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Cycler
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.ibl.EnvironmentMaps
import kotlin.math.max

class RoughnesMetalGridContent : PbrDemo.PbrContent("Roughness / Metal") {
    private val colors = Cycler(matColors)

    private val shaders = mutableListOf<PbrShader>()
    private var iblContent: Group? = null
    private var nonIblContent: Group? = null

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
                        shaders.forEach { it.albedo = colors.current.linColor }
                    }
                }
                +matLabel
                +button("color-left") {
                    layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                    layoutSpec.setSize(pcs(20f), dps(35f), full())
                    text = "<"

                    onClick += { _, _, _ ->
                        matLabel.text = colors.prev().name
                        shaders.forEach { it.albedo = colors.current.linColor }
                    }
                }
                +button("color-right") {
                    layoutSpec.setOrigin(pcs(80f), dps(y), zero())
                    layoutSpec.setSize(pcs(20f), dps(35f), full())
                    text = ">"

                    onClick += { _, _, _ ->
                        matLabel.text = colors.next().name
                        shaders.forEach { it.albedo = colors.current.linColor }
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

    override fun createContent(scene: Scene, envMaps: EnvironmentMaps, ctx: KoolContext): TransformGroup {
        content = transformGroup {
            isVisible = false

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
        iblContent?.children?.forEach {
            it as Mesh
            val pbrShader = it.shader as PbrShader
            pbrShader.irradianceMap = envMaps.irradianceMap
            pbrShader.reflectionMap = envMaps.reflectionMap
            pbrShader.brdfLut = envMaps.brdfLut
        }
    }

    private fun makeSpheres(withIbl: Boolean, envMaps: EnvironmentMaps) = group {
        val nRows = 7
        val nCols = 7
        val spacing = 2.5f

        for (y in 0 until nRows) {
            for (x in 0 until nCols) {
                +mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS)) {
                    generate {
                        uvSphere {
                            steps = 100
                            center.set((-(nCols - 1) * 0.5f + x) * spacing, (-(nRows - 1) * 0.5f + y) * spacing, 0f)
                            radius = 1f
                        }
                    }

                    val shader = pbrShader {
                        albedoSource = Albedo.STATIC_ALBEDO
                        albedo = colors.current.linColor
                        roughness = max(x / (nCols - 1).toFloat(), 0.05f)
                        metallic = y / (nRows - 1).toFloat()
                        if (withIbl) {
                            useImageBasedLighting(envMaps)
                        }
                    }
                    this.shader = shader
                    shaders += shader
                }
            }
        }
    }

    private data class MatColor(val name: String, val linColor: Color)

    companion object {
        private val matColors = listOf(
                MatColor("Red", Color.MD_RED.toLinear()),
                MatColor("Pink", Color.MD_PINK.toLinear()),
                MatColor("Purple", Color.MD_PURPLE.toLinear()),
                MatColor("Deep Purple", Color.MD_DEEP_PURPLE.toLinear()),
                MatColor("Indigo", Color.MD_INDIGO.toLinear()),
                MatColor("Blue", Color.MD_BLUE.toLinear()),
                MatColor("Cyan", Color.MD_CYAN.toLinear()),
                MatColor("Teal", Color.MD_TEAL.toLinear()),
                MatColor("Green", Color.MD_GREEN.toLinear()),
                MatColor("Light Green", Color.MD_LIGHT_GREEN.toLinear()),
                MatColor("Lime", Color.MD_LIME.toLinear()),
                MatColor("Yellow", Color.MD_YELLOW.toLinear()),
                MatColor("Amber", Color.MD_AMBER.toLinear()),
                MatColor("Orange", Color.MD_ORANGE.toLinear()),
                MatColor("Deep Orange", Color.MD_DEEP_ORANGE.toLinear()),
                MatColor("Brown", Color.MD_BROWN.toLinear()),
                MatColor("White", Color.WHITE.toLinear()),
                MatColor("Grey", Color.MD_GREY.toLinear()),
                MatColor("Blue Grey", Color.MD_BLUE_GREY.toLinear()),
                MatColor("Almost Black", Color(0.1f, 0.1f, 0.1f).toLinear())
        )
    }
}