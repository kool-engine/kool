package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchOnMainThread

class SceneBackgroundComponent(override val componentData: SceneBackgroundComponentData) :
    EditorModelComponent(),
    EditorDataComponent<SceneBackgroundComponentData>
{

    constructor(color: Color) : this(SceneBackgroundComponentData(SceneBackgroundData.SingleColor(color)))

    private var _sceneModel: SceneModel? = null
    val sceneModel: SceneModel
        get() = requireNotNull(_sceneModel) { "SceneBackgroundComponent was not yet created" }

    val backgroundState = mutableStateOf(componentData.sceneBackground).onChange {
        if (AppState.isEditMode) {
            componentData.sceneBackground = it
        }
        applyBackground(it)
    }

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        _sceneModel = requireNotNull(nodeModel as? SceneModel) {
            "SceneBackgroundComponent is only allowed in scenes (parent node is of type ${nodeModel::class})"
        }

        // re-sync public state with componentData state
        backgroundState.set(componentData.sceneBackground)

        when (val bgState = backgroundState.value) {
            is SceneBackgroundData.Hdri -> {
                sceneModel.shaderData.environmentMaps = AppAssets.loadHdriEnvironment(sceneModel.drawNode, bgState.hdriPath)
            }
            is SceneBackgroundData.SingleColor -> {
                sceneModel.shaderData.ambientColorLinear = bgState.color.toColor().toLinear()
            }
        }
    }

    private fun applyBackground(bgData: SceneBackgroundData) {
        val scene = _sceneModel ?: return
        launchOnMainThread {
            when (bgData) {
                is SceneBackgroundData.Hdri -> {
                    scene.shaderData.environmentMaps = AppAssets.loadHdriEnvironment(scene.drawNode, bgData.hdriPath)
                    UpdateSceneBackgroundComponent.updateSceneBackground(scene)
                }
                is SceneBackgroundData.SingleColor -> {
                    scene.shaderData.environmentMaps = null
                    scene.shaderData.ambientColorLinear = bgData.color.toColor().toLinear()
                    UpdateSceneBackgroundComponent.updateSceneBackground(scene)
                }
            }
        }
    }
}

interface UpdateSceneBackgroundComponent {
    fun updateBackground(sceneBackground: SceneBackgroundComponent) {
        when (val bg = sceneBackground.backgroundState.value) {
            is SceneBackgroundData.Hdri -> sceneBackground.sceneModel.shaderData.environmentMaps?.let { updateHdriBg(bg, it) }
            is SceneBackgroundData.SingleColor -> updateSingleColorBg(sceneBackground.sceneModel.shaderData.ambientColorLinear)
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
