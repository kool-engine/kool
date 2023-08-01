package de.fabmax.kool.editor.ui

import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.*
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ConsolePanel(ui: EditorUi) : EditorPanel("Console", IconMap.medium.CONSOLE, ui) {

    private val logMessages = RingBuffer<LogMessage>(maxMessages)
    private val filteredLogMessages = RingBuffer<LogMessage>(maxMessages)
    private val lineProvider = LogLineProvider()
    private val logLock = SynchronizedObject()

    private val isScrollLock = mutableStateOf(true)

    private var font = ui.consoleFont.value
    private var fontSize = 0f

    private var minLevel = mutableStateOf(Log.Level.DEBUG)
    private var messageFilter: Regex? = null

    override val windowSurface = editorPanelWithPanelBar(backgroundColor = { colors.backgroundVariant }) {
        checkConsoleFont()

        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, icon) {
                Row {
                    // register drag callbacks, to block window drag
                    modifier
                        .onDragStart {  }
                        .alignY(AlignmentY.Center)

                    divider(colors.dividerColor, marginStart = sizes.largeGap, marginEnd = sizes.largeGap, verticalMargin = sizes.smallGap)

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

                    divider(colors.dividerColor, marginStart = sizes.largeGap, marginEnd = sizes.largeGap, verticalMargin = sizes.smallGap)

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
                                messageFilter = if (it.isBlank()) null else Regex(it)
                                updateFilter()
                            }

                        defaultTextfieldStyle()
                    }
                }

                Box(width = Grow.Std) { }

                iconButton(IconMap.small.SCROLL_LOCK, "Scroll to end", isScrollLock.use()) {
                    isScrollLock.set(!isScrollLock.value)
                }
            }

            synchronized(logLock) {
                console()
            }
        }
    }

    init {
        Log.printer = { lvl: Log.Level, tag: String?, message: String ->
            synchronized(logLock) {
                val msg = LogMessage(lvl, tag, message, Clock.System.now(), Time.frameCount)
                logMessages += msg
                if (msg.isAccepted) {
                    filteredLogMessages += msg
                    windowSurface.triggerUpdate()
                }
            }
        }
        // set minimum log level to TRACE, actual message filtering is done dynamically within this class
        Log.level = Log.Level.TRACE
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
            hScrollbarModifier = {
                it
                    //.colors(colors.secondaryVariant.withAlpha(0.5f), colors.secondary)
                    .margin(start = sizes.gap * 0.75f, end = sizes.gap * 2f, bottom = sizes.gap * 0.75f)
            },
            vScrollbarModifier = {
                it
                    //.colors(colors.secondaryVariant.withAlpha(0.5f), colors.secondary)
                    .margin(sizes.gap * 0.75f)
            }
        ) {
            modifier
                .padding(horizontal = sizes.gap)
                .lastLineBottomPadding(sizes.largeGap)
                .backgroundColor(null)
                .onWheelY { ev ->
                    if (ev.pointer.deltaScrollY > 0.0) {
                        isScrollLock.set(false)
                    } else if (ev.pointer.deltaScrollY < 0.0 && listState.itemsTo == listState.numTotalItems - 1) {
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
            spans += "  f:${fmtStr("$frameIdx", 6)}" to frameFont
            spans += "  ${level.indicator}:  " to (levelFonts[level] ?: defaultTextAttrs)
            spans += message to (messageFonts[level] ?: defaultTextAttrs)
            tag?.let {
                spans += " [${tag}]" to longTagFont
            }
            return TextLine(spans)
        }


        private fun formatTime(time: Instant): String {
            val date = time.toLocalDateTime(TimeZone.currentSystemDefault())
            return "${fmtInt(date.hour)}:${fmtInt(date.minute)}:${fmtInt(date.second)}.${fmtInt((date.nanosecond / 1e6).toInt(), 3)}"
        }

        private fun fmtStr(str: String, len: Int): String {
            var fmt = str
            while (fmt.length < len) {
                fmt += " "
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
            return "$fmtTime $level ${message}${if (tag != null) " [${tag}]" else ""}"
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

        private fun updateFonts(baseFont: MsdfFont, baseSize: Float) {
            val font = baseFont.copy(baseSize)

            timeFont = TextAttributes(font, MdColor.BROWN tone 400)
            frameFont = TextAttributes(font, MdColor.CYAN)
            longTagFont = TextAttributes(font, MdColor.GREY tone 600)

            levelFonts[Log.Level.TRACE] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), MdColor.GREY tone 600)
            levelFonts[Log.Level.DEBUG] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), MdColor.GREY tone 400)
            levelFonts[Log.Level.INFO] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), MdColor.LIGHT_GREEN)
            levelFonts[Log.Level.WARN] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), MdColor.AMBER)
            levelFonts[Log.Level.ERROR] = TextAttributes(font.copy(weight = MsdfFont.WEIGHT_BOLD), MdColor.RED)

            messageFonts[Log.Level.TRACE] = TextAttributes(font, MdColor.GREY tone 600)
            messageFonts[Log.Level.DEBUG] = TextAttributes(font, MdColor.GREY tone 400)
            messageFonts[Log.Level.INFO] = TextAttributes(font, MdColor.LIGHT_GREEN tone 300)
            messageFonts[Log.Level.WARN] = TextAttributes(font, MdColor.AMBER tone 300)
            messageFonts[Log.Level.ERROR] = levelFonts[Log.Level.ERROR]!!
        }
    }
}
