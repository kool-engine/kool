package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorEditMode
import de.fabmax.kool.editor.Key
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.*

class FloatingToolbar(val editor: KoolEditor) : Composable {

    override fun UiScope.compose() = Column(width = sizes.baseSize) {
        modifier
            .margin(start = sizes.largeGap)
            .alignY(AlignmentY.Center)
            .background(RoundRectBackground(colors.backgroundVariantAlpha(0.7f), sizes.gap))
            .onPointer { it.pointer.consume() }

        val mode = editor.editMode.mode.use()

        Box(height = sizes.smallGap * 0.5f) { }

        iconButton(Icons.medium.select, "Box-select [${Key.ToggleBoxSelectMode.binding.keyInfo}]", mode == EditorEditMode.Mode.BOX_SELECT) {
            editor.editMode.toggleMode(EditorEditMode.Mode.BOX_SELECT)
        }
        iconButton(Icons.medium.circleCrosshair, "Locate selected object [${Key.FocusSelected.binding.keyInfo}]") {
            editor.editorCameraTransform.focusSelectedObject()
        }

        toolbarDivider(color = colors.strongDividerColor)

        iconButton(Icons.medium.move, "Move selected object [${Key.ToggleMoveMode.binding.keyInfo}]", mode == EditorEditMode.Mode.MOVE) {
            editor.editMode.toggleMode(EditorEditMode.Mode.MOVE)
        }
        iconButton(Icons.medium.rotate, "Rotate selected object [${Key.ToggleRotateMode.binding.keyInfo}]", mode == EditorEditMode.Mode.ROTATE) {
            editor.editMode.toggleMode(EditorEditMode.Mode.ROTATE)
        }
        iconButton(Icons.medium.resize, "Scale selected object [${Key.ToggleScaleMode.binding.keyInfo}]", mode == EditorEditMode.Mode.SCALE) {
            editor.editMode.toggleMode(EditorEditMode.Mode.SCALE)
        }

        Box(height = sizes.smallGap * 0.5f) { }
    }
}