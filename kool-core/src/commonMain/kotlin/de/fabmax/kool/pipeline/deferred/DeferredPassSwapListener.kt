package de.fabmax.kool.pipeline.deferred

interface DeferredPassSwapListener {

    fun onSwap(previousPasses: DeferredPasses, currentPasses: DeferredPasses)

}