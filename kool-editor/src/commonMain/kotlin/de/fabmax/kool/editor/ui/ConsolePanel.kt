package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.*
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class ConsolePanel(ui: EditorUi) : EditorPanel("Console", Icons.medium.console, ui) {

    private val logMessages = RingBuffer<LogMessage>(maxMessages)
    private val filteredLogMessages = RingBuffer<LogMessage>(maxMessages)
    private val lineProvider = LogLineProvider()
    private val logLock = SynchronizedObject()

    private val isScrollLock = mutableStateOf(true)

    private var font = ui.consoleFont.value
    private var fontSize = 0f

    private var minLevel = mutableStateOf(Log.Level.DEBUG)
    private var messageFilter: Regex? = null

    private var notificationLevel: Log.Level = Log.Level.TRACE

    override val windowSurface = editorPanelWithPanelBar(backgroundColor = { colors.backgroundVariant }) {
        checkConsoleFont()

        // clear notification bubble if console is visible
        notificationBubble.set(null)
        notificationLevel = Log.Level.TRACE

        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, icon) {
                Row {
                    // register drag callbacks, to block window drag
                    modifier
                        .onDragStart {  }
                        .alignY(AlignmentY.Center)

                    divider(colors.strongDividerColor, marginStart = sizes.largeGap, marginEnd = sizes.largeGap, verticalMargin = sizes.smallGap)

                    Text("Level:") {
                        modifier.alignY(AlignmentY.Center)
                    }
                    ComboBox {
                        defaultComboBoxStyle()
                        modifier
                            .width(sizes.baseSize * 2.5f)
                            .margin(horizontal = sizes.gap)
                            .alignY(AlignmentY.Center)
                            .items(Log.Level.entries)
                            .selectedIndex(minLevel.use().ordinal)
                            .onItemSelected {
                                minLevel.set(Log.Level.entries[it])
                                updateFilter()
                            }
                    }

                    divider(colors.secondaryAlpha(0.75f), marginStart = sizes.largeGap, marginEnd = sizes.largeGap, verticalMargin = sizes.smallGap)

                    Text("Filter:") {
                        modifier.alignY(AlignmentY.Center)
                    }
                    var filterText by remember("")
                    TextField(filterText) {
                        modifier
                            .width(sizes.baseSize * 5)
                            .margin(horizontal = sizes.gap)
                            .colors(lineColor = colors.secondaryVariant, lineColorFocused = colors.secondary)
                            .alignY(AlignmentY.Center)
                            .hint("Text or regex")
                            .onEnterPressed { surface.requestFocus(null) }
                            .onChange {
                                filterText = it
                                messageFilter = if (it.isBlank()) null else {
                                    try {
                                        Regex(it)
                                    } catch (e: Exception) {
                                        logW { "Invalid filter regex: ${e.message}" }
                                        messageFilter
                                    }
                                }
                                updateFilter()
                            }

                        defaultTextfieldStyle()
                    }
                }

                Box(width = Grow.Std) { }

                Box(height = Grow.Std) {
                    modifier.onDragStart {  }
                    iconButton(Icons.small.scrollLock, "Scroll to end", isScrollLock.use()) {
                        isScrollLock.set(!isScrollLock.value)
                    }
                }
            }

            synchronized(logLock) {
                console()
            }
        }
    }

    init {
        Log.printer = LogPrinter { lvl: Log.Level, tag: String?, message: String ->
            synchronized(logLock) {
                val msg = LogMessage(lvl, tag, message, Clock.System.now(), Time.frameCount)
                logMessages += msg
                if (msg.isAccepted) {
                    filteredLogMessages += msg
                    windowSurface.triggerUpdate()
                    checkNotification(msg)

                    // also forward log message to system out
                    println(msg)
                }
            }
        }
        // set minimum log level to TRACE, actual message filtering is done dynamically within this class
        Log.level = Log.Level.TRACE
    }

    private fun checkNotification(msg: LogMessage) {
        if (msg.level.level >= Log.Level.WARN.level
            && msg.level.level >= notificationLevel.level
            && windowDockable.dockedTo.value?.isOnTop(windowDockable) == false) {

            val notiColor = levelFonts[msg.level]?.background ?: Color.MAGENTA
            notificationBubble.set(NotificationBubble(notiColor))
        }
    }

    private fun updateFilter() {
        synchronized(logLock) {
            filteredLogMessages.clear()
            filteredLogMessages += logMessages.filter { it.isAccepted }
        }
    }

    private fun UiScope.console() {
        val listState = rememberListState()
        TextArea(
            lineProvider = lineProvider,
            state = listState,
            scrollPaneModifier = { it.margin(horizontal = sizes.gap) },
            hScrollbarModifier = defaultScrollbarModifierH(),
            vScrollbarModifier = defaultScrollbarModifierV()
        ) {
            modifier
                .lastLineBottomPadding(sizes.largeGap)
                .backgroundColor(null)
                .onWheelY { ev ->
                    if (ev.pointer.scroll.y > 0.0) {
                        isScrollLock.set(false)
                    } else if (ev.pointer.scroll.y < 0.0 && listState.itemsTo == listState.numTotalItems - 1) {
                        isScrollLock.set(true)
                    }
                }

            // make text area selectable
            installDefaultSelectionHandler()

            linesHolder.modifier.isAutoScrollToEnd(isScrollLock.use())
        }
    }

    private fun UiScope.checkConsoleFont() {
        if (sizes.normalText.sizePts != fontSize || font != ui.consoleFont.use()) {
            font = ui.consoleFont.use()
            fontSize = sizes.normalText.sizePts
            updateFonts(font, fontSize)

            synchronized(logLock) {
                logMessages.forEach { it.updateText() }
            }
        }
    }

    private inner class LogLineProvider : TextLineProvider {
        override val size: Int get() = filteredLogMessages.size
        override fun get(index: Int): TextLine {
            val logLine = filteredLogMessages[index]
            if (!logLine.isTextValid) {
                logLine.updateText()
            }
            return logLine.text
        }
    }

    private inner class LogMessage(val level: Log.Level, val tag: String?, message: String, time: Instant, val frameIdx: Int) {
        val fmtTime: String = formatTime(time)

        var isTextValid = false

        val fullMessage = message
        val message: String = if (message.length < maxMessageLen) message else message.substring(0, maxMessageLen) + "…"
        var text: TextLine = makeTextLine()
            private set

        val isAccepted: Boolean
            get() {
                if (level.level < minLevel.value.level) return false
                val filter = messageFilter ?: return true
                return filter.containsMatchIn(message) || (tag != null && filter.containsMatchIn(tag))
            }

        fun updateText() {
            text = makeTextLine()
        }

        private fun makeTextLine(): TextLine {
            isTextValid = level in levelFonts
            val spans = mutableListOf<Pair<String, TextAttributes>>()
            spans += fmtTime to timeFont
            spans += " f:${fmtFrameCnt(frameIdx, 4)} " to frameFont
            spans += " ${level.indicator} " to (levelFonts[level] ?: defaultTextAttrs)

            if ('\u001b' in message && ansiRegex.containsMatchIn(message)) {
                decodeAnsiMessage(" $message", spans, messageFonts[level] ?: defaultTextAttrs)
            } else {
                spans += " $message" to (messageFonts[level] ?: defaultTextAttrs)
            }

            tag?.let {
                spans += " [${tag}]" to longTagFont
            }
            return TextLine(spans)
        }

        private fun decodeAnsiMessage(message: String, spans: MutableList<Pair<String, TextAttributes>>, baseStyle: TextAttributes) {
            var style = baseStyle
            var startIndex = 0
            var match = ansiRegex.find(message)
            while (match != null) {
                if (match.range.first > startIndex) {
                    spans += message.substring(startIndex..<match.range.first) to style
                }

                try {
                    val ansiCode = match.groupValues[1].toInt()
                    when (ansiCode) {
                        0 -> style = baseStyle
                        1 -> style = TextAttributes(style.font.copy(weight = MsdfFont.WEIGHT_BOLD), style.color, style.background)

                        30 -> style = TextAttributes(style.font, MdColor.GREY tone 900, style.background)
                        31 -> style = TextAttributes(style.font, MdColor.RED tone 800, style.background)
                        32 -> style = TextAttributes(style.font, MdColor.GREEN tone 800, style.background)
                        33 -> style = TextAttributes(style.font, MdColor.AMBER tone 800, style.background)
                        34 -> style = TextAttributes(style.font, MdColor.BLUE tone 800, style.background)
                        35 -> style = TextAttributes(style.font, MdColor.PURPLE tone 800, style.background)
                        36 -> style = TextAttributes(style.font, MdColor.CYAN tone 800, style.background)
                        37 -> style = TextAttributes(style.font, MdColor.GREY tone 300, style.background)
                        90 -> style = TextAttributes(style.font, MdColor.GREY tone 600, style.background)
                        91 -> style = TextAttributes(style.font, MdColor.RED tone 500, style.background)
                        92 -> style = TextAttributes(style.font, MdColor.LIGHT_GREEN tone 500, style.background)
                        93 -> style = TextAttributes(style.font, MdColor.YELLOW tone 500, style.background)
                        94 -> style = TextAttributes(style.font, MdColor.LIGHT_BLUE tone 500, style.background)
                        95 -> style = TextAttributes(style.font, MdColor.PURPLE tone 300, style.background)
                        96 -> style = TextAttributes(style.font, MdColor.CYAN tone 500, style.background)
                        97 -> style = TextAttributes(style.font, MdColor.GREY tone 100, style.background)

                        40 -> style = TextAttributes(style.font, style.color, MdColor.GREY tone 900)
                        41 -> style = TextAttributes(style.font, style.color, MdColor.RED tone 800)
                        42 -> style = TextAttributes(style.font, style.color, MdColor.GREEN tone 800)
                        43 -> style = TextAttributes(style.font, style.color, MdColor.AMBER tone 800)
                        44 -> style = TextAttributes(style.font, style.color, MdColor.BLUE tone 800)
                        45 -> style = TextAttributes(style.font, style.color, MdColor.PURPLE tone 800)
                        46 -> style = TextAttributes(style.font, style.color, MdColor.CYAN tone 800)
                        47 -> style = TextAttributes(style.font, style.color, MdColor.GREY tone 300)
                        100 -> style = TextAttributes(style.font, style.color, MdColor.GREY tone 600)
                        101 -> style = TextAttributes(style.font, style.color, MdColor.RED tone 500)
                        102 -> style = TextAttributes(style.font, style.color, MdColor.LIGHT_GREEN tone 500)
                        103 -> style = TextAttributes(style.font, style.color, MdColor.YELLOW tone 500)
                        104 -> style = TextAttributes(style.font, style.color, MdColor.LIGHT_BLUE tone 500)
                        105 -> style = TextAttributes(style.font, style.color, MdColor.PURPLE tone 300)
                        106 -> style = TextAttributes(style.font, style.color, MdColor.CYAN tone 500)
                        107 -> style = TextAttributes(style.font, style.color, MdColor.GREY tone 100)
                    }
                } catch (e: Exception) {
                    logE { "Invalid ANSI code in message: ${message.replace("\u001b", "ESC")}" }
                }
                startIndex = match.range.last + 1
                match = ansiRegex.find(message, startIndex)
            }
            if (startIndex < message.length) {
                spans += message.substring(startIndex..message.lastIndex) to style
            }
        }

        private fun formatTime(time: Instant): String {
            val date = time.toLocalDateTime(TimeZone.currentSystemDefault())
            return "${fmtInt(date.hour)}:${fmtInt(date.minute)}:${fmtInt(date.second)}.${fmtInt((date.nanosecond / 1e6).toInt(), 3)}"
        }

        private fun fmtFrameCnt(frameCnt: Int, len: Int): String {
            var fmt = "$frameCnt"
            if (fmt.length >= len) {
                fmt = "…${fmt.substring(fmt.length - (len-1))}"
            } else {
                while (fmt.length < len) {
                    fmt = " $fmt"
                }
            }
            return fmt
        }

        private fun fmtInt(i: Int, len: Int = 2): String {
            var fmt = "$i"
            while (fmt.length < len) {
                fmt = "0$fmt"
            }
            return fmt
        }

        override fun toString(): String {
            return "$fmtTime f:${fmtFrameCnt(frameIdx, 4)} ${level.indicator}: ${fullMessage}${if (tag != null) " [${tag}]" else ""}"
        }
    }

    companion object {
        private const val maxMessages = 10000
        private const val maxMessageLen = 500

        private val defaultTextAttrs = TextAttributes(MsdfFont.DEFAULT_FONT, Color.MAGENTA)
        private var timeFont: TextAttributes = defaultTextAttrs
        private var frameFont: TextAttributes = defaultTextAttrs
        private var longTagFont: TextAttributes = defaultTextAttrs
        private val levelFonts = mutableMapOf<Log.Level, TextAttributes>()
        private val messageFonts = mutableMapOf<Log.Level, TextAttributes>()

        private val ansiRegex = Regex("\u001b\\[(\\d+)m")

        private fun updateFonts(baseFont: MsdfFont, baseSize: Float) {
            val font = baseFont.copy(baseSize)

            timeFont = TextAttributes(font, MdColor.BROWN tone 400)
            frameFont = TextAttributes(font, MdColor.CYAN tone 700)
            longTagFont = TextAttributes(font, MdColor.GREY tone 600)

            levelFonts[Log.Level.TRACE] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), MdColor.GREY tone 600, MdColor.GREY tone 850)
            levelFonts[Log.Level.DEBUG] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), MdColor.GREY tone 400, MdColor.GREY tone 800)
            levelFonts[Log.Level.INFO] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), Color.WHITE, MdColor.LIGHT_GREEN)
            levelFonts[Log.Level.WARN] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), Color.WHITE, MdColor.AMBER)
            levelFonts[Log.Level.ERROR] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), Color.WHITE, MdColor.RED)

            messageFonts[Log.Level.TRACE] = TextAttributes(font, MdColor.GREY tone 600)
            messageFonts[Log.Level.DEBUG] = TextAttributes(font, MdColor.GREY tone 400)
            messageFonts[Log.Level.INFO] = TextAttributes(font, MdColor.LIGHT_GREEN tone 300)
            messageFonts[Log.Level.WARN] = TextAttributes(font, MdColor.AMBER tone 200)
            messageFonts[Log.Level.ERROR] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), MdColor.RED)
        }
    }
}
