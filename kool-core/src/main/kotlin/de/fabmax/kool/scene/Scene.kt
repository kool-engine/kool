package de.fabmax.kool.scene

import de.fabmax.kool.Camera
import de.fabmax.kool.platform.RenderContext

/**
 * @author fabmax
 */
class Scene {

    var camera = Camera()
    var light = Light()
    var root: Node? = null

    fun onRender(ctx: RenderContext) {
        camera.updateCamera(ctx)
        root?.render(ctx)
    }

}

