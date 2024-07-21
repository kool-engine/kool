package de.fabmax.kool.demo.creativecoding

import de.fabmax.kool.demo.MenuSlider2
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.remember
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.ColorGradient
import kotlin.math.*

class Hexagons(val resources: CreativeCodingDemo.Resources) : CreativeContent("Hexagons") {

    private var settings = Settings()

    private val mesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.METAL_ROUGH).apply {
        shader = KslPbrShader {
            pipeline { cullMethod = CullMethod.NO_CULLING }
            color { vertexColor() }
            metallic { vertexProperty(Attribute.METAL_ROUGH, 0) }
            roughness { vertexProperty(Attribute.METAL_ROUGH, 1) }
            lighting {
                addShadowMaps(resources.shadowMaps)
                imageBasedAmbientLight(resources.imageEnv.irradianceMap)
            }
            reflectionMap = resources.imageEnv.reflectionMap

            modelCustomizer = ::darkenBacksides
        }
    }

    init {
        addNode(mesh)
        rebuildMesh(settings)
    }

    private fun rebuildMesh(settings: Settings) = mesh.apply {
        this@Hexagons.settings = settings

        // (re-)generate mesh geometry
        generate {
            var centerY = 0f

            // orbit loop: builds one hexagon ring per iteration
            for (orbitI in 0 until settings.numHexagonsOrbit * settings.numTurns) {
                val totalProgress = orbitI.toFloat() / (settings.numHexagonsOrbit * settings.numTurns)
                val ringRotationOffset = if (orbitI % 2 == 0) 0.5f else 0f

                withTransform {
                    val orbitAngle = orbitI * settings.orbitStep

                    // compute current scale: settings.endScale at start, 1 at center, back to settings.endScale at end
                    val scaleW = abs(totalProgress - 0.5f) * 2f
                    val scale = settings.endScale * scaleW + (1f - scaleW)

                    // move cursor to the center of the current ring
                    centerY += (1f / settings.numHexagonsOrbit) * settings.slope * scale
                    translate(0f, centerY, 0f)
                    scale(scale)
                    rotate(orbitAngle.deg, Vec3f.Y_AXIS)
                    translate(0f, 0f, settings.orbitRadius)
                    rotate(settings.twistAngle.deg, Vec3f.Z_AXIS)

                    // belt / ring loop: adds a single hexagon per iteration
                    for (beltI in 0 until settings.numHexagonsBelt) {
                        withTransform {
                            val beltAngle = (beltI + ringRotationOffset) * settings.beltStep
                            val radiusScale = cos(beltAngle.toRad()) * 0.5f
                            val randomPos = settings.beltRadius + randomF(-0.1f, 0.1f) * settings.beltRadius * settings.randomness

                            // compute color value: 0f to 1f depending on belt and orbit angle
                            val colorValue = sin(
                                (orbitAngle * 1.7f + beltAngle + randomF(0f, 60f) * settings.randomness).toRad()
                            ) * 0.5f + 0.5f

                            // set material properties
                            roughness = (0.5f * colorValue + randomF(-0.5f, 0.5f) * settings.randomness).clamp()
                            metallic = (1f - colorValue + randomF(-1f, 1f) * settings.randomness).clamp()
                            color = ColorGradient.PLASMA.getColor(colorValue).toLinear()

                            // move cursor to the center of the current hexagon
                            rotate(beltAngle.deg, Vec3f.X_AXIS)
                            translate(0f, 0f, randomPos)
                            scale(1f + radiusScale, 1f, 1f)

                            // add a solid 6-sided "circle" aka hexagon using the current material properties
                            circle {
                                steps = 6
                                radius = settings.hexagonRadius
                            }
                        }
                    }
                }
            }
        }

        // update parent group transform, so that the mesh is centered
        transform.setIdentity()
        transform.translate(0f, geometry.bounds.size.y * -0.5f + settings.beltRadius * settings.endScale, 0f)
    }

    override fun UiScope.settingsMenu() {
        var orbitRadius by remember(settings.orbitRadius)
        var beltRadius by remember(settings.beltRadius)
        var numberOfHexagons by remember(settings.numHexagonsBelt)
        var hexagonScale by remember(settings.hexagonScale)
        var slope by remember(settings.slope)
        var numTurns by remember(settings.numTurns)
        var endScale by remember(settings.endScale)
        var randomness by remember(settings.randomness)

        MenuSlider2("Orbit radius:", orbitRadius, 50f, 200f, CreativeCodingDemo.txtFormatInt) {
            orbitRadius = it
            rebuildMesh(settings.copy(orbitRadius = orbitRadius))
        }
        MenuSlider2("Belt radius:", beltRadius, 10f, 150f, CreativeCodingDemo.txtFormatInt) {
            beltRadius = it
            rebuildMesh(settings.copy(beltRadius = beltRadius))
        }
        MenuSlider2("Number of hexagons:", numberOfHexagons.toFloat(), 8f, 40f, CreativeCodingDemo.txtFormatInt) {
            numberOfHexagons = it.roundToInt()
            rebuildMesh(settings.copy(numHexagonsBelt = numberOfHexagons))
        }
        MenuSlider2("Hexagon size:", hexagonScale, 0.5f, 2f) {
            hexagonScale = it
            rebuildMesh(settings.copy(hexagonScale = hexagonScale))
        }
        MenuSlider2("Slope:", slope, 0f, 200f, CreativeCodingDemo.txtFormatInt) {
            slope = it
            rebuildMesh(settings.copy(slope = slope))
        }
        MenuSlider2("Number of turns:", numTurns.toFloat(), 1f, 10f, CreativeCodingDemo.txtFormatInt) {
            numTurns = it.roundToInt()
            rebuildMesh(settings.copy(numTurns = numTurns))
        }
        MenuSlider2("Tail scale:", endScale, 0f, 2f) {
            endScale = it
            rebuildMesh(settings.copy(endScale = endScale))
        }
        MenuSlider2("Randomness:", randomness, 0f, 1f) {
            randomness = it
            rebuildMesh(settings.copy(randomness = randomness))
        }
    }

    private fun darkenBacksides(prog: KslProgram) = prog.run {
        fragmentStage {
            main {
                val colorPort = getFloat4Port("baseColor")
                val modColor = float4Var(colorPort.input.input)
                `if`(!inIsFrontFacing) {
                    modColor.rgb set modColor.rgb * 0.1f.const
                }
                colorPort.input(modColor)
            }
        }
    }

    private data class Settings(
        val orbitRadius: Float = 130f,
        val beltRadius: Float = 50f,
        val numHexagonsBelt: Int = 20,
        val hexagonScale: Float = 1.15f,
        val slope: Float = 125f,
        val numTurns: Int = 5,
        val endScale: Float = 0.1f,
        val randomness: Float = 0.1f
    ) {
        val orbitCircumference = orbitRadius * 2f * PI.toFloat()
        val beltCircumference = beltRadius * 2f * PI.toFloat()
        val hexagonRadius = 0.5f * beltCircumference / numHexagonsBelt * hexagonScale
        val numHexagonsOrbit = (1.2f * orbitCircumference / beltCircumference * numHexagonsBelt).roundToInt() and 1.inv()

        val orbitStep = 360f / numHexagonsOrbit
        val beltStep = 360f / numHexagonsBelt
        val twistAngle = 10f * slope / orbitRadius
    }
}