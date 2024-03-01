package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.util.BaseReleasable
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class TimeQuery(private val gl: GlApi) : BaseReleasable() {
    private val queryBegin = gl.createQuery()
    private val queryEnd = gl.createQuery()

    var isInFlight = false
        private set
    var hasBegun = false
        private set

    val isAvailable: Boolean
        get() = isInFlight &&
                gl.getQueryParameter(queryBegin, gl.QUERY_RESULT_AVAILABLE) == gl.TRUE &&
                gl.getQueryParameter(queryEnd, gl.QUERY_RESULT_AVAILABLE) == gl.TRUE

    fun getQueryResultMillis(): Double {
        val begin = gl.getQueryParameterU64(queryBegin, gl.QUERY_RESULT)
        val end = gl.getQueryParameterU64(queryEnd, gl.QUERY_RESULT)
        isInFlight = false
        return (end - begin) / 1e6
    }

    fun begin() {
        if (gl.capabilities.hasTimestampQuery && !hasBegun && !isInFlight) {
            hasBegun = true
            isInFlight = true
            gl.queryCounter(queryBegin, gl.TIMESTAMP)
        }
    }

    fun end() {
        if (gl.capabilities.hasTimestampQuery && hasBegun) {
            hasBegun = false
            gl.queryCounter(queryEnd, gl.TIMESTAMP)
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
        gl.deleteQuery(queryBegin)
        gl.deleteQuery(queryEnd)
    }
}