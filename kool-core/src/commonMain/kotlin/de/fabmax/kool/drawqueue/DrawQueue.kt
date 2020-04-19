package de.fabmax.kool.drawqueue

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Mesh

class DrawQueue(val renderPass: RenderPass) {

    val commands: List<DrawCommand>
        get() = mutCommands

    private val mutCommands = mutableListOf<DrawCommand>()
    private val commandPool = mutableListOf<DrawCommand>()

    var meshFilter: (Mesh) -> Boolean = { true }

    fun clear() {
        commandPool.addAll(mutCommands)
        mutCommands.clear()
    }

    fun addMesh(mesh: Mesh, ctx: KoolContext): DrawCommand? {
        return if (!meshFilter(mesh)) {
            null
        } else {
            val cmd = if (commandPool.isNotEmpty()) {
                commandPool.removeAt(commandPool.lastIndex)
            } else {
                DrawCommand(renderPass)
            }
            cmd.mesh = mesh
            cmd.pipeline = mesh.getPipeline(ctx)
            cmd.captureMatrices()
            mutCommands.add(cmd)
            cmd
        }
    }
}