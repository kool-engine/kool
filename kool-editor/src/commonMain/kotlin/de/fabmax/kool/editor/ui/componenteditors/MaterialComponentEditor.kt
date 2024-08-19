package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.modules.ui2.*
import kotlin.reflect.KClass

class MaterialComponentEditor : ComponentEditor<MaterialComponent>() {

    override fun UiScope.compose() = Column(Grow.Std, Grow.Std) {
        if (components.size > 1) {
            Text("Multiple materials selected") {
                modifier
                    .size(Grow.Std, sizes.baseSize)
                    .alignY(AlignmentY.Center)
                    .textAlignX(AlignmentX.Center)
                    .font(sizes.italicText)
            }
            return@Column
        }

        menuRow {
            Text("Name:") {
                modifier
                    .alignY(AlignmentY.Center)
                    .width(sizes.editorLabelWidthSmall)
            }

            var editName by remember(component.name)
            TextField(editName) {
                if (!isFocused.use()) {
                    editName = component.name
                }
                defaultTextfieldStyle()
                modifier
                    .hint("Name")
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
                    .padding(vertical = sizes.smallGap)
                    .onChange { editName = it }
                    .onEnterPressed {
                        SetComponentDataAction(component, component.data, component.data.copy(name = it)).apply()
                        surface.unfocus(this)
                    }
            }
        }

        componentPanel(
            title = "Material",
            imageIcon = Icons.small.palette,
            onRemove = ::removeComponent,
            titleWidth = sizes.baseSize * 2.3f,
            scopeName = component.name,
            headerContent = {
                ComboBox {
                    defaultComboBoxStyle()

                    modifier
                        .margin(end = sizes.gap)
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                        .items(materialTypes)
                        .selectedIndex(materialTypes.indexOfFirst { it.matches(component.shaderData) })
                        .onItemSelected { index ->
                            val oldData = component.data
                            val newData = oldData.copy(shaderData = materialTypes[index].factory())
                            SetComponentDataAction(component, oldData, newData).apply()
                        }
                }
            }
        ) {
            component.dataState.use()
            materialEditor()
        }
    }

    private fun ColumnScope.materialEditor() = Column(width = Grow.Std, scopeName = component.shaderData::class.simpleName) {
        val matEditor: MaterialDataEditor<*> = when (val matData = component.shaderData) {
            is BlinnPhongShaderData -> TODO()
            is PbrShaderData -> {
                remember { PbrMaterialEditor(component, matData, this@MaterialComponentEditor) }
                    .apply { materialData = matData }
            }
            is UnlitShaderData -> TODO()
            is PbrSplatShaderData -> {
                remember { PbrSplatMaterialEditor(component, matData, this@MaterialComponentEditor) }
                    .apply { materialData = matData }
            }
        }
        with(matEditor) { materialEditor() }
    }

    private class MaterialTypeOption<T: MaterialShaderData>(val label: String, val dataType: KClass<T>, val factory: () -> T) {
        fun matches(data: MaterialShaderData): Boolean = dataType.isInstance(data)
        override fun toString() = label
    }

    companion object {
        private val materialTypes = listOf(
            MaterialTypeOption("PBR", PbrShaderData::class) { PbrShaderData() },
            MaterialTypeOption("Splatted PBR", PbrSplatShaderData::class) { PbrSplatShaderData() },
        )
    }
}