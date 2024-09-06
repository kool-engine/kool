package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.components.SceneComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.SceneUpAxis
import de.fabmax.kool.editor.ui.Icons
import de.fabmax.kool.editor.ui.labeledCheckbox
import de.fabmax.kool.editor.ui.labeledCombobox
import de.fabmax.kool.editor.ui.labeledIntTextField
import de.fabmax.kool.modules.ksl.blocks.ToneMapping
import de.fabmax.kool.modules.ui2.UiScope

class ScenePropertiesEditor : ComponentEditor<SceneComponent>() {

    override fun UiScope.compose() = componentPanel("Scene Settings", Icons.small.world) {
        components.forEach { it.dataState.use() }

        val cameras = listOf(CameraItem(EntityId.NULL, "None")) +
                scene.getAllComponents<CameraComponent>().map { CameraItem(it) }.sortedBy { it.label }

        val selectedCamId = component.data.cameraEntityId
        val selectedIndex = cameras.indexOfFirst { it.camEntityId == selectedCamId }
        labeledCombobox("Camera:", cameras, selectedIndex) {
            SetComponentDataAction(component, component.data, component.data.copy(cameraEntityId = it.camEntityId)).apply()
        }

        val selectedToneMappingIdx = toneMappingOptions.indexOfFirst { it.toneMap == component.data.toneMapping }
        labeledCombobox("Tone mapping:", toneMappingOptions, selectedToneMappingIdx) {
            SetComponentDataAction(component, component.data, component.data.copy(toneMapping = it.toneMap)).apply()
        }

        labeledIntTextField("Max number of lights:", component.data.maxNumLights, minValue = 0, maxValue = 8) {
            SetComponentDataAction(component, component.data, component.data.copy(maxNumLights = it)).apply()
        }

        val selectedAxisIdx = upAxisOptions.indexOfFirst { it.upAxis == component.data.upAxis }
        labeledCombobox("Up Axis:", upAxisOptions, selectedAxisIdx) {
            SetComponentDataAction(component, component.data, component.data.copy(upAxis = it.upAxis)).apply()
        }

        labeledCheckbox("Floating origin:", component.data.isFloatingOrigin) {
            SetComponentDataAction(component, component.data, component.data.copy(isFloatingOrigin = it)).apply()
        }
    }

    private fun CameraItem(cam: CameraComponent) = CameraItem(cam.gameEntity.id, cam.gameEntity.name)
    private class CameraItem(val camEntityId: EntityId, val label: String) {
        override fun toString() = label
    }

    private class ToneMappingOption(val toneMap: ToneMapping, val label: String) {
        override fun toString(): String = label
    }

    private class UpAxisOption(val upAxis: SceneUpAxis, val label: String) {
        override fun toString(): String = label
    }

    companion object {
        private val toneMappingOptions = listOf(
            ToneMappingOption(ToneMapping.Aces, "ACES"),
            ToneMappingOption(ToneMapping.AcesApproximated, "ACES (approximated)"),
            ToneMappingOption(ToneMapping.KhronosPbrNeutral, "Khronos PBR Neutral"),
            ToneMappingOption(ToneMapping.Uncharted2, "Uncharted 2"),
            ToneMappingOption(ToneMapping.ReinhardJodie, "Reinhard-Jodie"),
        )

        private val upAxisOptions = listOf(
            UpAxisOption(SceneUpAxis.X_AXIS, "X-Axis"),
            UpAxisOption(SceneUpAxis.Y_AXIS, "Y-Axis"),
            UpAxisOption(SceneUpAxis.Z_AXIS, "Z-Axis"),
        )
    }
}