package de.fabmax.kool.demo

import de.fabmax.kool.audio.Kick
import de.fabmax.kool.audio.Oscillator
import de.fabmax.kool.audio.Wave
import de.fabmax.kool.platform.PlatformImpl
import kotlin.js.Math

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    Demo(PlatformImpl.initContext())
}


private fun audioTest() {
    val audioCtx = js("new (window.AudioContext || window.webkitAudioContext)();")
    val sampleRate: Int = audioCtx.sampleRate
    val samples = sampleRate * 1
    println("sampleRate: $sampleRate")

//    val buffer = audioCtx.createBuffer(1, samples, sampleRate)
//    val data = buffer.getChannelData(0)
//    for (i in 0..samples-1) {
//        data[i] = Math.sin(i * 440 * 2 * Math.PI / sampleRate) * 0.5
//    }
//    val src = audioCtx.createBufferSource()
//    src.buffer = buffer
//    src.connect(audioCtx.destination)
//    src.start()

    val scriptNode = audioCtx.createScriptProcessor(8192, 1, 1)
    val buffer = audioCtx.createBuffer(1, scriptNode.bufferSize, sampleRate)

//    val osc = Oscillator(Wave.TRIANGLE_WAVE, sampleRate.toFloat())
//    osc.amplitude = 0.1f
    val kick = Kick(sampleRate.toFloat())
    var n = 0.0

    scriptNode.onaudioprocess = { ev: dynamic ->
        val outputBuffer = ev.outputBuffer

        for (c in 0..outputBuffer.numberOfChannels-1) {
            val data = outputBuffer.getChannelData(c)
            for (i in 0..outputBuffer.length - 1) {
                data[i] = kick.clockAndPlay(n++ / sampleRate)
            }
        }
    }

    val source = audioCtx.createBufferSource()
    source.buffer = buffer
    source.loop = false
    source.connect(scriptNode)
    scriptNode.connect(audioCtx.destination)
    source.start()
}