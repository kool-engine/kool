package de.fabmax.kool.modules.ui2

import de.fabmax.kool.Clipboard
import de.fabmax.kool.InputManager
import de.fabmax.kool.LocalKeyCode
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MsdfFont
import de.fabmax.kool.util.TextCaretNavigation
import kotlin.math.max
import kotlin.math.min


interface TextAreaScope : UiScope {
    override val modifier: TextAreaModifier

    val linesHolder: LazyListScope

    fun installDefaultSelectionHandler() {
        val selStartLine = weakRememberState(-1)
        val selCaretLine = weakRememberState(-1)
        val selStartChar = weakRememberState(0)
        val selCaretChar = weakRememberState(0)

        modifier.onSelectionChanged = { startLine, caretLine, startChar, caretChar ->
            selStartLine.set(startLine)
            selCaretLine.set(caretLine)
            selStartChar.set(startChar)
            selCaretChar.set(caretChar)
        }
        modifier.selectionStartLine = selStartLine.use()
        modifier.selectionCaretLine = selCaretLine.use()
        modifier.selectionStartChar = selStartChar.use()
        modifier.selectionCaretChar = selCaretChar.use()
    }
}

open class TextAreaModifier(surface: UiSurface) : UiModifier(surface) {
    var bottomPadding: Dp by property(Dp(100f))
    var lineStartPadding: Dp by property(Dp(0f))
    var lineEndPadding: Dp by property(Dp(100f))

    var editorHandler: TextEditorHandler? by property(null)

    var selectionStartLine: Int by property(-1)
    var selectionCaretLine: Int by property(-1)
    var selectionStartChar: Int by property(0)
    var selectionCaretChar: Int by property(0)
    var onSelectionChanged: ((Int, Int, Int, Int) -> Unit)? by property(null)
}

fun <T: TextAreaModifier> T.onSelectionChanged(block: ((Int, Int, Int, Int) -> Unit)?): T {
    onSelectionChanged = block
    return this
}
fun <T: TextAreaModifier> T.editorHandler(handler: TextEditorHandler): T { editorHandler = handler; return this }
fun <T: TextAreaModifier> T.setCaretPos(line: Int, caretPos: Int): T {
    selectionStartLine = line
    selectionCaretLine = line
    selectionStartChar = caretPos
    selectionCaretChar = caretPos
    onSelectionChanged?.invoke(selectionStartLine, selectionCaretLine, selectionStartChar, selectionCaretChar)
    return this
}
fun <T: TextAreaModifier> T.setSelectionRange(startLine: Int, caretLine: Int, startPos: Int, caretPos: Int): T {
    selectionStartLine = startLine
    selectionCaretLine = caretLine
    selectionStartChar = startPos
    selectionCaretChar = caretPos
    onSelectionChanged?.invoke(selectionStartLine, selectionCaretLine, selectionStartChar, selectionCaretChar)
    return this
}

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
        val textAreaMod = modifier
        selectionHandler.updateSelectionRange()
        linesHolder.itemsIndexed(lines) { i, line ->
            AttributedText(line.textLine) {
                modifier.width(Grow.MinFit)

                if (this@TextAreaNode.modifier.onSelectionChanged != null) {
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

                    modifier.padding(start = textAreaMod.lineStartPadding, end = textAreaMod.lineEndPadding)
                    if (i == lines.lastIndex) {
                        modifier
                            .textAlignY(AlignmentY.Top)
                            .padding(bottom = textAreaMod.bottomPadding)
                    }

                    selectionHandler.applySelectionRange(this, line)
                }
            }
        }
    }

    override fun onKeyEvent(keyEvent: InputManager.KeyEvent) {
        if (keyEvent.isCharTyped) {
            editText("${keyEvent.typedChar}")

        } else if (keyEvent.isPressed) {
            when (keyEvent.keyCode) {
                InputManager.KEY_BACKSPACE -> {
                    if (selectionHandler.isEmptySelection) {
                        selectionHandler.moveCaretLeft(wordWise = keyEvent.isCtrlDown, select = true)
                    }
                    editText("")
                }
                InputManager.KEY_DEL -> {
                    if (selectionHandler.isEmptySelection) {
                        selectionHandler.moveCaretRight(wordWise = keyEvent.isCtrlDown, select = true)
                    }
                    editText("")
                }
                InputManager.KEY_ENTER -> editText("\n")
                InputManager.KEY_NP_ENTER -> editText("\n")
                InputManager.KEY_CURSOR_LEFT -> selectionHandler.moveCaretLeft(wordWise = keyEvent.isCtrlDown, select = keyEvent.isShiftDown)
                InputManager.KEY_CURSOR_RIGHT -> selectionHandler.moveCaretRight(wordWise = keyEvent.isCtrlDown, select = keyEvent.isShiftDown)
                InputManager.KEY_CURSOR_UP -> selectionHandler.moveCaretLineUp(select = keyEvent.isShiftDown)
                InputManager.KEY_CURSOR_DOWN -> selectionHandler.moveCaretLineDown(select = keyEvent.isShiftDown)
                InputManager.KEY_PAGE_UP -> selectionHandler.moveCaretPageUp(select = keyEvent.isShiftDown)
                InputManager.KEY_PAGE_DOWN -> selectionHandler.moveCaretPageDown(select = keyEvent.isShiftDown)
                InputManager.KEY_HOME -> selectionHandler.moveCaretLineStart(select = keyEvent.isShiftDown)
                InputManager.KEY_END -> selectionHandler.moveCaretLineEnd(select = keyEvent.isShiftDown)
                InputManager.KEY_ESC -> {
                    selectionHandler.clearSelection()
                    surface.requestFocus(null)
                }
                else -> {
                    if (keyEvent.isCtrlDown) {
                        when (keyEvent.localKeyCode) {
                            KEY_CODE_SELECT_ALL -> selectionHandler.selectAll()
                            KEY_CODE_PASTE -> Clipboard.getStringFromClipboard { paste -> paste?.let { editText(it) } }
                            KEY_CODE_COPY -> selectionHandler.copySelection()?.let { Clipboard.copyToClipboard(it) }
                            KEY_CODE_CUT -> {
                                selectionHandler.copySelection()?.let {
                                    Clipboard.copyToClipboard(it)
                                    editText("")
                                }
                            }
                            else -> { }
                        }
                    }
                }
            }
        }
    }

    private fun editText(text: String) {
        val editor = modifier.editorHandler ?: return
        val caretPos = if (selectionHandler.isEmptySelection) {
            editor.insertText(selectionHandler.selectionCaretLine, selectionHandler.selectionCaretChar, text, this)
        } else {
            editor.replaceText(
                selectionHandler.selectionFromLine, selectionHandler.selectionToLine,
                selectionHandler.selectionFromChar, selectionHandler.selectionToChar,
                text, this
            )
        }
        selectionHandler.selectionChanged(caretPos.y, caretPos.y, caretPos.x, caretPos.x)
    }

    private inner class SelectionHandler {
        private var isSelecting = false

        var selectionStartLine = 0
        var selectionCaretLine = 0
        var selectionStartChar = 0
        var selectionCaretChar = 0

        private var caretLine: TextAreaLine? = null
        private var caretLineScope: AttributedTextScope? = null

        val isReverseSelection: Boolean
            get() = selectionCaretLine < selectionStartLine
        val isEmptySelection: Boolean
            get() = selectionStartLine == selectionCaretLine && selectionStartChar == selectionCaretChar

        val selectionFromLine: Int
            get() = min(selectionStartLine, selectionCaretLine)
        val selectionToLine: Int
            get() = max(selectionStartLine, selectionCaretLine)
        val selectionFromChar: Int
            get() = when {
                isReverseSelection -> selectionCaretChar
                selectionStartLine == selectionCaretLine -> min(selectionStartChar, selectionCaretChar)
                else -> selectionStartChar
            }
        val selectionToChar: Int
            get() = when {
                isReverseSelection -> selectionStartChar
                selectionStartLine == selectionCaretLine -> max(selectionStartChar, selectionCaretChar)
                else -> selectionCaretChar
            }

        fun updateSelectionRange() {
            selectionStartLine = modifier.selectionStartLine
            selectionCaretLine = modifier.selectionCaretLine
            selectionStartChar = modifier.selectionStartChar
            selectionCaretChar = modifier.selectionCaretChar
            caretLine = null
            caretLineScope = null
        }

        fun applySelectionRange(attributedText: AttributedTextScope, line: TextAreaLine) {
            val from = selectionFromLine
            val to = selectionToLine

            var selCaretPos = 0
            var selStartPos = 0

            if (line.lineIndex in (from + 1) until to) {
                // line is completely in selection range
                selStartPos = 0
                selCaretPos = line.length

            } else if (line.lineIndex == selectionStartLine && selectionStartLine == selectionCaretLine) {
                // single-line selection
                selStartPos = selectionStartChar
                selCaretPos = selectionCaretChar

            } else if (line.lineIndex == selectionFromLine) {
                // multi-line selection, first selected line
                if (isReverseSelection) {
                    // reverse selection
                    selStartPos = line.length
                    selCaretPos = selectionCaretChar
                } else {
                    // forward selection
                    selStartPos = selectionStartChar
                    selCaretPos = line.length
                }
            } else if (line.lineIndex == selectionToLine) {
                // multi-line selection, last selected line
                if (isReverseSelection) {
                    // reverse selection
                    selStartPos = selectionStartChar
                    selCaretPos = 0
                } else {
                    // forward selection
                    selStartPos = 0
                    selCaretPos = selectionCaretChar
                }
            }

            if (line.lineIndex == selectionCaretLine) {
                caretLine = line
                caretLineScope = attributedText
            }

            attributedText.modifier
                .selectionRange(selStartPos, selCaretPos)
                .isCaretVisible(isFocused.use() && line.lineIndex == selectionCaretLine)
        }

        fun copySelection(): String? {
            return if (isEmptySelection) {
                null

            } else if (selectionStartLine == selectionCaretLine) {
                // single-line selection
                val fromChar = min(selectionStartChar, selectionCaretChar)
                val toChar = max(selectionStartChar, selectionCaretChar)
                lines[selectionFromLine].textLine.text.substring(fromChar, toChar)

            } else {
                // multi-line selection
                return buildString {
                    append(lines[selectionFromLine].textLine.text.substring(selectionFromChar)).append('\n')
                    for (i in (selectionFromLine + 1) until selectionToLine) {
                        append(lines[i].textLine.text).append('\n')
                    }
                    append(lines[selectionToLine].textLine.text.substring(0, selectionToChar))
                }
            }
        }

        fun clearSelection() {
            selectionChanged(selectionCaretLine, selectionCaretLine, selectionCaretChar, selectionCaretChar, false)
        }

        fun selectAll() {
            selectionChanged(0, lines.lastIndex, 0, lines.last().length, false)
        }

        fun selectWord(attributedText: AttributedTextScope, line: TextAreaLine, ev: PointerEvent) {
            val txt = line.textLine.text
            val charIndex = attributedText.charIndexFromLocalX(ev.position.x)
            val startChar = TextCaretNavigation.startOfWord(txt, charIndex)
            val caretChar = TextCaretNavigation.endOfWord(txt, charIndex)
            caretLine = line
            caretLineScope = attributedText
            selectionChanged(line.lineIndex, line.lineIndex, startChar, caretChar)
        }

        fun selectLine(attributedText: AttributedTextScope, line: TextAreaLine) {
            selectionChanged(line.lineIndex, line.lineIndex, 0, line.length)
            caretLine = line
            caretLineScope = attributedText
        }

        fun onSelectStart(attributedText: AttributedTextScope, line: TextAreaLine, ev: PointerEvent, isSelecting: Boolean) {
            requestFocus()

            this.isSelecting = isSelecting
            val charIndex = attributedText.charIndexFromLocalX(ev.position.x)
            caretLine = line
            caretLineScope = attributedText
            selectionChanged(line.lineIndex, line.lineIndex, charIndex, charIndex)
        }

        fun onDrag(ev: PointerEvent) {
            caretLineScope?.apply {
                val dragLocalPos = MutableVec2f()
                uiNode.toLocal(ev.screenPosition, dragLocalPos)
                val charIndex = charIndexFromLocalX(dragLocalPos.x)
                selectionChanged(selectionStartLine, selectionCaretLine, selectionStartChar, charIndex, false)
            }
        }

        fun onSelectEnd() {
            isSelecting = false
        }

        fun onPointer(attributedText: AttributedTextScope, line: TextAreaLine, ev: PointerEvent) {
            if (isSelecting && ev.pointer.isDrag) {
                caretLine = line
                caretLineScope = attributedText
                selectionChanged(selectionStartLine, line.lineIndex, selectionStartChar, selectionCaretChar, false)
            }
        }

        fun moveCaretLeft(wordWise: Boolean, select: Boolean) {
            var line = caretLine ?: return
            var txt = line.textLine.text

            if (selectionCaretChar == 0 && line.lineIndex > 0) {
                line = lines[line.lineIndex - 1]
                selectionCaretChar = line.length
                caretLine = line
                txt = line.textLine.text

                if (wordWise) {
                    selectionCaretChar = TextCaretNavigation.moveWordLeft(txt, selectionCaretChar)
                }
                selectionCaretLine = line.lineIndex
                if (!select) {
                    selectionStartLine = line.lineIndex
                    selectionStartChar = selectionCaretChar
                }

            } else if (wordWise) {
                selectionCaretChar = TextCaretNavigation.moveWordLeft(txt, selectionCaretChar)
            } else {
                selectionCaretChar = (selectionCaretChar - 1).clamp(0, txt.length)
            }
            if (!select) {
                selectionStartLine = selectionCaretLine
                selectionStartChar = selectionCaretChar
            }
            selectionChanged(selectionStartLine, selectionCaretLine, selectionStartChar, selectionCaretChar)
        }

        fun moveCaretRight(wordWise: Boolean, select: Boolean) {
            var line = caretLine ?: return
            var txt = line.textLine.text

            if (selectionCaretChar == txt.length && line.lineIndex < lines.lastIndex) {
                line = lines[line.lineIndex + 1]
                selectionCaretChar = 0
                caretLine = line
                txt = line.textLine.text

                if (wordWise) {
                    selectionCaretChar = TextCaretNavigation.moveWordRight(txt, selectionCaretChar)
                }
                selectionCaretLine = line.lineIndex
                if (!select) {
                    selectionStartLine = line.lineIndex
                    selectionStartChar = selectionCaretChar
                }

            } else if (wordWise) {
                selectionCaretChar = TextCaretNavigation.moveWordRight(txt, selectionCaretChar)
            } else {
                selectionCaretChar = (selectionCaretChar + 1).clamp(0, txt.length)
            }
            if (!select) {
                selectionStartLine = selectionCaretLine
                selectionStartChar = selectionCaretChar
            }
            selectionChanged(selectionStartLine, selectionCaretLine, selectionStartChar, selectionCaretChar)
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
            val caretX = line.textLine.charIndexToPx(selectionCaretChar)

            if (targetLine in lines.indices) {
                line = lines[targetLine]
                selectionCaretChar = line.textLine.charIndexFromPx(caretX)
                selectionCaretLine = line.lineIndex
            } else if (targetLine < 0) {
                line = lines[0]
                selectionCaretChar = 0
                selectionCaretLine = line.lineIndex
            } else if (targetLine > lines.lastIndex) {
                line = lines[lines.lastIndex]
                selectionCaretChar = line.length
                selectionCaretLine = line.lineIndex
            }
            caretLine = line

            if (!select) {
                selectionStartLine = selectionCaretLine
                selectionStartChar = selectionCaretChar
            }
            selectionChanged(selectionStartLine, selectionCaretLine, selectionStartChar, selectionCaretChar)
        }

        fun moveCaretLineStart(select: Boolean) {
            selectionCaretChar = 0
            if (!select) {
                selectionStartLine = selectionCaretLine
                selectionStartChar = selectionCaretChar
            }
            selectionChanged(selectionStartLine, selectionCaretLine, selectionStartChar, selectionCaretChar)
        }

        fun moveCaretLineEnd(select: Boolean) {
            val line = caretLine ?: return
            selectionCaretChar = line.length
            if (!select) {
                selectionStartLine = selectionCaretLine
                selectionStartChar = selectionCaretChar
            }
            selectionChanged(selectionStartLine, selectionCaretLine, selectionStartChar, selectionCaretChar)
        }

        fun selectionChanged(startLine: Int, caretLine: Int, startChar: Int, caretChar: Int, scrollToCaret: Boolean = true) {
            selectionStartLine = startLine
            selectionCaretLine = caretLine
            selectionStartChar = startChar
            selectionCaretChar = caretChar

            if (startLine != modifier.selectionStartLine
                || caretLine != modifier.selectionCaretLine
                || startChar != modifier.selectionStartChar
                || caretChar != modifier.selectionCaretChar) {

                modifier.setSelectionRange(startLine, caretLine, startChar, caretChar)
                resetCaretBlinkState()
                if (scrollToCaret) {
                    scrollToCaret()
                }
            }
        }

        fun resetCaretBlinkState() {
            (caretLineScope as? AttributedTextNode)?.resetCaretBlinkState()
        }

        fun scrollToCaret() {
            val scrState = linesHolder.state
            val bottomLinePad = linesHolder.modifier.extraItemsAfter + 2
            if (selectionCaretLine < scrState.itemsFrom) {
                scrState.itemsFrom = selectionCaretLine.toFloat()
            } else if (selectionCaretLine > scrState.itemsTo - bottomLinePad) {
                val visLines = scrState.itemsTo - scrState.itemsFrom.toInt() - bottomLinePad
                scrState.itemsFrom = max(0f, selectionCaretLine.toFloat() - visLines)
            }

            val scrollPad = 16f
            val caretX = Dp.fromPx(caretLine?.textLine?.charIndexToPx(selectionCaretChar) ?: 0f).value
            val scrLt = scrState.xScrollDp.value
            val scrRt = scrState.xScrollDp.value + scrState.viewWidthDp.value
            if (caretX - scrollPad < scrLt) {
                scrState.scrollDpX(caretX - scrLt - scrollPad)
            } else if (caretX + scrollPad * 4 > scrRt) {
                scrState.scrollDpX(caretX - scrRt + scrollPad * 4)
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

class TextAreaLine(val textLine: TextLine, val lineIndex: Int) {
    val length: Int get() = textLine.length

    override fun toString(): String {
        return "L$lineIndex: ${textLine.spans.joinToString { "\"${it.first}\"" }}"
    }
}

interface TextEditorHandler {
    fun insertText(line: Int, caret: Int, insertion: String, textAreaScope: TextAreaScope): Vec2i
    fun replaceText(selectionStartLine: Int, selectionEndLine: Int, selectionStartChar: Int, selectionEndChar: Int, replacement: String, textAreaScope: TextAreaScope): Vec2i
}

class DefaultTextEditorHandler(val text: MutableList<TextAreaLine> = mutableStateListOf()) : TextEditorHandler {
    var editAttribs: TextAttributes? = null

    override fun insertText(line: Int, caret: Int, insertion: String, textAreaScope: TextAreaScope): Vec2i {
        return replaceText(line, line, caret, caret, insertion, textAreaScope)
    }

    override fun replaceText(
        selectionStartLine: Int,
        selectionEndLine: Int,
        selectionStartChar: Int,
        selectionEndChar: Int,
        replacement: String,
        textAreaScope: TextAreaScope
    ): Vec2i {
        val startLine = this[selectionStartLine] ?: return Vec2i(selectionEndChar, selectionEndLine)
        val endLine = this[selectionEndLine] ?: return Vec2i(selectionEndChar, selectionEndLine)
        val before = startLine.before(selectionStartChar)
        val after = endLine.after(selectionEndChar)

        val caretPos = MutableVec2i()
        val attr = editAttribs ?: before.lastAttribs() ?: after.firstAttribs() ?: TextAttributes(MsdfFont.DEFAULT_FONT, Color.GRAY)
        val replaceLines = replacement.toLines(before.lineIndex, attr)

        caretPos.y = before.lineIndex + replaceLines.lastIndex
        val insertion = if (replaceLines.size == 1) {
            caretPos.x = before.length + replaceLines[0].length
            listOf(before + replaceLines[0] + after)
        } else {
            caretPos.x = replaceLines.last().length
            listOf(before + replaceLines[0]) + replaceLines.subList(1, replaceLines.lastIndex) + (replaceLines.last() + after)
        }

        insertLines(insertion, selectionStartLine, selectionEndLine)
        return caretPos
    }

    fun insertLines(insertLines: List<TextAreaLine>, insertFrom: Int, insertTo: Int) {
        val linesBefore = mutableListOf<TextAreaLine>()
        val linesAfter = mutableListOf<TextAreaLine>()
        if (insertFrom > 0) {
            linesBefore += text.subList(0, insertFrom)
        }
        if (insertTo < text.lastIndex) {
            linesAfter += text.subList(insertTo + 1, text.size)
        }

//        println("before:")
//        linesBefore.forEach { println("  $it") }
//        println("insert:")
//        insertLines.forEach { println("  $it") }
//        println("after:")
//        linesAfter.forEach { println("  $it") }

        text.clear()
        text += linesBefore
        insertLines.forEach {
            text += it.withLineIndex(text.size)
        }
        linesAfter.forEach {
            text += it.withLineIndex(text.size)
        }
    }

    fun String.toLines(startLineIndex: Int, attributes: TextAttributes): List<TextAreaLine> {
        return lines().mapIndexed { i, str -> TextAreaLine(TextLine(listOf(str to attributes)), startLineIndex + i) }
    }

    operator fun get(line: Int): TextAreaLine? {
        return if (text.isEmpty()) {
            null
        } else {
            text[line.clamp(0, text.lastIndex)]
        }
    }

    operator fun TextAreaLine.plus(other: TextAreaLine): TextAreaLine {
        return TextAreaLine(TextLine(sanitize(textLine.spans + other.textLine.spans)), lineIndex)
    }

    fun TextAreaLine.withLineIndex(lineIndex: Int): TextAreaLine {
        return if (this.lineIndex == lineIndex) {
            this
        } else {
            TextAreaLine(textLine, lineIndex)
        }
    }

    fun TextAreaLine.firstAttribs(): TextAttributes? {
        return if (textLine.spans.isNotEmpty()) {
            textLine.spans.first().second
        } else {
            null
        }
    }

    fun TextAreaLine.lastAttribs(): TextAttributes? {
        return if (textLine.spans.isNotEmpty()) {
            textLine.spans.last().second
        } else {
            null
        }
    }

    fun TextAreaLine.before(charIndex: Int): TextAreaLine {
        val spans = textLine.spans
        val newSpans = mutableListOf<Pair<String, TextAttributes>>()
        var i = 0
        var spanI = 0
        while (spanI < spans.size && i + spans[spanI].first.length < charIndex) {
            newSpans += spans[spanI]
            i += spans[spanI].first.length
            spanI++
        }
        newSpans += spans[spanI].before(charIndex - i)
        return TextAreaLine(TextLine(sanitize(newSpans)), lineIndex)
    }

    fun TextAreaLine.after(charIndex: Int): TextAreaLine {
        val spans = textLine.spans
        val newSpans = mutableListOf<Pair<String, TextAttributes>>()
        var i = 0
        var spanI = 0
        while (spanI < spans.size && i + spans[spanI].first.length < charIndex) {
            i += spans[spanI].first.length
            spanI++
        }
        if (spanI < spans.size) {
            newSpans += spans[spanI].after(charIndex - i)
            for (j in spanI + 1 until spans.size) {
                newSpans += spans[j]
            }
        }
        return TextAreaLine(TextLine(sanitize(newSpans)), lineIndex)
    }

    fun TextAreaLine.append(text: String): TextAreaLine {
        val spans = textLine.spans
        val newSpans = mutableListOf<Pair<String, TextAttributes>>()
        newSpans += spans
        newSpans[spans.lastIndex] = spans.last().append(text)
        return TextAreaLine(TextLine(sanitize(newSpans)), lineIndex)
    }

    fun Pair<String, TextAttributes>.before(index: Int): Pair<String, TextAttributes> {
        return first.substring(0, index) to second
    }

    fun Pair<String, TextAttributes>.after(index: Int): Pair<String, TextAttributes> {
        return first.substring(index) to second
    }

    fun Pair<String, TextAttributes>.append(text: String): Pair<String, TextAttributes> {
        return (first + text) to second
    }

    fun sanitize(spans: List<Pair<String, TextAttributes>>): List<Pair<String, TextAttributes>> {
        val newSpans = mutableListOf<Pair<String, TextAttributes>>()
        if (spans.isNotEmpty()) {
            var prevSpan = spans[0]
            newSpans += prevSpan
            for (i in 1 until spans.size) {
                val span = spans[i]
                if (span.second == prevSpan.second) {
                    prevSpan = prevSpan.append(span.first)
                    newSpans[newSpans.lastIndex] = prevSpan
                } else if (span.first.isNotEmpty()) {
                    prevSpan = span
                    newSpans += span
                }
            }
        }
        if (newSpans.size > 1 && newSpans[0].first.isEmpty()) {
            newSpans.removeAt(0)
        }
        return newSpans
    }
}