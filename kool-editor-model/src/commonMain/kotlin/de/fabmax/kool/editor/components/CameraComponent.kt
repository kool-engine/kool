package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.sceneComponent
import de.fabmax.kool.editor.data.CameraComponentData
import de.fabmax.kool.editor.data.CameraTypeData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Camera

class CameraComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<CameraComponentData> = ComponentInfo(CameraComponentData(CameraTypeData.Perspective()))
) :
    GameEntityDataComponent<CameraComponent, CameraComponentData>(gameEntity, componentInfo),
    DrawNodeComponent
{
    val cameraState = mutableStateOf(data.camera).onChange {
        if (AppState.isEditMode) {
            data.camera = it
        }
        updateCamera(it, false)
    }

    override var drawNode: Camera = data.camera.createCamera()
        private set

    override suspend fun applyComponent() {
        super.applyComponent()
        cameraState.set(data.camera)
        updateCamera(data.camera, true)
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