package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.addEmptyNode
import de.fabmax.kool.editor.actions.addNewLight
import de.fabmax.kool.editor.actions.addNewMesh
import de.fabmax.kool.editor.actions.addNewModel
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.scene
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.math.Vec3f

fun addSceneObjectMenu(label: String, parent: GameEntity?, position: Vec3f? = null) = SubMenuItem<GameEntity?>(label, IconMap.small.plus) {
    val editor = KoolEditor.instance
    val scene = editor.activeScene.value ?: return@SubMenuItem

    subMenu("Mesh", IconMap.small.cube) {
        item("Box", IconMap.small.cube) { scene.addNewMesh(parent, ShapeData.defaultBox, position) }
        item("Rect", IconMap.small.rect) { scene.addNewMesh(parent, ShapeData.defaultRect, position) }
        item("Sphere", IconMap.small.shadowedSphere) { scene.addNewMesh(parent, ShapeData.defaultSphere, position) }
        item("Cylinder", IconMap.small.cylinder) { scene.addNewMesh(parent, ShapeData.defaultCylinder, position) }
        item("Capsule", IconMap.small.capsule) { scene.addNewMesh(parent, ShapeData.defaultCapsule, position) }
        item("Heightmap", IconMap.small.mountain) { scene.addNewMesh(parent, ShapeData.defaultHeightmap, position) }
        item("Custom", IconMap.small.emptyObject) { scene.addNewMesh(parent, ShapeData.defaultCustom, position) }
    }
    subMenu("glTF model", IconMap.small.file3d) {
        editor.availableAssets.modelAssets.forEach { modelAsset ->
            item(modelAsset.name) { scene.addNewModel(parent ?: it, modelAsset, position) }
        }
    }
    editor.activeScene.value?.let { sceneModel ->
        if (sceneModel.scene.lighting.lights.size < sceneModel.shaderData.maxNumberOfLights) {
            subMenu("Light", IconMap.small.light) {
                item("Directional", IconMap.small.sun) { scene.addNewLight(parent ?: it, LightTypeData.Directional(), position) }
                item("Spot", IconMap.small.spotLight) { scene.addNewLight(parent ?: it, LightTypeData.Spot(), position) }
                item("Point", IconMap.small.light) { scene.addNewLight(parent ?: it, LightTypeData.Point(), position) }
            }
        }
    }
    item("Empty node", IconMap.small.emptyObject) { scene.addEmptyNode(parent ?: it, position) }
}