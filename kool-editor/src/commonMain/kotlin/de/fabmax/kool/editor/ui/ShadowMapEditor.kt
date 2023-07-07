package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.components.ShadowMapComponent
import de.fabmax.kool.editor.data.ShadowMapInfo
import de.fabmax.kool.editor.data.ShadowMapTypeData
import de.fabmax.kool.modules.ui2.Column
import de.fabmax.kool.modules.ui2.Grow
import de.fabmax.kool.modules.ui2.UiScope
import kotlin.reflect.KClass

class ShadowMapEditor(override var component: ShadowMapComponent) : ComponentEditor<ShadowMapComponent> {
    override fun UiScope.compose() = collapsapsablePanel("Shadow Map") {
        Column(width = Grow.Std) {
            val shadowMap = component.shadowMapState.use()
            val selected = typeOptions.indexOfFirst { it.shadowMapType.isInstance(shadowMap) }
            labeledCombobox("Type:", typeOptions, selected) {
                component.shadowMapState.set(it.create())
            }
        }
    }

    companion object {
        private val typeOptions = listOf(
            ShadowMapOption("Single", ShadowMapTypeData.Single::class) { ShadowMapTypeData.Single(ShadowMapInfo()) },
            ShadowMapOption("Cascaded", ShadowMapTypeData.Cascaded::class) {
                ShadowMapTypeData.Cascaded(listOf(
                    ShadowMapInfo(rangeNear = 0.001f, rangeFar = 0.05f),
                    ShadowMapInfo(rangeNear = 0.05f, rangeFar = 0.25f),
                    ShadowMapInfo(rangeNear = 0.25f, rangeFar = 1f)
                ))
            },
        )
    }

    private data class ShadowMapOption<T: ShadowMapTypeData>(
        val name: String,
        val shadowMapType: KClass<T>,
        val create: () -> T
    ) {
        override fun toString(): String {
            return name
        }
    }
}