package de.fabmax.kool.demo.creativecoding

import de.fabmax.kool.demo.MenuSlider2
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.randomF
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.remember
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt

class SmallSpheres(val resources: CreativeCodingDemo.Resources) : CreativeContent("Small Spheres") {

    private val instances = MeshInstanceList(listOf(
        Attribute.INSTANCE_MODEL_MAT,
        Attribute.INSTANCE_COLOR,
        Attribute.METAL_ROUGH
    ))

    private val mesh = Mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS), instances = instances).apply {
        shader = KslPbrShader {
            vertices { isInstanced = true }
            color { instanceColor() }
            metallic { instanceProperty(Attribute.METAL_ROUGH, 0) }
            roughness { instanceProperty(Attribute.METAL_ROUGH, 1) }
            lighting {
                addShadowMaps(resources.shadowMaps)
                imageBasedAmbientLight(resources.imageEnv.irradianceMap)
            }
            reflectionMap = resources.imageEnv.reflectionMap
        }

        // generate geometry for a single sphere, multiple spheres will be displayed by using instancing
        generate {
            icoSphere {
                steps = 1
            }
        }
    }

    private var settings = Settings()

    init {
        addNode(mesh)
        rebuildInstances(settings)
    }

    private fun rebuildInstances(settings: Settings) {
        this.settings = settings

        // (re-)generate instances (the mesh geometry itself stays the same)
        instances.clear()

        for (i in 0 until settings.numSpheres) {
            val orbitProgress = i.toFloat() / settings.numSpheresPerOrbit

            // move transform matrix to start position
            val r = settings.innerRadius + orbitProgress * settings.orbitInc
            val h = cos(orbitProgress * 11.9f * PI.toFloat()) * orbitProgress / 20f
            val transform = MutableMat4f()
                .rotate(360f.deg * orbitProgress, Vec3f.Z_AXIS)
                .translate(r, 0f, h * settings.waviness)
                .scale(settings.sphereSize + (1f + randomF(-1f, 1f)) * settings.randomness)

            val color = ColorGradient.RED_WHITE_BLUE.getColor(h + randomF(-1f, 1f) * settings.randomness, -1.5f, 1.5f).toLinear()

            // add sphere instance properties: transform matrix + sphere color
            instances.addInstance {
                transform.putTo(this)
                color.putTo(this)
                // metallic
                put(randomF(0f, 0.5f) * settings.randomness)
                // roughness
                put(0.3f + randomF(0f, 0.5f) * settings.randomness)
            }
        }
    }

    override fun UiScope.settingsMenu() {
        var numSpheres by remember(settings.numSpheres)
        var innerRadius by remember(settings.innerRadius)
        var sphereSize by remember(settings.sphereSize)
        var waviness by remember(settings.waviness)
        var randomness by remember(settings.randomness)

        MenuSlider2("Number of spheres:", numSpheres.toFloat(), 1000f, 8000f, CreativeCodingDemo.txtFormatInt) {
            numSpheres = it.roundToInt()
            rebuildInstances(settings.copy(numSpheres = numSpheres))
        }
        MenuSlider2("Inner radius:", innerRadius, 0f, 200f, CreativeCodingDemo.txtFormatInt) {
            innerRadius = it
            rebuildInstances(settings.copy(innerRadius = innerRadius))
        }
        MenuSlider2("Sphere size:", sphereSize, 0.1f, 5f) {
            sphereSize = it
            rebuildInstances(settings.copy(sphereSize = sphereSize))
        }
        MenuSlider2("Waviness:", waviness, 0f, 20f) {
            waviness = it
            rebuildInstances(settings.copy(waviness = waviness))
        }
        MenuSlider2("Randomness:", randomness, 0f, 1f) {
            randomness = it
            rebuildInstances(settings.copy(randomness = randomness))
        }
    }

    private data class Settings(
        val innerRadius: Float = 50f,
        val sphereSize: Float = 3f,
        val numSpheres: Int = 5000,
        val numSpheresPerOrbit: Int = 150,
        val orbitInc: Float = 3f,
        val waviness: Float = 10f,
        val randomness: Float = 0.15f
    )

    companion object {
        private val rainbowGradient = ColorGradient(
            MdColor.BLUE,
            MdColor.CYAN,
            MdColor.GREEN,
            MdColor.YELLOW,
            MdColor.RED,
            MdColor.PURPLE,
            MdColor.BLUE
        )
    }
}