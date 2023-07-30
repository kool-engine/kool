package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetCameraAction
import de.fabmax.kool.editor.components.CameraComponent
import de.fabmax.kool.editor.data.CameraTypeData
import de.fabmax.kool.modules.ui2.*
import kotlin.reflect.KClass

class CameraEditor(component: CameraComponent) : ComponentEditor<CameraComponent>(component) {

    private val currentCam: CameraTypeData get() = component.cameraState.value
    private val camTypeIndex: Int
        get() = camTypes.indexOfFirst { it.camType.isInstance(currentCam) }

    override fun UiScope.compose() = componentPanel("Camera", IconMap.small.CAMERA, ::removeComponent) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            labeledCombobox("Type:", camTypes, camTypeIndex) {
                val newCam = when (it.camType) {
                    CameraTypeData.Perspective::class -> CameraTypeData.Perspective()
                    CameraTypeData.Orthographic::class -> CameraTypeData.Orthographic(1f)
                    else -> throw IllegalStateException("Unsupported cam type: ${it.camType}")
                }
                SetCameraAction(component, newCam, currentCam).apply()
            }

            // todo: camToView
            //var camToView by remember(false)
            //labeledSwitch("Camera to view", camToView) { camToView = it }

            menuDivider()

            when (component.cameraState.use()) {
                is CameraTypeData.Perspective -> perspectiveSettings()
                is CameraTypeData.Orthographic -> TODO()
            }
        }
    }

    private fun UiScope.perspectiveSettings() {
        labeledDoubleTextField(
            label = "Clip near:",
            value = currentCam.clipNear.toDouble(),
            minValue = 0.001,
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val camData = currentCam as CameraTypeData.Perspective
                val applyCam = camData.copy(clipNear = applyValue.toFloat())
                val undoCam = camData.copy(clipNear = undoValue.toFloat())
                SetCameraAction(component, applyCam, undoCam)
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
                SetCameraAction(component, applyCam, undoCam)
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
                SetCameraAction(component, applyCam, undoCam)
            }
        )
    }

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