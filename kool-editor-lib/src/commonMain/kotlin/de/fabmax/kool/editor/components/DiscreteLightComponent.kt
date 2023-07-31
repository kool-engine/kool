package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.DiscreteLightComponentData
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Light
import de.fabmax.kool.util.Color

class DiscreteLightComponent(nodeModel: SceneNodeModel, override val componentData: DiscreteLightComponentData) :
    SceneNodeComponent(nodeModel),
    EditorDataComponent<DiscreteLightComponentData>,
    ContentComponent
{
    val lightState = mutableStateOf(componentData.light).onChange {
        if (AppState.isEditMode) {
            componentData.light = it
        }
        updateLight(it, false)
    }

    var light: Light = componentData.light.createLight()
        private set

    override val contentNode: Light
        get() = light

    constructor(nodeModel: SceneNodeModel): this(nodeModel, DiscreteLightComponentData(LightTypeData.Directional(ColorData(Color.WHITE), 3f)))

    override suspend fun createComponent() {
        super.createComponent()
        lightState.set(componentData.light)
        updateLight(componentData.light, true)
    }

    override fun destroyComponent() {
        sceneModel.drawNode.lighting.removeLight(light)
        super.destroyComponent()
    }

    private fun updateLight(lightData: LightTypeData, forceReplaceNode: Boolean) {
        val updateLight = lightData.updateOrCreateLight(light)

        if (forceReplaceNode || updateLight != light) {
            val lighting = sceneModel.drawNode.lighting
            lighting.removeLight(light)

            light = updateLight
            nodeModel.setDrawNode(light)
            lighting.addLight(light)
        }

        nodeModel.getComponent<ShadowMapComponent>()?.updateLight(light)
    }
}