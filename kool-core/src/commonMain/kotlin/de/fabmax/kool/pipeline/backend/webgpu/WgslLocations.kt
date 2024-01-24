package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*

class WgslLocations(val layout: BindGroupLayouts) {

    val bindingLocations = buildMap {
        layout.asList.forEach { group ->
            var nextBinding = 0
            group.bindings.forEach { binding ->
                put(binding, Location(group.group, nextBinding))
                when (binding) {
                    is UniformBufferLayout -> nextBinding++

                    // textures require two binding slots (1st: sampler, 2nd: texture)
                    is Texture1dLayout -> nextBinding += 2
                    is Texture2dLayout -> nextBinding += 2
                    is Texture3dLayout -> nextBinding += 2
                    is TextureCubeLayout -> nextBinding += 2

                    is StorageTexture1dLayout -> nextBinding++
                    is StorageTexture2dLayout -> nextBinding++
                    is StorageTexture3dLayout -> nextBinding++
                }
            }
        }
    }

    operator fun get(binding: BindingLayout): Location {
        return bindingLocations[binding]!!
    }

    data class Location(val group: Int, val binding: Int)
}