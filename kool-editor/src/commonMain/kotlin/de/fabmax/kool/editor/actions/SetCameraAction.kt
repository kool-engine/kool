package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.data.CameraTypeData
import de.fabmax.kool.editor.data.NodeId

class SetCameraAction(
    nodeId: NodeId,
    val setCameraData: CameraTypeData,
    val undoCamData: CameraTypeData
) : ComponentAction<CameraComponent>(nodeId, CameraComponent::class) {

    override fun doAction() {
        component?.cameraState?.set(setCameraData)
    }

    override fun undoAction() {
        component?.cameraState?.set(undoCamData)
    }
}