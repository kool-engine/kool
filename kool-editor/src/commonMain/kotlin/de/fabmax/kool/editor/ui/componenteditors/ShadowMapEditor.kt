package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.ShadowMapComponent
import de.fabmax.kool.editor.data.ShadowMapInfo
import de.fabmax.kool.editor.data.ShadowMapTypeData
import de.fabmax.kool.editor.ui.ComboBoxItems
import de.fabmax.kool.editor.ui.Icons
import de.fabmax.kool.editor.ui.labeledCombobox
import de.fabmax.kool.modules.ui2.UiScope

class ShadowMapEditor : ComponentEditor<ShadowMapComponent>() {
    override fun UiScope.compose() = componentPanel("Shadow Map", Icons.small.shadow, ::removeComponent) {
        val shadowMapTypes = components.map { it.dataState.use().shadowMap.typeOption }
        val (typeItems, typeIdx) = typeOptions.getOptionsAndIndex(shadowMapTypes)
        labeledCombobox("Type:", typeItems, typeIdx) { selected ->
            selected.item?.let { type ->
                val shadowMap = when (type) {
                    TypeOption.Single -> ShadowMapTypeData.Single(ShadowMapInfo())
                    TypeOption.Cascaded -> ShadowMapTypeData.Cascaded(
                        listOf(
                            ShadowMapInfo(rangeNear = 0.001f, rangeFar = 0.05f),
                            ShadowMapInfo(rangeNear = 0.05f, rangeFar = 0.25f),
                            ShadowMapInfo(rangeNear = 0.25f, rangeFar = 1f)
                        )
                    )
                }
                components.map {
                    setShadowMapAction(it, it.data.shadowMap, shadowMap)
                }.fused().apply()
            }
        }
    }

    private fun setShadowMapAction(component: ShadowMapComponent, oldShadow: ShadowMapTypeData, newShadow: ShadowMapTypeData) =
        SetComponentDataAction(component, component.data.copy(shadowMap = oldShadow), component.data.copy(shadowMap = newShadow))

    private val ShadowMapTypeData.typeOption: TypeOption
        get() = TypeOption.entries.first { it.matches(this) }

    private enum class TypeOption(val label: String, val matches: (ShadowMapTypeData?) -> Boolean) {
        Single("Single", { it is ShadowMapTypeData.Single }),
        Cascaded("Cascaded", { it is ShadowMapTypeData.Cascaded }),
    }

    companion object {
        private val typeOptions = ComboBoxItems(TypeOption.entries) { it.label }
    }
}