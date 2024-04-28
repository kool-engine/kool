package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetNumberOfLightsAction
import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.components.ScenePropertiesComponent
import de.fabmax.kool.modules.ui2.*

class ScenePropertiesEditor(component: ScenePropertiesComponent) : ComponentEditor<ScenePropertiesComponent>(component) {

    override fun UiScope.compose() = componentPanel("Scene Settings", IconMap.small.world) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)


            val cameraNodes = listOf(CameraItem(-1L, "None", null)) +
                    sceneModel.getComponentsInScene<CameraComponent>().map { CameraItem(it) }.sortedBy { it.label }
            val selectedNodeId = sceneModel.cameraState.use()?.nodeModel?.nodeId ?: -1L
            val selectedIndex = cameraNodes.indexOfFirst { it.nodeId == selectedNodeId }
            labeledCombobox("Camera:", cameraNodes, selectedIndex) {
                sceneModel.cameraState.set(it.camComponent)
            }

            labeledIntTextField("Max number of lights:", sceneModel.maxNumLightsState.use(), minValue = 0, maxValue = 8) {
                SetNumberOfLightsAction(sceneModel, it).apply()
            }
        }
    }

    private class CameraItem(val nodeId: Long, val label: String, val camComponent: CameraComponent?) {
        constructor(cam: CameraComponent) : this(cam.nodeModel.nodeId, cam.nodeModel.name, cam)

        override fun toString() = label
    }
}