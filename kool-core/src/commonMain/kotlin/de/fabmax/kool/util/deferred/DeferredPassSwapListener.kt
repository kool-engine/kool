package de.fabmax.kool.util.deferred

interface DeferredPassSwapListener {

    fun onSwap(previousPasses: DeferredPasses, currentPasses: DeferredPasses)

}