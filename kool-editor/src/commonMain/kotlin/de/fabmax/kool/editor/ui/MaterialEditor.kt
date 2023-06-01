package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetMaterialAction
import de.fabmax.kool.editor.actions.UpdateMaterialAction
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.MaterialComponent
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.modules.ui2.*

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
        // shader setting callback functions need to use cast material.shaderData instead of pbrData because otherwise
        // pbrData is captured on first invocation and will never be updated

        (pbrData.baseColor as? ConstColorAttribute)?.let { baseColor ->
            colorSetting("Base color:", baseColor, material) {
                (material.shaderData as PbrShaderData).copy(baseColor = it)
            }
        }
        (pbrData.emission as? ConstColorAttribute)?.let { emission ->
            colorSetting("Emission color:", emission, material) {
                (material.shaderData as PbrShaderData).copy(emission = it)
            }
        }
    }

    private inline fun UiScope.colorSetting(
        label: String,
        colorAttr: ConstColorAttribute,
        material: MaterialData,
        crossinline shaderDataSetter: (ConstColorAttribute) -> MaterialShaderData
    ) {
        labeledColorPicker(
            label,
            colorAttr.color.toColor().toSrgb(),
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val undoMaterial = shaderDataSetter(ConstColorAttribute(ColorData(undoValue.toLinear())))
                val applyMaterial = shaderDataSetter(ConstColorAttribute(ColorData(applyValue.toLinear())))
                UpdateMaterialAction(material, applyMaterial, undoMaterial)
            }
        )
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