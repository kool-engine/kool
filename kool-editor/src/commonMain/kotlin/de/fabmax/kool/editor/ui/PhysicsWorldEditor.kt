package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.components.PhysicsWorldComponent
import de.fabmax.kool.modules.ui2.*

class PhysicsWorldEditor(component: PhysicsWorldComponent) : ComponentEditor<PhysicsWorldComponent>(component) {

    override fun UiScope.compose() = componentPanel(
        title = "Physics World",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            labeledCheckbox(
                label = "Continuous collision detection",
                component.physicsWorldState.use().isContinuousCollisionDetection,
            ) {

            }
        }
    }

}