package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.data.CameraTypeData

class SetCameraAction(
    component: CameraComponent,
    val setCameraData: CameraTypeData,
    val undoCamData: CameraTypeData
) : ComponentAction<CameraComponent>(component) {

    private val component: CameraComponent? get() = nodeModel?.getComponent()

    override fun doAction() {
        component?.cameraState?.set(setCameraData)
    }

    override fun undoAction() {
        component?.cameraState?.set(undoCamData)
    }
}