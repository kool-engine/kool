package de.fabmax.kool.demo

import de.fabmax.kool.audio.*
import de.fabmax.kool.platform.*
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.LineMesh

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
    private val audioGen: AudioGenerator

    private val toneA = Oscillator(Wave.SINE, 440f)
    private val toneB = Oscillator(Wave.SINE, 44f)
    private val toneC = Oscillator(Wave.SINE, 16000f)

    private val waveform = Waveform(512, 48000)

    init {
        +waveform
        +sphericalInputTransform {
            +camera
        }

        toneA.gain = 0.05f
        toneC.gain = 0.01f

        audioGen = Platform.getAudioImpl().newAudioGenerator { t -> nextSample(t) }
        audioGen.enableFftComputation(1024)
    }

    private fun nextSample(t: Double): Float {
        val sample = shaker.clockAndPlay(t) + kick.clockAndPlay(t)
        waveform.updateSample(sample)
        return sample
    }

    override fun dispose(ctx: RenderContext) {
        audioGen.stop()
        super.dispose(ctx)
    }


    private inner class Waveform(val points: Int, val sampleRate: Int) : Group() {

        val lines = Array(5, {
            LineMesh().apply {
                +this
                for (i in 1..points) {
                    val idx = meshData.addVertex {
                        position.set((i - points/2) / 256f, 0f, 0f)
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

                //drawTimeDomain()
                drawFreqDomain()

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

        private fun drawFreqDomain() {
            val freqData = audioGen.getPowerSpectrum()

            for (i in 0..freqData.capacity-1) {
                if (i >= points) {
                    break
                }
                items[lineIdx].index = i
                items[lineIdx].position.y = Math.clamp(freqData[i] / 90f, -1f, 0f) + 1f
                //items[lineIdx].position.y = freqData[i]
            }
        }

        private fun drawTimeDomain() {
            val end = ((playT * sampleRate) % sampleBuf.size).toInt()
            var pos = end - points
            if (pos < 0) {
                pos += sampleBuf.size
            }

            for (i in 0..points-1) {
                items[lineIdx].index = i
                items[lineIdx].position.y = sampleBuf[pos++] * 2f
                if (pos >= sampleBuf.size) {
                    pos = 0
                }
            }
        }
    }
}
