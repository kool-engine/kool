package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.RemoveComponentAction
import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.data.NodeId
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.util.sceneModel
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.MdColor

abstract class ComponentEditor<T: EditorModelComponent>() : Composable {
    var components: List<T> = emptyList()
    val component: T get() = components[0]

    val nodeId: NodeId get() = component.nodeModel.nodeId
    val nodeModel: NodeModel get() = component.nodeModel
    val sceneModel: SceneModel get() = nodeModel.sceneModel

    protected fun removeComponent() {
        RemoveComponentAction(nodeId, component).apply()
    }
}

fun UiScope.componentPanel(
    title: String,
    imageIcon: IconProvider? = null,
    onRemove: (() -> Unit)? = null,
    titleWidth: Dimension = Grow.Std,
    headerContent: (RowScope.() -> Unit)? = null,
    block: ColumnScope.() -> Any?
) = collapsapsablePanel(
    title,
    imageIcon,
    titleWidth = titleWidth,
    headerContent = {
        headerContent?.invoke(this)
        onRemove?.let { remove ->
            Box {
                var isHovered by remember(false)
                val fgColor = colors.onBackground
                val bgColor = if (isHovered) MdColor.RED else colors.componentBg

                modifier
                    .alignY(AlignmentY.Center)
                    .margin(end = sizes.gap * 0.75f)
                    .padding(sizes.smallGap * 0.5f)
                    .onEnter { isHovered = true }
                    .onExit { isHovered = false }
                    .onClick { remove() }
                    .background(CircularBackground(bgColor))

                Image {
                    modifier.iconImage(IconMap.small.trash, fgColor)
                }
            }
        }
    },
    block = block
)
