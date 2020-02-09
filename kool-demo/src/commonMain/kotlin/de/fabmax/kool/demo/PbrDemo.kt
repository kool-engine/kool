package de.fabmax.kool.demo

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.pbrMapGen.BrdfLutPass
import de.fabmax.kool.util.pbrMapGen.IrradianceMapPass
import de.fabmax.kool.util.pbrMapGen.ReflectionMapPass
import kotlin.math.max

/**
 * @author fabmax
 */


fun pbrDemoScene(ctx: KoolContext): Scene {
    return PbrDemo(ctx).scene
}

class PbrDemo(val ctx: KoolContext) {
    val scene: Scene

    private var irradianceMapPass: IrradianceMapPass? = null
    private var reflectionMapPass: ReflectionMapPass? = null
    private var brdfLut: BrdfLutPass? = null

    private val loadedHdris = MutableList<Texture?>(hdriTextures.size) { null }
    private var currentHdri = 0

    init {
        scene = setupScene()

        val nextHdriKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_LEFT, "Next environment map", { it.isPressed }) {
            currentHdri = (--currentHdri + hdriTextures.size) % hdriTextures.size
            updateHdri(currentHdri)
        }
        val prevHdriKeyListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_RIGHT, "Prev environment map", { it.isPressed }) {
            currentHdri = ++currentHdri % hdriTextures.size
            updateHdri(currentHdri)
        }

        scene.onDispose += {
            ctx.inputMgr.removeKeyListener(nextHdriKeyListener)
            ctx.inputMgr.removeKeyListener(prevHdriKeyListener)

            loadedHdris.forEach { it?.dispose() }
            irradianceMapPass?.dispose(ctx)
            reflectionMapPass?.dispose(ctx)
            brdfLut?.dispose(ctx)
        }
    }

    private fun setupScene() = scene {
        setupLighting()

        +orbitInputTransform {
            +camera
            // let the camera slowly rotate around vertical axis
            onPreRender += { ctx -> verticalRotation += ctx.deltaT * 2f }
            zoomMethod = OrbitInputTransform.ZoomMethod.ZOOM_CENTER
        }

        loadHdri(currentHdri) { tex ->
            val irrMapPass = IrradianceMapPass(tex)
            val reflMapPass = ReflectionMapPass(tex)
            val brdfLutPass = BrdfLutPass()
            irradianceMapPass = irrMapPass
            reflectionMapPass = reflMapPass
            brdfLut = brdfLutPass

            ctx.offscreenPasses += irrMapPass.offscreenPass
            ctx.offscreenPasses += reflMapPass.offscreenPass
            ctx.offscreenPasses += brdfLutPass.offscreenPass

            this += Skybox(reflMapPass.reflectionMap, 1.0f)

//            colorGrid(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut)
//            roughnessMetallicGrid(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut)
            pbrMat(irrMapPass.irradianceMap, reflMapPass.reflectionMap, brdfLutPass.brdfLut, ctx)
        }
    }

    private fun Scene.setupLighting() {
        val lightStrength = 250f
        val extent = 10f
        val light1 = Light().setPoint(Vec3f(extent, extent, extent)).setColor(Color.WHITE, lightStrength)
        val light2 = Light().setPoint(Vec3f(-extent, -extent, extent)).setColor(Color.WHITE, lightStrength)
        val light3 = Light().setPoint(Vec3f(-extent, extent, extent)).setColor(Color.WHITE, lightStrength)
        val light4 = Light().setPoint(Vec3f(extent, -extent, extent)).setColor(Color.WHITE, lightStrength)

        lighting.lights.clear()
        lighting.lights.add(light1)
        lighting.lights.add(light2)
        lighting.lights.add(light3)
        lighting.lights.add(light4)
    }

    private fun updateHdri(idx: Int) {
        loadHdri(idx) { tex ->
            irradianceMapPass?.let {
                it.hdriTexture = tex
                it.update(ctx)
            }
            reflectionMapPass?.let {
                it.hdriTexture = tex
                it.update(ctx)
            }
        }
    }

    private fun loadHdri(idx: Int, recv: (Texture) -> Unit) {
        val tex = loadedHdris[idx]
        if (tex == null) {
            ctx.assetMgr.loadAndPrepareTexture(hdriTextures[idx], hdriTexProps) {
                loadedHdris[idx] = it
                recv(it)
            }
        } else {
            recv(tex)
        }
    }

    private fun setPbrMaterial(shader: PbrShader, mat: MaterialMaps) {
        shader.albedoMap = mat.albedo
        shader.normalMap = mat.normal
        shader.roughnessMap = mat.roughness
        shader.metallicMap = mat.metallic ?: defaultMetallicTex
        shader.ambientOcclusionMap = mat.ao ?: defaultAoTex
        shader.displacementMap = mat.displacement ?: defaultDispTex
    }

    private fun Scene.colorGrid(irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture) {
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
                +colorMesh {
                    generate {
                        //color = colors[(x * nRows + y) % colors.size].gamma()
                        color = colors[(y * nCols + x) % colors.size].gamma()
                        sphere {
                            steps = 100
                            center.set((-(nCols-1) * 0.5f + x) * spacing, ((nRows-1) * 0.5f - y) * spacing, 0f)
                            radius = 1.5f
                        }
                    }

                    val pbrConfig = PbrShader.PbrConfig()
                    pbrConfig.irradianceMap = irradianceMap
                    pbrConfig.reflectionMap = reflectionMap
                    pbrConfig.brdfLut = brdfLut

                    val shader = PbrShader(pbrConfig)
                    shader.roughness = 0.1f
                    shader.metallic = 1f
                    pipelineLoader = shader
                }
            }
        }
    }

    private fun Scene.roughnessMetallicGrid(irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture) {
        val nRows = 7
        val nCols = 7
        val spacing = 2.5f

        for (y in 0 until nRows) {
            for (x in 0 until nCols) {
                +colorMesh {
                    generate {
                        color = Color.DARK_RED
                        //color = Color.MD_GREEN.gamma()
                        sphere {
                            steps = 100
                            center.set((-(nCols-1) * 0.5f + x) * spacing, (-(nRows-1) * 0.5f + y) * spacing, 0f)
                            radius = 1f
                        }
                    }

                    val pbrConfig = PbrShader.PbrConfig()
                    pbrConfig.irradianceMap = irradianceMap
                    pbrConfig.reflectionMap = reflectionMap
                    pbrConfig.brdfLut = brdfLut

                    val shader = PbrShader(pbrConfig)
                    shader.roughness = max(x / (nCols - 1).toFloat(), 0.05f)
                    shader.metallic = y / (nRows - 1).toFloat()
                    pipelineLoader = shader
                }
            }
        }
    }

    private fun Scene.pbrMat(irradianceMap: CubeMapTexture, reflectionMap: CubeMapTexture, brdfLut: Texture, ctx: KoolContext) {
        val matMaps = materials.values.toList()
        var matIdx = 0

        val pbrConfig = PbrShader.PbrConfig()
        pbrConfig.irradianceMap = irradianceMap
        pbrConfig.reflectionMap = reflectionMap
        pbrConfig.brdfLut = brdfLut
        pbrConfig.albedoMap = matMaps[matIdx].albedo
        pbrConfig.normalMap = matMaps[matIdx].normal
        pbrConfig.roughnessMap = matMaps[matIdx].roughness
        pbrConfig.metallicMap = matMaps[matIdx].metallic ?: defaultMetallicTex
        pbrConfig.ambientOcclusionMap = matMaps[matIdx].ao ?: defaultAoTex
        pbrConfig.displacementMap = matMaps[matIdx].displacement ?: defaultDispTex

        +transformGroup {
            +textureMesh(isNormalMapped = true) {
                generate {
                    vertexModFun = {
                        texCoord.x *= 4
                        texCoord.y *= 2
                    }
                    sphere {
                        steps = 700
                        radius = 3f
                    }
                }

                val shader = PbrShader(pbrConfig)
                shader.roughness = 0.1f
                shader.metallic = 1f
                pipelineLoader = shader

                val nextMatListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_UP, "Change Mat +", { it.isPressed }) {
                    matMaps[matIdx].disposeMaps()
                    matIdx = (matIdx + 1) % matMaps.size
                    setPbrMaterial(shader, matMaps[matIdx])
                }
                val prevMatListener = ctx.inputMgr.registerKeyListener(InputManager.KEY_CURSOR_DOWN, "Change Mat -", { it.isPressed }) {
                    matMaps[matIdx].disposeMaps()
                    matIdx = (matIdx + matMaps.size - 1) % matMaps.size
                    setPbrMaterial(shader, matMaps[matIdx])
                }

                this@pbrMat.onDispose += {
                    ctx.inputMgr.removeKeyListener(nextMatListener)
                    ctx.inputMgr.removeKeyListener(prevMatListener)
                    matMaps[matIdx].disposeMaps()
                }
            }

            onPreRender += { ctx ->
                rotate(-2f * ctx.deltaT, Vec3f.Y_AXIS)
            }
        }
    }

    private data class MaterialMaps(val albedo: Texture, val normal: Texture, val roughness: Texture, val metallic: Texture?, val ao: Texture?, val displacement: Texture?) {
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
        // HDRIs are encoded as RGBE images, use nearest sampling to not mess up the exponent
        private val hdriTexProps = TextureProps(
                minFilter = FilterMethod.NEAREST,
                magFilter = FilterMethod.NEAREST,
                mipMapping = true)

        private val hdriTextures = listOf(
                "skybox/hdri/circus_arena_2k.rgbe.png",
                "skybox/hdri/newport_loft.rgbe.png",
                "skybox/hdri/lakeside_2k.rgbe.png",
                "skybox/hdri/spruit_sunrise_2k.rgbe.png",
                "skybox/hdri/driving_school.rgbe.png",
                "skybox/hdri/royal_esplanade_2k.rgbe.png",
                "skybox/hdri/shanghai_bund_2k.rgbe.png",
                "skybox/hdri/vignaioli_night_2k.rgbe.png",
                "skybox/hdri/winter_evening_2k.rgbe.png"
        )

        private val defaultMetallicTex = SingleColorTexture(Color.BLACK)
        private val defaultAoTex = SingleColorTexture(Color.WHITE)
        private val defaultDispTex = SingleColorTexture(Color.BLACK)

        private val materials = mutableMapOf(
                "Bamboo" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-roughness.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/bamboo-wood-semigloss/bamboo-wood-semigloss-ao.png") },
                        null
                ),

                "Brown Mud" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_diff_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_nor_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_rough_2k.jpg") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_ao_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/brown_mud_leaves_01/brown_mud_leaves_01_disp_2k.jpg") }
                ),

                "Castle Brick" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_diff_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_nor_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_rough_2k.jpg") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_ao_2k.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/castle_brick/castle_brick_02_red_disp_2k.jpg") }
                ),

                "Copper Rock" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-alb.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-rough.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-metal.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-ao.png") },
                        Texture { it.loadTextureData("reserve/pbr/copper_rock/copper-rock1-height.png") }
                ),

                "Dungeon Stone" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-albedo2.png") },
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-roughness.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-ao.png") },
                        Texture { it.loadTextureData("reserve/pbr/dungeon-stone1/dungeon-stone1-height.png") }
                ),

                "Granite" to MaterialMaps(
                        //Texture { it.loadTextureData("reserve/pbr/granitesmooth1/granitesmooth1-albedo2.png") },
                        Texture { it.loadTextureData("reserve/pbr/granitesmooth1/granitesmooth1-albedo4.png") },
                        Texture { it.loadTextureData("reserve/pbr/granitesmooth1/granitesmooth1-normal2.png") },
                        Texture { it.loadTextureData("reserve/pbr/granitesmooth1/granitesmooth1-roughness3.png") },
                        null,
                        null,
                        null
                ),

                "Greasy Metal" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/greasy_pan/greasy-metal-pan1-albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/greasy_pan/greasy-metal-pan1-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/greasy_pan/greasy-metal-pan1-roughness.png") },
                        Texture { it.loadTextureData("reserve/pbr/greasy_pan/greasy-metal-pan1-metal.png") },
                        null,
                        null
                ),

                "Hardwood Planks" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-normal-dx.png") },
                        Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-roughness.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-ao.png") },
                        null //Texture { it.loadTextureData("reserve/pbr/hardwood_planks/hardwood-brown-planks-height.png") }
                ),

                "Splotchy Metal" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/metal_splotchy/metal-splotchy-albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/metal_splotchy/metal-splotchy-normal-dx.png") },
                        Texture { it.loadTextureData("reserve/pbr/metal_splotchy/metal-splotchy-rough.png") },
                        Texture { it.loadTextureData("reserve/pbr/metal_splotchy/metal-splotchy-metal.png") },
                        null,
                        null
                ),

                "Weave Steel" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_COL_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_NRM_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_ROUGHNESS_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_METALNESS_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_AO_2K_METALNESS.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/MetalDesignerWeaveSteel002/MetalDesignerWeaveSteel002_DISP_2K_METALNESS.jpg") }
                ),

                "Rusted Iron" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/rusted_iron2/rustediron2_basecolor.png") },
                        Texture { it.loadTextureData("reserve/pbr/rusted_iron2/rustediron2_normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/rusted_iron2/rustediron2_roughness.png") },
                        Texture { it.loadTextureData("reserve/pbr/rusted_iron2/rustediron2_metallic.png") },
                        null,
                        null
                ),

                "Scuffed Plastic" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/scuffed-plastic-1/scuffed-plastic4-alb.png") },
                        Texture { it.loadTextureData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-rough.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/scuffed-plastic-1/scuffed-plastic-ao.png") },
                        null
                ),

                "Snow Covered Path" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_albedo.png") },
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_normal-dx.png") },
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_roughness.png") },
                        null,
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_ao.png") },
                        Texture { it.loadTextureData("reserve/pbr/snowcoveredpath/snowcoveredpath_height.png") }
                ),

                "Marble" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/streaked-marble/streaked-marble-albedo2.png") },
                        Texture { it.loadTextureData("reserve/pbr/streaked-marble/streaked-marble-normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/streaked-marble/streaked-marble-roughness1.png") },
                        null,
                        null,
                        null
                ),

                "Onyx Tiles" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_COL_2K.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_NRM_2K.jpg") },
                        Texture { it.loadTextureData("reserve/pbr/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_REFL_2K.jpg") },
                        null,
                        null,
                        Texture { it.loadTextureData("reserve/pbr/TilesOnyxOpaloBlack001/TilesOnyxOpaloBlack001_DISP_2K.jpg") }
                ),

                "Scuffed Titanium" to MaterialMaps(
                        Texture { it.loadTextureData("reserve/pbr/Titanium-Scuffed/Titanium-Scuffed_basecolor.png") },
                        Texture { it.loadTextureData("reserve/pbr/Titanium-Scuffed/Titanium-Scuffed_normal.png") },
                        Texture { it.loadTextureData("reserve/pbr/Titanium-Scuffed/Titanium-Scuffed_roughness.png") },
                        Texture { it.loadTextureData("reserve/pbr/Titanium-Scuffed/Titanium-Scuffed_metallic.png") },
                        null,
                        null
                ),

                "Water Worn Stone" to MaterialMaps(
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