package de.fabmax.kool.platform.vk

import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Scene

class RenderPassGraph {

    val groups = mutableListOf<RenderPassGroup>()

    var requiredCommandBuffers = 0
        private set

    private val freeGroups = mutableListOf<RenderPassGroup>()

    private val remainingPasses = mutableSetOf<RenderPass>()
    private val addedPasses = mutableSetOf<RenderPass>()

    fun updateGraph(scenes: List<Scene>) {
        freeGroups += groups
        groups.clear()

        remainingPasses.clear()
        addedPasses.clear()

        for (i in scenes.indices) {
            val scene = scenes[i]
            for (j in scene.offscreenPasses.indices) {
                val offscreen = scene.offscreenPasses[j]
                if (offscreen.isEnabled) {
                    remainingPasses.add(offscreen)
                }
            }
        }

        // we require one command buffer per offscreen pass plus an additional one for all onscreen passes
        requiredCommandBuffers = remainingPasses.size + 1

        // grouped render passes together while considering that their dependency render passes are in groups
        // rendered before
        // the loop looks kinda scary, however number of render passes should typically very small (<10), so that
        // it *should* be fast enough...
        while (remainingPasses.isNotEmpty()) {
            var added: RenderPass? = null
            for (candidate in remainingPasses) {
                if (addedPasses.containsAll(candidate.dependencies)) {
                    if (groups.isNotEmpty()) {
                        val grp = groups.last()
                        if (grp.containsNone(candidate.dependencies)) {
                            grp += candidate
                            added = candidate
                            break
                        }
                    }
                    if (added == null) {
                        val grp = newGroup(false)
                        grp += candidate
                        groups += grp
                        added = candidate
                        break
                    }
                }
            }
            if (added == null) {
                throw IllegalStateException("Circular render pass dependency")
            } else {
                addedPasses += added
                remainingPasses -= added
            }
        }

        // on screen group is special: all included render passes will be merged into a single command buffer
        // this way correct draw order of overlaying scenes (e.g. game scene + menu overlay) is maintained
        val onScreenGroup = newGroup(true)
        groups.add(onScreenGroup)
        for (i in scenes.indices) {
            onScreenGroup += scenes[i].mainRenderPass
        }

        for (i in 1 until groups.size) {
            groups[i].dependencies += groups[i-1]
        }
    }

    private fun newGroup(isOnScreen: Boolean): RenderPassGroup {
        val group = if (freeGroups.isNotEmpty()) {
            freeGroups.removeAt(freeGroups.lastIndex)
        } else {
            RenderPassGroup()
        }
        group.clear()
        group.isOnScreen = isOnScreen
        return group
    }

    class RenderPassGroup {
        val dependencies = mutableSetOf<RenderPassGroup>()

        val renderPasses = mutableListOf<RenderPass>()
        val renderPassDependencies = mutableListOf<RenderPass>()

        var isOnScreen = false
        var signalSemaphore = 0L

        fun clear() {
            renderPassDependencies.clear()
            renderPasses.clear()
            dependencies.clear()
        }

        operator fun plusAssign(renderPass: RenderPass) {
            renderPasses.add(renderPass)
            renderPassDependencies.addAll(renderPass.dependencies)
        }

        fun containsNone(passes: List<RenderPass>): Boolean {
            for (i in passes.indices) {
                if (passes[i] in renderPasses) {
                    return false
                }
            }
            return true
        }
    }

}