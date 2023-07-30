package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.launchOnMainThread

class FloatingToolbar(val ui: EditorUi) : Composable {

    private val actionMode = mutableStateOf(EditActionMode.NONE)

    init {
        EditorState.onSelectionChanged += {
            updateGizmo()
        }
    }

    private fun updateGizmo() {
        if (actionMode.value in transformTools) {
            val editModel = EditorState.getSelectedSceneNodes().getOrNull(0)
            launchOnMainThread {
                ui.editor.gizmoOverlay.setTransformObject(editModel)
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

        imageButton(IconMap.medium.SELECT, "Box-select [B]", mode == EditActionMode.BOX_SELECT) {
            toggleActionMode(EditActionMode.BOX_SELECT)
        }
        imageButton(IconMap.medium.CIRCLE_CROSSHAIR, "Locate selected object [NP Decimal]") {
            ui.editor.editorCameraTransform.focusSelectedObject()
        }

        menuDivider()

        imageButton(IconMap.medium.MOVE, "Move selected object [G]", mode == EditActionMode.MOVE) {
            toggleActionMode(EditActionMode.MOVE)
        }
        imageButton(IconMap.medium.ROTATE, "Rotate selected object [R]", mode == EditActionMode.ROTATE) {
            toggleActionMode(EditActionMode.ROTATE)
        }
        imageButton(IconMap.medium.SCALE, "Scale selected object [S]", mode == EditActionMode.SCALE) {
            toggleActionMode(EditActionMode.SCALE)
        }

        Box(height = sizes.smallGap * 0.5f) { }
    }

    private fun UiScope.imageButton(
        icon: IconProvider,
        tooltip: String,
        toggleState: Boolean = false,
        onClick: (() -> Unit)? = null
    ) = Box {
        var isHovered by remember(false)
        var isClickFeedback by remember(false)

        val bgColor = when {
            isClickFeedback -> colors.elevatedComponentBgHovered
            isHovered -> colors.componentBgHovered
            toggleState -> colors.componentBg
            else -> null
        }

        bgColor?.let {
            modifier.background(RoundRectBackground(it, sizes.smallGap))
        }

        modifier
            .alignX(AlignmentX.Center)
            .margin(vertical = sizes.smallGap)
            .padding(sizes.smallGap * 0.5f)
            .onPointer { isClickFeedback = it.pointer.isLeftButtonDown }
            .onEnter { isHovered = true }
            .onExit {
                isHovered = false
                isClickFeedback = false
            }
            .onClick {
                onClick?.invoke()
            }

        Image {
            val tint = when {
                toggleState -> colors.primary
                else -> colors.onBackground
            }
            modifier
                .align(AlignmentX.Center, AlignmentY.Center)
                .iconImage(icon, tint)
        }

        Tooltip(tooltip, borderColor = colors.secondaryVariant)
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