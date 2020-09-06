package de.fabmax.kool.util.ibl

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient

object EnvironmentHelper {

    fun singleColorEnvironment(scene: Scene, color: Color, autoDispose: Boolean = true): EnvironmentMaps {
        val bgColor = BufferedTextureData.singleColor(color.toLinear())
        val props = TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE, addressModeW = AddressMode.CLAMP_TO_EDGE,
                minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST,
                mipMapping = false, maxAnisotropy = 1
        )
        val cubeTex = CubeMapTexture(props, "singleColorEnv-$color") {
            CubeMapTextureData(bgColor, bgColor, bgColor, bgColor, bgColor, bgColor)
        }

        val brdfLutPass = BrdfLutPass(scene)
        val brdfLut = Texture(brdfLutPass.config.colorAttachments[0].getTextureProps(false), "singleColorEnv-brdf")
        brdfLutPass.copyTargetsColor += brdfLut

        val maps = EnvironmentMaps(cubeTex, cubeTex, brdfLut)
        if (autoDispose) {
            scene.onDispose += {
                maps.dispose()
            }
        }

        scene.addOffscreenPass(brdfLutPass)

        return maps
    }

    fun gradientColorEnvironment(scene: Scene, gradient: ColorGradient, ctx: KoolContext, autoDispose: Boolean = true): EnvironmentMaps {
        val gradientPass = GradientCubeGenerator(scene, gradient, ctx)
        val irrMapPass = IrradianceMapPass.irradianceMapFromCube(scene, gradientPass.colorTexture!!)
        val reflMapPass = ReflectionMapPass.reflectionMapFromCube(scene, gradientPass.colorTexture!!)
        val brdfLutPass = BrdfLutPass(scene)

        irrMapPass.dependsOn(gradientPass)
        reflMapPass.dependsOn(gradientPass)

        val maps = EnvironmentMaps(irrMapPass.copyColor(), reflMapPass.copyColor(), brdfLutPass.copyColor())
        if (autoDispose) {
            scene.onDispose += {
                maps.dispose()
            }
        }

        scene.addOffscreenPass(gradientPass)
        scene.addOffscreenPass(irrMapPass)
        scene.addOffscreenPass(reflMapPass)
        scene.addOffscreenPass(brdfLutPass)

        return maps
    }

    suspend fun hdriEnvironment(scene: Scene, hdriPath: String, assetManager: AssetManager, autoDispose: Boolean = true): EnvironmentMaps {
        val hdriTexProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = false, maxAnisotropy = 1)
        val hdri = assetManager.loadAndPrepareTexture(hdriPath, hdriTexProps)
        return hdriEnvironment(scene, hdri, autoDispose)
    }

    fun hdriEnvironment(scene: Scene, hdri: Texture, autoDispose: Boolean = true): EnvironmentMaps {
        val rgbeDecoder = RgbeDecoder(scene, hdri)
        val irrMapPass = IrradianceMapPass.irradianceMapFromHdri(scene, rgbeDecoder.colorTexture!!)
        val reflMapPass = ReflectionMapPass.reflectionMapFromHdri(scene, rgbeDecoder.colorTexture!!)
        val brdfLutPass = BrdfLutPass(scene)

        irrMapPass.dependsOn(rgbeDecoder)
        reflMapPass.dependsOn(rgbeDecoder)

        val maps = EnvironmentMaps(irrMapPass.copyColor(), reflMapPass.copyColor(), brdfLutPass.copyColor())
        if (autoDispose) {
            scene.onDispose += {
                maps.dispose()
            }
        }

        scene.addOffscreenPass(rgbeDecoder)
        scene.addOffscreenPass(irrMapPass)
        scene.addOffscreenPass(reflMapPass)
        scene.addOffscreenPass(brdfLutPass)

        return maps
    }

}

class EnvironmentMaps(val irradianceMap: CubeMapTexture, val reflectionMap: CubeMapTexture, val brdfLut: Texture) {
    fun dispose() {
        irradianceMap.dispose()
        reflectionMap.dispose()
        brdfLut.dispose()
    }
}
