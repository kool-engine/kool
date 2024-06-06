package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.ShadowMapComponentData
import de.fabmax.kool.editor.data.ShadowMapInfo
import de.fabmax.kool.editor.data.ShadowMapTypeData
import de.fabmax.kool.scene.Light
import de.fabmax.kool.util.*

class ShadowMapComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<ShadowMapComponentData> = ComponentInfo(
        ShadowMapComponentData(ShadowMapTypeData.Single(ShadowMapInfo()))
    )
) : GameEntityDataComponent<ShadowMapComponent, ShadowMapComponentData>(gameEntity, componentInfo) {

    val shadowMapType: ShadowMapTypeData get() = data.shadowMap
    val clipNear: Float get() = data.clipNear
    val clipFar: Float get() = data.clipFar

    var shadowMap: ShadowMap? = null
        private set

    init {
        dependsOn(DiscreteLightComponent::class)
    }

    override fun onDataChanged(oldData: ShadowMapComponentData, newData: ShadowMapComponentData) {
        updateShadowMap(newData)
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        updateShadowMap(data)
    }

    override fun destroyComponent() {
        disposeShadowMap()
        UpdateShadowMapsComponent.updateShadowMaps(sceneEntity)
        super.destroyComponent()
    }

    fun updateLight(light: Light) {
        val current = shadowMap?.light
        if (current != null && current::class == light::class) {
            shadowMap?.light = light
        } else {
            updateShadowMap(data)
        }
    }

    private fun updateShadowMap(data: ShadowMapComponentData) {
        logD { "Update shadow map: ${data.shadowMap::class.simpleName}, near: $clipNear, far: $clipFar" }

        val light = gameEntity.getComponent<DiscreteLightComponent>()?.drawNode
        if (light == null) {
            logE { "Unable to get DiscreteLightComponent of sceneNode ${gameEntity.name}" }
            return
        }
        if (light is Light.Point) {
            logE { "Point light shadow maps are not yet supported" }
            return
        }

        // dispose old shadow map
        disposeShadowMap()

        // create new shadow map
        shadowMap = when (data.shadowMap) {
            is ShadowMapTypeData.Single -> {
                SimpleShadowMap(sceneComponent.drawNode, light, mapSize = data.shadowMap.mapInfo.mapSize).apply {
                    this.clipNear = data.clipNear
                    this.clipFar = data.clipFar
                }
            }
            is ShadowMapTypeData.Cascaded -> {
                CascadedShadowMap(
                    sceneComponent.drawNode,
                    light,
                    data.clipFar,
                    data.shadowMap.mapInfos.size,
                    mapSizes = data.shadowMap.mapInfos.map { it.mapSize }
                ).apply {
                    data.shadowMap.mapInfos.forEachIndexed { i, info ->
                        mapRanges[i].near = info.rangeNear
                        mapRanges[i].far = info.rangeFar
                    }
                }
            }
        }.also {
            scene.shaderData.shadowMaps += it
            UpdateShadowMapsComponent.updateShadowMaps(sceneEntity)
        }
    }

    private fun disposeShadowMap() {
        val scene = sceneComponent.drawNode
        shadowMap?.let {
            gameEntity.scene.shaderData.shadowMaps -= it
            when (it) {
                is SimpleShadowMap -> {
                    scene.removeOffscreenPass(it)
                    it.release()
                }
                is CascadedShadowMap -> {
                    it.subMaps.forEach { pass ->
                        scene.removeOffscreenPass(pass)
                        pass.release()
                    }
                }
            }
        }
    }
}

interface UpdateShadowMapsComponent {
    fun updateShadowMaps(shadowMaps: List<ShadowMap>)

    companion object {
        fun updateShadowMaps(sceneEntity: GameEntity) {
            sceneEntity.scene.getAllComponents<UpdateShadowMapsComponent>().forEach {
                it.updateShadowMaps(sceneEntity.scene.shaderData.shadowMaps)
            }
        }
    }
}
