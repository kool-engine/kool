package de.fabmax.kool.editor.components

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.SsaoComponentData
import de.fabmax.kool.editor.data.SsaoSettings
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ao.AoPipeline

class SsaoComponent(override val nodeModel: SceneModel, override val componentData: SsaoComponentData) :
    EditorModelComponent(nodeModel),
    EditorDataComponent<SsaoComponentData>
{

    constructor(nodeModel: SceneModel): this(nodeModel, SsaoComponentData())

    val ssaoState = mutableStateOf(componentData.settings).onChange {
        if (AppState.isEditMode) {
            componentData.settings = it
        }
        applySettings(it)
    }

    var aoPipeline: AoPipeline? = null

    override suspend fun createComponent() {
        super.createComponent()
        aoPipeline = AoPipeline.createForward(nodeModel.drawNode)
        nodeModel.shaderData.ssaoMap = aoPipeline?.aoMap
        UpdateSsaoComponent.updateSceneSsao(nodeModel)

        // re-sync public state with componentData state
        ssaoState.set(componentData.settings)
        applySettings(ssaoState.value)
    }

    override fun destroyComponent() {
        aoPipeline?.removeAndDispose(KoolSystem.requireContext())
        nodeModel.shaderData.ssaoMap = null
        UpdateSsaoComponent.updateSceneSsao(nodeModel)
        super.destroyComponent()
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
