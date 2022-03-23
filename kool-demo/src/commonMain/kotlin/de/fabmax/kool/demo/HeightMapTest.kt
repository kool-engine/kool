package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.blinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.defaultCamTransform
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.HeightMap

class HeightMapTest : DemoScene("Height Map Test") {

    private lateinit var heightMap: HeightMap
    private lateinit var colorTex: Texture2d
    private lateinit var normalTex: Texture2d
    private lateinit var ibl: EnvironmentMaps

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        heightMap = HeightMap.fromRawData(loadAsset("${Demo.heightMapPath}/terrain.raw")!!, 20f)
        // more or less the same, but falls back to 8-bit height-resolution in javascript
        //heightMap = HeightMap.fromTextureData2d(loadTextureData2d("${Demo.heightMapPath}/terrain.png", TexFormat.R_F16), 20f)

        colorTex = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png")
        normalTex = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")

        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.envMapBasePath}/blaubeuren_outskirts_1k.rgbe.png", this)
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultCamTransform()

        lighting.apply {
            singleLight {
                setDirectional(Vec3f(-1f, -1f, -1f))
                setColor(Color.WHITE, 1f)
            }
        }

        +textureMesh(isNormalMapped = true) {
            generate {
                grid {
                    sizeX = 50f
                    sizeY = 50f
                    useHeightMap(heightMap)
                    texCoordScale.set(25f, 25f)
                }
            }

            shader = blinnPhongShader {
                color {
                    addTextureColor(colorTex)
                }
                normalMapping {
                    setNormalMap(normalTex)
                }
                shadow {
                    addShadowMap(CascadedShadowMap(this@setupMainScene, 0))
                }
                imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                specularStrength = 0.5f
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            }
        }
    }
}