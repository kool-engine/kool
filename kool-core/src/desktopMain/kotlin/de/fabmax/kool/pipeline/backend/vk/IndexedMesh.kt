package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.*
import org.lwjgl.util.vma.Vma.*
import org.lwjgl.vulkan.VK10.*

class IndexedMesh(val backend: RenderBackendVk, val mesh: Mesh) : BaseReleasable() {

    val numVertices = mesh.geometry.numVertices
    val numIndices = mesh.geometry.indices.position

    val vertexBuffer = createVertexBuffer()
    val vertexBufferI = createVertexBufferI()
    val indexBuffer = createIndexBuffer()

    var instanceBuffer: InstanceBuffer? = null
        private set

    init {
        vertexBuffer.releaseWith(this)
        indexBuffer.releaseWith(this)
        vertexBufferI?.let { it.releaseWith(this) }

        mesh.instances?.let { recreateInstanceBuffer(it) }
    }

    fun updateInstanceBuffer() {
        mesh.instances?.let { insts ->
            var buf = instanceBuffer
            if (buf == null || buf.maxInsts < insts.maxInstances) {
                recreateInstanceBuffer(insts)
                buf = instanceBuffer!!
            }

            buf.buffer.mappedFloats {
                insts.dataF.useRaw { put(it) }
            }
        }
    }

    private fun recreateInstanceBuffer(instances: MeshInstanceList) {
        instanceBuffer?.let {
            it.buffer.cancelReleaseWith(this)
            it.buffer.release()
        }

        memStack {
            val bufferSize = instances.maxInstances * instances.strideBytesF.toLong()
            val stagingAllocUsage = VMA_MEMORY_USAGE_CPU_TO_GPU
            val buffer = Buffer(backend, bufferSize, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, stagingAllocUsage)
            instanceBuffer = InstanceBuffer(buffer, instances.maxInstances)
        }
    }

    private fun createVertexBuffer(): Buffer {
        memStack {
            val bufferSize = numVertices * mesh.geometry.byteStrideF.toLong()
            val stagingAllocUsage = VMA_MEMORY_USAGE_CPU_ONLY
            val stagingBuffer = Buffer(backend, bufferSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
            stagingBuffer.mappedFloats {
                mesh.geometry.dataF.useRaw { put(it) }
            }

            val usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT or VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
            val allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
            val buffer = Buffer(backend, bufferSize, usage, allocUsage)
            buffer.put(stagingBuffer)
            stagingBuffer.release()
            return buffer
        }
    }

    private fun createVertexBufferI(): Buffer? {
        if (mesh.geometry.byteStrideI > 0) {
            memStack {
                val bufferSize = numVertices * mesh.geometry.byteStrideI.toLong()
                val stagingAllocUsage = VMA_MEMORY_USAGE_CPU_ONLY
                val stagingBuffer = Buffer(backend, bufferSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
                stagingBuffer.mappedInts {
                    mesh.geometry.dataI.useRaw { put(it) }
                }

                val usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT or VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
                val allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
                val buffer = Buffer(backend, bufferSize, usage, allocUsage)
                buffer.put(stagingBuffer)
                stagingBuffer.release()
                return buffer
            }
        } else {
            return null
        }
    }

    private fun createIndexBuffer(): Buffer {
        memStack {
            val bufferSize = numIndices * 4L
            val stagingAllocUsage = VMA_MEMORY_USAGE_CPU_ONLY
            val stagingBuffer = Buffer(backend, bufferSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
            stagingBuffer.mappedInts {
                mesh.geometry.indices.useRaw { put(it) }
            }

            val usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT or VK_BUFFER_USAGE_INDEX_BUFFER_BIT
            val allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
            val buffer = Buffer(backend, bufferSize, usage, allocUsage)
            buffer.put(stagingBuffer)
            stagingBuffer.release()
            return buffer
        }
    }

//    override fun freeResources() {
        //logD { "Destroyed IndexedMesh" }
//    }

    class InstanceBuffer(val buffer: Buffer, val maxInsts: Int)
}