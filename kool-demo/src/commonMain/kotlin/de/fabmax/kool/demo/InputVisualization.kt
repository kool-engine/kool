package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.KeyEvent
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.ClearColorLoad
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.UniqueId
import kotlin.math.abs
import kotlin.math.max

class InputVisualization(val ctx: KoolContext) {

    private class KeyInstance(val id: Long, val label: String) {
        var timeLastActive: Double = Time.gameTime
        val isHeld = mutableStateOf(true)

        fun updateActivity() {
            timeLastActive = Time.gameTime
            isHeld.set(true)
        }

        fun release() {
            isHeld.set(false)
            timeLastActive = Time.gameTime
        }
    }

    private val keyHistory = mutableStateListOf<KeyInstance>()
    private val maxHistorySize = 7
    private val keyLifeTime = 2.0
    private val fadeDuration = 0.5

    private val isLmbDown = mutableStateOf(false)
    private val isMmbDown = mutableStateOf(false)
    private val isRmbDown = mutableStateOf(false)

    private val scrollValue = mutableStateOf(0f)
    private var scrollDecayTimer = 0f

    private val inputHandler = object : InputStack.InputHandler("InputVisHandler") {
        override fun handleKeyEvents(keyEvents: List<KeyEvent>, ctx: KoolContext) {
            keyEvents.forEach { ev ->
                val keyName = getKeyDisplayName(ev)

                if (ev.isPressed) {
                    if (ev.isRepeated) {
                        val existing = keyHistory.findLast { it.label == keyName }
                        if (existing != null) {
                            existing.updateActivity()
                        } else {
                            addKeyToHistory(keyName)
                        }
                    } else {
                        addKeyToHistory(keyName)
                    }
                } else if (ev.isReleased) {
                    val existing = keyHistory.findLast { it.label == keyName }
                    existing?.release()
                }
            }
        }
    }

    val ui = Scene("InputVisualization")
    private var uiSurface: UiSurface

    init {
        ui.setupUiScene(ClearColorLoad)

        ui.onUpdate += {
            val ptr = PointerInput.primaryPointer
            val time = Time.gameTime

            isLmbDown.set(ptr.isLeftButtonDown)
            isMmbDown.set(ptr.isMiddleButtonDown)
            isRmbDown.set(ptr.isRightButtonDown)

            if (ptr.scroll.y != 0f) {
                scrollValue.set(ptr.scroll.y)
                scrollDecayTimer = 0.5f
            } else if (scrollDecayTimer > 0f) {
                scrollDecayTimer -= Time.deltaT
                if (scrollDecayTimer <= 0f) {
                    scrollValue.set(0f)
                }
            }

            if (keyHistory.isNotEmpty()) {
                val oldest = keyHistory.first()
                if (!oldest.isHeld.value && (time - oldest.timeLastActive > keyLifeTime + fadeDuration)) {
                    keyHistory.removeAt(0)
                }
                uiSurface.triggerUpdate()
            }

            val show = Settings.showInputVisualization.value

            if (show) {
                if (inputHandler !in InputStack.handlerStack) {
                    InputStack.pushTop(inputHandler)
                }
            } else {
                if (inputHandler in InputStack.handlerStack) {
                    InputStack.remove(inputHandler)
                    keyHistory.clear()
                }
            }
        }

        ui.onRelease {
            InputStack.remove(inputHandler)
        }

        uiSurface = ui.addPanelSurface(
            name = "InputVizSurface",
            backgroundColor = { null }
        ) {
            surface.inputMode = UiSurface.InputCaptureMode.CapturePassthrough

            if (!Settings.showInputVisualization.use()) return@addPanelSurface

            modifier
                .layout(CellLayout)
                .width(Grow.Std)
                .height(Grow.Std)
                .zLayer(UiSurface.LAYER_FLOATING)

            Box {
                modifier
                    .align(AlignmentX.End, AlignmentY.Bottom)
                    .margin(bottom = 20.dp, end = 20.dp)
                    .layout(ColumnLayout)
                    .background(null)

                Row {
                    modifier
                        .alignX(AlignmentX.End)
                        .margin(bottom = sizes.smallGap)

                    if (isLmbDown.use()) MouseBox("LMB")
                    if (isMmbDown.use()) MouseBox("MMB")
                    if (isRmbDown.use()) MouseBox("RMB")

                    val scroll = scrollValue.use()
                    if (abs(scroll) > 0.01f) {
                        val iconRot = if (scroll > 0) ArrowScope.ROTATION_UP else ArrowScope.ROTATION_DOWN
                        SquareBox {
                            Arrow(rotation = iconRot) {
                                modifier
                                    .align(AlignmentX.Center, AlignmentY.Center)
                                    .size(sizes.largeGap * 2f, sizes.largeGap * 2f)
                                    .colors(arrowColor = colors.primary)
                            }
                        }
                    }
                }

                Row {
                    modifier
                        .alignX(AlignmentX.End)
                        .height(64.dp)

                    keyHistory.use().forEach { keyInst ->
                        KeyBoxAnimated(keyInst)
                    }
                }
            }
        }
    }

    private fun addKeyToHistory(label: String) {
        val last = keyHistory.lastOrNull()
        if (last != null && last.label == label && last.isHeld.value) {
            last.updateActivity()
            return
        }

        if (keyHistory.size >= maxHistorySize) {
            keyHistory.removeAt(0)
        }
        keyHistory.add(KeyInstance(UniqueId.nextId(), label))
    }

    private fun UiScope.SquareBox(
        bgColor: Color = Color("00000080"),
        borderColor: Color = colors.primary,
        block: UiScope.() -> Unit
    ) {
        val boxSize = 64.dp
        Box {
            modifier
                .size(boxSize, boxSize)
                .margin(start = sizes.smallGap)
                .background(RoundRectBackground(bgColor, sizes.gap))
                .border(RoundRectBorder(borderColor, sizes.gap, sizes.borderWidth * 2f))

            block()
        }
    }

    private fun UiScope.MouseBox(text: String) {
        SquareBox(borderColor = colors.primary) {
            Text(text) {
                modifier
                    .align(AlignmentX.Center, AlignmentY.Center)
                    .font(sizes.largeText)
                    .textColor(colors.primary)
            }
        }
    }

    private fun UiScope.KeyBoxAnimated(keyInst: KeyInstance) {
        val isHeld = keyInst.isHeld.use()
        val timeAlive = Time.gameTime - keyInst.timeLastActive

        var alpha = 1f
        if (!isHeld && timeAlive > keyLifeTime) {
            alpha = max(0f, 1f - ((timeAlive - keyLifeTime) / fadeDuration).toFloat())
        }

        val bgColor = Color("000000").withAlpha(0.5f * alpha)
        val baseColor = if (isHeld) MdColor.YELLOW else colors.primaryVariant
        val textColorBase = if (isHeld) MdColor.YELLOW else colors.primary
        val borderColor = baseColor.withAlpha(alpha)
        val textColor = textColorBase.withAlpha(alpha)

        val isLongText = keyInst.label.length > 2
        val boxHeight = 64.dp

        Box {
            modifier
                .height(boxHeight)
                .margin(start = sizes.smallGap)
                .background(RoundRectBackground(bgColor, sizes.gap))
                .border(RoundRectBorder(borderColor, sizes.gap, sizes.borderWidth * 2f))

            if (isLongText) {
                modifier.width(FitContent).padding(horizontal = sizes.largeGap)
            } else {
                modifier.width(boxHeight)
            }

            Text(keyInst.label) {
                modifier
                    .align(AlignmentX.Center, AlignmentY.Center)
                    .font(sizes.largeText)
                    .textColor(textColor)
            }
        }
    }

    private fun getKeyDisplayName(ev: KeyEvent): String {
        val char = ev.typedChar
        if (char != 0.toChar() && !char.isISOControl() && ev.keyCode.code != 32) {
            return char.uppercase()
        }

        val name = ev.keyCode.name
        return when {
            name.startsWith("KEY_") -> name.removePrefix("KEY_")
            name == " " -> "SPACE"
            else -> name
        }
    }
}