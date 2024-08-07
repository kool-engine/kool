package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.components.MaterialComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.ui.Icons
import de.fabmax.kool.editor.ui.baseSize
import de.fabmax.kool.editor.ui.defaultComboBoxStyle
import de.fabmax.kool.editor.ui.italicText
import de.fabmax.kool.modules.ui2.*
import kotlin.reflect.KClass

class MaterialComponentEditor : ComponentEditor<MaterialComponent>() {

    override fun UiScope.compose() {
        if (components.size > 1) {
            Text("Multiple materials selected") {
                modifier
                    .size(Grow.Std, sizes.baseSize)
                    .alignY(AlignmentY.Center)
                    .textAlignX(AlignmentX.Center)
                    .font(sizes.italicText)
            }
            return
        }

        componentPanel(
            title = "Material",
            imageIcon = Icons.small.palette,
            onRemove = ::removeComponent,
            titleWidth = sizes.baseSize * 2.3f,
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
        matEditor.materialEditor()
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