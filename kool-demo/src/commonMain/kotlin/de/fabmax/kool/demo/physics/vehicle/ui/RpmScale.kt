package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.ui.ComponentUi
import de.fabmax.kool.scene.ui.UiComponent
import de.fabmax.kool.scene.ui.UiRoot
import de.fabmax.kool.scene.ui.UiShader
import de.fabmax.kool.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sin

class RpmScale(root: UiRoot) : UiComponent("rpm", root) {
    var criticlaRpm = 5000f
        set(value) {
            field = value
            requestUiUpdate()
        }
    var maxRpm = 6000f
        set(value) {
            field = value
            requestUiUpdate()
        }
    var value = 750f
        set(value) {
            field = value
            isTrackUpdate = true
        }

    private var isTrackUpdate = false

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return RpmScaleUi(this)
    }

    override fun updateComponent(ctx: KoolContext) {
        super.updateComponent(ctx)
        if (isTrackUpdate) {
            isTrackUpdate = false
            val rpmUi = ui.prop
            if (rpmUi is RpmScaleUi) {
                rpmUi.updateTrack()
            } else {
                requestUiUpdate()
            }
        }
    }
}

class RpmScaleUi(private val rpmScale: RpmScale) : BarUi(rpmScale) {
    private val trackColor = ColorGradient(Color.WHITE.withAlpha(0.5f), Color.MD_LIGHT_BLUE)

    private val largeFontMesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)) { }
    private val largeFontMeshBuilder = MeshBuilder(largeFontMesh.geometry)

    private val smallFontMesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)) { }
    private val smallFontMeshBuilder = MeshBuilder(smallFontMesh.geometry)

    private lateinit var largeFont: Font
    private lateinit var smallFont: Font

    init {
        // compute track positions
        val w = 0.15f
        val r = 0.2f + w / 2
        val cy = 1f - w/2 - r
        val cx = cy * tilt + r + w/2f
        centerTrack += Vec2f(w/2f, 0f)
        for (i in 0..10) {
            val a = (180f - tiltAng - (90f - tiltAng) / 10f * i).toRad()
            centerTrack += Vec2f(cx + r * cos(a), cy + r * sin(a))
        }
        centerTrack += Vec2f(2.75f - w/2f * tilt, 1f - w / 2f)

        // compute track length
        var len = 0f
        for (i in 0 until centerTrack.lastIndex) {
            len += centerTrack[i].distance(centerTrack[i+1])
        }
        var pos = 0f
        for (i in 0 until centerTrack.lastIndex) {
            centerTrackRelPos += pos / len
            pos += centerTrack[i].distance(centerTrack[i+1])
        }
        centerTrackRelPos += 1f

        // compute left directions
        centerTrackLefts += Vec2f(-w/2, 0f)
        for (i in 1 until centerTrack.lastIndex) {
            val v = MutableVec2f(centerTrack[i+1]).subtract(centerTrack[i]).norm().scale(w/2).rotate(90f)
            centerTrackLefts += v
        }
        centerTrackLefts += Vec2f(w/2 * tilt, w/2)
    }

    override fun updateComponentAlpha() {
        super.updateComponentAlpha()
        (largeFontMesh.shader as UiShader).apply { alpha = rpmScale.alpha }
        (smallFontMesh.shader as UiShader).apply { alpha = rpmScale.alpha }
    }

    override fun createUi(ctx: KoolContext) {
        super.createUi(ctx)

        largeFont = uiFont(VehicleUi.fontFamily, 19f * VehicleUi.scale, rpmScale.dpi, ctx, Font.ITALIC)
        largeFontMesh.shader = UiShader(largeFont)
        rpmScale += largeFontMesh

        smallFont = uiFont(VehicleUi.fontFamily, 14f * VehicleUi.scale, rpmScale.dpi, ctx, Font.ITALIC)
        smallFontMesh.shader = UiShader(smallFont)
        rpmScale += smallFontMesh
    }

    override fun updateUi(ctx: KoolContext) {
        numIntervals = (rpmScale.maxRpm / 1000).toInt()
        if (rpmScale.maxRpm % 1000 > 0) {
            numIntervals++
        }
        trackScale = rpmScale.height
        super.updateUi(ctx)

        meshBuilder.fillTrack(rpmScale.criticlaRpm / rpmScale.maxRpm, 1f) { Color.MD_PINK.withAlpha(0.3f) }

        rpmScale.setupBuilder(largeFontMeshBuilder)
        rpmScale.setupBuilder(smallFontMeshBuilder)

        drawRpms()
        updateTrack()
    }

    fun updateTrack() {
        rpmScale.setupBuilder(trackBuilder)

        val p = min(rpmScale.value, rpmScale.criticlaRpm) / rpmScale.maxRpm
        trackBuilder.fillTrack(0f, p) { trackColor.getColor(it, 0.0f, rpmScale.criticlaRpm / rpmScale.maxRpm) }
        if (p < 1) {
            trackBuilder.color = Color.MD_PINK
            trackBuilder.fillTrack(p, rpmScale.value / rpmScale.maxRpm) { trackBuilder.color }
        }
    }

    private fun drawRpms() {
        largeFontMeshBuilder.color = Color.WHITE
        smallFontMeshBuilder.color = Color.WHITE

        val pos = MutableVec2f()
        val lt = MutableVec2f()
        for (i in 1 .. numIntervals) {
            getTrackPosAt(i.toFloat() / numIntervals, pos, lt)

            largeFontMeshBuilder.text(largeFont) {
                text = "$i"
                origin.x = round(pos.x - lt.x * 2.3f - largeFont.textWidth(text) / 2)
                origin.y = round(pos.y - lt.y * 2.3f - largeFont.normHeight / 2)
                origin.z = 0f
            }
        }

        getTrackPosAt(0.12f, pos, lt)
        smallFontMeshBuilder.withTransform {
            translate(pos.x + lt.x * 1.7f, pos.y + lt.y * 1.7f, 0f)
            rotate(90f - tiltAng + 2, Vec3f.Z_AXIS)
            text(smallFont) { text = "x1000 rpm" }
        }
    }

    override fun MeshBuilder.drawIntervals(w: Float) {
        withTransform {
            translate(0f, 0f, 10f)
            meshBuilder.color = Color.WHITE
            val pos = MutableVec2f()
            val lt = MutableVec2f()
            val cnt = numIntervals * 2
            for (i in 1 until cnt) {
                val f = if (i % 2 == 0) 0.5f else 0.75f
                getTrackPosAt(i.toFloat() / cnt, pos, lt)
                line(pos.x - lt.x * f, pos.y - lt.y * f, pos.x - lt.x, pos.y - lt.y, w)
                line(pos.x + lt.x * f, pos.y + lt.y * f, pos.x + lt.x, pos.y + lt.y, w)
            }
        }
    }
}