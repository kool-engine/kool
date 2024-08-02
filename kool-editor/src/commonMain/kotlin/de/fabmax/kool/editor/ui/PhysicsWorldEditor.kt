package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toVec3f
import de.fabmax.kool.modules.ui2.UiScope

class PhysicsWorldEditor : ComponentEditor<PhysicsWorldComponent>() {

    override fun UiScope.compose() = componentPanel(
        title = "Physics World",
        imageIcon = Icons.small.physics,
        onRemove = ::removeComponent,
    ) {
        components.forEach { it.dataState.use() }

        booleanPropertyEditor(
            dataGetter = { it.data },
            valueGetter = { it.isContinuousCollisionDetection },
            valueSetter = { oldData, newValue -> oldData.copy(isContinuousCollisionDetection = newValue) },
            actionMapper = { component, undoData, applyData ->
                SetComponentDataAction(component, undoData, applyData)
            },
            label = "Continuous collision detection",
        )

        vec3dPropertyEditor<Vec3d>(
            dataGetter = { it.data.gravity.toVec3d() },
            valueGetter = { it },
            valueSetter = { _, newValue -> newValue },
            actionMapper = { component, undoData, applyData ->
                setGravityAction(component, undoData.toVec3f(), applyData.toVec3f())
            },
            "Gravity:"
        )
    }

    private fun setGravityAction(component: PhysicsWorldComponent, oldGravity: Vec3f, newGravity: Vec3f) =
        SetComponentDataAction(component, component.data.copy(gravity = Vec3Data(oldGravity)), component.data.copy(gravity = Vec3Data(newGravity)))

}