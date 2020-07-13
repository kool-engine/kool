package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font

class ColorGridContent : PbrDemo.PbrContent("Color Grid") {
    private val shaders = mutableListOf<PbrShader>()
    private var iblContent: Group? = null
    private var nonIblContent: Group? = null

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
                        shaders.forEach { it.roughness = value / 100f }
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
                        shaders.forEach { it.metallic = value / 100f }
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

    override fun createContent(scene: Scene, irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture, ctx: KoolContext): TransformGroup {
        content = transformGroup {
            isVisible = false

            val ibl = makeSpheres(true, irradianceMap, reflectionMap, brdfLut)
            val nonIbl = makeSpheres(false, irradianceMap, reflectionMap, brdfLut).apply { isVisible = false }

            +ibl
            +nonIbl

            iblContent = ibl
            nonIblContent = nonIbl
        }
        return content!!
    }

    private fun makeSpheres(withIbl: Boolean, irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture) = group {
        val nRows = 4
        val nCols = 5
        val spacing = 4.5f

        val colors = mutableListOf<Color>()
        colors += Color.MD_COLORS
        colors.remove(Color.MD_LIGHT_BLUE)
        colors.remove(Color.MD_GREY)
        colors.remove(Color.MD_BLUE_GREY)
        colors += Color.WHITE
        colors += Color.MD_GREY
        colors += Color.MD_BLUE_GREY
        colors += Color(0.1f, 0.1f, 0.1f)

        for (y in 0 until nRows) {
            for (x in 0 until nCols) {
                +mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS)) {
                    generate {
                        uvSphere {
                            steps = 100
                            center.set((-(nCols - 1) * 0.5f + x) * spacing, ((nRows - 1) * 0.5f - y) * spacing, 0f)
                            radius = 1.5f
                        }
                    }

                    val shader = pbrShader {
                        albedoSource = Albedo.STATIC_ALBEDO
                        albedo = colors[(y * nCols + x) % colors.size].toLinear()
                        roughness = 0.1f
                        metallic = 0f
                        if (withIbl) {
                            useImageBasedLighting(irradianceMap, reflectionMap, brdfLut)
                        }
                    }
                    this.shader = shader
                    shaders += shader
                }
            }
        }
    }
}