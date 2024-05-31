package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.CameraComponentData
import de.fabmax.kool.editor.data.CameraTypeData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Camera

class CameraComponent(
    nodeModel: SceneNodeModel,
    override val componentData: CameraComponentData = CameraComponentData(CameraTypeData.Perspective())
) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<CameraComponentData>,
    ContentComponent
{
    val cameraState = mutableStateOf(componentData.camera).onChange {
        if (AppState.isEditMode) {
            componentData.camera = it
        }
        updateCamera(it, false)
    }

    var camera: Camera = componentData.camera.createCamera()
        private set

    override val contentNode: Camera
        get() = camera

    override suspend fun createComponent() {
        super.createComponent()
        cameraState.set(componentData.camera)
        updateCamera(componentData.camera, true)
    }

    private fun updateCamera(cameraData: CameraTypeData, forceReplaceNode: Boolean) {
        val updateCamera = cameraData.updateOrCreateCamera(camera)
        if (forceReplaceNode || updateCamera != camera) {
            if (sceneModel.drawNode.camera == camera) {
                sceneModel.drawNode.camera = updateCamera
            }

            camera = updateCamera
            nodeModel.setDrawNode(camera)
        }
    }
}