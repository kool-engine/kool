package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.cachedSceneComponents
import de.fabmax.kool.editor.components.SceneBackgroundComponent.ListenerComponent
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SsaoComponentData
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ao.AoPipeline

class SsaoComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SsaoComponentData> = ComponentInfo(SsaoComponentData())
) : GameEntityDataComponent<SsaoComponentData>(gameEntity, componentInfo) {

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

        aoPipeline = AoPipeline.createForward(sceneComponent.drawNode)
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
