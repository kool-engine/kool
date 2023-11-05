package de.fabmax.kool.physics

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.Time
import kotlin.math.min

/**
 * PhysicsStepper provides an interface for implementing different physics simulation time strategies.
 */
abstract class PhysicsStepper {
    var simTimeFactor = 1f

    var maxTimeStepPerFrame = 0.1f

    private val perf = PerfTimer()
    var perfCpuTime = 0f
        private set
    var perfTimeFactor = 1f
        private set

    fun stepSimulation(world: PhysicsWorld, ctx: KoolContext): Float {
        perf.reset()

        if (simTimeFactor > 0f) {
            val timeAdvance = doSimSteps(world, ctx)

            val ms = perf.takeMs().toFloat()
            perfCpuTime = perfCpuTime * 0.8f + ms * 0.2f
            perfTimeFactor = perfTimeFactor * 0.9f + (timeAdvance / (Time.deltaT * simTimeFactor)) * 0.1f

            return timeAdvance
        } else {
            perfCpuTime = 0f
            perfTimeFactor = 0f
            return 0f
        }
    }

    protected abstract fun doSimSteps(world: PhysicsWorld, ctx: KoolContext): Float
}

/**
 * Simple synchronous physics stepper, which always advances the simulation by the current frame time, resulting
 * in smooth physics simulation over a wide range of frame rates. Since the physics time step depends on the current
 * frame time, physics simulation is not deterministic with this stepper.
 * Moreover, because simulation runs synchronously, the main thread is blocked while physics simulation is done.
 */
class SimplePhysicsStepper : PhysicsStepper() {
    var maxSingleStepTime: Float = 0.02f

    override fun doSimSteps(world: PhysicsWorld, ctx: KoolContext): Float {
        var remainingStepTime = min(maxTimeStepPerFrame, Time.deltaT * simTimeFactor)
        var timeAdvance = 0f
        while (remainingStepTime > 0.001f) {
            val singleStep = min(remainingStepTime, maxSingleStepTime)
            world.singleStepSync(singleStep)
            remainingStepTime -= singleStep
            timeAdvance += singleStep
        }
        return timeAdvance
    }
}

/**
 * Provides deterministic physics behavior by using a constant time step. Moreover, this stepper uses
 * asynchronous physics stepping, i.e. the bulk of physics simulation is done in parallel to graphics stuff resulting
 * in higher performance.
 * However, because a constant time step is used, physics time and render time can diverge by up to one
 * [constantTimeStep], which can result in a somewhat stuttery appearance.
 */
class ConstantPhysicsStepperAsync(val constantTimeStep: Float = 1f / 60f) : PhysicsStepper() {
    private var isStepInProgress = false
    private var internalSimTime = 0.0
    private var desiredSimTime = 0.0

    var maxSubSteps = 5

    private var subStepLimit = maxSubSteps

    override fun doSimSteps(world: PhysicsWorld, ctx: KoolContext): Float {
        var timeAdvance = 0f
        desiredSimTime += min(maxTimeStepPerFrame, Time.deltaT * simTimeFactor)

        // get results from previous sim step, which was done in parallel to last frame render
        if (isStepInProgress) {
            world.fetchAsyncStepResults()
            isStepInProgress = false
            internalSimTime += constantTimeStep
            timeAdvance += constantTimeStep
        }

        // do more synchronous / blocking sim steps if needed, in case past frame time was too large
        var subSteps = subStepLimit
        while (shouldAdvance(internalSimTime, desiredSimTime, false) && subSteps-- > 0) {
            world.singleStepSync(constantTimeStep)
            internalSimTime += constantTimeStep
            timeAdvance += constantTimeStep
        }

        if (subSteps == 0 && subStepLimit > 1) {
            subStepLimit--
        } else if(subStepLimit < maxSubSteps) {
            subStepLimit++
        }

        // start next async / non-blocking sim step
        if (shouldAdvance(internalSimTime, desiredSimTime + constantTimeStep, true)) {
            world.singleStepAsync(constantTimeStep)
            isStepInProgress = true
        }

        return timeAdvance
    }

    private fun shouldAdvance(currentTime: Double, desiredTime: Double, isFirst: Boolean): Boolean {
        return if (isFirst) {
            currentTime + constantTimeStep < desiredTime
        } else {
            // only do additional steps if it's really necessary (i.e. allow some hysteresis to reduce jitter)
            currentTime + constantTimeStep * 1.5f < desiredTime
        }
    }
}

/**
 * Provides deterministic physics behavior by using a constant time step.
 * However, because a constant time step is used, physics time and render time can diverge by up to one
 * [constantTimeStep], which can result in a somewhat stuttery appearance.
 */
class ConstantPhysicsStepperSync(val constantTimeStep: Float = 1f / 60f) : PhysicsStepper() {
    private var internalSimTime = 0.0
    private var desiredSimTime = 0.0

    override fun doSimSteps(world: PhysicsWorld, ctx: KoolContext): Float {
        var timeAdvance = 0f
        desiredSimTime += min(maxTimeStepPerFrame, Time.deltaT * simTimeFactor)

        // step simulation until desired time is reached
        while (shouldAdvance(internalSimTime, desiredSimTime + constantTimeStep, timeAdvance == 0f)) {
            world.singleStepSync(constantTimeStep)
            timeAdvance += constantTimeStep
            internalSimTime += constantTimeStep
        }

        return timeAdvance
    }

    private fun shouldAdvance(currentTime: Double, desiredTime: Double, isFirst: Boolean): Boolean {
        return if (isFirst) {
            currentTime + constantTimeStep < desiredTime
        } else {
            // only do additional steps if it's really necessary (i.e. allow some hysteresis to reduce jitter)
            currentTime + constantTimeStep * 1.5f < desiredTime
        }
    }
}
