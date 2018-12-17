package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
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
        private set(value) {
            if (field != null) {
                scene.dispose(field!!)
            }
            field = value
        }
    private var isDefaultShadowMap = false

    init {
        scene.onPreRender += { ctx ->
            shadowMap?.renderShadowMap(this, ctx)
        }
        scene.onDispose += { ctx ->
            shadowMap?.dispose(ctx)
        }
    }

    fun useDefaultShadowMap(ctx: KoolContext) {
        isDefaultShadowMap = true
        shadowMap = ctx.renderingHints.shadowPreset.createShadowMap(ctx.renderingHints)
    }

    fun useCustomShadowMap(customShadowMap: ShadowMap) {
        isDefaultShadowMap = false
        shadowMap = customShadowMap
    }

    fun disableShadowMap() {
        isDefaultShadowMap = false
        shadowMap = null
    }

    fun onRenderingHintsChanged(ctx: KoolContext) {
        if (isDefaultShadowMap) {
            useDefaultShadowMap(ctx)
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
