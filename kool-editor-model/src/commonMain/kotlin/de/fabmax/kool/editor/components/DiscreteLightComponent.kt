package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.DiscreteLightComponentData
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color

class DiscreteLightComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<DiscreteLightComponentData> = ComponentInfo(
        DiscreteLightComponentData(LightTypeData.Directional(ColorData(Color.WHITE), 3f))
    )
) :
    GameEntityDataComponent<DiscreteLightComponent, DiscreteLightComponentData>(gameEntity, componentInfo),
    DrawNodeComponent
{
    val lightState = mutableStateOf(data.light).onChange {
        if (AppState.isEditMode) {
            data.light = it
        }
        updateLight(it, false)
    }

    override var drawNode: Light = data.light.createLight()
        private set

    override suspend fun applyComponent() {
        super.applyComponent()
        lightState.set(data.light)
        updateLight(data.light, true)
    }

    override fun destroyComponent() {
        val scene = sceneEntity.drawNode as Scene
        scene.lighting.removeLight(drawNode)
        super.destroyComponent()
    }

    private fun updateLight(lightData: LightTypeData, forceReplaceNode: Boolean) {
        val updateLight = lightData.updateOrCreateLight(drawNode)

        if (forceReplaceNode || updateLight != drawNode) {
            val scene = sceneEntity.drawNode as Scene
            val lighting = scene.lighting
            lighting.removeLight(drawNode)

            drawNode = updateLight
            gameEntity.replaceDrawNode(drawNode)
            lighting.addLight(drawNode)
        }

        gameEntity.getComponent<ShadowMapComponent>()?.updateLight(drawNode)
    }
}