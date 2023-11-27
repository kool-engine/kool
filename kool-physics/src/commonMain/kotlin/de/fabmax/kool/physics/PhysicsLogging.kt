package de.fabmax.kool.physics

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logW

object PhysicsLogging {

    const val LOG_MASK_NONE = 0
    const val LOG_MASK_ALL = -1

    const val DEBUG_INFO = 1
    const val DEBUG_WARNING = 2
    const val INVALID_PARAMETER = 4
    const val INVALID_OPERATION = 8
    const val OUT_OF_MEMORY = 16
    const val INTERNAL_ERROR = 32
    const val ABORT = 64
    const val PERF_WARNING = 128

    var logMask = LOG_MASK_ALL

    internal fun logPhysics(code: Int, message: String, file: String, line: Int) {
        if (code and logMask != 0) {
            if (code == INTERNAL_ERROR && "PxPhysXGpuModuleLoader" in file) {
                // this is actually fine...
                logD("PhysX") { "${message.trim()} - CUDA acceleration not available" }
                return
            }

            val logMsg = "[${codeToString(code)}] ${message.trim()} [$file:$line]"
            when (code) {
                DEBUG_INFO -> logI("PhysX") { logMsg }
                DEBUG_WARNING, PERF_WARNING -> logW("PhysX") { logMsg }
                else -> logE("PhysX") { logMsg }
            }
        }
    }

    private fun codeToString(code: Int): String {
        return when (code) {
            DEBUG_INFO -> "DEBUG_INFO"
            DEBUG_WARNING -> "DEBUG_WARNING"
            INVALID_PARAMETER -> "INVALID_PARAMETER"
            INVALID_OPERATION -> "INVALID_OPERATION"
            OUT_OF_MEMORY -> "OUT_OF_MEMORY"
            INTERNAL_ERROR -> "INTERNAL_ERROR"
            ABORT -> "ABORT"
            PERF_WARNING -> "PERF_WARNING"
            else -> "UNKNOWN($code)"
        }
    }
}