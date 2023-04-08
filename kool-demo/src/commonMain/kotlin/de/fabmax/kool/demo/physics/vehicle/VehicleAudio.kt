package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.Assets
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.randomI
import de.fabmax.kool.modules.audio.*
import de.fabmax.kool.physics.ContactListener
import de.fabmax.kool.physics.ContactPoint
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidActor
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class VehicleAudio(physicsWorld: PhysicsWorld) {

    var isEnabled = false

    var rpm = 0f
    var throttle = 0f
    var brake = 0f
    var speed = 0f

    var slip = 0f

    var gearOut = false
    var gearIn = false

    private var audioOutput: AudioOutput? = null
    val isStarted: Boolean
        get() = audioOutput != null

    private lateinit var rpmMix: RpmMixNode
    private lateinit var squealMix: SquealModulateNode
    private lateinit var gearShiftNode: WavNode

    private val contactListener = VehicleContactListener()
    private val crashSounds = mutableListOf<AudioClip>()

    init {
        physicsWorld.registerContactListener(contactListener)
    }

    suspend fun loadAudio(assetMgr: Assets) {
        assetMgr.apply {
            for (i in 1..5) {
                crashSounds += loadAudioClip("${DemoLoader.soundPath}/car/crash$i.wav")
            }

            val idle1 = WavNode(WavFile(loadBlobAsset("${DemoLoader.soundPath}/car/rpm_idle1.wav")))
            val idle2 = WavNode(WavFile(loadBlobAsset("${DemoLoader.soundPath}/car/rpm_idle2.wav")))
            val idleMix = ModulateNode(idle1, idle2).apply { gain = 1.5f }

            val mid1 = WavNode(WavFile(loadBlobAsset("${DemoLoader.soundPath}/car/rpm_mid1.wav")))
            val mid2 = WavNode(WavFile(loadBlobAsset("${DemoLoader.soundPath}/car/rpm_mid2.wav")))
            val midMix = ModulateNode(mid1, mid2).apply {
                gainAmpliMod1 = 0.2f
                gainAmpliMod2 = 0.2f
                gainAmpliBase1 = 0.8f
                gainAmpliBase2 = 0.4f
                speedAmpliMod1 = 0.015f
                speedAmpliMod2 = 0.015f
            }

            val high1 = WavNode(WavFile(loadBlobAsset("${DemoLoader.soundPath}/car/rpm_high1.wav")))
            val high2 = WavNode(WavFile(loadBlobAsset("${DemoLoader.soundPath}/car/rpm_high2.wav"))).apply { speed = 1.05f }
            val highMix = ModulateNode(high1, high2).apply {
                gainAmpliMod1 = 0.2f
                gainAmpliMod2 = 0.2f
                gainAmpliBase1 = 0.8f
                gainAmpliBase2 = 0.4f
                speedAmpliMod1 = 0.01f
                speedAmpliMod2 = 0.01f
            }

            rpmMix = RpmMixNode(idleMix, midMix, highMix)

            val squeal1 = WavNode(WavFile(loadBlobAsset("${DemoLoader.soundPath}/car/squeal1.wav")))
            val squeal2 = WavNode(WavFile(loadBlobAsset("${DemoLoader.soundPath}/car/squeal2.wav")))
            squealMix = SquealModulateNode(squeal1, squeal2)

            gearShiftNode = WavNode(WavFile(loadBlobAsset("${DemoLoader.soundPath}/car/gear_shift.wav"))).apply { loop = false }

            rpmMix.gain = 0.7f
            squealMix.gain = 0.5f
            gearShiftNode.gain = 0f
        }
    }

    fun start() {
        isEnabled = true
        val gearShiftGain = 0.5f

        audioOutput = AudioOutput().apply {
            mixer.addNode(rpmMix)
            mixer.addNode(squealMix)
            mixer.addNode(gearShiftNode)
            mixer.gain = 0.5f

            onBufferUpdate = { t ->
                rpmMix.rpm = rpm
                rpmMix.modulate(t)

                squealMix.slip = slip
                squealMix.vehicleSpeed = speed
                squealMix.modulate(t)

                if (gearOut) {
                    gearOut = false
                    gearShiftNode.pos = 0.0
                    gearShiftNode.speed = 1f
                    gearShiftNode.gain = gearShiftGain * 0.5f
                }
                if (gearIn) {
                    gearIn = false
                    gearShiftNode.pos = 0.0
                    gearShiftNode.speed = 0.5f
                    gearShiftNode.gain = gearShiftGain
                }
            }
        }
    }

    fun stop() {
        audioOutput?.close()
        audioOutput = null
        isEnabled = false
    }

    private inner class VehicleContactListener : ContactListener {
        override fun onTouchFound(actorA: RigidActor, actorB: RigidActor, contactPoints: List<ContactPoint>?) {
            if (isEnabled && crashSounds.isNotEmpty()) {
                val minImpulse = 10f
                var impulse = 0f
                contactPoints?.let {
                    impulse = 0f
                    it.forEach { pt ->
                        val imp = pt.impulse.length()
                        if (imp > impulse) {
                            impulse = imp
                        }
                    }
                }

                if (impulse > minImpulse) {
                    val clip = crashSounds[randomI(crashSounds.indices)]
                    clip.play()
                    clip.volume = (sqrt(impulse) / 400).clamp(0.1f, 0.7f)
                }
            }
        }
    }

    private open class ModulateNode(in1: AudioNode, in2: AudioNode) : MixNode(listOf(in1, in2)) {
        var gainFreq1 = 10f
        var gainFreq2 = 7.17f
        var gainAmpliMod1 = 0.3f
        var gainAmpliMod2 = 0.3f
        var gainAmpliBase1 = 0.7f
        var gainAmpliBase2 = 0.7f

        var speedFreq1 = 6f
        var speedFreq2 = 4.3f
        var speedAmpliMod1 = 0.02f
        var speedAmpliMod2 = 0.02f

        open fun modulate(t: Double) {
            val g0 = (sin(t * gainFreq1).toFloat() * gainAmpliMod1) + gainAmpliBase1
            val g1 = (sin(t * gainFreq2).toFloat() * gainAmpliMod2) + gainAmpliBase2
            val s0 = (sin(t * speedFreq1).toFloat() * speedAmpliMod1) + 1f
            val s1 = (sin(t * speedFreq2).toFloat() * speedAmpliMod2) + 1f

            val s = g0 + g1
            inputGains[0] = g0 / s
            inputGains[1] = g1 / s

            inputSpeeds[0] = s0
            inputSpeeds[1] = s1
        }
    }

    private class SquealModulateNode(squal1: AudioNode, squal2: AudioNode) : ModulateNode(squal1, squal2) {
        var slip = 0f
        var vehicleSpeed = 0f

        private var modSpeed = 1f
        private var modGain = 1f

        init {
            speedAmpliMod1 = 0.04f
            speedAmpliMod2 = 0.04f
        }

        override fun nextSample(dt: Float): Float {
            val spd = speed
            val gn = gain
            speed *= modSpeed
            gain *= modGain
            val smpl = super.nextSample(dt)
            speed = spd
            gain = gn
            return smpl
        }

        override fun modulate(t: Double) {
            super.modulate(t)

            modSpeed = lerp(vehicleSpeed, 10f, 50f, 1.1f, 1.4f)
            modGain = (lerp(slip, 0.1f, 0.75f, 0f, 1.0f) *
                    lerp(vehicleSpeed, 5f, 30f, 0f, 1f)).pow(0.75f)
        }
    }

    private class RpmMixNode(val lowRpm: ModulateNode, val midRpm: ModulateNode, val highRpm: ModulateNode): MixNode(listOf(lowRpm, midRpm, highRpm)) {
        var minRpm = 750f
        var maxRpm = 6000f
        var rpm = minRpm
            set(value) {
                field = value.clamp(minRpm, maxRpm)
            }

        var lowRpmCenter = 1000f
        var lowRpmMinSpeed = 0.8f
        var lowRpmMaxSpeed = 1.5f

        var midRpmCenter = 2000f
        var midRpmMinSpeed = 0.5f
        var midRpmMaxSpeed = 1.5f

        var highRpmCenter = 4500f
        var highRpmMinSpeed = 0.5f
        var highRpmMaxSpeed = 1.5f

        fun modulate(t: Double) {
            var gainLow = 0f
            var gainMid = 0f
            var gainHigh = 0f

            var speedLow = 1f
            var speedMid = 1f
            var speedHigh = 1f

            when {
                rpm < lowRpmCenter -> {
                    gainLow = 1f
                    speedLow = lerp(rpm, minRpm, lowRpmCenter, lowRpmMinSpeed, 1f)
                }
                rpm < midRpmCenter -> {
                    gainMid = lerp(rpm, lowRpmCenter, midRpmCenter, 0f, 1f)
                    gainLow = 1f - gainMid

                    speedLow = lerp(rpm, lowRpmCenter, midRpmCenter, 1f, lowRpmMaxSpeed)
                    speedMid = lerp(rpm, lowRpmCenter, midRpmCenter, midRpmMinSpeed, 1f)
                }
                rpm < highRpmCenter -> {
                    gainHigh = lerp(rpm, midRpmCenter, highRpmCenter, 0f, 1f)
                    gainMid = 1f - gainMid

                    speedMid = lerp(rpm, midRpmCenter, highRpmCenter, 1f, midRpmMaxSpeed)
                    speedHigh = lerp(rpm, midRpmCenter, highRpmCenter, highRpmMinSpeed, 1f)
                }
                else -> {
                    gainHigh = 1f
                    speedHigh = lerp(rpm, highRpmCenter, maxRpm, 1f, highRpmMaxSpeed)
                }
            }

            if (gainLow > 0f) {
                lowRpm.modulate(t)
            }
            if (gainMid > 0f) {
                midRpm.modulate(t)
            }
            if (gainHigh > 0f) {
                highRpm.modulate(t)
            }

            inputGains[0] = gainLow
            inputGains[1] = gainMid
            inputGains[2] = gainHigh

            inputSpeeds[0] = speedLow
            inputSpeeds[1] = speedMid
            inputSpeeds[2] = speedHigh
        }
    }

    companion object {
        private fun lerp(pos: Float, min: Float, max: Float, valueAtMin: Float, valueAtMax: Float): Float {
            return when {
                pos < min -> valueAtMin
                pos > max -> valueAtMax
                else -> {
                    val wMin = (max - pos) / (max - min)
                    valueAtMin * wMin + valueAtMax * (1 - wMin)
                }
            }
        }
    }
}