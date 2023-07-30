package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.DockNodeLeaf
import de.fabmax.kool.modules.ui2.docking.UiDockable

abstract class EditorPanel(
    val name: String,
    val icon: IconProvider,
    val ui: EditorUi,
    defaultWidth: Dp = ui.dock.dockingSurface.sizes.baseSize * 8,
    defaultHeight: Dp = ui.dock.dockingSurface.sizes.baseSize * 8
) {

    val editor: KoolEditor get() = ui.editor
    val dnd: DndController get() = ui.dndController
    val dndCtx: DragAndDropContext<EditorDndItem<*>> get() = ui.dndController.dndContext

    val windowDockable = UiDockable(name, ui.dock)

    abstract val windowSurface: UiSurface

    init {
        editorPanels[name] = this
        windowDockable.setFloatingBounds(width = defaultWidth, height = defaultHeight)
    }

    protected fun editorPanel(
        isResizable: Boolean = true,
        block: UiScope.() -> Unit
    ) = WindowSurface(
        windowDockable,
        colors = EditorUi.EDITOR_THEME_COLORS,
        borderColor = { null },
        isResizable = isResizable,
    ) {
        modifier.backgroundColor(colors.background)
        if (!windowDockable.isDocked.use()) {
            modifier.border(RectBorder(UiColors.border, sizes.borderWidth))
        }
        block()
    }

    protected fun editorPanelWithPanelBar(
        block: UiScope.() -> Unit
    ) = WindowSurface(
        windowDockable,
        colors = EditorUi.EDITOR_THEME_COLORS,
        borderColor = { null },
        isResizable = true,
    ) {
        modifier.backgroundColor(colors.background)

        val isDocked = windowDockable.isDocked.use()
        if (isDocked) {
            Row(width = Grow.Std, height = Grow.Std) {
                windowDockable.dockedTo.use()?.let { dockNode ->
                    val isPanelBarLeft = dockNode.boundsRightDp.value.px < dockNode.dock.root.boundsRightDp.value.px * 0.99f
                    if (isPanelBarLeft) {
                        panelBar(dockNode)
                        Box(width = sizes.borderWidth, height = Grow.Std) { modifier.backgroundColor(UiColors.titleBg) }
                        block()
                    } else {
                        block()
                        Box(width = sizes.borderWidth, height = Grow.Std) { modifier.backgroundColor(UiColors.titleBg) }
                        panelBar(dockNode)
                    }
                }
            }
        } else {
            modifier.border(RectBorder(UiColors.border, sizes.borderWidth))
            block()
        }
    }

    private fun UiScope.panelBar(dockNode: DockNodeLeaf) = Column(width = sizes.baseSize - sizes.borderWidth, height = Grow.Std) {
        modifier.backgroundColor(colors.backgroundMid)
        //modifier.backgroundColor(UiColors.titleBg)

        dockNode.dockedItems.mapNotNull { editorPanels[it.name] }.forEach { panel ->
            panelButton(panel, dockNode)
        }
    }


    fun UiScope.panelButton(panel: EditorPanel, dockNode: DockNodeLeaf) = Box(height = sizes.baseSize - sizes.gap) {
        var isHovered by remember(false)
        var isClickFeedback by remember(false)
        val isSelected = panel == this@EditorPanel

        val bgColor = when {
            isClickFeedback -> colors.elevatedComponentBgHovered
            isHovered -> colors.componentBgHovered
            isSelected -> colors.componentBg
            else -> null
        }

        bgColor?.let {
            modifier.background(RoundRectBackground(it, sizes.smallGap))
        }

        modifier
            .alignX(AlignmentX.Center)
            .margin(sizes.smallGap)
            .padding(sizes.smallGap * 0.5f)
            .onPointer { isClickFeedback = it.pointer.isLeftButtonDown }
            .onEnter { isHovered = true }
            .onExit {
                isHovered = false
                isClickFeedback = false
            }
            .onClick {
                dockNode.bringToTop(panel.windowDockable)
            }

        Image {
            modifier
                .align(AlignmentX.Center, AlignmentY.Center)
                .iconImage(panel.icon, colors.onBackground)
        }

        Tooltip(panel.name, borderColor = colors.secondaryVariant)
    }

    companion object {
        val editorPanels = mutableMapOf<String, EditorPanel>()
    }
}