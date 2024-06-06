package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetScenePropertiesAction
import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.modules.ui2.UiScope

class ScenePropertiesEditor : ComponentEditor<SceneComponent>() {

    override fun UiScope.compose() = componentPanel("Scene Settings", IconMap.small.world) {
        components.forEach { it.dataState.use() }

        val cameraNodes = listOf(
            CameraItem(EntityId(-1L), "None", EntityId(0L))
        ) + scene.getAllComponents<CameraComponent>().map { CameraItem(it) }.sortedBy { it.label }

        val selectedCamId = component.data.cameraEntityId
        val selectedIndex = cameraNodes.indexOfFirst { it.entityId == selectedCamId }
        labeledCombobox("Camera:", cameraNodes, selectedIndex) {
            SetScenePropertiesAction(entityId, component.data, component.data.copy(cameraEntityId = it.camComponentId)).apply()
        }

        labeledIntTextField("Max number of lights:", component.maxNumLights, minValue = 0, maxValue = 8) {
            SetScenePropertiesAction(entityId, component.data, component.data.copy(maxNumLights = it)).apply()
        }
    }

    private fun CameraItem(cam: CameraComponent) = CameraItem(cam.gameEntity.id, cam.gameEntity.name, cam.gameEntity.id)
    private class CameraItem(val entityId: EntityId, val label: String, val camComponentId: EntityId) {
        override fun toString() = label
    }
}