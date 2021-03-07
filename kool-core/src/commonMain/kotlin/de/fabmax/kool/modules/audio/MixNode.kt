package de.fabmax.kool.modules.audio

open class MixNode(inputNodes: List<AudioNode> = emptyList()) : AudioNode() {

    private val mutInputs = mutableListOf<AudioNode>().apply { addAll(inputNodes) }
    val inputs: List<AudioNode>
        get() = mutInputs

    var inputGains = FloatArray(mutInputs.size) { 1f }
        private set
    var inputSpeeds = FloatArray(mutInputs.size) { 1f }
        private set

    fun addNode(node: AudioNode, inputGain: Float = 1f, inputSpeed: Float = 1f) {
        mutInputs += node
        inputGains = FloatArray(mutInputs.size) { i -> if (i in inputGains.indices) inputGains[i] else inputGain }
        inputSpeeds = FloatArray(mutInputs.size) { i -> if (i in inputSpeeds.indices) inputSpeeds[i] else inputSpeed }
    }

    fun removeNode(node: AudioNode) {
        val removeI = mutInputs.indexOf(node)
        if (removeI >= 0) {
            mutInputs.removeAt(removeI)
            val newGains = FloatArray(mutInputs.size)
            val newSpeeds = FloatArray(mutInputs.size)
            for (i in mutInputs.indices) {
                val fromI = if (i < removeI) i else i+1
                newGains[i] = inputGains[fromI]
                newSpeeds[i] = inputSpeeds[fromI]
            }
            inputGains = newGains
            inputSpeeds = newSpeeds
        }
    }

    override fun nextSample(dt: Float): Float {
        val speedDt = dt * speed
        var out = 0f
        for (i in mutInputs.indices) {
            if (inputGains[i] > 0f) {
                out += mutInputs[i].nextSample(speedDt * inputSpeeds[i]) * inputGains[i]
            }
        }
        return out * gain
    }

}