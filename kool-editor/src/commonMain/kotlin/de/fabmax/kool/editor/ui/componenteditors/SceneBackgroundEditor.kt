package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.AssetItem
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.SetBackgroundAction
import de.fabmax.kool.editor.components.SceneBackgroundComponent
import de.fabmax.kool.editor.data.ColorData
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.modules.ui2.ColumnScope
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.ibl.ReflectionMapPass
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.logW

class SceneBackgroundEditor : ComponentEditor<SceneBackgroundComponent>() {

    private val editorSingleBgColor = mutableStateOf(MdColor.GREY tone 900)

    private val selectedHdri = mutableStateOf(0)
    private val skyLod = mutableStateOf(2f)

    override fun UiScope.compose() = componentPanel("Scene Background", Icons.small.background) {
        components.forEach { it.dataState.use() }

        choicePropertyEditor(
            choices = typeOptions,
            dataGetter = { it.data },
            valueGetter = { it.typeOption },
            valueSetter = { oldData, newValue ->
                val bg: SceneBackgroundData = when (newValue) {
                    TypeOption.SingleColor -> SceneBackgroundData.SingleColor(ColorData(editorSingleBgColor.value, false))
                    TypeOption.Hdri -> {
                        val hdriTexture = availableHdriTextures().getOrNull(selectedHdri.value)
                        if (hdriTexture == null) {
                            logW { "Unable to set HDR background: No suitable texture found in available assets" }
                            oldData.sceneBackground
                        } else {
                            SceneBackgroundData.Hdri(hdriTexture.path, skyLod.value)
                        }
                    }
                }
                SceneBackgroundComponentData(bg)
            },
            actionMapper = sceneBgActionMapper,
            label = "Type:"
        )

        menuDivider()

        when (val type = component.data.sceneBackground) {
            is SceneBackgroundData.Hdri -> hdriBgProperties(type)
            is SceneBackgroundData.SingleColor -> singleColorBgProperties(type)
        }
    }

    private fun ColumnScope.singleColorBgProperties(singleColorBg: SceneBackgroundData.SingleColor) {
        editorSingleBgColor.set(singleColorBg.color.toColorSrgb())
        labeledColorPicker(
            "Background color:",
            editorSingleBgColor.use(),
            editHandler = ActionValueEditHandler { undoValue, applyValue ->
                val oldBg = SceneBackgroundData.SingleColor(ColorData(undoValue, false))
                val newBg = SceneBackgroundData.SingleColor(ColorData(applyValue, false))
                SetBackgroundAction(entityId, SceneBackgroundComponentData(oldBg), SceneBackgroundComponentData(newBg))
            }
        )
    }

    private fun ColumnScope.hdriBgProperties(hdriBg: SceneBackgroundData.Hdri) {
        val hdriTextures = availableHdriTextures()
        selectedHdri.set(hdriTextures.indexOfFirst { it.path == hdriBg.hdriPath })
        skyLod.set(hdriBg.skyLod)
        labeledCombobox(
            label = "HDRI texture:",
            items = hdriTextures,
            selectedIndex = selectedHdri.use()
        ) {
            if (it.path != hdriBg.hdriPath) {
                val oldBg = component.data.sceneBackground
                val newBg = SceneBackgroundData.Hdri(it.path, skyLod.value)
                SetBackgroundAction(entityId, SceneBackgroundComponentData(oldBg), SceneBackgroundComponentData(newBg)).apply()
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
                SetBackgroundAction(entityId, SceneBackgroundComponentData(oldBg), SceneBackgroundComponentData(newBg))
            }
        )
    }

    private fun UiScope.availableHdriTextures(): List<AssetItem> {
        return KoolEditor.instance.availableAssets.hdriAssets.use()
    }

    private val SceneBackgroundComponentData.typeOption: TypeOption get() = TypeOption.entries.first { it.matches(sceneBackground) }

    private enum class TypeOption(val label: String, val matches: (SceneBackgroundData) -> Boolean) {
        SingleColor("Single color", { it is SceneBackgroundData.SingleColor }),
        Hdri("HDRI image", { it is SceneBackgroundData.Hdri }),
    }

    companion object {
        private val typeOptions = ComboBoxItems(TypeOption.entries) { it.label }
        private val sceneBgActionMapper: (SceneBackgroundComponent, SceneBackgroundComponentData, SceneBackgroundComponentData) -> EditorAction = { component, undoData, applyData ->
            SetBackgroundAction(component.gameEntity.id, undoData, applyData)
        }
    }
}
