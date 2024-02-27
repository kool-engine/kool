package de.fabmax.kool.demo.tests

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.MenuRow
import de.fabmax.kool.demo.labelStyle
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.demo.sectionTitleStyle
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.gizmo.GizmoFrame
import de.fabmax.kool.modules.gizmo.GizmoMode
import de.fabmax.kool.modules.gizmo.SimpleGizmo
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.MdColor

class GizmoTest : DemoScene("Gizmo Test") {

    private val gizmo = SimpleGizmo()

    private val meshA: Mesh = ColorMesh(name = "Mesh A")
    private val meshB: Mesh = ColorMesh(name = "Mesh B")

    private val transformNode = mutableStateOf(meshA).onChange { gizmo.transformNode = it }
    private val transformMode = mutableStateOf(gizmo.mode).onChange { gizmo.mode = it }
    private val transformFrame = mutableStateOf(gizmo.transformFrame).onChange { gizmo.transformFrame = it }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera()

        //gizmo.gizmoNode.isDistanceIndependentSize = false
        addNode(gizmo)

        addNode(meshA)
        meshA.addNode(meshB)

        meshA.apply {
            generate {
                cube { }
            }
            shader = KslBlinnPhongShader {
                color {
                    vertexColor()
                }
            }
            transform.rotate(15f.deg, Vec3f.Y_AXIS)
        }

        meshB.apply {
            generate {
                cube { }
            }
            shader = KslBlinnPhongShader {
                color {
                    vertexColor()
                }
            }
            transform.translate(2f, 1f, -0.5f)
            transform.rotate(10f.deg, 10f.deg, 10f.deg)
        }

        gizmo.transformNode = meshA

        addLineMesh {
            addLine(MdColor.RED, Vec3f.ZERO, Vec3f(10f, 0f, 0f))
            addLine(MdColor.LIGHT_GREEN, Vec3f.ZERO, Vec3f(0f, 10f, 0f))
            addLine(MdColor.BLUE, Vec3f.ZERO, Vec3f(0f, 0f, 10f))
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        Text("Mode") { sectionTitleStyle() }
        gizmoMode(GizmoMode.TRANSLATE, "Translate")
        gizmoMode(GizmoMode.ROTATE, "Rotate")
        gizmoMode(GizmoMode.SCALE, "Scale")

        Text("Frame") { sectionTitleStyle() }
        gizmoFrame(GizmoFrame.GLOBAL, "Global")
        gizmoFrame(GizmoFrame.PARENT, "Parent")
        gizmoFrame(GizmoFrame.LOCAL, "Local")

        Text("Transform Node") { sectionTitleStyle() }
        meshButton(meshA)
        meshButton(meshB)
    }

    private fun UiScope.gizmoMode(mode: GizmoMode, label: String) = MenuRow {
        RadioButton(transformMode.use() == mode) {
            modifier
                .alignY(AlignmentY.Center)
                .margin(end = sizes.gap)
                .onToggle {
                    if (it) {
                        transformMode.set(mode)
                    }
                }
        }
        Text(label) {
            labelStyle(Grow.Std)
            modifier.onClick { transformMode.set(mode) }
        }
    }

    private fun UiScope.gizmoFrame(frame: GizmoFrame, label: String) = MenuRow {
        RadioButton(transformFrame.use() == frame) {
            modifier
                .alignY(AlignmentY.Center)
                .margin(end = sizes.gap)
                .onToggle {
                    if (it) {
                        transformFrame.set(frame)
                    }
                }
        }
        Text(label) {
            labelStyle(Grow.Std)
            modifier.onClick { transformFrame.set(frame) }
        }
    }

    private fun UiScope.meshButton(mesh: Mesh) = MenuRow {
        RadioButton(transformNode.use() == mesh) {
            modifier
                .alignY(AlignmentY.Center)
                .margin(end = sizes.gap)
                .onToggle {
                    if (it) {
                        transformNode.set(mesh)
                    }
                }
        }
        Text(mesh.name) {
            labelStyle(Grow.Std)
            modifier.onClick { transformNode.set(mesh) }
        }
    }
}