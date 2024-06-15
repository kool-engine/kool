package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.DockNodeLeaf
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.util.Color

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

    val notificationBubble = mutableStateOf<NotificationBubble?>(null)

    abstract val windowSurface: UiSurface

    init {
        editorPanels[name] = this
        windowDockable.setFloatingBounds(width = defaultWidth, height = defaultHeight)
    }

    fun triggerUpdate() {
        windowSurface.triggerUpdate()
    }

    protected fun editorPanel(
        isResizable: Boolean = true,
        block: UiScope.() -> Unit
    ) = WindowSurface(
        windowDockable,
        borderColor = { null },
        isResizable = isResizable,
    ) {
        surface.colors = ui.uiColors.use()
        surface.sizes = ui.uiSizes.use()

        modifier.backgroundColor(colors.background)
        if (!windowDockable.isDocked.use()) {
            modifier.border(RectBorder(UiColors.border, sizes.borderWidth))
        }
        block()
    }

    protected fun editorPanelWithPanelBar(
        backgroundColor: UiScope.() -> Color? = { null },
        block: UiScope.() -> Unit
    ) = WindowSurface(
        windowDockable,
        borderColor = { null },
        isResizable = true,
    ) {
        surface.colors = ui.uiColors.use()
        surface.sizes = ui.uiSizes.use()

        modifier.backgroundColor(backgroundColor() ?: colors.background)

        val isDocked = windowDockable.isDocked.use()
        if (isDocked) {
            Row(width = Grow.Std, height = Grow.Std, scopeName = name) {
                windowDockable.dockedTo.use()?.let { dockNode ->
                    val isPanelBarLeft = dockNode.boundsLeftDp.value.px < 1f
                            || dockNode.boundsRightDp.value.px < dockNode.dock.root.boundsRightDp.value.px * 0.99f
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

    private fun UiScope.panelBar(dockNode: DockNodeLeaf) = Column(width = sizes.panelBarWidth, height = Grow.Std) {
        modifier
            .backgroundColor(colors.backgroundMid)
            .padding(top = sizes.smallGap * 0.5f)
        dockNode.dockedItems.mapNotNull { editorPanels[it.name] }.forEach { panel ->
            panelButton(panel, dockNode)
        }
    }


    fun UiScope.panelButton(panel: EditorPanel, dockNode: DockNodeLeaf) {
        val panelNoti = panel.notificationBubble.use()

        val notiBubble: (UiScope.() -> Unit)? = if (panelNoti != null) {
            {
                Box {
                    modifier
                        .zLayer(200)
                        .size(sizes.gap, sizes.gap)
                        .background(CircularBackground(panelNoti.color))
                        .align(AlignmentX.End, AlignmentY.Top)
                        .margin(sizes.smallGap * 0.5f)
                }
            }
        } else null

        iconButton(panel.icon, panel.name, panel == this@EditorPanel, boxBlock = notiBubble) {
            dockNode.bringToTop(panel.windowDockable)
        }
    }

    data class NotificationBubble(val color: Color)

    companion object {
        val editorPanels = mutableMapOf<String, EditorPanel>()
    }
}