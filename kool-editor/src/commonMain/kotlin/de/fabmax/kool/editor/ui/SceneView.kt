package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorEditMode
import de.fabmax.kool.editor.actions.deleteNode
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MsdfFont
import de.fabmax.kool.util.Viewport
import kotlin.math.roundToInt

class SceneView(ui: EditorUi) : EditorPanel("Scene View", Icons.medium.camera, ui) {

    val isShowOverlays = mutableStateOf(true)
    val isShowKeyInfo = mutableStateOf(false)
    val toolbar = FloatingToolbar(editor)
    val keyInfo = KeyInfo(ui)

    private var viewBox: UiNode? = null
    private val boxSelector = BoxSelector()

    private val labels = mutableStateListOf<Label>()
    private val contextMenuPos = mutableStateOf<Vec2f?>(null)

    override val windowSurface: UiSurface = editorPanel(false) {
        modifier
            .background(null)
            .isBlocking(false)

        Column(Grow.Std, Grow.Std) {
            modifier.background(null)
            viewBox = uiNode

            if (isShowOverlays.use()) {
                Box(width = Grow.Std, height = Grow.Std) {
                    modifier
                        .padding(Dp.ZERO)
                        .background(null)

                    if (editor.editMode.mode.use() == EditorEditMode.Mode.BOX_SELECT) {
                        boxSelector()
                    } else {
                        boxSelector.isBoxSelect.set(false)
                    }
                }
            }
        }

        if (isShowOverlays.use()) {
            drawLabels(labels.use())

            toolbar()
            if (isShowKeyInfo.use()) {
                keyInfo()
            }

            val itemPopupMenu = remember { ContextPopupMenu<GameEntity?>("scene-popup") }
            contextMenuPos.use()?.let {
                surface.isFocused.set(true)
                val selectedObject = editor.selectionOverlay.selection.firstOrNull()
                itemPopupMenu.show(it, makeContextMenu(), selectedObject)
                contextMenuPos.set(null)
            }
            itemPopupMenu()
        }
    }

    init {
        windowSurface.inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
        windowDockable.setFloatingBounds(width = Grow.Std, height = Grow.Std)
    }

    fun addLabel(label: Label) {
        if (label !in labels) {
            labels += label
        }
    }

    fun removeLabel(label: Label) {
        labels -= label
    }

    fun showSceneContextMenu(pointer: Pointer) {
        contextMenuPos.set(pointer.pos)
    }

    private fun makeContextMenu() = SubMenuItem {
        val selection = editor.selectionOverlay.selection
        menuItems += addSceneObjectMenu("Add object", selection.firstOrNull()?.parent)
        if (selection.size == 1 && !selection.first().isSceneRoot) {
            divider()
            item("Focus object", Icons.small.circleCrosshair) { editor.focusObject(it) }
            item("Delete object", Icons.small.trash) { deleteNode(it) }
        }
    }

    private fun UiScope.drawLabels(labels: List<Label>) {
        if (labels.isEmpty()) {
            return
        }

        val msdf = sizes.normalText as MsdfFont
        val font = msdf.copy(weight = MsdfFont.WEIGHT_BOLD, sizePts = msdf.sizePts * 0.8f)
        val bgColor = Color.WHITE.withAlpha(0.7f)
        val fgColor = MdColor.GREY tone 800
        val r = sizes.gap * 1.2f

        for (lbl in labels) {
            if (lbl.isVisible.use()) {
                Text(lbl.text.use()) {
                    modifier
                        .height(r * 2f)
                        .onMeasured { lbl.offsetX.set(Dp.fromPx(it.rightPx - it.leftPx) * 0.5f) }
                        .margin(start = lbl.x.use() - lbl.offsetX.use(), top = lbl.y.use() - r)
                        .background(RoundRectBackground(bgColor, r))
                        .border(RoundRectBorder(fgColor, r, 2.dp))
                        .padding(horizontal = sizes.gap)
                        .textColor(fgColor)
                        .font(font)
                }
            }
        }
    }

    fun applyViewportTo(targetScene: Scene) {
        targetScene.mainRenderPass.isFillFrame = false
        targetScene.onRenderScene += {
            viewBox?.let { box ->
                val x = box.leftPx.roundToInt()
                val w = box.rightPx.roundToInt() - x
                val y = box.topPx.roundToInt()
                val h = box.bottomPx.roundToInt() - y
                if (!targetScene.mainRenderPass.viewport.equals(x, y, w, h)) {
                    targetScene.mainRenderPass.viewport = Viewport(x, y, w, h)
                }
            }
        }
    }

    class Label(text: String = "", x: Dp = Dp.ZERO, y: Dp = Dp.ZERO, isVisible: Boolean = true) {
        val text = mutableStateOf(text)
        val x = mutableStateOf(x)
        val y = mutableStateOf(y)
        val isVisible = mutableStateOf(isVisible)

        val offsetX = mutableStateOf(Dp.ZERO)
    }
}