package de.fabmax.kool.drawqueue

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Mesh

class DrawQueue {

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
            DrawCommand()
        }
        cmd.mesh = mesh
        cmd.pipeline = mesh.getPipeline(ctx)
        cmd.captureMvp(ctx)
        mutCommands.add(cmd)
        return cmd
    }
}