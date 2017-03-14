package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */
class Slider(name: String, min: Float, max: Float, value: Float) : UiComponent(name), InputManager.DragHandler {

    var onValueChanged: List<Slider.(Float) -> Unit> = mutableListOf()

    var trackColor = Color.GRAY
    val trackColorHighlighted = ThemeOrCustomProp(Color.LIGHT_GRAY)
    val knobColor = ThemeOrCustomProp(Color.WHITE)

    var min = min
        set(value) {
            if (value != field) {
                field = value
                isFgUpdateNeeded = true
            }
        }

    var max = max
        set(value) {
            if (value != field) {
                field = value
                isFgUpdateNeeded = true
            }
        }

    var value = value
        set(value) {
            if (value != field) {
                field = Math.clamp(value, min, max)
                isFgUpdateNeeded = true

                for (i in onValueChanged.indices) {
                    onValueChanged[i](value)
                }
            }
        }

    private val meshData = MeshData(true, true, true)
    private val meshBuilder = MeshBuilder(meshData)
    private val mesh = Mesh(meshData)
    private var meshAdded = false

    private var trackWidth = 0f
    private var knobR = 0f
    private var knobPos = MutableVec2f()

    private var prevHit = MutableVec2f()
    private var hitDelta = MutableVec2f()

    init {
        mesh.shader = basicShader {
            lightModel = LightModel.PHONG_LIGHTING
            colorModel = ColorModel.VERTEX_COLOR
            isAlpha = true
        }

        onHover += { ptr, rt, ctx ->
            hitDelta.set(rt.hitPositionLocal.x, rt.hitPositionLocal.y).subtract(prevHit)
            prevHit.set(rt.hitPositionLocal.x, rt.hitPositionLocal.y)

            if (ptr.isLeftButtonEvent && ptr.isLeftButtonDown && isOverKnob(prevHit.x, prevHit.y)) {
                // register drag handler to handle knob movement
                ctx.inputMgr.registerDragHandler(this@Slider)
            }
        }
        onHoverExit += { _,_,_ ->
            hitDelta.set(0f, 0f)
        }
    }

    private fun isOverKnob(x: Float, y: Float): Boolean {
        val dx = x - knobPos.x
        val dy = y - knobPos.y
        return dx*dx + dy*dy < knobR*knobR
    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>): Int {
        if (dragPtrs.size == 1 && dragPtrs[0].isValid && dragPtrs[0].isLeftButtonDown) {
            // drag event is handled, no other drag handler should do something
            // we use the delta computed in local coordinates in the onHover handler instead of the native pointer
            // delta, which is in screen coords
            value += (hitDelta.x / trackWidth) * (max - min)

            // don't process the drag event any further (camera rotation, etc.)
            return InputManager.DragHandler.HANDLED

        } else {
            // knob dragging stopped
            return InputManager.DragHandler.REMOVE_HANDLER
        }
    }

    override fun applyComponentAlpha() {
        super.applyComponentAlpha()
        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.alpha = alpha
        }
    }

    override fun updateForeground(ctx: RenderContext) {
        super.updateForeground(ctx)
        if (!meshAdded) {
            meshAdded = true
            this += mesh
        }

        knobR = dp(10f)
        val trackH = dp(6f)
        val x = padding.left.toUnits(width, dpi) + knobR
        val y = (height - trackH) / 2
        trackWidth = width - x - knobR - padding.right.toUnits(width, dpi)
        knobPos.set(x + trackWidth * (value - min) / (max - min), height / 2f)

        setupBuilder(meshBuilder)
        if (value > min) {
            meshBuilder.color = trackColorHighlighted.apply()
            meshBuilder.rect {
                origin.set(x, y, 0f)
                width = knobPos.x - x + trackH
                height = trackH
                cornerRadius = trackH / 2f
                cornerSteps = 4
            }
        }
        if (value < max) {
            meshBuilder.color = trackColor
            meshBuilder.rect {
                origin.set(knobPos.x - trackH, y, 0f)
                width = trackWidth - knobPos.x + x + trackH
                height = trackH
                cornerRadius = trackH / 2f
                cornerSteps = 4
            }
        }
        meshBuilder.color = knobColor.apply()
        meshBuilder.circle {
            center.set(knobPos.x, knobPos.y, 0f)
            radius = knobR
            steps = 30
        }

    }

    override fun applyTheme(theme: UiTheme, ctx: RenderContext) {
        super.applyTheme(theme, ctx)
        knobColor.setTheme(theme.accentColor)
        trackColorHighlighted.setTheme(MutableColor().add(theme.accentColor, 0.4f))
    }
}
