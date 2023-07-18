package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.SetModelAnimationAction
import de.fabmax.kool.editor.actions.SetModelPathAction
import de.fabmax.kool.editor.actions.SetModelSceneAction
import de.fabmax.kool.editor.components.ModelComponent
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.ui2.*

class ModelEditor(component: ModelComponent) : ComponentEditor<ModelComponent>(component) {

    override fun UiScope.compose() = componentPanel(
        title = "Model",
        imageIcon = IconMap.TREE,
        onRemove = ::removeComponent
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.gap)

            val items = KoolEditor.instance.availableAssets.modelAssets.use()
            val selModel = component.modelPathState.use()
            val selIndex = items.indexOfFirst { it.path == selModel }
            labeledCombobox("Model:", items, selIndex) {
                SetModelPathAction(component, it.path).apply()
            }

            val gltf = component.gltfState.use()
            if (gltf != null) {
                menuDivider()

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

    companion object {
        private val NoneAnimation = AnimationOption("None", -1)
    }
}