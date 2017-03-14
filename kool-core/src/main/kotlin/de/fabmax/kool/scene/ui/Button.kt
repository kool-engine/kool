package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputHandler
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.Node
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.util.*
import kotlin.Unit

/**
 * @author fabmax
 */
open class Button(name: String) : UiComponent(name) {

    var text = ""
        set(value) {
            if (value != field) {
                field = value
                isUiUpdateNeeded = true
            }
        }

    var textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
        set(value) {
            if (value != field) {
                field = value
                isUiUpdateNeeded = true
            }
        }

    var padding = Margin(dps(16f), dps(16f), dps(16f), dps(16f))
        set(value) {
            if (value != field) {
                field = value
                isUiUpdateNeeded = true
            }
        }

    var onClick: List<Node.(InputHandler.Pointer, RayTest, RenderContext) -> Unit> = mutableListOf()

    val font = ThemeOrCustomProp(Font.DEFAULT_FONT)
    val textColor = ThemeOrCustomProp(Color.WHITE)
    val textColorHovered = ThemeOrCustomProp(Color.WHITE)

    var isHovered = false
        protected set

    protected var hoverAnimator = LinearAnimator(InterpolatedFloat(0f, 1f))
    protected var colorWeightStd = 1f
    protected var colorWeightHovered = 0f
    protected var fgColor = MutableColor()

    protected val meshData = MeshData(true, true, true)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)
    protected var meshAdded = false

    protected var isUiUpdateNeeded = false

    init {
        mesh.shader = fontShader()

        hoverAnimator.speed = 0f
        hoverAnimator.value.onUpdate = { v ->
            colorWeightHovered = v
            colorWeightStd = 1f - v
            isUiUpdateNeeded = true
        }

        onHoverEnter += { ptr, rt, ctx ->
            isHovered = true
            hoverAnimator.duration = 0.1f
            hoverAnimator.speed = 1f
        }

        onHoverExit += { ptr, rt, ctx ->
            isHovered = false
            hoverAnimator.duration = 0.2f
            hoverAnimator.speed = -1f
        }

        onHover += { ptr, rt, ctx ->
            if (ptr.isLeftButtonEvent && !ptr.isLeftButtonDown) {
                for (i in onClick.indices) {
                    onClick[i](ptr, rt, ctx)
                }
            }
        }
    }

    override fun render(ctx: RenderContext) {
        hoverAnimator.tick(ctx)

        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.alpha = alpha
        }

        if (isUiUpdateNeeded) {
            isUiUpdateNeeded = false
            updateButtonUi(ctx)
        }

        super.render(ctx)
    }

    override fun update(ctx: RenderContext) {
        if (!meshAdded) {
            meshAdded = true
            this += mesh
        }
        updateButtonUi(ctx)
        super.update(ctx)
    }

    protected open fun updateButtonUi(ctx: RenderContext) {
        textColor.updateProp()
        textColorHovered.updateProp()
        if (font.needsUpdate()) {
            font.prop?.dispose(ctx)
            font.updateProp()
        }
        val font = font.propOrDefault

        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.texture = font
        }

        fgColor.clear()
        fgColor.add(textColor.propOrDefault, colorWeightStd)
        fgColor.add(textColorHovered.propOrDefault, colorWeightHovered)

        val txtWidth = font.textWidth(text)
        val txtHeight = font.fontProps.sizeUnits * 0.7f
        setupBuilder(meshBuilder)
        meshBuilder.color = fgColor
        meshBuilder.text(font) {
            val x = when (textAlignment.xAlignment) {
                Alignment.START -> padding.left.toUnits(width, dpi)
                Alignment.CENTER -> (width - txtWidth) / 2f
                Alignment.END -> width - txtWidth - padding.right.toUnits(width, dpi)
            }
            val y = when (textAlignment.yAlignment) {
                Alignment.START -> height - padding.top.toUnits(width, dpi) - txtHeight
                Alignment.CENTER -> (height - txtHeight) / 2f
                Alignment.END -> padding.bottom.toUnits(width, dpi)
            }
            origin.set(x, y, 0f)
            text = this@Button.text
        }
    }

    override fun applyTheme(theme: UiTheme, ctx: RenderContext) {
        super.applyTheme(theme, ctx)

        font.setTheme(theme.standardFont(root?.uiDpi ?: 96f))
        textColor.setTheme(theme.foregroundColor)
        textColorHovered.setTheme(theme.accentColor)
    }
}
