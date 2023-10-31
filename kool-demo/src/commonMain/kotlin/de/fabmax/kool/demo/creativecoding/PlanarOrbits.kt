package de.fabmax.kool.demo.creativecoding

import de.fabmax.kool.demo.LabeledSwitch
import de.fabmax.kool.demo.MenuSlider2
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.remember
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.multiShape
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import kotlin.math.roundToInt

class PlanarOrbits(resources: CreativeCodingDemo.Resources) : CreativeContent("Planar orbits") {

    private var settings = Settings()

    private val sunMesh = createMesh(resources)
    private val orbitMeshX = createMesh(resources)
    private val orbitMeshY = createMesh(resources)
    private val orbitMeshZ = createMesh(resources)

    private val orbitGroupX = Node().apply { addNode(orbitMeshX) }
    private val orbitGroupY = Node().apply { addNode(orbitMeshY) }
    private val orbitGroupZ = Node().apply { addNode(orbitMeshZ) }

    init {
        rebuildSun()
        rebuildOrbits()

        onUpdate += {
            if (settings.rotateOrbits) {
                orbitGroupX.transform.rotate(5f.deg * Time.deltaT, Vec3f.X_AXIS)
                orbitGroupY.transform.rotate(5f.deg * Time.deltaT, Vec3f.Y_AXIS)
                orbitGroupZ.transform.rotate(5f.deg * Time.deltaT, Vec3f.Z_AXIS)
            }
        }
    }

    private fun createMesh(resources: CreativeCodingDemo.Resources): Mesh = addMesh(
        Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.METAL_ROUGH
    ) {
        shader = KslPbrShader {
            color { vertexColor() }
            metallic { vertexProperty(Attribute.METAL_ROUGH, 0) }
            roughness { vertexProperty(Attribute.METAL_ROUGH, 1) }
            shadow { addShadowMaps(resources.shadowMaps) }
            imageBasedAmbientColor(resources.imageEnv.irradianceMap)
            reflectionMap = resources.imageEnv.reflectionMap
        }
    }

    private fun rebuildSun() = sunMesh.generate {
        color = MdColor.AMBER.toLinear()
        icoSphere {
            steps = 4
            radius = settings.sunRadius
            metallic = 1f
            roughness = 0.5f
        }
    }

    private fun rebuildOrbits() {
        rebuildOrbitX()
        rebuildOrbitY()
        rebuildOrbitZ()
    }

    private fun rebuildOrbitX() {
        orbitMeshX.generate {
            rotate(90f.deg, Vec3f.Z_AXIS)
            generateOrbit(settings.innerRadiusX, xGradient)
        }
    }

    private fun rebuildOrbitY() {
        orbitMeshY.generate {
            generateOrbit(settings.innerRadiusY, yGradient)
        }
    }

    private fun rebuildOrbitZ() {
        orbitMeshZ.generate {
            rotate(90f.deg, Vec3f.X_AXIS)
            generateOrbit(settings.innerRadiusZ, zGradient)
        }
    }

    private fun MeshBuilder.generateOrbit(innerRadius: Float, gradient: ColorGradient) {
        var orbitRadius = innerRadius
        for (i in 0 until settings.numRings) {
            generateRing(orbitRadius, gradient)
            orbitRadius += 6f + (10f + randomF(-2f, 6f) * settings.randomness) * settings.gap
        }
        geometry.generateNormals()
    }

    private fun MeshBuilder.generateRing(radius: Float, gradient: ColorGradient) {
        val arcStep = 3f
        val startAngle = randomF(0f, 360f) * settings.randomness
        var sweep = 0f
        while (sweep < 360f) {
            val gap = (10f + randomF(0f, 15f) * settings.randomness) * settings.gap
            val width = 6f + randomF(-2f, 6f) * settings.randomness
            val height = 2f + randomF(-1.5f, 4f) * settings.randomness
            val steps = 10 + (randomI(-9..20) * settings.randomness).roundToInt()

            val p = smoothStep(0.5f, 5f, height)
            color = gradient.getColor(p)
            metallic = 0.7f - p * 0.7f
            roughness = 0.3f + p * 0.7f

            if (sweep + gap + arcStep * steps > 360f) {
                val subStep = (360f - sweep - gap) / steps
                if (subStep > 0f) {
                    generateArc(radius, width, height, sweep + startAngle, subStep, steps)
                }
                break
            }
            generateArc(radius, width, height, sweep + startAngle, arcStep, steps)
            sweep += arcStep * steps + gap
        }
    }

    private fun MeshBuilder.generateArc(
        radius: Float,
        width: Float,
        height: Float,
        startAngle: Float,
        arcStep: Float,
        numSteps: Int
    ) {
        transform.push()
        rotate(startAngle.deg, Vec3f.NEG_Y_AXIS)

        val box = boxProfile(radius, width, height)
        box.sampleAndFillBottom()

        box.sample(connect = false)
        for (i in 0 until numSteps) {
            rotate(arcStep.deg, Vec3f.NEG_Y_AXIS)
            box.sample()
        }
        box.sampleAndFillTop()
        transform.pop()
    }

    private fun MeshBuilder.boxProfile(radius: Float, width: Float, height: Float) = profile {
        val halfW = width * 0.5f
        val halfH = height * 0.5f

        multiShape {
            simpleShape(false) {
                xy(radius - halfW, -halfH)
                xy(radius + halfW, -halfH)
            }
            simpleShape(false) {
                xy(radius + halfW, -halfH)
                xy(radius + halfW, halfH)
            }
            simpleShape(false) {
                xy(radius + halfW, halfH)
                xy(radius - halfW, halfH)
            }
            simpleShape(false) {
                xy(radius - halfW, halfH)
                xy(radius - halfW, -halfH)
            }
        }
    }

    override fun UiScope.settingsMenu() {
        val rotateOrbits = remember(settings.rotateOrbits)
        var sunRadius by remember(settings.sunRadius)
        var innerRadiusX by remember(settings.innerRadiusX)
        var innerRadiusY by remember(settings.innerRadiusY)
        var innerRadiusZ by remember(settings.innerRadiusZ)
        var numRings by remember(settings.numRings)
        var gap by remember(settings.gap)
        var randomness by remember(settings.randomness)

        LabeledSwitch("Rotate orbits:", rotateOrbits) {
            settings = settings.copy(rotateOrbits = it)
        }
        MenuSlider2("Sun radius:", sunRadius, 0f, 100f, CreativeCodingDemo.txtFormatInt) {
            sunRadius = it
            settings = settings.copy(sunRadius = sunRadius)
            rebuildSun()
        }
        MenuSlider2("Inner radius X:", innerRadiusX, 50f, 200f, CreativeCodingDemo.txtFormatInt) {
            innerRadiusX = it
            settings = settings.copy(innerRadiusX = innerRadiusX)
            rebuildOrbitX()
        }
        MenuSlider2("Inner radius Y:", innerRadiusY, 50f, 200f, CreativeCodingDemo.txtFormatInt) {
            innerRadiusY = it
            settings = settings.copy(innerRadiusY = innerRadiusY)
            rebuildOrbitY()
        }
        MenuSlider2("Inner radius Z:", innerRadiusZ, 50f, 200f, CreativeCodingDemo.txtFormatInt) {
            innerRadiusZ = it
            settings = settings.copy(innerRadiusZ = innerRadiusZ)
            rebuildOrbitZ()
        }
        MenuSlider2("Number of rings:", numRings.toFloat(), 4f, 20f, CreativeCodingDemo.txtFormatInt) {
            numRings = it.roundToInt()
            settings = settings.copy(numRings = numRings)
            rebuildOrbits()
        }
        MenuSlider2("Gap size:", gap, 0f, 1f) {
            gap = it
            settings = settings.copy(gap = gap)
            rebuildOrbits()
        }
        MenuSlider2("Randomness:", randomness, 0f, 1f) {
            randomness = it
            settings = settings.copy(randomness = randomness)
            rebuildOrbits()
        }
    }

    private data class Settings(
        val rotateOrbits: Boolean = false,
        val sunRadius: Float = 50f,
        val innerRadiusX: Float = 70f,
        val innerRadiusY: Float = 80f,
        val innerRadiusZ: Float = 90f,
        val numRings: Int = 12,
        val gap: Float = 0.4f,
        val randomness: Float = 0.5f
    )

    companion object {
        val xGradient = ColorGradient(MdColor.DEEP_ORANGE tone 800, MdColor.DEEP_ORANGE tone 100, toLinear = true)
        val yGradient = ColorGradient(MdColor.LIGHT_GREEN tone 800, MdColor.LIGHT_GREEN tone 100, toLinear = true)
        val zGradient = ColorGradient(MdColor.BLUE tone 800, MdColor.BLUE tone 100, toLinear = true)
    }
}