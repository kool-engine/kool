package de.fabmax.kool.demo.tests

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.demo.MenuRow
import de.fabmax.kool.demo.labelStyle
import de.fabmax.kool.demo.menu.DemoMenu
import de.fabmax.kool.demo.sectionTitleStyle
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.gizmo.GizmoFrame
import de.fabmax.kool.modules.gizmo.GizmoMode
import de.fabmax.kool.modules.gizmo.SimpleGizmo
import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.launchDelayed

class GizmoTest : DemoScene("Gizmo Test") {

    private val gizmo = SimpleGizmo()

    private val meshA: Mesh = ColorMesh(name = "Parent")
    private val meshB: Mesh = ColorMesh(name = "Child")

    private val transformNode = mutableStateOf(meshA).onChange { gizmo.transformNode = it }
    private val transformMode = mutableStateOf(gizmo.mode).onChange { gizmo.mode = it }
    private val transformFrame = mutableStateOf(gizmo.transformFrame).onChange { gizmo.transformFrame = it }

    private val clickListener = InputStack.PointerListener { pointerState, _ ->
        val ptr = pointerState.primaryPointer
        if (ptr.isLeftButtonClicked && !ptr.isConsumed()) {
            val rayTest = RayTest()
            if (mainScene.computePickRay(ptr, rayTest.ray)) {
                mainScene.rayTest(rayTest)
                if (rayTest.hitNode == meshA) {
                    transformNode.set(meshA)
                } else if (rayTest.hitNode == meshB) {
                    transformNode.set(meshB)
                }
            }
        }
    }

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
            transform.rotate(25f.deg, Vec3f.Y_AXIS)
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

        // somewhat hacky: We wait one frame before the set the initial transform node to the gizmo.
        // This ensures that the node was rendered at least once before it's transform is captured. This is needed
        // because otherwise the node's model matrix might not be valid, and then it's transform is not correctly
        // captured.
        launchDelayed(1) {
            gizmo.transformNode = meshA
        }

        addLineMesh {
            addLine(MdColor.RED, Vec3f.ZERO, Vec3f(10f, 0f, 0f))
            addLine(MdColor.LIGHT_GREEN, Vec3f.ZERO, Vec3f(0f, 10f, 0f))
            addLine(MdColor.BLUE, Vec3f.ZERO, Vec3f(0f, 0f, 10f))
        }

        InputStack.defaultInputHandler.pointerListeners += clickListener
        onRelease {
            InputStack.defaultInputHandler.pointerListeners -= clickListener
        }
    }

    override fun createMenu(menu: DemoMenu, ctx: KoolContext) = menuSurface {
        Text("Mode") { sectionTitleStyle() }
        gizmoMode(GizmoMode.TRANSLATE, "Translate")
        gizmoMode(GizmoMode.ROTATE, "Rotate")
        gizmoMode(GizmoMode.SCALE, "Scale")

        Box(height = sizes.smallGap) { }

        when (transformMode.use()) {
            GizmoMode.TRANSLATE -> translationValues()
            GizmoMode.ROTATE -> rotationValues()
            GizmoMode.SCALE -> scaleValues()
        }

        Text("Frame") { sectionTitleStyle() }
        gizmoFrame(GizmoFrame.GLOBAL, "Global")
        gizmoFrame(GizmoFrame.PARENT, "Parent")
        gizmoFrame(GizmoFrame.LOCAL, "Local")

        Text("Selected Node") { sectionTitleStyle() }
        meshButton(meshA)
        meshButton(meshB)
    }

    private fun UiScope.translationValues() {
        val t = gizmo.translationState.use()
        MenuRow {
            Text("X = ") { modifier.textColor(MdColor.RED) }
            numberText(t.x, 3) { gizmo.translationState.set(Vec3d(it, t.y, t.z)) }
        }
        MenuRow {
            Text("Y = ") { modifier.textColor(MdColor.LIGHT_GREEN) }
            numberText(t.y, 3) { gizmo.translationState.set(Vec3d(t.x, it, t.z)) }
        }
        MenuRow {
            Text("Z = ") { modifier.textColor(MdColor.BLUE) }
            numberText(t.z, 3) { gizmo.translationState.set(Vec3d(t.x, t.y, it)) }
        }
    }

    private fun UiScope.rotationValues() {
        val q = gizmo.rotationState.use()
        val r = q.toEulers()

        println("q: $q -> r: $r")

        MenuRow {
            Text("X = ") { modifier.textColor(MdColor.RED) }
            numberText(r.x, 3) { gizmo.rotationState.set(MutableQuatD().rotateByEulers(Vec3d(it, r.y, r.z))) }
        }
        MenuRow {
            Text("Y = ") { modifier.textColor(MdColor.LIGHT_GREEN) }
            numberText(r.y, 3) { gizmo.rotationState.set(MutableQuatD().rotateByEulers(Vec3d(r.x, it, r.z))) }
        }
        MenuRow {
            Text("Z = ") { modifier.textColor(MdColor.BLUE) }
            numberText(r.z, 3) { gizmo.rotationState.set(MutableQuatD().rotateByEulers(Vec3d(r.x, r.y, it))) }
        }
    }

    private fun UiScope.scaleValues() {
        val s = gizmo.scaleState.use()
        MenuRow {
            Text("X = ") { modifier.textColor(MdColor.RED) }
            numberText(s.x, 3) { gizmo.translationState.set(Vec3d(it, s.y, s.z)) }
        }
        MenuRow {
            Text("Y = ") { modifier.textColor(MdColor.LIGHT_GREEN) }
            numberText(s.y, 3) { gizmo.translationState.set(Vec3d(s.x, it, s.z)) }
        }
        MenuRow {
            Text("Z = ") { modifier.textColor(MdColor.BLUE) }
            numberText(s.z, 3) { gizmo.translationState.set(Vec3d(s.x, s.y, it)) }
        }
    }

    private fun UiScope.numberText(value: Double, precision: Int, setter: (Double) -> Unit) {
        TextField {
            var text by remember(value.toString(precision))
            if (!isFocused.value) {
                text = value.toString(precision)
            }
            modifier
                .text(text)
                .textAlignX(AlignmentX.End)
                .width(Grow.Std)
                .onEnterPressed {
                    text.toDoubleOrNull()?.let { setter(it) }
                    surface.requestFocus(null)
                }
                .onChange { txt -> text = txt }
        }
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