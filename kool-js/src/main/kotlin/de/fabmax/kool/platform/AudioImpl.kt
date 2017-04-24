package de.fabmax.kool.platform

/**
 * @author fabmax
 */

internal class AudioImpl(private val platform: PlatformImpl) : Audio() {
    override fun newAudioGenerator(generatorFun: AudioGenerator.(Double) -> Float): AudioGenerator {
        return AudioGeneratorImpl(generatorFun)
    }

    private inner class AudioGeneratorImpl(generatorFun: AudioGenerator.(Double) -> Float) : AudioGenerator() {
        override val sampleRate: Float = platform.audioCtx.sampleRate

        override var isPaused: Boolean = false
            set(value) {
                if (field != value) {
                    field = value
                    if (value) {
                        source.stop()
                    } else {
                        source.start()
                    }
                }
            }

        private val source: dynamic
        private val scriptNode: dynamic

        init {
            scriptNode = platform.audioCtx.createScriptProcessor(8192, 1, 1)
            val buffer = platform.audioCtx.createBuffer(1, scriptNode.bufferSize, sampleRate)
            var n = 0

            scriptNode.onaudioprocess = { ev: dynamic ->
                val outputBuffer = ev.outputBuffer
                val data = outputBuffer.getChannelData(0)
                for (i in 0..outputBuffer.length - 1) {
                    data[i] = generatorFun(n++ / sampleRate.toDouble())
                }
            }

            source = platform.audioCtx.createBufferSource()
            source.buffer = buffer
            source.loop = true
            source.connect(scriptNode)
            scriptNode.connect(platform.audioCtx.destination)
            source.start()
        }

        override fun stop() {
            scriptNode.disconnect()
            source.loop = false
            source.disconnect()
            source.stop()
        }

    }
}
