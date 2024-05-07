package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetRigidBodyPropertiesAction
import de.fabmax.kool.editor.components.RigidActorComponent
import de.fabmax.kool.editor.data.RigidActorProperties
import de.fabmax.kool.editor.data.RigidActorType
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.modules.ui2.*

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
            selectedIndex = ShapeOption.entries.indexOfFirst { it.matches(bodyProps.shapes.firstOrNull()) }
        ) {
            val newProps = when (it) {
                ShapeOption.UseMesh -> bodyProps.copy(shapes = emptyList())
                ShapeOption.Box -> bodyProps.copy(shapes = listOf(ShapeData.Box(Vec3Data(1.0, 1.0, 1.0))))
                ShapeOption.Sphere -> bodyProps.copy(shapes = listOf(ShapeData.Sphere(1.0)))
                ShapeOption.Cylinder -> bodyProps.copy(shapes = listOf(ShapeData.Cylinder(1.0, 1.0, 1.0)))
                ShapeOption.Capsule -> bodyProps.copy(shapes = listOf(ShapeData.Capsule(1.0, 1.0)))
                ShapeOption.Heightmap -> bodyProps.copy(shapes = listOf(ShapeData.Heightmap("")))
            }
            SetRigidBodyPropertiesAction(nodeId, bodyProps, newProps).apply()
        }

        when (val shape = bodyProps.shapes.firstOrNull()) {
            is ShapeData.Box -> boxShapeEditor(shape, bodyProps)
            is ShapeData.Capsule -> capsuleEditor(shape, bodyProps)
            is ShapeData.Cylinder -> cylinderEditor(shape, bodyProps)
            is ShapeData.Sphere -> sphereEditor(shape, bodyProps)
            is ShapeData.Heightmap -> heightmapEditor(shape, bodyProps)
            else -> { }
        }
    }

    private fun ColumnScope.boxShapeEditor(shape: ShapeData.Box, bodyProps: RigidActorProperties) {
        labeledXyzRow(
            label = "Size:",
            xyz = shape.size.toVec3d(),
            dragChangeSpeed = DragChangeRates.SIZE_VEC3,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shapes = listOf(ShapeData.Box(Vec3Data(undo))))
                val applyShape = bodyProps.copy(shapes = listOf(ShapeData.Box(Vec3Data(apply))))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
    }

    private fun ColumnScope.capsuleEditor(shape: ShapeData.Capsule, bodyProps: RigidActorProperties) {
        labeledDoubleTextField(
            label = "Radius:",
            value = shape.radius,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shapes = listOf(shape.copy(radius = undo)))
                val applyShape = bodyProps.copy(shapes = listOf(shape.copy(radius = apply)))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
        labeledDoubleTextField(
            label = "Length:",
            value = shape.length,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shapes = listOf(shape.copy(length = undo)))
                val applyShape = bodyProps.copy(shapes = listOf(shape.copy(length = apply)))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
    }

    private fun ColumnScope.cylinderEditor(shape: ShapeData.Cylinder, bodyProps: RigidActorProperties) {
        labeledDoubleTextField(
            label = "Radius:",
            value = shape.topRadius,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shapes = listOf(shape.copy(topRadius = undo)))
                val applyShape = bodyProps.copy(shapes = listOf(shape.copy(topRadius = apply)))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
        labeledDoubleTextField(
            label = "Length:",
            value = shape.length,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shapes = listOf(shape.copy(length = undo)))
                val applyShape = bodyProps.copy(shapes = listOf(shape.copy(length = apply)))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
    }

    private fun ColumnScope.sphereEditor(shape: ShapeData.Sphere, bodyProps: RigidActorProperties) {
        labeledDoubleTextField(
            label = "Radius:",
            value = shape.radius,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val undoShape = bodyProps.copy(shapes = listOf(shape.copy(radius = undo)))
                val applyShape = bodyProps.copy(shapes = listOf(shape.copy(radius = apply)))
                SetRigidBodyPropertiesAction(nodeId, undoShape, applyShape)
            }
        )
    }

    private fun ColumnScope.heightmapEditor(shape: ShapeData.Heightmap, bodyProps: RigidActorProperties) {
        TODO()
    }

    private class TypeOption(val label: String, val type: RigidActorType) {
        override fun toString(): String = label
    }

    private enum class ShapeOption(val label: String, val matches: (ShapeData?) -> Boolean) {
        UseMesh("Use mesh", { it == null }),
        Box("Box", { it is ShapeData.Box }),
        Sphere("Sphere", { it is ShapeData.Sphere }),
        Cylinder("Cylinder", { it is ShapeData.Cylinder }),
        Capsule("Capsule", { it is ShapeData.Capsule }),
        Heightmap("Heightmap", { it is ShapeData.Heightmap });

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