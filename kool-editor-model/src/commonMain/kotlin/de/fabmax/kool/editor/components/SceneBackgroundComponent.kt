package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Skybox
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
) : GameEntityDataComponent<SceneBackgroundComponentData>(gameEntity, componentInfo) {

    private val listeners by cachedSceneComponents<ListenerComponent>()

    var skybox: Skybox.Cube? = null
        private set

    init {
        when (val bgData = data.sceneBackground) {
            is SceneBackgroundData.Hdri -> requiredAssets += AssetReference.Hdri(bgData.hdriPath)
            else -> { }
        }
    }

    override fun onDataChanged(oldData: SceneBackgroundComponentData, newData: SceneBackgroundComponentData) {
        val prevHdriPath = (oldData.sceneBackground as? SceneBackgroundData.Hdri)?.hdriPath
        applyBackground(newData, prevHdriPath)
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        when (val bgState = data.sceneBackground) {
            is SceneBackgroundData.Hdri -> {
                scene.shaderData.environmentMaps = AppAssets.loadHdri(bgState.hdriPath)
            }
            is SceneBackgroundData.SingleColor -> {
                scene.shaderData.ambientColorLinear = bgState.color.toColorLinear()
            }
        }
        applyBackground(data, null)
    }

    private fun applyBackground(data: SceneBackgroundComponentData, prevHdriPath: String?) {
        launchOnMainThread {
            requiredAssets.clear()
            when (val bgData = data.sceneBackground) {
                is SceneBackgroundData.Hdri -> {
                    if (bgData.hdriPath != prevHdriPath) {
                        requiredAssets += AssetReference.Hdri(bgData.hdriPath)
                        scene.shaderData.environmentMaps = AppAssets.loadHdri(bgData.hdriPath)
                    }
                    updateSkybox(bgData, scene.shaderData.environmentMaps!!)
                }
                is SceneBackgroundData.SingleColor -> {
                    scene.shaderData.environmentMaps = null
                    scene.shaderData.ambientColorLinear = bgData.color.toColorLinear()
                    scene.scene.clearColor = bgData.color.toColorSrgb()
                    skybox?.isVisible = false
                }
            }
            listeners.forEach { it.onBackgroundChanged(this, data) }
        }
    }

    private fun updateSkybox(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
        val scene = sceneComponent.drawNode
        scene.clearColor = null
        val skybox = this.skybox ?: Skybox.Cube()

        skybox.name = "Skybox"
        skybox.isVisible = true
        skybox.skyboxShader.setSingleSky(ibl.reflectionMap)
        skybox.skyboxShader.lod = hdriBg.skyLod
        if (this.skybox == null) {
            this.skybox = skybox
        }
        scene.removeNode(skybox)
        scene.addNode(skybox, 0)
    }

    interface ListenerComponent {
        fun onBackgroundChanged(component: SceneBackgroundComponent, backgroundData: SceneBackgroundComponentData) {
            val scene = component.scene
            when (val bg = component.data.sceneBackground) {
                is SceneBackgroundData.Hdri -> scene.shaderData.environmentMaps?.let { updateHdriBg(bg, it) }
                is SceneBackgroundData.SingleColor -> updateSingleColorBg(scene.shaderData.ambientColorLinear)
            }
        }

        fun updateSingleColorBg(bgColorLinear: Color)
        fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps)
    }
}
