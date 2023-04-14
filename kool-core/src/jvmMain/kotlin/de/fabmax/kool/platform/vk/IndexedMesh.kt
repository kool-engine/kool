package de.fabmax.kool.platform.vk

import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Uint32BufferImpl
import de.fabmax.kool.util.memStack
import org.lwjgl.util.vma.Vma.*
import org.lwjgl.vulkan.VK10.*

class IndexedMesh(val sys: VkSystem, val mesh: Mesh) : VkResource() {

    val numVertices = mesh.geometry.numVertices
    val numIndices = mesh.geometry.indices.position

    val vertexBuffer = createVertexBuffer()
    val vertexBufferI = createVertexBufferI()
    val indexBuffer = createIndexBuffer()

    var instanceBuffer: InstanceBuffer? = null
        private set

    init {
        addDependingResource(vertexBuffer)
        addDependingResource(indexBuffer)
        vertexBufferI?.let { addDependingResource(it) }

        mesh.instances?.let { recreateInstanceBuffer(it) }
    }

    fun updateInstanceBuffer() {
        mesh.instances?.let {
            var buf = instanceBuffer
            if (buf == null || buf.maxInsts < it.maxInstances) {
                recreateInstanceBuffer(it)
                buf = instanceBuffer!!
            }

            buf.buffer.mappedFloats {
                it.dataF.flip()
                put((it.dataF as Float32BufferImpl).buffer)
            }
        }
    }

    private fun recreateInstanceBuffer(instances: MeshInstanceList) {
        instanceBuffer?.let {
            removeDependingResource(it.buffer)
            it.buffer.destroy()
        }

        memStack {
            val bufferSize = instances.maxInstances * instances.strideBytesF.toLong()
            val stagingAllocUsage = VMA_MEMORY_USAGE_CPU_TO_GPU
            val buffer = Buffer(sys, bufferSize, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, stagingAllocUsage)
            buffer.mappedFloats {
                instances.dataF.flip()
                put((instances.dataF as Float32BufferImpl).buffer)
            }
            instanceBuffer = InstanceBuffer(buffer, instances.maxInstances)
        }
    }

    private fun createVertexBuffer(): Buffer {
        memStack {
            val bufferSize = numVertices * mesh.geometry.byteStrideF.toLong()
            val stagingAllocUsage = VMA_MEMORY_USAGE_CPU_ONLY
            val stagingBuffer = Buffer(sys, bufferSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
            stagingBuffer.mappedFloats {
                mesh.geometry.dataF.flip()
                put((mesh.geometry.dataF as Float32BufferImpl).buffer)
            }

            val usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT or VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
            val allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
            val buffer = Buffer(sys, bufferSize, usage, allocUsage)
            buffer.put(stagingBuffer)
            stagingBuffer.destroy()
            return buffer
        }
    }

    private fun createVertexBufferI(): Buffer? {
        if (mesh.geometry.byteStrideI > 0) {
            memStack {
                val bufferSize = numVertices * mesh.geometry.byteStrideI.toLong()
                val stagingAllocUsage = VMA_MEMORY_USAGE_CPU_ONLY
                val stagingBuffer = Buffer(sys, bufferSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
                stagingBuffer.mappedInts {
                    mesh.geometry.dataI.flip()
                    put((mesh.geometry.dataI as Uint32BufferImpl).buffer)
                }

                val usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT or VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
                val allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
                val buffer = Buffer(sys, bufferSize, usage, allocUsage)
                buffer.put(stagingBuffer)
                stagingBuffer.destroy()
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
            val stagingBuffer = Buffer(sys, bufferSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
            stagingBuffer.mappedInts {
                mesh.geometry.indices.flip()
                put((mesh.geometry.indices as Uint32BufferImpl).buffer)
            }

            val usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT or VK_BUFFER_USAGE_INDEX_BUFFER_BIT
            val allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
            val buffer = Buffer(sys, bufferSize, usage, allocUsage)
            buffer.put(stagingBuffer)
            stagingBuffer.destroy()
            return buffer
        }
    }

    override fun freeResources() {
        //logD { "Destroyed IndexedMesh" }
    }

    class InstanceBuffer(val buffer: Buffer, val maxInsts: Int)
}