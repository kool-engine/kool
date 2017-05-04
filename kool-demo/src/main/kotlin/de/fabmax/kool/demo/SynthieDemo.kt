package de.fabmax.kool.demo

import de.fabmax.kool.audio.*
import de.fabmax.kool.platform.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.LineMesh
import de.fabmax.kool.util.MutableColor
import de.fabmax.kool.util.Vec3f

/**
 * @author fabmax
 */

fun synthieScene(): Scene {
    return SynthieScene()
}

private class SynthieScene: Scene() {

    private val shaker = Shaker(60f)
    private val kick = Kick(120f)
    private val snare = Snare(60f)
    private val pad = Pad()
    private val audioGen: AudioGenerator

    private val toneA = Oscillator(Wave.SINE, 440f)
    private val toneB = Oscillator(Wave.SINE, 44f)
    private val toneC = Oscillator(Wave.SINE, 16000f)

    private val waveform = Waveform(2048, 48000)

    init {
        +waveform
        +Heightmap(256, 256)
        +sphericalInputTransform {
            +camera
        }

        toneA.gain = 0.05f
        toneC.gain = 0.01f

        audioGen = Platform.getAudioImpl().newAudioGenerator { t -> nextSample(t) }
        audioGen.enableFftComputation(1024)
    }

    private fun nextSample(t: Double): Float {
        val sample = pad.clockAndPlay(t) + shaker.clockAndPlay(t) + kick.clockAndPlay(t)
        waveform.updateSample(sample)
        return sample
    }

    override fun dispose(ctx: RenderContext) {
        audioGen.stop()
        super.dispose(ctx)
    }

    private inner class Heightmap(val width: Int, val length: Int) : TransformGroup() {
        val quads = colorMesh {
            meshData.usage = GL.DYNAMIC_DRAW
            generator = {
                // Set y-axis as surface normal for all quad vertices
                vertexModFun = {
                    normal.set(Vec3f.Y_AXIS)
                }

                // create width * length degenerated quads
                for (z in 1..length) {
                    for (x in 1..width) {
                        rect { size.set(0f, 0f) }
                    }
                }
            }
        }
        val quadIt = quads.meshData.data[0]
        val tmpColor = MutableColor()
        var zPos = -10000f

        val sampleInterval = .05f
        var nextSample = 0f

        init {
            +quads
            scale(1f / 32f, 1f / 32f, 1f / 32f)
            translate(0f, -32f, -zPos + length / 5f)
        }

        override fun render(ctx: RenderContext) {
            nextSample -= ctx.deltaT

            if (nextSample <= 0) {
                nextSample += Math.max(sampleInterval, -nextSample)

                val freqData = audioGen.getPowerSpectrum()
                for (i in 0..width-1) {
                    val c = (Math.clamp(freqData[i] / 90f, -1f, 0f) + 1f)
                    val h = c * 50f
                    val x = i - width * 0.5f

                    jet(c, tmpColor)

                    quadIt.position.set(x, h, zPos)
                    quadIt.color.set(tmpColor)
                    quadIt.index++

                    quadIt.position.set(x, h, zPos + 0.9f)
                    quadIt.color.set(tmpColor)
                    quadIt.index++

                    quadIt.position.set(x + 0.9f, h, zPos + 0.9f)
                    quadIt.color.set(tmpColor)
                    quadIt.index++

                    quadIt.position.set(x + 0.9f, h, zPos)
                    quadIt.color.set(tmpColor)
                    quadIt.index++
                }
                if (quadIt.index == width * length * 4) {
                    quadIt.index = 0
                }
                zPos += 1f
                if (zPos > 10000) {
                    translate(0f, 0f, zPos + 10000)
                    zPos = -10000f
                }
                quads.meshData.isSyncRequired = true
            }
            translate(0f, 0f, -ctx.deltaT / sampleInterval)

            super.render(ctx)
        }

        private fun jet(value: Float, color: MutableColor) {
            val f = Math.clamp(value, 0f, 1f) * 9f
            val case = f.toInt()
            color.set(0f, 0f, 0f, 1f)
            when (case) {
                0 -> {
                    color.r = 0.2f + f * 0.8f
                }
                1 -> {
                    color.r = 1f
                    color.g = f - 1f
                }
                2 -> {
                    color.r = 3f - f
                    color.g = 1f
                }
                3 -> {
                    color.g = 1f
                    color.b = f - 3f
                }
                4 -> {
                    color.g = 5f - f
                    color.b = 1f
                }
                5 -> {
                    color.r = f - 5f
                    color.b = 1f
                }
                else -> {
                    color.set(Color.MAGENTA)
                }
            }
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
                meshData.usage = GL.DYNAMIC_DRAW
            }
        })
        val items = Array(lines.size, { i -> lines[i].meshData.data[0] })

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

            for (i in 0..points-1) {
                items[lineIdx].index = i
                items[lineIdx].position.y = sampleBuf[pos++] * 2f + 2f
                if (pos >= sampleBuf.size) {
                    pos = 0
                }
            }
        }
    }
}
