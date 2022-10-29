package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.InputManager
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Random
import de.fabmax.kool.math.randomI
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont
import kotlin.math.max
import kotlin.math.min

class TextAreaWindow(val uiDemo: UiDemo) : UiDemo.DemoWindow {

    private val windowState = WindowState().apply { setWindowSize(Dp(1200f), Dp(800f)) }

    private val lines = mutableListOf<SelectableText>()

    init {
        val r = Random(randomI())
        for (i in 0 until 100) {
            val str = randomText(r)
            val spans = randomStyle(str, r)
            lines += SelectableText(TextLine(spans), i)
        }
    }

    private fun randomText(random: Random): String {
        var str = ""
        val w = random.randomI(5, 10)
        for (j in 0 .. w) {
            str += words[random.randomI(words.indices)]
            if (j < w) str += " "
        }
        return str
    }

    private fun randomStyle(str: String, random: Random): List<Pair<String, TextAttributes>> {
        val spans = mutableListOf<Pair<String, TextAttributes>>()
        var j = 0
        while (j < str.length) {
            val size = sizes[random.randomI(sizes.indices)]
            val weight = weights[random.randomI(weights.indices)]
            val cutoff = if (size > 40f && weight >= MsdfFont.WEIGHT_EXTRA_BOLD) MsdfFont.CUTOFF_OUTLINED_THIN else MsdfFont.CUTOFF_SOLID
            val fgColor = random.randomI(MdColor.PALETTE.indices)
            val glowColor: Color? = if (random.randomF() > 0.3f) null else MdColor.PALETTE[random.randomI(MdColor.PALETTE.indices)]
            var bgColor: Int? = if (random.randomF() > 0.3f) null else random.randomI(MdColor.PALETTE.indices)
            if (bgColor == fgColor) {
                bgColor = null
            }

            val j1 = min(str.length, j + random.randomI(4, 8))
            spans += str.substring(j, j1) to TextAttributes(
                font = MsdfFont(
                    sizePts = size,
                    italic = if (random.randomF() > 0.8f) MsdfFont.ITALIC_STD else MsdfFont.ITALIC_NONE,
                    weight = weight,
                    glowColor = glowColor,
                    cutoff = cutoff
                ),
                color = MdColor.PALETTE[fgColor],
                background = bgColor?.let { MdColor.PALETTE[it] }
            )
            j = j1
        }
        return spans
    }

    override val windowSurface: UiSurface = Window(windowState, name = "Text Area") {
        surface.sizes = uiDemo.selectedUiSize.use()
        surface.colors = uiDemo.selectedColors.use()

        TitleBar(onCloseAction = { uiDemo.closeWindow(this@TextAreaWindow, it.ctx) })
        WindowContent()
    }
    override val windowScope: WindowScope = windowSurface.windowScope!!

    fun UiScope.WindowContent() = Column(Grow.Std, Grow.Std) {
        LazyList(
            isGrowWidth = false,
            withHorizontalScrollbar = true,
            containerModifier = { it.backgroundColor(MdColor.GREY tone 900) },
            hScrollbarModifier = { it.margin(start = sizes.gap, end = sizes.gap * 2f,bottom = sizes.gap) },
            vScrollbarModifier = { it.margin(sizes.gap) }
        ) {
            val selectionHandler = weakRemember { LineSelectionHandler(this) }
            items(lines) { line ->
                AttributedText(line.textLine) {
                    modifier.margin(horizontal = sizes.gap)
                    selectionHandler.setSelectionRange(this, line)
                    selectionHandler.install(this, line)
                }
            }
        }
    }

    private class SelectableText(val textLine: TextLine, val lineIndex: Int) {
        val caretPos = mutableStateOf(0)
        val selectionStart = mutableStateOf(0)
    }

    private class LineSelectionHandler(val parentScope: UiScope) : UiScope by parentScope, Focusable {
        private var isSelecting = false
        private val selectionStartLine = mutableStateOf(-1)
        private val selectionCaretLine = mutableStateOf(-1)

        private var caretLine: SelectableText? = null
        private var caretLineScope: AttributedTextScope? = null

        val selectionFromLine: Int
            get() = min(selectionStartLine.value, selectionCaretLine.value)
        val selectionToLine: Int
            get() = max(selectionStartLine.value, selectionCaretLine.value)

        override val isFocused = mutableStateOf(false)

        fun setSelectionRange(attributedText: AttributedTextScope, line: SelectableText) {
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

        fun install(attributedText: AttributedTextScope, line: SelectableText) {
            attributedText.modifier
                .onClick { onDragStart(attributedText, line, it, false) }
                .onDragStart { onDragStart(attributedText, line, it, true) }
                .onDrag { onDrag(it) }
                .onDragEnd { onDragEnd() }
                .onPointer { onPointer(attributedText, line, it) }
        }

        fun clearSelection() {
            selectionStartLine.set(-1)
            selectionCaretLine.set(-1)
        }

        fun onDragStart(attributedText: AttributedTextScope, line: SelectableText, ev: PointerEvent, isSelecting: Boolean) {
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

        fun onDragEnd() {
            isSelecting = false
            caretLine = null
            caretLineScope = null
        }

        fun onPointer(attributedText: AttributedTextScope, line: SelectableText, ev: PointerEvent) {
            if (isSelecting && ev.pointer.isDrag) {
                selectionCaretLine.set(line.lineIndex)
                caretLine = line
                caretLineScope = attributedText
            }
        }

        override fun onFocusGain() {
            super.onFocusGain()
            println("got focus")
        }

        override fun onFocusLost() {
            super.onFocusLost()
            //clearSelection()
            println("lost focus")
        }

        override fun onKeyEvent(keyEvent: InputManager.KeyEvent) {
            println("key event")
        }
    }

    companion object {
        private val words = listOf(
            "time", "year", "people", "way", "day", "man", "thing", "woman", "life", "child", "world", "school",
            "state", "family", "student", "group", "country", "problem", "hand", "part", "place", "case", "week",
            "company", "system", "program", "question", "work", "government", "number", "night", "point", "home",
            "water", "room", "mother", "area", "money", "story", "fact", "month", "lot", "right", "study", "book",
            "eye", "job", "word", "business", "issue", "side", "kind", "head", "house", "service", "friend",
            "father", "power", "hour", "game", "line", "end", "member", "law", "car", "city", "community", "name",
            "president", "team", "minute", "idea", "kid", "body", "information", "back", "parent", "face", "others",
            "level", "office", "door", "health", "person", "art", "war", "history", "party", "result", "change",
            "morning", "reason", "research", "girl", "guy", "moment", "air", "teacher", "force", "education",

            "be", "have", "do", "say", "go", "can", "get", "would", "make", "know", "will", "think", "take", "see",
            "come", "could", "want", "look", "use", "find", "give", "tell", "work", "may", "should", "call", "try",
            "ask", "need", "feel", "become", "leave", "put", "mean", "keep", "let", "begin", "seem", "help", "talk",
            "turn", "start", "might", "show", "hear", "play", "run", "move", "like", "live", "believe", "hold",
            "bring", "happen", "must", "write", "provide", "sit", "stand", "lose", "pay", "meet", "include",
            "continue", "set", "learn", "change", "lead", "understand", "watch", "follow", "stop", "create", "speak",
            "read", "allow", "add", "spend", "grow", "open", "walk", "win", "offer", "remember", "love", "consider",
            "appear", "buy", "wait", "serve", "die", "send", "expect", "build", "stay", "fall", "cut", "reach",
            "kill", "remain"
        )

        private val sizes = listOf(24f, 30f, 36f, 42f, 48f)
        private val weights = listOf(MsdfFont.WEIGHT_EXTRA_LIGHT, MsdfFont.WEIGHT_LIGHT, MsdfFont.WEIGHT_REGULAR, MsdfFont.WEIGHT_BOLD, MsdfFont.WEIGHT_EXTRA_BOLD)
    }
}