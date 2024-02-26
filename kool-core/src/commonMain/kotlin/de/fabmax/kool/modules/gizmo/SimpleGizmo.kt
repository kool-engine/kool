package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.InputStack
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class SimpleGizmo(name: String = "simple-gizmo") : Node(name), GizmoListener {

    val gizmoNode = GizmoNode()

    var transformNode: Node? = null
        set(value) {
            field = value
            updateGizmoFromClient()
        }

    var transformFrame = GizmoFrame.GLOBAL
        set(value) {
            field = value
            updateGizmoFromClient()
        }

    init {
        addNode(gizmoNode)
        gizmoNode.gizmoListeners += this
        InputStack.defaultInputHandler.pointerListeners += gizmoNode
    }

    override fun release() {
        super.release()
        InputStack.defaultInputHandler.pointerListeners -= gizmoNode
    }

    fun setupTranslationGizmo() {
        gizmoNode.clearHandles()
        gizmoNode.addTranslationHandles()
    }

    fun setupRotationGizmo() {
        gizmoNode.clearHandles()
        gizmoNode.addRotationHandles()
    }

    fun setupScaleGizmo() {
        gizmoNode.clearHandles()
        gizmoNode.addScaleHandles()
    }

    fun updateGizmoFromClient() {
        val client = transformNode ?: return

        // todo: consider reference frame
        client.transform.decompose(gizmoNode.gizmoTransform.translation, gizmoNode.gizmoTransform.rotation, gizmoNode.gizmoTransform.scale)
        gizmoNode.gizmoTransform.markDirty()
    }

    override fun onGizmoUpdate(transform: TrsTransformD) {
        val client = transformNode ?: return

        // todo: consider reference frame
        val translation = transform.translation
        val rotation = transform.rotation
        val scale = transform.scale
        client.transform.setCompositionOf(translation, rotation, scale)
        client.updateModelMatRecursive()
    }
}

enum class GizmoFrame {
    GLOBAL,
    LOCAL,
    PARENT
}

fun GizmoNode.addTranslationHandles() {
    addHandle(
        AxisHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X,
            handleShape = AxisHandle.HandleType.ARROW,
            name = "axis-POS_X"
        )
    )
    addHandle(
        AxisHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y,
            handleShape = AxisHandle.HandleType.ARROW,
            name = "axis-POS_Y"
        )
    )
    addHandle(
        AxisHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z,
            handleShape = AxisHandle.HandleType.ARROW,
            name = "axis-POS_Z"
        )
    )

    addHandle(
        PlaneHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X,
            name = "plane-POS_X"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y,
            name = "plane-POS_X"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z,
            name = "plane-POS_X"
        )
    )

    addHandle(
        CenterCircleHandle(
            color = Color.WHITE,
            radius = 0.2f
        )
    )
}

fun GizmoNode.addRotationHandles() {
    addHandle(
        AxisRotationHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X
        )
    )
    addHandle(
        AxisRotationHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y
        )
    )
    addHandle(
        AxisRotationHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z
        )
    )

    addHandle(
        CenterCircleHandle(
            color = Color.WHITE,
            radius = 0.2f,
            gizmoOperation = FreeRotation()
        )
    )
    addHandle(
        CenterCircleHandle(
            color = Color.WHITE,
            radius = 1f,
            hitTestMode = CenterCircleHandle.HitTestMode.LINE,
            gizmoOperation = CamPlaneRotation()
        )
    )
}

fun GizmoNode.addScaleHandles() {
    addHandle(
        AxisHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X,
            handleShape = AxisHandle.HandleType.SPHERE,
            gizmoOperation = AxisScale(GizmoHandle.Axis.POS_X),
            name = "scale-axis-x"
        )
    )
    addHandle(
        AxisHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y,
            handleShape = AxisHandle.HandleType.SPHERE,
            gizmoOperation = AxisScale(GizmoHandle.Axis.POS_Y),
            name = "scale-axis-y"
        )
    )
    addHandle(
        AxisHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z,
            handleShape = AxisHandle.HandleType.SPHERE,
            gizmoOperation = AxisScale(GizmoHandle.Axis.POS_Z),
            name = "scale-axis-z"
        )
    )

    addHandle(
        PlaneHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X,
            gizmoOperation = PlaneScale(Vec3d.X_AXIS),
            name = "scale-plane-x"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y,
            gizmoOperation = PlaneScale(Vec3d.Y_AXIS),
            name = "scale-plane-y"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z,
            gizmoOperation = PlaneScale(Vec3d.Z_AXIS),
            name = "scale-plane-z"
        )
    )

    addHandle(
        CenterCircleHandle(
            color = Color.WHITE,
            radius = 1f,
            innerRadius = 0.2f,
            gizmoOperation = UniformScale(),
            name = "scale-uniform"
        )
    )
}