package de.fabmax.kool.modules.ui2

import de.fabmax.kool.InputManager
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.util.Color
import kotlin.math.max
import kotlin.math.min


interface TextAreaScope : UiScope {
    override val modifier: TextAreaModifier

    val linesHolder: LazyListScope
}

open class TextAreaModifier(surface: UiSurface) : UiModifier(surface) {
    var isSelectable: Boolean by property(false)
    var isEditable: Boolean by property(false)
}

fun <T: TextAreaModifier> T.isSelectable(flag: Boolean): T { isSelectable = flag; return this }
fun <T: TextAreaModifier> T.isEditable(flag: Boolean): T { isEditable = flag; return this }

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
    private val selectionHandler = LineSelectionHandler()

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
                modifier.margin(horizontal = sizes.gap)

                if (this@TextAreaNode.modifier.isSelectable) {
                    modifier
                        .onClick { selectionHandler.onSelectStart(this, line, it, false) }
                        .onDragStart { selectionHandler.onSelectStart(this, line, it, true) }
                        .onDrag { selectionHandler.onDrag(it) }
                        .onDragEnd { selectionHandler.onSelectEnd() }
                        .onPointer { selectionHandler.onPointer(this, line, it) }

                    selectionHandler.setSelectionRange(this, line)
                }
            }
        }
    }

    override fun onFocusGain() {
        super.onFocusGain()
        println("got focus")
    }

    override fun onFocusLost() {
        super.onFocusLost()
        println("lost focus")
    }

    override fun onKeyEvent(keyEvent: InputManager.KeyEvent) {
        println("key event")
    }

    private inner class LineSelectionHandler {
        private var isSelecting = false
        private val selectionStartLine = mutableStateOf(-1)
        private val selectionCaretLine = mutableStateOf(-1)

        private var caretLine: TextAreaLine? = null
        private var caretLineScope: AttributedTextScope? = null

        val selectionFromLine: Int
            get() = min(selectionStartLine.value, selectionCaretLine.value)
        val selectionToLine: Int
            get() = max(selectionStartLine.value, selectionCaretLine.value)

        fun setSelectionRange(attributedText: AttributedTextScope, line: TextAreaLine) {
            val from = selectionFromLine
            val to = selectionToLine
            if (line.lineIndex !in from..to) {
                line.caretPos.set(0)
                line.selectionStart.set(0)
            } else if (line.lineIndex in (from + 1) until to) {
                line.selectionStart.set(0)
                line.caretPos.set(line.textLine.length)
            } else if (line.lineIndex == selectionStartLine.value && selectionStartLine.value != selectionCaretLine.value) {
                if (selectionStartLine.value < selectionCaretLine.value) {
                    line.caretPos.set(line.textLine.length)
                } else {
                    line.caretPos.set(0)
                }
            }

            selectionStartLine.use()
            selectionCaretLine.use()

            attributedText.modifier
                .selectionRange(line.selectionStart.use(), line.caretPos.use())
                .isCaretVisible(isFocused.use() && line.lineIndex == selectionCaretLine.value)
        }

        fun onSelectStart(attributedText: AttributedTextScope, line: TextAreaLine, ev: PointerEvent, isSelecting: Boolean) {
            requestFocus()

            this.isSelecting = isSelecting
            selectionStartLine.set(line.lineIndex)
            selectionCaretLine.set(line.lineIndex)

            val charIndex = attributedText.charIndexFromLocalX(ev.position.x)
            line.caretPos.set(charIndex)
            line.selectionStart.set(charIndex)
        }

        fun onDrag(ev: PointerEvent) {
            caretLineScope?.apply {
                val dragLocalPos = MutableVec2f()
                uiNode.toLocal(ev.screenPosition, dragLocalPos)
                val charIndex = charIndexFromLocalX(dragLocalPos.x)
                caretLine?.let { line ->
                    line.caretPos.set(charIndex)
                    if (selectionCaretLine.value < selectionStartLine.value) {
                        line.selectionStart.set(line.textLine.length)
                    } else if (selectionCaretLine.value > selectionStartLine.value) {
                        line.selectionStart.set(0)
                    }
                }
            }
        }

        fun onSelectEnd() {
            isSelecting = false
            caretLine = null
            caretLineScope = null
        }

        fun onPointer(attributedText: AttributedTextScope, line: TextAreaLine, ev: PointerEvent) {
            if (isSelecting && ev.pointer.isDrag) {
                selectionCaretLine.set(line.lineIndex)
                caretLine = line
                caretLineScope = attributedText
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> TextAreaNode = { parent, surface -> TextAreaNode(parent, surface) }
    }
}

class TextAreaLine(val textLine: TextLine, val lineIndex: Int) {
    val caretPos = mutableStateOf(0)
    val selectionStart = mutableStateOf(0)
}
