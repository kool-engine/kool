package de.fabmax.kool.platform.vk

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.Uint32BufferImpl
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.serialization.ModelConverter
import de.fabmax.kool.util.serialization.ModelMeshData
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_CPU_ONLY
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_GPU_ONLY
import org.lwjgl.vulkan.VK10.*

class IndexedMesh(val sys: VkSystem, val data: IndexedVertexList) : VkResource() {

    val numVertices = data.numVertices
    val numIndices = data.indices.position

    val vertexBuffer = createVertexBuffer()
    val indexBuffer = createIndexBuffer()

    init {
        addDependingResource(vertexBuffer)
        addDependingResource(indexBuffer)
    }

    private fun createVertexBuffer(): Buffer {
        memStack {
            val bufferSize = numVertices * data.strideBytesF.toLong()
            val stagingAllocUsage = VMA_MEMORY_USAGE_CPU_ONLY
            val stagingBuffer = Buffer(sys, bufferSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
            stagingBuffer.mappedFloats {
                data.dataF.flip()
                put((data.dataF as Float32BufferImpl).buffer)
            }

            val usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT or VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
            val allocUsage = VMA_MEMORY_USAGE_GPU_ONLY
            val buffer = Buffer(sys, bufferSize, usage, allocUsage)
            buffer.put(stagingBuffer)
            stagingBuffer.destroy()
            return buffer
        }
    }

    private fun createIndexBuffer(): Buffer {
        memStack {
            val bufferSize = numIndices * 4L
            val stagingAllocUsage = VMA_MEMORY_USAGE_CPU_ONLY
            val stagingBuffer = Buffer(sys, bufferSize, VK_BUFFER_USAGE_TRANSFER_SRC_BIT, stagingAllocUsage)
            stagingBuffer.mappedInts {
                data.indices.flip()
                put((data.indices as Uint32BufferImpl).buffer)
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

    companion object {
        fun loadModel(sys: VkSystem, path: String): IndexedMesh {
            logI { "Loading model $path..." }
            val model = ModelConverter.convertModel(path, false)
            val positions = model.meshes[0].attributes[ModelMeshData.ATTRIB_POSITIONS] ?: throw NoSuchElementException("No positions")
            val texCoords = model.meshes[0].attributes[ModelMeshData.ATTRIB_TEXTURE_COORDS] ?: throw NoSuchElementException("No texture coordinates")

            val data = IndexedVertexList(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS))
            for (i in 0 until model.meshes[0].numVertices) {
                data.addVertex {
                    position.set(positions[i*3 + 0], positions[i*3 + 1], positions[i*3 + 2])
                    normal.set(1f, 1f, 1f)
                    texCoord.set(texCoords[i*2 + 0], 1f - texCoords[i*2 + 1])
                }
            }
            data.addIndices(model.meshes[0].indices)
            val mesh = IndexedMesh(sys, data)
            logI { "Loaded ${mesh.numVertices} vertices / ${mesh.numIndices / 3} triangles" }
            return mesh
        }
    }
}