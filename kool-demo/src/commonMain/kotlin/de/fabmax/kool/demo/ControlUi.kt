package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.toString
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.uiFont

fun controlUi(ctx: KoolContext, block: ControlUiBuilder.() -> Unit) = uiScene { scene ->
    val builder = ControlUiBuilder(scene, this, ctx)
    builder.block()
    builder.finish()
}

class ControlUiBuilder(val uiScene: Scene, val uiRoot: UiRoot, ctx: KoolContext) {

    var menuWidth = 300f
    var menuHeight = 0f

    val menuContainer: UiContainer
    var menuY = -40f

    private val smallFontProps = FontProps(Font.SYSTEM_FONT, 14f)
    val smallFont: Font

    init {
        uiRoot.apply {
            smallFont = uiFont(smallFontProps.family, smallFontProps.sizePts, uiDpi, ctx, smallFontProps.style, smallFontProps.chars)

            theme = theme(UiTheme.DARK) {
                componentUi { BlankComponentUi() }
                containerUi { BlankComponentUi() }
            }

            menuContainer = container("menu") {
                ui.setCustom(SimpleComponentUi(this))
            }
            +menuContainer
        }
    }

    fun button(text: String, onClick: Button.() -> Unit): Button {
        val button: Button
        uiRoot.apply {
            button = button(text) {
                layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                this.onClick += { _,_,_ ->
                    onClick()
                }
            }
            menuContainer += button
        }
        menuY -= 35f
        return button
    }

    fun <T> cycler(label: String?, cycler: Cycler<T>, onChange: (T, T) -> Unit) {
        uiRoot.apply {
            menuContainer.apply {
                label?.let {
                    +label(it) {
                        layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                        layoutSpec.setSize(pcs(100f), dps(35f), full())
                    }
                    menuY -= 35f
                }
                val mainButton = button("cycler-main") {
                    layoutSpec.setOrigin(pcs(15f), dps(menuY), zero())
                    layoutSpec.setSize(pcs(70f), dps(35f), full())
                    text = cycler.current.toString()
                    onClick += { _, _, _ ->
                        val prev = cycler.current
                        text = cycler.next().toString()
                        onChange(cycler.current, prev)
                    }
                }
                +mainButton
                +button("cycle-left") {
                    layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                    layoutSpec.setSize(pcs(20f), dps(35f), full())
                    text = "<"

                    onClick += { _, _, _ ->
                        val prev = cycler.current
                        mainButton.text = cycler.prev().toString()
                        onChange(cycler.current, prev)
                    }
                }
                +button("cycle-right") {
                    layoutSpec.setOrigin(pcs(80f), dps(menuY), zero())
                    layoutSpec.setSize(pcs(20f), dps(35f), full())
                    text = ">"

                    onClick += { _, _, _ ->
                        val prev = cycler.current
                        mainButton.text = cycler.next().toString()
                        onChange(cycler.current, prev)
                    }
                }
            }
        }
        menuY -= 35f
    }

    fun gap(yGap: Float) {
        menuY -= yGap
    }

    fun image(imageTex: Texture2d? = null, imageShader: Shader? = null): UiImage {
        val image: UiImage
        uiRoot.apply {
            image = UiImage(imageTex, imageShader)
            +image
        }
        return image
    }

    fun section(title: String, block: ControlUiBuilder.() -> Unit) {
        title(title)
        block()
    }

    fun slider(initialValue: Float, min: Float, max: Float, onChange: Slider.() -> Unit): Slider {
        val slider: Slider
        uiRoot.apply {
            menuContainer.apply {
                slider = slider("radiusSlider", min, max, initialValue) {
                    layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f), full())
                    onValueChanged += { onChange() }
                }
                +slider
            }
        }
        menuY -= 35f
        return slider
    }

    fun sliderWithValue(text: String, initialValue: Float, min: Float, max: Float,
                        precision: Int = 2, textFormat: (Float) -> String = { it.toString(precision) },
                        onChange: Slider.() -> Unit): Slider {
        val slider: Slider
        uiRoot.apply {
            menuContainer.apply {
                +label(text) {
                    layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                    layoutSpec.setSize(pcs(75f), dps(35f), full())
                }
                val valueLabel = label(textFormat(initialValue)) {
                    layoutSpec.setOrigin(pcs(75f), dps(menuY), zero())
                    layoutSpec.setSize(pcs(25f), dps(35f), full())
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                }
                +valueLabel
                menuY -= 35f
                slider = slider("slider", min, max, initialValue) {
                    layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                    layoutSpec.setSize(pcs(100f), dps(35f), full())
                    onValueChanged += {
                        valueLabel.text = textFormat(value)
                        onChange()
                    }
                }
                +slider
            }
        }
        menuY -= 35f
        return slider
    }

    fun sliderWithValueSmall(text: String, initialValue: Float, min: Float, max: Float,
                             precision: Int = 2, textFormat: (Float) -> String = { it.toString(precision) },
                             widthLabel: Float = 15f, widthValue: Float = 15f,
                             onChange: Slider.() -> Unit): Slider {
        val slider: Slider
        uiRoot.apply {
            menuContainer.apply {
                +label(text) {
                    layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                    layoutSpec.setSize(pcs(widthLabel), dps(35f), full())
                }
                val valueLabel = label(textFormat(initialValue)) {
                    layoutSpec.setOrigin(pcs(100 - widthValue), dps(menuY), zero())
                    layoutSpec.setSize(pcs(widthValue), dps(35f), full())
                    textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                }
                +valueLabel
                slider = slider("slider", min, max, initialValue) {
                    layoutSpec.setOrigin(pcs(widthLabel), dps(menuY), zero())
                    layoutSpec.setSize(pcs(100 - widthLabel - widthValue), dps(35f), full())
                    onValueChanged += {
                        valueLabel.text = textFormat(value)
                        onChange()
                    }
                }
                +slider
            }
        }
        menuY -= 35f
        return slider
    }

    fun text(text: String): Label {
        val label: Label
        uiRoot.apply {
            label = label(text) {
                layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
            }
            menuContainer += label
        }
        menuY -= 35f
        return label
    }

    fun textWithValue(text: String, valueText: String): Label {
        val label: Label
        uiRoot.apply {
            menuContainer += label(text) {
                layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                layoutSpec.setSize(pcs(50f), dps(30f), full())
            }
            label = label(valueText) {
                layoutSpec.setOrigin(pcs(50f), dps(menuY), zero())
                layoutSpec.setSize(pcs(50f), dps(30f), full())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
            }
            menuContainer += label
        }
        menuY -= 35f
        return label
    }

    fun title(text: String): Label {
        val label: Label
        uiRoot.apply {
            label = label(text.uppercase()) {
                layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                font.setCustom(smallFont)
                textColor.setCustom(theme.accentColor)
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
            }
            menuContainer += label
        }
        menuY -= 35f
        return label
    }

    fun toggleButton(text: String, initialState: Boolean, onChange: ToggleButton.() -> Unit): ToggleButton {
        val toggleButton: ToggleButton
        uiRoot.apply {
            toggleButton = toggleButton(text) {
                layoutSpec.setOrigin(pcs(0f), dps(menuY), zero())
                layoutSpec.setSize(pcs(100f), dps(30f), full())
                isEnabled = initialState
                onStateChange += onChange
            }
            menuContainer += toggleButton
        }
        menuY -= 35f
        return toggleButton
    }

    fun finish() {
        menuContainer.apply {
            if (menuHeight == 0f) {
                menuHeight = -menuY - 20
            }
            layoutSpec.setOrigin(dps(-menuWidth - 120f), dps(-menuHeight - 120f), zero())
            layoutSpec.setSize(dps(menuWidth), dps(menuHeight), full())
        }
    }
}

class UiImage(imageTex: Texture2d?, imageShader: Shader?) : Group() {

    var relativeX = 0.05f
    var relativeY = 0.05f
    var relativeWidth = 0.33f
    var aspectRatio = ASPECT_RATIO_VIEWPORT

    init {
        +textureMesh("image") {
            generate {
                rect {
                    size.set(1f, 1f)
                    mirrorTexCoordsY()
                }
            }
            shader = imageShader ?: ModeledShader.TextureColor(imageTex)
        }

        onUpdate += { ev ->
            val ar = if (aspectRatio != ASPECT_RATIO_VIEWPORT) aspectRatio else ev.viewport.aspectRatio
            val scaleX = ev.viewport.width * relativeWidth
            val scaleY = scaleX / ar

            setIdentity()
            val x = ev.viewport.width * relativeX
            val y = ev.viewport.height * relativeY
            translate(x, y, 0f)
            scale(scaleX, scaleY, 1f)
        }
    }

    companion object {
        const val ASPECT_RATIO_VIEWPORT = 0f
    }
}
