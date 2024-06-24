package de.fabmax.kool.editor.components

import de.fabmax.kool.scene.Camera

fun interface CameraAwareComponent {
    fun updateSceneCamera(camera: Camera)
}