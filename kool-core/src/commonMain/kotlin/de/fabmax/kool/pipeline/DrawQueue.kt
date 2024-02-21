package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.LazyMat4d
import de.fabmax.kool.util.LazyMat4f

class DrawQueue(val renderPass: RenderPass, val view: RenderPass.View) {

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

    private val commandPool = mutableListOf<DrawCommand>()

    @PublishedApi
    internal val orderedQueues = mutableListOf(OrderedQueue(0))
    private var prevQueue = orderedQueues[0]

    var drawGroupId = 0

    fun reset(isDoublePrecision: Boolean) {
        this.isDoublePrecision = isDoublePrecision

        var deleteAny = false
        for (i in orderedQueues.indices) {
            val queue = orderedQueues[i]
            if (queue.commands.isEmpty() && queue.groupId != 0) {
                queue.markDelete = true
                deleteAny = true
            }
            commandPool.addAll(queue.commands)
            queue.commands.clear()
        }
        if (deleteAny) {
            orderedQueues.removeAll { it.markDelete }
        }

        drawGroupId = 0
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

    private fun getOrderedQueue(): OrderedQueue {
        if (prevQueue.groupId == drawGroupId) {
            return prevQueue
        }
        for (i in orderedQueues.indices) {
            if (orderedQueues[i].groupId == drawGroupId) {
                prevQueue = orderedQueues[i]
                return orderedQueues[i]
            }
        }
        prevQueue = OrderedQueue(drawGroupId)
        orderedQueues.add(prevQueue)
        orderedQueues.sortBy { it.groupId }
        return prevQueue
    }

    fun addMesh(mesh: Mesh, pipeline: DrawPipeline): DrawCommand {
        val cmd = if (commandPool.isNotEmpty()) {
            commandPool.removeAt(commandPool.lastIndex)
        } else {
            DrawCommand(this, mesh, pipeline)
        }
        cmd.setup(mesh, pipeline, drawGroupId)
        getOrderedQueue().commands.add(cmd)
        return cmd
    }

    fun recycleDrawCommand(cmd: DrawCommand) {
        if (cmd.queue !== this) {
            throw IllegalArgumentException("DrawCommand does not belong to this DrawQueue")
        }
        commandPool.add(cmd)
    }

    inline fun forEach(block: (DrawCommand) -> Unit) {
        for (i in orderedQueues.indices) {
            val queue = orderedQueues[i]
            for (j in queue.commands.indices) {
                block(queue.commands[j])
            }
        }
    }

    fun iterator(): MutableIterator<DrawCommand> = QueueIterator()

    class OrderedQueue(val groupId: Int, val commands: MutableList<DrawCommand> = mutableListOf()) {
        internal var markDelete = false
    }

    private inner class QueueIterator : MutableIterator<DrawCommand> {
        val queueIt = orderedQueues.iterator()
        var commandIt = nextGroup()
        var prevCommandIt = commandIt

        override fun hasNext(): Boolean {
            return commandIt?.hasNext() == true
        }

        override fun next(): DrawCommand {
            val it = commandIt!!
            prevCommandIt = it
            val next = it.next()
            if (!it.hasNext()) {
                commandIt = nextGroup()
            }
            return next
        }

        override fun remove() {
            prevCommandIt!!.remove()
        }

        private fun nextGroup(): MutableIterator<DrawCommand>? {
            while (queueIt.hasNext()) {
                val it = queueIt.next().commands.iterator()
                if (it.hasNext()) {
                    return it
                }
            }
            return null
        }
    }
}