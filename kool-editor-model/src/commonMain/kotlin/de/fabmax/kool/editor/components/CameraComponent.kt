package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.sceneComponent
import de.fabmax.kool.editor.data.CameraComponentData
import de.fabmax.kool.editor.data.CameraTypeData
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Camera

class CameraComponent(
    gameEntity: GameEntity,
    componentData: CameraComponentData = CameraComponentData(CameraTypeData.Perspective())
) :
    GameEntityDataComponent<CameraComponentData>(gameEntity, componentData),
    DrawNodeComponent
{
    val cameraState = mutableStateOf(componentData.camera).onChange {
        if (AppState.isEditMode) {
            componentData.camera = it
        }
        updateCamera(it, false)
    }

    override var drawNode: Camera = componentData.camera.createCamera()
        private set

    override suspend fun applyComponent() {
        super.applyComponent()
        cameraState.set(componentData.camera)
        updateCamera(componentData.camera, true)
    }

    private fun updateCamera(cameraData: CameraTypeData, forceReplaceNode: Boolean) {
        val updateCamera = cameraData.updateOrCreateCamera(drawNode)
        updateCamera.name = gameEntity.name
        if (forceReplaceNode || updateCamera != drawNode) {
            val scene = gameEntity.sceneComponent.drawNode
            if (scene.camera == drawNode) {
                scene.camera = updateCamera
            }

            drawNode = updateCamera
            gameEntity.replaceDrawNode(drawNode)
        }
    }
}