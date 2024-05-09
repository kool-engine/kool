package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.RequiredAsset
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchOnMainThread

class SceneBackgroundComponent(override val nodeModel: SceneModel, override val componentData: SceneBackgroundComponentData) :
    EditorModelComponent(nodeModel),
    EditorDataComponent<SceneBackgroundComponentData>
{

    constructor(nodeModel: SceneModel, color: Color, isLinear: Boolean = true) : this(
        nodeModel, SceneBackgroundComponentData(SceneBackgroundData.SingleColor(ColorData(color, isLinear)))
    )

    val backgroundState = mutableStateOf(componentData.sceneBackground).onChange {
        if (AppState.isEditMode) {
            componentData.sceneBackground = it
        }
        applyBackground(it)
    }

    init {
        when (val bgData = componentData.sceneBackground) {
            is SceneBackgroundData.Hdri -> requiredAssets += RequiredAsset.HdriEnvironment(bgData.hdriPath)
            else -> { }
        }
    }

    override suspend fun createComponent() {
        super.createComponent()

        // re-sync public state with componentData state
        backgroundState.set(componentData.sceneBackground)

        when (val bgState = backgroundState.value) {
            is SceneBackgroundData.Hdri -> {
                nodeModel.shaderData.environmentMaps = AppAssets.loadHdriEnvironment(bgState.hdriPath)
            }
            is SceneBackgroundData.SingleColor -> {
                nodeModel.shaderData.ambientColorLinear = bgState.color.toColorLinear()
            }
        }
    }

    private fun applyBackground(bgData: SceneBackgroundData) {
        launchOnMainThread {
            requiredAssets.clear()
            when (bgData) {
                is SceneBackgroundData.Hdri -> {
                    requiredAssets += RequiredAsset.HdriEnvironment(bgData.hdriPath)
                    nodeModel.shaderData.environmentMaps = AppAssets.loadHdriEnvironment(bgData.hdriPath)
                    UpdateSceneBackgroundComponent.updateSceneBackground(nodeModel)
                }
                is SceneBackgroundData.SingleColor -> {
                    nodeModel.shaderData.environmentMaps = null
                    nodeModel.shaderData.ambientColorLinear = bgData.color.toColorLinear()
                    UpdateSceneBackgroundComponent.updateSceneBackground(nodeModel)
                }
            }
        }
    }
}

interface UpdateSceneBackgroundComponent {
    fun updateBackground(sceneBackground: SceneBackgroundComponent) {
        when (val bg = sceneBackground.backgroundState.value) {
            is SceneBackgroundData.Hdri -> sceneBackground.nodeModel.shaderData.environmentMaps?.let { updateHdriBg(bg, it) }
            is SceneBackgroundData.SingleColor -> updateSingleColorBg(sceneBackground.nodeModel.shaderData.ambientColorLinear)
        }
    }

    fun updateSingleColorBg(bgColorLinear: Color)
    fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps)

    companion object {
        fun updateSceneBackground(sceneModel: SceneModel) {
            sceneModel.project.getComponentsInScene<UpdateSceneBackgroundComponent>(sceneModel).forEach {
                it.updateBackground(sceneModel.sceneBackground)
            }
        }
    }
}
