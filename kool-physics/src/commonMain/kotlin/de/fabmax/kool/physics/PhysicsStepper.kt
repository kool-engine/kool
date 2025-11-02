package de.fabmax.kool.physics

import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Time
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.time.measureTime

interface PhysicsStepper {
    var desiredTimeFactor: Float
    var isPaused: Boolean

    val physicsTime: Double
    val actualTimeFactor: Float
    val cpuMilliesPerStep: Float

    suspend fun stepPhysics()

    suspend fun waitForSimulation()
}

class AsyncPhysicsStepper(
    val world: PhysicsWorld,
    val singleTimeStep: Float = 1f / 60f,
) : PhysicsStepper {
    override var desiredTimeFactor: Float = 1f
    override var isPaused: Boolean = false

    override var physicsTime: Double = 0.0; private set
    override var actualTimeFactor: Float = 1f; private set
    override var cpuMilliesPerStep: Float = 0f; private set

    private var deferredStep: Deferred<Unit>? = null
    private var refTime = 0.0
    private var prevCaptureTime = 0.0

    var maxSimulationDeltaT: Float = 0.1f
    var deltaTVariance: Float = 1.1f

    override suspend fun stepPhysics() {
        waitForSimulation()

        val dt = if (isPaused) 0f else (Time.deltaT * desiredTimeFactor).coerceAtMost(maxSimulationDeltaT)
        refTime += dt

        val deltaCapture = physicsTime - prevCaptureTime
        val weightB = ((refTime - prevCaptureTime) / deltaCapture).toFloat().clamp(0f, 1f)
        world.interpolateSimulation(prevCaptureTime, physicsTime, refTime, weightB)
        actualTimeFactor = actualTimeFactor * 0.8f + dt / Time.deltaT * 0.2f

        val expectedNextFrameTime = refTime + dt * deltaTVariance
        val nextDelta = expectedNextFrameTime - physicsTime
        if (nextDelta > 0f) {
            val requiredSteps = ceil(nextDelta / singleTimeStep).toInt()
            prevCaptureTime = physicsTime
            physicsTime += requiredSteps * singleTimeStep
            if (physicsTime < refTime) {
                physicsTime = refTime
            }

            withContext(Physics.physicsDispatcher) {
                deferredStep = async {
                    val cpuTime = measureTime {
                        repeat(requiredSteps) {
                            world.stepSimulation(singleTimeStep)
                        }
                    }
                    val secsPerStep = (cpuTime.inWholeMicroseconds / 1e3).toFloat()
                    cpuMilliesPerStep = cpuMilliesPerStep * 0.8f + secsPerStep * 0.2f
                    world.captureSimulation(physicsTime)
                }
            }
        }
    }

    override suspend fun waitForSimulation() {
        deferredStep?.await()
    }
}