package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.sceneComponent
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.DiscreteLightComponentData
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color

class DiscreteLightComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<DiscreteLightComponentData> = ComponentInfo(
        DiscreteLightComponentData(LightTypeData.Directional(ColorData(Color.WHITE), 3f))
    )
) :
    GameEntityDataComponent<DiscreteLightComponentData>(gameEntity, componentInfo),
    SceneNodeComponent
{
    var light: Light = data.light.createLight()
        private set

    private val entityTransformUpdateCb: (RenderPass.UpdateEvent) -> Unit = {
        light.transform.setMatrix(gameEntity.localToViewF)
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        updateLight(data.light)
        attachLight(light.parent)
    }

    override fun destroyComponent() {
        val scene = sceneComponent.sceneNode
        scene.lighting.removeLight(light)
        light.parent?.removeNode(light)
        super.destroyComponent()
    }

    override fun onDataChanged(oldData: DiscreteLightComponentData, newData: DiscreteLightComponentData) {
        updateLight(newData.light)
    }

    fun attachLightToNode(parent: Node) {
        light.onUpdate -= entityTransformUpdateCb
        light.parent?.removeNode(light)
        parent.addNode(light)
    }

    fun attachLightToGameEntity() {
        light.parent?.removeNode(light)
        light.onUpdate += entityTransformUpdateCb
        gameEntity.sceneComponent.sceneNode.addNode(light)
    }

    private fun updateLight(lightData: LightTypeData) {
        val lighting = sceneComponent.sceneNode.lighting

        if (!lightData.updateLight(light)) {
            lighting.removeLight(light)
            val parent = light.parent
            parent?.removeNode(light)

            lighting.removeLight(light)
            light = lightData.createLight()
            attachLight(parent)
        }
        if (light !in lighting.lights) {
            lighting.addLight(light)
        }

        light.name = gameEntity.name
        gameEntity.getComponent<ShadowMapComponent>()?.updateLight(light)
    }

    private fun attachLight(parent: Node?) {
        if (parent != null) {
            attachLightToNode(parent)
        } else {
            attachLightToGameEntity()
        }
    }
}