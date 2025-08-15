package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.PassData
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.checkIsNotReleased
import de.fabmax.kool.util.logE
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class GpuPass(var name: String) : BaseReleasable() {

    val dependencies = mutableListOf<GpuPass>()

    var parentScene: Scene? = null

    val onUpdate = BufferedList<(() -> Unit)>()
    val onAfterCollect = BufferedList<(() -> Unit)>()

    var isEnabled = true

    var isProfileGpu = false
    var tGpu: Duration = 0.0.seconds
    var tRecord: Duration = 0.0.seconds
    var tUpdate: Duration = 0.0.seconds

    fun dependsOn(pass: GpuPass) {
        dependencies += pass
    }

    fun onUpdate(block: () -> Unit) {
        onUpdate += block
    }

    fun onAfterCollect(block: () -> Unit) {
        onAfterCollect += block
    }

    fun collect(passData: PassData, ctx: KoolContext) {
        update(passData, ctx)
        onAfterCollect.update()
        for (i in onAfterCollect.indices) {
            onAfterCollect[i]()
        }
    }

    protected open fun update(passData: PassData, ctx: KoolContext) {
        checkIsNotReleased()
        onUpdate.update()
        for (i in onUpdate.indices) {
            onUpdate[i]()
        }
    }

    override fun toString(): String {
        return "${this::class.simpleName}:$name"
    }

    companion object {
        fun sortByDependencies(passes: MutableList<GpuPass>) {
            val open = passes.toMutableSet()
            val closed = mutableSetOf<GpuPass>()

            passes.clear()

            while (open.isNotEmpty()) {
                var anyClosed = false
                val openIt = open.iterator()
                while (openIt.hasNext()) {
                    val pass = openIt.next()
                    var close = true
                    for (j in pass.dependencies.indices) {
                        val dep = pass.dependencies[j]
                        if (dep !in closed) {
                            close = false
                            break
                        }
                    }
                    if (close) {
                        anyClosed = true
                        openIt.remove()
                        closed += pass
                        passes += pass
                    }
                }
                if (!anyClosed) {
                    logE { "Failed to sort gpu passes, remaining:" }
                    open.forEach { p ->
                        val missingPasses = p.dependencies.filter { it !in closed }.map { it.name }
                        logE { "  ${p.name}, missing dependencies: $missingPasses" }
                    }
                    break
                }
            }
        }
    }
}