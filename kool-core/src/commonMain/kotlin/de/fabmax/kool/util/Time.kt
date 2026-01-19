package de.fabmax.kool.util

import androidx.compose.runtime.BroadcastFrameClock
import de.fabmax.kool.InternalKoolAPI
import de.fabmax.kool.util.Time.frameCount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock

object Time {
    /**
     * Time since previous frame in seconds.
     */
    var deltaT: Float = 0f; private set

    /**
     * Time since the app started in seconds.
     */
    var gameTime: Double = 0.0; internal set

    /**
     * Frames per second (with some averaging applied).
     */
    var fps = 60.0; private set
    private val frameTimes = DoubleArray(25) { 0.017 }

    /**
     * Number of rendered frames since the app started.
     */
    var frameCount: Int = 0
        internal set(value) {
            field = value
            _frameFlow.update { value }
        }
    private val _frameFlow = MutableStateFlow(0)

    /**
     * Returns the current number of rendered frames (like [frameCount]) as a [StateFlow].
     */
    val frameFlow: StateFlow<Int> = _frameFlow.asStateFlow()

    /**
     * Precision timer value, unit is seconds. Absolute value has no real meaning, but it can be used to precisely
     * measure time intervals. Precision depends on platform, on JVM it should be around nanoseconds, on JS it should
     * be around 0.1 milliseconds.
     */
    val precisionTime: Double get() = nanoTime / 1_000_000_000.0

    /**
     * Current system clock time in nanoseconds.
     */
    val nanoTime: Long get() {
        val now = Clock.System.now()
        return now.epochSeconds * 1_000_000_000L + now.nanosecondsOfSecond
    }

    /**
     * Frame clock from Jetpack compose runtime.
     * Frames are emitted after polling user input and dispatching any queued frontend scope tasks.
     *
     * Any blocks waiting for a new frame will run immediately when the frame is emitted, then switch
     * to their parent context to return the result.
     */
    @InternalKoolAPI
    val composeFrameClock = BroadcastFrameClock()

    internal fun update(dt: Double) {
        gameTime += dt
        deltaT = dt.toFloat()
        frameTimes[frameCount % frameTimes.size] = dt
        var sum = 0.0
        for (i in frameTimes.indices) { sum += frameTimes[i] }
        fps = (frameTimes.size / sum) * 0.1 + fps * 0.9
    }
}