package de.fabmax.kool.scene.animation

import de.fabmax.kool.util.Mat4f

class Bone(val name: String, numVertices: Int) : AnimatedNode {
    var parent: Bone? = null
    val children = mutableListOf<Bone>()

    val offsetMatrix = Mat4f()
    val transform = Mat4f()

    var vertexIds = IntArray(numVertices)
    var vertexWeights = FloatArray(numVertices)

    override fun setTransform(transform: Mat4f) {
        this.transform.set(transform)
    }
}
