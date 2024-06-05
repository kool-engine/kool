package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetNumberOfLightsAction
import de.fabmax.kool.editor.api.sceneComponent
import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.modules.ui2.UiScope

class ScenePropertiesEditor : ComponentEditor<SceneComponent>() {

    override fun UiScope.compose() = componentPanel("Scene Settings", IconMap.small.world) {
        val sceneComponent = scene.sceneComponent
        val cameraNodes = listOf(CameraItem(EntityId(-1L), "None", null)) +
                scene.getAllComponents<CameraComponent>().map { CameraItem(it) }.sortedBy { it.label }
        val selectedNodeId = sceneComponent.cameraState.use()?.gameEntity?.entityId ?: -1L
        val selectedIndex = cameraNodes.indexOfFirst { it.entityId == selectedNodeId }
        labeledCombobox("Camera:", cameraNodes, selectedIndex) {
            sceneComponent.cameraState.set(it.camComponent)
        }

        labeledIntTextField("Max number of lights:", sceneComponent.maxNumLightsState.use(), minValue = 0, maxValue = 8) {
            SetNumberOfLightsAction(entityId, it).apply()
        }
    }

    private class CameraItem(val entityId: EntityId, val label: String, val camComponent: CameraComponent?) {
        constructor(cam: CameraComponent) : this(cam.gameEntity.entityId, cam.gameEntity.name, cam)

        override fun toString() = label
    }
}