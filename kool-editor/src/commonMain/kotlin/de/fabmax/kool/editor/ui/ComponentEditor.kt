package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.RemoveComponentAction
import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.*

abstract class ComponentEditor<T: EditorModelComponent>(var component: T ) : Composable {
    open val nodeModel: NodeModel
        get() = component.nodeModel

    open val sceneModel: SceneModel
        get() = requireNotNull(EditorState.activeScene.value)

    protected fun removeComponent() {
        RemoveComponentAction(nodeModel, component).apply()
    }
}

fun UiScope.componentPanel(
    title: String,
    imageIcon: ImageIconMap.IconImageProvider? = null,
    onRemove: (() -> Unit)? = null,
    headerContent: (RowScope.() -> Unit)? = null,
    block: ColumnScope.() -> Any?
) = collapsapsablePanel(
    title,
    imageIcon,
    headerContent = {
        headerContent?.invoke(this)
        onRemove?.let { remove ->
            if (headerContent == null) {
                Box(width = Grow.Std) { }
            }
            Box {
                var isHovered by remember(false)
                val fgColor = colors.onBackground
                val bgColor = if (isHovered) colors.elevatedComponentBgHovered else null

                modifier
                    .alignY(AlignmentY.Center)
                    .margin(end = sizes.gap * 0.75f)
                    .padding(sizes.smallGap * 0.5f)
                    .onEnter { isHovered = true }
                    .onExit { isHovered = false }
                    .onClick { remove() }

                bgColor?.let {
                    modifier.background(RoundRectBackground(it, sizes.smallGap * 0.5f))
                }

                Image {
                    modifier.iconImage(IconMap.TRASH, fgColor)
                }
            }
        }
    },
    block
)
