package de.fabmax.kool.modules.ui2

import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.Scene

/**
 * Creates a new scene and sets it up as a UI scene.
 *
 * @see [setupUiScene]
 */
fun UiScene(
    name: String = "UiScene",
    clearScreen: Boolean = false,
    block: Scene.() -> Unit
) = Scene(name).apply {
    setupUiScene(clearScreen)
    block()
}

/**
 * Sets up a scene to host UI content. To do so, this method installs an orthographic camera, which auto-adjusts its
 * clip size to the viewport size. Also, by default, screen-clearing is disabled, because UIs usually are drawn on
 * top of stuff, which should not be cleared away.
 */
fun Scene.setupUiScene(clearScreen: Boolean = false) {
    if (!clearScreen) {
        clearColor = null
    }

    camera = OrthographicCamera()
    onUpdate += { ev ->
        // Setup camera to cover viewport size with origin in upper left corner.
        // Camera clip space uses OpenGL coordinates -> y-axis points downwards, i.e. bottom coordinate has to be
        // set to negative viewport height. UI surface internally mirrors y-axis to get a regular UI coordinate
        // system (however, this means triangle index order or face orientation has to be inverted).
        (camera as? OrthographicCamera)?.let { cam ->
            cam.left = 0f
            cam.top = 0f
            cam.right = ev.viewport.width.toFloat()
            cam.bottom = -ev.viewport.height.toFloat()
        }
    }
}