package de.fabmax.kool.util.ibl

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient

object EnvironmentHelper {

    fun singleColorEnvironment(scene: Scene, color: Color, autoDispose: Boolean = true): EnvironmentMaps {
        val bgColor = TextureData2d.singleColor(color.toLinear())
        val props = TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE, addressModeW = AddressMode.CLAMP_TO_EDGE,
                minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST,
                mipMapping = false, maxAnisotropy = 1
        )
        val cubeTex = TextureCube(props, "singleColorEnv-$color") {
            TextureDataCube(bgColor, bgColor, bgColor, bgColor, bgColor, bgColor)
        }

        val brdfLutPass = BrdfLutPass(scene)
        val brdfLut = Texture2d(brdfLutPass.config.colorAttachments[0].getTextureProps(false), "singleColorEnv-brdf")
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

    suspend fun gradientColorEnvironment(scene: Scene, gradient: ColorGradient, ctx: KoolContext, autoDispose: Boolean = true): EnvironmentMaps {
        val gradientTex = GradientCubeGenerator.makeGradientTex(gradient, ctx)
        val gradientPass = GradientCubeGenerator(scene, gradientTex, ctx)
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

    suspend fun hdriEnvironment(scene: Scene, hdriPath: String, assetManager: AssetManager, autoDispose: Boolean = true, brightness: Float = 1f): EnvironmentMaps {
        val hdriTexProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = false, maxAnisotropy = 1)
        val hdri = assetManager.loadAndPrepareTexture(hdriPath, hdriTexProps)
        return hdriEnvironment(scene, hdri, autoDispose, brightness)
    }

    fun hdriEnvironment(scene: Scene, hdri: Texture2d, autoDispose: Boolean = true, brightness: Float = 1f): EnvironmentMaps {
        val rgbeDecoder = RgbeDecoder(scene, hdri, brightness)
        val irrMapPass = IrradianceMapPass.irradianceMapFromHdri(scene, rgbeDecoder.colorTexture!!)
        val reflMapPass = ReflectionMapPass.reflectionMapFromHdri(scene, rgbeDecoder.colorTexture!!)
        val brdfLutPass = BrdfLutPass(scene)

        irrMapPass.dependsOn(rgbeDecoder)
        reflMapPass.dependsOn(rgbeDecoder)

        val maps = EnvironmentMaps(irrMapPass.copyColor(), reflMapPass.copyColor(), brdfLutPass.copyColor())
        if (autoDispose) {
            scene.onDispose += {
                maps.dispose()
                hdri.dispose()
            }
        }

        scene.addOffscreenPass(rgbeDecoder)
        scene.addOffscreenPass(irrMapPass)
        scene.addOffscreenPass(reflMapPass)
        scene.addOffscreenPass(brdfLutPass)

        return maps
    }

}

class EnvironmentMaps(val irradianceMap: TextureCube, val reflectionMap: TextureCube, val brdfLut: Texture2d) {
    fun dispose() {
        irradianceMap.dispose()
        reflectionMap.dispose()
        brdfLut.dispose()
    }
}
