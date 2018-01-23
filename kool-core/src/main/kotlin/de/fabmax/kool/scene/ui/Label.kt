package de.fabmax.kool.scene.ui

import de.fabmax.kool.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

open class Label(name: String, root: UiRoot) : UiComponent(name, root) {

    var text = ""
        set(value) {
            if (value != field) {
                field = value
                requestUiUpdate()
            }
        }

    var textAlignment = Gravity(Alignment.START, Alignment.CENTER)
        set(value) {
            if (value != field) {
                field = value
                requestUiUpdate()
            }
        }

    // fixme: updateUi() is not issued when custom values are set
    val font = ThemeOrCustomProp(Font.DEFAULT_FONT)
    val textColor = ThemeOrCustomProp(Color.WHITE)

    override fun setThemeProps() {
        super.setThemeProps()
        font.setTheme(root.theme.standardFont(dpi))
        textColor.setTheme(root.theme.foregroundColor)
    }

    override fun createThemeUi(ctx: RenderContext): ComponentUi {
        return root.theme.newLabelUi(this)
    }
}

open class LabelUi(val label: Label, private val baseUi: ComponentUi) : ComponentUi by baseUi {

    protected val meshData = MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)
    protected var meshAdded = false

    protected var font = label.font.prop
    protected var textColor = MutableColor()

    protected var textStartX = 0f
    protected var textWidth = 0f
    protected var textBaseline = 0f

    override fun updateComponentAlpha() {
        baseUi.updateComponentAlpha()
        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.alpha = label.alpha
        }
    }

    override fun createUi(ctx: RenderContext) {
        baseUi.createUi(ctx)
        mesh.shader = fontShader {
            lightModel = label.root.shaderLightModel
            colorModel = ColorModel.VERTEX_COLOR
            isAlpha = true
        }
        label += mesh
    }

    override fun updateUi(ctx: RenderContext) {
        baseUi.updateUi(ctx)

        if (label.font.isUpdate) {
            label.font.prop.dispose(ctx)
            font = label.font.apply()
        }

        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.texture = font
        }

        label.setupBuilder(meshBuilder)
        updateTextColor()
        computeTextMetrics()
        renderText(ctx)
    }

    override fun disposeUi(ctx: RenderContext) {
        baseUi.disposeUi(ctx)
        label -= mesh
        mesh.dispose(ctx)
    }

    protected open fun computeTextMetrics() {
        textWidth = font.textWidth(label.text)

        textStartX = when (label.textAlignment.xAlignment) {
            Alignment.START -> label.padding.left.toUnits(label.width, label.dpi)
            Alignment.CENTER -> (label.width - textWidth) / 2f
            Alignment.END -> label.width - textWidth - label.padding.right.toUnits(label.width, label.dpi)
        }

        textBaseline = when (label.textAlignment.yAlignment) {
            Alignment.START -> label.height - label.padding.top.toUnits(label.width, label.dpi) - font.normHeight
            Alignment.CENTER -> (label.height - font.normHeight) / 2f
            Alignment.END -> label.padding.bottom.toUnits(label.height, label.dpi)
        }
    }

    protected open fun renderText(ctx: RenderContext) {
        meshBuilder.color = textColor
        meshBuilder.text(font) {
            origin.set(textStartX, textBaseline, label.dp(4f))
            text = label.text
        }
    }

    protected open fun updateTextColor() {
        textColor.set(label.textColor.apply())
    }
}
