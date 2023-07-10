package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.SetBackgroundAction
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.components.SceneBackgroundComponent
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.ibl.ReflectionMapPass
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.launchOnMainThread
import kotlin.reflect.KClass

class SceneBackgroundEditor(component: SceneBackgroundComponent) :
    ComponentEditor<SceneBackgroundComponent>(component)
{

    private val editorSingleBgColor = mutableStateOf(MdColor.GREY tone 900)

    private val selectedHdri = mutableStateOf(0)
    private val skyLod = mutableStateOf(2f)

    override fun UiScope.compose() = collapsapsablePanel("Scene Background") {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.gap)

            var selectedIndex by remember(0)
            selectedIndex = BackgroundTypeOptions.indexOfBackground(component.componentData)
            labeledCombobox(
                label = "Type:",
                items = BackgroundTypeOptions.items,
                selectedIndex = selectedIndex
            ) {
                if (!it.type.isInstance(component.backgroundState.use())) {
                    when (it.type) {
                        SceneBackgroundData.Hdri::class -> selectHdriBackground()
                        SceneBackgroundData.SingleColor::class -> selectSingleColorBackground()
                    }
                }
            }

            menuDivider()

            when (val type = component.backgroundState.use()) {
                is SceneBackgroundData.Hdri -> hdriBgProperties(type)
                is SceneBackgroundData.SingleColor -> singleColorBgProperties(type)
            }
        }
    }

    private fun UiScope.selectHdriBackground() {
        val hdriTexture = availableHdriTextures().getOrNull(selectedHdri.value) ?: return
        val oldBg = component.backgroundState.value
        val newBg = SceneBackgroundData.Hdri(hdriTexture.path, skyLod.value)
        SetBackgroundAction(component, oldBg, newBg).apply()
    }

    private fun selectSingleColorBackground() {
        val oldBg = component.backgroundState.value
        val newBg = SceneBackgroundData.SingleColor(ColorData(editorSingleBgColor.value, false))
        SetBackgroundAction(component, oldBg, newBg).apply()
    }

    private fun UiScope.singleColorBgProperties(singleColorBg: SceneBackgroundData.SingleColor) = Column(
        width = Grow.Std,
        scopeName = "singleColorBg"
    ) {
        editorSingleBgColor.set(singleColorBg.color.toColorSrgb())
        labeledColorPicker(
            "Background color:",
            editorSingleBgColor.use(),
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val oldBg = SceneBackgroundData.SingleColor(ColorData(undoValue, false))
                val newBg = SceneBackgroundData.SingleColor(ColorData(applyValue, false))
                SetBackgroundAction(component, oldBg, newBg)
            }
        )
    }

    private fun UiScope.hdriBgProperties(hdriBg: SceneBackgroundData.Hdri) = Column(
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
                    sceneModel.shaderData.environmentMaps = AppAssets.loadHdriEnvironment(component.sceneModel.drawNode, it.path)
                    val oldBg = component.backgroundState.value
                    val newBg = SceneBackgroundData.Hdri(it.path, skyLod.value)
                    SetBackgroundAction(component, oldBg, newBg).apply()
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
                SetBackgroundAction(component, oldBg, newBg)
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
