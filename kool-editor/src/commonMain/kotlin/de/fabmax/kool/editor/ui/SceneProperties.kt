package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetBackgroundAction
import de.fabmax.kool.editor.data.SceneBackgroundData
import de.fabmax.kool.editor.model.MScene
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

    fun indexOfBackground(background: SceneBackgroundData): Int {
        return when (background) {
            is SceneBackgroundData.SingleColor -> 0
            is SceneBackgroundData.Hdri -> 1
        }
    }
}

fun UiScope.sceneBackground(sceneModel: MScene) = collapsapsablePanel("Scene Background") {
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
                    .selectedIndex(BackgroundTypeOptions.indexOfBackground(sceneModel.sceneData.background))
                    .onItemSelected {
                        val bgType = BackgroundTypeOptions.items[it]
                        if (!bgType.type.isInstance(sceneModel.sceneData.background)) {
                            //EditorActions.applyAction(SetShapeAction(nodeModel, nodeModel.shape, shapeType.factory()))
                            logI { "Set bg: $bgType" }
                        }
                    }
            }
        }

        when (val type = sceneModel.backgroundMutableState.use()) {
            is SceneBackgroundData.Hdri -> hdriBgProperties(sceneModel, type)
            is SceneBackgroundData.SingleColor -> singleColorBgProperties(sceneModel, type)
        }
    }
}

private fun UiScope.singleColorBgProperties(sceneModel: MScene, singleColorBg: SceneBackgroundData.SingleColor) = Column(
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
        val bg = SceneBackgroundData.SingleColor(color)
        sceneModel.backgroundMutableState.set(bg)
        EditorActions.applyAction(
            SetBackgroundAction(sceneModel, bg)
        )
    }
}

private fun UiScope.hdriBgProperties(sceneModel: MScene, hdriBg: SceneBackgroundData.Hdri) = Column(
    width = Grow.Std,
    scopeName = "hdriBg"
) {

}
