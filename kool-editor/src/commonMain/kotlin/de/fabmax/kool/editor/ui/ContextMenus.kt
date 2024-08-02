package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.addEmptyNode
import de.fabmax.kool.editor.actions.addNewLight
import de.fabmax.kool.editor.actions.addNewMesh
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.scene
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.math.Vec3f

fun addSceneObjectMenu(label: String, parent: GameEntity?, position: Vec3f? = null) = SubMenuItem<GameEntity?>(label, Icons.small.plus) {
    val editor = KoolEditor.instance
    val scene = editor.activeScene.value ?: return@SubMenuItem

    subMenu("Mesh", Icons.small.cube) {
        item("Box", Icons.small.cube) { scene.addNewMesh(parent, ShapeData.defaultBox, position) }
        item("Rect", Icons.small.rect) { scene.addNewMesh(parent, ShapeData.defaultRect, position) }
        item("Sphere", Icons.small.shadowedSphere) { scene.addNewMesh(parent, ShapeData.defaultSphere, position) }
        item("Cylinder", Icons.small.cylinder) { scene.addNewMesh(parent, ShapeData.defaultCylinder, position) }
        item("Capsule", Icons.small.capsule) { scene.addNewMesh(parent, ShapeData.defaultCapsule, position) }
        item("Custom", Icons.small.code) { scene.addNewMesh(parent, ShapeData.Custom, position) }
    }
    if (editor.availableAssets.modelAssets.isNotEmpty()) {
        subMenu("glTF model", Icons.small.file3d) {
            editor.availableAssets.modelAssets.forEach { model ->
                item(model.name) { scene.addNewMesh(parent, ShapeData.Model(model.path), position) }
            }
        }
    }
    if (editor.availableAssets.heightmapAssets.isNotEmpty()) {
        subMenu("Heightmap", Icons.small.mountain) {
            editor.availableAssets.heightmapAssets.forEach { heightmap ->
                item(heightmap.name) { scene.addNewMesh(parent, ShapeData.Heightmap(heightmap.path), position) }
            }
        }
    }
    editor.activeScene.value?.let { sceneModel ->
        if (sceneModel.scene.lighting.lights.size < sceneModel.shaderData.maxNumberOfLights) {
            subMenu("Light", Icons.small.light) {
                item("Directional", Icons.small.sun) { scene.addNewLight(parent, LightTypeData.Directional(), position) }
                item("Spot", Icons.small.spotLight) { scene.addNewLight(parent, LightTypeData.Spot(), position) }
                item("Point", Icons.small.light) { scene.addNewLight(parent, LightTypeData.Point(), position) }
            }
        }
    }
    item("Empty node", Icons.small.emptyObject) { scene.addEmptyNode(parent, position) }
}