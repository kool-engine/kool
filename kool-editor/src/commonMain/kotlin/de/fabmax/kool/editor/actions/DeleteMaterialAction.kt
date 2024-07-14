package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.components.MaterialReferenceComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.util.launchOnMainThread

class DeleteMaterialAction(
    materialToDelete: MaterialComponent
) : EditorAction {

    private var material = materialToDelete
    private val materialData = materialToDelete.gameEntity.entityData

    private var users: List<MaterialReferenceComponent> = emptyList()

    override fun doAction() {
        val proj = KoolEditor.instance.projectModel
        users = proj.createdScenes.values.flatMap { user ->
            user.getAllComponents<MaterialReferenceComponent>().filter { it.isHoldingMaterial(material) }
        }
        users.forEach { it.setPersistent(it.data.copy(materialId = EntityId.NULL)) }
        KoolEditor.instance.projectModel.removeMaterial(material)
    }

    override fun undoAction() {
        launchOnMainThread {
            material = KoolEditor.instance.projectModel.addMaterial(materialData)
            users.forEach { it.setPersistent(it.data.copy(materialId = material.id)) }
        }
    }
}