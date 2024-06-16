package de.fabmax.kool.modules.ui2

import de.fabmax.kool.Clipboard
import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.*
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.EditableText
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.Time
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

interface TextFieldScope : UiScope, Focusable {
    override val modifier: TextFieldModifier
}

open class TextFieldModifier(surface: UiSurface) : UiModifier(surface) {
    var text: String by property("")
    var font: Font by property { it.sizes.normalText }
    var hint: String by property("")
    var hintFont: Font? by property(null)
    var isEditable: Boolean by property(true)
    var maxLength: Int by property(100)
    var textAlignX: AlignmentX by property(AlignmentX.Start)

    var textColor: Color by property { it.colors.onBackground }
    var hintColor: Color by property { it.colors.onBackgroundAlpha(0.5f) }
    var selectionColor: Color by property { it.colors.primaryAlpha(0.5f) }
    var cursorColor: Color by property { it.colors.onBackground }
    var lineColor: Color? by property { it.colors.primaryVariant }
    var lineFocusedColor: Color? by property { it.colors.primary }
    var selectionStart: Int by property { -1 }
    var selectionEnd: Int by property { -1 }

    var onChange: ((String) -> Unit)? by property(null)
    var onEnterPressed: ((String) -> Unit)? by property(null)
}

fun <T: TextFieldModifier> T.text(text: String): T { this.text = text; return this }
fun <T: TextFieldModifier> T.font(font: Font): T { this.font = font; return this }
fun <T: TextFieldModifier> T.hint(hint: String): T { this.hint = hint; return this }
fun <T: TextFieldModifier> T.hintFont(font: Font): T { this.hintFont = font; return this }
fun <T: TextFieldModifier> T.isEditable(flag: Boolean): T { isEditable = flag; return this }
fun <T: TextFieldModifier> T.maxLength(len: Int): T { maxLength = len; return this }
fun <T: TextFieldModifier> T.textAlignX(alignment: AlignmentX): T { textAlignX = alignment; return this }
fun <T: TextFieldModifier> T.onChange(block: ((String) -> Unit)?): T { this.onChange = block; return this }
fun <T: TextFieldModifier> T.onEnterPressed(block: ((String) -> Unit)?): T { this.onEnterPressed = block; return this }

fun <T: TextFieldModifier> T.selectionRange(start: Int, end: Int): T {
    this.selectionStart = start
    this.selectionEnd = end
    return this
}

fun <T: TextFieldModifier> T.colors(
    textColor: Color = this.textColor,
    hintColor: Color = this.hintColor,
    selectionColor: Color = this.selectionColor,
    cursorColor: Color = this.cursorColor,
    lineColor: Color? = this.lineColor,
    lineColorFocused: Color? = this.lineFocusedColor
): T {
    this.textColor = textColor
    this.hintColor = hintColor
    this.selectionColor = selectionColor
    this.cursorColor = cursorColor
    this.lineColor = lineColor
    this.lineFocusedColor = lineColorFocused
    return this
}

inline fun UiScope.TextField(
    text: String = "",
    scopeName: String? = null,
    block: TextFieldScope.() -> Unit
): TextFieldScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val textField = uiNode.createChild(scopeName, TextFieldNode::class, TextFieldNode.factory)
    if (textField.isFocused.use()) {
        surface.onEachFrame(textField::updateCaretBlinkState)
    }
    textField.modifier
        .text(text)
        .onClick(textField)
        .hoverListener(textField)
        .dragListener(textField)
    textField.block()
    return textField
}

open class TextFieldNode(parent: UiNode?, surface: UiSurface)
    : UiNode(parent, surface), TextFieldScope, Clickable, Hoverable, Draggable
{
    override val modifier = TextFieldModifier(surface)
    override val isFocused = mutableStateOf(false)

    private val textProps = TextProps(Font.DEFAULT_FONT)
    private val textCache = CachedTextGeometry(this)

    private val textOrigin = MutableVec2f()
    private var overflowOffset = 0f
    private var caretBlink = 0f
    private val isCaretBlink = mutableStateOf(false)
    private val caretWidth = Dp(1f)

    private val editText = EditableText()

    override fun measureContentSize(ctx: KoolContext) {
        val isHint = modifier.text.isEmpty()
        val font = if (isHint) modifier.hintFont ?: modifier.font else modifier.font
        val dispText = if (isHint) modifier.hint else modifier.text

        surface.applyFontScale(modifier.font, ctx)
        val textMetrics = textCache.getTextMetrics(dispText, font)
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) modWidth.px else textMetrics.width + caretWidth.px + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else textMetrics.height + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        editText.text = modifier.text
        editText.maxLength = modifier.maxLength
        if (modifier.selectionStart >= 0 && modifier.selectionEnd >= 0) {
            editText.selectionStart = modifier.selectionStart
            editText.caretPosition = modifier.selectionEnd
        }

        val isFocused = isFocused.use()
        val isHint = modifier.text.isEmpty()
        val txtFont = if (isHint) modifier.hintFont ?: modifier.font else modifier.font
        val dispText = if (isHint) modifier.hint else modifier.text
        val textColor = if (isHint) modifier.hintColor else modifier.textColor

        val textMetrics = textCache.textMetrics

        textOrigin.y = (heightPx - textMetrics.height) / 2f + textMetrics.yBaseline
        textOrigin.x = when (modifier.textAlignX) {
            AlignmentX.Start -> paddingStartPx
            AlignmentX.Center -> (widthPx - textMetrics.width) / 2f
            AlignmentX.End -> widthPx - textMetrics.width - caretWidth.px - paddingEndPx
        }
        textOrigin.x += checkTextOverflow(txtFont)
        val lineY = textOrigin.y - textMetrics.descentPx - 1.dp.px

        // draw text
        textProps.apply {
            font = txtFont
            text = dispText
            isYAxisUp = false
            origin.set(textOrigin.x, textOrigin.y, 0f)
        }
        textCache.addTextGeometry(getTextBuilder(txtFont).geometry, textProps, textColor)

        val draw = getUiPrimitives()
        val lineColor = if (isFocused) modifier.lineFocusedColor else modifier.lineColor
        lineColor?.let {
            draw.localRect(paddingStartPx, lineY, innerWidthPx, 1.dp.px, it)
        }

        if (isFocused) {
            draw.renderCaretAndSelection()
        }
    }

    private fun UiPrimitiveMesh.renderCaretAndSelection() {
        val h = (modifier.font.sizePts + 4f).dp.px
        val caretX = textX(modifier.font, editText.caretPosition)

        // blinking caret
        if (isCaretBlink.use() && surface.isFocused.use()) {
            localRect(caretX, textOrigin.y + 4.dp.px - h, caretWidth.px, h, modifier.cursorColor)
        }

        // selection
        if (editText.selectionStart != editText.caretPosition) {
            val selStartX = textX(modifier.font, editText.selectionStart)
            val left = min(selStartX, caretX)
            val right = max(selStartX, caretX)
            localRect(left, textOrigin.y + 4.dp.px - h, right - left, h, modifier.selectionColor)
        }
    }

    private fun checkTextOverflow(font: Font): Float {
        if (textCache.textMetrics.width < innerWidthPx) {
            overflowOffset = 0f
        } else {
            val cursorX = textX(font, editText.caretPosition) + overflowOffset
            if (cursorX < 0f) {
                overflowOffset -= cursorX
            } else if (cursorX > widthPx) {
                overflowOffset += widthPx - cursorX - 2.dp.px
            }
        }
        return overflowOffset
    }

    private fun textX(font: Font, charIndex: Int): Float {
        var x = textOrigin.x
        for (i in 0 until min(charIndex, editText.text.length)) {
            x += font.charWidth(editText.text[i])
        }
        return x
    }

    private fun textIndex(font: Font, localX: Float): Int {
        var x = textOrigin.x
        for (i in 0 until editText.text.length) {
            val w = font.charWidth(editText.text[i])
            if (x + w >= localX) {
                return if (abs(x - localX) < abs(x + w - localX)) {
                    i
                } else {
                    i+1
                }
            }
            x += w
        }
        return editText.text.length
    }

    private fun resetCaretBlinkState() {
        caretBlink = 0.5f
        isCaretBlink.set(true)
    }

    @Suppress("UNUSED_PARAMETER")
    fun updateCaretBlinkState(ctx: KoolContext) {
        if (isFocused.value) {
            caretBlink -= Time.deltaT
            if (caretBlink < 0f) {
                isCaretBlink.set(!isCaretBlink.value)
                caretBlink += 0.5f
                if (caretBlink < 0f) {
                    caretBlink = 0.5f
                }
            }
        } else {
            caretBlink = 0f
            isCaretBlink.set(false)
        }
    }

    override fun onClick(ev: PointerEvent) {
        surface.requestFocus(this)

        editText.caretPosition = textIndex(modifier.font, ev.position.x)
        editText.selectionStart = editText.caretPosition

        if (ev.pointer.isLeftButtonClicked && ev.pointer.leftButtonRepeatedClickCount > 1) {
            // double click -> select clicked word
            editText.moveCaret(EditableText.MOVE_WORD_LEFT, false)
            // MOVE_WORD_LEFT moves the cursor past the separating space, skip that before we select the word
            while (editText.text.length > editText.caretPosition && editText.text[editText.caretPosition].isWhitespace()) {
                editText.caretPosition++
                editText.selectionStart++
            }
            editText.moveCaret(EditableText.MOVE_WORD_RIGHT, true)
            while (editText.copySelection()?.last()?.isWhitespace() == true) {
                editText.caretPosition--
            }
        }
        surface.triggerUpdate()
    }

    override fun onHover(ev: PointerEvent) {
        PointerInput.cursorShape = CursorShape.TEXT
    }

    override fun onDragStart(ev: PointerEvent) = onClick(ev)

    override fun onDrag(ev: PointerEvent) {
        PointerInput.cursorShape = CursorShape.TEXT
        val selPos = textIndex(modifier.font, ev.position.x)
        if (selPos != editText.caretPosition) {
            editText.caretPosition = selPos
            surface.triggerUpdate()
        }
    }

    override fun onKeyEvent(keyEvent: KeyEvent) {
        if (!modifier.isEditable) {
            return
        }

        var isTextUpdate = false
        var triggerUpdate = true
        if (keyEvent.isCharTyped) {
            editText.charTyped(keyEvent.typedChar)
            isTextUpdate = true

        } else if (keyEvent.isPressed) {
            when (keyEvent.keyCode) {
                KeyboardInput.KEY_BACKSPACE -> {
                    editText.backspace()
                    isTextUpdate = true
                }
                KeyboardInput.KEY_DEL -> {
                    editText.deleteSelection()
                    isTextUpdate = true
                }
                KeyboardInput.KEY_CURSOR_LEFT -> {
                    if (keyEvent.isCtrlDown) {
                        editText.moveCaret(EditableText.MOVE_WORD_LEFT, keyEvent.isShiftDown)
                    } else {
                        editText.moveCaret(EditableText.MOVE_LEFT, keyEvent.isShiftDown)
                    }
                }
                KeyboardInput.KEY_CURSOR_RIGHT -> {
                    if (keyEvent.isCtrlDown) {
                        editText.moveCaret(EditableText.MOVE_WORD_RIGHT, keyEvent.isShiftDown)
                    } else {
                        editText.moveCaret(EditableText.MOVE_RIGHT, keyEvent.isShiftDown)
                    }
                }
                KeyboardInput.KEY_HOME -> editText.moveCaret(EditableText.MOVE_START, keyEvent.isShiftDown)
                KeyboardInput.KEY_END -> editText.moveCaret(EditableText.MOVE_END, keyEvent.isShiftDown)
                KeyboardInput.KEY_ESC -> surface.requestFocus(null)
                KeyboardInput.KEY_TAB -> surface.cycleFocus(backwards = keyEvent.isShiftDown)
                KeyboardInput.KEY_ENTER -> modifier.onEnterPressed?.invoke(editText.text)
                KeyboardInput.KEY_NP_ENTER -> modifier.onEnterPressed?.invoke(editText.text)
                else -> {
                    triggerUpdate = false
                    if (keyEvent.isCtrlDown) {
                        when (keyEvent.localKeyCode) {
                            KEY_CODE_COPY -> editText.copySelection()?.let { Clipboard.copyToClipboard(it) }
                            KEY_CODE_CUT -> {
                                editText.cutSelection()?.let { Clipboard.copyToClipboard(it) }
                                isTextUpdate = true
                            }
                            KEY_CODE_PASTE -> {
                                Clipboard.getStringFromClipboard {
                                    if (it != null) editText.replaceSelection(it)
                                    modifier.onChange?.invoke(editText.text)
                                }
                                isTextUpdate = true
                            }
                            KEY_CODE_SELECT_ALL -> {
                                editText.selectionStart = 0
                                editText.caretPosition = editText.text.length
                                triggerUpdate = true
                            }
                            else -> { }
                        }
                    }
                }
            }
        }
        if (isTextUpdate) {
            modifier.onChange?.invoke(editText.text)
            resetCaretBlinkState()
        }
        if (triggerUpdate) {
            surface.triggerUpdate()
            resetCaretBlinkState()
        }
    }

    companion object {
        private val KEY_CODE_SELECT_ALL = LocalKeyCode('a')
        private val KEY_CODE_CUT = LocalKeyCode('x')
        private val KEY_CODE_COPY = LocalKeyCode('c')
        private val KEY_CODE_PASTE = LocalKeyCode('v')

        val factory: (UiNode, UiSurface) -> TextFieldNode = { parent, surface -> TextFieldNode(parent, surface) }
    }
}