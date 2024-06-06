package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchOnMainThread

fun SceneBackgroundComponent(gameEntity: GameEntity, color: Color, isLinear: Boolean = true): SceneBackgroundComponent {
    return SceneBackgroundComponent(
        gameEntity,
        ComponentInfo(SceneBackgroundComponentData(SceneBackgroundData.SingleColor(ColorData(color, isLinear))))
    )
}

class SceneBackgroundComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SceneBackgroundComponentData>
) : GameEntityDataComponent<SceneBackgroundComponent, SceneBackgroundComponentData>(gameEntity, componentInfo) {

    val backgroundState = mutableStateOf(data.sceneBackground).onChange {
        if (AppState.isEditMode) {
            data.sceneBackground = it
        }
        applyBackground(it)
    }

    init {
        when (val bgData = data.sceneBackground) {
            is SceneBackgroundData.Hdri -> requiredAssets += AssetReference.Hdri(bgData.hdriPath)
            else -> { }
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        // re-sync public state with componentData state
        backgroundState.set(data.sceneBackground)

        when (val bgState = backgroundState.value) {
            is SceneBackgroundData.Hdri -> {
                sceneComponent.shaderData.environmentMaps = AppAssets.loadHdri(bgState.hdriPath)
            }
            is SceneBackgroundData.SingleColor -> {
                sceneComponent.shaderData.ambientColorLinear = bgState.color.toColorLinear()
            }
        }
    }

    private fun applyBackground(bgData: SceneBackgroundData) {
        launchOnMainThread {
            requiredAssets.clear()
            when (bgData) {
                is SceneBackgroundData.Hdri -> {
                    requiredAssets += AssetReference.Hdri(bgData.hdriPath)
                    sceneComponent.shaderData.environmentMaps = AppAssets.loadHdri(bgData.hdriPath)
                    UpdateSceneBackgroundComponent.updateSceneBackground(gameEntity)
                }
                is SceneBackgroundData.SingleColor -> {
                    sceneComponent.shaderData.environmentMaps = null
                    sceneComponent.shaderData.ambientColorLinear = bgData.color.toColorLinear()
                    UpdateSceneBackgroundComponent.updateSceneBackground(gameEntity)
                }
            }
        }
    }
}

interface UpdateSceneBackgroundComponent {
    fun updateBackground(sceneBackground: SceneBackgroundComponent) {
        val sceneComponent = sceneBackground.sceneComponent
        when (val bg = sceneBackground.backgroundState.value) {
            is SceneBackgroundData.Hdri -> sceneComponent.shaderData.environmentMaps?.let { updateHdriBg(bg, it) }
            is SceneBackgroundData.SingleColor -> updateSingleColorBg(sceneComponent.shaderData.ambientColorLinear)
        }
    }

    fun updateSingleColorBg(bgColorLinear: Color)
    fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps)

    companion object {
        fun updateSceneBackground(sceneEntity: GameEntity) {
            sceneEntity.getComponent<SceneBackgroundComponent>()?.let { sceneBg ->
                sceneEntity.scene.getAllComponents<UpdateSceneBackgroundComponent>().forEach {
                    it.updateBackground(sceneBg)
                }
            }
        }
    }
}
