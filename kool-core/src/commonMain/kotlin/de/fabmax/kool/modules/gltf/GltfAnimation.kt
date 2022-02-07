package de.fabmax.kool.modules.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A keyframe animation.
 *
 * @param channels An array of channels, each of which targets an animation's sampler at a node's property. Different
 *                 channels of the same animation can't have equal targets.
 * @param samplers An array of samplers that combines input and output accessors with an interpolation algorithm to
 *                 define a keyframe graph (but not its target).
 * @param name     The user-defined name of this object.
 */
@Serializable
data class GltfAnimation(
    val channels: List<Channel>,
    val samplers: List<Sampler>,
    val name: String? = null
) {

    /**
     * Targets an animation's sampler at a node's property.
     *
     * @param sampler The index of a sampler in this animation used to compute the value for the target.
     * @param target  The index of the node and TRS property to target.
     */
    @Serializable
    data class Channel(
            val sampler: Int,
            val target: Target
    ) {
        @Transient
        lateinit var samplerRef: Sampler
    }

    /**
     * The index of the node and TRS property that an animation channel targets.
     *
     * @param node The index of the node to target.
     * @param path The name of the node's TRS property to modify, or the "weights" of the Morph Targets it instantiates.
     *             For the "translation" property, the values that are provided by the sampler are the translation along
     *             the x, y, and z axes. For the "rotation" property, the values are a quaternion in the order
     *             (x, y, z, w), where w is the scalar. For the "scale" property, the values are the scaling factors along
     *             the x, y, and z axes.
     */
    @Serializable
    data class Target(
            val node: Int = -1,
            val path: String
    ) {
        @Transient
        var nodeRef: GltfNode? = null

        companion object {
            const val PATH_TRANSLATION = "translation"
            const val PATH_ROTATION = "rotation"
            const val PATH_SCALE = "scale"
            const val PATH_WEIGHTS = "weights"
        }
    }

    /**
     * Combines input and output accessors with an interpolation algorithm to define a keyframe graph (but not its target).
     *
     * @param input         The index of an accessor containing keyframe input values, e.g., time.
     * @param interpolation Interpolation algorithm (linear, step or cubic spline).
     * @param output        The index of an accessor, containing keyframe output values.
     */
    @Serializable
    data class Sampler(
            val input: Int,
            val interpolation: String = INTERPOLATION_LINEAR,
            val output: Int
    ) {
        @Transient
        lateinit var inputAccessorRef: GltfAccessor

        @Transient
        lateinit var outputAccessorRef: GltfAccessor

        companion object {
            const val INTERPOLATION_LINEAR = "LINEAR"
            const val INTERPOLATION_STEP = "STEP"
            const val INTERPOLATION_CUBICSPLINE = "CUBICSPLINE"
        }
    }
}