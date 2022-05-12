package de.fabmax.kool.pipeline.ibl

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

        val maps = EnvironmentMaps(cubeTex, cubeTex)
        if (autoDispose) {
            scene.onDispose += {
                maps.dispose()
            }
        }
        return maps
    }

    suspend fun gradientColorEnvironment(scene: Scene, gradient: ColorGradient, ctx: KoolContext, autoDispose: Boolean = true): EnvironmentMaps {
        val gradientTex = GradientCubeGenerator.makeGradientTex(gradient, ctx)
        val gradientPass = GradientCubeGenerator(scene, gradientTex, ctx)
        if (autoDispose) {
            scene.onDispose += {
                gradientTex.dispose()
            }
        }
        return renderPassEnvironment(scene, gradientPass, autoDispose)
    }

    suspend fun hdriEnvironment(scene: Scene, hdriPath: String, assetManager: AssetManager, autoDispose: Boolean = true, brightness: Float = 1f): EnvironmentMaps {
        val hdriTexProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = false, maxAnisotropy = 1)
        val hdri = assetManager.loadAndPrepareTexture(hdriPath, hdriTexProps)
        return hdriEnvironment(scene, hdri, autoDispose, brightness)
    }

    fun hdriEnvironment(scene: Scene, hdri: Texture2d, autoDispose: Boolean = true, brightness: Float = 1f): EnvironmentMaps {
        val rgbeDecoder = RgbeDecoder(scene, hdri, brightness)
        if (autoDispose) {
            scene.onDispose += {
                hdri.dispose()
            }
        }
        return renderPassEnvironment(scene, rgbeDecoder, autoDispose)
    }

    fun renderPassEnvironment(scene: Scene, renderPass: OffscreenRenderPass, autoDispose: Boolean = true): EnvironmentMaps {
        val tex = when (renderPass) {
            is OffscreenRenderPassCube -> renderPass.colorTexture!!
            is OffscreenRenderPass2d -> renderPass.colorTexture!!
            else -> throw IllegalArgumentException("Supplied OffscreenRenderPass must be OffscreenRenderPassCube or OffscreenRenderPass2d")
        }
        val irrMapPass = IrradianceMapPass.irradianceMap(scene, tex)
        val reflMapPass = ReflectionMapPass.reflectionMap(scene, tex)

        irrMapPass.dependsOn(renderPass)
        reflMapPass.dependsOn(renderPass)

        val maps = EnvironmentMaps(irrMapPass.copyColor(), reflMapPass.copyColor())
        if (autoDispose) {
            scene.onDispose += {
                maps.dispose()
            }
        }

        scene.addOffscreenPass(renderPass)
        scene.addOffscreenPass(irrMapPass)
        scene.addOffscreenPass(reflMapPass)
        return maps
    }
}

class EnvironmentMaps(val irradianceMap: TextureCube, val reflectionMap: TextureCube) {
    fun dispose() {
        irradianceMap.dispose()
        reflectionMap.dispose()
    }
}
