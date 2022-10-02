package de.fabmax.kool.demo.physics.vehicle.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.Settings
import de.fabmax.kool.demo.physics.vehicle.DemoVehicle
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.math.cos

class VehicleUi(val vehicle: DemoVehicle) {

    var onToggleSound: (Boolean) -> Unit = { }

    val dashboard = Dashboard()
    val timerUi = Timer(this)

    private val menuColors = Colors.darkColors(
        accent = MdColor.ORANGE,
        accentVariant = MdColor.ORANGE.mix(Color.BLACK, 0.3f),
        background = Color("00000070")
    )

    val uiSurface = UiSurface(colors = menuColors) {
        val themeSizes = Settings.uiSize.use().sizes
        val nrmFont = themeSizes.normalText
        surface.sizes = themeSizes.copy(
            normalText = FontProps(nrmFont.family, nrmFont.sizePts * 1.1f, Font.ITALIC),
            largeText = FontProps(nrmFont.family, nrmFont.sizePts * 3.5f, Font.ITALIC, chars = "-01234567890.:"),
        )

        modifier
            .background(null)
            .width(Grow.Std)
            .height(Grow.Std)

        dashboard()
        timerUi()
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