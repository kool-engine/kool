package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedSceneComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SceneComponentData
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Scene

class SceneComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SceneComponentData>
) :
    GameEntityDataComponent<SceneComponentData>(gameEntity, componentInfo)
{
    val sceneNode: Scene = Scene(gameEntity.name).apply {
        lighting.clear()
        mainRenderPass.isDoublePrecision = data.isFloatingOrigin
    }

    val isFloatingOrigin: Boolean get() = data.isFloatingOrigin
    val isDoublePrecision: Boolean get() = sceneNode.mainRenderPass.isDoublePrecision

    val cameraComponent: CameraComponent? get() = gameEntity.scene.sceneEntities[data.cameraEntityId]?.getComponent()

    private val camListeners by cachedSceneComponents<CameraAwareComponent>()

    init {
        componentOrder = COMPONENT_ORDER_EARLY
        gameEntity.scene.shaderData.apply {
            maxNumberOfLights = data.maxNumLights
            toneMapping = data.toneMapping
        }
    }

    override fun onDataChanged(oldData: SceneComponentData, newData: SceneComponentData) {
        sceneNode.mainRenderPass.isDoublePrecision = newData.isFloatingOrigin

        gameEntity.scene.shaderData.apply {
            maxNumberOfLights = newData.maxNumLights
            toneMapping = newData.toneMapping
        }

        if (oldData.cameraEntityId != newData.cameraEntityId) {
            val newCam = gameEntity.scene.sceneEntities[newData.cameraEntityId]?.getComponent<CameraComponent>()?.camera
            newCam?.let { setCamera(it) }
        }
    }

    fun setCamera(cam: Camera) {
        sceneNode.camera = cam
        camListeners.forEach { it.updateSceneCamera(cam) }
    }
}
