package de.fabmax.kool.pipeline.deferred

fun interface DeferredPassSwapListener {
    fun onSwap(previousPasses: DeferredPasses, currentPasses: DeferredPasses)
}