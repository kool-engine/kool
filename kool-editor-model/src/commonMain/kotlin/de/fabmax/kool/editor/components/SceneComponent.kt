package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.SceneShaderData
import de.fabmax.kool.editor.api.cachedSceneComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.MaterialComponentData
import de.fabmax.kool.editor.data.SceneComponentData
import de.fabmax.kool.scene.Scene

class SceneComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SceneComponentData>
) :
    GameEntityDataComponent<SceneComponentData>(gameEntity, componentInfo),
    EditorScene.SceneShaderDataListener,
    MaterialComponent.ListenerComponent
{
    val sceneNode: Scene = Scene(gameEntity.name).apply {
        tryEnableInfiniteDepth()
        lighting.clear()
    }

    val cameraComponent: CameraComponent? get() = gameEntity.scene.sceneEntities[data.cameraEntityId]?.getComponent()

    private val camListeners by cachedSceneComponents<CameraAwareComponent>()

    init {
        componentOrder = COMPONENT_ORDER_EARLY
        gameEntity.scene.shaderData.maxNumberOfLights = data.maxNumLights
    }

    override fun onDataChanged(oldData: SceneComponentData, newData: SceneComponentData) {
        scene.shaderData.maxNumberOfLights = newData.maxNumLights

        if (oldData.cameraEntityId != newData.cameraEntityId) {
            val newCam = gameEntity.scene.sceneEntities[newData.cameraEntityId]?.getComponent<CameraComponent>()?.camera
            newCam?.let {
                sceneNode.camera = newCam
                camListeners.forEach { it.updateSceneCamera(newCam) }
            }
        }
    }

    override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
        scene.sceneNodes.onSceneShaderDataChanged(scene, sceneShaderData)
    }

    override suspend fun onMaterialChanged(component: MaterialComponent, materialData: MaterialComponentData) {
        scene.sceneNodes.onMaterialChanged(component, materialData)
    }
}
