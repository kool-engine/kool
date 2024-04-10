package de.fabmax.kool

import de.fabmax.kool.scene.Scene

class KoolApplication(val ctx: KoolContext) {

    inline fun addScene(name: String? = null, block: Scene.() -> Unit): Scene {
        val scene = Scene(name).apply(block)
        ctx.addScene(scene)
        return scene
    }

    fun removeScene(scene: Scene) = ctx.removeScene(scene)

}