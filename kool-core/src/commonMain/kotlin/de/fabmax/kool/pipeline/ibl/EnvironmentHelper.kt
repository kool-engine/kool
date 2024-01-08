package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.RenderLoop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EnvironmentHelper {

    fun singleColorEnvironment(color: Color): EnvironmentMaps {
        val bgColor = TextureData2d.singleColor(color.toLinear())
        val props = TextureProps(
            generateMipMaps = false,
            defaultSamplerSettings = SamplerSettings().nearest()
        )
        val cubeTex = TextureCube(props, "singleColorEnv-$color") {
            TextureDataCube(bgColor, bgColor, bgColor, bgColor, bgColor, bgColor)
        }
        return EnvironmentMaps(cubeTex, cubeTex)
    }

    fun gradientColorEnvironment(gradient: ColorGradient): EnvironmentMaps {
        val scene = KoolSystem.requireContext().backgroundScene
        val gradientTex = GradientTexture(gradient)
        val gradientPass = GradientCubeGenerator(scene, gradientTex)
        gradientTex.releaseWith(gradientPass)
        return renderPassEnvironment(gradientPass)
    }

    suspend fun hdriEnvironment(hdriPath: String, brightness: Float = 1f): EnvironmentMaps {
        val samplerSettings = SamplerSettings().nearest()
        val hdriTexProps = TextureProps(generateMipMaps = false, defaultSamplerSettings = samplerSettings)
        val hdri = Assets.loadTexture2d(hdriPath, hdriTexProps)
        return withContext(Dispatchers.RenderLoop) {
            hdriEnvironment(hdri, brightness)
        }
    }

    fun hdriEnvironment(
        hdri: Texture2d,
        brightness: Float = 1f,
        releaseHdriTexAfterConversion: Boolean = true
    ): EnvironmentMaps {
        val scene = KoolSystem.requireContext().backgroundScene
        val rgbeDecoder = RgbeDecoder(scene, hdri, brightness)
        if (releaseHdriTexAfterConversion) {
            hdri.releaseWith(rgbeDecoder)
        }
        return renderPassEnvironment(rgbeDecoder)
    }

    private fun renderPassEnvironment(renderPass: OffscreenRenderPass): EnvironmentMaps {
        val tex = when (renderPass) {
            is OffscreenRenderPassCube -> renderPass.colorTexture!!
            is OffscreenRenderPass2d -> renderPass.colorTexture!!
            else -> throw IllegalArgumentException("Supplied OffscreenRenderPass must be OffscreenRenderPassCube or OffscreenRenderPass2d")
        }
        val scene = KoolSystem.requireContext().backgroundScene
        val irrMapPass = IrradianceMapPass.irradianceMap(scene, tex)
        val reflMapPass = ReflectionMapPass.reflectionMap(scene, tex)

        irrMapPass.dependsOn(renderPass)
        reflMapPass.dependsOn(renderPass)

        val maps = EnvironmentMaps(irrMapPass.copyColor(), reflMapPass.copyColor())
        scene.addOffscreenPass(renderPass)
        scene.addOffscreenPass(irrMapPass)
        scene.addOffscreenPass(reflMapPass)
        return maps
    }
}

class EnvironmentMaps(val irradianceMap: TextureCube, val reflectionMap: TextureCube) : BaseReleasable() {
    override fun release() {
        irradianceMap.release()
        if (irradianceMap !== reflectionMap) {
            reflectionMap.release()
        }
        super.release()
    }
}
