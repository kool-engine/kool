package de.fabmax.kool.demo.pbr

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time

class PbrMaterialContent(val sphereProto: PbrDemo.SphereProto) : PbrDemo.PbrContent("PBR material") {
    private val shaders = mutableListOf<KslPbrShader>()
    private var iblContent: Node? = null
    private var nonIblContent: Node? = null

    private val selectedMatIdx = mutableStateOf(3)
    val currentMat: MaterialMaps get() = materials[selectedMatIdx.value]

    private val displacement = mutableStateOf(0.25f).onChange { disp -> shaders.forEach { it.displacement = disp } }

    private fun updatePbrMaterial() {
        shaders.forEach {
            it.colorMap = currentMat.albedo
            it.normalMap = currentMat.normal
            it.roughnessMap = currentMat.roughness
            it.metallicMap = currentMat.metallic ?: defaultMetallicTex
            it.materialAoMap = currentMat.ao ?: defaultAoTex
            it.displacementMap = currentMat.displacement ?: defaultDispTex
        }
    }

    override fun UiScope.createContentMenu() {
        MenuRow {
            Text("Material") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(materials)
                    .selectedIndex(selectedMatIdx.use())
                    .onItemSelected {
                        currentMat.disposeMaps()
                        selectedMatIdx.set(it)
                        updatePbrMaterial()
                    }
            }
        }
        MenuRow {
            val txtSize = UiSizes.baseSize * 0.75f
            Text("Displacement") { labelStyle(FitContent) }
            MenuSlider(displacement.use(), 0f, 1f, txtWidth = txtSize) { displacement.set(it) }
        }
    }

    override fun setUseImageBasedLighting(enabled: Boolean) {
        iblContent?.isVisible = enabled
        nonIblContent?.isVisible = !enabled
    }

    override fun createContent(scene: Scene, envMaps: EnvironmentMaps, ctx: KoolContext): Node {
        content = Node().apply {
            isVisible = false

            val ibl = makeSphere(true, scene, envMaps)
            val nonIbl = makeSphere(false, scene, envMaps).apply { isVisible = false }

            iblContent = ibl
            nonIblContent = nonIbl

            onUpdate += {
                if (autoRotate) {
                    transform.rotate(-2f * Time.deltaT, Vec3f.Y_AXIS)
                }
            }
        }
        return content!!
    }

    override fun updateEnvironmentMap(envMaps: EnvironmentMaps) {
        (iblContent as Mesh?)?.let {
            val pbrShader = it.shader as KslPbrShader
            pbrShader.ambientMap = envMaps.irradianceMap
            pbrShader.reflectionMap = envMaps.reflectionMap
        }
    }

    private fun Node.makeSphere(withIbl: Boolean, scene: Scene, envMaps: EnvironmentMaps) = addTextureMesh(isNormalMapped = true) {
        geometry.addGeometry(sphereProto.detailSphere)
        val shader = KslPbrShader{
            color { textureColor() }
            normalMapping { setNormalMap() }
            roughness { textureProperty() }
            metallic { textureProperty() }
            ao { materialAo { textureProperty() } }
            vertices {
                displacement {
                    textureProperty()
                    uniformProperty(displacement.value, blendMode = PropertyBlockConfig.BlendMode.Multiply)
                }
            }
            if (withIbl) {
                imageBasedAmbientColor(envMaps.irradianceMap)
                reflectionMap = envMaps.reflectionMap
            }
        }
        this.shader = shader
        shaders += shader

        updatePbrMaterial()

        scene.onDispose += {
            materials.forEach { it.disposeMaps() }
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

        override fun toString() = name
    }

    companion object {

        private val defaultMetallicTex = SingleColorTexture(Color.BLACK)
        private val defaultAoTex = SingleColorTexture(Color.WHITE)
        private val defaultDispTex = SingleColorTexture(Color.BLACK)

        private val assetPath = DemoLoader.materialPath
        
        private val materials = mutableListOf(
                MaterialMaps(
                        "Bamboo",
                        Texture2d { Assets.loadTextureData("$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-albedo.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-normal.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-roughness.jpg") },
                        null,
                        Texture2d { Assets.loadTextureData("$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-ao.jpg") },
                        null
                ),

                MaterialMaps(
                        "Castle Brick",
                        Texture2d { Assets.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_diff_2k.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_nor_2k.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_rough_2k.jpg") },
                        null,
                        Texture2d { Assets.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_ao_2k.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/castle_brick/castle_brick_02_red_disp_2k.jpg") }
                ),

                MaterialMaps(
                        "Granite",
                        Texture2d { Assets.loadTextureData("$assetPath/granitesmooth1/granitesmooth1-albedo4.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/granitesmooth1/granitesmooth1-normal2.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/granitesmooth1/granitesmooth1-roughness3.jpg") },
                        null,
                        null,
                        null
                ),

                MaterialMaps(
                        "Weave Steel",
                        Texture2d { Assets.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_ROUGHNESS_2K_METALNESS.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_METALNESS_2K_METALNESS.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_AO_2K_METALNESS.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_DISP_2K_METALNESS.jpg") }
                ),

                MaterialMaps(
                        "Scuffed Plastic",
                        Texture2d { Assets.loadTextureData("$assetPath/scuffed-plastic-1/scuffed-plastic4-alb.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/scuffed-plastic-1/scuffed-plastic-normal.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/scuffed-plastic-1/scuffed-plastic-rough.jpg") },
                        null,
                        Texture2d { Assets.loadTextureData("$assetPath/scuffed-plastic-1/scuffed-plastic-ao.jpg") },
                        null
                ),

                MaterialMaps(
                        "Snow Covered Path",
                        Texture2d { Assets.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_albedo.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_normal-dx.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_roughness.jpg") },
                        null,
                        Texture2d { Assets.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_ao.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/snowcoveredpath/snowcoveredpath_height.jpg") }
                ),

                MaterialMaps(
                        "Marble",
                        Texture2d { Assets.loadTextureData("$assetPath/streaked-marble/streaked-marble-albedo2.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/streaked-marble/streaked-marble-normal.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/streaked-marble/streaked-marble-roughness1.jpg") },
                        null,
                        null,
                        null
                ),

                MaterialMaps(
                        "Onyx Tiles",
                        Texture2d { Assets.loadTextureData("$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_COL_2K.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_NRM_2K.jpg") },
                        Texture2d { Assets.loadTextureData("$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_REFL_2K.jpg") },
                        null,
                        null,
                        Texture2d { Assets.loadTextureData("$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_DISP_2K.jpg") }
                )
        )
    }
}