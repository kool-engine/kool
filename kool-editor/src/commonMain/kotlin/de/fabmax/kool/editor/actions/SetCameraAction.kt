package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.data.CameraTypeData
import de.fabmax.kool.editor.data.EntityId

class SetCameraAction(
    entityId: EntityId,
    val setCameraData: CameraTypeData,
    val undoCamData: CameraTypeData
) : ComponentAction<CameraComponent>(entityId, CameraComponent::class) {

    override fun doAction() {
        component?.cameraState?.set(setCameraData)
    }

    override fun undoAction() {
        component?.cameraState?.set(undoCamData)
    }
}