package de.fabmax.kool.util

import de.fabmax.kool.math.clamp
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.math.ceil
import kotlin.time.measureTime


interface SimulationStepper {
    var desiredTimeFactor: Float
    var isPaused: Boolean

    val simulationTime: Double
    val actualTimeFactor: Float
    val cpuMillisPerStep: Float

    suspend fun stepPhysics()

    suspend fun waitForSimulation()
}

interface InterpolatableSimulation {
    fun simulateStep(timeStep: Float)
    fun captureStepResults(simulationTime: Double)
    fun interpolateSteps(simulationTimePrev: Double, simulationTimeNext: Double, simulationTimeLerp: Double, weightNext: Float)
}

class AsyncSimulationStepper(
    val simulation: InterpolatableSimulation,
    val simulationCoroutineContext: CoroutineContext,
    val singleTimeStep: Float = 1f / 60f,
) : SimulationStepper {
    override var desiredTimeFactor: Float = 1f
    override var isPaused: Boolean = false

    override var simulationTime: Double = 0.0; private set
    override var actualTimeFactor: Float = 1f; private set
    override var cpuMillisPerStep: Float = 0f; private set

    private var deferredStep: Deferred<Unit>? = null
    private var refTime = 0.0
    private var simulationTimePrev = 0.0

    var maxSimulationDeltaT: Float = 0.1f
    var deltaTVariance: Float = 1.1f

    override suspend fun stepPhysics() {
        waitForSimulation()

        val dt = if (isPaused) 0f else (Time.deltaT * desiredTimeFactor).coerceAtMost(maxSimulationDeltaT)
        refTime += dt

        val deltaCapture = simulationTime - simulationTimePrev
        val weightNext = ((refTime - simulationTimePrev) / deltaCapture).toFloat().clamp(0f, 1f)
        simulation.interpolateSteps(simulationTimePrev, simulationTime, refTime, weightNext)
        actualTimeFactor = actualTimeFactor * 0.8f + dt / Time.deltaT * 0.2f

        val expectedNextFrameTime = refTime + dt * deltaTVariance
        val nextDelta = expectedNextFrameTime - simulationTime
        if (nextDelta > 0f) {
            val requiredSteps = ceil(nextDelta / singleTimeStep).toInt()
            simulationTimePrev = simulationTime
            simulationTime += requiredSteps * singleTimeStep
            if (simulationTime < refTime) {
                simulationTime = refTime
            }

            withContext(simulationCoroutineContext) {
                deferredStep = async {
                    val cpuTime = measureTime {
                        repeat(requiredSteps) {
                            simulation.simulateStep(singleTimeStep)
                        }
                    }
                    val secsPerStep = (cpuTime.inWholeMicroseconds / 1e3).toFloat()
                    cpuMillisPerStep = cpuMillisPerStep * 0.8f + secsPerStep * 0.2f
                    simulation.captureStepResults(simulationTime)
                }
            }
        }
    }

    override suspend fun waitForSimulation() {
        deferredStep?.await()
    }
}