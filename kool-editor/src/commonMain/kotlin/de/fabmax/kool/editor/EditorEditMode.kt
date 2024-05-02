package de.fabmax.kool.editor

import de.fabmax.kool.modules.gizmo.GizmoMode
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.launchOnMainThread

class EditorEditMode(val editor: KoolEditor) {

    val mode = mutableStateOf(Mode.NONE).onChange { applyMode(it) }

    private fun applyMode(mode: Mode) {
//        when (mode) {
//            todo
//        }
        updateGizmo(mode)
    }

    fun updateGizmo() = updateGizmo(mode.value)

    private fun updateGizmo(mode: Mode) {
        if (mode in transformTools) {
            launchOnMainThread {
                editor.gizmoOverlay.setTransformObjects(editor.selectionOverlay.getSelectedSceneNodes())
                editor.gizmoOverlay.transformMode = when (mode) {
                    Mode.MOVE -> GizmoMode.TRANSLATE
                    Mode.ROTATE -> GizmoMode.ROTATE
                    Mode.SCALE -> GizmoMode.SCALE
                    else -> GizmoMode.TRANSLATE
                }
            }
        } else {
            launchOnMainThread {
                editor.gizmoOverlay.setTransformObject(null)
            }
        }
    }

    companion object {
        private val transformTools = setOf(Mode.MOVE, Mode.ROTATE, Mode.SCALE)
    }

    enum class Mode {
        NONE,
        BOX_SELECT,
        MOVE,
        MOVE_IMMEDIATE,
        ROTATE,
        ROTATE_IMMEDIATE,
        SCALE,
        SCALE_IMMEDIATE,
    }
}