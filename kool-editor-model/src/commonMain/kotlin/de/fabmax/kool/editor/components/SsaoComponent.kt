package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SsaoComponentData
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ao.AoPipeline

class SsaoComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SsaoComponentData> = ComponentInfo(SsaoComponentData())
) : GameEntityDataComponent<SsaoComponent, SsaoComponentData>(gameEntity, componentInfo) {

    var aoPipeline: AoPipeline? = null
        private set

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
        UpdateSsaoComponent.updateSceneSsao(gameEntity)
        applySettings(data)
    }

    override fun destroyComponent() {
        aoPipeline?.release()
        scene.shaderData.ssaoMap = null
        UpdateSsaoComponent.updateSceneSsao(gameEntity)
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
}

interface UpdateSsaoComponent {
    fun updateSsao(ssaoMap: Texture2d?)

    companion object {
        fun updateSceneSsao(sceneEntity: GameEntity) {
            sceneEntity.scene.getAllComponents<UpdateSsaoComponent>().forEach {
                it.updateSsao(sceneEntity.scene.shaderData.ssaoMap)
            }
        }
    }
}
