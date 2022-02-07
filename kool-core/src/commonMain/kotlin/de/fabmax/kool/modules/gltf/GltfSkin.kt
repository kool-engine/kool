package de.fabmax.kool.modules.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Joints and matrices defining a skin.
 *
 * @param inverseBindMatrices The index of the accessor containing the floating-point 4x4 inverse-bind matrices. The
 *                            default is that each matrix is a 4x4 identity matrix, which implies that inverse-bind
 *                            matrices were pre-applied.
 * @param skeleton            The index of the node used as a skeleton root.
 * @param joints              Indices of skeleton nodes, used as joints in this skin.
 * @param name                The user-defined name of this object.
 */
@Serializable
data class GltfSkin(
    val inverseBindMatrices: Int = -1,
    val skeleton: Int = -1,
    val joints: List<Int>,
    val name: String? = null
) {
    @Transient
    var inverseBindMatrixAccessorRef: GltfAccessor? = null
    @Transient
    lateinit var jointRefs: List<GltfNode>
}