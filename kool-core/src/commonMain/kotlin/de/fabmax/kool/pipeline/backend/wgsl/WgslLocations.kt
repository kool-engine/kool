package de.fabmax.kool.pipeline.backend.wgsl

import de.fabmax.kool.modules.ksl.lang.KslVertexAttribute
import de.fabmax.kool.pipeline.*

class WgslLocations(val bindingLayout: BindGroupLayouts, val vertexLayout: VertexLayout?) {

    val bindingLocations = buildMap {
        bindingLayout.asList.forEach { group ->
            var nextBinding = 0
            group.bindings.forEach { binding ->
                put(binding, Location(group.group, nextBinding))
                when (binding) {
                    is UniformBufferLayout<*> -> nextBinding++
                    is StorageBufferLayout -> nextBinding++

                    // textures require two binding slots (1st: sampler, 2nd: texture)
                    is Texture1dLayout -> nextBinding += 2
                    is Texture2dLayout -> nextBinding += 2
                    is Texture3dLayout -> nextBinding += 2
                    is TextureCubeLayout -> nextBinding += 2
                    is Texture2dArrayLayout -> nextBinding += 2
                    is TextureCubeArrayLayout -> nextBinding += 2

                    is StorageTexture1dLayout -> nextBinding++
                    is StorageTexture2dLayout -> nextBinding++
                    is StorageTexture3dLayout -> nextBinding++
                }
            }
        }
    }

    val vertexLocations = buildMap {
        val matrixCols = mapOf(GpuType.Mat2 to 2, GpuType.Mat3 to 3, GpuType.Mat4 to 4)
        var vLoc = 0

        vertexLayout?.let { layout ->
            layout.bindings
                .sortedBy { it.inputRate.name }     // INSTANCE first, VERTEX second
                .flatMap { it.vertexAttributes }
                .forEach { attr ->
                    val cols = matrixCols.getOrElse(attr.type) { 1 }
                    val locs = (0 until cols).map { i ->
                        if (cols == 1) {
                            VertexAttributeLocation(attr.name, vLoc++)
                        } else {
                            VertexAttributeLocation("${attr.name}_$i", vLoc++)
                        }
                    }
                    put(attr.name, locs)
                }
        }
    }

    operator fun get(binding: BindingLayout): Location {
        return bindingLocations[binding]!!
    }

    operator fun get(attribute: VertexLayout.VertexAttribute): List<VertexAttributeLocation> {
        return vertexLocations[attribute.name]!!
    }

    operator fun get(attribute: KslVertexAttribute<*>): List<VertexAttributeLocation> {
        return vertexLocations[attribute.name]!!
    }

    data class Location(val group: Int, val binding: Int)

    data class VertexAttributeLocation(val name: String, val location: Int)
}