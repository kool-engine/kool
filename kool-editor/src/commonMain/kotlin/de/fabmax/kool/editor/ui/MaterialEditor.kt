package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetMaterialAction
import de.fabmax.kool.editor.actions.UpdateMaterialAction
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.ConstColorAttribute
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.model.MaterialHolderComponent
import de.fabmax.kool.editor.model.MaterialModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.model.updateMaterial
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color

class MaterialEditor(var sceneNodeModel: SceneNodeModel, var materialHolder: MaterialHolderComponent) : Composable {

    private var undoMaterial: MaterialData? = null

    override fun UiScope.compose() = collapsapsablePanel(
        title = "Material",
        scopeName = "material-${materialHolder.materialData?.id}"
    ) {

        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            val (items, idx) = makeMaterialItemsAndIndex()
            val selectedIndex = remember(idx)
            selectedIndex.set(idx)
            labeledCombobox("Material:", items, selectedIndex) { item ->
                EditorActions.applyAction(SetMaterialAction(sceneNodeModel, materialHolder, item.getMaterialModel()))
            }

            materialHolder.materialModelState.use()?.let { selectedMaterial ->
                menuDivider()
                materialEditor(selectedMaterial)
            }
        }
    }

    private fun UiScope.materialEditor(material: MaterialModel) {
        val materialData = material.materialState.use()
        val baseColor = materialData.baseColor as? ConstColorAttribute
        if (baseColor != null) {
            val colorState = remember(baseColor.color.toColor().toSrgb() as Color)
            labeledColorPicker(
                "Base color:",
                colorState,
                onShow = { undoMaterial = materialData },
                onHide = { color ->
                    undoMaterial?.let {
                        val applyColor = ConstColorAttribute(ColorData(color.toLinear()))
                        val applyMaterial = materialData.copy(baseColor = applyColor)
                        EditorActions.applyAction(UpdateMaterialAction(material, applyMaterial, it))
                    }
                }
            ) { color ->
                val previewBaseColor = ConstColorAttribute(ColorData(color.toLinear()))
                material.materialState.set(materialData.copy(baseColor = previewBaseColor))
                EditorState.projectModel.updateMaterial(material)
            }
        }
    }

    private fun makeMaterialItemsAndIndex(): Pair<List<MaterialItem>, Int> {
        val items = mutableListOf(
            MaterialItem("None", null),
            MaterialItem("New material", null)
        )
        var index = 0
        EditorState.projectModel.materials.values.forEachIndexed { i, material ->
            if (materialHolder.isHoldingMaterial(material)) {
                index = i + 2
            }
            items += MaterialItem(material.materialState.value.name, material)
        }
        return items to index
    }

    private class MaterialItem(val itemText: String, val material: MaterialModel?) {
        override fun toString(): String = itemText

        fun getMaterialModel(): MaterialModel? {
            return material ?: if (itemText == "New material") EditorState.projectModel.createNewMaterial() else null
        }
    }
}