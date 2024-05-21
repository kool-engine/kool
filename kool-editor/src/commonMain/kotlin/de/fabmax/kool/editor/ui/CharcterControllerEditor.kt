package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.components.CharacterControllerComponent
import de.fabmax.kool.modules.ui2.*

class CharcterControllerEditor : ComponentEditor<CharacterControllerComponent>() {

    override fun UiScope.compose() = componentPanel(
        title = "Character Controller",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

//            val charProps = component.physicsWorldState.use()

        }
    }

}