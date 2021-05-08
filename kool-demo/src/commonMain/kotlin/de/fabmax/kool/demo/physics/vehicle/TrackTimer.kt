package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.TriggerListener
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.vehicle.Vehicle

class TrackTimer(val vehicle: Vehicle, val track: Track, val world: VehicleWorld) {

    var enterPos = Vec3f.ZERO
    var enterSize = Vec3f.ZERO
    lateinit var enterTrigger: RigidStatic

    var exitPos = Vec3f.ZERO
    var exitSize = Vec3f.ZERO
    lateinit var exitTrigger: RigidStatic

    var checkPos1 = Vec3f.ZERO
    var checkSize1 = Vec3f.ZERO
    lateinit var checkTrigger1: RigidStatic

    var checkPos2 = Vec3f.ZERO
    var checkSize2 = Vec3f.ZERO
    lateinit var checkTrigger2: RigidStatic

    var trackTime = 0f
        private set
    var checkPoint = 0
    var timerState = TimerState.STOPPED
        private set
    var isReverse = false
        private set

    var onCheckPoint1: (Float) -> Unit = { }
    var onCheckPoint2: (Float) -> Unit = { }

    private val enterExitListener = EnterExitListener()
    private inner class EnterExitListener : TriggerListener {
        private var wasRunningOnEnter = true

        override fun onActorEntered(trigger: RigidActor, actor: RigidActor) {
            if (actor !== vehicle) {
                return
            }
            wasRunningOnEnter = timerState == TimerState.STARTED
            if (wasRunningOnEnter && checkPoint == 2) {
                timerState = TimerState.STOPPED
            }
        }

        override fun onActorExited(trigger: RigidActor, actor: RigidActor) {
            if (actor !== vehicle) {
                return
            }
            if (!wasRunningOnEnter || checkPoint < 2) {
                reset(trigger == exitTrigger)
                timerState = TimerState.STARTED
            }
        }
    }

    private val checkPointListener = CheckPointListener()
    private inner class CheckPointListener : TriggerListener {
        override fun onActorEntered(trigger: RigidActor, actor: RigidActor) {
            if (actor !== vehicle) {
                return
            }

            val cpIndex = when {
                !isReverse && trigger == checkTrigger1 -> 1
                !isReverse && trigger == checkTrigger2 -> 2
                isReverse && trigger == checkTrigger1 -> 2
                isReverse && trigger == checkTrigger2 -> 1
                else -> 0
            }

            if (cpIndex > checkPoint) {
                checkPoint = cpIndex
                if (cpIndex == 1) {
                    onCheckPoint1(trackTime)
                } else {
                    onCheckPoint2(trackTime)
                }
            }
        }
    }

    init {
        world.physics.onPhysicsUpdate += { deltaT ->
            if (timerState == TimerState.STARTED) {
                trackTime += deltaT
            }
        }
    }

    fun reset(isReverse: Boolean) {
        timerState = TimerState.STOPPED
        trackTime = 0f
        checkPoint = 0
        track.guardRail.isReverse = isReverse
        this.isReverse = isReverse
    }

    fun buildTriggers() {
        val material = world.defaultMaterial
        enterTrigger = RigidStatic().apply {
            isTrigger = true
            attachShape(Shape(BoxGeometry(enterSize), material))
            position = enterPos
        }
        world.physics.addActor(enterTrigger)

        exitTrigger = RigidStatic().apply {
            isTrigger = true
            attachShape(Shape(BoxGeometry(exitSize), material))
            position = exitPos
        }
        world.physics.addActor(exitTrigger)

        checkTrigger1 = RigidStatic().apply {
            isTrigger = true
            attachShape(Shape(BoxGeometry(checkSize1), material))
            position = checkPos1
        }
        world.physics.addActor(checkTrigger1)

        checkTrigger2 = RigidStatic().apply {
            isTrigger = true
            attachShape(Shape(BoxGeometry(checkSize2), material))
            position = checkPos2
        }
        world.physics.addActor(checkTrigger2)

        world.physics.registerTriggerListener(enterTrigger, enterExitListener)
        world.physics.registerTriggerListener(exitTrigger, enterExitListener)
        world.physics.registerTriggerListener(checkTrigger1, checkPointListener)
        world.physics.registerTriggerListener(checkTrigger2, checkPointListener)
    }

    enum class TimerState {
        STOPPED,
        STARTED
    }
}