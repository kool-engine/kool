package de.fabmax.kool.scene

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.ShadowMap

/**
 * @author fabmax
 */
class Lighting(val scene: Scene) {
    var primaryLight = DirectionalLight()

    var shadowMap: ShadowMap? = null
        set(value) {
            if (field != null) {
                scene.dispose(field!!)
            }
            field = value
        }

    init {
        scene.onPreRender += { ctx ->
            shadowMap?.renderShadowMap(this, ctx)
        }
        scene.onDispose += { ctx ->
            shadowMap?.dispose(ctx)
        }
    }
}

abstract class Light {
    val color = MutableColor(Color.WHITE)
}

class DirectionalLight : Light() {
    val direction = MutableVec3f(1f, 1f, 1f)
}

// todo: point light, spot light, ...
