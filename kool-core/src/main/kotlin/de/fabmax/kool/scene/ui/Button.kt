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

    protected val meshData = MeshData(true, true, true)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)

    var text = ""
        set(value) {
            field = value
            isUpdateNeeded = true
        }

    var font = Font.DEFAULT_FONT
        set(value) {
            field = value
            isUpdateNeeded = true
        }

    var textColor = Color.WHITE
        set(value) {
            field = value
            isUpdateNeeded = true
        }

    var textColorHovered = Color.LIME
        set(value) {
            field = value
            isUpdateNeeded = true
        }

    var isHovered = false
        protected set

    protected var hoverAnimator = LinearAnimator(InterpolatedFloat(0f, 1f))
    protected var colorWeightStd = 1f
    protected var colorWeightHovered = 0f
    protected var mixColor = MutableColor()

    init {
        this += mesh
        mesh.shader = fontShader()

        hoverAnimator.speed = 0f
        hoverAnimator.value.onUpdate = { v ->
            colorWeightHovered = v
            colorWeightStd = 1f - v
            isUpdateNeeded = true
        }

        onHoverEnter += { ptr, rt, ctx ->
            isHovered = true
            hoverAnimator.duration = 0.2f
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

        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.texture = font
        }

        mixColor.set(0f, 0f, 0f, 0f)
        mixColor.add(textColor, colorWeightStd)
        mixColor.add(textColorHovered, colorWeightHovered)

        val txtWidth = font.textWidth(text)
        setupBuilder(meshBuilder)
        meshBuilder.run {
            color = mixColor
            text(font) {
                origin.set((width - txtWidth) / 2f, (height - font.fontProps.sizeUnits * 0.7f) / 2f, 0f)
                text = this@Button.text
            }
        }
    }
}
