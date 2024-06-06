package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SceneComponentData
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.logW

class SceneComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SceneComponentData>
) :
    GameEntityDataComponent<SceneComponent, SceneComponentData>(gameEntity, componentInfo),
    DrawNodeComponent
{
    override val drawNode: Scene = Scene(gameEntity.name).apply { tryEnableInfiniteDepth() }

    val maxNumLights: Int get() = data.maxNumLights
    val cameraComponent: CameraComponent? get() = gameEntity.scene.sceneEntities[data.cameraEntityId]?.getComponent()

    init {
        componentOrder = COMPONENT_ORDER_EARLY
        gameEntity.scene.shaderData.maxNumberOfLights = data.maxNumLights
    }

    override fun onDataChanged(oldData: SceneComponentData, newData: SceneComponentData) {
        if (oldData.maxNumLights != newData.maxNumLights) {
            gameEntity.scene.getAllComponents<UpdateMaxNumLightsComponent>().forEach { comp ->
                comp.updateMaxNumLightsComponent(newData.maxNumLights)
            }
        }
        if (oldData.cameraEntityId != newData.cameraEntityId) {
            val newCam: CameraComponent? = gameEntity.scene.sceneEntities[data.cameraEntityId]?.getComponent()
            gameEntity.scene.getAllComponents<UpdateSceneCameraComponent>().forEach { comp ->
                comp.updateSceneCameraComponent(newCam?.drawNode)
            }
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        val cam = cameraComponent
        if (cam != null) {
            drawNode.camera = cam.drawNode
        } else {
            logW { "Scene ${gameEntity.name} has no camera attached" }
        }
    }
}

interface UpdateMaxNumLightsComponent {
    fun updateMaxNumLightsComponent(newMaxNumLights: Int)
}

interface UpdateSceneCameraComponent {
    fun updateSceneCameraComponent(newCamera: Camera?)
}
