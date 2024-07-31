package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.PlatformFunctions
import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.modules.ui2.docking.UiDockable
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.launchOnMainThread

abstract class EditorDialog(name: String, val ui: EditorUi = KoolEditor.instance.ui, isResizable: Boolean = false) {

    val dialogDockable = UiDockable(name).apply {
        setFloatingBounds(Dp.ZERO, Dp.ZERO, Dp(500f), FitContent, AlignmentX.Center, AlignmentY.Center)
    }

    val dialogActions = mutableListOf<DialogAction>()
    val onClose = mutableListOf<() -> Unit>()

    val dialog = WindowSurface(dialogDockable, borderColor = { null }, isResizable = isResizable) {
        surface.colors = ui.uiColors.use()
        surface.sizes = ui.uiSizes.use()

        modifier.border(RoundRectBorder(UiColors.border, sizes.gap, sizes.borderWidth))

        Column(Grow.Std, Grow.Std) {
            Row(Grow.Std, height = sizes.heightTitleBar) {
                modifier
                    .padding(horizontal = sizes.gap * 1.5f)
                    .background(TitleBarBackground(UiColors.titleBg, sizes.gap.px, false))

                if (!dialogDockable.isDocked.use()) {
                    modifier.margin(sizes.borderWidth)
                }
                with(dialogDockable) {
                    registerDragCallbacks()
                }

                Text(dialogDockable.name) {
                    modifier
                        .width(Grow.Std)
                        .textColor(UiColors.titleText)
                        .font(sizes.boldText)
                        .alignY(AlignmentY.Center)
                }

                closeButton { onCloseClicked() }
            }


            Box(width = Grow.Std, height = Grow.Std) {
                modifier.padding(horizontal = sizes.largeGap, vertical = sizes.largeGap)
                dialogContent()
            }
            if (dialogActions.isNotEmpty()) {
                divider(horizontalMargin = Dp.ZERO, color = colors.hoverBg)

                Row(height = sizes.baseSize * 1.25f) {
                    modifier
                        .alignX(AlignmentX.End)

                    dialogActions.forEach {
                        Button(it.text) {
                            modifier
                                .onClick(it.action)
                                .alignY(AlignmentY.Center)
                                .margin(horizontal = sizes.largeGap)

                            if (it.baseSizes > 0) {
                                modifier.width(sizes.baseSize * it.baseSizes)
                            }
                        }
                    }
                }
            }
        }
    }

    protected val closeOnEscFocusable = object : Focusable {
        override val isFocused = mutableStateOf(false)

        override fun onKeyEvent(keyEvent: KeyEvent) {
            if (keyEvent.keyCode == KeyboardInput.KEY_ESC && keyEvent.isReleased) {
                onCloseClicked()
            }
        }
    }

    init {
        dialog.isVisible = false
        dialog.requestFocus(closeOnEscFocusable)
    }

    fun show() {
        dialog.isVisible = true
        ui.addNode(dialog)
    }

    fun hide() {
        onClose.forEach { it() }
        ui.removeNode(dialog)
        dialog.isVisible = false
        dialog.release()
    }

    abstract fun UiScope.dialogContent(): Any

    fun addOkAction(text: String = "OK", action: (PointerEvent) -> Unit) {
        dialogActions += DialogAction(text, action = action)
    }

    fun addCancelAction(text: String = "Cancel", action: (() -> Unit)? = null) {
        dialogActions += DialogAction(text) { onCloseClicked() }
        action?.let { onClose += it }
    }

    protected open fun onCloseClicked() {
        hide()
    }

    class DialogAction(val text: String, val baseSizes: Float = 2f, val action: (PointerEvent) -> Unit)
}

class TextDialog(title: String, val text: String) : EditorDialog(title) {
    override fun UiScope.dialogContent() = Text(text) {
        modifier
            .width(Grow.Std)
            .isWrapText(true)
    }
}

class EnterTextDialog(title: String, text: String, val hint: String, val onEnterPressed: (String) -> Unit) : EditorDialog(title) {
    val text = mutableStateOf(text)
    override fun UiScope.dialogContent() = TextField(text.use()) {
        defaultTextfieldStyle()
        modifier
            .width(Grow.Std)
            .hint(hint)
            .onChange { text.set(it) }
            .onEnterPressed {
                onEnterPressed(it)
                hide()
            }
        remember { surface.requestFocus(this) }
    }
}

fun OkCancelTextDialog(
    title: String,
    text: String,
    onCancel: (() -> Unit)? = null,
    onOk: () -> Unit
) = TextDialog(title, text).apply {
    launchDelayed(1) { dialog.isFocused.set(true) }
    addOkAction {
        onOk()
        hide()
    }
    addCancelAction(action = onCancel)
    show()
}

fun OkCancelEnterTextDialog(
    title: String,
    text: String = "",
    hint: String = "",
    onCancel: (() -> Unit)? = null,
    onOk: (String) -> Unit
) = EnterTextDialog(title, text, hint, onOk).apply {
    launchDelayed(1) { dialog.isFocused.set(true) }
    addOkAction {
        onOk(this.text.value)
        hide()
    }
    addCancelAction(action = onCancel)
    show()
}

class BrowsePathDialog(title: String, path: String, val hint: String, val onEnterPressed: (String) -> Unit) : EditorDialog(title) {
    val text = mutableStateOf(path)
    override fun UiScope.dialogContent() = Row(width = Grow.Std) {
        TextField(text.use()) {
            defaultTextfieldStyle()
            modifier
                .alignY(AlignmentY.Center)
                .width(Grow.Std)
                .hint(hint)
                .onChange { text.set(it) }
                .onEnterPressed {
                    onEnterPressed(it)
                    hide()
                }
            remember { surface.requestFocus(this) }
        }
        Button("...") {
            defaultButtonStyle()

            Tooltip("Browse")

            modifier
                .alignY(AlignmentY.Center)
                .margin(start = sizes.largeGap)
                .onClick {
                    launchOnMainThread {
                        PlatformFunctions.chooseFilePath()?.let { text.set(it) }
                    }
                }
        }
    }
}

fun OkCancelBrowsePathDialog(
    title: String,
    path: String = "",
    hint: String = "",
    onCancel: (() -> Unit)? = null,
    onOk: (String) -> Unit
) = BrowsePathDialog(title, path, hint, onOk).apply {
    launchDelayed(1) { dialog.isFocused.set(true) }
    addOkAction {
        onOk(this.text.value)
        hide()
    }
    addCancelAction(action = onCancel)
    show()
}
