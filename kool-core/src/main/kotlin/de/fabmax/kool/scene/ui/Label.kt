package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

open class Label(name: String) : UiComponent(name) {

    var text = ""
        set(value) {
            if (value != field) {
                field = value
                isFgUpdateNeeded = true
            }
        }

    var textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
        set(value) {
            if (value != field) {
                field = value
                isFgUpdateNeeded = true
            }
        }

    val font = ThemeOrCustomProp(Font.DEFAULT_FONT)
    val textColor = ThemeOrCustomProp(Color.WHITE)

    protected var foregroundColor = MutableColor()

    protected val meshData = MeshData(true, true, true)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)
    protected var meshAdded = false

    init {
        mesh.shader = fontShader {
            lightModel = LightModel.PHONG_LIGHTING
            colorModel = ColorModel.VERTEX_COLOR
            isAlpha = true
        }
    }

    override fun applyComponentAlpha() {
        super.applyComponentAlpha()
        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.alpha = alpha
        }
    }

    override fun updateForeground(ctx: RenderContext) {
        super.updateForeground(ctx)
        if (!meshAdded) {
            meshAdded = true
            this += mesh
        }

        if (font.isUpdate) {
            font.prop?.dispose(ctx)
        }
        val font = font.apply()

        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.texture = font
        }

        val txtWidth = font.textWidth(text)
        setupBuilder(meshBuilder)
        meshBuilder.color = foregroundColor
        meshBuilder.text(font) {
            val x = when (textAlignment.xAlignment) {
                Alignment.START -> padding.left.toUnits(width, dpi)
                Alignment.CENTER -> (width - txtWidth) / 2f
                Alignment.END -> width - txtWidth - padding.right.toUnits(width, dpi)
            }
            val y = when (textAlignment.yAlignment) {
                Alignment.START -> height - padding.top.toUnits(width, dpi) - font.normHeight
                Alignment.CENTER -> (height - font.normHeight) / 2f
                Alignment.END -> padding.bottom.toUnits(width, dpi)
            }
            origin.set(x, y, 0f)
            text = this@Label.text
        }
    }

    override fun applyTheme(theme: UiTheme, ctx: RenderContext) {
        super.applyTheme(theme, ctx)
        font.setTheme(theme.standardFont(root?.uiDpi ?: 96f))
        textColor.setTheme(theme.foregroundColor)
    }
}
