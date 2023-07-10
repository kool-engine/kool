package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.SsaoComponentData
import de.fabmax.kool.editor.data.SsaoSettings
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ao.AoPipeline

class SsaoComponent(override val componentData: SsaoComponentData) :
    EditorModelComponent(),
    EditorDataComponent<SsaoComponentData>
{

    constructor(): this(SsaoComponentData())

    val ssaoState = mutableStateOf(componentData.settings).onChange {
        if (AppState.isEditMode) {
            componentData.settings = it
        }
        applySettings(it)
    }

    private var _sceneModel: SceneModel? = null
    val sceneModel: SceneModel
        get() = requireNotNull(_sceneModel) { "SceneBackgroundComponent was not yet created" }

    private var aoPipeline: AoPipeline? = null

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        _sceneModel = requireNotNull(nodeModel as? SceneModel) {
            "SceneBackgroundComponent is only allowed in scenes (parent node is of type ${nodeModel::class})"
        }

        aoPipeline = AoPipeline.createForward(sceneModel.drawNode)
        sceneModel.shaderData.ssaoMap = aoPipeline?.aoMap
        UpdateSsaoComponent.updateSceneSsao(sceneModel)

        // re-sync public state with componentData state
        ssaoState.set(componentData.settings)
        applySettings(ssaoState.value)
    }

    private fun applySettings(ssaoSettings: SsaoSettings) {
        val radiusSign = if (ssaoSettings.isRelativeRadius) -1f else 1f
        aoPipeline?.apply {
            mapSize = ssaoSettings.mapSize
            kernelSz = ssaoSettings.samples
            radius = ssaoSettings.radius * radiusSign
            strength = ssaoSettings.strength
            power = ssaoSettings.power
        }
    }
}

interface UpdateSsaoComponent {
    fun updateSsao(ssaoMap: Texture2d?)

    companion object {
        fun updateSceneSsao(sceneModel: SceneModel) {
            sceneModel.project.getComponentsInScene<UpdateSsaoComponent>(sceneModel).forEach {
                it.updateSsao(sceneModel.shaderData.ssaoMap)
            }
        }
    }
}
