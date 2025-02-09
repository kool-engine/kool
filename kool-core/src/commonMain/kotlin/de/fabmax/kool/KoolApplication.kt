package de.fabmax.kool

import de.fabmax.kool.modules.ui2.setupUiScene
import de.fabmax.kool.pipeline.ClearColor
import de.fabmax.kool.pipeline.ClearColorLoad
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.launchDelayed

class KoolApplication(val ctx: KoolContext)


/**
 * Creates a new [Scene], adds it to the application context [KoolApplication.ctx] and returns it. The provided [block]
 * can be used to set up the scene and add content to it.
 */
inline fun KoolApplication.addScene(name: String = "content-scene", block: Scene.() -> Unit): Scene {
    val scene = Scene(name).apply(block)
    ctx.addScene(scene)
    return scene
}

/**
 * Creates a new [Scene], configures it as a UI scene (by calling [Scene.setupUiScene]), adds it to the
 * application context [KoolApplication.ctx] and returns it. [clearColor] determines the screen background color.
 * Stand-alone UIs should set this to their desired background color, overlay UIs should keep the value at null
 * (the default), so that the screen-clearing is disabled and the overlaid scene remains visible.
 *
 * The provided [block] can be used to set up the scene and add content to it.
 */
inline fun KoolApplication.addUiScene(
    clearColor: ClearColor = ClearColorLoad,
    name: String = "ui-scene",
    block: Scene.() -> Unit
): Scene = addScene(name) {
    setupUiScene(clearColor)
    block()
}

/**
 * Removes the given [scene] from the application context but does not release it. This should be used in case
 * the scene will be re-added at some later point in time.
 */
fun KoolApplication.removeScene(scene: Scene) = ctx.removeScene(scene)

/**
 * Removes the given [scene] from the application context and releases it freeing any occupied resources (scene
 * mesh geometry, etc.). The scene is released after a delay of one frame, so that it can safely be rendered one final
 * time in case the current frame is already in flight.
 */
fun KoolApplication.removeAndReleaseScene(scene: Scene) {
    ctx.removeScene(scene)
    launchDelayed(1) {
        scene.release()
    }
}