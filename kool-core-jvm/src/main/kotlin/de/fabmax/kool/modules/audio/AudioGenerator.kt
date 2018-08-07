package de.fabmax.kool.modules.audio

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.createFloat32Buffer
import org.jtransforms.fft.FloatFFT_1D
import org.jtransforms.utils.CommonUtils
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.concurrent.thread

/**
 * @author fabmax
 */

actual class AudioGenerator actual constructor(ctx: KoolContext, generatorFun: AudioGenerator.(Float) -> Float) {

    private val pauseLock = java.lang.Object()
    private var isStopRequested = false

    private var fftHelper: FftHelper? = null

    actual val sampleRate = 48000f
    actual var isPaused: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                synchronized(pauseLock) {
                    pauseLock.notify()
                }
            }
        }

    init {
        CommonUtils.setThreadsBeginN_1D_FFT_2Threads(16384)
        CommonUtils.setThreadsBeginN_1D_FFT_4Threads(16384 * 2)

        thread(start = true, isDaemon = true) {
            val sampleRate = 48000f
            val format = AudioFormat(sampleRate, 16, 1, true, true)
            val line = AudioSystem.getSourceDataLine(format)
            line.open(format)
            line.start()

            val numSamples = 256
            val buf = ByteBuffer.allocate(numSamples * 2)
            val samples = buf.asShortBuffer()
            var sampleIdx = 0L

            var startTime = System.currentTimeMillis()
            val dt = 1f / sampleRate

            while (!isStopRequested) {
                samples.rewind()
                for (i in 0..numSamples-1) {
                    var f = generatorFun(dt)
                    if (f > 1f) { f = 1f }
                    if (f < -1f) { f = -1f }
                    samples.put((f * 32767).toShort())
                    fftHelper?.putSample(f)
                    sampleIdx++
                }

                val data = buf.array()
                line.write(data, 0, data.size)

                // don't generate too many samples in advance...
                val played = (sampleIdx / sampleRate) * 1000
                val sleepT = startTime + played.toLong() - System.currentTimeMillis() - 100
                if (sleepT > 0) {
                    Thread.sleep(sleepT)
                }


                if (isPaused) {
                    val t = System.currentTimeMillis()
                    synchronized (pauseLock) {
                        pauseLock.wait()
                    }
                    startTime += System.currentTimeMillis() - t
                }
            }
        }
    }

    actual fun stop() {
        isStopRequested = true
    }

    actual fun enableFftComputation(nSamples: Int) {
        if (nSamples <= 0) {
            fftHelper = null
        } else {
            fftHelper = FftHelper(nSamples)
        }
    }

    actual fun getPowerSpectrum(): Float32Buffer {
        return fftHelper?.getOutput() ?: createFloat32Buffer(1)
    }
}

private class FftHelper(val nPoints: Int) {
    private val fft = FloatFFT_1D(nPoints.toLong())
    private val outputBuf = createFloat32Buffer(nPoints / 2)
    private val smoothSpec = FloatArray(nPoints / 2)
    private val samples = FloatArray(nPoints)
    private val blackmanWnd: FloatArray
    private var writeIdx = 0

    var smoothFac = 0.5f

    init {
        val alpha = 0.16
        val a0 = (1.0 - alpha) / 2.0
        val a1 = 0.5
        val a2 = alpha / 2.0
        val pi = Math.PI

        blackmanWnd = FloatArray(nPoints, { i ->
            (a0 - a1 * Math.cos(2 * pi * i / nPoints) + a2 * Math.cos(4 * pi * i / nPoints)).toFloat()
        })
    }

    fun putSample(sample: Float) {
        samples[writeIdx] = sample * blackmanWnd[writeIdx]
        if (++writeIdx == nPoints) {
            writeIdx = 0
            fft.realForward(samples)
            updateSpectrum()
        }
    }

    fun updateSpectrum() {
        synchronized (smoothSpec) {
            for (i in smoothSpec.indices) {
                val re = samples[i * 2]
                val im = samples[i * 2 + 1]
                val x = Math.sqrt(re * re + im * im.toDouble()).toFloat() / nPoints
                smoothSpec[i] = smoothSpec[i] * smoothFac + x * (1 - smoothFac)
            }
        }
    }

    fun getOutput(): Float32Buffer {
        synchronized (smoothSpec) {
            for (i in smoothSpec.indices) {
                outputBuf[i] = 20f * Math.log10(smoothSpec[i].toDouble()).toFloat()
            }
        }
        return outputBuf
    }
}