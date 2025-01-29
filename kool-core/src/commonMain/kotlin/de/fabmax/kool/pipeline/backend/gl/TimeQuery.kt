package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.util.BaseReleasable
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

class TimeQuery(private val gl: GlApi) : BaseReleasable() {
    private val beginTime = gl.createQuery()
    private val endTime = gl.createQuery()

    var isInFlight = false
        private set
    var hasBegun = false
        private set

    val isAvailable: Boolean
        get() = isInFlight &&
                gl.getQueryParameter(beginTime, gl.QUERY_RESULT_AVAILABLE) == gl.TRUE &&
                gl.getQueryParameter(endTime, gl.QUERY_RESULT_AVAILABLE) == gl.TRUE

    fun getQueryResult(): Duration {
        val begin = gl.getQueryParameterU64(beginTime, gl.QUERY_RESULT)
        val end = gl.getQueryParameterU64(endTime, gl.QUERY_RESULT)
        isInFlight = false
        return (end - begin).nanoseconds
    }

    fun begin() {
        if (gl.capabilities.hasTimestampQuery && !hasBegun && !isInFlight) {
            hasBegun = true
            isInFlight = true
            gl.queryCounter(beginTime, gl.TIMESTAMP)
        }
    }

    fun end() {
        if (gl.capabilities.hasTimestampQuery && hasBegun) {
            hasBegun = false
            gl.queryCounter(endTime, gl.TIMESTAMP)
        }
    }

    inline fun timedScope(block: () -> Unit) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

        val doQuery = !isInFlight
        if (doQuery) begin()
        block()
        if (doQuery) end()
    }

    override fun release() {
        super.release()
        gl.deleteQuery(beginTime)
        gl.deleteQuery(endTime)
    }
}