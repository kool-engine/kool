package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.OffscreenRenderPass2dPingPong
import de.fabmax.kool.pipeline.OffscreenRenderPassCube
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.platform.Lwjgl3Context

class RenderPassGraph {

    val groups = mutableListOf<RenderPassGroup>()

    var requiredCommandBuffers = 0
        private set

    private val groupPool = mutableListOf<RenderPassGroup>()

    private val remainingPasses = mutableSetOf<RenderPass>()
    private val processedPasses = mutableSetOf<RenderPass>()
    private val addedPasses = mutableSetOf<RenderPass>()

    fun updateGraph(ctx: Lwjgl3Context) {
        val scenes = ctx.scenes

        groupPool += groups
        groups.clear()

        remainingPasses.clear()
        processedPasses.clear()
        addedPasses.clear()

        // we require at least one cmd buffer for on screen content
        requiredCommandBuffers = 1

        // collect all offscreen render passes of all scenes (onscreen passes are handled separately)
        for (j in ctx.backgroundPasses.indices) {
            val offscreen = ctx.backgroundPasses[j]
            if (offscreen.isEnabled) {
                remainingPasses.add(offscreen)
                requiredCommandBuffers += offscreen.requiredBuffers
            } else {
                processedPasses.add(offscreen)
            }
        }
        for (i in scenes.indices) {
            val scene = scenes[i]
            if (scene.isVisible) {
                for (j in scene.offscreenPasses.indices) {
                    val offscreen = scene.offscreenPasses[j]
                    if (offscreen.isEnabled) {
                        remainingPasses.add(offscreen)
                        requiredCommandBuffers += offscreen.requiredBuffers
                    } else {
                        processedPasses.add(offscreen)
                    }
                }
            }
        }

        // grouped render passes together while considering that their dependency render passes are in groups
        // rendered before
        // the loop looks kinda scary, however number of render passes should typically very small (<10), so that
        // it *should* be fast enough...
        while (remainingPasses.isNotEmpty()) {
            var added: RenderPass? = null
            for (candidate in remainingPasses) {
                if (processedPasses.containsAll(candidate.dependencies)) {
                    if (groups.isNotEmpty()) {
                        val grp = groups.last()
                        if (grp.containsNone(candidate.dependencies)) {
                            grp += candidate
                            added = candidate
                            break
                        }
                    }

                    // todo: it might be better to check if there are other remaining passes, which could be added
                    //       to the existing group instead of immediately creating a new group
                    val grp = newGroup(false)
                    grp += candidate
                    groups += grp
                    added = candidate
                    break
                }
            }
            if (added == null) {
                System.err.println("Remaining render passes:")
                remainingPasses.forEach { rp ->
                    System.err.println("    ${rp.name}, depends on:")
                    rp.dependencies.forEach { dep ->
                        System.err.println("        ${dep.name}")
                    }

                }
                break
            } else {
                addedPasses += added
                processedPasses += added
                remainingPasses -= added
            }
        }

        // on screen group is special: all included render passes will be merged into a single command buffer
        // this way correct draw order of overlaying scenes (e.g. game scene + menu overlay) is maintained
        val onScreenGroup = newGroup(true)
        groups.add(onScreenGroup)
        for (i in scenes.indices) {
            if (scenes[i].isVisible) {
                onScreenGroup += scenes[i].mainRenderPass
            }
        }

        for (i in 1 until groups.size) {
            groups[i].dependencies += groups[i-1]
        }
    }

    private val OffscreenRenderPass.requiredBuffers: Int
        get() = when (this) {
            is OffscreenRenderPass2dPingPong -> pingPongPasses
            is OffscreenRenderPassCube -> 6
            else -> 1
        }

    private fun newGroup(isOnScreen: Boolean): RenderPassGroup {
        val group = if (groupPool.isNotEmpty()) {
            groupPool.removeAt(groupPool.lastIndex)
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
        var numRequiredCmdBuffers = 0

        var isOnScreen = false
        var signalSemaphore = 0L

        fun clear() {
            numRequiredCmdBuffers = 0
            renderPassDependencies.clear()
            renderPasses.clear()
            dependencies.clear()
        }

        operator fun plusAssign(renderPass: RenderPass) {
            renderPasses.add(renderPass)
            renderPassDependencies.addAll(renderPass.dependencies)
            numRequiredCmdBuffers += if (renderPass is OffscreenRenderPass2dPingPong) renderPass.pingPongPasses else 1
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