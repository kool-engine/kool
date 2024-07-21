package de.fabmax.kool.demo.creativecoding

import de.fabmax.kool.demo.MenuSlider2
import de.fabmax.kool.math.Mat4fStack
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.remember
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MutableColor
import kotlin.math.*

class LargeSpheres(val resources: CreativeCodingDemo.Resources) : CreativeContent("Large Spheres") {

    private val instances = MeshInstanceList(listOf(
        Attribute.INSTANCE_MODEL_MAT,
        Attribute.INSTANCE_COLOR
    ))

    private val mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, instances = instances).apply {
        shader = KslPbrShader {
            vertices { isInstanced = true }
            color { instanceColor() }
            metallic(0f)
            roughness(0.25f)
            lighting {
                addShadowMaps(resources.shadowMaps)
                imageBasedAmbientLight(resources.imageEnv.irradianceMap)
            }
            reflectionMap = resources.imageEnv.reflectionMap
        }

        // generate geometry for a single sphere, multiple spheres will be displayed by using instancing
        generate {
            icoSphere {
                steps = 3
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

        // orbit loop: generates a single string of spheres per iteration
        for (iString in 0 until settings.numStrings) {
            val orbitProgress = iString.toFloat() / settings.numStrings

            // move transform matrix to start position
            val transform = Mat4fStack()
            transform
                .rotate(360f.deg * orbitProgress, Vec3f.Z_AXIS)
                .translate(settings.innerRadius, 0f, 0f)
                .scale(7f)

            var zCurvature = settings.startCurvature
            val yCurvature = cos(2f * PI.toFloat() * orbitProgress) * settings.planeCurvature

            // string loop, computes a single sphere instance per iteration
            for (iSphere in 0 until settings.spheresPerString) {
                val stringProgress = iSphere.toFloat() / settings.spheresPerString
                val sphereSize = 0.3f + sin((stringProgress * 0.9f + 0.1f).pow(0.6f) * PI.toFloat()) * 0.7f
                val color = MutableColor(rainbowGradient.getColor((stringProgress + orbitProgress) % 1f).toLinear())

                // add sphere instance properties: transform matrix + sphere color
                instances.addInstance {
                    transform.push().scale(settings.sphereSize * sphereSize)
                    transform.putTo(this)
                    transform.pop()
                    color.putTo(this)
                }

                // forward transform matrix to next sphere position
                transform
                    .translate(2.5f * sphereSize, 0f, 0f)
                    .rotate(zCurvature.deg, Vec3f.Z_AXIS)
                    .rotate(yCurvature.deg, Vec3f.Y_AXIS)
                zCurvature += settings.curvatureInc
            }
        }
    }

    override fun UiScope.settingsMenu() {
        var innerRadius by remember(settings.innerRadius)
        var sphereSize by remember(settings.sphereSize)
        var numStrings by remember(settings.numStrings)
        var spheresPerString by remember(settings.spheresPerString)
        var startCurvature by remember(settings.startCurvature)
        var curvatureInc by remember(settings.curvatureInc)
        var planeCurvature by remember(settings.planeCurvature)

        MenuSlider2("Inner radius:", innerRadius, 0f, 200f, CreativeCodingDemo.txtFormatInt) {
            innerRadius = it
            rebuildInstances(settings.copy(innerRadius = innerRadius))
        }
        MenuSlider2("Sphere size:", sphereSize, 0.1f, 3f) {
            sphereSize = it
            rebuildInstances(settings.copy(sphereSize = sphereSize))
        }
        MenuSlider2("Number of strings:", numStrings.toFloat(), 2f, 32f, CreativeCodingDemo.txtFormatInt) {
            numStrings = it.roundToInt()
            rebuildInstances(settings.copy(numStrings = numStrings))
        }
        MenuSlider2("Number of spheres:", spheresPerString.toFloat(), 5f, 50f, CreativeCodingDemo.txtFormatInt) {
            spheresPerString = it.roundToInt()
            rebuildInstances(settings.copy(spheresPerString = spheresPerString))
        }
        MenuSlider2("String curvature:", startCurvature, 0f, 30f) {
            startCurvature = it
            rebuildInstances(settings.copy(startCurvature = startCurvature))
        }
        MenuSlider2("Curvature increment:", curvatureInc, 0f, 5f) {
            curvatureInc = it
            rebuildInstances(settings.copy(curvatureInc = curvatureInc))
        }
        MenuSlider2("Plane curvature:", planeCurvature, 0f, 30f) {
            planeCurvature = it
            rebuildInstances(settings.copy(planeCurvature = planeCurvature))
        }
    }

    private data class Settings(
        val innerRadius: Float = 50f,
        val sphereSize: Float = 1.5f,
        val numStrings: Int = 8,
        val spheresPerString: Int = 40,
        val startCurvature: Float = 5f,
        val curvatureInc: Float = 1f,
        val planeCurvature: Float = 5f
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