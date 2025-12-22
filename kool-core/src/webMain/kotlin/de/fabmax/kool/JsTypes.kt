package de.fabmax.kool

import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import kotlin.js.*

fun JsNumber?.toFloat(default: Float = 0f) = this?.toDouble()?.toFloat() ?: default

fun IntArray.toUint32Array(): Uint32Array = Uint32Array(size).also {
    for (i in indices) {
        it[i] = this[i]
    }
}

fun Uint32Array.toIntArray(): IntArray = IntArray(length).also {
    for (i in 0 until length) {
        it[i] = this[i]
    }
}

fun Array<String>.toJsArray(): JsArray<JsString> {
    val js = JsArray<JsString>()
    for (i in indices) {
        js[i] = this[i].toJsString()
    }
    return js
}

external interface Gamepad : JsAny {
    val axes: JsArray<out JsNumber>
    val buttons: JsArray<out GamepadButton>
    val connected: Boolean
    val id: String
    val index: Int
    val mapping: String
    val timestamp: Double
}

external interface GamepadButton : JsAny {
    val pressed: Boolean
    val touched: Boolean
    val value: Double
}

external class AudioContext : JsAny {
    val sampleRate: Float
    val destination: AudioDestinationNode

    fun createBuffer(numberOfChannels: Int, length: Int, sampleRate: Float): AudioBuffer
    fun createBufferSource(): AudioBufferSourceNode
    fun createScriptProcessor(bufferSize: Int, numberOfInputChannels: Int, numberOfOutputChannels: Int): ScriptProcessorNode
}

external interface ScriptProcessorNode : AudioNode, JsAny {
    val bufferSize: Int
    var onaudioprocess: ((AudioProcessingEvent) -> Unit)
}

external interface AudioProcessingEvent : JsAny {
    val outputBuffer: AudioBuffer
}

external interface AudioBuffer : JsAny {
    val length: Int
    fun getChannelData(channel: Int): Float32Array
}

external interface AudioBufferSourceNode : AudioNode, JsAny {
    var buffer: AudioBuffer?
    var loop: Boolean

    fun start()
    fun stop()
}

external interface AudioNode : JsAny {
    fun connect(node: AudioNode)
    fun disconnect()
}

external interface AudioDestinationNode : AudioNode, JsAny
