package de.fabmax.kool.modules.ui2

import de.fabmax.kool.Clipboard
import de.fabmax.kool.InputManager
import de.fabmax.kool.LocalKeyCode
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.TextCaretNavigation
import kotlin.math.max
import kotlin.math.min


interface TextAreaScope : UiScope {
    override val modifier: TextAreaModifier

    val linesHolder: LazyListScope
}

open class TextAreaModifier(surface: UiSurface) : UiModifier(surface) {
    var isSelectable: Boolean by property(false)
}

fun <T: TextAreaModifier> T.isSelectable(flag: Boolean): T { isSelectable = flag; return this }

fun UiScope.TextArea(
    lines: List<TextAreaLine> = emptyList(),
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    withVerticalScrollbar: Boolean = true,
    withHorizontalScrollbar: Boolean = true,
    scrollbarColor: Color? = null,
    containerModifier: ((UiModifier) -> Unit)? = null,
    scrollPaneModifier: ((ScrollPaneModifier) -> Unit)? = null,
    vScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    hScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    state: LazyListState = weakRememberListState(),
    block: TextAreaScope.() -> Unit
) {
    ScrollArea(
        width, height,
        withVerticalScrollbar,
        withHorizontalScrollbar,
        scrollbarColor,
        containerModifier,
        vScrollbarModifier,
        hScrollbarModifier,
        state
    ) {
        scrollPaneModifier?.let { it(modifier) }

        val textArea = uiNode.createChild(TextAreaNode::class, TextAreaNode.factory)
        textArea.linesHolder.state = state
        textArea.block()

        // set text after block(), so that custom settings (isSelectable / editable) can take an effect
        textArea.setText(lines)
    }
}

open class TextAreaNode(parent: UiNode?, surface: UiSurface) : BoxNode(parent, surface), TextAreaScope, Focusable {
    override val modifier = TextAreaModifier(surface)
    override val isFocused = mutableStateOf(false)

    private lateinit var lines: List<TextAreaLine>

    override val linesHolder = LazyListNode(this, surface)
    private val selectionHandler = SelectionHandler()

    override fun applyDefaults() {
        super.applyDefaults()
        linesHolder.applyDefaults()
        linesHolder.modifier.layout(ColumnLayout)
        mutChildren += linesHolder
    }

    fun setText(lines: List<TextAreaLine>) {
        this.lines = lines
        linesHolder.items(lines) { line ->
            AttributedText(line.textLine) {
                modifier
                    .width(Grow.MinFit)
                    .margin(horizontal = sizes.gap)

                if (this@TextAreaNode.modifier.isSelectable) {
                    modifier
                        .onClick {
                            when (it.pointer.leftButtonRepeatedClickCount) {
                                1 -> selectionHandler.onSelectStart(this, line, it, false)
                                2 -> selectionHandler.selectWord(this, line, it)
                                3 -> selectionHandler.selectLine(this, line)
                            }
                        }
                        .onDragStart { selectionHandler.onSelectStart(this, line, it, true) }
                        .onDrag { selectionHandler.onDrag(it) }
                        .onDragEnd { selectionHandler.onSelectEnd() }
                        .onPointer { selectionHandler.onPointer(this, line, it) }

                    selectionHandler.applySelectionRange(this, line)
                }
            }
        }
    }

    override fun onKeyEvent(keyEvent: InputManager.KeyEvent) {
        if (keyEvent.isPressed) {
            when (keyEvent.keyCode) {
                InputManager.KEY_CURSOR_LEFT -> selectionHandler.moveCaretLeft(wordWise = keyEvent.isCtrlDown, select = keyEvent.isShiftDown)
                InputManager.KEY_CURSOR_RIGHT -> selectionHandler.moveCaretRight(wordWise = keyEvent.isCtrlDown, select = keyEvent.isShiftDown)
                InputManager.KEY_CURSOR_UP -> selectionHandler.moveCaretLineUp(select = keyEvent.isShiftDown)
                InputManager.KEY_CURSOR_DOWN -> selectionHandler.moveCaretLineDown(select = keyEvent.isShiftDown)
                InputManager.KEY_PAGE_UP -> selectionHandler.moveCaretPageUp(select = keyEvent.isShiftDown)
                InputManager.KEY_PAGE_DOWN -> selectionHandler.moveCaretPageDown(select = keyEvent.isShiftDown)
                InputManager.KEY_ESC -> {
                    selectionHandler.clearSelection()
                    surface.requestFocus(null)
                }
                else -> {
                    if (keyEvent.isCtrlDown) {
                        when (keyEvent.localKeyCode) {
                            KEY_CODE_COPY -> selectionHandler.copySelection()?.let { Clipboard.copyToClipboard(it) }
                            KEY_CODE_CUT -> {  }
                            KEY_CODE_PASTE -> {  }
                            KEY_CODE_SELECT_ALL -> {
                                selectionHandler.selectAll()
                            }
                            else -> { }
                        }
                    }
                }
            }
        }
    }

    private inner class SelectionHandler {
        private var isSelecting = false

        // line indices of selection range
        private val selectionStartLine = mutableStateOf(-1)
        private val selectionCaretLine = mutableStateOf(-1)
        // selection range caret positions within start / end line of selection range
        private val selectionStartChar = mutableStateOf(-1)
        private val selectionCaretChar = mutableStateOf(-1)

        private var caretLine: TextAreaLine? = null
        private var caretLineScope: AttributedTextScope? = null

        val selectionFromLine: Int
            get() = min(selectionStartLine.value, selectionCaretLine.value)
        val selectionToLine: Int
            get() = max(selectionStartLine.value, selectionCaretLine.value)

        val isEmptySelection: Boolean
            get() = selectionStartLine.value == selectionCaretLine.value && selectionStartChar.value == selectionCaretChar.value

        fun applySelectionRange(attributedText: AttributedTextScope, line: TextAreaLine) {
            val from = selectionFromLine
            val to = selectionToLine

            var selCaretPos = 0
            var selStartPos = 0

            if (line.lineIndex in (from + 1) until to) {
                // line is completely in selection range
                selStartPos = 0
                selCaretPos = line.textLine.length

            } else if (line.lineIndex == selectionStartLine.value && selectionStartLine.value == selectionCaretLine.value) {
                // single-line selection
                selStartPos = selectionStartChar.use()
                selCaretPos = selectionCaretChar.use()

            } else if (line.lineIndex == selectionFromLine) {
                // multi-line selection, first selected line
                if (selectionStartLine.value < selectionCaretLine.value) {
                    // forward selection
                    selStartPos = selectionStartChar.use()
                    selCaretPos = line.textLine.length
                } else {
                    // reverse selection
                    selStartPos = line.textLine.length
                    selCaretPos = selectionCaretChar.use()
                }
            } else if (line.lineIndex == selectionToLine) {
                // multi-line selection, last selected line
                if (selectionStartLine.value < selectionCaretLine.value) {
                    // forward selection
                    selStartPos = 0
                    selCaretPos = selectionCaretChar.use()
                } else {
                    // reverse selection
                    selStartPos = selectionStartChar.use()
                    selCaretPos = 0
                }
            }

            if (line.lineIndex == selectionCaretLine.value) {
                caretLineScope = attributedText
            }

            selectionStartLine.use()
            selectionCaretLine.use()

            attributedText.modifier
                .selectionRange(selStartPos, selCaretPos)
                .isCaretVisible(isFocused.use() && line.lineIndex == selectionCaretLine.value)
        }

        fun copySelection(): String? {
            return if (isEmptySelection) {
                null

            } else if (selectionStartLine.value == selectionCaretLine.value) {
                // single-line selection
                val fromChar = min(selectionStartChar.value, selectionCaretChar.value)
                val toChar = max(selectionStartChar.value, selectionCaretChar.value)
                lines[selectionFromLine].textLine.text.substring(fromChar, toChar)

            } else {
                // multi-line selection
                var fromChar = selectionStartChar.value
                var toChar = selectionCaretChar.value
                if (selectionCaretLine.value < selectionStartLine.value) {
                    toChar = selectionStartChar.value
                    fromChar = selectionCaretChar.value
                }

                return buildString {
                    append(lines[selectionFromLine].textLine.text.substring(fromChar)).append('\n')
                    for (i in (selectionFromLine + 1) until selectionToLine) {
                        append(lines[i].textLine.text).append('\n')
                    }
                    append(lines[selectionToLine].textLine.text.substring(0, toChar))
                }
            }
        }

        fun clearSelection() {
            selectionStartLine.set(-1)
            selectionCaretLine.set(-1)
            selectionStartChar.set(0)
            selectionCaretChar.set(0)
        }

        fun selectAll() {
            selectionStartLine.set(0)
            selectionCaretLine.set(lines.lastIndex)
            selectionStartChar.set(0)
            selectionCaretChar.set(lines.last().textLine.length)
            resetCaretBlinkState()
            scrollToCaret()
        }

        fun selectWord(attributedText: AttributedTextScope, line: TextAreaLine, ev: PointerEvent) {
            selectionStartLine.set(line.lineIndex)
            selectionCaretLine.set(line.lineIndex)

            val txt = line.textLine.text
            val charIndex = attributedText.charIndexFromLocalX(ev.position.x)
            selectionStartChar.set(TextCaretNavigation.startOfWord(txt, charIndex))
            selectionCaretChar.set(TextCaretNavigation.endOfWord(txt, charIndex))
            caretLine = line
            caretLineScope = attributedText
        }

        fun selectLine(attributedText: AttributedTextScope, line: TextAreaLine) {
            selectionStartLine.set(line.lineIndex)
            selectionCaretLine.set(line.lineIndex)

            selectionStartChar.set(0)
            selectionCaretChar.set(line.textLine.length)
            caretLine = line
            caretLineScope = attributedText
        }

        fun onSelectStart(attributedText: AttributedTextScope, line: TextAreaLine, ev: PointerEvent, isSelecting: Boolean) {
            requestFocus()

            this.isSelecting = isSelecting
            selectionStartLine.set(line.lineIndex)
            selectionCaretLine.set(line.lineIndex)

            val charIndex = attributedText.charIndexFromLocalX(ev.position.x)
            selectionStartChar.set(charIndex)
            selectionCaretChar.set(charIndex)
            caretLine = line
            caretLineScope = attributedText
        }

        fun onDrag(ev: PointerEvent) {
            caretLineScope?.apply {
                val dragLocalPos = MutableVec2f()
                uiNode.toLocal(ev.screenPosition, dragLocalPos)
                val charIndex = charIndexFromLocalX(dragLocalPos.x)
                selectionCaretChar.set(charIndex)
            }
        }

        fun onSelectEnd() {
            isSelecting = false
        }

        fun onPointer(attributedText: AttributedTextScope, line: TextAreaLine, ev: PointerEvent) {
            if (isSelecting && ev.pointer.isDrag) {
                selectionCaretLine.set(line.lineIndex)
                caretLine = line
                caretLineScope = attributedText
                resetCaretBlinkState()
            }
        }

        fun moveCaretLeft(wordWise: Boolean, select: Boolean) {
            var line = caretLine ?: return
            var txt = line.textLine.text

            if (selectionCaretChar.value == 0 && line.lineIndex > 0) {
                line = lines[line.lineIndex - 1]
                selectionCaretChar.set(line.textLine.length)
                caretLine = line
                txt = line.textLine.text

                if (wordWise) {
                    selectionCaretChar.set(TextCaretNavigation.moveWordLeft(txt, selectionCaretChar.value))
                }
                selectionCaretLine.set(line.lineIndex)
                if (!select) {
                    selectionStartLine.set(line.lineIndex)
                    selectionStartChar.set(selectionCaretChar.value)
                }

            } else if (wordWise) {
                selectionCaretChar.set(TextCaretNavigation.moveWordLeft(txt, selectionCaretChar.value))
            } else {
                selectionCaretChar.set((selectionCaretChar.value - 1).clamp(0, txt.length))
            }
            if (!select) {
                selectionStartLine.set(selectionCaretLine.value)
                selectionStartChar.set(selectionCaretChar.value)
            }
            resetCaretBlinkState()
            scrollToCaret()
        }

        fun moveCaretRight(wordWise: Boolean, select: Boolean) {
            var line = caretLine ?: return
            var txt = line.textLine.text

            if (selectionCaretChar.value == txt.length && line.lineIndex < lines.lastIndex) {
                line = lines[line.lineIndex + 1]
                selectionCaretChar.set(0)
                caretLine = line
                txt = line.textLine.text

                if (wordWise) {
                    selectionCaretChar.set(TextCaretNavigation.moveWordRight(txt, selectionCaretChar.value))
                }
                selectionCaretLine.set(line.lineIndex)
                if (!select) {
                    selectionStartLine.set(line.lineIndex)
                    selectionStartChar.set(selectionCaretChar.value)
                }

            } else if (wordWise) {
                selectionCaretChar.set(TextCaretNavigation.moveWordRight(txt, selectionCaretChar.value))
            } else {
                selectionCaretChar.set((selectionCaretChar.value + 1).clamp(0, txt.length))
            }
            if (!select) {
                selectionStartLine.set(selectionCaretLine.value)
                selectionStartChar.set(selectionCaretChar.value)
            }
            resetCaretBlinkState()
            scrollToCaret()
        }

        fun moveCaretLineUp(select: Boolean) {
            val line = caretLine ?: return
            moveCaretToLine(line.lineIndex - 1, select)
        }

        fun moveCaretLineDown(select: Boolean) {
            val line = caretLine ?: return
            moveCaretToLine(line.lineIndex + 1, select)
        }

        fun moveCaretPageUp(select: Boolean) {
            val line = caretLine ?: return
            val bottomLinePad = linesHolder.modifier.extraItemsAfter + 2
            val numPageLines = max(1, linesHolder.state.itemsTo - linesHolder.state.itemsFrom.toInt() - bottomLinePad)
            moveCaretToLine(line.lineIndex - numPageLines, select)
        }

        fun moveCaretPageDown(select: Boolean) {
            val line = caretLine ?: return
            val bottomLinePad = linesHolder.modifier.extraItemsAfter + 2
            val numPageLines = max(1, linesHolder.state.itemsTo - linesHolder.state.itemsFrom.toInt() - bottomLinePad)
            moveCaretToLine(line.lineIndex + numPageLines, select)
        }
        private fun moveCaretToLine(targetLine: Int, select: Boolean) {
            var line = caretLine ?: return
            val caretX = line.textLine.charIndexToPx(selectionCaretChar.value)

            if (targetLine in lines.indices) {
                line = lines[targetLine]
                selectionCaretChar.set(line.textLine.charIndexFromPx(caretX))
                selectionCaretLine.set(line.lineIndex)
            } else if (targetLine < 0) {
                line = lines[0]
                selectionCaretChar.set(0)
                selectionCaretLine.set(line.lineIndex)
            } else if (targetLine > lines.size) {
                line = lines[lines.lastIndex]
                selectionCaretChar.set(line.textLine.length)
                selectionCaretLine.set(line.lineIndex)
            }
            caretLine = line

            if (!select) {
                selectionStartLine.set(selectionCaretLine.value)
                selectionStartChar.set(selectionCaretChar.value)
            }
            resetCaretBlinkState()
            scrollToCaret()
        }

        private fun resetCaretBlinkState() {
            (caretLineScope as? AttributedTextNode)?.resetCaretBlinkState()
        }

        private fun scrollToCaret() {
            val bottomLinePad = linesHolder.modifier.extraItemsAfter + 2
            if (selectionCaretLine.value < linesHolder.state.itemsFrom) {
                linesHolder.state.itemsFrom = selectionCaretLine.value.toFloat()
            } else if (selectionCaretLine.value > linesHolder.state.itemsTo - bottomLinePad) {
                val visLines = linesHolder.state.itemsTo - linesHolder.state.itemsFrom.toInt() - bottomLinePad
                linesHolder.state.itemsFrom = max(0f, selectionCaretLine.value.toFloat() - visLines)
            }
        }
    }

    companion object {
        private val KEY_CODE_SELECT_ALL = LocalKeyCode('a')
        private val KEY_CODE_CUT = LocalKeyCode('x')
        private val KEY_CODE_COPY = LocalKeyCode('c')
        private val KEY_CODE_PASTE = LocalKeyCode('v')

        val factory: (UiNode, UiSurface) -> TextAreaNode = { parent, surface -> TextAreaNode(parent, surface) }
    }
}

class TextAreaLine(val textLine: TextLine, val lineIndex: Int)
