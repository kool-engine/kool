package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.JointComponent
import de.fabmax.kool.editor.components.RigidActorComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.JointData
import de.fabmax.kool.modules.ui2.*

class JointEditor : ComponentEditor<JointComponent>() {

    override fun UiScope.compose() {
        val componentJoints = components.map { it.dataState.use().jointData.jointOption }
        val isAllTheSameJoint = componentJoints.all { it == componentJoints[0] }
        val (jointItems, shapeIdx) = jointOptions.getOptionsAndIndex(componentJoints)

        componentPanel(
            title = "Joint",
            imageIcon = Icons.small.physics,
            onRemove = ::removeComponent,

            headerContent = {
                ComboBox {
                    defaultComboBoxStyle()
                    modifier
                        .margin(end = sizes.gap)
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                        .items(jointItems)
                        .selectedIndex(shapeIdx)
                        .onItemSelected { index ->
                            jointItems[index].item?.let { applyJointType(it) }
                        }
                }
            }
        ) {
            val actorComponents = scene.getAllComponents<RigidActorComponent>().map { RigidActorItem(it) }.sortedBy { it.label }
            val actorOptionsA = listOf(RigidActorItem(EntityId.NULL, "None (fixed)")) + actorComponents
            val actorOptionsB = listOf(RigidActorItem(EntityId.NULL, "None")) + actorComponents


            val actorIdA = component.data.bodyA
            val actorIdB = component.data.bodyB
            val selectedIndexA = actorOptionsA.indexOfFirst { it.entityId == actorIdA }
            val selectedIndexB = actorOptionsB.indexOfFirst { it.entityId == actorIdB }
            labeledCombobox("Body A:", actorOptionsA, selectedIndexA) {
                SetComponentDataAction(component, component.data, component.data.copy(bodyA = it.entityId)).apply()
            }
            labeledCombobox("Body B:", actorOptionsB, selectedIndexB) {
                SetComponentDataAction(component, component.data, component.data.copy(bodyB = it.entityId)).apply()
            }

            if (isAllTheSameJoint) {
                menuDivider()
                when (components[0].data.jointData) {
                    is JointData.Fixed -> fixedEditor()
                    is JointData.Distance -> distanceEditor()
                    is JointData.Prismatic -> prismaticEditor()
                    is JointData.Revolute -> revoluteEditor()
                    is JointData.Spherical -> sphericalEditor()
                }
            }
        }
    }

    private inline fun <reified T : JointData> JointComponent.getData(): T = data.jointData as T

    private fun ColumnScope.fixedEditor() {
        breakSettings(
            isBreakableGetter = { (it as JointData.Fixed).isBreakable },
            isBreakableSetter = { oldData, newValue -> (oldData as JointData.Fixed).copy(isBreakable = newValue) },
            forceGetter = { (it as JointData.Fixed).breakForce.toDouble() },
            forceSetter = { oldData, newValue -> (oldData as JointData.Fixed).copy(breakForce = newValue.toFloat()) },
            torqueGetter = { (it as JointData.Fixed).breakTorque.toDouble() },
            torqueSetter = { oldData, newValue -> (oldData as JointData.Fixed).copy(breakTorque = newValue.toFloat()) },
        )
    }

    private fun ColumnScope.distanceEditor() {
        doublePropertyEditor(
            dataGetter = { it.getData<JointData.Distance>() },
            valueGetter = { it.minDistance.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(minDistance = newValue.toFloat()) },
            actionMapper = jointDataActionMapper,
            label = "Min distance:",
            minValue = 0.0
        )
        doublePropertyEditor(
            dataGetter = { it.getData<JointData.Distance>() },
            valueGetter = { it.maxDistance.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(maxDistance = newValue.toFloat()) },
            actionMapper = jointDataActionMapper,
            label = "Max distance:",
            minValue = 0.0
        )
        breakSettings(
            isBreakableGetter = { (it as JointData.Distance).isBreakable },
            isBreakableSetter = { oldData, newValue -> (oldData as JointData.Distance).copy(isBreakable = newValue) },
            forceGetter = { (it as JointData.Distance).breakForce.toDouble() },
            forceSetter = { oldData, newValue -> (oldData as JointData.Distance).copy(breakForce = newValue.toFloat()) },
            torqueGetter = { (it as JointData.Distance).breakTorque.toDouble() },
            torqueSetter = { oldData, newValue -> (oldData as JointData.Distance).copy(breakTorque = newValue.toFloat()) },
        )
    }

    private fun ColumnScope.revoluteEditor() {
        booleanPropertyEditor(
            dataGetter = { it.getData<JointData.Revolute>() },
            valueGetter = { it.isMotor },
            valueSetter = { oldData, newValue -> oldData.copy(isMotor = newValue) },
            actionMapper = jointDataActionMapper,
            label = "Is motor:",
        )
        if (component.getData<JointData.Revolute>().isMotor) {
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.motorSpeed.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(motorSpeed = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Motor speed:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.motorTorque.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(motorTorque = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Motor torque:",
                minValue = 0.0
            )
        }
        breakSettings(
            isBreakableGetter = { (it as JointData.Revolute).isBreakable },
            isBreakableSetter = { oldData, newValue -> (oldData as JointData.Revolute).copy(isBreakable = newValue) },
            forceGetter = { (it as JointData.Revolute).breakForce.toDouble() },
            forceSetter = { oldData, newValue -> (oldData as JointData.Revolute).copy(breakForce = newValue.toFloat()) },
            torqueGetter = { (it as JointData.Revolute).breakTorque.toDouble() },
            torqueSetter = { oldData, newValue -> (oldData as JointData.Revolute).copy(breakTorque = newValue.toFloat()) },
        )
    }

    private fun ColumnScope.sphericalEditor() {
        booleanPropertyEditor(
            dataGetter = { it.getData<JointData.Spherical>() },
            valueGetter = { it.isLimited },
            valueSetter = { oldData, newValue -> oldData.copy(isLimited = newValue) },
            actionMapper = jointDataActionMapper,
            label = "Is limited:",
        )
        if (component.getData<JointData.Spherical>().isLimited) {
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Spherical>() },
                valueGetter = { it.limitAngleY.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limitAngleY = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Limit angle Y:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Spherical>() },
                valueGetter = { it.limitAngleZ.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limitAngleZ = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Limit angle Z:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Spherical>() },
                valueGetter = { it.stiffness.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(stiffness = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Stiffness:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Spherical>() },
                valueGetter = { it.damping.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(damping = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Damping:"
            )
        }
        breakSettings(
            isBreakableGetter = { (it as JointData.Spherical).isBreakable },
            isBreakableSetter = { oldData, newValue -> (oldData as JointData.Spherical).copy(isBreakable = newValue) },
            forceGetter = { (it as JointData.Spherical).breakForce.toDouble() },
            forceSetter = { oldData, newValue -> (oldData as JointData.Spherical).copy(breakForce = newValue.toFloat()) },
            torqueGetter = { (it as JointData.Spherical).breakTorque.toDouble() },
            torqueSetter = { oldData, newValue -> (oldData as JointData.Spherical).copy(breakTorque = newValue.toFloat()) },
        )
    }

    private fun ColumnScope.prismaticEditor() {
        booleanPropertyEditor(
            dataGetter = { it.getData<JointData.Prismatic>() },
            valueGetter = { it.isLimited },
            valueSetter = { oldData, newValue -> oldData.copy(isLimited = newValue) },
            actionMapper = jointDataActionMapper,
            label = "Is limited:",
        )
        if (component.getData<JointData.Prismatic>().isLimited) {
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Prismatic>() },
                valueGetter = { it.lowerLimit.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(lowerLimit = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Lower limit:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Prismatic>() },
                valueGetter = { it.upperLimit.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(upperLimit = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Upper limit:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Prismatic>() },
                valueGetter = { it.stiffness.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(stiffness = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Stiffness:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Prismatic>() },
                valueGetter = { it.damping.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(damping = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Damping:"
            )
        }
        breakSettings(
            isBreakableGetter = { (it as JointData.Prismatic).isBreakable },
            isBreakableSetter = { oldData, newValue -> (oldData as JointData.Prismatic).copy(isBreakable = newValue) },
            forceGetter = { (it as JointData.Prismatic).breakForce.toDouble() },
            forceSetter = { oldData, newValue -> (oldData as JointData.Prismatic).copy(breakForce = newValue.toFloat()) },
            torqueGetter = { (it as JointData.Prismatic).breakTorque.toDouble() },
            torqueSetter = { oldData, newValue -> (oldData as JointData.Prismatic).copy(breakTorque = newValue.toFloat()) },
        )
    }

    private fun ColumnScope.breakSettings(
        isBreakableGetter: (JointData) -> Boolean,
        isBreakableSetter: (oldData: JointData, newValue: Boolean) -> JointData,
        forceGetter: (JointData) -> Double,
        forceSetter: (oldData: JointData, newValue: Double) -> JointData,
        torqueGetter: (JointData) -> Double,
        torqueSetter: (oldData: JointData, newValue: Double) -> JointData
    ) {
        booleanPropertyEditor(
            dataGetter = { it.data.jointData },
            valueGetter = isBreakableGetter,
            valueSetter = isBreakableSetter,
            actionMapper = jointDataActionMapper,
            label = "Can break:",
        )
        if (isBreakableGetter(component.dataState.use().jointData)) {
            doublePropertyEditor(
                dataGetter = { it.data.jointData },
                valueGetter = forceGetter,
                valueSetter = forceSetter,
                actionMapper = jointDataActionMapper,
                label = "Break force:",
                minValue = 0.0
            )
            doublePropertyEditor(
                dataGetter = { it.data.jointData },
                valueGetter = torqueGetter,
                valueSetter = torqueSetter,
                actionMapper = jointDataActionMapper,
                label = "Break torque:",
                minValue = 0.0
            )
        }
    }

    private fun applyJointType(option: JointOption) {
        val newData = when (option) {
            JointOption.Fixed -> JointData.Fixed()
            JointOption.Distance -> JointData.Distance()
            JointOption.Revolute -> JointData.Revolute()
            JointOption.Spherical -> JointData.Spherical()
            JointOption.Prismatic -> JointData.Prismatic()
        }
        val actions = components
            .filter { it.data::class != newData::class }
            .map { SetComponentDataAction(it, it.data, it.data.copy(jointData = newData)) }
        if (actions.isNotEmpty()) {
            actions.fused().apply()
        }
    }

    private fun RigidActorItem(actor: RigidActorComponent) = RigidActorItem(actor.gameEntity.id, actor.gameEntity.name)

    private class RigidActorItem(val entityId: EntityId, val label: String) {
        override fun toString() = label
    }

    private val JointData.jointOption: JointOption get() = JointOption.entries.first { it.matches(this) }

    private enum class JointOption(val label: String, val matches: (JointData?) -> Boolean) {
        Fixed("Fixed", { it is JointData.Fixed }),
        Distance("Distance", { it is JointData.Distance }),
        Revolute("Revolute", { it is JointData.Revolute }),
        Spherical("Spherical", { it is JointData.Spherical }),
        Prismatic("Prismatic", { it is JointData.Prismatic })
    }

    companion object {
        private val jointOptions = ComboBoxItems(JointOption.entries) { it.label }
        private val jointDataActionMapper: (JointComponent, JointData, JointData) -> EditorAction = { component, undoData, applyData ->
            SetComponentDataAction(component, component.data.copy(jointData = undoData), component.data.copy(jointData = applyData))
        }
    }
}