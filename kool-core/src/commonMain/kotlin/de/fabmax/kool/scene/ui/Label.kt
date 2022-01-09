package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.*
import kotlin.math.min

/**
 * @author fabmax
 */

open class Label(name: String, root: UiRoot) : UiComponent(name, root) {

    var text = name
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

    var autoWrapWidth = -1f

    // fixme: updateUi() is not issued when custom values are set
    val font = ThemeOrCustomProp<Font?>(null)
    val textColor = ThemeOrCustomProp(Color.WHITE)

    override fun setThemeProps(ctx: KoolContext) {
        super.setThemeProps(ctx)
        font.setTheme(standardFont())
        textColor.setTheme(root.theme.foregroundColor)
    }

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.newLabelUi(this)
    }
}

open class LabelUi(val label: Label, val baseUi: ComponentUi) : ComponentUi by baseUi {

    protected var font = label.font.prop
    protected var textColor = MutableColor()

    protected val geom = IndexedVertexList(UiShader.UI_MESH_ATTRIBS)
    protected val meshBuilder = MeshBuilder(geom)
    protected val mesh = Mesh(geom)
    protected val shader = UiShader()

    var textStartX = 0f
    var textWidth = 0f
    var textBaseline = 0f

    override fun updateComponentAlpha() {
        baseUi.updateComponentAlpha()
        shader.alpha(label.alpha)
    }

    override fun createUi(ctx: KoolContext) {
        baseUi.createUi(ctx)
        mesh.shader = shader
        label += mesh
    }

    override fun updateUi(ctx: KoolContext) {
        baseUi.updateUi(ctx)

        if (!label.font.isUpdate && label.font.prop == null) {
            label.font.setCustom(Font.DEFAULT_FONT)
        }
        if (label.font.isUpdate) {
//            label.font.prop?.dispose(ctx)
            font = label.font.apply()
            font?.let { shader.setFont(it, ctx) }
        }

        label.setupBuilder(meshBuilder)
        updateTextColor()
        computeTextMetrics()
        renderText(label.text, ctx)
    }

    override fun onRender(ctx: KoolContext) {
//        shader.setDrawBounds(label.drawBounds)
        baseUi.onRender(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        baseUi.dispose(ctx)
        label -= mesh
        mesh.dispose(ctx)
    }

    protected open fun computeTextMetrics() {
        val txtW = font?.textWidth(label.text) ?: 0f
        textWidth = if (label.autoWrapWidth > 0) min(label.autoWrapWidth, txtW) else txtW

        textStartX = when (label.textAlignment.xAlignment) {
            Alignment.START -> label.padding.left.toUnits(label.width, label.dpi)
            Alignment.CENTER -> (label.width - textWidth) / 2f
            Alignment.END -> label.width - textWidth - label.padding.right.toUnits(label.width, label.dpi)
        }

        textBaseline = when (label.textAlignment.yAlignment) {
            Alignment.START -> label.height - label.padding.top.toUnits(label.width, label.dpi) - (font?.normHeight ?: 0f)
            Alignment.CENTER -> (label.height - (font?.normHeight ?: 0f)) / 2f
            Alignment.END -> label.padding.bottom.toUnits(label.height, label.dpi)
        }
    }

    protected open fun renderText(dispText: String, ctx: KoolContext) {
        meshBuilder.color = textColor
        val fnt = font
        if (fnt != null) {
            meshBuilder.text(fnt) {
                origin.set(textStartX, textBaseline, label.dp(0.1f))
                text = dispText
                autoWrapWidth = label.autoWrapWidth
            }
        }
    }

    protected open fun updateTextColor() {
        textColor.set(label.textColor.apply())
    }
}
