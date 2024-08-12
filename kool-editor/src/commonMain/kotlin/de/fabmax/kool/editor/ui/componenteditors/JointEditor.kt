package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.JointComponent
import de.fabmax.kool.editor.components.RigidActorComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.data.JointComponentData
import de.fabmax.kool.editor.data.JointData
import de.fabmax.kool.editor.data.LimitData
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.math.PI_F
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.math.toRad
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.physics.joints.D6JointMotion
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class JointEditor : ComponentEditor<JointComponent>() {

    override fun UiScope.compose() {
        val componentJoints = components.map { it.dataState.use().jointData.jointOption }
        val isAllTheSameJoint = componentJoints.all { it == componentJoints[0] }
        val (jointItems, shapeIdx) = jointOptions.getOptionsAndIndex(componentJoints)

        componentPanel(
            title = "Joint",
            imageIcon = Icons.small.joint,
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
            modifier.padding(bottom = 0.dp)

            val (itemsA, itemsB) = remember {
                val actorComponents = scene.getAllComponents<RigidActorComponent>()
                    .map { RigidActorItem(it) }
                    .sortedBy { it.label }
                val actorOptionsA = listOf(RigidActorItem(EntityId.NULL, "None (fixed)")) + actorComponents
                val actorOptionsB = listOf(RigidActorItem(EntityId.NULL, "None")) + actorComponents
                ComboBoxItems(actorOptionsA) to ComboBoxItems(actorOptionsB)
            }
            choicePropertyEditor(
                choices = itemsA,
                dataGetter = { it.data },
                valueGetter = { data -> itemsA.options.find { it.item?.entityId == data.bodyA }?.item ?: itemsA.options[0].item!! },
                valueSetter = { oldData, newValue -> oldData.copy(bodyA = newValue.entityId) },
                actionMapper = jointComponentDataActionMapper,
                label = "Body A:"
            )
            choicePropertyEditor(
                choices = itemsB,
                dataGetter = { it.data },
                valueGetter = { data -> itemsB.options.find { it.item?.entityId == data.bodyB }?.item ?: itemsB.options[0].item!! },
                valueSetter = { oldData, newValue -> oldData.copy(bodyB = newValue.entityId) },
                actionMapper = jointComponentDataActionMapper,
                label = "Body B:"
            )

            if (isAllTheSameJoint) {
                menuDivider()
                when (components[0].data.jointData) {
                    is JointData.Fixed -> fixedEditor()
                    is JointData.Distance -> distanceEditor()
                    is JointData.Prismatic -> prismaticEditor()
                    is JointData.Revolute -> revoluteEditor()
                    is JointData.Spherical -> sphericalEditor()
                    is JointData.D6 -> d6Editor()
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
        booleanPropertyEditor(
            dataGetter = { it.getData<JointData.Revolute>() },
            valueGetter = { it.limit != null },
            valueSetter = { oldData, newValue -> oldData.copy(limit = if (newValue) LimitData(-PI_F, PI_F) else null) },
            actionMapper = jointDataActionMapper,
            label = "Is limited:",
        )
        if (component.getData<JointData.Revolute>().limit != null) {
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.limit!!.limit1.toDouble().toDeg() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(limit1 = newValue.toFloat().toRad())) },
                actionMapper = jointDataActionMapper,
                minValue = -360.0,
                maxValue = 360.0,
                label = "Lower limit:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.limit!!.limit2.toDouble().toDeg() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(limit2 = newValue.toFloat().toRad())) },
                actionMapper = jointDataActionMapper,
                minValue = -360.0,
                maxValue = 360.0,
                label = "Upper limit:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.limit!!.stiffness.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(stiffness = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Stiffness:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.limit!!.damping.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(damping = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Damping:"
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
            valueGetter = { it.limit != null },
            valueSetter = { oldData, newValue -> oldData.copy(limit = if (newValue) LimitData(PI_F, PI_F) else null) },
            actionMapper = jointDataActionMapper,
            label = "Is limited:",
        )
        if (component.getData<JointData.Spherical>().limit != null) {
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Spherical>() },
                valueGetter = { it.limit!!.limit1.toDouble().toDeg() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(limit1 = newValue.toFloat().toRad())) },
                actionMapper = jointDataActionMapper,
                minValue = -360.0,
                maxValue = 360.0,
                label = "Limit angle Y:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Spherical>() },
                valueGetter = { it.limit!!.limit2.toDouble().toDeg() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(limit2 = newValue.toFloat().toRad())) },
                actionMapper = jointDataActionMapper,
                minValue = -360.0,
                maxValue = 360.0,
                label = "Limit angle Z:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.limit!!.stiffness.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(stiffness = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Stiffness:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.limit!!.damping.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(damping = newValue.toFloat())) },
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

    private fun ColumnScope.d6Editor() {
        breakSettings(
            isBreakableGetter = { (it as JointData.D6).isBreakable },
            isBreakableSetter = { oldData, newValue -> (oldData as JointData.D6).copy(isBreakable = newValue) },
            forceGetter = { (it as JointData.D6).breakForce.toDouble() },
            forceSetter = { oldData, newValue -> (oldData as JointData.D6).copy(breakForce = newValue.toFloat()) },
            torqueGetter = { (it as JointData.D6).breakTorque.toDouble() },
            torqueSetter = { oldData, newValue -> (oldData as JointData.D6).copy(breakTorque = newValue.toFloat()) },
        )

        val d6Datas = components.map { it.dataState.use().jointData as JointData.D6 }
        d6LinearEditor(
            label = "Linear X:",
            indicatorColor = MdColor.RED,
            d6Datas = d6Datas,
            motionGetter = { it.motionXOption },
            motionSetter = { option -> applyD6Data { it.copy(motionX = option.option) } }
        )
        d6LinearEditor(
            label = "Linear Y:",
            indicatorColor = MdColor.LIGHT_GREEN,
            d6Datas = d6Datas,
            motionGetter = { it.motionYOption },
            motionSetter = { option -> applyD6Data { it.copy(motionY = option.option) } }
        )
        d6LinearEditor(
            label = "Linear Z:",
            indicatorColor = MdColor.BLUE,
            d6Datas = d6Datas,
            motionGetter = { it.motionZOption },
            motionSetter = { option -> applyD6Data { it.copy(motionZ = option.option) } }
        )
    }

    private fun applyD6Data(dataMod: (JointData.D6) -> JointData.D6) {
        components.map {
            SetComponentDataAction(it, it.data, it.data.copy(jointData = dataMod(it.data.jointData as JointData.D6)))
        }.fused().apply()
    }

    private fun ColumnScope.d6LinearEditor(
        label: String,
        indicatorColor: Color,
        d6Datas: List<JointData.D6>,
        motionGetter: (JointData.D6) -> D6MotionOption,
        motionSetter: (D6MotionOption) -> Unit,
    ) = collapsablePanelLvl2(
        title = label,
        indicatorColor = indicatorColor,
        isAlwaysShowIndicator = true,
        headerContent = {
            val (motionItems, shapeIdx) = d6MotionOptions.getOptionsAndIndex(d6Datas.map(motionGetter))
            ComboBox {
                defaultComboBoxStyle()
                modifier
                    .margin(end = sizes.gap)
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
                    .items(motionItems)
                    .selectedIndex(shapeIdx)
                    .onItemSelected { index -> motionItems[index].item?.let(motionSetter) }
            }
        }
    ) {
        val motions = d6Datas.map(motionGetter)
        val allTheSameMotion = motions.all { it == motions[0] }
        if (allTheSameMotion) {
            TODO()
        }

        Unit
    }

    private fun ColumnScope.prismaticEditor() {
        booleanPropertyEditor(
            dataGetter = { it.getData<JointData.Prismatic>() },
            valueGetter = { it.limit != null },
            valueSetter = { oldData, newValue -> oldData.copy(limit = if (newValue) LimitData(-1f, 1f) else null) },
            actionMapper = jointDataActionMapper,
            label = "Is limited:",
        )
        if (component.getData<JointData.Revolute>().limit != null) {
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Prismatic>() },
                valueGetter = { it.limit!!.limit1.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(limit1 = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Lower limit:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Prismatic>() },
                valueGetter = { it.limit!!.limit2.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(limit2 = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Upper limit:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.limit!!.stiffness.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(stiffness = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Stiffness:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.limit!!.damping.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(damping = newValue.toFloat())) },
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
            JointOption.D6 -> JointData.D6()
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
        D6("D6", { it is JointData.D6 }),
        Revolute("Revolute", { it is JointData.Revolute }),
        Spherical("Spherical", { it is JointData.Spherical }),
        Prismatic("Prismatic", { it is JointData.Prismatic }),
        Distance("Distance", { it is JointData.Distance }),
        Fixed("Fixed", { it is JointData.Fixed }),
    }

    private val JointData.D6.motionXOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == motionX }
    private val JointData.D6.motionYOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == motionY }
    private val JointData.D6.motionZOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == motionZ }
    private val JointData.D6.motionTwistOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == motionTwist }
    private val JointData.D6.motionSwingYOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == motionSwingY }
    private val JointData.D6.motionSwingZOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == motionSwingZ }

    private enum class D6MotionOption(val label: String, val option: D6JointMotion) {
        Locked("Locked", D6JointMotion.Locked),
        Limited("Limited", D6JointMotion.Limited),
        Free("Free", D6JointMotion.Free),
    }

    companion object {
        private val jointOptions = ComboBoxItems(JointOption.entries) { it.label }
        private val jointComponentDataActionMapper: (JointComponent, JointComponentData, JointComponentData) -> EditorAction = { component, undoData, applyData ->
            SetComponentDataAction(component, undoData, applyData)
        }
        private val jointDataActionMapper: (JointComponent, JointData, JointData) -> EditorAction = { component, undoData, applyData ->
            SetComponentDataAction(component, component.data.copy(jointData = undoData), component.data.copy(jointData = applyData))
        }

        private val d6MotionOptions = ComboBoxItems(D6MotionOption.entries) { it.label }
    }
}