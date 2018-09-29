package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.MutableColor

/**
 * @author fabmax
 */
class Slider(name: String, min: Float, max: Float, value: Float, root: UiRoot) :
        UiComponent(name, root), InputManager.DragHandler {

    var onValueChanged: List<Slider.(Float) -> Unit> = mutableListOf()

    var trackColor = Color.GRAY
    val trackColorHighlighted = ThemeOrCustomProp(Color.LIGHT_GRAY)
    val knobColor = ThemeOrCustomProp(Color.WHITE)

    var trackWidth = 0f
    var knobSize = 0f
    var knobPosition = MutableVec2f()

    var min = min
        set(value) {
            if (value != field) {
                field = value
                requestUiUpdate()
            }
        }

    var max = max
        set(value) {
            if (value != field) {
                field = value
                requestUiUpdate()
            }
        }

    var value = value
        set(value) {
            if (value != field) {
                field = value.clamp(min, max)
                requestUiUpdate()

                for (i in onValueChanged.indices) {
                    onValueChanged[i](field)
                }
            }
        }

    private var prevHit = MutableVec2f()
    private var hitDelta = MutableVec2f()

    init {
        onHover += { ptr, rt, ctx ->
            val ptX = rt.hitPositionLocal.x - contentBounds.min.x
            val ptY = rt.hitPositionLocal.y - contentBounds.min.y
            hitDelta.set(ptX, ptY).subtract(prevHit)
            prevHit.set(ptX, ptY)

            if (ptr.isLeftButtonEvent && ptr.isLeftButtonDown && isOverKnob(prevHit.x, prevHit.y)) {
                // register drag handler to handle knob movement
                scene?.registerDragHandler(this@Slider)
                hitDelta.set(0f, 0f)
            }
        }
    }

    fun setValue(min: Float, max: Float, value: Float) {
        this.min = min
        this.max = max
        this.value = value
    }

    private fun isOverKnob(x: Float, y: Float): Boolean {
        val dx = x - knobPosition.x
        val dy = y - knobPosition.y
        return dx*dx + dy*dy < knobSize * knobSize
    }

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>, ctx: KoolContext): Int {
        if (dragPtrs.size == 1 && dragPtrs[0].isValid && dragPtrs[0].isLeftButtonDown) {
            // drag event is handled, no other drag handler should do something
            // we use the delta computed in local coordinates in the onHover handler instead of the native pointer
            // delta, which is in screen coords
            value += (hitDelta.x / trackWidth) * (max - min)

            // don't let other drag handlers process the drag event (camera rotation, etc.)
            return InputManager.DragHandler.HANDLED

        } else {
            // knob dragging stopped
            return InputManager.DragHandler.REMOVE_HANDLER
        }
    }

    override fun setThemeProps(ctx: KoolContext) {
        super.setThemeProps(ctx)
        knobColor.setTheme(root.theme.accentColor)
        trackColorHighlighted.setTheme(MutableColor().add(root.theme.accentColor, 0.4f))
    }

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.newSliderUi(this)
    }
}

open class SliderUi(val slider: Slider, val baseUi: ComponentUi) : ComponentUi by baseUi {

    protected val meshData = MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)

    override fun updateComponentAlpha() {
        baseUi.updateComponentAlpha()
        val shader = mesh.shader
        if (shader is BasicShader) {
            shader.alpha = slider.alpha
        }
    }

    override fun createUi(ctx: KoolContext) {
        baseUi.createUi(ctx)

        mesh.shader = basicShader {
            lightModel = LightModel.PHONG_LIGHTING
            colorModel = ColorModel.VERTEX_COLOR
            isAlpha = true
        }
        slider += mesh
    }

    override fun dispose(ctx: KoolContext) {
        baseUi.dispose(ctx)

        mesh.dispose(ctx)
        slider -= mesh
    }

    override fun updateUi(ctx: KoolContext) {
        baseUi.updateUi(ctx)

        slider.knobSize = slider.dp(10f)
        val trackH = slider.dp(6f)
        val x = slider.padding.left.toUnits(slider.width, slider.dpi) + slider.knobSize
        val y = (slider.height - trackH) / 2
        val p = (slider.value - slider.min) / (slider.max - slider.min)
        slider.trackWidth = slider.width - x - slider.knobSize - slider.padding.right.toUnits(slider.width, slider.dpi)
        slider.knobPosition.set(x + slider.trackWidth * p, slider.height / 2f)

        slider.setupBuilder(meshBuilder)
        if (slider.value > slider.min) {
            meshBuilder.color = slider.trackColorHighlighted.apply()
            meshBuilder.rect {
                origin.set(x, y, slider.dp(4f))
                size.set(slider.knobPosition.x - x + trackH, trackH)
                cornerRadius = trackH / 2f
                cornerSteps = 4
            }
        }
        if (slider.value < slider.max) {
            meshBuilder.color = slider.trackColor
            meshBuilder.rect {
                origin.set(slider.knobPosition.x - trackH, y, slider.dp(4f))
                size.set(slider.trackWidth - slider.knobPosition.x + x + trackH, trackH)
                cornerRadius = trackH / 2f
                cornerSteps = 4
            }
        }
        meshBuilder.color = slider.knobColor.apply()
        meshBuilder.circle {
            center.set(slider.knobPosition.x, slider.knobPosition.y, slider.dp(6f))
            radius = slider.knobSize
            steps = 30
        }
    }
}
