package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.*
import de.fabmax.kool.editor.components.ModelComponent
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.MdColor

class ModelEditor : ComponentEditor<ModelComponent>() {

    override fun UiScope.compose() {
        val allTheSameModel = components.all {
            it.data.modelPath == components[0].data.modelPath
        }
        componentPanel(
            title = "Model",
            imageIcon = IconMap.small.tree,
            onRemove = ::removeComponent,
            titleWidth = sizes.baseSize * 2.3f,
            headerContent = {
                val (items, idx) = makeModelItemsAndIndex(allTheSameModel)
                ComboBox {
                    defaultComboBoxStyle()
                    modifier
                        .margin(horizontal = sizes.gap)
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                        .items(items)
                        .selectedIndex(idx)
                        .onItemSelected { index ->
                            if (allTheSameModel || index > 0) {
                                components.mapNotNull { comp ->
                                    items[index].model?.let { SetModelPathAction(comp.gameEntity.id, it.path) }
                                }.fused().apply()
                            }
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
            if (allTheSameModel) {
                val gltf = components[0].gltfState.use() ?: return@componentPanel Unit
                val scenes = gltf.scenes.mapIndexed { i, scene -> SceneOption(scene.name ?: "Scene $i", i) }
                labeledCombobox("Model scene:", scenes, components[0].sceneIndexState.use()) { selected ->
                    components.map {
                        SetModelSceneAction(it.gameEntity.id, selected.index)
                    }.fused().apply()
                }

                val animations = gltf.animationOptions()
                labeledCombobox("Animation:", animations, components[0].animationIndexState.use() + 1) { selected ->
                    components.map {
                        SetModelAnimationAction(it.gameEntity.id, selected.index)
                    }.fused().apply()
                }
            }
        }
    }

    private fun GltfFile.animationOptions(): List<AnimationOption> {
        return listOf(NoneAnimation) + animations.mapIndexed { i, animation ->
            AnimationOption(animation.name ?: "Animation $i", i)
        }
    }

    private fun UiScope.makeModelItemsAndIndex(allTheSame: Boolean): Pair<List<ModelItem>, Int> {
        val items = mutableListOf<ModelItem>()

        if (!allTheSame) {
            items.add(0, ModelItem("", null))
        }

        var index = 0
        KoolEditor.instance.availableAssets.modelAssets.use().forEachIndexed { i, model ->
            if (allTheSame && components[0].data.modelPath == model.path) {
                index = i
            }
            items += ModelItem(model.name, model)
        }
        return items to index
    }

    private class ModelItem(val itemText: String, val model: AssetItem?) {
        override fun toString(): String = itemText
    }

    private data class SceneOption(val name: String, val index: Int) {
        override fun toString(): String = name
    }

    private data class AnimationOption(val name: String, val index: Int) {
        override fun toString(): String = name
    }

    private inner class ModelDndHandler(dropTarget: UiNode) :
        DndHandler(dropTarget, setOf(DndItemFlavor.DndItemModel))
    {
        override fun onMatchingReceive(
            dragItem: EditorDndItem<*>,
            dragPointer: PointerEvent,
            source: DragAndDropHandler<EditorDndItem<*>>?
        ) {
            val dragModelItem = dragItem.get(DndItemFlavor.DndItemModel)
            val actions = components
                .filter { it.modelPathState.value != dragModelItem.path }
                .map { SetModelPathAction(it.gameEntity.id, dragModelItem.path) }
            if (actions.isNotEmpty()) {
                FusedAction(actions).apply()
            }
        }
    }

    companion object {
        private val NoneAnimation = AnimationOption("None", -1)
    }
}