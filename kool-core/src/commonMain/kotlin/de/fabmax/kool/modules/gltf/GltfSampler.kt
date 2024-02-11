package de.fabmax.kool.modules.gltf

import de.fabmax.kool.pipeline.AddressMode
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.util.logE
import kotlinx.serialization.Serializable

/**
 * Texture sampler properties for filtering and wrapping modes.
 *
 * @param magFilter Magnification filter.
 * @param minFilter Minification filter.
 * @param wrapS     S (U) wrapping mode.
 * @param wrapT     T (V) wrapping mode.
 * @param name      The user-defined name of this object.
 */
@Serializable
data class GltfSampler(
    val magFilter: Int = LINEAR,
    val minFilter: Int = LINEAR,
    val wrapS: Int = REPEAT,
    val wrapT: Int = REPEAT,
    val name: String? = null
) {

    val magFilterKool: FilterMethod
        get() = when (magFilter) {
            NEAREST -> FilterMethod.NEAREST
            LINEAR -> FilterMethod.LINEAR
            else -> {
                logE { "Invalid magFilter: $magFilter" }
                FilterMethod.LINEAR
            }
        }

    val minFilterKool: FilterMethod
        get() = when (minFilter) {
            NEAREST -> FilterMethod.NEAREST
            LINEAR -> FilterMethod.LINEAR
            NEAREST_MIPMAP_NEAREST -> FilterMethod.LINEAR
            LINEAR_MIPMAP_NEAREST -> FilterMethod.LINEAR
            NEAREST_MIPMAP_LINEAR -> FilterMethod.LINEAR
            LINEAR_MIPMAP_LINEAR -> FilterMethod.LINEAR
            else -> {
                logE { "Invalid minFilter: $minFilter" }
                FilterMethod.LINEAR
            }
        }

    val addressModeU: AddressMode
        get() = when (wrapS) {
            CLAMP_TOEDGE -> AddressMode.CLAMP_TO_EDGE
            MIRRORED_REPEAT -> AddressMode.MIRRORED_REPEAT
            REPEAT -> AddressMode.REPEAT
            else -> {
                logE { "Invalid wrapS: $wrapS" }
                AddressMode.REPEAT
            }
        }

    val addressModeV: AddressMode
        get() = when (wrapT) {
            CLAMP_TOEDGE -> AddressMode.CLAMP_TO_EDGE
            MIRRORED_REPEAT -> AddressMode.MIRRORED_REPEAT
            REPEAT -> AddressMode.REPEAT
            else -> {
                logE { "Invalid wrapT: $wrapS" }
                AddressMode.REPEAT
            }
        }

    companion object {
        const val NEAREST = 9728
        const val LINEAR = 9729
        const val NEAREST_MIPMAP_NEAREST = 9984
        const val LINEAR_MIPMAP_NEAREST = 9985
        const val NEAREST_MIPMAP_LINEAR = 9986
        const val LINEAR_MIPMAP_LINEAR = 9987

        const val CLAMP_TOEDGE = 33071
        const val MIRRORED_REPEAT = 33648
        const val REPEAT = 10497
    }
}