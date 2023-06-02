package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color

class SceneBackgroundComponent(override val componentData: SceneBackgroundComponentData)
    : EditorDataComponent<SceneBackgroundComponentData> {

    constructor(color: Color) : this(SceneBackgroundComponentData(SceneBackgroundData.SingleColor(color)))

    val backgroundState = mutableStateOf(componentData.sceneBackground).onChange { componentData.sceneBackground = it }

    var loadedEnvironmentMaps: EnvironmentMaps? = null

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        val scene = requireNotNull(nodeModel as? SceneModel) {
            "SceneBackgroundComponent is only allowed in scenes (parent node is of type ${nodeModel::class})"
        }
        val hdriBg = backgroundState.value as? SceneBackgroundData.Hdri ?: return
        loadedEnvironmentMaps = AppAssets.loadHdriEnvironment(scene.node, hdriBg.hdriPath)
    }

    override suspend fun initComponent(nodeModel: EditorNodeModel) { }
}

interface UpdateSceneBackgroundComponent : EditorModelComponent {
    fun updateBackground(sceneBackground: SceneBackgroundComponent) {
        when (val bg = sceneBackground.backgroundState.value) {
            is SceneBackgroundData.Hdri -> sceneBackground.loadedEnvironmentMaps?.let { updateHdriBg(bg, it) }
            is SceneBackgroundData.SingleColor -> updateSingleColorBg(bg.color.toColor())
        }
    }

    fun updateSingleColorBg(bgColorSrgb: Color)
    fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps)

    companion object {
        fun updateSceneBackground(sceneModel: SceneModel) {
            sceneModel.project.getComponentsInScene<UpdateSceneBackgroundComponent>(sceneModel).forEach {
                it.updateBackground(sceneModel.sceneBackground)
            }
        }
    }
}
