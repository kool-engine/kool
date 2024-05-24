package de.fabmax.kool.demo.pbr

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.*
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.SingleColorTexture
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.util.*

class PbrMaterialContent(val sphereProto: PbrDemo.SphereProto, val scene: Scene) : PbrDemo.PbrContent("PBR material") {
    private val shaders = mutableListOf<KslPbrShader>()
    private var vertexDisplacedContent: Mesh? = null
    private var parallaxContent: Mesh? = null

    private val useParallaxMapping = mutableStateOf(true).onChange {
        parallaxContent?.isVisible = it
        vertexDisplacedContent?.isVisible = !it
    }
    private val selectedMatIdx = mutableStateOf(3)
    private val loadedMaterials = Array<MaterialMaps?>(materialLoaders.size) { null }

    private val displacement = mutableStateOf(0.35f).onChange { disp ->
        shaders.forEach {
            it.parallaxStrength = disp
            it.vertexDisplacementStrength = disp
        }
    }

    private fun updatePbrMaterial() {
        launchOnMainThread {
            val materialIdx = selectedMatIdx.value
            val maps = loadedMaterials[materialIdx] ?: materialLoaders[materialIdx].second().also {
                it.releaseWith(scene)
                loadedMaterials[materialIdx] = it
            }

            shaders.forEach {
                it.colorMap = maps.albedo
                it.normalMap = maps.normal
                it.roughnessMap = maps.roughness
                it.metallicMap = maps.metallic ?: defaultMetallicTex
                it.materialAoMap = maps.ao ?: defaultAoTex
                it.vertexDisplacementMap = maps.displacement ?: defaultDispTex
                it.parallaxMap = maps.displacement ?: defaultParallaxTex
            }
        }
    }

    override fun UiScope.createContentMenu() {
        MenuRow {
            Text("Material") { labelStyle() }
            ComboBox {
                modifier
                    .width(Grow.Std)
                    .margin(start = sizes.largeGap)
                    .items(materialLoaders.map { it.first })
                    .selectedIndex(selectedMatIdx.use())
                    .onItemSelected {
                        selectedMatIdx.set(it)
                        updatePbrMaterial()
                    }
            }
        }

        MenuSlider2("Displacement Strength:", displacement.use(), 0f, 1f) { displacement.set(it) }

        MenuRow {
            Text("Displacement Method:") { labelStyle(Grow.Std) }
        }
        val isParallax = useParallaxMapping.use()
        LabeledRadioButton("Parallax Mapping", isParallax) { useParallaxMapping.set(true) }
        LabeledRadioButton("Vertex Displacement", !isParallax) { useParallaxMapping.set(false) }
    }

    override fun createContent(scene: Scene, envMaps: EnvironmentMaps, ctx: KoolContext): Node {
        content = Node().apply {
            isVisible = false

            vertexDisplacedContent = makeVertexSphere(envMaps).apply { isVisible = !useParallaxMapping.value }
            parallaxContent = makeParallaxSphere(envMaps).apply { isVisible = useParallaxMapping.value }

            onUpdate += {
                if (autoRotate) {
                    transform.rotate((-2f * Time.deltaT).deg, Vec3f.Y_AXIS)
                }
            }
        }
        return content!!
    }

    override fun updateEnvironmentMap(envMaps: EnvironmentMaps) {
        vertexDisplacedContent?.let {
            val pbrShader = it.shader as KslPbrShader
            pbrShader.ambientMap = envMaps.irradianceMap
            pbrShader.reflectionMap = envMaps.reflectionMap
        }
        parallaxContent?.let {
            val pbrShader = it.shader as KslPbrShader
            pbrShader.ambientMap = envMaps.irradianceMap
            pbrShader.reflectionMap = envMaps.reflectionMap
        }
    }

    private fun Node.makeVertexSphere(envMaps: EnvironmentMaps) = addTextureMesh(isNormalMapped = true) {
        geometry.addGeometry(sphereProto.detailSphere)

        val shader = KslPbrShader {
            color { textureColor() }
            normalMapping { setNormalMap() }
            roughness { textureProperty() }
            metallic { textureProperty() }
            ao { materialAo { textureProperty() } }
            vertices {
                displacement {
                    textureProperty()
                    constProperty(-0.5f, blendMode = PropertyBlockConfig.BlendMode.Add)
                    uniformProperty(displacement.value, blendMode = PropertyBlockConfig.BlendMode.Multiply)
                }
            }
            imageBasedAmbientColor(envMaps.irradianceMap)
            reflectionMap = envMaps.reflectionMap
        }
        this.shader = shader
        shaders += shader

        updatePbrMaterial()
    }

    private fun Node.makeParallaxSphere(envMaps: EnvironmentMaps) = addTextureMesh(isNormalMapped = true) {
        geometry.addGeometry(sphereProto.parallaxSphere)

        val shader = KslPbrShader {
            color { textureColor() }
            normalMapping { setNormalMap() }
            roughness { textureProperty() }
            metallic { textureProperty() }
            ao { materialAo { textureProperty() } }
            vertices {
                displacement {
                    constProperty(0.5f)
                    uniformProperty(displacement.value, blendMode = PropertyBlockConfig.BlendMode.Multiply)
                }
            }
            parallaxMapping {
                useParallaxMap(strength = displacement.value, maxSteps = 16)
            }

            imageBasedAmbientColor(envMaps.irradianceMap)
            reflectionMap = envMaps.reflectionMap
        }
        this.shader = shader
        shaders += shader

        updatePbrMaterial()
    }

    class MaterialMaps(
        val albedo: Texture2d,
        val normal: Texture2d,
        val roughness: Texture2d,
        val metallic: Texture2d?,
        val ao: Texture2d?,
        val displacement: Texture2d?
    ) : BaseReleasable() {
        override fun release() {
            super.release()
            albedo.release()
            normal.release()
            roughness.release()
            metallic?.release()
            ao?.release()
            displacement?.release()
        }
    }

    companion object {
        private val defaultMetallicTex = SingleColorTexture(Color.BLACK)
        private val defaultAoTex = SingleColorTexture(Color.WHITE)
        private val defaultDispTex = SingleColorTexture(Color.BLACK)
        private val defaultParallaxTex = SingleColorTexture(Color.WHITE)

        private val assetPath = DemoLoader.materialPath

        private suspend fun loadMaps(
            albedoPath: String,
            normalPath: String,
            roughnessPath: String,
            metallicPath: String?,
            aoPath: String?,
            displacementPath: String?,
        ): MaterialMaps {
            val singleChProps = TextureProps(format = TexFormat.R)
            val albedo = Assets.loadTexture2dAsync(albedoPath)
            val normal = Assets.loadTexture2dAsync(normalPath)
            val roughness = Assets.loadTexture2dAsync(roughnessPath, props = singleChProps)
            val metallic = metallicPath?.let { Assets.loadTexture2dAsync(it, props = singleChProps) }
            val ao = aoPath?.let { Assets.loadTexture2dAsync(it, props = singleChProps) }
            val displacement = displacementPath?.let { Assets.loadTexture2dAsync(it, props = singleChProps) }
            return MaterialMaps(albedo.await(), normal.await(), roughness.await(), metallic?.await(), ao?.await(), displacement?.await())
        }

        private val materialLoaders = listOf<Pair<String, suspend () -> MaterialMaps>>(
            "Bamboo" to {
                loadMaps(
                    "$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-albedo.jpg",
                    "$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-normal.jpg",
                    "$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-roughness.jpg",
                    null,
                    "$assetPath/bamboo-wood-semigloss/bamboo-wood-semigloss-ao.jpg",
                    null
                )
            },
            "Castle Brick" to {
                loadMaps(
                    "$assetPath/castle_brick/castle_brick_02_red_diff_2k.jpg",
                    "$assetPath/castle_brick/castle_brick_02_red_nor_2k.jpg",
                    "$assetPath/castle_brick/castle_brick_02_red_rough_2k.jpg",
                    null,
                    "$assetPath/castle_brick/castle_brick_02_red_ao_2k.jpg",
                    "$assetPath/castle_brick/castle_brick_02_red_disp_2k.jpg"
                )
            },
            "Granite" to {
                loadMaps(
                    "$assetPath/granitesmooth1/granitesmooth1-albedo4.jpg",
                    "$assetPath/granitesmooth1/granitesmooth1-normal2.jpg",
                    "$assetPath/granitesmooth1/granitesmooth1-roughness3.jpg",
                    null,
                    null,
                    null
                )
            },
            "Weave Steel" to {
                loadMaps(
                    "$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg",
                    "$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg",
                    "$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_ROUGHNESS_2K_METALNESS.jpg",
                    "$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_METALNESS_2K_METALNESS.jpg",
                    "$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_AO_2K_METALNESS.jpg",
                    "$assetPath/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_DISP_2K_METALNESS.jpg"
                )
            },
            "Scuffed Plastic" to {
                loadMaps(
                    "$assetPath/scuffed-plastic-1/scuffed-plastic4-alb.jpg",
                    "$assetPath/scuffed-plastic-1/scuffed-plastic-normal.jpg",
                    "$assetPath/scuffed-plastic-1/scuffed-plastic-rough.jpg",
                    null,
                    "$assetPath/scuffed-plastic-1/scuffed-plastic-ao.jpg",
                    null
                )
            },
            "Snow Covered Path" to {
                loadMaps(
                    "$assetPath/snowcoveredpath/snowcoveredpath_albedo.jpg",
                    "$assetPath/snowcoveredpath/snowcoveredpath_normal-dx.jpg",
                    "$assetPath/snowcoveredpath/snowcoveredpath_roughness.jpg",
                    null,
                    "$assetPath/snowcoveredpath/snowcoveredpath_ao.jpg",
                    "$assetPath/snowcoveredpath/snowcoveredpath_height.jpg"
                )
            },
            "Marble" to {
                loadMaps(
                    "$assetPath/streaked-marble/streaked-marble-albedo2.jpg",
                    "$assetPath/streaked-marble/streaked-marble-normal.jpg",
                    "$assetPath/streaked-marble/streaked-marble-roughness1.jpg",
                    null,
                    null,
                    null
                )
            },
            "Onyx Tiles" to {
                loadMaps(
                    "$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_COL_2K.jpg",
                    "$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_NRM_2K.jpg",
                    "$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_REFL_2K.jpg",
                    null,
                    null,
                    "$assetPath/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_DISP_2K.jpg"
                )
            },
        )
    }
}