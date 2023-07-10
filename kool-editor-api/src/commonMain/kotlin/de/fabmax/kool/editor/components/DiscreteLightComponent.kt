package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.DiscreteLightComponentData
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color

class DiscreteLightComponent(override val componentData: DiscreteLightComponentData) :
    SceneNodeComponent(),
    EditorDataComponent<DiscreteLightComponentData>,
    ContentComponent
{
    val lightState = mutableStateOf(componentData.light).onChange {
        if (AppState.isEditMode) {
            componentData.light = it
        }
        updateLight(it)
    }

    var light: Light = Light.Directional()
        private set

    override val contentNode: Node
        get() = light

    constructor(): this(DiscreteLightComponentData(LightTypeData.Directional(ColorData(Color.WHITE), 3f)))

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        super.createComponent(nodeModel)
        lightState.set(componentData.light)
        updateLight(componentData.light)
    }

    override fun onNodeRemoved(nodeModel: EditorNodeModel) {
        sceneModel.drawNode.lighting.removeLight(light)
    }

    override fun onNodeAdded(nodeModel: EditorNodeModel) {
        updateLight(componentData.light)
    }

    private fun updateLight(lightData: LightTypeData) {
        val lighting = sceneModel.drawNode.lighting
        val lightIdx = lighting.lights.indexOf(light)

        light = when (lightData) {
            is LightTypeData.Directional -> if (light is Light.Directional) light else Light.Directional()
            is LightTypeData.Point -> if (light is Light.Point) light else Light.Point()
            is LightTypeData.Spot -> {
                val spot = light as? Light.Spot ?: Light.Spot()
                spot.apply {
                    spotAngle = lightData.spotAngle
                    coreRatio = lightData.coreRatio
                }
            }
        }
        light.lightIndex = lightIdx
        light.setColor(lightData.color.toColorLinear(), lightData.intensity)
        nodeModel.setContentNode(light)

        if (lightIdx in lighting.lights.indices) {
            lighting.lights[lightIdx] = light
        } else {
            lighting.addLight(light)
        }
    }
}