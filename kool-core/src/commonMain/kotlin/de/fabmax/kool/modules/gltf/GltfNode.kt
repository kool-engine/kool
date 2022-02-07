package de.fabmax.kool.modules.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A node in the node hierarchy. When the node contains skin, all mesh.primitives must contain JOINTS_0 and WEIGHTS_0
 * attributes. A node can have either a matrix or any combination of translation/rotation/scale (TRS) properties. TRS
 * properties are converted to matrices and postmultiplied in the T * R * S order to compose the transformation matrix;
 * first the scale is applied to the vertices, then the rotation, and then the translation. If none are provided, the
 * transform is the identity. When a node is targeted for animation (referenced by an animation.channel.target), only
 * TRS properties may be present; matrix will not be present.
 *
 * @param camera      The index of the camera referenced by this node.
 * @param children    The indices of this node's children.
 * @param skin        The index of the skin referenced by this node.
 * @param matrix      A floating-point 4x4 transformation matrix stored in column-major order.
 * @param mesh        The index of the mesh in this node.
 * @param rotation    The node's unit quaternion rotation in the order (x, y, z, w), where w is the scalar.
 * @param scale       The node's non-uniform scale, given as the scaling factors along the x, y, and z axes.
 * @param translation The node's translation along the x, y, and z axes.
 * @param weights     The weights of the instantiated Morph Target. Number of elements must match number of Morph
 *                    Targets of used mesh.
 * @param name        The user-defined name of this object.
 */
@Serializable
data class GltfNode(
    val camera: Int = -1,
    val children: List<Int> = emptyList(),
    val skin: Int = -1,
    val matrix: List<Float>? = null,
    val mesh: Int = -1,
    val rotation: List<Float>? = null,
    val scale: List<Float>? = null,
    val translation: List<Float>? = null,
    val weights: List<Float>? = null,
    val name: String? = null
) {
    @Transient
    lateinit var childRefs: List<GltfNode>
    @Transient
    var meshRef: GltfMesh? = null
    @Transient
    var skinRef: GltfSkin? = null
}