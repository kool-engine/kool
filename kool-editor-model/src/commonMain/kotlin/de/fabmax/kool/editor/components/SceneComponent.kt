package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedSceneComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SceneComponentData
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.logW

class SceneComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SceneComponentData>
) :
    GameEntityDataComponent<SceneComponentData>(gameEntity, componentInfo),
    DrawNodeComponent
{
    override val drawNode: Scene = Scene(gameEntity.name).apply {
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
            val newCam = gameEntity.scene.sceneEntities[newData.cameraEntityId]?.getComponent<CameraComponent>()?.drawNode
            newCam?.let {
                drawNode.camera = newCam
                camListeners.forEach { it.updateSceneCamera(newCam) }
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
