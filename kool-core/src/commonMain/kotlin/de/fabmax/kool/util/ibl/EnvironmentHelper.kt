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
        val cubeTex = CubeMapTexture("singleColorEnv-$color", props) {
            CubeMapTextureData(bgColor, bgColor, bgColor, bgColor, bgColor, bgColor)
        }

        val brdfLutPass = BrdfLutPass(scene)
        val brdfLut = Texture("singleColorEnv-brdf", brdfLutPass.config.colorAttachments[0].getTextureProps(false))
        brdfLutPass.copyTargetsColor += brdfLut

        val maps = EnvironmentMaps(cubeTex, cubeTex, brdfLut)
        if (autoDispose) {
            scene.onDispose += {
                maps.dispose()
            }
        }
        return maps
    }

    fun gradientColorEnvironment(scene: Scene, gradient: ColorGradient, ctx: KoolContext, autoDispose: Boolean = true): EnvironmentMaps {
        val gradientPass = GradientEnvGenerator(gradient, ctx)

        val irrMapPass = IrradianceMapPass(scene, gradientPass.colorTexture!!)
        irrMapPass.dependsOn(gradientPass)
        val irrMap = CubeMapTexture("gradientEnv-irradiance", irrMapPass.config.colorAttachments[0].getTextureProps(false))
        irrMapPass.copyTargetsColor += irrMap

        val reflMapPass = ReflectionMapPass(scene, gradientPass.colorTexture!!)
        reflMapPass.dependsOn(gradientPass)
        val reflMap = CubeMapTexture("gradientEnv-reflection", reflMapPass.config.colorAttachments[0].getTextureProps(true))
        reflMapPass.copyTargetsColor += reflMap

        val brdfLutPass = BrdfLutPass(scene)
        val brdfLut = Texture("gradientEnv-brdf", brdfLutPass.config.colorAttachments[0].getTextureProps(false))
        brdfLutPass.copyTargetsColor += brdfLut

        val maps = EnvironmentMaps(irrMap, reflMap, brdfLut)
        if (autoDispose) {
            scene.onDispose += {
                maps.dispose()
            }
        }
        return maps
    }

    suspend fun hdriEnvironment(scene: Scene, hdriPath: String, assetManager: AssetManager, autoDispose: Boolean = true): EnvironmentMaps {
        val hdriTexProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = true)
        val hdri = assetManager.loadAndPrepareTexture(hdriPath, hdriTexProps)
        return hdriEnvironment(scene, hdri, autoDispose)
    }

    fun hdriEnvironment(scene: Scene, hdri: Texture, autoDispose: Boolean = true): EnvironmentMaps {
        val hdriPass = HdriEnvGenerator(scene, hdri, 256)
        val hdriCube = hdriPass.colorTexture!!

        val brdfLutPass = BrdfLutPass(scene)
        val brdfLut = Texture("hdriEnv-brdf", brdfLutPass.config.colorAttachments[0].getTextureProps(false))
        brdfLutPass.copyTargetsColor += brdfLut

        val irrMapPass = IrradianceMapPass(scene, hdriCube)
        irrMapPass.dependsOn(hdriPass)
        val irrMap = CubeMapTexture("hdriEnv-irradiance", irrMapPass.config.colorAttachments[0].getTextureProps(false))
        irrMapPass.copyTargetsColor += irrMap

        val reflMapPass = ReflectionMapPass(scene, hdriCube)
        reflMapPass.dependsOn(hdriPass)
        val reflMap = CubeMapTexture("hdriEnv-reflection", reflMapPass.config.colorAttachments[0].getTextureProps(true))
        reflMapPass.copyTargetsColor += reflMap

        val maps = EnvironmentMaps(irrMap, reflMap, brdfLut)
        if (autoDispose) {
            scene.onDispose += {
                maps.dispose()
            }
        }
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
