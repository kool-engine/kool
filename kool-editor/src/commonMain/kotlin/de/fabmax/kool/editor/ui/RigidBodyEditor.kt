package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetRigidBodyPropertiesAction
import de.fabmax.kool.editor.components.RigidBodyComponent
import de.fabmax.kool.editor.data.RigidBodyProperties
import de.fabmax.kool.editor.data.RigidBodyShape
import de.fabmax.kool.editor.data.RigidBodyType
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.modules.ui2.*
import kotlin.reflect.KClass

class RigidBodyEditor(component: RigidBodyComponent) : ComponentEditor<RigidBodyComponent>(component) {

    override fun UiScope.compose() = componentPanel(
        title = "Collider",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            val bodyProps = component.bodyState.use()

            labeledCombobox(
                label = "Type:",
                items = typeOptions,
                typeOptions.indexOfFirst { it.type == bodyProps.type }
            ) {
                SetRigidBodyPropertiesAction(nodeId, bodyProps, bodyProps.copy(type = it.type)).apply()
            }

            if (bodyProps.type == RigidBodyType.DYNAMIC) {
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

    private fun ColumnScope.shapeEditor(bodyProps: RigidBodyProperties) {
        menuDivider()
        labeledCombobox(
            label = "Shape:",
            items = ShapeOption.entries,
            selectedIndex = ShapeOption.entries.indexOfFirst { it.shapeType.isInstance(bodyProps.shape) }
        ) {
            val newProps = when (it) {
                ShapeOption.UseMesh -> bodyProps.copy(shape = RigidBodyShape.UseMesh)
                ShapeOption.Box -> bodyProps.copy(shape = RigidBodyShape.Box(Vec3Data(1.0, 1.0, 1.0)))
                ShapeOption.Sphere -> bodyProps.copy(shape = RigidBodyShape.Sphere(1f))
                ShapeOption.Cylinder -> bodyProps.copy(shape = RigidBodyShape.Cylinder(1f, 1f))
                ShapeOption.Capsule -> bodyProps.copy(shape = RigidBodyShape.Capsule(1f, 1f))
            }
            SetRigidBodyPropertiesAction(nodeId, bodyProps, newProps).apply()
        }

        when (val shape = bodyProps.shape) {
            is RigidBodyShape.Box -> boxShapeEditor(shape, bodyProps)
            is RigidBodyShape.Capsule -> capsuleEditor(shape, bodyProps)
            is RigidBodyShape.Cylinder -> cylinderEditor(shape, bodyProps)
            is RigidBodyShape.Sphere -> sphereEditor(shape, bodyProps)
            RigidBodyShape.UseMesh -> {
                // todo: check if mesh shape is a valid rigid body shape
            }
        }
    }

    private fun ColumnScope.boxShapeEditor(shape: RigidBodyShape.Box, bodyProps: RigidBodyProperties) {
        labeledXyzRow(
            label = "Size:",
            xyz = shape.size.toVec3d(),
            dragChangeSpeed = DragChangeRates.SIZE_VEC3,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shape = RigidBodyShape.Box(Vec3Data(undo)))
                val applyShape = bodyProps.copy(shape = RigidBodyShape.Box(Vec3Data(apply)))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
    }

    private fun ColumnScope.capsuleEditor(shape: RigidBodyShape.Capsule, bodyProps: RigidBodyProperties) {
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

    private fun ColumnScope.cylinderEditor(shape: RigidBodyShape.Cylinder, bodyProps: RigidBodyProperties) {
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

    private fun ColumnScope.sphereEditor(shape: RigidBodyShape.Sphere, bodyProps: RigidBodyProperties) {
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

    private class TypeOption(val label: String, val type: RigidBodyType) {
        override fun toString(): String = label
    }

    private enum class ShapeOption(val label: String, val shapeType: KClass<out RigidBodyShape>) {
        UseMesh("Use mesh", RigidBodyShape.UseMesh::class),
        Box("Box", RigidBodyShape.Box::class),
        Sphere("Sphere", RigidBodyShape.Sphere::class),
        Cylinder("Cylinder", RigidBodyShape.Cylinder::class),
        Capsule("Capsule", RigidBodyShape.Capsule::class);

        override fun toString(): String = label
    }

    companion object {
        private val typeOptions = listOf(
            TypeOption("Dynamic", RigidBodyType.DYNAMIC),
            TypeOption("Kinematic", RigidBodyType.KINEMATIC),
            TypeOption("Static", RigidBodyType.STATIC),
        )
    }
}