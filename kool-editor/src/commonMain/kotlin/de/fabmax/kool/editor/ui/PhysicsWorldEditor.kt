package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetPhysicsWorldGravityAction
import de.fabmax.kool.editor.actions.SetPhysicsWorldPropertiesAction
import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.toVec3f
import de.fabmax.kool.modules.ui2.UiScope

class PhysicsWorldEditor : ComponentEditor<PhysicsWorldComponent>() {

    override fun UiScope.compose() = componentPanel(
        title = "Physics World",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        components.forEach { it.physicsWorldState.use() }

        booleanPropertyEditor(
            dataGetter = { it.physicsWorldState.value },
            valueGetter = { it.isContinuousCollisionDetection },
            valueSetter = { oldData, newValue -> oldData.copy(isContinuousCollisionDetection = newValue) },
            actionMapper = { component, undoData, applyData ->
                SetPhysicsWorldPropertiesAction(component.gameEntity.entityId, undoData, applyData)
            },
            label = "Continuous collision detection",
        )

        vec3dPropertyEditor<Vec3d>(
            dataGetter = { it.physicsWorldState.value.gravity.toVec3d() },
            valueGetter = { it },
            valueSetter = { _, newValue -> newValue },
            actionMapper = { component, undoData, applyData ->
                SetPhysicsWorldGravityAction(component.gameEntity.entityId, undoData.toVec3f(), applyData.toVec3f())
            },
            "Gravity:"
        )
    }

}