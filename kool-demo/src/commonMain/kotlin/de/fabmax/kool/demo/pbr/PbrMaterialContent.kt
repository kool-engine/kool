package de.fabmax.kool.demo.pbr

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font

class PbrMaterialContent : PbrDemo.PbrContent("PBR Material") {
    val matCycler = PbrDemo.Cycler(materials)
    val currentMat: MaterialMaps
        get() = matCycler.current

    private val shaders = mutableListOf<PbrShader>()
    private var iblContent: Group? = null
    private var nonIblContent: Group? = null

    fun nextMaterial(): MaterialMaps {
        currentMat.disposeMaps()
        matCycler.next()
        updatePbrMaterial()
        return currentMat
    }

    fun prevMaterial(): MaterialMaps {
        currentMat.disposeMaps()
        matCycler.prev()
        updatePbrMaterial()
        return currentMat
    }

    private fun updatePbrMaterial() {
        shaders.forEach {
            it.albedoMap = currentMat.albedo
            it.normalMap = currentMat.normal
            it.roughnessMap = currentMat.roughness
            it.metallicMap = currentMat.metallic ?: defaultMetallicTex
            it.ambientOcclusionMap = currentMat.ao ?: defaultAoTex
            it.displacementMap = currentMat.displacement ?: defaultDispTex
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

    override fun createContent(scene: Scene, irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture, ctx: KoolContext): TransformGroup {
        content = transformGroup {
            isVisible = false

            val ibl = makeSphere(true, scene, irradianceMap, reflectionMap, brdfLut)
            val nonIbl = makeSphere(false, scene, irradianceMap, reflectionMap, brdfLut).apply { isVisible = false }

            +ibl
            +nonIbl

            iblContent = ibl
            nonIblContent = nonIbl

            onPreRender += { ctx ->
                rotate(-2f * ctx.deltaT, Vec3f.Y_AXIS)
            }
        }
        return content!!
    }

    private fun makeSphere(withIbl: Boolean, scene: Scene, irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture) = group {
        +textureMesh(isNormalMapped = true) {
            generate {
                vertexModFun = {
                    texCoord.x *= 4
                    texCoord.y *= 2
                }
                sphere {
                    steps = 700
                    radius = 7f
                }
            }

            val pbrConfig = PbrShader.PbrConfig()
            pbrConfig.isImageBasedLighting = withIbl
            pbrConfig.albedoSource = PbrShader.AlbedoSource.TEXTURE_ALBEDO
            pbrConfig.isNormalMapped = true
            pbrConfig.isRoughnessMapped = true
            pbrConfig.isMetallicMapped = true
            pbrConfig.isAmbientOcclusionMapped = true
            pbrConfig.isDisplacementMapped = true
            val shader = PbrShader(pbrConfig).apply {
                this.irradianceMap = irradianceMap
                this.reflectionMap = reflectionMap
                this.brdfLut = brdfLut
            }
            pipelineLoader = shader
            shaders += shader

            updatePbrMaterial()

            scene.onDispose += {
                matCycler.forEach { it.disposeMaps() }
            }
        }
    }

    data class MaterialMaps(val name: String, val albedo: Texture, val normal: Texture, val roughness: Texture, val metallic: Texture?, val ao: Texture?, val displacement: Texture?) {
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

        private val materials = mutableListOf(
                MaterialMaps(
                        "Bamboo",
                        Texture { it.loadTextureData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-roughness.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-ao.png") },
                        null
                ),

                MaterialMaps(
                        "Brown Mud",
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_diff_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_nor_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_rough_2k.jpg") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_ao_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_disp_2k.jpg") }
                ),

                MaterialMaps(
                        "Castle Brick",
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_diff_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_nor_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_rough_2k.jpg") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_ao_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_disp_2k.jpg") }
                ),

                MaterialMaps(
                        "Copper Rock",
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-alb.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-rough.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-metal.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-ao.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-height.png") }
                ),

                MaterialMaps(
                        "Dungeon Stone",
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-albedo2.png") },
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-roughness.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-ao.png") },
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-height.png") }
                ),

                MaterialMaps(
                        "Granite",
                        //Texture { it.loadTextureData("reserve/pbr/granitesmooth1/granitesmooth1-albedo2.png") },
                        Texture { it.loadTextureData("reserve/pbr/granitesmooth1/granitesmooth1-albedo4.png") },
                        Texture { it.loadTextureData("reserve/pbr/granitesmooth1/granitesmooth1-normal2.png") },
                        Texture { it.loadTextureData("reserve/pbr/granitesmooth1/granitesmooth1-roughness3.png") },
                        null,
                        null,
                        null
                ),

                MaterialMaps(
                        "Greasy Metal",
                        Texture { it.loadTextureData("reserve/pbr/greasy_pan/greasy-metal-pan1-albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/greasy_pan/greasy-metal-pan1-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/greasy_pan/greasy-metal-pan1-roughness.png") },
                        Texture { it.loadTextureData("reserve/pbr/greasy_pan/greasy-metal-pan1-metal.png") },
                        null,
                        null
                ),

                MaterialMaps(
                        "Hardwood Planks",
                        Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-normal-dx.png") },
                        Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-roughness.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-ao.png") },
                        null //Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-height.png") }
                ),

                MaterialMaps(
                        "Splotchy Metal",
                        Texture { it.loadTextureData("reserve/pbr/metal_splotchy/metal-splotchy-albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/metal_splotchy/metal-splotchy-normal-dx.png") },
                        Texture { it.loadTextureData("reserve/pbr/metal_splotchy/metal-splotchy-rough.png") },
                        Texture { it.loadTextureData("reserve/pbr/metal_splotchy/metal-splotchy-metal.png") },
                        null,
                        null
                ),

                MaterialMaps(
                        "Weave Steel",
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_ROUGHNESS_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_METALNESS_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_AO_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_DISP_2K_METALNESS.jpg") }
                ),

                MaterialMaps(
                        "Rusted Iron",
                        Texture { it.loadTextureData("reserve/pbr/rusted_iron2/rustediron2_basecolor.png") },
                        Texture { it.loadTextureData("reserve/pbr/rusted_iron2/rustediron2_normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/rusted_iron2/rustediron2_roughness.png") },
                        Texture { it.loadTextureData("reserve/pbr/rusted_iron2/rustediron2_metallic.png") },
                        null,
                        null
                ),

                MaterialMaps(
                        "Scuffed Plastic",
                        Texture { it.loadTextureData("reserve/pbr/scuffed-plastic-1/scuffed-plastic4-alb.png") },
                        Texture { it.loadTextureData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-rough.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-ao.png") },
                        null
                ),

                MaterialMaps(
                        "Snow Covered Path",
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_normal-dx.png") },
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_roughness.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_ao.png") },
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_height.png") }
                ),

                MaterialMaps(
                        "Marble",
                        Texture { it.loadTextureData("reserve/pbr/streaked-marble/streaked-marble-albedo2.png") },
                        Texture { it.loadTextureData("reserve/pbr/streaked-marble/streaked-marble-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/streaked-marble/streaked-marble-roughness1.png") },
                        null,
                        null,
                        null
                ),

                MaterialMaps(
                        "Onyx Tiles",
                        Texture { it.loadTextureData("reserve/pbr/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_COL_2K.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_NRM_2K.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_REFL_2K.jpg") },
                        null,
                        null,
                        Texture { it.loadTextureData("reserve/pbr/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_DISP_2K.jpg") }
                ),

                MaterialMaps(
                        "Scuffed Titanium",
                        Texture { it.loadTextureData("reserve/pbr/Titanium-Scuffed/Titanium-Scuffed_basecolor.png") },
                        Texture { it.loadTextureData("reserve/pbr/Titanium-Scuffed/Titanium-Scuffed_normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/Titanium-Scuffed/Titanium-Scuffed_roughness.png") },
                        Texture { it.loadTextureData("reserve/pbr/Titanium-Scuffed/Titanium-Scuffed_metallic.png") },
                        null,
                        null
                ),

                MaterialMaps(
                        "Water Worn Stone",
                        Texture { it.loadTextureData("reserve/pbr/waterwornstone1/waterwornstone1_Base_Color.png") },
                        Texture { it.loadTextureData("reserve/pbr/waterwornstone1/waterwornstone1_Normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/waterwornstone1/waterwornstone1_Roughness.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/waterwornstone1/waterwornstone1_Ambient_Occlusion.png") },
                        Texture { it.loadTextureData("reserve/pbr/waterwornstone1/waterwornstone1_Height.png") }
                )
        )
    }
}