package de.fabmax.kool.modules.ui2

import de.fabmax.kool.Clipboard
import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.LocalKeyCode
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MsdfFont
import de.fabmax.kool.util.TextCaretNavigation
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.min


interface TextAreaScope : UiScope {
    override val modifier: TextAreaModifier

    val linesHolder: LazyListScope

    fun installDefaultSelectionHandler() {
        val selStartLine = remember(-1)
        val selCaretLine = remember(-1)
        val selStartChar = remember(0)
        val selCaretChar = remember(0)

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
    var lineStartPadding: Dp by property(Dp(0f))
    var lineEndPadding: Dp by property(Dp(100f))
    var firstLineTopPadding: Dp by property(Dp(0f))
    var lastLineBottomPadding: Dp by property(Dp(16f))

    var editorHandler: TextEditorHandler? by property(null)

    var selectionStartLine: Int by property(-1)
    var selectionCaretLine: Int by property(-1)
    var selectionStartChar: Int by property(0)
    var selectionCaretChar: Int by property(0)
    var onSelectionChanged: ((Int, Int, Int, Int) -> Unit)? by property(null)
}

fun <T: TextAreaModifier> T.lineStartPadding(padding: Dp): T { lineStartPadding = padding; return this }
fun <T: TextAreaModifier> T.lineEndPadding(padding: Dp): T { lineEndPadding = padding; return this }
fun <T: TextAreaModifier> T.firstLineTopPadding(padding: Dp): T { firstLineTopPadding = padding; return this }
fun <T: TextAreaModifier> T.lastLineBottomPadding(padding: Dp): T { lastLineBottomPadding = padding; return this }

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
    lineProvider: TextLineProvider,
    width: Dimension = Grow.Std,
    height: Dimension = Grow.Std,
    withVerticalScrollbar: Boolean = true,
    withHorizontalScrollbar: Boolean = true,
    scrollbarColor: Color? = null,
    scrollPaneModifier: ((ScrollPaneModifier) -> Unit)? = null,
    vScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    hScrollbarModifier: ((ScrollbarModifier) -> Unit)? = null,
    state: LazyListState = rememberListState(),
    scopeName: String? = null,
    block: TextAreaScope.() -> Unit
) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val textArea = uiNode.createChild(scopeName, TextAreaNode::class, TextAreaNode.factory)
    textArea.listState = state
    textArea.modifier
        .size(width, height)
        .onWheelX { state.scrollDpX(it.pointer.scroll.x * -20f) }
        .onWheelY { state.scrollDpY(it.pointer.scroll.y * -50f) }

    textArea.setupContent(
        lineProvider,
        withVerticalScrollbar,
        withHorizontalScrollbar,
        scrollbarColor,
        scrollPaneModifier,
        vScrollbarModifier,
        hScrollbarModifier,
        block
    )
}

open class TextAreaNode(parent: UiNode?, surface: UiSurface) : BoxNode(parent, surface), TextAreaScope, Focusable {
    override val modifier = TextAreaModifier(surface)
    override val isFocused = mutableStateOf(false)

    private lateinit var lineProvider: TextLineProvider

    lateinit var listState: LazyListState
    override lateinit var linesHolder: LazyListNode
    private val selectionHandler = SelectionHandler()

    fun setupContent(
        lineProvider: TextLineProvider,
        withVerticalScrollbar: Boolean,
        withHorizontalScrollbar: Boolean,
        scrollbarColor: Color?,
        scrollPaneModifier: ((ScrollPaneModifier) -> Unit)?,
        vScrollbarModifier: ((ScrollbarModifier) -> Unit)?,
        hScrollbarModifier: ((ScrollbarModifier) -> Unit)?,
        block: TextAreaScope.() -> Unit
    ) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

        this.lineProvider = lineProvider

        ScrollPane(listState) {
            modifier.width(Grow.MinFit)
            scrollPaneModifier?.let { it(modifier) }

            linesHolder = uiNode.createChild(null, LazyListNode::class, LazyListNode.factory)
            linesHolder.state = listState
            linesHolder.modifier
                .orientation(ListOrientation.Vertical)
                .layout(ColumnLayout)
                .width(Grow.MinFit)

            block.invoke(this@TextAreaNode)

            setText(lineProvider)
        }

        if (withVerticalScrollbar) {
            VerticalScrollbar {
                lazyListAware(listState, ScrollbarOrientation.Vertical, ListOrientation.Vertical, scrollbarColor, vScrollbarModifier)
            }
        }
        if (withHorizontalScrollbar) {
            HorizontalScrollbar {
                lazyListAware(listState, ScrollbarOrientation.Horizontal, ListOrientation.Vertical, scrollbarColor, hScrollbarModifier)
            }
        }

    }

    private fun setText(lineProvider: TextLineProvider) {
        val textAreaMod = this@TextAreaNode.modifier
        selectionHandler.updateSelectionRange()
        linesHolder.indices(lineProvider.size) { lineIndex ->
            val line = lineProvider[lineIndex]
            setupTextLine(this, line, lineIndex, textAreaMod, lineProvider)
        }
    }

    protected open fun setupTextLine(
        scope: UiScope,
        line: TextLine,
        lineIndex: Int,
        textAreaMod: TextAreaModifier,
        lineProvider: TextLineProvider,
    ): UiScope = scope.AttributedText(line) {
        modifier.width(Grow.MinFit)

        if (this@TextAreaNode.modifier.onSelectionChanged != null) {
            modifier
                .onClick {
                    when (it.pointer.leftButtonRepeatedClickCount) {
                        1 -> selectionHandler.onSelectStart(this, lineIndex, it, false)
                        2 -> selectionHandler.selectWord(this, line.text, lineIndex, it)
                        3 -> selectionHandler.selectLine(this, line.text, lineIndex)
                    }
                }
                .onDragStart { selectionHandler.onSelectStart(this, lineIndex, it, true) }
                .onDrag { selectionHandler.onDrag(it) }
                .onDragEnd { selectionHandler.onSelectEnd() }
                .onPointer { selectionHandler.onPointer(this, lineIndex, it) }

            modifier.padding(start = textAreaMod.lineStartPadding, end = textAreaMod.lineEndPadding)
            if (lineIndex == 0) {
                modifier
                    .textAlignY(AlignmentY.Bottom)
                    .padding(top = textAreaMod.firstLineTopPadding)
            }
            if (lineIndex == lineProvider.lastIndex) {
                modifier
                    .textAlignY(AlignmentY.Top)
                    .padding(bottom = textAreaMod.lastLineBottomPadding)
            }

            selectionHandler.applySelectionRange(this, line, lineIndex)
        }
    }

    override fun onKeyEvent(keyEvent: KeyEvent) {
        if (keyEvent.isCharTyped) {
            editText("${keyEvent.typedChar}")

        } else if (keyEvent.isPressed) {
            when (keyEvent.keyCode) {
                KeyboardInput.KEY_BACKSPACE -> {
                    if (selectionHandler.isEmptySelection) {
                        selectionHandler.moveCaretLeft(wordWise = keyEvent.isCtrlDown, select = true)
                    }
                    editText("")
                }
                KeyboardInput.KEY_DEL -> {
                    if (selectionHandler.isEmptySelection) {
                        selectionHandler.moveCaretRight(wordWise = keyEvent.isCtrlDown, select = true)
                    }
                    editText("")
                }
                KeyboardInput.KEY_ENTER -> editText("\n")
                KeyboardInput.KEY_NP_ENTER -> editText("\n")
                KeyboardInput.KEY_CURSOR_LEFT -> selectionHandler.moveCaretLeft(wordWise = keyEvent.isCtrlDown, select = keyEvent.isShiftDown)
                KeyboardInput.KEY_CURSOR_RIGHT -> selectionHandler.moveCaretRight(wordWise = keyEvent.isCtrlDown, select = keyEvent.isShiftDown)
                KeyboardInput.KEY_CURSOR_UP -> selectionHandler.moveCaretLineUp(select = keyEvent.isShiftDown)
                KeyboardInput.KEY_CURSOR_DOWN -> selectionHandler.moveCaretLineDown(select = keyEvent.isShiftDown)
                KeyboardInput.KEY_PAGE_UP -> selectionHandler.moveCaretPageUp(select = keyEvent.isShiftDown)
                KeyboardInput.KEY_PAGE_DOWN -> selectionHandler.moveCaretPageDown(select = keyEvent.isShiftDown)
                KeyboardInput.KEY_HOME -> selectionHandler.moveCaretLineStart(select = keyEvent.isShiftDown)
                KeyboardInput.KEY_END -> selectionHandler.moveCaretLineEnd(select = keyEvent.isShiftDown)
                KeyboardInput.KEY_ESC -> {
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

        private val caretLine: TextLine?
            get() = if (selectionCaretLine in 0 until lineProvider.size) lineProvider[selectionCaretLine] else null
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
            caretLineScope = null
        }

        fun applySelectionRange(attributedText: AttributedTextScope, line: TextLine, lineIndex: Int) {
            val from = selectionFromLine
            val to = selectionToLine

            var selCaretPos = 0
            var selStartPos = 0

            if (lineIndex in (from + 1) until to) {
                // line is completely in selection range
                selStartPos = 0
                selCaretPos = line.length

            } else if (lineIndex == selectionStartLine && selectionStartLine == selectionCaretLine) {
                // single-line selection
                selStartPos = selectionStartChar
                selCaretPos = selectionCaretChar

            } else if (lineIndex == selectionFromLine) {
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
            } else if (lineIndex == selectionToLine) {
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

            if (lineIndex == selectionCaretLine) {
                caretLineScope = attributedText
            }

            attributedText.modifier
                .selectionRange(selStartPos, selCaretPos)
                .isCaretVisible(isFocused.use() && lineIndex == selectionCaretLine)
        }

        fun copySelection(): String? {
            return if (isEmptySelection) {
                null

            } else if (selectionStartLine == selectionCaretLine) {
                // single-line selection
                val fromChar = min(selectionStartChar, selectionCaretChar)
                val toChar = max(selectionStartChar, selectionCaretChar)
                lineProvider[selectionFromLine].text.substring(fromChar, toChar)

            } else {
                // multi-line selection
                return buildString {
                    append(lineProvider[selectionFromLine].text.substring(selectionFromChar)).append('\n')
                    for (i in (selectionFromLine + 1) until selectionToLine) {
                        append(lineProvider[i].text).append('\n')
                    }
                    append(lineProvider[selectionToLine].text.substring(0, selectionToChar))
                }
            }
        }

        fun clearSelection() {
            selectionChanged(selectionCaretLine, selectionCaretLine, selectionCaretChar, selectionCaretChar, false)
        }

        fun selectAll() {
            selectionChanged(0, lineProvider.lastIndex, 0, lineProvider[lineProvider.lastIndex].length, false)
        }

        fun selectWord(attributedText: AttributedTextScope, text: String, lineIndex: Int, ev: PointerEvent) {
            val charIndex = attributedText.charIndexFromLocalX(ev.position.x)
            val startChar = TextCaretNavigation.startOfWord(text, charIndex)
            val caretChar = TextCaretNavigation.endOfWord(text, charIndex)
            caretLineScope = attributedText
            selectionChanged(lineIndex, lineIndex, startChar, caretChar)
        }

        fun selectLine(attributedText: AttributedTextScope, text: String, lineIndex: Int) {
            selectionChanged(lineIndex, lineIndex, 0, text.length)
            caretLineScope = attributedText
        }

        fun onSelectStart(attributedText: AttributedTextScope, lineIndex: Int, ev: PointerEvent, isSelecting: Boolean) {
            requestFocus()

            this.isSelecting = isSelecting
            val charIndex = attributedText.charIndexFromLocalX(ev.position.x)
            caretLineScope = attributedText
            selectionChanged(lineIndex, lineIndex, charIndex, charIndex)
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

        fun onPointer(attributedText: AttributedTextScope, lineIndex: Int, ev: PointerEvent) {
            if (isSelecting && ev.pointer.isDrag) {
                caretLineScope = attributedText
                selectionChanged(selectionStartLine, lineIndex, selectionStartChar, selectionCaretChar, false)
            }
        }

        fun moveCaretLeft(wordWise: Boolean, select: Boolean) {
            caretLine?.text?.let { txt ->
                if (selectionCaretChar == 0 && selectionCaretLine > 0) {
                    selectionCaretLine--
                    val line = lineProvider[selectionCaretLine]
                    val newTxt = line.text
                    selectionCaretChar = line.length

                    if (wordWise) {
                        selectionCaretChar = TextCaretNavigation.moveWordLeft(newTxt, selectionCaretChar)
                    }
                    if (!select) {
                        selectionStartLine = selectionCaretLine
                        selectionStartChar = selectionCaretChar
                    }

                } else if (wordWise) {
                    selectionCaretChar = TextCaretNavigation.moveWordLeft(txt, selectionCaretChar)
                } else {
                    selectionCaretChar = (selectionCaretChar - 1).clamp(0, txt.length)
                }
            }
            if (!select) {
                selectionStartLine = selectionCaretLine
                selectionStartChar = selectionCaretChar
            }
            selectionChanged(selectionStartLine, selectionCaretLine, selectionStartChar, selectionCaretChar)
        }

        fun moveCaretRight(wordWise: Boolean, select: Boolean) {
            caretLine?.text?.let { txt ->
                if (selectionCaretChar == txt.length && selectionCaretLine < lineProvider.lastIndex) {
                    selectionCaretLine++
                    val line = lineProvider[selectionCaretLine]
                    val newTxt = line.text
                    selectionCaretChar = 0

                    if (wordWise) {
                        selectionCaretChar = TextCaretNavigation.moveWordRight(newTxt, selectionCaretChar)
                    }
                    if (!select) {
                        selectionStartLine = selectionCaretLine
                        selectionStartChar = selectionCaretChar
                    }

                } else if (wordWise) {
                    selectionCaretChar = TextCaretNavigation.moveWordRight(txt, selectionCaretChar)
                } else {
                    selectionCaretChar = (selectionCaretChar + 1).clamp(0, txt.length)
                }
            }
            if (!select) {
                selectionStartLine = selectionCaretLine
                selectionStartChar = selectionCaretChar
            }
            selectionChanged(selectionStartLine, selectionCaretLine, selectionStartChar, selectionCaretChar)
        }

        fun moveCaretLineUp(select: Boolean) {
            moveCaretToLine(selectionCaretLine - 1, select)
        }

        fun moveCaretLineDown(select: Boolean) {
            moveCaretToLine(selectionCaretLine + 1, select)
        }

        fun moveCaretPageUp(select: Boolean) {
            val bottomLinePad = 2
            val numPageLines = max(1, linesHolder.state.numVisibleItems - bottomLinePad)
            moveCaretToLine(selectionCaretLine - numPageLines, select)
        }

        fun moveCaretPageDown(select: Boolean) {
            val bottomLinePad = 2
            val numPageLines = max(1, linesHolder.state.numVisibleItems - bottomLinePad)
            moveCaretToLine(selectionCaretLine + numPageLines, select)
        }

        private fun moveCaretToLine(targetLine: Int, select: Boolean) {
            val line = caretLine ?: return
            val caretX = line.charIndexToPx(selectionCaretChar)

            if (targetLine in 0 until lineProvider.size) {
                selectionCaretChar = lineProvider[targetLine].charIndexFromPx(caretX)
                selectionCaretLine = targetLine
            } else if (targetLine < 0) {
                selectionCaretChar = 0
                selectionCaretLine = 0
            } else if (targetLine > lineProvider.lastIndex) {
                selectionCaretChar = lineProvider[lineProvider.lastIndex].length
                selectionCaretLine = lineProvider.lastIndex
            }

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
            scrState.scrollToItem.set(selectionCaretLine)

            val scrollPad = 16f
            val caretX = Dp.fromPx(caretLine?.charIndexToPx(selectionCaretChar) ?: 0f).value
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

interface TextLineProvider {
    val size: Int
    val lastIndex: Int get() = size - 1
    operator fun get(index: Int): TextLine
}

class ListTextLineProvider(val lines: MutableList<TextLine> = mutableStateListOf()) : TextLineProvider {
    override val size: Int get() = lines.size
    override operator fun get(index: Int) = lines[index]
}

interface TextEditorHandler {
    fun insertText(line: Int, caret: Int, insertion: String, textAreaScope: TextAreaScope): Vec2i
    fun replaceText(selectionStartLine: Int, selectionEndLine: Int, selectionStartChar: Int, selectionEndChar: Int, replacement: String, textAreaScope: TextAreaScope): Vec2i
}

class DefaultTextEditorHandler(val text: MutableList<TextLine> = mutableStateListOf()) : TextEditorHandler {
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
        val replaceLines = replacement.toLines(attr)

        caretPos.y = selectionStartLine + replaceLines.lastIndex
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

    private fun insertLines(insertLines: List<TextLine>, insertFrom: Int, insertTo: Int) {
        val linesBefore = mutableListOf<TextLine>()
        val linesAfter = mutableListOf<TextLine>()
        if (insertFrom > 0) {
            linesBefore += text.subList(0, insertFrom)
        }
        if (insertTo < text.lastIndex) {
            linesAfter += text.subList(insertTo + 1, text.size)
        }

        text.clear()
        text += linesBefore
        text += insertLines
        text += linesAfter
    }

    fun String.toLines(attributes: TextAttributes): List<TextLine> {
        return lines().map { str -> TextLine(listOf(str to attributes)) }
    }

    operator fun get(line: Int): TextLine? {
        return if (text.isEmpty()) {
            null
        } else {
            text[line.clamp(0, text.lastIndex)]
        }
    }

    operator fun TextLine.plus(other: TextLine): TextLine {
        return TextLine(sanitize(spans + other.spans))
    }

    fun TextLine.firstAttribs(): TextAttributes? {
        return if (spans.isNotEmpty()) {
            spans.first().second
        } else {
            null
        }
    }

    fun TextLine.lastAttribs(): TextAttributes? {
        return if (spans.isNotEmpty()) {
            spans.last().second
        } else {
            null
        }
    }

    fun TextLine.before(charIndex: Int): TextLine {
        val newSpans = mutableListOf<Pair<String, TextAttributes>>()
        var i = 0
        var spanI = 0
        while (spanI < spans.size && i + spans[spanI].first.length < charIndex) {
            newSpans += spans[spanI]
            i += spans[spanI].first.length
            spanI++
        }
        newSpans += spans[spanI].before(charIndex - i)
        return TextLine(sanitize(newSpans))
    }

    fun TextLine.after(charIndex: Int): TextLine {
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
        return TextLine(sanitize(newSpans))
    }

    fun TextLine.append(text: String): TextLine {
        val newSpans = mutableListOf<Pair<String, TextAttributes>>()
        newSpans += spans
        newSpans[spans.lastIndex] = spans.last().append(text)
        return TextLine(sanitize(newSpans))
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