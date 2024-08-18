package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.data.CameraTypeData
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.modules.ui2.ColumnScope
import de.fabmax.kool.modules.ui2.UiScope
import kotlin.reflect.KClass

class CameraEditor : ComponentEditor<CameraComponent>() {

    private val currentCam: CameraTypeData get() = component.data.camera
    private val camTypeIndex: Int
        get() = camTypes.indexOfFirst { it.camType.isInstance(currentCam) }

    override fun UiScope.compose() = componentPanel("Camera", Icons.small.camera, ::removeComponent) {
        labeledCombobox("Type:", camTypes, camTypeIndex) {
            val newCam = when (it.camType) {
                CameraTypeData.Perspective::class -> CameraTypeData.Perspective()
                CameraTypeData.Orthographic::class -> CameraTypeData.Orthographic(1f)
                else -> throw IllegalStateException("Unsupported cam type: ${it.camType}")
            }
            setCamDataAction(component, currentCam, newCam).apply()
        }

        menuDivider()

        when (component.dataState.use().camera) {
            is CameraTypeData.Perspective -> perspectiveSettings()
            is CameraTypeData.Orthographic -> TODO()
        }
    }

    private fun ColumnScope.perspectiveSettings() {
        labeledDoubleTextField(
            label = "Clip near:",
            value = currentCam.clipNear.toDouble(),
            minValue = 0.001,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val camData = currentCam as CameraTypeData.Perspective
                val applyCam = camData.copy(clipNear = applyValue.toFloat())
                val undoCam = camData.copy(clipNear = undoValue.toFloat())
                setCamDataAction(component, undoCam, applyCam)
            }
        )
        labeledDoubleTextField(
            label = "Clip far:",
            value = currentCam.clipFar.toDouble(),
            minValue = currentCam.clipNear.toDouble() + 1,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 1000.0,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val camData = currentCam as CameraTypeData.Perspective
                val applyCam = camData.copy(clipFar = applyValue.toFloat())
                val undoCam = camData.copy(clipFar = undoValue.toFloat())
                setCamDataAction(component, undoCam, applyCam)
            }
        )
        labeledDoubleTextField(
            label = "Field of view:",
            value = (currentCam as CameraTypeData.Perspective).fovY.toDouble(),
            minValue = 1.0,
            maxValue = 120.0,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * 120.0,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val camData = currentCam as CameraTypeData.Perspective
                val applyCam = camData.copy(fovY = applyValue.toFloat())
                val undoCam = camData.copy(fovY = undoValue.toFloat())
                setCamDataAction(component, undoCam, applyCam)
            }
        )
    }

    private fun setCamDataAction(component: CameraComponent, oldCamData: CameraTypeData, newCameraData: CameraTypeData) =
        SetComponentDataAction(component, component.data.copy(camera = oldCamData), component.data.copy(camera = newCameraData))

    private class CameraTypeOption<T: CameraTypeData>(val name: String, val camType: KClass<T>) {
        override fun toString(): String = name
    }

    companion object {
        private val camTypes = listOf(
            CameraTypeOption("Perspective", CameraTypeData.Perspective::class),

            // fixme: orthographic cam is not really supported yet in editor, so we hide the option
            //CameraTypeOption("Orthographic", CameraTypeData.Orthographic::class),
        )
    }
}