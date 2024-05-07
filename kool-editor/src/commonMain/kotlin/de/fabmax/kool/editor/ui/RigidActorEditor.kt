package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetRigidBodyPropertiesAction
import de.fabmax.kool.editor.components.RigidActorComponent
import de.fabmax.kool.editor.data.RigidActorProperties
import de.fabmax.kool.editor.data.RigidActorShape
import de.fabmax.kool.editor.data.RigidActorType
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.modules.ui2.*
import kotlin.reflect.KClass

class RigidActorEditor(component: RigidActorComponent) : ComponentEditor<RigidActorComponent>(component) {

    override fun UiScope.compose() = componentPanel(
        title = "Rigid Actor",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            val bodyProps = component.actorState.use()

            labeledCombobox(
                label = "Type:",
                items = typeOptions,
                typeOptions.indexOfFirst { it.type == bodyProps.type }
            ) {
                SetRigidBodyPropertiesAction(nodeId, bodyProps, bodyProps.copy(type = it.type)).apply()
            }

            if (bodyProps.type == RigidActorType.DYNAMIC) {
                labeledDoubleTextField(
                    label = "Mass:",
                    value = bodyProps.mass.toDouble(),
                    minValue = 0.001,
                    dragChangeSpeed = DragChangeRates.SIZE,
                    editHandler = ActionValueEditHandler { undo, apply ->
                        SetRigidBodyPropertiesAction(
                            nodeId,
                            bodyProps.copy(mass = undo.toFloat()),
                            bodyProps.copy(mass = apply.toFloat())
                        )
                    }
                )
            }

            shapeEditor(bodyProps)
        }
    }

    private fun ColumnScope.shapeEditor(bodyProps: RigidActorProperties) {
        menuDivider()
        labeledCombobox(
            label = "Shape:",
            items = ShapeOption.entries,
            selectedIndex = ShapeOption.entries.indexOfFirst { it.shapeType.isInstance(bodyProps.shape) }
        ) {
            val newProps = when (it) {
                ShapeOption.UseMesh -> bodyProps.copy(shape = RigidActorShape.UseMesh)
                ShapeOption.Box -> bodyProps.copy(shape = RigidActorShape.Box(Vec3Data(1.0, 1.0, 1.0)))
                ShapeOption.Sphere -> bodyProps.copy(shape = RigidActorShape.Sphere(1f))
                ShapeOption.Cylinder -> bodyProps.copy(shape = RigidActorShape.Cylinder(1f, 1f))
                ShapeOption.Capsule -> bodyProps.copy(shape = RigidActorShape.Capsule(1f, 1f))
                ShapeOption.Heightmap -> bodyProps.copy(shape = RigidActorShape.Heightmap(""))
            }
            SetRigidBodyPropertiesAction(nodeId, bodyProps, newProps).apply()
        }

        when (val shape = bodyProps.shape) {
            is RigidActorShape.Box -> boxShapeEditor(shape, bodyProps)
            is RigidActorShape.Capsule -> capsuleEditor(shape, bodyProps)
            is RigidActorShape.Cylinder -> cylinderEditor(shape, bodyProps)
            is RigidActorShape.Sphere -> sphereEditor(shape, bodyProps)
            is RigidActorShape.Heightmap -> heightmapEditor(shape, bodyProps)
            RigidActorShape.UseMesh -> {
                // todo: check if mesh shape is a valid rigid body shape
            }
        }
    }

    private fun ColumnScope.boxShapeEditor(shape: RigidActorShape.Box, bodyProps: RigidActorProperties) {
        labeledXyzRow(
            label = "Size:",
            xyz = shape.size.toVec3d(),
            dragChangeSpeed = DragChangeRates.SIZE_VEC3,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shape = RigidActorShape.Box(Vec3Data(undo)))
                val applyShape = bodyProps.copy(shape = RigidActorShape.Box(Vec3Data(apply)))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
    }

    private fun ColumnScope.capsuleEditor(shape: RigidActorShape.Capsule, bodyProps: RigidActorProperties) {
        labeledDoubleTextField(
            label = "Radius:",
            value = shape.radius.toDouble(),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shape = shape.copy(radius = undo.toFloat()))
                val applyShape = bodyProps.copy(shape = shape.copy(radius = apply.toFloat()))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
        labeledDoubleTextField(
            label = "Length:",
            value = shape.length.toDouble(),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shape = shape.copy(length = undo.toFloat()))
                val applyShape = bodyProps.copy(shape = shape.copy(length = apply.toFloat()))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
    }

    private fun ColumnScope.cylinderEditor(shape: RigidActorShape.Cylinder, bodyProps: RigidActorProperties) {
        labeledDoubleTextField(
            label = "Radius:",
            value = shape.radius.toDouble(),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shape = shape.copy(radius = undo.toFloat()))
                val applyShape = bodyProps.copy(shape = shape.copy(radius = apply.toFloat()))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
        labeledDoubleTextField(
            label = "Length:",
            value = shape.length.toDouble(),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shape = shape.copy(length = undo.toFloat()))
                val applyShape = bodyProps.copy(shape = shape.copy(length = apply.toFloat()))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
    }

    private fun ColumnScope.sphereEditor(shape: RigidActorShape.Sphere, bodyProps: RigidActorProperties) {
        labeledDoubleTextField(
            label = "Radius:",
            value = shape.radius.toDouble(),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shape = shape.copy(radius = undo.toFloat()))
                val applyShape = bodyProps.copy(shape = shape.copy(radius = apply.toFloat()))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
    }

    private fun ColumnScope.heightmapEditor(shape: RigidActorShape.Heightmap, bodyProps: RigidActorProperties) {
        TODO()
    }

    private class TypeOption(val label: String, val type: RigidActorType) {
        override fun toString(): String = label
    }

    private enum class ShapeOption(val label: String, val shapeType: KClass<out RigidActorShape>) {
        UseMesh("Use mesh", RigidActorShape.UseMesh::class),
        Box("Box", RigidActorShape.Box::class),
        Sphere("Sphere", RigidActorShape.Sphere::class),
        Cylinder("Cylinder", RigidActorShape.Cylinder::class),
        Capsule("Capsule", RigidActorShape.Capsule::class),
        Heightmap("Heightmap", RigidActorShape.Heightmap::class);

        override fun toString(): String = label
    }

    companion object {
        private val typeOptions = listOf(
            TypeOption("Dynamic", RigidActorType.DYNAMIC),
            TypeOption("Kinematic", RigidActorType.KINEMATIC),
            TypeOption("Static", RigidActorType.STATIC),
        )
    }
}