package de.fabmax.kool.drawqueue

class DrawQueue {

    val sceneSetup = SceneSetup()
    val commands = mutableListOf<DrawCommand>()

    fun clear() {
        commands.clear()
    }

    operator fun plusAssign(command: DrawCommand) = addElement(command)

    fun addElement(command: DrawCommand) {
        commands += command
    }

}