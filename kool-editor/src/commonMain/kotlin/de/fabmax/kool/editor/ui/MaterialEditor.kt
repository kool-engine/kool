package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetMaterialAction
import de.fabmax.kool.editor.actions.UpdateMaterialAction
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.MaterialComponent
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.model.updateMaterial
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color

class MaterialEditor(var sceneNodeModel: SceneNodeModel, var materialComponent: MaterialComponent) : Composable {

    private var undoMaterial: MaterialShaderData? = null

    override fun UiScope.compose() = collapsapsablePanel(
        title = "Material",
        scopeName = "material-${materialComponent.materialData?.id}"
    ) {

        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            val (items, idx) = makeMaterialItemsAndIndex()
            val selectedIndex = remember(idx)
            selectedIndex.set(idx)
            labeledCombobox("Material:", items, selectedIndex) { item ->
                EditorActions.applyAction(SetMaterialAction(sceneNodeModel, materialComponent, item.getMaterialModel()))
            }

            materialComponent.materialState.use()?.let { selectedMaterial ->
                menuDivider()
                materialEditor(selectedMaterial)
            }
        }
    }

    private fun UiScope.materialEditor(material: MaterialData) {
        when (val shaderData = material.shaderData) {
            is BlinnPhongShaderData -> TODO()
            is PbrShaderData -> pbrMaterialEditor(material, shaderData)
            is UnlitShaderData -> TODO()
        }
    }

    private fun UiScope.pbrMaterialEditor(material: MaterialData, pbrData: PbrShaderData) {
        val baseColor = pbrData.baseColor as? ConstColorAttribute
        if (baseColor != null) {
            val colorState = remember(baseColor.color.toColor().toSrgb() as Color)
            labeledColorPicker(
                "Base color:",
                colorState,
                onShow = { undoMaterial = material.shaderData },
                onHide = { color ->
                    undoMaterial?.let {
                        val applyColor = ConstColorAttribute(ColorData(color.toLinear()))
                        val applyMaterial = pbrData.copy(baseColor = applyColor)
                        EditorActions.applyAction(UpdateMaterialAction(material, applyMaterial, it))
                    }
                }
            ) { color ->
                val previewBaseColor = ConstColorAttribute(ColorData(color.toLinear()))
                material.shaderData = pbrData.copy(baseColor = previewBaseColor)
                EditorState.projectModel.updateMaterial(material)
            }
        }
    }

    private fun makeMaterialItemsAndIndex(): Pair<List<MaterialItem>, Int> {
        val items = mutableListOf(
            MaterialItem("Default", null),
            MaterialItem("New material", null)
        )
        var index = 0
        EditorState.projectModel.materials.values.forEachIndexed { i, material ->
            if (materialComponent.isHoldingMaterial(material)) {
                index = i + 2
            }
            items += MaterialItem(material.name, material)
        }
        return items to index
    }

    private class MaterialItem(val itemText: String, val material: MaterialData?) {
        override fun toString(): String = itemText

        fun getMaterialModel(): MaterialData? {
            return material ?: if (itemText == "New material") EditorState.projectModel.createNewMaterial() else null
        }
    }
}