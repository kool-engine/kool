package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetShadowMapTypeAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.ShadowMapComponent
import de.fabmax.kool.editor.data.ShadowMapInfo
import de.fabmax.kool.editor.data.ShadowMapTypeData
import de.fabmax.kool.modules.ui2.*

class ShadowMapEditor : ComponentEditor<ShadowMapComponent>() {
    override fun UiScope.compose() = componentPanel("Shadow Map", IconMap.small.shadow, ::removeComponent) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            val shadowMapTypes = components.map { it.shadowMapState.use().typeOption }
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
                        SetShadowMapTypeAction(it.nodeModel.nodeId, shadowMap)
                    }.fused().apply()
                }
            }
        }
    }

    private val ShadowMapTypeData.typeOption: TypeOption get() =
        TypeOption.entries.first { it.matches(this) }

    private enum class TypeOption(val label: String, val matches: (ShadowMapTypeData?) -> Boolean) {
        Single("Single", { it is ShadowMapTypeData.Single }),
        Cascaded("Cascaded", { it is ShadowMapTypeData.Cascaded }),
    }

    companion object {
        private val typeOptions = ComboBoxItems(TypeOption.entries) { it.label }
    }
}