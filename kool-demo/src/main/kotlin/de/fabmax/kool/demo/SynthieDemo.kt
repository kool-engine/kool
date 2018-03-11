package de.fabmax.kool.demo

import de.fabmax.kool.InputManager
import de.fabmax.kool.RenderContext
import de.fabmax.kool.audio.*
import de.fabmax.kool.gl.GL_DYNAMIC_DRAW
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.*
import kotlin.math.abs
import kotlin.math.max

/**
 * @author fabmax
 */

fun synthieScene(ctx: RenderContext): List<Scene> {
    val content = SynthieScene()
    val menu = synthieMenu(content, ctx)
    return listOf(content, menu)
}

private fun synthieMenu(content: SynthieScene, ctx: RenderContext): Scene = uiScene(ctx.screenDpi) {
    theme = theme(UiTheme.DARK) {
        componentUi { BlankComponentUi() }
        containerUi { BlankComponentUi() }
    }

    val menu = container("menu") {
        layoutSpec.setOrigin(zero(), zero(), zero())
        layoutSpec.setSize(pcs(100f), dps(260f), zero())
        ui.setCustom(SimpleComponentUi(this))

        +container("sequencer") {
            layoutSpec.setOrigin(pcs(50f) - dps(240f), dps(10f), zero())
            layoutSpec.setSize(dps(320f), pcs(100f), zero())

            for (col in content.melody.sequence.indices) {
                for (row in (0..15).reversed()) {
                    +SequenceButton(col, row, content.melody, this@uiScene)
                }
            }
        }

        +VerticalLayout("volumes", this@uiScene).apply {
            layoutSpec.setOrigin(pcs(50f, true) + dps(250f, true), dps(10f, true), zero())
            layoutSpec.setSize(dps(240f, true), dps(160f, true), zero())
            //ui.setCustom(SimpleComponentUi(this).apply { color.setCustom(Color.RED) })

            +label("meloLbl") {
                layoutSpec.setOrigin(dps(5f), dps(120f, true), zero())
                layoutSpec.setSize(dps(70f, true), dps(40f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                padding = Margin(zero(), zero(), zero(), zero())
                text = "Melody"
            }
            +slider("melo") {
                layoutSpec.setOrigin(dps(70f), dps(120f, true), zero())
                layoutSpec.setSize(dps(170f), dps(40f), zero())
                padding = Margin(zero(), zero(), zero(), zero())

                onValueChanged += { value ->
                    content.melody.gain = value / 50f
                }
            }

            +label("padLbl") {
                layoutSpec.setOrigin(dps(5f), dps(80f, true), zero())
                layoutSpec.setSize(dps(70f, true), dps(40f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                padding = Margin(zero(), zero(), zero(), zero())
                text = "Pad"
            }
            +slider("pad") {
                layoutSpec.setOrigin(dps(70f), dps(80f, true), zero())
                layoutSpec.setSize(dps(170f), dps(40f), zero())
                padding = Margin(zero(), zero(), zero(), zero())

                onValueChanged += { value ->
                    content.pad.gain = value / 50f
                }
            }

            +label("shkLbl") {
                layoutSpec.setOrigin(dps(5f), dps(40f, true), zero())
                layoutSpec.setSize(dps(70f, true), dps(40f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                padding = Margin(zero(), zero(), zero(), zero())
                text = "Shaker"
            }
            +slider("shk") {
                layoutSpec.setOrigin(dps(70f), dps(40f), zero())
                layoutSpec.setSize(dps(170f), dps(40f), zero())
                padding = Margin(zero(), zero(), zero(), zero())

                onValueChanged += { value ->
                    content.shaker.gain = value / 50f
                }
            }

            +label("kickLbl") {
                layoutSpec.setOrigin(dps(5f), zero(), zero())
                layoutSpec.setSize(dps(70f, true), dps(40f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                padding = Margin(zero(), zero(), zero(), zero())
                text = "Kick"
            }
            +slider("kick") {
                layoutSpec.setOrigin(dps(70f), zero(), zero())
                layoutSpec.setSize(dps(170f), dps(40f), zero())
                padding = Margin(zero(), zero(), zero(), zero())

                onValueChanged += { value ->
                    content.kick.gain = value / 50f
                }
            }
        }

        onHoverEnter += { _, _, _ ->
            // disable mouse interaction on content scene while pointer is over menu
            content.isPickingEnabled = false
        }
        onHoverExit += { _, rt, _->
            if (!rt.isHit) {
                // enable mouse interaction on content scene when pointer leaves menu (and nothing else in this scene
                // is hit instead)
                content.isPickingEnabled = true
            }
        }
    }
    +menu
}

/**
 * Super-hacky UI Container which is rotated by 90 degrees
 */
private class VerticalLayout(name: String, root: UiRoot) : UiContainer(name, root) {
    override fun doLayout(bounds: BoundingBox, ctx: RenderContext) {
        if (bounds != contentBounds) {
            contentBounds.clear()
        }
        super.doLayout(bounds, ctx)
        if (bounds != contentBounds) {
            translate(-posInParent.x, -posInParent.y, -posInParent.z)
            rotate(90f, Vec3f.Z_AXIS)
            translate(posInParent.y, -posInParent.x, posInParent.z)
        }
    }
}

private class Melody : SampleNode() {
    val sequence = arrayOf(
            0, 0, 0, 5, 3, 3, 3, 8,
            0, 0, 0, 8, 8, 10, 3, 15
    )
    var index = 0f

    private val lfo1 = Oscillator(Wave.SINE, 1f / 32f).apply { gain = 140f }
    private val lfo2 = Oscillator(Wave.SINE, 0.5f).apply { gain = 0.2f; phaseShift = 0.5f }

    private val osc1 = Oscillator(Wave.SAW).apply { gain = 0.7f }
    private val osc2 = Oscillator(Wave.SQUARE).apply { gain = 0.4f }
    private val osc3 = Oscillator(Wave.SINE).apply { gain = 0.8f }
    private val osc4 = Oscillator(Wave.SQUARE).apply { gain = 1.2f }

    private val moodFilter = MoodFilter(this)

    override fun generate(dt: Float): Float {
        val p = t * 4
        val r = p - p.toInt()
        val i = p.toInt() % sequence.size
        index = (i + r).toFloat()
        val n = sequence[i]

        //val n = sequence[(t * 4).toInt() % sequence.size]
        var osc = 0f
        if (n >= 0) {
            val f = note(n + 7, 0)
            osc = osc1.next(dt, f) + osc2.next(dt, f / 2f) + osc3.next(dt, f / 2f) + osc4.next(dt, f * 3f)
        }
        return moodFilter.filter(lfo1.next(dt) + 1050, lfo2.next(dt), perc(osc, 48f, (t % 0.125).toFloat()), dt) * 0.25f
    }
}

private class SequenceButton(val col: Int, val row: Int, val melody: Melody, root: UiRoot) :
        Button("seq-$col-$row", root) {
    private val background = SequenceButtonUi(this)
    private val colorAnimator = CosAnimator(InterpolatedColor(MutableColor(Color.WHITE.withAlpha(0.2f)),
            MutableColor(Color.LIME.withAlpha(0.6f))))
    private var wasHovered = false

    init {
        layoutSpec.setOrigin(dps(col*20f), dps(row*15f), zero())
        layoutSpec.setSize(dps(18f), dps(13f), zero())
        ui.setCustom(background)

        colorAnimator.duration = 0.3f
        colorAnimator.speed = -1f

        onHover += { ptr, _, _ -> onHover(ptr) }
        onHoverExit += { _,_,_ -> wasHovered = false }
    }

    private fun onHover(ptr: InputManager.Pointer) {
        if (ptr.isLeftButtonEvent && ptr.isLeftButtonDown) {
            // button was explicitly clicked -> toggle state
            if (melody.sequence[col] == row) {
                melody.sequence[col] = -1
            } else {
                melody.sequence[col] = row
            }
        } else if (!wasHovered && ptr.isLeftButtonDown) {
            // button was hovered with mouse pressed, enable it (and do nothing if it already is enabled)
            melody.sequence[col] = row
        }
        wasHovered = true
    }

    override fun render(ctx: RenderContext) {
        if (melody.sequence[col] == row) {
            colorAnimator.speed = 1f
        } else {
            colorAnimator.speed = -1f
        }
        colorAnimator.tick(ctx)
        background.bgColor.set(colorAnimator.value.value)
        if (isHovered) {
            background.bgColor.a += 0.4f
        } else {
            val a = melody.index - col
            if (a > 0 && a <= 1) {
                background.bgColor.a += (0.5f - abs(a - 0.5f)).clamp(0f, 0.1f)
            }
        }

        super.render(ctx)
    }
}

private class SequenceButtonUi(btn: SequenceButton) : SimpleComponentUi(btn) {
    val bgColor = MutableColor()

    override fun onRender(ctx: RenderContext) {
        (shader as BasicShader).staticColor.set(bgColor)
        super.onRender(ctx)
    }
}

private class SynthieScene: Scene() {

    val melody = Melody()
    val shaker = Shaker(60f)
    val kick = Kick(120f)
    val pad = Pad()

    private val audioGen: AudioGenerator
    private val waveform = Waveform(2048, 48000)

    init {
        +waveform
        +Heightmap(256, 256)
        +sphericalInputTransform {
            +camera
            setMouseRotation(20f, -20f)
            zoom = 8f
        }

        audioGen = AudioGenerator { dt -> nextSample(dt) }
        audioGen.enableFftComputation(1024)
    }

    private fun nextSample(dt: Float): Float {
        val sample = kick.next(dt) + shaker.next(dt) + pad.next(dt) + melody.next(dt)
        waveform.updateSample(sample)
        return sample
    }

    override fun dispose(ctx: RenderContext) {
        audioGen.stop()
        super.dispose(ctx)
    }

    private inner class Heightmap(val width: Int, val length: Int) : TransformGroup() {
        val quads = colorMesh {
            isFrustumChecked = false
            meshData.usage = GL_DYNAMIC_DRAW
            generator = {
                // Set y-axis as surface normal for all quad vertices
                vertexModFun = {
                    normal.set(Vec3f.Y_AXIS)
                }

                // create width * length degenerated quads
                for (z in 1..length) {
                    for (x in 1..width) {
                        rect {
                            size.set(0f, 0f)
                        }
                    }
                }
            }
        }
        val quadV = quads.meshData[0]
        var zPos = -10000f

        val sampleInterval = .05f
        var nextSample = 0f

        init {
            +quads
        }

        override fun render(ctx: RenderContext) {
            nextSample -= ctx.deltaT.toFloat()

            if (nextSample <= 0) {
                nextSample += max(sampleInterval, -nextSample)

                val freqData = audioGen.getPowerSpectrum()
                for (i in 0 until width) {
                    val c = (freqData[i] / 90f).clamp(-1f, 0f) + 1f
                    val h = c * 50f
                    val x = i - width * 0.5f
                    val color = ColorGradient.VIRIDIS.getColor(c + 0.05f, 0f, 0.7f)

                    quadV.position.set(x, h, zPos)
                    quadV.color.set(color)
                    quadV.index++

                    quadV.position.set(x, h, zPos + 0.9f)
                    quadV.color.set(color)
                    quadV.index++

                    quadV.position.set(x + 0.9f, h, zPos + 0.9f)
                    quadV.color.set(color)
                    quadV.index++

                    quadV.position.set(x + 0.9f, h, zPos)
                    quadV.color.set(color)
                    quadV.index++
                }
                if (quadV.index == width * length * 4) {
                    quadV.index = 0
                }
                zPos += 1f
                if (zPos > 10000) {
                    zPos = -10000f
                }
                quads.meshData.isSyncRequired = true
            }

            setIdentity()
            scale(1f / 32f, 1f / 32f, 1f / 32f)
            translate(0f, -32f, -zPos + length / 5f)

            super.render(ctx)
        }
    }

    private inner class Waveform(val points: Int, val sampleRate: Int) : Group() {
        val lines = Array(5, {
            LineMesh().apply {
                +this
                for (i in 1..points) {
                    val idx = meshData.addVertex {
                        position.set((i - points/2) / 256f, 1f, 0f)
                    }
                    if (i > 1) {
                        meshData.addIndices(idx - 1, idx)
                    }
                }
                lineWidth = 1f
                shader = basicShader {
                    colorModel = ColorModel.STATIC_COLOR
                    lightModel = LightModel.NO_LIGHTING
                }
                (shader as BasicShader).staticColor.set(Color.LIME)
                meshData.usage = GL_DYNAMIC_DRAW
            }
        })
        val vertices = Array(lines.size, { i -> lines[i].meshData[0] })

        val sampleBuf = FloatArray(sampleRate)
        var sampleIdx = 0
        var updateFrms = 2
        var playT = 0.0
        var lineIdx = 0

        fun updateSample(value: Float) {
            sampleBuf[sampleIdx++] = value
            if (sampleIdx == sampleBuf.size) {
                sampleIdx = 0
            }
        }

        override fun render(ctx: RenderContext) {
            playT += ctx.deltaT

            if (--updateFrms == 0) {
                updateFrms = 2
                lineIdx = (lineIdx + 1) % lines.size

                drawTimeDomain()

                lines[lineIdx].meshData.isSyncRequired = true
                for (i in lines.indices) {
                    var idx = (lineIdx - i)
                    if (idx < 0) {
                        idx += lines.size
                    }
                    (lines[idx].shader as BasicShader).staticColor.w = 1f - i / lines.size.toFloat()
                }
            }

            super.render(ctx)
        }

        private fun drawTimeDomain() {
            val end = ((playT * sampleRate) % sampleBuf.size).toInt()
            var pos = end - points
            if (pos < 0) {
                pos += sampleBuf.size
            }

            for (i in 0 until points) {
                vertices[lineIdx].index = i
                vertices[lineIdx].position.y = sampleBuf[pos++] * 2f + 2f
                if (pos >= sampleBuf.size) {
                    pos = 0
                }
            }
        }
    }
}
