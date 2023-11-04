package de.fabmax.kool.pipeline.drawqueue

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.LazyMat4d
import de.fabmax.kool.util.LazyMat4f

class DrawQueue(val renderPass: RenderPass) {

    var isDoublePrecision = false
        private set

    val projMat = MutableMat4f()

    /**
     * Single precision view matrix captured from the render pass camera when [setupCamera] is called; always valid,
     * independent of [isDoublePrecision].
     * @see [viewMatD]
     */
    val viewMatF = MutableMat4f()

    /**
     * Double precision view matrix captured from the render pass camera when [setupCamera] is called; always valid,
     * independent of [isDoublePrecision].
     * @see [viewMatF]
     */
    val viewMatD = MutableMat4d()

    /**
     * Single precision proj * view matrix captured from the render pass camera when [setupCamera] is called; always
     * valid, independent of [isDoublePrecision].
     * @see [viewProjMatD]
     */
    val viewProjMatF = MutableMat4f()

    /**
     * Double precision proj * view matrix captured from the render pass camera when [setupCamera] is called; always
     * valid, independent of [isDoublePrecision]. However, if [isDoublePrecision] is false, the content of this matrix
     * is copied from [viewProjMatF], i.e. it is also only single-precision.
     * @see [viewProjMatF]
     */
    val viewProjMatD = MutableMat4d()

    private val lazyInvProj = LazyMat4f { projMat.invert(it) }
    private val lazyInvViewF = LazyMat4f { viewMatF.invert(it) }
    private val lazyInvViewD = LazyMat4d { viewMatD.invert(it) }

    val invProjMat: Mat4f get() = lazyInvProj.get()

    val invViewMatF: Mat4f get() = lazyInvViewF.get()

    val invViewMatD: Mat4d get() = lazyInvViewD.get()

    val commands: List<DrawCommand>
        get() = mutCommands

    private val mutCommands = mutableListOf<DrawCommand>()
    private val commandPool = mutableListOf<DrawCommand>()

    fun reset(isDoublePrecision: Boolean) {
        this.isDoublePrecision = isDoublePrecision
        commandPool.addAll(mutCommands)
        mutCommands.clear()
    }

    fun setupCamera(camera: Camera) {
        projMat.set(camera.proj)

        if (isDoublePrecision) {
            viewMatD.set(camera.dataD.view)
            viewProjMatD.set(camera.dataD.viewProj)
            viewMatF.set(viewMatD)
            viewProjMatF.set(viewProjMatD)
        } else {
            viewMatF.set(camera.dataF.view)
            viewProjMatF.set(camera.viewProj)
            viewMatD.set(viewMatF)
            viewProjMatD.set(viewProjMatF)
        }
        lazyInvProj.isDirty = true
        lazyInvViewF.isDirty = true
        lazyInvViewD.isDirty = true
    }

    fun addMesh(mesh: Mesh, ctx: KoolContext): DrawCommand {
        val cmd = if (commandPool.isNotEmpty()) {
            commandPool.removeAt(commandPool.lastIndex)
        } else {
            DrawCommand(this, mesh)
        }
        cmd.setup(mesh, ctx)
        mutCommands.add(cmd)
        return cmd
    }
}