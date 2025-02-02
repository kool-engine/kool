package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.ClearColorDontCare
import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
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
) :
    GameEntityDataComponent<SceneBackgroundComponentData>(gameEntity, componentInfo),
    EditorScene.SceneShaderDataListener
{

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
                scene.shaderData.environmentMap = AppAssets.loadHdriOrNull(bgState.hdriPath)
            }
            is SceneBackgroundData.SingleColor -> {
                scene.shaderData.ambientColorLinear = bgState.color.toColorLinear()
            }
        }
        applyBackground(data, null)
    }

    override fun onSceneShaderDataChanged(scene: EditorScene, sceneShaderData: SceneShaderData) {
        val skybox = this.skybox ?: return
        val hdriBackground = data.sceneBackground as? SceneBackgroundData.Hdri ?: return

        val colorConv = skybox.skyboxShader.colorSpaceConversion
        if (colorConv is ColorSpaceConversion.LinearToSrgbHdr && colorConv.toneMapping != sceneShaderData.toneMapping) {
            applyBackground(data, hdriBackground.hdriPath)
        }
    }

    private fun applyBackground(data: SceneBackgroundComponentData, prevHdriPath: String?) {
        launchOnMainThread {
            requiredAssets.clear()
            when (val bgData = data.sceneBackground) {
                is SceneBackgroundData.Hdri -> {
                    if (bgData.hdriPath != prevHdriPath) {
                        requiredAssets += AssetReference.Hdri(bgData.hdriPath)
                        scene.shaderData.environmentMap = AppAssets.loadHdriOrNull(bgData.hdriPath)
                    }
                    updateSkybox(bgData, scene.shaderData)
                }
                is SceneBackgroundData.SingleColor -> {
                    scene.shaderData.environmentMap = null
                    scene.shaderData.ambientColorLinear = bgData.color.toColorLinear()
                    scene.scene.clearColor = ClearColorFill(bgData.color.toColorSrgb())
                    skybox?.isVisible = false
                }
            }
            listeners.forEach { it.onBackgroundChanged(this, data) }
        }
    }

    private fun updateSkybox(hdriBg: SceneBackgroundData.Hdri, sceneShaderData: SceneShaderData) {
        val scene = sceneComponent.sceneNode
        scene.clearColor = ClearColorDontCare
        val ibl = sceneShaderData.environmentMap!!

        skybox?.let { skybox ->
            val colorConv = skybox.skyboxShader.colorSpaceConversion
            if (colorConv is ColorSpaceConversion.LinearToSrgbHdr && colorConv.toneMapping != sceneShaderData.toneMapping) {
                scene.removeNode(skybox)
                skybox.release()
                this.skybox = null
            }
        }

        val skybox = this.skybox ?: Skybox.Cube(colorSpaceConversion = ColorSpaceConversion.LinearToSrgbHdr(sceneShaderData.toneMapping))
        skybox.name = "Skybox"
        skybox.isVisible = true
        skybox.isPickable = false
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
                is SceneBackgroundData.Hdri -> scene.shaderData.environmentMap?.let { updateHdriBg(bg, it) }
                is SceneBackgroundData.SingleColor -> updateSingleColorBg(scene.shaderData.ambientColorLinear)
            }
        }

        fun updateSingleColorBg(bgColorLinear: Color)
        fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMap)
    }
}
