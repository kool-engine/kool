package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.ShadowMapComponentData
import de.fabmax.kool.editor.data.ShadowMapData
import de.fabmax.kool.editor.data.ShadowMapInfo
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.modules.ui2.mutableStateOf

class ShadowMapComponent(override val componentData: ShadowMapComponentData) :
    SceneNodeComponent(),
    EditorDataComponent<ShadowMapComponentData>
{
    val shadowMapState = mutableStateOf(componentData.shadowMap).onChange {
        if (AppState.isEditMode) {
            componentData.shadowMap = it
        }
        updateShadowMap(it)
    }

    constructor() : this(
        ShadowMapComponentData(
            ShadowMapData.Single(ShadowMapInfo(2048, 2048, 0.1f, 1000f))
        )
    )

    init {
        dependsOn(DiscreteLightComponent::class)
    }

    override suspend fun createComponent(nodeModel: EditorNodeModel) {
        super.createComponent(nodeModel)
        shadowMapState.set(componentData.shadowMap)
        updateShadowMap(componentData.shadowMap)
    }

    private fun updateShadowMap(shadowMapInfo: ShadowMapData) {

    }
}