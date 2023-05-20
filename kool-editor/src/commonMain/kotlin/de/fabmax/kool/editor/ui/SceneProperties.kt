package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetBackgroundAction
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.ecs.SceneBackgroundComponent
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.pipeline.ibl.ReflectionMapPass
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.launchOnMainThread
import kotlin.reflect.KClass

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

fun UiScope.sceneBackground(sceneModel: SceneModel, sceneBackgroundComponent: SceneBackgroundComponent) = collapsapsablePanel("Scene Background") {
    Column(width = Grow.Std) {
        modifier
            .padding(horizontal = sizes.gap)
            .margin(bottom = sizes.smallGap)

        val selectedIndex = remember(BackgroundTypeOptions.indexOfBackground(sceneBackgroundComponent.componentData))
        labeledCombobox(
            label = "Type:",
            items = BackgroundTypeOptions.items,
            selectedIndex = selectedIndex
        ) {
            if (!it.type.isInstance(sceneModel.sceneBackground.backgroundState.use())) {
                val oldBg = sceneModel.sceneBackground.backgroundState.value

                when (it.type) {
                    SceneBackgroundData.Hdri::class -> {
                        SceneBackgroundData.SingleColor(MdColor.GREY tone 900)
                        KoolEditor.instance.availableAssets.textureAssets.firstOrNull { asset ->
                            asset.name.lowercase().endsWith(".rgbe.png")
                        }?.let { hdriAsset ->
                            launchOnMainThread {
                                sceneModel.sceneBackground.loadedEnvironmentMaps = AppAssets.loadHdriEnvironment(sceneModel.node, hdriAsset.path)
                                val newBg = SceneBackgroundData.Hdri(hdriAsset.path)
                                EditorActions.applyAction(SetBackgroundAction(sceneModel, oldBg, newBg))
                            }
                        }
                    }
                    SceneBackgroundData.SingleColor::class -> {
                        val newBg = SceneBackgroundData.SingleColor(MdColor.GREY tone 900)
                        EditorActions.applyAction(SetBackgroundAction(sceneModel, oldBg, newBg))
                    }
                }
            }
        }

        when (val type = sceneBackgroundComponent.backgroundState.use()) {
            is SceneBackgroundData.Hdri -> hdriBgProperties(sceneModel, type)
            is SceneBackgroundData.SingleColor -> singleColorBgProperties(sceneModel, type)
        }
    }
}

private fun UiScope.singleColorBgProperties(sceneModel: SceneModel, singleColorBg: SceneBackgroundData.SingleColor) = Column(
    width = Grow.Std,
    scopeName = "singleColorBg"
) {
    val bgColor = singleColorBg.color.toColor()
    val hsv = bgColor.toHsv()
    val hue = remember(hsv.x)
    val sat = remember(hsv.y)
    val bri = remember(hsv.z)
    val hexString = remember(bgColor.toHexString(false))

    ColorChooserV(hue, sat, bri, null, hexString) { color ->
        val oldBg = sceneModel.sceneBackground.backgroundState.value
        val newBg = SceneBackgroundData.SingleColor(color)
        // fixme: apply EditorAction only on end of drag to avoid spamming undo / redo history
        EditorActions.applyAction(SetBackgroundAction(sceneModel, oldBg, newBg))
    }
}

private fun UiScope.hdriBgProperties(sceneModel: SceneModel, hdriBg: SceneBackgroundData.Hdri) = Column(
    width = Grow.Std,
    scopeName = "hdriBg"
) {

    val hdriTextures = KoolEditor.instance.availableAssets.textureAssets.use()
        .filter { it.name.lowercase().endsWith(".rgbe.png") }
    val skyLod = remember(hdriBg.skyLod)

    val selectedIndex = remember(hdriTextures.indexOfFirst { it.path == hdriBg.hdriPath })
    labeledCombobox(
        label = "HDRI texture:",
        items = hdriTextures,
        selectedIndex = selectedIndex
    ) {
        if (it.path != hdriBg.hdriPath) {
            launchOnMainThread {
                sceneModel.sceneBackground.loadedEnvironmentMaps = AppAssets.loadHdriEnvironment(sceneModel.node, it.path)
                val oldBg = sceneModel.sceneBackground.backgroundState.value
                val newBg = SceneBackgroundData.Hdri(it.path)
                EditorActions.applyAction(SetBackgroundAction(sceneModel, oldBg, newBg))
            }
        }
    }

    labeledSlider(
        label = "Skybox blurriness:",
        value = skyLod,
        min = 0f,
        max = ReflectionMapPass.REFLECTION_MIP_LEVELS.toFloat(),
        precision = 2
    ) {
        val oldBg = sceneModel.sceneBackground.backgroundState.value as? SceneBackgroundData.Hdri
        if (oldBg != null) {
            val newBg = oldBg.copy(skyLod = skyLod.value)
            EditorActions.applyAction(SetBackgroundAction(sceneModel, oldBg, newBg))
        }
    }
}
