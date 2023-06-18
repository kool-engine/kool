package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetBackgroundAction
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.ibl.ReflectionMapPass
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.launchOnMainThread
import kotlin.reflect.KClass

class SceneBackgroundEditor(var sceneModel: SceneModel) : Composable {

    private val editorSingleBgColor = mutableStateOf(MdColor.GREY tone 900)
    private val editorSingleBgColorOld = mutableStateOf(MdColor.GREY tone 900)

    private val selectedHdri = mutableStateOf(0)
    private val skyLod = mutableStateOf(2f)

    override fun UiScope.compose() = collapsapsablePanel("Scene Background") {
        val sceneBackgroundComponent = sceneModel.sceneBackground

        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.gap)

            var selectedIndex by remember(0)
            selectedIndex = BackgroundTypeOptions.indexOfBackground(sceneBackgroundComponent.componentData)
            labeledCombobox(
                label = "Type:",
                items = BackgroundTypeOptions.items,
                selectedIndex = selectedIndex
            ) {
                if (!it.type.isInstance(sceneModel.sceneBackground.backgroundState.use())) {
                    when (it.type) {
                        SceneBackgroundData.Hdri::class -> selectHdriBackground()
                        SceneBackgroundData.SingleColor::class -> selectSingleColorBackground()
                    }
                }
            }

            menuDivider()

            when (val type = sceneBackgroundComponent.backgroundState.use()) {
                is SceneBackgroundData.Hdri -> hdriBgProperties(sceneModel, type)
                is SceneBackgroundData.SingleColor -> singleColorBgProperties(sceneModel, type)
            }
        }
    }

    private fun UiScope.selectHdriBackground() {
        val hdriTexture = availableHdriTextures().getOrNull(selectedHdri.value) ?: return
        val oldBg = sceneModel.sceneBackground.backgroundState.value
        val newBg = SceneBackgroundData.Hdri(hdriTexture.path, skyLod.value)
        EditorActions.applyAction(SetBackgroundAction(sceneModel, oldBg, newBg))
    }

    private fun selectSingleColorBackground() {
        val oldBg = sceneModel.sceneBackground.backgroundState.value
        val newBg = SceneBackgroundData.SingleColor(editorSingleBgColor.value)
        EditorActions.applyAction(SetBackgroundAction(sceneModel, oldBg, newBg))
    }

    private fun UiScope.singleColorBgProperties(sceneModel: SceneModel, singleColorBg: SceneBackgroundData.SingleColor) = Column(
        width = Grow.Std,
        scopeName = "singleColorBg"
    ) {
        editorSingleBgColor.set(singleColorBg.color.toColor())
        labeledColorPicker(
            "Background color:",
            editorSingleBgColor.use(),
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val oldBg = SceneBackgroundData.SingleColor(undoValue)
                val newBg = SceneBackgroundData.SingleColor(applyValue)
                SetBackgroundAction(sceneModel, oldBg, newBg)
            }
        )
    }

    private fun UiScope.hdriBgProperties(sceneModel: SceneModel, hdriBg: SceneBackgroundData.Hdri) = Column(
        width = Grow.Std,
        scopeName = "hdriBg"
    ) {

        val hdriTextures = availableHdriTextures()
        selectedHdri.set(hdriTextures.indexOfFirst { it.path == hdriBg.hdriPath })
        skyLod.set(hdriBg.skyLod)
        labeledCombobox(
            label = "HDRI texture:",
            items = hdriTextures,
            selectedIndex = selectedHdri.use()
        ) {
            if (it.path != hdriBg.hdriPath) {
                launchOnMainThread {
                    sceneModel.sceneBackground.loadedEnvironmentMaps = AppAssets.loadHdriEnvironment(sceneModel.drawNode, it.path)
                    val oldBg = sceneModel.sceneBackground.backgroundState.value
                    val newBg = SceneBackgroundData.Hdri(it.path, skyLod.value)
                    EditorActions.applyAction(SetBackgroundAction(sceneModel, oldBg, newBg))
                }
            }
        }

        labeledDoubleTextField(
            label = "Skybox blurriness:",
            value = skyLod.use().toDouble(),
            precision = 2,
            minValue = 0.0,
            maxValue = ReflectionMapPass.REFLECTION_MIP_LEVELS.toDouble(),
            dragChangeSpeed = DragChangeRates.RANGE_0_TO_1 * ReflectionMapPass.REFLECTION_MIP_LEVELS,
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                skyLod.set(applyValue.toFloat())
                val oldBg = hdriBg.copy(skyLod = undoValue.toFloat())
                val newBg = hdriBg.copy(skyLod = applyValue.toFloat())
                SetBackgroundAction(sceneModel, oldBg, newBg)
            }
        )
    }

    private fun UiScope.availableHdriTextures(): List<AssetItem> {
        return KoolEditor.instance.availableAssets.hdriTextureAssets.use()
    }

    private data class BackgroundTypeOption<T: SceneBackgroundData>(val name: String, val type: KClass<T>) {
        override fun toString() = name
    }

    private object BackgroundTypeOptions {
        val items = listOf(
            BackgroundTypeOption("Single color", SceneBackgroundData.SingleColor::class),
            BackgroundTypeOption("HDRI image", SceneBackgroundData.Hdri::class),
        )

        fun indexOfBackground(background: SceneBackgroundComponentData): Int {
            return when (background.sceneBackground) {
                is SceneBackgroundData.SingleColor -> 0
                is SceneBackgroundData.Hdri -> 1
            }
        }
    }

}
