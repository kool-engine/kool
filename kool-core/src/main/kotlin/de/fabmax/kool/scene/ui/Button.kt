package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.fontShader

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

    var textColor = Color.BLACK
        set(value) {
            field = value
            isUpdateNeeded = true
        }

    init {
        this += mesh
        mesh.shader = fontShader()
    }

    override fun update(ctx: RenderContext) {
        super.update(ctx)

        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.texture = font
        }

        val txtWidth = font.textWidth(text)
        setupBuilder(meshBuilder)
        meshBuilder.run {
            color = textColor
            text(font) {
                origin.set((width - txtWidth) / 2f, (height - font.fontProps.sizeUnits * 0.7f) / 2f, 0f)
                text = this@Button.text
            }
        }
    }
}
