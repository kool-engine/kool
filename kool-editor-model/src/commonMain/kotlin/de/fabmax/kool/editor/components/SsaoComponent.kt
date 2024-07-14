package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedSceneComponents
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SsaoComponentData
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ao.AoPipeline
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.PerspectiveCamera

class SsaoComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SsaoComponentData> = ComponentInfo(SsaoComponentData())
) : GameEntityDataComponent<SsaoComponentData>(gameEntity, componentInfo), CameraAwareComponent {

    var aoPipeline: AoPipeline? = null
        private set
    val ssaoMap: Texture2d? get() = aoPipeline?.aoMap

    private val listeners by cachedSceneComponents<ListenerComponent>()

    init {
        dependsOn(SceneComponent::class)
    }

    override fun onDataChanged(oldData: SsaoComponentData, newData: SsaoComponentData) {
        applySettings(newData)
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        aoPipeline = AoPipeline.createForward(sceneComponent.sceneNode)
        scene.shaderData.ssaoMap = aoPipeline?.aoMap
        applySettings(data)

        listeners.forEach { it.onSsaoChanged(this) }
    }

    override fun destroyComponent() {
        aoPipeline?.release()
        aoPipeline = null
        scene.shaderData.ssaoMap = null

        listeners.forEach { it.onSsaoChanged(this) }

        super.destroyComponent()
    }

    override fun updateSceneCamera(camera: Camera) {
        val fwdPipeline = aoPipeline as? AoPipeline.ForwardAoPipeline ?: return
        val perspectiveCam = camera as? PerspectiveCamera ?: return
        fwdPipeline.proxyCamera.trackedCam = perspectiveCam
    }

    private fun applySettings(ssaoSettings: SsaoComponentData) {
        val radiusSign = if (ssaoSettings.isRelativeRadius) -1f else 1f
        aoPipeline?.apply {
            mapSize = ssaoSettings.mapSize
            kernelSz = ssaoSettings.samples
            radius = ssaoSettings.radius * radiusSign
            strength = ssaoSettings.strength
            power = ssaoSettings.power
        }
    }

    fun interface ListenerComponent {
        fun onSsaoChanged(component: SsaoComponent)
    }
}
