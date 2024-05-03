package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorEditMode
import de.fabmax.kool.modules.ui2.*

class FloatingToolbar(val ui: EditorUi) : Composable {

    private fun toggleEditMode(mode: EditorEditMode.Mode) {
        val editMode = ui.editor.editMode
        if (editMode.mode.value == mode) {
            editMode.mode.set(EditorEditMode.Mode.NONE)
        } else {
            editMode.mode.set(mode)
        }
    }

    override fun UiScope.compose() = Column(width = sizes.baseSize) {
        modifier
            .margin(start = sizes.largeGap)
            .alignY(AlignmentY.Center)
            .background(RoundRectBackground(colors.backgroundVariantAlpha(0.7f), sizes.gap))
            .onPointer { it.pointer.consume() }

        val mode = ui.editor.editMode.mode.use()

        Box(height = sizes.smallGap * 0.5f) { }

        iconButton(IconMap.medium.select, "Box-select [B]", mode == EditorEditMode.Mode.BOX_SELECT) {
            toggleEditMode(EditorEditMode.Mode.BOX_SELECT)
        }
        iconButton(IconMap.medium.circleCrosshair, "Locate selected object [NP Decimal]") {
            ui.editor.editorCameraTransform.focusSelectedObject()
        }

        menuDivider(color = colors.strongDividerColor)

        iconButton(IconMap.medium.move, "Move selected object [G]", mode == EditorEditMode.Mode.MOVE) {
            toggleEditMode(EditorEditMode.Mode.MOVE)
        }
        iconButton(IconMap.medium.rotate, "Rotate selected object [R]", mode == EditorEditMode.Mode.ROTATE) {
            toggleEditMode(EditorEditMode.Mode.ROTATE)
        }
        iconButton(IconMap.medium.scale, "Scale selected object [S]", mode == EditorEditMode.Mode.SCALE) {
            toggleEditMode(EditorEditMode.Mode.SCALE)
        }

        Box(height = sizes.smallGap * 0.5f) { }
    }
}