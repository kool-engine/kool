package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.SetModelAnimationAction
import de.fabmax.kool.editor.actions.SetModelPathAction
import de.fabmax.kool.editor.actions.SetModelSceneAction
import de.fabmax.kool.editor.components.ModelComponent
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.MdColor

class ModelEditor(component: ModelComponent) : ComponentEditor<ModelComponent>(component) {

    override fun UiScope.compose() = componentPanel(
        title = "Model",
        imageIcon = IconMap.small.TREE,
        onRemove = ::removeComponent,
        titleWidth = sizes.baseSize * 2.3f,
        headerContent = {
            val items = KoolEditor.instance.availableAssets.modelAssets.use()
            val selModel = component.modelPathState.use()
            val selIndex = items.indexOfFirst { it.path == selModel }
            ComboBox {
                defaultComboBoxStyle()
                modifier
                    .margin(horizontal = sizes.gap)
                    .size(Grow.Std, sizes.lineHeight)
                    .alignY(AlignmentY.Center)
                    .items(items)
                    .selectedIndex(selIndex)
                    .onItemSelected {
                        SetModelPathAction(component, items[it].path).apply()
                    }

                val handler = remember { ModelDndHandler(uiNode) }
                KoolEditor.instance.ui.dndController.registerHandler(handler, surface)

                if (handler.isDrag.use()) {
                    val w = if (handler.isHovered.use()) 0.5f else 0.3f
                    modifier
                        .border(RoundRectBorder(MdColor.GREEN, sizes.smallGap, sizes.borderWidth))
                        .colors(
                            textBackgroundColor = colors.componentBg.mix(MdColor.GREEN, w),
                            textBackgroundHoverColor = colors.componentBgHovered.mix(MdColor.GREEN, w),
                            expanderColor = colors.elevatedComponentBg.mix(MdColor.GREEN, w),
                            expanderHoverColor = colors.elevatedComponentBgHovered.mix(MdColor.GREEN, w)
                        )
                }
            }
        }
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.gap)

            val gltf = component.gltfState.use()
            if (gltf != null) {
                val scenes = gltf.scenes.mapIndexed { i, scene -> SceneOption(scene.name ?: "Scene $i", i) }
                labeledCombobox("Scene:", scenes, component.sceneIndexState.use()) {
                    SetModelSceneAction(component, it.index).apply()
                }

                val animations = gltf.animationOptions()
                labeledCombobox("Animation:", animations, component.animationIndexState.use() + 1) {
                    SetModelAnimationAction(component, it.index).apply()
                }
            }
        }
    }

    private fun GltfFile.animationOptions(): List<AnimationOption> {
        return listOf(NoneAnimation) + animations.mapIndexed { i, animation ->
            AnimationOption(animation.name ?: "Animation $i", i)
        }
    }

    private data class SceneOption(val name: String, val index: Int) {
        override fun toString(): String = name
    }

    private data class AnimationOption(val name: String, val index: Int) {
        override fun toString(): String = name
    }

    private inner class ModelDndHandler(dropTarget: UiNode) :
        DndHandler(dropTarget, setOf(DndItemFlavor.ASSET_ITEM_MODEL))
    {
        override fun onMatchingReceive(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) {
            val dragModelItem = dragItem.get(DndItemFlavor.ASSET_ITEM_MODEL)
            if (dragModelItem.path != component.modelPathState.value) {
                SetModelPathAction(component, dragModelItem.path).apply()
            }
        }
    }

    companion object {
        private val NoneAnimation = AnimationOption("None", -1)
    }
}