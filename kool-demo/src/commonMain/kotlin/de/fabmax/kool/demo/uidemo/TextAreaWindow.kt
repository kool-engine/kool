package de.fabmax.kool.demo.uidemo

import de.fabmax.kool.math.randomF
import de.fabmax.kool.math.randomI
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont
import kotlin.math.min
import kotlin.random.Random

class TextAreaWindow(uiDemo: UiDemo) : DemoWindow("Text Area", uiDemo) {

    private val lines = mutableStateListOf<TextLine>()

    init {
        windowDockable.setFloatingBounds(width = Dp(1200f), height = Dp(800f))
        val r = Random(randomI())
        for (i in 0 until 100) {
            val str = "$i: ${randomText(r)}"
            val spans = randomStyle(str, r)
            lines += TextLine(spans)
        }
    }

    override fun UiScope.modifyWindow() {
        modifier.backgroundColor(MdColor.GREY tone 900)
    }

    override fun UiScope.windowContent() = TextArea(
        ListTextLineProvider(lines),
        hScrollbarModifier = { it.margin(start = sizes.gap, end = sizes.gap * 2f,bottom = sizes.gap) },
        vScrollbarModifier = { it.margin(sizes.gap) }
    ) {
        modifier.padding(horizontal = sizes.gap)
        // make text area selectable
        installDefaultSelectionHandler()
        // make text area editable
        modifier.editorHandler(remember { DefaultTextEditorHandler(lines) })
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
            val italic = if (random.randomF() > 0.8f) MsdfFont.ITALIC_STD else MsdfFont.ITALIC_NONE
            val cutoff = if (size > 40f && weight >= MsdfFont.WEIGHT_EXTRA_BOLD) MsdfFont.CUTOFF_OUTLINED_THIN else MsdfFont.CUTOFF_SOLID
            val fgColor = random.randomI(MdColor.PALETTE.indices)
            val glowColor: Color? = if (random.randomF() > 0.3f) null else MdColor.PALETTE[random.randomI(MdColor.PALETTE.indices)]

            val j1 = min(str.length, j + random.randomI(4, 8))
            spans += str.substring(j, j1) to TextAttributes(
                font = MsdfFont(MsdfFont.DEFAULT_FONT_DATA, size, italic, weight, cutoff, glowColor),
                color = MdColor.PALETTE[fgColor],
            )
            j = j1
        }
        return spans
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