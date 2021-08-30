package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.MutableColor

/**
 * @author fabmax
 */
class Slider(name: String, min: Float, max: Float, value: Float, root: UiRoot) :
        UiComponent(name, root), Scene.DragHandler {

    val onValueChanged: MutableList<Slider.(Float) -> Unit> = mutableListOf()
    val onDragFinished: MutableList<Slider.(Float) -> Unit> = mutableListOf()

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

    private val pickRay = Ray()
    private val hitPlane = Plane()
    private val hitPos = MutableVec3f()
    private val initHitPos = MutableVec3f()
    private var startDrag = false
    private var startDragValue = 0f

    init {
        hitPlane.n.set(Vec3f.Z_AXIS)
        onHover += { ptr, rt, _ ->
            val ptX = rt.hitPositionLocal.x - componentBounds.min.x
            val ptY = rt.hitPositionLocal.y - componentBounds.min.y
            if (ptr.isLeftButtonEvent && ptr.isLeftButtonDown && isOverKnob(ptX, ptY)) {
                // register drag handler to handle knob movement
                root.scene.registerDragHandler(this@Slider)
                startDrag = true
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

    override fun handleDrag(dragPtrs: List<InputManager.Pointer>, scene: Scene, ctx: KoolContext) {
        if (dragPtrs.size == 1 && !dragPtrs[0].isConsumed() && dragPtrs[0].isLeftButtonDown &&
                computeLocalPickRay(dragPtrs[0], ctx, pickRay)) {
            if (hitPlane.intersectionPoint(pickRay, hitPos)) {
                if (startDrag) {
                    startDrag = false
                    initHitPos.set(hitPos)
                    startDragValue = value
                }
                val deltaX = hitPos.x - initHitPos.x
                value = startDragValue + (deltaX / trackWidth) * (max - min)
            }
            dragPtrs[0].consume()

        } else {
            // knob dragging stopped
            scene.removeDragHandler(this)
            onDragFinished.forEach { it.invoke(this, value) }
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

    protected val geom = IndexedVertexList(UiShader.UI_MESH_ATTRIBS)
    protected val meshBuilder = MeshBuilder(geom)
    protected val mesh = Mesh(geom)
    protected val shader = UiShader()

    override fun updateComponentAlpha() {
        baseUi.updateComponentAlpha()
        shader.alpha(slider.alpha)
    }

    override fun createUi(ctx: KoolContext) {
        baseUi.createUi(ctx)
        mesh.shader = shader
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
                origin.set(x, y, slider.dp(.1f))
                size.set(slider.knobPosition.x - x + trackH, trackH)
                cornerRadius = trackH / 2f
                cornerSteps = 4
                zeroTexCoords()
            }
        }
        if (slider.value < slider.max) {
            meshBuilder.color = slider.trackColor
            meshBuilder.rect {
                origin.set(slider.knobPosition.x - trackH, y, slider.dp(.1f))
                size.set(slider.trackWidth - slider.knobPosition.x + x + trackH, trackH)
                cornerRadius = trackH / 2f
                cornerSteps = 4
                zeroTexCoords()
            }
        }
        meshBuilder.color = slider.knobColor.apply()
        meshBuilder.circle {
            zeroTexCoords()
            center.set(slider.knobPosition.x, slider.knobPosition.y, slider.dp(.2f))
            radius = slider.knobSize
            steps = 30
        }
    }

    override fun onRender(ctx: KoolContext) {
//        mesh.shader?.setDrawBounds(slider.drawBounds)
        baseUi.onRender(ctx)
    }
}
