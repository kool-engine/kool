package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.gizmo.GizmoMode
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.launchOnMainThread

class FloatingToolbar(val ui: EditorUi) : Composable {

    private val actionMode = mutableStateOf(EditActionMode.NONE)

    init {
        ui.editor.selectionOverlay.onSelectionChanged += {
            updateGizmo()
        }
    }

    private fun updateGizmo() {
        if (actionMode.value in transformTools) {
            launchOnMainThread {
                ui.editor.gizmoOverlay.setTransformObjects(ui.editor.selectionOverlay.getSelectedSceneNodes())
                ui.editor.gizmoOverlay.transformMode = when (actionMode.value) {
                    EditActionMode.MOVE -> GizmoMode.TRANSLATE
                    EditActionMode.ROTATE -> GizmoMode.ROTATE
                    EditActionMode.SCALE -> GizmoMode.SCALE
                    else -> GizmoMode.TRANSLATE
                }
            }
        } else {
            launchOnMainThread {
                ui.editor.gizmoOverlay.setTransformObject(null)
            }
        }
    }

    fun toggleActionMode(actionMode: EditActionMode) {
        if (this.actionMode.value == actionMode) {
            this.actionMode.set(EditActionMode.NONE)
        } else {
            this.actionMode.set(actionMode)
        }

        ui.sceneView.isBoxSelectMode.set(this.actionMode.value == EditActionMode.BOX_SELECT)
        updateGizmo()
    }

    override fun UiScope.compose() = Column(width = sizes.baseSize) {
        modifier
            .margin(start = sizes.largeGap)
            .alignY(AlignmentY.Center)
            .background(RoundRectBackground(colors.backgroundVariantAlpha(0.7f), sizes.gap))
            .onPointer { it.pointer.consume() }

        val mode = actionMode.use()

        Box(height = sizes.smallGap * 0.5f) { }

        iconButton(IconMap.medium.select, "Box-select [B]", mode == EditActionMode.BOX_SELECT) {
            toggleActionMode(EditActionMode.BOX_SELECT)
        }
        iconButton(IconMap.medium.circleCrosshair, "Locate selected object [NP Decimal]") {
            ui.editor.editorCameraTransform.focusSelectedObject()
        }

        menuDivider(color = colors.strongDividerColor)

        iconButton(IconMap.medium.move, "Move selected object [G]", mode == EditActionMode.MOVE) {
            toggleActionMode(EditActionMode.MOVE)
        }
        iconButton(IconMap.medium.rotate, "Rotate selected object [R]", mode == EditActionMode.ROTATE) {
            toggleActionMode(EditActionMode.ROTATE)
        }
        iconButton(IconMap.medium.scale, "Scale selected object [S]", mode == EditActionMode.SCALE) {
            toggleActionMode(EditActionMode.SCALE)
        }

        Box(height = sizes.smallGap * 0.5f) { }
    }

    companion object {
        private val transformTools = setOf(EditActionMode.MOVE, EditActionMode.ROTATE, EditActionMode.SCALE)
    }

    enum class EditActionMode {
        NONE,
        BOX_SELECT,
        MOVE,
        ROTATE,
        SCALE
    }
}