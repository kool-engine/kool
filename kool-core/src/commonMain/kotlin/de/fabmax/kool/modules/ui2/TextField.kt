package de.fabmax.kool.modules.ui2

import de.fabmax.kool.Clipboard
import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.LocalKeyCode
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.EditableText
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.Time
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
    var hintColor: Color by property { it.colors.onBackground.withAlpha(0.5f) }
    var selectionColor: Color by property { it.colors.primary.withAlpha(0.5f) }
    var cursorColor: Color by property { it.colors.onBackground }
    var lineColor: Color by property { it.colors.primaryVariant }
    var lineFocusedColor: Color by property { it.colors.primary }

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

fun <T: TextFieldModifier> T.colors(
    textColor: Color = this.textColor,
    hintColor: Color = this.hintColor,
    selectionColor: Color = this.selectionColor,
    cursorColor: Color = this.cursorColor,
    lineColor: Color = this.lineColor,
    lineColorFocused: Color = this.lineFocusedColor
): T {
    this.textColor = textColor
    this.hintColor = hintColor
    this.selectionColor = selectionColor
    this.cursorColor = cursorColor
    this.lineColor = lineColor
    this.lineFocusedColor = lineColorFocused
    return this
}

inline fun UiScope.TextField(text: String = "", block: TextFieldScope.() -> Unit): TextFieldScope {
    val textField = uiNode.createChild(TextFieldNode::class, TextFieldNode.factory)
    surface.onEachFrame(textField::updateCursorState)
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
    override val isFocused: Boolean get() = isFocusedState.value

    private val textProps = TextProps(Font.DEFAULT_FONT)
    private val textCache = CachedText(this)

    private val textOrigin = MutableVec2f()
    private var overflowOffset = 0f
    private var prevClickTime = 0.0
    private var isFocusedState = mutableStateOf(false)
    private var cursorBlink = 0f
    private var cursorShow = mutableStateOf(false)

    private val editText = EditableText()

    override fun measureContentSize(ctx: KoolContext) {
        val isHint = modifier.text.isEmpty()
        val font = if (isHint) modifier.hintFont ?: modifier.font else modifier.font
        val dispText = if (isHint) modifier.hint else modifier.text

        surface.applyFontScale(modifier.font, ctx)
        val textMetrics = textCache.getTextMetrics(dispText, font)
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) modWidth.px else textMetrics.width + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else textMetrics.height + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth + 8.dp.px, measuredHeight + 8.dp.px)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        editText.text = modifier.text
        editText.maxLength = modifier.maxLength

        val isFocused = isFocusedState.use()
        val isHint = modifier.text.isEmpty()
        val txtFont = if (isHint) modifier.hintFont ?: modifier.font else modifier.font
        val dispText = if (isHint) modifier.hint else modifier.text
        val textColor = if (isHint) modifier.hintColor else modifier.textColor

        val textMetrics = textCache.textMetrics

        textOrigin.y = (heightPx - textMetrics.height) / 2f + textMetrics.yBaseline
        textOrigin.x = when (modifier.textAlignX) {
            AlignmentX.Start -> paddingStartPx + 4.dp.px
            AlignmentX.Center -> (widthPx - textMetrics.width) / 2f
            AlignmentX.End -> widthPx - textMetrics.width - paddingEndPx - 4.dp.px
        }
        textOrigin.x += checkTextOverflow(txtFont)
        val lineY = textOrigin.y + 4.dp.px

        // draw text
        textProps.apply {
            font = txtFont
            text = dispText
            isYAxisUp = false
            origin.set(textOrigin.x, textOrigin.y, 0f)
        }
        textCache.addTextGeometry(getTextBuilder(txtFont, ctx).geometry, textProps, textColor)

        val draw = getUiPrimitives()
        val lineColor = if (isFocused) modifier.lineFocusedColor else modifier.lineColor
        draw.localRect(paddingStartPx, lineY, innerWidthPx, 1.dp.px, lineColor)

        if (isFocused) {
            draw.renderCursor(txtFont)
        }
    }

    private fun UiPrimitiveMesh.renderCursor(font: Font) {
        val h = (modifier.font.sizePts + 4f).dp.px
        val cursorX = textX(font, editText.caretPosition)

        // cursor (blinking)
        if (cursorShow.use()) {
            localRect(cursorX, textOrigin.y + 4.dp.px - h, 1f.dp.px, h, modifier.cursorColor)
        }

        // selection
        if (editText.selectionStart != editText.caretPosition) {
            val selStartX = textX(font, editText.selectionStart)
            val left = min(selStartX, cursorX)
            val right = max(selStartX, cursorX)
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

    private fun resetCursorBlink() {
        cursorBlink = 0.5f
        cursorShow = mutableStateOf(true)
    }

    fun updateCursorState() {
        if (isFocusedState.value) {
            cursorBlink -= Time.deltaT
            if (cursorBlink < 0f) {
                cursorShow.set(!cursorShow.value)
                cursorBlink += 0.5f
                if (cursorBlink < 0f) {
                    cursorBlink = 0.5f
                }
            }
        } else {
            cursorBlink = 0f
            cursorShow = mutableStateOf(false)
        }
    }

    override fun onClick(ev: PointerEvent) {
        surface.requestFocus(this)

        editText.caretPosition = textIndex(modifier.font, ev.position.x)
        editText.selectionStart = editText.caretPosition

        val t = Time.gameTime
        if (ev.pointer.isLeftButtonClicked && t - prevClickTime < 0.3) {
            // double click -> select clicked word
            editText.moveCaret(EditableText.MOVE_WORD_LEFT, false)
            // MOVE_WORD_LEFT moves the cursor past the separating space, skip that before we select the word
            while (editText.text.length > editText.caretPosition && editText.text[editText.caretPosition] == ' ') {
                editText.caretPosition++
                editText.selectionStart++
            }
            editText.moveCaret(EditableText.MOVE_WORD_RIGHT, true)
            while (editText.copySelection()?.endsWith(" ") == true) {
                editText.caretPosition--
            }
        }
        prevClickTime = t
        surface.triggerUpdate()
    }

    override fun onFocusGain() {
        isFocusedState.set(true)
    }

    override fun onFocusLost() {
        isFocusedState.set(false)
    }

    override fun onEnter(ev: PointerEvent) {
        ev.ctx.inputMgr.cursorShape = InputManager.CursorShape.TEXT
    }

    override fun onExit(ev: PointerEvent) {
        ev.ctx.inputMgr.cursorShape = InputManager.CursorShape.DEFAULT
    }

    override fun onDragStart(ev: PointerEvent) = onClick(ev)

    override fun onDrag(ev: PointerEvent) {
        val selPos = textIndex(modifier.font, ev.position.x)
        if (selPos != editText.caretPosition) {
            editText.caretPosition = selPos
            surface.triggerUpdate()
        }
    }

    override fun onKeyEvent(keyEvent: InputManager.KeyEvent) {
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
                InputManager.KEY_BACKSPACE -> {
                    editText.backspace()
                    isTextUpdate = true
                }
                InputManager.KEY_DEL -> {
                    editText.deleteSelection()
                    isTextUpdate = true
                }
                InputManager.KEY_CURSOR_LEFT -> {
                    if (keyEvent.isCtrlDown) {
                        editText.moveCaret(EditableText.MOVE_WORD_LEFT, keyEvent.isShiftDown)
                    } else {
                        editText.moveCaret(EditableText.MOVE_LEFT, keyEvent.isShiftDown)
                    }
                }
                InputManager.KEY_CURSOR_RIGHT -> {
                    if (keyEvent.isCtrlDown) {
                        editText.moveCaret(EditableText.MOVE_WORD_RIGHT, keyEvent.isShiftDown)
                    } else {
                        editText.moveCaret(EditableText.MOVE_RIGHT, keyEvent.isShiftDown)
                    }
                }
                InputManager.KEY_HOME -> editText.moveCaret(EditableText.MOVE_START, keyEvent.isShiftDown)
                InputManager.KEY_END -> editText.moveCaret(EditableText.MOVE_END, keyEvent.isShiftDown)
                InputManager.KEY_ESC -> surface.requestFocus(null)
                InputManager.KEY_ENTER -> modifier.onEnterPressed?.invoke(editText.text)
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
            resetCursorBlink()
        }
        if (triggerUpdate) {
            surface.triggerUpdate()
            resetCursorBlink()
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