package de.fabmax.kool.scene.animation

import de.fabmax.kool.math.Mat4f

class Bone(val name: String, numVertices: Int) : AnimatedNode {
    var parent: Bone? = null
    var id = 0
    val children = mutableListOf<Bone>()

    val offsetMatrix = Mat4f()
    val transform = Mat4f()

    var vertexIds = IntArray(numVertices)
    var vertexWeights = FloatArray(numVertices)

    override fun clearTransform() {
        for (i in 0..15) {
            transform.matrix[i] = 0f
        }
    }

    override fun addTransform(transform: Mat4f, weight: Float) {
        for (i in 0..15) {
            this.transform.matrix[i] += transform[i] * weight
        }
    }
}
