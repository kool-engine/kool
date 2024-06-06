package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.sceneComponent
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.SsaoComponentData
import de.fabmax.kool.editor.data.SsaoSettings
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ao.AoPipeline

class SsaoComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<SsaoComponentData> = ComponentInfo(SsaoComponentData())
) : GameEntityDataComponent<SsaoComponent, SsaoComponentData>(gameEntity, componentInfo) {

    val ssaoState = mutableStateOf(data.settings).onChange {
        if (AppState.isEditMode) {
            data.settings = it
        }
        applySettings(it)
    }

    var aoPipeline: AoPipeline? = null

    init {
        dependsOn(SceneComponent::class)
    }

    override suspend fun applyComponent() {
        super.applyComponent()

        aoPipeline = AoPipeline.createForward(sceneComponent.drawNode)
        sceneComponent.shaderData.ssaoMap = aoPipeline?.aoMap
        UpdateSsaoComponent.updateSceneSsao(gameEntity)

        // re-sync public state with componentData state
        ssaoState.set(data.settings)
        applySettings(ssaoState.value)
    }

    override fun destroyComponent() {
        aoPipeline?.release()
        sceneComponent.shaderData.ssaoMap = null
        UpdateSsaoComponent.updateSceneSsao(gameEntity)
        super.destroyComponent()
    }

    private fun applySettings(ssaoSettings: SsaoSettings) {
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
                it.updateSsao(sceneEntity.sceneComponent.shaderData.ssaoMap)
            }
        }
    }
}
