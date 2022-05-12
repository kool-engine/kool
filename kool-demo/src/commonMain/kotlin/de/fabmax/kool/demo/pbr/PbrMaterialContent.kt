package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Cycler
import de.fabmax.kool.demo.Demo
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Color

class PbrMaterialContent(val sphereProto: PbrDemo.SphereProto) : PbrDemo.PbrContent("PBR Material") {
    val matCycler = Cycler(materials).apply { index = 3 }
    val currentMat: MaterialMaps
        get() = matCycler.current

    private val shaders = mutableListOf<PbrShader>()
    private var iblContent: Group? = null
    private var nonIblContent: Group? = null

    private fun nextMaterial(): MaterialMaps {
        currentMat.disposeMaps()
        matCycler.next()
        updatePbrMaterial()
        return currentMat
    }

    private fun prevMaterial(): MaterialMaps {
        currentMat.disposeMaps()
        matCycler.prev()
        updatePbrMaterial()
        return currentMat
    }

    private fun updatePbrMaterial() {
        shaders.forEach {
            it.albedoMap(currentMat.albedo)
            it.normalMap(currentMat.normal)
            it.roughnessMap(currentMat.roughness)
            it.metallicMap(currentMat.metallic ?: defaultMetallicTex)
            it.aoMap(currentMat.ao ?: defaultAoTex)
            it.displacementMap(currentMat.displacement ?: defaultDispTex)
        }
    }

    override fun createMenu(parent: UiContainer, smallFont: Font, yPos: Float) {
        parent.root.apply {
            ui = container("pbr-mat-container") {
                isVisible = false
                layoutSpec.setOrigin(pcs(0f), dps(yPos - 60f), zero())
                layoutSpec.setSize(pcs(100f), dps(60f), full())

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
                y -= 30f
                val matLabel = button("selected-mat") {
                    layoutSpec.setOrigin(pcs(15f), dps(y), zero())
                    layoutSpec.setSize(pcs(70f), dps(35f), full())
                    text = currentMat.name

                    onClick += { _, _, _ ->
                        text = nextMaterial().name
                    }
                }
                +matLabel
                +button("mat-left") {
                    layoutSpec.setOrigin(pcs(0f), dps(y), zero())
                    layoutSpec.setSize(pcs(20f), dps(35f), full())
                    text = "<"

                    onClick += { _, _, _ ->
                        matLabel.text = prevMaterial().name
                    }
                }
                +button("mat-right") {
                    layoutSpec.setOrigin(pcs(80f), dps(y), zero())
                    layoutSpec.setSize(pcs(20f), dps(35f), full())
                    text = ">"

                    onClick += { _, _, _ ->
                        matLabel.text = nextMaterial().name
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

            val ibl = makeSphere(true, scene, envMaps)
            val nonIbl = makeSphere(false, scene, envMaps).apply { isVisible = false }

            +ibl
            +nonIbl

            iblContent = ibl
            nonIblContent = nonIbl

            onUpdate += {
                if (autoRotate) {
                    rotate(-2f * ctx.deltaT, Vec3f.Y_AXIS)
                }
            }
        }
        return content!!
    }

    override fun updateEnvironmentMap(envMaps: EnvironmentMaps) {
        iblContent?.children?.forEach {
            it as Mesh
            val pbrShader = it.shader as PbrShader
            pbrShader.irradianceMap(envMaps.irradianceMap)
            pbrShader.reflectionMap(envMaps.reflectionMap)
        }
    }

    private fun makeSphere(withIbl: Boolean, scene: Scene, envMaps: EnvironmentMaps) = group {
        +textureMesh(isNormalMapped = true) {
            geometry.addGeometry(sphereProto.detailSphere)
            val shader = pbrShader{
                albedoSource = Albedo.TEXTURE_ALBEDO
                isNormalMapped = true
                isRoughnessMapped = true
                isMetallicMapped = true
                isAoMapped = true
                isDisplacementMapped = true
                displacementStrength = 0.25f
                if (withIbl) {
                    useImageBasedLighting(envMaps)
                }
            }
            this.shader = shader
            shaders += shader

            updatePbrMaterial()

            scene.onDispose += {
                matCycler.forEach { it.disposeMaps() }
            }
        }
    }

    data class MaterialMaps(val name: String, val albedo: Texture2d, val normal: Texture2d, val roughness: Texture2d, val metallic: Texture2d?, val ao: Texture2d?, val displacement: Texture2d?) {
        fun disposeMaps() {
            albedo.dispose()
            normal.dispose()
            roughness.dispose()
            metallic?.dispose()
            ao?.dispose()
            displacement?.dispose()
        }
    }

    companion object {

        private val defaultMetallicTex = SingleColorTexture(Color.BLACK)
        private val defaultAoTex = SingleColorTexture(Color.WHITE)
        private val defaultDispTex = SingleColorTexture(Color.BLACK)

        private val assetPath = Demo.materialPath
        
        private val materials = mutableListOf(
                MaterialMaps(
                        "Bamboo",
                        Texture2d { it.loadTextureData("$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-albedo.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-normal.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-roughness.jpg") },
                        null,
                        Texture2d { it.loadTextureData("$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-ao.jpg") },
                        null
                ),

                MaterialMaps(
                        "Castle Brick",
                        Texture2d { it.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_diff_2k.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_nor_2k.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_rough_2k.jpg") },
                        null,
                        Texture2d { it.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_ao_2k.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_disp_2k.jpg") }
                ),

                MaterialMaps(
                        "Granite",
                        Texture2d { it.loadTextureData("$assetPath/granitesmooth1/granitesmooth1-albedo4.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/granitesmooth1/granitesmooth1-normal2.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/granitesmooth1/granitesmooth1-roughness3.jpg") },
                        null,
                        null,
                        null
                ),

                MaterialMaps(
                        "Weave Steel",
                        Texture2d { it.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_ROUGHNESS_2K_METALNESS.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_METALNESS_2K_METALNESS.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_AO_2K_METALNESS.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_DISP_2K_METALNESS.jpg") }
                ),

                MaterialMaps(
                        "Scuffed Plastic",
                        Texture2d { it.loadTextureData("$assetPath/scuffed-plastic-1/scuffed-plastic4-alb.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/scuffed-plastic-1/scuffed-plastic-normal.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/scuffed-plastic-1/scuffed-plastic-rough.jpg") },
                        null,
                        Texture2d { it.loadTextureData("$assetPath/scuffed-plastic-1/scuffed-plastic-ao.jpg") },
                        null
                ),

                MaterialMaps(
                        "Snow Covered Path",
                        Texture2d { it.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_albedo.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_normal-dx.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_roughness.jpg") },
                        null,
                        Texture2d { it.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_ao.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_height.jpg") }
                ),

                MaterialMaps(
                        "Marble",
                        Texture2d { it.loadTextureData("$assetPath/streaked-marble/streaked-marble-albedo2.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/streaked-marble/streaked-marble-normal.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/streaked-marble/streaked-marble-roughness1.jpg") },
                        null,
                        null,
                        null
                ),

                MaterialMaps(
                        "Onyx Tiles",
                        Texture2d { it.loadTextureData("$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_COL_2K.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_NRM_2K.jpg") },
                        Texture2d { it.loadTextureData("$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_REFL_2K.jpg") },
                        null,
                        null,
                        Texture2d { it.loadTextureData("$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_DISP_2K.jpg") }
                )
        )
    }
}