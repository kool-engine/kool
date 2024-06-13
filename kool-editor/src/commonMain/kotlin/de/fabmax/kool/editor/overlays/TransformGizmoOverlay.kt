package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.EditorKeyListener
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.util.SelectionTransform
import de.fabmax.kool.input.CursorMode
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.modules.gizmo.GizmoListener
import de.fabmax.kool.modules.gizmo.GizmoMode
import de.fabmax.kool.modules.gizmo.SimpleGizmo
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD

class TransformGizmoOverlay : Node("Transform gizmo") {

    private val gizmo = SimpleGizmo()
    private var selectionTransform: SelectionTransform? = null

    private var hasTransformAuthority = false
    val isTransformDrag: Boolean get() = hasTransformAuthority

    val transformFrame = mutableStateOf(gizmo.transformFrame).onChange {
        gizmo.transformFrame = it
    }

    var transformMode: GizmoMode by gizmo::mode

    private val cancelListener = EditorKeyListener.cancelListener("Object transform") {
        gizmo.gizmoNode.cancelManipulation()
    }

    private val gizmoListener = object : GizmoListener {
        override fun onManipulationStart(startTransform: TrsTransformD) {
            hasTransformAuthority = true
            selectionTransform?.startTransform()
            cancelListener.push()
            PointerInput.cursorMode = CursorMode.LOCKED
        }

        override fun onManipulationFinished(startTransform: TrsTransformD, endTransform: TrsTransformD) {
            hasTransformAuthority = false
            selectionTransform?.applyTransform(true)
            cancelListener.pop()
            PointerInput.cursorMode = CursorMode.NORMAL
        }

        override fun onManipulationCanceled(startTransform: TrsTransformD) {
            hasTransformAuthority = false
            selectionTransform?.restoreInitialTransform()
            cancelListener.pop()
            PointerInput.cursorMode = CursorMode.NORMAL
        }

        override fun onGizmoUpdate(transform: TrsTransformD) {
            selectionTransform?.updateTransform()
            selectionTransform?.applyTransform(false)
        }
    }

    init {
        gizmo.gizmoNode.gizmoListeners += gizmoListener
        gizmo.isVisible = false
        addNode(gizmo)
        onUpdate {
            if (!hasTransformAuthority && selectionTransform?.primaryTransformNode != null) {
                gizmo.updateGizmoFromClient()
            }
        }
    }

    fun setTransformObject(nodeModel: GameEntity?) {
        if (nodeModel != null) {
            setTransformObjects(listOf(nodeModel))
        } else {
            setTransformObjects(emptyList())
        }
    }

    fun setTransformObjects(nodeModels: List<GameEntity>) {
        selectionTransform = SelectionTransform(nodeModels)

        val prim = selectionTransform?.primaryTransformNode
        if (prim != null) {
            gizmo.transformNode = prim.drawNode
            gizmo.isVisible = true
        } else {
            gizmo.isVisible = false
        }
    }
}