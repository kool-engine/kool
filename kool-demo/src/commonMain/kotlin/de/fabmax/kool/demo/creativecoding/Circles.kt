package de.fabmax.kool.demo.creativecoding

import de.fabmax.kool.demo.MenuSlider2
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.remember
import de.fabmax.kool.scene.TriangulatedLineMesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class Circles : CreativeContent("Circles") {

    private var settings = Settings()
    private val lineMesh = TriangulatedLineMesh()

    init {
        addNode(lineMesh)
        rebuildLines(settings)
    }

    private fun rebuildLines(settings: Settings) = lineMesh.apply {
        this@Circles.settings = settings
        clear()

        drawOrbit(
            settings.numberOfCircles,
            settings.randomness,
            settings.circleRadius,
            settings.orbitRadius,
            settings.numberOfTwists,
            0f,
            Vec3f(0f, 0f, 1f)
        )

        if (settings.innerRatio > 0f) {
            drawOrbit(
                settings.numberOfCircles,
                settings.randomness,
                settings.circleRadius * settings.innerRatio,
                settings.orbitRadius,
                settings.numberOfTwists,
                0.5f,
                Vec3f(0f, 1f, 0f)
            )
        }
    }

    private fun TriangulatedLineMesh.drawOrbit(
        numberOfCircles: Int,
        randomness: Float,
        circleRadius: Float,
        orbitRadius: Float,
        numberOfTwists: Int,
        gradientOffset: Float,
        startUpVector: Vec3f
    ) {
        val up = MutableVec3f(startUpVector)

        val circleStep = 360f / numberOfCircles
        for (i in 0 until numberOfCircles) {
            val orbitProgress = i.toFloat() / numberOfCircles
            val randomAngle = randomF(-circleStep, circleStep) * randomness
            val randomRadius = circleRadius * randomF() * randomness * 0.5f
            val randomWidth = 3f + randomF(-1f, 1f) * randomness

            val rad = (i * circleStep + randomAngle).toRad()
            val x = cos(rad) * orbitRadius
            val y = sin(rad) * orbitRadius

            addCircle(
                center = Vec3f(x, y, 0f),
                radius = circleRadius + randomRadius,
                upAxis = up,
                color = circleGradient.getColor((orbitProgress + gradientOffset) % 1f),
                width = randomWidth
            )

            up.rotate((circleStep * numberOfTwists * 0.5f).deg, Vec3f.X_AXIS)
        }
    }

    override fun UiScope.settingsMenu() {
        var orbitRadius by remember(settings.orbitRadius)
        var circleRadius by remember(settings.circleRadius)
        var innerRatio by remember(settings.innerRatio)
        var numberOfCircles by remember(settings.numberOfCircles)
        var numberOfTwists by remember(settings.numberOfTwists)
        var randomness by remember(settings.randomness)

        MenuSlider2("Orbit radius:", orbitRadius, 50f, 200f, CreativeCodingDemo.txtFormatInt) {
            orbitRadius = it
            rebuildLines(settings.copy(orbitRadius = orbitRadius))
        }
        MenuSlider2("Circle radius:", circleRadius, 10f, 150f, CreativeCodingDemo.txtFormatInt) {
            circleRadius = it
            rebuildLines(settings.copy(circleRadius = circleRadius))
        }
        MenuSlider2("Inner circle ratio:", innerRatio, 0f, 1f) {
            innerRatio = it
            rebuildLines(settings.copy(innerRatio = innerRatio))
        }
        MenuSlider2("Number of circles:", numberOfCircles.toFloat(), 20f, 500f, CreativeCodingDemo.txtFormatInt) {
            numberOfCircles = it.roundToInt()
            rebuildLines(settings.copy(numberOfCircles = numberOfCircles))
        }
        MenuSlider2("Number of twists:", numberOfTwists.toFloat(), 0f, 20f, CreativeCodingDemo.txtFormatInt) {
            numberOfTwists = it.roundToInt()
            rebuildLines(settings.copy(numberOfTwists = numberOfTwists))
        }
        MenuSlider2("Randomness:", randomness, 0f, 2f) {
            randomness = it
            rebuildLines(settings.copy(randomness = randomness))
        }
    }

    private fun TriangulatedLineMesh.addCircle(
        center: Vec3f,
        radius: Float,
        upAxis: Vec3f,
        color: Color,
        width: Float,
        resolution: Int = 5
    ) {
        val frontAxis = upAxis.ortho(MutableVec3f())
        val rightAxis = upAxis.cross(frontAxis, MutableVec3f())

        val rotation = MutableMat3f()
        rotation.setColumn(0, rightAxis)
        rotation.setColumn(1, frontAxis)
        rotation.setColumn(2, upAxis)

        val transform = MutableMat4f()
        transform.translate(center)
        transform.mulUpperLeft(rotation)

        for (i in 0 .. 360 step resolution) {
            val rad = i.toFloat().toRad()
            val x = cos(rad) * radius
            val y = sin(rad) * radius
            val point = transform.transform(MutableVec3f(x, y, 0f))
            lineTo(point, color, width)
        }
        stroke()
    }

    companion object {
        private val circleGradient = ColorGradient(
            MdColor.BLUE,
            MdColor.CYAN,
            MdColor.GREEN,
            MdColor.YELLOW,
            MdColor.RED,
            MdColor.PURPLE,
            MdColor.BLUE
        )
    }

    private data class Settings(
        val orbitRadius: Float = 125f,
        val circleRadius: Float = 60f,
        val innerRatio: Float = 0.7f,
        val numberOfCircles: Int = 200,
        val numberOfTwists: Int = 7,
        val randomness: Float = 0.05f
    )
}