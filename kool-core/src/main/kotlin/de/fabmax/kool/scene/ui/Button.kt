package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */
open class Button(name: String) : UiComponent(name) {

    var text = ""
        set(value) {
            field = value
            isUpdateNeeded = true
        }

    val font = ThemeOrCustomProp(Font.DEFAULT_FONT)
    val textColor = ThemeOrCustomProp(Color.WHITE)
    val textColorHovered = ThemeOrCustomProp(Color.WHITE)

    var isHovered = false
        protected set

    protected var hoverAnimator = LinearAnimator(InterpolatedFloat(0f, 1f))
    protected var colorWeightStd = 1f
    protected var colorWeightHovered = 0f
    protected var mixColor = MutableColor()

    protected val meshData = MeshData(true, true, true)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)
    protected var meshAdded = false

    init {
        mesh.shader = fontShader()

        hoverAnimator.speed = 0f
        hoverAnimator.value.onUpdate = { v ->
            colorWeightHovered = v
            colorWeightStd = 1f - v
            isUpdateNeeded = true
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

        onRender += { ctx -> hoverAnimator.tick(ctx) }
    }

    override fun update(ctx: RenderContext) {
        super.update(ctx)

        if (!meshAdded) {
            meshAdded = true
            this += mesh
        }

        textColor.updateProp()
        textColorHovered.updateProp()
        if (font.needsUpdate()) {
            font.prop?.dispose(ctx)
            font.updateProp()
        }

        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.texture = font.propOrDefault
        }

        mixColor.set(0f, 0f, 0f, 0f)
        mixColor.add(textColor.propOrDefault, colorWeightStd)
        mixColor.add(textColorHovered.propOrDefault, colorWeightHovered)

        val txtWidth = font.propOrDefault.textWidth(text)
        setupBuilder(meshBuilder)
        meshBuilder.run {
            color = mixColor
            text(font.propOrDefault) {
                origin.set((width - txtWidth) / 2f, (height - font.fontProps.sizeUnits * 0.7f) / 2f, 0f)
                text = this@Button.text
            }
        }
    }

    override fun applyTheme(theme: UiTheme, ctx: RenderContext) {
        super.applyTheme(theme, ctx)

        font.setTheme(theme.standardFont(root?.uiDpi ?: 96f))
        textColor.setTheme(theme.foregroundColor)
        textColorHovered.setTheme(theme.accentColor)
    }
}
