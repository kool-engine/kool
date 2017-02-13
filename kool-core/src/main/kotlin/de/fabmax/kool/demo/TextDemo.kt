package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.group
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.fontShader
import de.fabmax.kool.util.textureMesh

/**
 * @author fabmax
 */
fun textDemo(ctx: RenderContext) {
    val font = Font("Segoe UI", 48.0f)
    val str = "Hello World!"

    ctx.scene.root = group {
        +sphericalInputTransform { +ctx.scene.camera }

        +textureMesh {
            shader = fontShader(font, Color.LIME) { }
            translate(-1f, -.25f, 0f)

            for (c in str) {
                val metrics = font.charMap[c]!!

                rect {
                    width = metrics.width / 100
                    height = metrics.height / 100

                    texCoordUpperLeft.set(metrics.uvMin)
                    texCoordUpperRight.set(metrics.uvMax.x, metrics.uvMin.y)
                    texCoordLowerLeft.set(metrics.uvMin.x, metrics.uvMax.y)
                    texCoordLowerRight.set(metrics.uvMax)
                }

                translate(metrics.advance / 100, 0f, 0f)
            }
        }
    }


    ctx.clearColor = Color(0.05f, 0.15f, 0.25f, 1f)
    ctx.run()
}
