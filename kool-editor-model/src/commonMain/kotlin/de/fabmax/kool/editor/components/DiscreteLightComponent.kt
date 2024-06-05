package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.DiscreteLightComponentData
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color

class DiscreteLightComponent(
    gameEntity: GameEntity,
    componentData: DiscreteLightComponentData = DiscreteLightComponentData(LightTypeData.Directional(ColorData(Color.WHITE), 3f))
) :
    GameEntityDataComponent<DiscreteLightComponentData>(gameEntity, componentData),
    DrawNodeComponent<Light>
{
    val lightState = mutableStateOf(componentData.light).onChange {
        if (AppState.isEditMode) {
            componentData.light = it
        }
        updateLight(it, false)
    }

    override var typedDrawNode: Light = componentData.light.createLight()
        private set

    override suspend fun applyComponent() {
        super.applyComponent()
        lightState.set(componentData.light)
        updateLight(componentData.light, true)
    }

    override fun destroyComponent() {
        val scene = sceneEntity.drawNode as Scene
        scene.lighting.removeLight(typedDrawNode)
        super.destroyComponent()
    }

    private fun updateLight(lightData: LightTypeData, forceReplaceNode: Boolean) {
        val updateLight = lightData.updateOrCreateLight(typedDrawNode)

        if (forceReplaceNode || updateLight != typedDrawNode) {
            val scene = sceneEntity.drawNode as Scene
            val lighting = scene.lighting
            lighting.removeLight(typedDrawNode)

            typedDrawNode = updateLight
            gameEntity.replaceDrawNode(typedDrawNode)
            lighting.addLight(typedDrawNode)
        }

        gameEntity.getComponent<ShadowMapComponent>()?.updateLight(typedDrawNode)
    }
}