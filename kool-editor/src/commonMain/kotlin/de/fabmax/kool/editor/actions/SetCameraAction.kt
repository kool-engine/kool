package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.data.CameraTypeData

class SetCameraAction(
    val cameraComponent: CameraComponent,
    val setCameraData: CameraTypeData,
    val undoCamData: CameraTypeData
) : EditorAction {

    override fun doAction() {
        cameraComponent.cameraState.set(setCameraData)
    }

    override fun undoAction() {
        cameraComponent.cameraState.set(undoCamData)
    }
}