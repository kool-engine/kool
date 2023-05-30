package de.fabmax.kool.editor.actions

import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.MaterialHolderComponent
import de.fabmax.kool.editor.model.MaterialModel
import de.fabmax.kool.editor.model.UpdateMaterialComponent

class SetMaterialAction(
    val nodeModel: EditorNodeModel,
    val materialHolder: MaterialHolderComponent,
    val setMaterialModel: MaterialModel?,
) : EditorAction {

    private val prevMaterial = materialHolder.materialModelState.value

    override fun apply() {
        materialHolder.materialModelState.set(setMaterialModel)
        nodeModel.getComponents<UpdateMaterialComponent>().forEach { it.updateMaterial(setMaterialModel) }
    }

    override fun undo() {
        materialHolder.materialModelState.set(prevMaterial)
        nodeModel.getComponents<UpdateMaterialComponent>().forEach { it.updateMaterial(prevMaterial) }
    }
}