package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.JointComponent
import de.fabmax.kool.editor.components.RigidActorComponent
import de.fabmax.kool.editor.data.*
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

            booleanPropertyEditor(
                dataGetter = { it.data },
                valueGetter = { it.isCollisionEnabled },
                valueSetter = { oldData, newValue -> oldData.copy(isCollisionEnabled = newValue) },
                actionMapper = jointComponentDataActionMapper,
                label = "Can collide:",
            )
            breakSettings()

            if (isAllTheSameJoint) {
                menuDivider()
                when (components[0].data.jointData) {
                    is JointData.Fixed -> { }
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
    }

    private fun ColumnScope.revoluteEditor() {
        booleanPropertyEditor(
            dataGetter = { it.getData<JointData.Revolute>() },
            valueGetter = { it.isMotor },
            valueSetter = { oldData, newValue -> oldData.copy(isMotor = newValue) },
            actionMapper = jointDataActionMapper,
            label = "Is driven:",
        )
        if (component.getData<JointData.Revolute>().isMotor) {
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.driveSpeed.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(driveSpeed = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Drive speed:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Revolute>() },
                valueGetter = { it.driveTorque.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(driveTorque = newValue.toFloat()) },
                actionMapper = jointDataActionMapper,
                label = "Drive torque:",
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
                dataGetter = { it.getData<JointData.Spherical>() },
                valueGetter = { it.limit!!.stiffness.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(stiffness = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Stiffness:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Spherical>() },
                valueGetter = { it.limit!!.damping.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(damping = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Damping:"
            )
        }
    }

    private fun ColumnScope.d6Editor() {
        val d6Datas = components.map { it.dataState.use().jointData as JointData.D6 }
        d6LinearEditor(
            label = "Linear X:",
            indicatorColor = MdColor.RED,
            d6Datas = d6Datas,
            axis = d6LinearDataX,
        )
        d6LinearEditor(
            label = "Linear Y:",
            indicatorColor = MdColor.LIGHT_GREEN,
            d6Datas = d6Datas,
            axis = d6LinearDataY,
        )
        d6LinearEditor(
            label = "Linear Z:",
            indicatorColor = MdColor.BLUE,
            d6Datas = d6Datas,
            axis = d6LinearDataZ,
        )
        d6AngularEditor(
            label = "Angular X:",
            indicatorColor = MdColor.PURPLE,
            d6Datas = d6Datas,
            axis = d6AngularDataX,
        )
        d6AngularEditor(
            label = "Angular Y:",
            indicatorColor = MdColor.LIME,
            d6Datas = d6Datas,
            axis = d6AngularDataY,
        )
        d6AngularEditor(
            label = "Angular Z:",
            indicatorColor = MdColor.CYAN,
            d6Datas = d6Datas,
            axis = d6AngularDataZ,
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
        axis: D6AxisData,
    ) = collapsablePanelLvl2(
        title = label,
        indicatorColor = indicatorColor,
        isAlwaysShowIndicator = true,
        startExpanded = true,
        headerContent = {
            val (motionItems, shapeIdx) = d6MotionOptions.getOptionsAndIndex(d6Datas.map(axis.motionGetter))
            ComboBox {
                defaultComboBoxStyle()
                modifier
                    .margin(end = sizes.gap)
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
                    .items(motionItems)
                    .selectedIndex(shapeIdx)
                    .onItemSelected { index -> motionItems[index].item?.let(axis.motionSetter) }
            }
        }
    ) {
        val motions = d6Datas.map(axis.motionGetter)
        val allTheSameMotion = motions.all { it == motions[0] }
        if (allTheSameMotion) {
            if (motions[0] == D6MotionOption.Limited) {
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.limitGetter(it).limit1.toDouble() },
                    valueSetter = { oldData, newValue -> axis.limitSetter(oldData, axis.limitGetter(oldData).copy(limit1 = newValue.toFloat())) },
                    actionMapper = jointDataActionMapper,
                    label = "Lower limit:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.limitGetter(it).limit2.toDouble() },
                    valueSetter = { oldData, newValue -> axis.limitSetter(oldData, axis.limitGetter(oldData).copy(limit2 = newValue.toFloat())) },
                    actionMapper = jointDataActionMapper,
                    label = "Upper limit:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.limitGetter(it).stiffness.toDouble() },
                    valueSetter = { oldData, newValue -> axis.limitSetter(oldData, axis.limitGetter(oldData).copy(stiffness = newValue.toFloat())) },
                    actionMapper = jointDataActionMapper,
                    label = "Stiffness:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.limitGetter(it).damping.toDouble() },
                    valueSetter = { oldData, newValue -> axis.limitSetter(oldData, axis.limitGetter(oldData).copy(damping = newValue.toFloat())) },
                    actionMapper = jointDataActionMapper,
                    label = "Damping:"
                )
                menuDivider()
            }
        }
        if (motions[0] != D6MotionOption.Locked) {
            booleanPropertyEditor(
                dataGetter = { it.getData<JointData.D6>() },
                valueGetter = { axis.driveGetter(it) != null },
                valueSetter = { oldData, newValue -> axis.driveSetter(oldData, if (newValue) D6DriveData() else null) },
                actionMapper = jointDataActionMapper,
                label = "Is driven:",
            )
            if (axis.driveGetter(component.getData()) != null) {
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.driveGetter(it)?.targetVelocity?.toDouble() ?: 0.0 },
                    valueSetter = { oldData, newValue ->
                        axis.driveSetter(oldData, (axis.driveGetter(oldData) ?: D6DriveData()).copy(targetVelocity = newValue.toFloat()))
                    },
                    actionMapper = jointDataActionMapper,
                    label = "Target velocity:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.driveGetter(it)?.forceLimit?.toDouble() ?: 0.0 },
                    valueSetter = { oldData, newValue ->
                        axis.driveSetter(oldData, (axis.driveGetter(oldData) ?: D6DriveData()).copy(forceLimit = newValue.toFloat()))
                    },
                    actionMapper = jointDataActionMapper,
                    label = "Force limit:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.driveGetter(it)?.stiffness?.toDouble() ?: 0.0 },
                    valueSetter = { oldData, newValue ->
                        axis.driveSetter(oldData, (axis.driveGetter(oldData) ?: D6DriveData()).copy(stiffness = newValue.toFloat()))
                    },
                    actionMapper = jointDataActionMapper,
                    label = "Stiffness:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.driveGetter(it)?.damping?.toDouble() ?: 0.0 },
                    valueSetter = { oldData, newValue ->
                        axis.driveSetter(oldData, (axis.driveGetter(oldData) ?: D6DriveData()).copy(damping = newValue.toFloat()))
                    },
                    actionMapper = jointDataActionMapper,
                    label = "Damping:"
                )
            }
        }
    }

    private fun ColumnScope.d6AngularEditor(
        label: String,
        indicatorColor: Color,
        d6Datas: List<JointData.D6>,
        axis: D6AxisData,
    ) = collapsablePanelLvl2(
        title = label,
        indicatorColor = indicatorColor,
        isAlwaysShowIndicator = true,
        startExpanded = true,
        headerContent = {
            val (motionItems, shapeIdx) = d6MotionOptions.getOptionsAndIndex(d6Datas.map(axis.motionGetter))
            ComboBox {
                defaultComboBoxStyle()
                modifier
                    .margin(end = sizes.gap)
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
                    .items(motionItems)
                    .selectedIndex(shapeIdx)
                    .onItemSelected { index -> motionItems[index].item?.let(axis.motionSetter) }
            }
        }
    ) {
        val motions = d6Datas.map(axis.motionGetter)
        val allTheSameMotion = motions.all { it == motions[0] }
        if (allTheSameMotion) {
            if (motions[0] == D6MotionOption.Limited) {
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.limitGetter(it).limit1.toDouble().toDeg() },
                    valueSetter = { oldData, newValue -> axis.limitSetter(oldData, axis.limitGetter(oldData).copy(limit1 = newValue.toFloat().toRad())) },
                    actionMapper = jointDataActionMapper,
                    label = "Lower limit:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.limitGetter(it).limit2.toDouble().toDeg() },
                    valueSetter = { oldData, newValue -> axis.limitSetter(oldData, axis.limitGetter(oldData).copy(limit2 = newValue.toFloat().toRad())) },
                    actionMapper = jointDataActionMapper,
                    label = "Upper limit:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.limitGetter(it).stiffness.toDouble() },
                    valueSetter = { oldData, newValue -> axis.limitSetter(oldData, axis.limitGetter(oldData).copy(stiffness = newValue.toFloat())) },
                    actionMapper = jointDataActionMapper,
                    label = "Stiffness:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.limitGetter(it).damping.toDouble() },
                    valueSetter = { oldData, newValue -> axis.limitSetter(oldData, axis.limitGetter(oldData).copy(damping = newValue.toFloat())) },
                    actionMapper = jointDataActionMapper,
                    label = "Damping:"
                )
                menuDivider()
            }
        }
        if (motions[0] != D6MotionOption.Locked) {
            booleanPropertyEditor(
                dataGetter = { it.getData<JointData.D6>() },
                valueGetter = { axis.driveGetter(it) != null },
                valueSetter = { oldData, newValue -> axis.driveSetter(oldData, if (newValue) D6DriveData() else null) },
                actionMapper = jointDataActionMapper,
                label = "Is driven:",
            )
            if (axis.driveGetter(component.getData()) != null) {
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.driveGetter(it)?.targetVelocity?.toDouble() ?: 0.0 },
                    valueSetter = { oldData, newValue ->
                        axis.driveSetter(oldData, (axis.driveGetter(oldData) ?: D6DriveData()).copy(targetVelocity = newValue.toFloat()))
                    },
                    actionMapper = jointDataActionMapper,
                    label = "Target velocity:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.driveGetter(it)?.forceLimit?.toDouble() ?: 0.0 },
                    valueSetter = { oldData, newValue ->
                        axis.driveSetter(oldData, (axis.driveGetter(oldData) ?: D6DriveData()).copy(forceLimit = newValue.toFloat()))
                    },
                    actionMapper = jointDataActionMapper,
                    label = "Force limit:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.driveGetter(it)?.stiffness?.toDouble() ?: 0.0 },
                    valueSetter = { oldData, newValue ->
                        axis.driveSetter(oldData, (axis.driveGetter(oldData) ?: D6DriveData()).copy(stiffness = newValue.toFloat()))
                    },
                    actionMapper = jointDataActionMapper,
                    label = "Stiffness:"
                )
                doublePropertyEditor(
                    dataGetter = { it.getData<JointData.D6>() },
                    valueGetter = { axis.driveGetter(it)?.damping?.toDouble() ?: 0.0 },
                    valueSetter = { oldData, newValue ->
                        axis.driveSetter(oldData, (axis.driveGetter(oldData) ?: D6DriveData()).copy(damping = newValue.toFloat()))
                    },
                    actionMapper = jointDataActionMapper,
                    label = "Damping:"
                )
            }
        }
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
                dataGetter = { it.getData<JointData.Prismatic>() },
                valueGetter = { it.limit!!.stiffness.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(stiffness = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Stiffness:"
            )
            doublePropertyEditor(
                dataGetter = { it.getData<JointData.Prismatic>() },
                valueGetter = { it.limit!!.damping.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(limit = oldData.limit!!.copy(damping = newValue.toFloat())) },
                actionMapper = jointDataActionMapper,
                label = "Damping:"
            )
        }
    }

    private fun ColumnScope.breakSettings() {
        booleanPropertyEditor(
            dataGetter = { it.data },
            valueGetter = { it.isBreakable },
            valueSetter = { oldData, newValue -> oldData.copy(isBreakable = newValue) },
            actionMapper = jointComponentDataActionMapper,
            label = "Can break:",
        )
        if (component.dataState.use().isBreakable) {
            doublePropertyEditor(
                dataGetter = { it.data },
                valueGetter = { it.breakForce.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(breakForce = newValue.toFloat()) },
                actionMapper = jointComponentDataActionMapper,
                label = "Break force:",
                minValue = 0.0
            )
            doublePropertyEditor(
                dataGetter = { it.data },
                valueGetter = { it.breakTorque.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(breakTorque = newValue.toFloat()) },
                actionMapper = jointComponentDataActionMapper,
                label = "Break torque:",
                minValue = 0.0
            )
        }
    }

    private fun applyJointType(option: JointOption) {
        val newData = when (option) {
            JointOption.D6 -> JointData.D6()
            JointOption.Fixed -> JointData.Fixed
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

    private val d6LinearDataX = object : D6AxisData {
        override val limitGetter: (JointData.D6) -> LimitData = { it.linearLimitX ?: LimitData(-1f, 1f) }
        override val limitSetter: (JointData.D6, LimitData) -> JointData.D6 = { d, l -> d.copy(linearLimitX = l) }
        override val motionGetter: (JointData.D6) -> D6MotionOption = { it.lineaMotionXOption }
        override val motionSetter: (D6MotionOption) -> Unit = { option ->
            applyD6Data {
                if (it.linearMotionX == option.option) it else {
                    when (option.option) {
                        D6JointMotion.Free -> it.copy(linearMotionX = option.option, linearLimitX = null)
                        D6JointMotion.Limited -> it.copy(linearMotionX = option.option, linearLimitX = LimitData(-1f, 1f))
                        D6JointMotion.Locked -> it.copy(linearMotionX = option.option, linearLimitX = null, linearDriveX = null)
                    }
                }
            }
        }
        override val driveGetter: (JointData.D6) -> D6DriveData? = { it.linearDriveX }
        override val driveSetter: (JointData.D6, D6DriveData?) -> JointData.D6 get() = { data, drv -> data.copy(linearDriveX = drv) }
    }

    private val d6LinearDataY = object : D6AxisData {
        override val limitGetter: (JointData.D6) -> LimitData = { it.linearLimitY ?: LimitData(-1f, 1f) }
        override val limitSetter: (JointData.D6, LimitData) -> JointData.D6 = { d, l -> d.copy(linearLimitY = l) }
        override val motionGetter: (JointData.D6) -> D6MotionOption = { it.lineaMotionYOption }
        override val motionSetter: (D6MotionOption) -> Unit = { option ->
            applyD6Data {
                if (it.linearMotionY == option.option) it else {
                    when (option.option) {
                        D6JointMotion.Free -> it.copy(linearMotionY = option.option, linearLimitY = null)
                        D6JointMotion.Limited -> it.copy(linearMotionY = option.option, linearLimitY = LimitData(-1f, 1f))
                        D6JointMotion.Locked -> it.copy(linearMotionY = option.option, linearLimitY = null, linearDriveY = null)
                    }
                }
            }
        }
        override val driveGetter: (JointData.D6) -> D6DriveData? = { it.linearDriveY }
        override val driveSetter: (JointData.D6, D6DriveData?) -> JointData.D6 get() = { data, drv -> data.copy(linearDriveY = drv) }
    }

    private val d6LinearDataZ = object : D6AxisData {
        override val limitGetter: (JointData.D6) -> LimitData = { it.linearLimitZ ?: LimitData(-1f, 1f) }
        override val limitSetter: (JointData.D6, LimitData) -> JointData.D6 = { d, l -> d.copy(linearLimitZ = l) }
        override val motionGetter: (JointData.D6) -> D6MotionOption = { it.lineaMotionZOption }
        override val motionSetter: (D6MotionOption) -> Unit = { option ->
            applyD6Data {
                if (it.linearMotionZ == option.option) it else {
                    when (option.option) {
                        D6JointMotion.Free -> it.copy(linearMotionZ = option.option, linearLimitZ = null)
                        D6JointMotion.Limited -> it.copy(linearMotionZ = option.option, linearLimitZ = LimitData(-1f, 1f))
                        D6JointMotion.Locked -> it.copy(linearMotionZ = option.option, linearLimitZ = null, linearDriveZ = null)
                    }
                }
            }
        }
        override val driveGetter: (JointData.D6) -> D6DriveData? = { it.linearDriveZ }
        override val driveSetter: (JointData.D6, D6DriveData?) -> JointData.D6 get() = { data, drv -> data.copy(linearDriveZ = drv) }
    }

    private val d6AngularDataX = object : D6AxisData {
        override val limitGetter: (JointData.D6) -> LimitData = { it.angularLimitX ?: LimitData(-PI_F, PI_F) }
        override val limitSetter: (JointData.D6, LimitData) -> JointData.D6 = { d, l -> d.copy(angularLimitX = l) }
        override val motionGetter: (JointData.D6) -> D6MotionOption = { it.angularMotionXOption }
        override val motionSetter: (D6MotionOption) -> Unit = { option ->
            applyD6Data {
                if (it.angularMotionX == option.option) it else {
                    when (option.option) {
                        D6JointMotion.Free -> it.copy(angularMotionX = option.option, angularLimitX = null)
                        D6JointMotion.Limited -> it.copy(angularMotionX = option.option, angularLimitX = LimitData(-PI_F, PI_F))
                        D6JointMotion.Locked -> it.copy(angularMotionX = option.option, angularLimitX = null, angularDriveX = null)
                    }
                }
            }
        }
        override val driveGetter: (JointData.D6) -> D6DriveData? = { it.angularDriveX }
        override val driveSetter: (JointData.D6, D6DriveData?) -> JointData.D6 get() = { data, drv -> data.copy(angularDriveX = drv) }
    }

    private val d6AngularDataY = object : D6AxisData {
        override val limitGetter: (JointData.D6) -> LimitData = { it.angularLimitY ?: LimitData(-PI_F, PI_F) }
        override val limitSetter: (JointData.D6, LimitData) -> JointData.D6 = { d, l -> d.copy(angularLimitY = l) }
        override val motionGetter: (JointData.D6) -> D6MotionOption = { it.angularMotionYOption }
        override val motionSetter: (D6MotionOption) -> Unit = { option ->
            applyD6Data {
                if (it.angularMotionY == option.option) it else {
                    when (option.option) {
                        D6JointMotion.Free -> it.copy(angularMotionY = option.option, angularLimitY = null)
                        D6JointMotion.Limited -> it.copy(angularMotionY = option.option, angularLimitY = LimitData(-PI_F, PI_F))
                        D6JointMotion.Locked -> it.copy(angularMotionY = option.option, angularLimitY = null, angularDriveY = null)
                    }
                }
            }
        }
        override val driveGetter: (JointData.D6) -> D6DriveData? = { it.angularDriveY }
        override val driveSetter: (JointData.D6, D6DriveData?) -> JointData.D6 get() = { data, drv -> data.copy(angularDriveY = drv) }
    }

    private val d6AngularDataZ = object : D6AxisData {
        override val limitGetter: (JointData.D6) -> LimitData = { it.angularLimitZ ?: LimitData(-PI_F, PI_F) }
        override val limitSetter: (JointData.D6, LimitData) -> JointData.D6 = { d, l -> d.copy(angularLimitZ = l) }
        override val motionGetter: (JointData.D6) -> D6MotionOption = { it.angularMotionZOption }
        override val motionSetter: (D6MotionOption) -> Unit = { option ->
            applyD6Data {
                if (it.angularMotionZ == option.option) it else {
                    when (option.option) {
                        D6JointMotion.Free -> it.copy(angularMotionZ = option.option, angularLimitZ = null)
                        D6JointMotion.Limited -> it.copy(angularMotionZ = option.option, angularLimitZ = LimitData(-PI_F, PI_F))
                        D6JointMotion.Locked -> it.copy(angularMotionZ = option.option, angularLimitZ = null, angularDriveZ = null)
                    }
                }
            }
        }
        override val driveGetter: (JointData.D6) -> D6DriveData? = { it.angularDriveZ }
        override val driveSetter: (JointData.D6, D6DriveData?) -> JointData.D6 get() = { data, drv -> data.copy(angularDriveZ = drv) }
    }

    private interface D6AxisData {
        val motionGetter: (JointData.D6) -> D6MotionOption
        val motionSetter: (D6MotionOption) -> Unit
        val limitGetter: (JointData.D6) -> LimitData
        val limitSetter: (JointData.D6, LimitData) -> JointData.D6
        val driveGetter: (JointData.D6) -> D6DriveData?
        val driveSetter: (JointData.D6, D6DriveData?) -> JointData.D6
    }

    private enum class JointOption(val label: String, val matches: (JointData?) -> Boolean) {
        D6("D6", { it is JointData.D6 }),
        Revolute("Revolute", { it is JointData.Revolute }),
        Spherical("Spherical", { it is JointData.Spherical }),
        Prismatic("Prismatic", { it is JointData.Prismatic }),
        Distance("Distance", { it is JointData.Distance }),
        Fixed("Fixed", { it is JointData.Fixed }),
    }

    private enum class D6MotionOption(val label: String, val option: D6JointMotion) {
        Locked("Locked", D6JointMotion.Locked),
        Limited("Limited", D6JointMotion.Limited),
        Free("Free", D6JointMotion.Free),
    }

    companion object {
        private val JointData.D6.lineaMotionXOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == linearMotionX }
        private val JointData.D6.lineaMotionYOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == linearMotionY }
        private val JointData.D6.lineaMotionZOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == linearMotionZ }
        private val JointData.D6.angularMotionXOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == angularMotionX }
        private val JointData.D6.angularMotionYOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == angularMotionY }
        private val JointData.D6.angularMotionZOption: D6MotionOption get() = D6MotionOption.entries.first { it.option == angularMotionZ }

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