package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SceneComponentData
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.logW

class SceneComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SceneComponentData>
) :
    GameEntityDataComponent<SceneComponent, SceneComponentData>(gameEntity, componentInfo),
    DrawNodeComponent
{
    override val drawNode: Scene = Scene(gameEntity.name).apply { tryEnableInfiniteDepth() }

    val shaderData = SceneShaderData()

    val maxNumLights: Int get() = data.maxNumLights
    val cameraComponent: CameraComponent? get() = gameEntity.scene.sceneEntities[data.cameraEntityId]?.getComponent()

    init {
        componentOrder = COMPONENT_ORDER_EARLY
        shaderData.maxNumberOfLights = data.maxNumLights
    }

    override fun onDataChanged(oldData: SceneComponentData, newData: SceneComponentData) {
        if (oldData.maxNumLights != newData.maxNumLights) {
            gameEntity.scene.getAllComponents<UpdateMaxNumLightsComponent>().forEach { comp ->
                comp.updateMaxNumLightsComponent(newData.maxNumLights)
            }
        }
        if (oldData.cameraEntityId != newData.cameraEntityId) {
            val newCam: CameraComponent? = gameEntity.scene.sceneEntities[data.cameraEntityId]?.getComponent()
            gameEntity.scene.getAllComponents<UpdateSceneCameraComponent>().forEach { comp ->
                comp.updateSceneCameraComponent(newCam?.drawNode)
            }
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        val cam = cameraComponent
        if (cam != null) {
            drawNode.camera = cam.drawNode
        } else {
            logW { "Scene ${gameEntity.name} has no camera attached" }
        }
    }

//    private inner class BackgroundUpdater :
//        GameEntityComponent(gameEntity),
//        UpdateSceneBackgroundComponent
//    {
//        var skybox: Skybox.Cube? = null
//
//        override suspend fun applyComponent() {
//            super.applyComponent()
//            updateBackground(sceneBackground)
//        }
//
//        override fun updateSingleColorBg(bgColorLinear: Color) {
//            drawNode.clearColor = bgColorLinear.toSrgb()
//            skybox?.isVisible = false
//        }
//
//        override fun updateHdriBg(hdriBg: SceneBackgroundData.Hdri, ibl: EnvironmentMaps) {
//            drawNode.clearColor = null
//            val skybox = this.skybox ?: Skybox.Cube()
//            skybox.name = "Skybox"
//            skybox.isVisible = true
//            skybox.skyboxShader.setSingleSky(ibl.reflectionMap)
//            skybox.skyboxShader.lod = hdriBg.skyLod
//            if (this.skybox == null) {
//                this.skybox = skybox
//            }
//            drawNode.removeNode(skybox)
//            drawNode.addNode(skybox, 0)
//        }
//    }

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
