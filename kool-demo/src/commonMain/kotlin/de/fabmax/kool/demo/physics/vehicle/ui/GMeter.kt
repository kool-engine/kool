package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.ui.ComponentUi
import de.fabmax.kool.scene.ui.UiComponent
import de.fabmax.kool.scene.ui.UiRoot
import de.fabmax.kool.scene.ui.UiShader
import de.fabmax.kool.util.*
import kotlin.math.sqrt


class GMeter(name: String, root: UiRoot) : UiComponent(name, root) {
    var latAccel = 0f
        set(value) {
            field = value
            isValueUpdate = true
        }
    var longAccel = 0f
        set(value) {
            field = value
            isValueUpdate = true
        }

    var color = ColorGradient(MdColor.ORANGE tone 100, MdColor.ORANGE)

    private var isValueUpdate = false

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return GMeterUi(this)
    }

    override fun updateComponent(ctx: KoolContext) {
        super.updateComponent(ctx)
        if (isValueUpdate) {
            isValueUpdate = false
            val meterUi = ui.prop
            if (meterUi is GMeterUi) {
                meterUi.updateValue()
            } else {
                requestUiUpdate()
            }
        }
    }
}

class GMeterUi(private val gMeter: GMeter) : ComponentUi {
    private val mesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)) { }
    private val meshBuilder = MeshBuilder(mesh.geometry)

    private val bgMesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)) { }
    private val bgMeshBuilder = MeshBuilder(bgMesh.geometry)

    private lateinit var font: Font

    override fun updateComponentAlpha() {
        (mesh.shader as UiShader).apply { alpha(gMeter.alpha) }
    }

    override fun createUi(ctx: KoolContext) {
        font = Font(FontProps(VehicleUi.fontFamily, 12f * VehicleUi.scale, Font.ITALIC))
        bgMesh.shader = UiShader()
        gMeter += bgMesh

        mesh.shader = UiShader()
        gMeter += mesh
    }

    override fun updateUi(ctx: KoolContext) {
        gMeter.setupBuilder(bgMeshBuilder)
        bgMeshBuilder.drawMeter(1f)

        updateValue()
    }

    fun updateValue() {
        val a = (sqrt(gMeter.latAccel * gMeter.latAccel + gMeter.longAccel * gMeter.longAccel) / G_METER_SCALE).clamp(0f, 1f)
        val dotColor = gMeter.color.getColor(a)

        gMeter.setupBuilder(meshBuilder)
        meshBuilder.color = dotColor
        meshBuilder.circle {
            center.set(-gMeter.latAccel / G_METER_SCALE, -gMeter.longAccel / G_METER_SCALE, 0f).scale(gMeter.height / 2)
            if (center.length() > gMeter.height / 2) {
                center.norm().scale(gMeter.height / 2)
            }
            center.x += gMeter.width / 2
            center.y += gMeter.height / 2
            radius = gMeter.height / 15f
        }
    }

    private fun MeshBuilder.drawMeter(w: Float) {
        bgMeshBuilder.color = Color.WHITE.withAlpha(0.2f)
        for (i in 1..3) {
            circle {
                steps = 40
                center.set(gMeter.width / 2, gMeter.height / 2, 0f)
                radius = gMeter.height / 6f * i
            }
        }
        bgMeshBuilder.color = Color.WHITE.withAlpha(0.5f)
        line(0f, gMeter.height / 2, gMeter.width, gMeter.height / 2, w)
        line(gMeter.width / 2, 0f, gMeter.width / 2, gMeter.height, w)
    }

    companion object {
        const val G_METER_SCALE = 1.5f * 9.81f
    }
}