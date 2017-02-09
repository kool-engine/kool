package de.fabmax.kool.demo

import de.fabmax.kool.SharedAssetTexture
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.transformGroup
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.textureMesh

/**
 * @author fabmax
 */
fun textureDemo(ctx: RenderContext) {

    ctx.scene.root = transformGroup {
        for (i in -1..1) {
            +textureMesh {
                translate(-2f + i * 5, -2f, 0f)
                rect {
                    width = 4f
                    height = 4f
                }
                shader?.texture = SharedAssetTexture("test.png")
            }
        }
    }


    ctx.clearColor = Color(0.05f, 0.15f, 0.25f, 1f)
    ctx.scene.camera.position.set(0f, 0f, 15f)
    ctx.run()
}
