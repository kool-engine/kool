package de.fabmax.kool.pipeline.drawqueue

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Mesh

class DrawQueue(val renderPass: RenderPass) {

    val commands: List<DrawCommand>
        get() = mutCommands

    private val mutCommands = mutableListOf<DrawCommand>()
    private val commandPool = mutableListOf<DrawCommand>()

    fun clear() {
        commandPool.addAll(mutCommands)
        mutCommands.clear()
    }

    fun addMesh(mesh: Mesh, ctx: KoolContext): DrawCommand {
        val cmd = if (commandPool.isNotEmpty()) {
            commandPool.removeAt(commandPool.lastIndex)
        } else {
            DrawCommand(renderPass, mesh)
        }
        cmd.setup(mesh, ctx)
        mutCommands.add(cmd)
        return cmd
    }
}