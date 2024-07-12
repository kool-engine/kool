package de.fabmax.kool.editor

import de.fabmax.kool.editor.util.ImmediateTransformEditMode
import de.fabmax.kool.modules.gizmo.GizmoMode
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logD

class EditorEditMode(val editor: KoolEditor) {

    val mode = mutableStateOf(Mode.NONE).onChange { _, new -> applyMode(new) }

    private val immediateTransform = ImmediateTransformEditMode(editor)

    fun toggleMode(mode: Mode) {
        if (this.mode.value == mode) {
            this.mode.set(Mode.NONE)
        } else {
            this.mode.set(mode)
        }
    }

    private fun applyMode(mode: Mode) {
        logD { "Set editor mode: $mode" }
        when (mode) {
            Mode.MOVE_IMMEDIATE -> immediateTransform.start(mode)
            Mode.ROTATE_IMMEDIATE -> immediateTransform.start(mode)
            Mode.SCALE_IMMEDIATE -> immediateTransform.start(mode)
            Mode.NONE -> {
                if (immediateTransform.isActive) {
                    immediateTransform.finish(true)
                }
            }
            else -> { }
        }
        updateGizmo(mode)
    }

    fun updateGizmo() = updateGizmo(mode.value)

    private fun updateGizmo(mode: Mode) = launchOnMainThread {
        if (mode in transformTools) {
            editor.gizmoOverlay.setTransformObjects(editor.selectionOverlay.getSelectedSceneEntities())
            editor.gizmoOverlay.transformMode = when (mode) {
                Mode.MOVE -> GizmoMode.TRANSLATE
                Mode.ROTATE -> GizmoMode.ROTATE
                Mode.SCALE -> GizmoMode.SCALE
                else -> GizmoMode.TRANSLATE
            }
        } else {
            editor.gizmoOverlay.setTransformObject(null)
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