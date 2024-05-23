package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetCharControllerPropertiesAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.CharacterControllerComponent
import de.fabmax.kool.modules.ui2.*

class CharacterControllerEditor : ComponentEditor<CharacterControllerComponent>() {

    override fun UiScope.compose() = componentPanel(
        title = "Character Controller",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            val charProps = components.map { it.charControllerState.use() }

            labeledDoubleTextField(
                label = "Radius:",
                value = condenseDouble(charProps.map { it.shape.radius }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val props = components.map { it.charControllerState.value }
                    components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, props[i].shape.radius)
                        val mergedApply = mergeDouble(apply, props[i].shape.radius)
                        val undoProps = props[i].copy(shape = props[i].shape.copy(radius = mergedUndo))
                        val applyProps = props[i].copy(shape = props[i].shape.copy(radius = mergedApply))
                        SetCharControllerPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                    }.fused()
                }
            )
            labeledDoubleTextField(
                label = "Height:",
                value = condenseDouble(charProps.map { it.shape.length }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val props = components.map { it.charControllerState.value }
                    components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, props[i].shape.length)
                        val mergedApply = mergeDouble(apply, props[i].shape.length)
                        val undoProps = props[i].copy(shape = props[i].shape.copy(length = mergedUndo))
                        val applyProps = props[i].copy(shape = props[i].shape.copy(length = mergedApply))
                        SetCharControllerPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                    }.fused()
                }
            )

            menuDivider()

            labeledDoubleTextField(
                label = "Walk speed:",
                value = condenseDouble(charProps.map { it.walkSpeed }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val props = components.map { it.charControllerState.value }
                    components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, props[i].walkSpeed)
                        val mergedApply = mergeDouble(apply, props[i].walkSpeed)
                        val undoProps = props[i].copy(walkSpeed = mergedUndo)
                        val applyProps = props[i].copy(walkSpeed = mergedApply)
                        SetCharControllerPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                    }.fused()
                }
            )
            labeledDoubleTextField(
                label = "Run speed:",
                value = condenseDouble(charProps.map { it.runSpeed }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val props = components.map { it.charControllerState.value }
                    components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, props[i].runSpeed)
                        val mergedApply = mergeDouble(apply, props[i].runSpeed)
                        val undoProps = props[i].copy(runSpeed = mergedUndo)
                        val applyProps = props[i].copy(runSpeed = mergedApply)
                        SetCharControllerPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                    }.fused()
                }
            )
            labeledDoubleTextField(
                label = "Crouch speed:",
                value = condenseDouble(charProps.map { it.crouchSpeed }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val props = components.map { it.charControllerState.value }
                    components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, props[i].crouchSpeed)
                        val mergedApply = mergeDouble(apply, props[i].crouchSpeed)
                        val undoProps = props[i].copy(crouchSpeed = mergedUndo)
                        val applyProps = props[i].copy(crouchSpeed = mergedApply)
                        SetCharControllerPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                    }.fused()
                }
            )
            labeledDoubleTextField(
                label = "Jump speed:",
                value = condenseDouble(charProps.map { it.jumpSpeed }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val props = components.map { it.charControllerState.value }
                    components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, props[i].jumpSpeed)
                        val mergedApply = mergeDouble(apply, props[i].jumpSpeed)
                        val undoProps = props[i].copy(jumpSpeed = mergedUndo)
                        val applyProps = props[i].copy(jumpSpeed = mergedApply)
                        SetCharControllerPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                    }.fused()
                }
            )
            labeledDoubleTextField(
                label = "Slope limit:",
                value = condenseDouble(charProps.map { it.slopeLimit }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val props = components.map { it.charControllerState.value }
                    components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, props[i].slopeLimit)
                        val mergedApply = mergeDouble(apply, props[i].slopeLimit)
                        val undoProps = props[i].copy(slopeLimit = mergedUndo)
                        val applyProps = props[i].copy(slopeLimit = mergedApply)
                        SetCharControllerPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                    }.fused()
                }
            )

            menuDivider()

            val isKeyboardCtrl = charProps.all { it.enableDefaultControls }

            labeledCheckbox("Default keyboard controls", isKeyboardCtrl) { enabled ->
                val props = components.map { it.charControllerState.value }
                components.mapIndexed { i, component ->
                    SetCharControllerPropertiesAction(component.nodeModel.nodeId, props[i], props[i].copy(enableDefaultControls = enabled))
                }.fused().apply()
            }
            if (isKeyboardCtrl) {
                labeledCheckbox("Run by default", charProps.all { it.runByDefault }) { enabled ->
                    val props = components.map { it.charControllerState.value }
                    components.mapIndexed { i, component ->
                        SetCharControllerPropertiesAction(component.nodeModel.nodeId, props[i], props[i].copy(runByDefault = enabled))
                    }.fused().apply()
                }
            }
        }
    }

}