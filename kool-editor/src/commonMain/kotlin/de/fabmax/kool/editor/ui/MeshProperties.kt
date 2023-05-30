package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetShapeAction
import de.fabmax.kool.editor.data.MeshShapeData
import de.fabmax.kool.editor.model.MeshComponent
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*
import kotlin.math.abs
import kotlin.reflect.KClass

private data class ShapeOption<T: MeshShapeData>(val name: String, val type: KClass<T>, val factory: () -> T) {
    override fun toString() = name
}

private object ShapeOptions {
    val items = listOf(
        ShapeOption("Box", MeshShapeData.Box::class) { MeshShapeData.defaultBox },
        ShapeOption("Rect", MeshShapeData.Rect::class) { MeshShapeData.defaultRect },
        ShapeOption("Ico-Sphere", MeshShapeData.IcoSphere::class) { MeshShapeData.defaultIcoSphere },
        ShapeOption("UV-Sphere", MeshShapeData.UvSphere::class) { MeshShapeData.defaultUvSphere },
        ShapeOption("Cylinder", MeshShapeData.Cylinder::class) { MeshShapeData.defaultCylinder },
        ShapeOption("Capsule", MeshShapeData.Capsule::class) { MeshShapeData.defaultCapsule },
        ShapeOption("Empty", MeshShapeData.Empty::class) { MeshShapeData.Empty },
    )

    fun indexOfShape(shape: MeshShapeData): Int {
        return when (shape) {
            is MeshShapeData.Box -> 0
            is MeshShapeData.Rect -> 1
            is MeshShapeData.IcoSphere -> 2
            is MeshShapeData.UvSphere -> 3
            is MeshShapeData.Cylinder -> 4
            is MeshShapeData.Capsule -> 5
            MeshShapeData.Empty -> 6
        }
    }
}

fun UiScope.meshTypeProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent) = collapsapsablePanel(
    title = "Mesh",
    scopeName = "${nodeModel.nodeData.nodeId}"
) {
    // todo: support multiple primitives per mesh
    val shape = meshComponent.shapesState.use().getOrNull(0) ?: return@collapsapsablePanel Unit

    Column(width = Grow.Std) {
        modifier
            .padding(horizontal = sizes.gap)
            .margin(bottom = sizes.smallGap)

        val selectedIndex = remember(ShapeOptions.indexOfShape(shape))
        labeledCombobox(
            label = "Shape:",
            items = ShapeOptions.items,
            selectedIndex = selectedIndex
        ) {
            if (!it.type.isInstance(shape)) {
                EditorActions.applyAction(
                    SetShapeAction(nodeModel, meshComponent, shape, it.factory())
                )
            }
        }

        menuDivider()

        when (val shapeType = shape) {
            is MeshShapeData.Box -> boxProperties(nodeModel, meshComponent, shapeType)
            is MeshShapeData.Rect -> rectProperties(nodeModel, meshComponent, shapeType)
            is MeshShapeData.IcoSphere -> icoSphereProperties(nodeModel, meshComponent, shapeType)
            is MeshShapeData.UvSphere -> uvSphereProperties(nodeModel, meshComponent, shapeType)
            is MeshShapeData.Cylinder -> cylinderProperties(nodeModel, meshComponent, shapeType)
            is MeshShapeData.Capsule -> capsuleProperties(nodeModel, meshComponent, shapeType)
            MeshShapeData.Empty -> { }
        }
    }
}

private fun UiScope.boxProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent, box: MeshShapeData.Box) = Column(
    width = Grow.Std,
    scopeName = "boxProperties"
) {
    xyzRow(
        label = "Size:",
        x = box.size.x,
        y = box.size.y,
        z = box.size.z
    ) { x, y, z ->
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, box, MeshShapeData.Box(box.size.copy(x, y, z)))
        )
    }
}

private fun UiScope.rectProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent, rect: MeshShapeData.Rect) = Column(
    width = Grow.Std,
    scopeName = "rectProperties"
) {
    xyRow(
        label = "Size:",
        x = rect.size.x,
        y = rect.size.y
    ) { x, y ->
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, rect, MeshShapeData.Rect(rect.size.copy(x, y)))
        )
    }
}

private fun UiScope.icoSphereProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent, icoSphere: MeshShapeData.IcoSphere) = Column(
    width = Grow.Std,
    scopeName = "icoSphereProperties"
) {
    labeledDoubleTextField("Radius:", icoSphere.radius) {
        val r = if (!it.isFinite()) 1.0 else abs(it)
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, icoSphere, icoSphere.copy(radius = r))
        )
    }
    labeledIntTextField("Sub-divisions:", icoSphere.subDivisions) {
        val s = it.clamp(0, 7)
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, icoSphere, icoSphere.copy(subDivisions = s))
        )
    }
}

private fun UiScope.uvSphereProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent, uvSphere: MeshShapeData.UvSphere) = Column(
    width = Grow.Std,
    scopeName = "uvSphereProperties"
) {
    labeledDoubleTextField("Radius:", uvSphere.radius) {
        val r = if (!it.isFinite()) 1.0 else abs(it)
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, uvSphere, uvSphere.copy(radius = r))
        )
    }
    labeledIntTextField("Steps:", uvSphere.steps) {
        val s = it.clamp(3, 100)
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, uvSphere, uvSphere.copy(steps = s))
        )
    }
}

private fun UiScope.cylinderProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent, cylinder: MeshShapeData.Cylinder) = Column(
    width = Grow.Std,
    scopeName = "cylinderProperties"
) {
    val isUniRadius = remember(cylinder.topRadius == cylinder.bottomRadius)
    isUniRadius.set(cylinder.topRadius == cylinder.bottomRadius)

    labeledCheckbox("Uniform radius:", isUniRadius) {
        if (cylinder.topRadius != cylinder.bottomRadius) {
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, cylinder, cylinder.copy(topRadius = cylinder.bottomRadius))
            )
        }
    }

    if (isUniRadius.use()) {
        labeledDoubleTextField("Radius:", cylinder.bottomRadius) {
            val r = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, cylinder, cylinder.copy(bottomRadius = r, topRadius = r))
            )
        }
    } else {
        labeledDoubleTextField("Top-radius:", cylinder.topRadius) {
            val r = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, cylinder, cylinder.copy(topRadius = r))
            )
        }
        labeledDoubleTextField("Bottom-radius:", cylinder.bottomRadius) {
            val r = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, cylinder, cylinder.copy(bottomRadius = r))
            )
        }
    }
    labeledDoubleTextField("Height:", cylinder.height) {
        val l = if (!it.isFinite()) 1.0 else abs(it)
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, cylinder, cylinder.copy(height = l))
        )
    }
    labeledIntTextField("Steps:", cylinder.steps) {
        val s = it.clamp(3, 100)
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, cylinder, cylinder.copy(steps = s))
        )
    }
}

private fun UiScope.capsuleProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent, capsule: MeshShapeData.Capsule) = Column(
    width = Grow.Std,
    scopeName = "capsuleProperties"
) {
    labeledDoubleTextField("Radius:", capsule.radius) {
        val r = if (!it.isFinite()) 1.0 else abs(it)
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, capsule, capsule.copy(radius = r))
        )
    }
    labeledDoubleTextField("Height:", capsule.height) {
        val l = if (!it.isFinite()) 1.0 else abs(it)
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, capsule, capsule.copy(height = l))
        )
    }
    labeledIntTextField("Steps:", capsule.steps) {
        val s = it.clamp(3, 100)
        EditorActions.applyAction(
            SetShapeAction(nodeModel, meshComponent, capsule, capsule.copy(steps = s))
        )
    }
}
