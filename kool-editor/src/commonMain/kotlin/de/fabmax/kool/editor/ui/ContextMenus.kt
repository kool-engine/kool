package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.*
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.scene
import de.fabmax.kool.editor.data.JointData
import de.fabmax.kool.editor.data.LightTypeData
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.math.Vec3d

fun addSceneObjectMenu(label: String, parent: GameEntity?, position: Vec3d? = null) = SubMenuItem<GameEntity?>(label, Icons.small.plus) {
    val editor = KoolEditor.instance
    val scene = editor.activeScene.value ?: return@SubMenuItem

    subMenu("Mesh", Icons.small.cube) {
        item("Box", Icons.small.cube) { scene.addMesh(parent, ShapeData.defaultBox, position) }
        item("Rect", Icons.small.rect) { scene.addMesh(parent, ShapeData.defaultRect, position) }
        item("Sphere", Icons.small.shadowedSphere) { scene.addMesh(parent, ShapeData.defaultSphere, position) }
        item("Cylinder", Icons.small.cylinder) { scene.addMesh(parent, ShapeData.defaultCylinder, position) }
        item("Capsule", Icons.small.capsule) { scene.addMesh(parent, ShapeData.defaultCapsule, position) }
        item("Custom", Icons.small.code) { scene.addMesh(parent, ShapeData.Custom, position) }
    }
    if (editor.availableAssets.modelAssets.isNotEmpty()) {
        subMenu("glTF model", Icons.small.file3d) {
            editor.availableAssets.modelAssets.forEach { model ->
                item(model.name) { scene.addMesh(parent, ShapeData.Model(model.path), position) }
            }
        }
    }
    if (editor.availableAssets.heightmapAssets.isNotEmpty()) {
        subMenu("Heightmap", Icons.small.mountain) {
            editor.availableAssets.heightmapAssets.forEach { heightmap ->
                item(heightmap.name) { scene.addMesh(parent, ShapeData.Heightmap(heightmap.path), position) }
            }
        }
    }
    editor.activeScene.value?.let { sceneModel ->
        if (sceneModel.scene.lighting.lights.size < sceneModel.shaderData.maxNumberOfLights) {
            subMenu("Light", Icons.small.light) {
                item("Directional", Icons.small.sun) { scene.addLight(parent, LightTypeData.Directional(), position) }
                item("Spot", Icons.small.spotLight) { scene.addLight(parent, LightTypeData.Spot(), position) }
                item("Point", Icons.small.light) { scene.addLight(parent, LightTypeData.Point(), position) }
            }
        }
    }
    subMenu("Joint", Icons.small.joint) {
        item("D6") { scene.addJoint(parent, JointData.D6(), position) }
        item("Revolute") { scene.addJoint(parent, JointData.Revolute(), position) }
        item("Spherical") { scene.addJoint(parent, JointData.Spherical(), position) }
        item("Prismatic") { scene.addJoint(parent, JointData.Prismatic(), position) }
        item("Distance") { scene.addJoint(parent, JointData.Distance(), position) }
        item("Fixed") { scene.addJoint(parent, JointData.Fixed, position) }
    }
    item("Camera", Icons.small.camera) { scene.addCamera(parent, position) }
    item("Empty node", Icons.small.emptyObject) { scene.addEmptyNode(parent, position) }
}