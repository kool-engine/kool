package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.EditorKeyListener
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.SPEED_MOD_ACCURATE
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.SPEED_MOD_NORMAL
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_NO_TICK
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_ROTATION_MAJOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_ROTATION_MINOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_SCALE_MAJOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_SCALE_MINOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_TRANSLATION_MAJOR
import de.fabmax.kool.editor.overlays.TransformGizmoOverlay.Companion.TICK_TRANSLATION_MINOR
import de.fabmax.kool.editor.util.SelectionTransform
import de.fabmax.kool.input.CursorMode
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.modules.gizmo.GizmoListener
import de.fabmax.kool.modules.gizmo.GizmoMode
import de.fabmax.kool.modules.gizmo.GizmoNode
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
            if (transformMode != GizmoMode.ROTATE) {
                PointerInput.cursorMode = CursorMode.LOCKED
            }
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
            gizmo.gizmoNode.applySpeedAndTickRate()
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

    companion object {
        const val SPEED_MOD_NORMAL = 1.0
        const val SPEED_MOD_ACCURATE = 0.1

        const val TICK_NO_TICK = 0.0

        const val TICK_TRANSLATION_MAJOR = 1.0
        const val TICK_ROTATION_MAJOR = 5.0
        const val TICK_SCALE_MAJOR = 0.1

        const val TICK_TRANSLATION_MINOR = 0.1
        const val TICK_ROTATION_MINOR = 1.0
        const val TICK_SCALE_MINOR = 0.01
    }
}

fun GizmoNode.applySpeedAndTickRate() {
    dragSpeedModifier = if (KeyboardInput.isShiftDown) SPEED_MOD_ACCURATE else SPEED_MOD_NORMAL
    if (KeyboardInput.isCtrlDown) {
        translationTick = if (KeyboardInput.isShiftDown) TICK_TRANSLATION_MINOR else TICK_TRANSLATION_MAJOR
        rotationTick = if (KeyboardInput.isShiftDown) TICK_ROTATION_MINOR else TICK_ROTATION_MAJOR
        scaleTick = if (KeyboardInput.isShiftDown) TICK_SCALE_MINOR else TICK_SCALE_MAJOR
    } else {
        translationTick = TICK_NO_TICK
        rotationTick = TICK_NO_TICK
        scaleTick = TICK_NO_TICK
    }
}