package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.sceneComponent
import de.fabmax.kool.editor.data.CameraComponentData
import de.fabmax.kool.editor.data.CameraTypeData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node

class CameraComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<CameraComponentData> = ComponentInfo(CameraComponentData(CameraTypeData.Perspective()))
) :
    GameEntityDataComponent<CameraComponentData>(gameEntity, componentInfo),
    SceneNodeComponent
{
    var camera: Camera = data.camera.createCamera()
        private set

    private val entityTransformUpdateCb: (RenderPass.UpdateEvent) -> Unit = {
        camera.transform.setMatrix(gameEntity.transform.globalTransform.matF)
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        updateCamera(data.camera)
        attachCamera(camera.parent)
    }

    override fun destroyComponent() {
        camera.parent?.removeNode(camera)
        super.destroyComponent()
    }

    override fun onDataChanged(oldData: CameraComponentData, newData: CameraComponentData) {
        updateCamera(newData.camera)
    }

    fun attachCameraToNode(parent: Node) {
        camera.onUpdate -= entityTransformUpdateCb
        camera.parent?.removeNode(camera)
        parent.addNode(camera)
    }

    fun attachCameraToGameEntity() {
        camera.parent?.removeNode(camera)
        camera.onUpdate += entityTransformUpdateCb
        gameEntity.sceneComponent.sceneNode.addNode(camera)
    }

    private fun updateCamera(cameraData: CameraTypeData) {
        if (!cameraData.updateCamera(camera)) {
            val parent = camera.parent
            parent?.removeNode(camera)
            camera = cameraData.createCamera()
            attachCamera(parent)
        }
        camera.name = gameEntity.name
    }

    private fun attachCamera(parent: Node?) {
        if (sceneComponent.data.cameraEntityId == gameEntity.id && !AppState.isEditMode) {
            sceneComponent.setCamera(camera)
        }
        if (parent != null) {
            attachCameraToNode(parent)
        } else {
            attachCameraToGameEntity()
        }
    }
}