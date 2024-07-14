package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.isPreparedOrRunning
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.ShadowMapComponentData
import de.fabmax.kool.editor.data.ShadowMapInfo
import de.fabmax.kool.editor.data.ShadowMapTypeData
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Light
import de.fabmax.kool.util.*

class ShadowMapComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<ShadowMapComponentData> = ComponentInfo(
        ShadowMapComponentData(ShadowMapTypeData.Single(ShadowMapInfo()))
    )
) : GameEntityDataComponent<ShadowMapComponentData>(gameEntity, componentInfo), CameraAwareComponent {

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
        disposeShadowMap(null)
        super.destroyComponent()
    }

    fun updateLight(light: Light) {
        if (isPreparedOrRunning) {
            val current = shadowMap?.light
            if (current != null && current::class == light::class) {
                shadowMap?.light = light
            } else {
                updateShadowMap(data)
            }
        }
    }

    override fun updateSceneCamera(camera: Camera) {
        when (val shadow = shadowMap) {
            is CascadedShadowMap -> shadow.subMaps.forEach { it.sceneCam = camera }
            is SimpleShadowMap -> shadow.sceneCam = camera
            else -> { }
        }
    }

    private fun updateShadowMap(data: ShadowMapComponentData) {
        logD { "Update shadow map: ${data.shadowMap::class.simpleName}, near: $clipNear, far: $clipFar" }

        val light = gameEntity.getComponent<DiscreteLightComponent>()?.light
        if (light == null) {
            logE { "Unable to get DiscreteLightComponent of sceneNode ${gameEntity.name}" }
            return
        }
        if (light is Light.Point) {
            logE { "Point light shadow maps are not yet supported" }
            return
        }

        // create new shadow map
        val newShadowMap = when (data.shadowMap) {
            is ShadowMapTypeData.Single -> {
                SimpleShadowMap(sceneComponent.sceneNode, light, mapSize = data.shadowMap.mapInfo.mapSize).apply {
                    this.clipNear = data.clipNear
                    this.clipFar = data.clipFar
                }
            }
            is ShadowMapTypeData.Cascaded -> {
                CascadedShadowMap(
                    sceneComponent.sceneNode,
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
        }
        disposeShadowMap(replaceMap = newShadowMap)
    }

    private fun disposeShadowMap(replaceMap: ShadowMap?) {
        val updateShadowMaps = gameEntity.scene.shaderData.shadowMaps.toMutableList()
        shadowMap?.let { updateShadowMaps -= it }
        replaceMap?.let { updateShadowMaps += it }
        gameEntity.scene.shaderData.shadowMaps = updateShadowMaps

        shadowMap?.let {
            val scene = sceneComponent.sceneNode
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
        shadowMap = replaceMap
    }
}
