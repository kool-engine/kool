package de.fabmax.kool.editor.ui

import de.fabmax.kool.math.Easing
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MutableColor
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sin

class SceneView(ui: EditorUi) : EditorPanel("Scene View", ui) {

    private var viewBox: UiNode? = null

    private val playStopAnimator = AnimatedFloatBidir(0.2f)
    private val playPauseAnimator = AnimatedFloatBidir(0.2f, initValue = 1f)
    private val playButtonBg = PlayButtonBg()
    private val resetButtonBg = ResetButtonBg()
    private val stopButtonBg = StopButtonBg()

    override val windowSurface: UiSurface = EditorPanelWindow(false) {
        modifier.background(null)

        Column(Grow.Std, Grow.Std) {
            modifier.background(null)
            Row(Grow.Std, sizes.baseSize) {
                modifier
                    .padding(horizontal = sizes.gap - sizes.borderWidth)
                    .backgroundColor(UiColors.titleBg)
            }
            Box(width = Grow.Std, height = Grow.Std) {
                modifier
                    .padding(Dp.ZERO)
                    .background(null)
                viewBox = uiNode
            }
        }

        val btnSzCenter = sizes.baseSize * 1.3f
        val btnSzOuter = sizes.baseSize * 1.1f
        val btnExtent = btnSzOuter * 0.85f

        Box(width = btnExtent * 2 + btnSzOuter, height = btnSzCenter) {
            modifier
                .margin(top = sizes.smallGap * -0.5f)
                .alignX(AlignmentX.Center)

            val p = 1f - Easing.quadRev(playStopAnimator.progressAndUse())
            Button {
                modifier
                    .size(btnSzOuter, btnSzOuter)
                    .alignY(AlignmentY.Center)
                    .margin(start = btnExtent * p)
                    .isClickFeedback(false)
                    .background(resetButtonBg)
                    .onEnter { resetButtonBg.hoverAnimator.start(1f) }
                    .onExit { resetButtonBg.hoverAnimator.start(0f) }
                    .onClick {
                        resetButtonBg.clickAnimator.start()
                    }
                    .border(CircularBorder(colors.background, 2.dp))
            }
            Button {
                modifier
                    .size(btnSzOuter, btnSzOuter)
                    .alignX(AlignmentX.End)
                    .alignY(AlignmentY.Center)
                    .margin(end = btnExtent * p)
                    .isClickFeedback(false)
                    .background(stopButtonBg)
                    .onEnter { stopButtonBg.hoverAnimator.start(1f) }
                    .onExit { stopButtonBg.hoverAnimator.start(0f) }
                    .onClick {
                        stopButtonBg.clickAnimator.start()
                        playStopAnimator.start(0f)
                    }
                    .border(CircularBorder(colors.background, 2.dp))
            }
            Button {
                modifier
                    .size(btnSzCenter, btnSzCenter)
                    .align(AlignmentX.Center, AlignmentY.Center)
                    .isClickFeedback(false)
                    .background(playButtonBg)
                    .onEnter { playButtonBg.hoverAnimator.start(1f) }
                    .onExit { playButtonBg.hoverAnimator.start(0f) }
                    .onClick {
                        playButtonBg.clickAnimator.start()
                        playStopAnimator.start(1f)
                        playPauseAnimator.toggle()
                    }
                    .border(CircularBorder(colors.background, 2.dp))
            }
        }
    }

    init {
        windowSurface.inputMode = UiSurface.InputCaptureMode.CaptureOverBackground
        windowDockable.setFloatingBounds(width = Grow.Std, height = Grow.Std)
    }

    fun applyViewportTo(targetScene: Scene) {
        targetScene.mainRenderPass.useWindowViewport = false
        targetScene.onRenderScene += { ctx ->
            viewBox?.let { box ->
                val x = box.leftPx.roundToInt()
                val w = box.rightPx.roundToInt() - x
                val h = box.bottomPx.roundToInt() - box.topPx.roundToInt()
                val y = ctx.windowHeight - box.bottomPx.roundToInt()
                targetScene.mainRenderPass.viewport.set(x, y, w, h)
            }
        }
    }

    private open inner class SceneButtonBg(val accent: Color) : UiRenderer<UiNode> {
        var fgColor = ColorGradient(EditorUi.EDITOR_THEME_COLORS.onBackgroundAlpha(0.5f), EditorUi.EDITOR_THEME_COLORS.onBackground.mix(accent, 0.9f))
        var bgColor = ColorGradient(UiColors.titleBg.mix(EditorUi.EDITOR_THEME_COLORS.secondaryVariant, 0.5f), EditorUi.EDITOR_THEME_COLORS.secondaryVariant.mix(accent, 0.1f))
        val hoverAnimator = AnimatedFloatBidir(0.15f, 0.3f)
        val clickAnimator = AnimatedFloat(0.15f)

        override fun renderUi(node: UiNode) = node.run {
            val r = widthPx * 0.5f
            val pHover = Easing.smooth(hoverAnimator.progressAndUse())
            val pClick = clickAnimator.progressAndUse()

            getUiPrimitives().localCircle(widthPx * 0.5f, heightPx * 0.5f, r, bgColor.getColor(pHover))
            buttonFg(pHover)
            if (clickAnimator.isActive) {
                getUiPrimitives().localCircle(
                    widthPx * 0.5f, heightPx * 0.5f, r, Color.WHITE.withAlpha(0.7f - pClick * 0.5f)
                )
            }
        }

        open fun UiNode.buttonFg(pHover: Float) { }
    }

    private inner class PlayButtonBg : SceneButtonBg(MdColor.GREEN) {
        private val pPlay = listOf(
            Vec3f(1f, 0f, 0f),
            Vec3f(1f, 0f, 0f),
            Vec3f(cos(120f.toRad()), sin(120f.toRad()), 0f),
            Vec3f(cos(120f.toRad()), 0f, 0f),
        )
        private val pPause = listOf(
            Vec3f(1f, 0.2f, 0f),
            Vec3f(1f, 0.6f, 0f),
            Vec3f(-1f, 0.6f, 0f),
            Vec3f(-1f, 0.2f, 0f),
        )

        init {
            fgColor = ColorGradient(EditorUi.EDITOR_THEME_COLORS.onBackgroundAlpha(0.5f), EditorUi.EDITOR_THEME_COLORS.onBackground)
            bgColor = ColorGradient(UiColors.titleBg.mix(accent, 0.3f), accent)
        }

        override fun UiNode.buttonFg(pHover: Float) {
            val r = innerWidthPx * 0.35f
            val pAnim = Easing.smooth(playPauseAnimator.progressAndUse())

            getPlainBuilder().configured(fgColor.getColor(pHover)) {
                translate(round(widthPx * 0.5f), round(heightPx * 0.5f), 0f)
                rotate(90f * (1f - pAnim), 0f, 0f, 1f)

                val vi = IntArray(4)
                val pa = MutableVec3f()
                val pb = MutableVec3f()
                for (i in 0..3) {
                    pa.set(pPlay[i]).scale(pAnim)
                    pb.set(pPause[i]).scale(1f - pAnim)
                    pa.add(pb)
                    vi[i] = vertex { set(round(pa.x * r), round(pa.y * r), round(pa.z * r)) }
                }
                addTriIndices(vi[0], vi[1], vi[2])
                addTriIndices(vi[0], vi[2], vi[3])

                for (i in 0..3) {
                    pa.set(pPlay[i]).scale(pAnim)
                    pb.set(pPause[i]).scale(1f - pAnim)
                    pa.add(pb)
                    vi[i] = vertex { set(round(pa.x * r), round(pa.y * -r), round(pa.z * r)) }
                }
                addTriIndices(vi[0], vi[1], vi[2])
                addTriIndices(vi[0], vi[2], vi[3])
            }
        }
    }

    private inner class StopButtonBg : SceneButtonBg(MdColor.RED) {
        override fun UiNode.buttonFg(pHover: Float) {
            val r = innerWidthPx * 0.3f
            val s = round(2 * r)
            getUiPrimitives().localRect(round(widthPx * 0.5f - r), round(heightPx * 0.5f - r), s, s, fgColor.getColor(pHover))
        }
    }

    private inner class ResetButtonBg : SceneButtonBg(MdColor.AMBER) {
        override fun UiNode.buttonFg(pHover: Float) {
            val r = innerWidthPx * 0.35f
            val fg = fgColor.getColor(pHover)
            getUiPrimitives().localRect(round(widthPx * 0.5f - r * 1.1f), round(heightPx * 0.5f - r), round(r * 0.4f), round(r * 2f), fg)
            getPlainBuilder().configured(MutableColor(fg).apply { a *= Easing.quadRev(playStopAnimator.value) }) {
                translate(round(widthPx * 0.5f + r * 0.3f), round(heightPx * 0.5f), 0f)
                val i0 = vertex { set(-r, 0f, 0f) }
                val i1 = vertex { set(-cos(120f.toRad()) * r, sin(120f.toRad()) * r, 0f) }
                val i2 = vertex { set(-cos(240f.toRad()) * r, sin(240f.toRad()) * r, 0f) }
                addTriIndices(i0, i1, i2)
            }
        }
    }
}