package de.fabmax.kool.editor.data

import de.fabmax.kool.modules.ksl.KslLitShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import kotlinx.serialization.Serializable

@Serializable
sealed interface SceneBackgroundData {
    fun applyBackground(scene: Scene)

    @Serializable
    class SingleColor(val color: ColorData) : SceneBackgroundData {

        constructor(color: Color) : this(ColorData(color))

        override fun applyBackground(scene: Scene) {
            val color = color.toColor()
            scene.mainRenderPass.clearColor = color

            val linColor = color.toLinear()
            fun Node.applyBgColor() {
                if (this is Mesh) {
                    (this.shader as? KslLitShader)?.let {
                        it.ambientFactor = linColor
                    }
                }
                children.forEach { it.applyBgColor() }
            }
            scene.applyBgColor()
        }
    }

    @Serializable
    class Hdri(val hdriPath: String) : SceneBackgroundData {
        override fun applyBackground(scene: Scene) {
            val existing = scene.findNode("Background skybox")
        }
    }
}

