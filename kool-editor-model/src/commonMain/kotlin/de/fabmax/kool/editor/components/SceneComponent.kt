package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.data.SceneComponentData
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.logW

class SceneComponent(gameEntity: GameEntity, componentData: SceneComponentData) :
    GameEntityDataComponent<SceneComponentData>(gameEntity, componentData)
{
    val scene: Scene get() = gameEntity.drawNode as Scene

    val shaderData = SceneShaderData()

    val maxNumLightsState: MutableStateValue<Int>
    val cameraState: MutableStateValue<CameraComponent?>

    @Deprecated("remove this?")
    val sceneBackground: SceneBackgroundComponent by lazy {
        gameEntity.getOrPutComponent<SceneBackgroundComponent> { SceneBackgroundComponent(gameEntity, MdColor.GREY toneLin 900) }
    }
    private val backgroundUpdater: BackgroundUpdater by lazy {
        gameEntity.getOrPutComponent { BackgroundUpdater() }
    }

    init {
        componentOrder = COMPONENT_ORDER_EARLY

        maxNumLightsState = mutableStateOf(componentData.maxNumLights).onChange {
            shaderData.maxNumberOfLights = it
            if (AppState.isEditMode) {
                componentData.maxNumLights = it
            }
            scene.lighting.maxNumberOfLights = it
            gameEntity.scene.getAllComponents<UpdateMaxNumLightsComponent>().forEach { comp ->
                comp.updateMaxNumLightsComponent(it)
            }
        }
        cameraState = mutableStateOf<CameraComponent?>(null).onChange {
            if (AppState.isEditMode) {
                componentData.cameraEntityId = it?.gameEntity?.id ?: EntityId(-1L)
            } else {
                // only set scene cam if not in edit mode. In edit mode, editor camera is used instead
                it?.typedDrawNode?.let { cam -> scene.camera = cam }
            }
            gameEntity.scene.getAllComponents<UpdateSceneCameraComponent>().forEach { comp ->
                comp.updateSceneCameraComponent(it?.typedDrawNode)
            }
        }

        shaderData.maxNumberOfLights = maxNumLightsState.value
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        val cam = gameEntity.scene.sceneEntities[componentData.cameraEntityId]?.getComponent<CameraComponent>()
        if (cam != null) {
            cameraState.set(cam)
            scene.camera = cam.typedDrawNode
        } else {
            logW { "Scene ${gameEntity.name} has no camera attached" }
        }
    }

    private inner class BackgroundUpdater :
        GameEntityComponent(gameEntity),
        UpdateSceneBackgroundComponent
    {
        var skybox: Skybox.Cube? = null

        override suspend fun applyComponent() {
            super.applyComponent()
            updateBackground(sceneBackground)
        }

        override fun updateSingleColorBg(bgColorLinear: Color) {
            scene.clearColor = bgColorLinear.toSrgb()
            skybox?.isVisible = false
        }

        override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
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
    }

    class SceneShaderData {
        var maxNumberOfLights: Int = 4
        var environmentMaps: EnvironmentMaps? = null
        var ambientColorLinear: Color = Color.BLACK
        val shadowMaps = mutableListOf<ShadowMap>()
        var ssaoMap: Texture2d? = null
    }
}

interface UpdateMaxNumLightsComponent {
    fun updateMaxNumLightsComponent(newMaxNumLights: Int)
}

interface UpdateSceneCameraComponent {
    fun updateSceneCameraComponent(newCamera: Camera?)
}
