package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlin.math.cos
import kotlin.math.round

class VehicleUi(ctx: KoolContext) {

    var speedKph: Float = 0f
        set(value) {
            field = value
            speedValue.text = "${round(value).toInt()}"
        }

    var torqueNm: Float = 0f
        set(value) {
            field = value
            torqueValue.text = "${round(value).toInt()}"
        }

    var powerKW: Float = 0f
        set(value) {
            field = value
            powerValue.text = "${round(value).toInt()}"
        }

    var gear: Int = 0
        set(value) {
            field = value
            gearValue.text = when {
                value < 0 -> "Rev"
                value == 0 -> "N"
                value == 1 -> "1st"
                value == 2 -> "2nd"
                value == 3 -> "3rd"
                else -> "${value}th"
            }
        }

    var rpm: Float
        get() = rpmBar.value
        set(value) { rpmBar.value = value }

    var criticalRpm: Float
        get() = rpmBar.criticlaRpm
        set(value) { rpmBar.criticlaRpm = value }

    var maxRpm: Float
        get() = rpmBar.maxRpm
        set(value) { rpmBar.maxRpm = value }

    var throttle: Float
        get() = throttleBar.value
        set(value) { throttleBar.value = value }

    var brake: Float
        get() = brakeBar.value
        set(value) { brakeBar.value = value }

    var steering: Float
        get() = steeringBar.value
        set(value) { steeringBar.value = -value }

    var lateralAcceleration: Float
        get() = gMeter.latAccel
        set(value) { gMeter.latAccel = value }

    var longitudinalAcceleration: Float
        get() = gMeter.longAccel
        set(value) { gMeter.longAccel = value }

    private lateinit var speedValue: Label
    private lateinit var gearValue: Label
    private lateinit var torqueValue: Label
    private lateinit var powerValue: Label

    private lateinit var rpmBar: RpmScale
    private lateinit var throttleBar: VerticalBar
    private lateinit var brakeBar: VerticalBar
    private lateinit var steeringBar: HorizontalBar
    private lateinit var gMeter: GMeter

    var trackTime = 0f
        set(value) {
            field = value
            trackTimeLbl.text = formatTime(value)
        }
    var sec1Time = 0f
        set(value) {
            field = value
            sec1Lbl.text = formatTime(value)
        }
    var sec2Time = 0f
        set(value) {
            field = value
            sec2Lbl.text = formatTime(value)
        }

    private lateinit var trackTimeLbl: Label
    private lateinit var sec1Lbl: Label
    private lateinit var sec2Lbl: Label

    var onToggleSound: (Boolean) -> Unit = { }

    val uiScene = uiScene(dpi = ctx.screenDpi) {
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        val largeFont = uiFont(fontFamily, 89 * scale, uiDpi, ctx, style = Font.ITALIC, chars = "-01234567890.:")
        val midFont = uiFont(fontFamily, 64 * scale, uiDpi, ctx, style = Font.ITALIC, chars = "-01234567890.:")
        val smallFont = uiFont(fontFamily, 19 * scale, uiDpi, ctx, style = Font.ITALIC)

        +container("dashboard") {
            layoutSpec.setOrigin(zero(), zero(), zero())
            layoutSpec.setSize(dps(720f * scale), dps(170f * scale), full())
            ui.setCustom(DashboardComponentUi(this))

            +RpmScale(this@uiScene).apply {
                rpmBar = this
                layoutSpec.setOrigin(dps(10f  * scale), dps(15f * scale), zero())
                layoutSpec.setSize(dps(400f * scale), dps(140f * scale), full())
                maxRpm = 6000f
            }

            +label("speedValue") {
                speedValue = this
                layoutSpec.setOrigin(dps(75f  * scale), dps(15f * scale), zero())
                layoutSpec.setSize(dps(170f * scale), dps(90f * scale), full())
                font.setCustom(largeFont)
                textAlignment = Gravity(Alignment.END, Alignment.END)
                text = "0"
            }
            +label("speedUnit") {
                layoutSpec.setOrigin(dps(220f  * scale), dps(15f * scale), zero())
                layoutSpec.setSize(dps(60f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.START, Alignment.END)
                text = "km/h"
            }

            +label("gearValue") {
                gearValue = this
                layoutSpec.setOrigin(dps(274f  * scale), dps(65f * scale), zero())
                layoutSpec.setSize(dps(75f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.END, Alignment.END)
                text = "N"
            }
            +label("gearLabel") {
                layoutSpec.setOrigin(dps(324f  * scale), dps(65f * scale), zero())
                layoutSpec.setSize(dps(175f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.START, Alignment.END)
                text = "Gear"
            }

            +label("torqueValue") {
                torqueValue = this
                layoutSpec.setOrigin(dps(274f  * scale), dps(40f * scale), zero())
                layoutSpec.setSize(dps(74.5f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.END, Alignment.END)
                text = "0"
            }
            +label("torqueUnit") {
                layoutSpec.setOrigin(dps(324f  * scale), dps(40f * scale), zero())
                layoutSpec.setSize(dps(174.5f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.START, Alignment.END)
                text = "Nm"
            }

            +label("powerValue") {
                powerValue = this
                layoutSpec.setOrigin(dps(274f  * scale), dps(15f * scale), zero())
                layoutSpec.setSize(dps(74f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.END, Alignment.END)
                text = "0"
            }
            +label("powerUnit") {
                layoutSpec.setOrigin(dps(324f  * scale), dps(15f * scale), zero())
                layoutSpec.setSize(dps(174f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.START, Alignment.END)
                text = "kW"
            }

            +VerticalBar("throttleBar", this@uiScene).apply {
                throttleBar = this
                layoutSpec.setOrigin(dps(382f  * scale), dps(15f * scale), zero())
                layoutSpec.setSize(dps(50f * scale), dps(140f * scale), full())
                trackColor = ColorGradient(Color.WHITE.withAlpha(0.5f), Color.MD_ORANGE)
            }

            +VerticalBar("brakeBar", this@uiScene).apply {
                brakeBar = this
                layoutSpec.setOrigin(dps(447f  * scale), dps(15f * scale), zero())
                layoutSpec.setSize(dps(50f * scale), dps(140f * scale), full())
                trackColor = ColorGradient(Color.WHITE.withAlpha(0.5f), Color.MD_DEEP_ORANGE)
            }

            +HorizontalBar("steering", this@uiScene).apply {
                steeringBar = this
                layoutSpec.setOrigin(dps(512f  * scale), dps(15f * scale), zero())
                layoutSpec.setSize(dps(110f * scale), dps(25f * scale), full())
            }

            +GMeter("gMeter", this@uiScene).apply {
                gMeter = this
                layoutSpec.setOrigin(dps(532f  * scale), dps(45f * scale), zero())
                layoutSpec.setSize(dps(110f * scale), dps(110f * scale), full())
            }
        }

        +container("timer") {
            layoutSpec.setSize(dps(600f * scale), dps(110f * scale), full())
            layoutSpec.setOrigin(pcs(50f) - dps(300f * scale), dps(-110f * scale), zero())
            ui.setCustom(TimerComponentUi(this))

            +label("trackTime") {
                trackTimeLbl = this
                layoutSpec.setOrigin(dps(100f), dps(30f * scale), zero())
                layoutSpec.setSize(dps(200f * scale), dps(70f * scale), full())
                font.setCustom(midFont)
                textAlignment = Gravity(Alignment.END, Alignment.END)
                text = "0:00.00"
            }
            +label("Total") {
                layoutSpec.setOrigin(dps(275f * scale), dps(30f * scale), zero())
                layoutSpec.setSize(dps(60f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.START, Alignment.END)
                textColor.setCustom(Color.MD_GREY_400)
            }

            +label("sectorTime1") {
                sec1Lbl = this
                layoutSpec.setOrigin(dps(320f * scale), dps(60f * scale), zero())
                layoutSpec.setSize(dps(120f * scale), dps(45f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.END, Alignment.END)
                text = "-:--.--"
            }
            +label("Sec 1") {
                layoutSpec.setOrigin(dps(420f * scale), dps(60f * scale), zero())
                layoutSpec.setSize(dps(60f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.START, Alignment.END)
                textColor.setCustom(Color.MD_GREY_400)
            }

            +label("sectorTime2") {
                sec2Lbl = this
                layoutSpec.setOrigin(dps(320f * scale), dps(30f * scale), zero())
                layoutSpec.setSize(dps(120f * scale), dps(45f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.END, Alignment.END)
                text = "-:--.--"
            }
            +label("Sec 2") {
                layoutSpec.setOrigin(dps(420f * scale), dps(30f * scale), zero())
                layoutSpec.setSize(dps(60f * scale), dps(35f * scale), full())
                font.setCustom(smallFont)
                textAlignment = Gravity(Alignment.START, Alignment.END)
                textColor.setCustom(Color.MD_GREY_400)
            }

            +toggleButton("Sound") {
                layoutSpec.setOrigin(dps(225f * scale), dps(0f * scale), zero())
                layoutSpec.setSize(dps(150f * scale), dps(35f * scale), full())
                knobColorOn.setCustom(Color.MD_ORANGE)
                trackColorOn.setCustom(Color.MD_ORANGE_200)
                textColor.setCustom(Color.MD_GREY_400)
                textColorHovered.setCustom(Color.MD_ORANGE)
                onClick += { _, _, _ ->
                    onToggleSound(isEnabled)
                }
            }
        }
    }

    private fun formatTime(time: Float): String {
        var secs = (time % 60f).toString(2)
        if (secs.length == 4) {
            secs = "0$secs"
        }
        return "${time.toInt() / 60}:$secs"
    }

    private class DashboardComponentUi(val component: UiComponent) : ComponentUi {
        val meshBuilder = MeshBuilder(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS))
        val mesh = Mesh(meshBuilder.geometry)

        override fun updateComponentAlpha() {
            (mesh.shader as UiShader).apply { alpha = component.alpha }
        }

        override fun createUi(ctx: KoolContext) {
            mesh.shader = UiShader()
            component.addNode(mesh, 0)
        }

        override fun dispose(ctx: KoolContext) {
            component -= mesh
            mesh.dispose(ctx)
        }

        override fun updateUi(ctx: KoolContext) {
            val tilt = cos((90f - 15).toRad())

            component.setupBuilder(meshBuilder)

            meshBuilder.color = Color.BLACK.withAlpha(0.4f)
            meshBuilder.vertex(Vec3f(0f, component.height, 0f), Vec3f.Z_AXIS)
            meshBuilder.vertex(Vec3f(0f, 0f, 0f), Vec3f.Z_AXIS)

            meshBuilder.vertex(Vec3f(0.5f * component.width, component.height, 0f), Vec3f.Z_AXIS)
            meshBuilder.vertex(Vec3f(0.5f * component.width - tilt * component.height, 0f, 0f), Vec3f.Z_AXIS)

            meshBuilder.color = Color.BLACK.withAlpha(0f)
            meshBuilder.vertex(Vec3f(1f * component.width, component.height, 0f), Vec3f.Z_AXIS)
            meshBuilder.vertex(Vec3f(1f * component.width - tilt * component.height, 0f, 0f), Vec3f.Z_AXIS)

            meshBuilder.geometry.addTriIndices(0, 1, 2)
            meshBuilder.geometry.addTriIndices(1, 2, 3)

            meshBuilder.geometry.addTriIndices(2, 3, 4)
            meshBuilder.geometry.addTriIndices(3, 4, 5)
        }
    }

    private class TimerComponentUi(val component: UiComponent) : ComponentUi {
        val meshBuilder = MeshBuilder(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS))
        val mesh = Mesh(meshBuilder.geometry)

        override fun updateComponentAlpha() {
            (mesh.shader as UiShader).apply { alpha = component.alpha }
        }

        override fun createUi(ctx: KoolContext) {
            mesh.shader = UiShader()
            component.addNode(mesh, 0)
        }

        override fun dispose(ctx: KoolContext) {
            component -= mesh
            mesh.dispose(ctx)
        }

        override fun updateUi(ctx: KoolContext) {
            component.setupBuilder(meshBuilder)

            meshBuilder.color = Color.BLACK.withAlpha(0f)
            meshBuilder.vertex(Vec3f(0f, component.height, 0f), Vec3f.Z_AXIS)
            meshBuilder.vertex(Vec3f(0f, 0f, 0f), Vec3f.Z_AXIS)

            meshBuilder.color = Color.BLACK.withAlpha(0.4f)
            meshBuilder.vertex(Vec3f(0.25f * component.width, component.height, 0f), Vec3f.Z_AXIS)
            meshBuilder.vertex(Vec3f(0.25f * component.width, 0f, 0f), Vec3f.Z_AXIS)
            meshBuilder.vertex(Vec3f(0.75f * component.width, component.height, 0f), Vec3f.Z_AXIS)
            meshBuilder.vertex(Vec3f(0.75f * component.width, 0f, 0f), Vec3f.Z_AXIS)

            meshBuilder.color = Color.BLACK.withAlpha(0f)
            meshBuilder.vertex(Vec3f(component.width, component.height, 0f), Vec3f.Z_AXIS)
            meshBuilder.vertex(Vec3f(component.width, 0f, 0f), Vec3f.Z_AXIS)

            meshBuilder.geometry.addTriIndices(0, 1, 2)
            meshBuilder.geometry.addTriIndices(1, 2, 3)

            meshBuilder.geometry.addTriIndices(2, 3, 4)
            meshBuilder.geometry.addTriIndices(3, 4, 5)

            meshBuilder.geometry.addTriIndices(4, 5, 6)
            meshBuilder.geometry.addTriIndices(5, 6, 7)
        }
    }

    companion object {
        const val scale = 1f
        const val fontFamily = Font.SYSTEM_FONT
    }
}