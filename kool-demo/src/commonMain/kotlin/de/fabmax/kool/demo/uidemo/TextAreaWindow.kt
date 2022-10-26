package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.math.Random
import de.fabmax.kool.math.randomI
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont
import kotlin.math.min

class TextAreaWindow(val uiDemo: UiDemo) : UiDemo.DemoWindow {

    private val windowState = WindowState().apply { setWindowSize(Dp(1200f), Dp(800f)) }

    private val texts = mutableListOf<TextLine>()

    init {
        val r = Random(randomI())
        val sizes = listOf(24f, 30f, 36f, 42f, 48f)
        val weights = listOf(MsdfFont.WEIGHT_EXTRA_LIGHT, MsdfFont.WEIGHT_LIGHT, MsdfFont.WEIGHT_REGULAR, MsdfFont.WEIGHT_BOLD, MsdfFont.WEIGHT_EXTRA_BOLD)
        for (i in 0 until 100) {
            val spans = mutableListOf<Pair<String, TextAttributes>>()
            var str = ""
            val w = r.randomI(5, 10)
            for (j in 0 .. w) {
                str += words[r.randomI(words.indices)]
                if (j < w) str += " "
            }
            var j = 0
            while (j < str.length) {
                val j1 = min(str.length, j + r.randomI(4, 8))

                val size = sizes[r.randomI(sizes.indices)]
                val weight = weights[r.randomI(weights.indices)]
                val fgColor = r.randomI(MdColor.PALETTE.indices)
                var bgColor: Int? = if (r.randomF() > 0.3f) null else r.randomI(MdColor.PALETTE.indices)
                if (bgColor == fgColor) {
                    bgColor = null
                }
                val glowColor: Color? = if (r.randomF() > 0.3f) null else MdColor.PALETTE[r.randomI(MdColor.PALETTE.indices)]

                val cutoff = if (size > 40f && weight >= MsdfFont.WEIGHT_EXTRA_BOLD) MsdfFont.CUTOFF_OUTLINED_THIN else MsdfFont.CUTOFF_SOLID

                spans += str.substring(j, j1) to TextAttributes(
                    font = MsdfFont(
                        sizePts = size,
                        italic = if (r.randomF() > 0.8f) MsdfFont.ITALIC_STD else MsdfFont.ITALIC_NONE,
                        weight = weight,
                        glowColor = glowColor,
                        cutoff = cutoff
                    ),
                    color = MdColor.PALETTE[fgColor],
                    background = bgColor?.let { MdColor.PALETTE[it] }
                )
                j = j1
            }
            texts += TextLine(spans)
        }
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
            items(texts) {
                AttributedText(it) { modifier.margin(horizontal = sizes.gap) }
            }
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
    }
}