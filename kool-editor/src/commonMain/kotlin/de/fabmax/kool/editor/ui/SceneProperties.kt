package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetBackgroundAction
import de.fabmax.kool.editor.data.SceneBackgroundComponentData
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.ecs.SceneBackgroundComponent
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.logI
import kotlin.reflect.KClass

private data class BackgroundTypeOption<T: SceneBackgroundData>(val name: String, val type: KClass<T>, val factory: () -> T) {
    override fun toString() = name
}

private object BackgroundTypeOptions {
    val items = listOf(
        BackgroundTypeOption("Single color", SceneBackgroundData.SingleColor::class) { SceneBackgroundData.SingleColor(MdColor.GREY tone 900) },
        BackgroundTypeOption("HDRI image", SceneBackgroundData.Hdri::class) { SceneBackgroundData.Hdri("") },
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

        Row(width = Grow.Std, height = sizes.lineHeight) {
            modifier.margin(top = sizes.smallGap)
            Text("Type:") {
                modifier
                    .width(sizes.baseSize * 3f)
                    .font(sizes.boldText)
                    .alignY(AlignmentY.Center)
            }

            ComboBox {
                modifier
                    .size(Grow.Std, sizes.lineHeight)
                    .items(BackgroundTypeOptions.items)
                    .selectedIndex(BackgroundTypeOptions.indexOfBackground(sceneModel.sceneBackground.componentData))
                    .onItemSelected {
                        val bgType = BackgroundTypeOptions.items[it]
                        if (!bgType.type.isInstance(sceneModel.sceneBackground.componentData)) {
                            logI { "Set bg: $bgType" }
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
        val oldBg = sceneModel.sceneBackground.componentData
        val newBg = SceneBackgroundComponentData(SceneBackgroundData.SingleColor(color))
        // fixme: apply EditorAction only on end of drag to avoid spamming undo / redo history
        EditorActions.applyAction(
            SetBackgroundAction(sceneModel, oldBg, newBg)
        )
    }
}

private fun UiScope.hdriBgProperties(sceneModel: SceneModel, hdriBg: SceneBackgroundData.Hdri) = Column(
    width = Grow.Std,
    scopeName = "hdriBg"
) {

}
