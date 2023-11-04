package de.fabmax.kool.scene.animation

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.scene.Node

class Skin {
    val nodes = mutableListOf<SkinNode>()

    fun updateJointTransforms() {
        for (i in nodes.indices) {
            if (!nodes[i].hasParent) {
                nodes[i].updateJointTransform()
            }
        }
    }

    fun printHierarchy() {
        nodes.filter { !it.hasParent }.forEach { it.printHierarchy("") }
    }

    class SkinNode(val joint: Node, val inverseBindMatrix: Mat4f) {
        val jointTransform = MutableMat4f()

        private val tmpMat4f = MutableMat4f()
        private var parent: SkinNode? = null
        private val children = mutableListOf<SkinNode>()

        val hasParent: Boolean
            get() = parent != null

        fun addChild(node: SkinNode) {
            node.parent = this
            children += node
        }

        fun updateJointTransform() {
            jointTransform.set(joint.transform.matrixF)
            for (i in children.indices) {
                children[i].updateJointTransform(jointTransform)
            }
            jointTransform.mul(inverseBindMatrix)
        }

        private fun updateJointTransform(parentTransform: Mat4f) {
            tmpMat4f.set(joint.transform.matrixF)
            jointTransform.set(parentTransform).mul(tmpMat4f)
            for (i in children.indices) {
                children[i].updateJointTransform(jointTransform)
            }
            jointTransform.mul(inverseBindMatrix)
        }

        fun printHierarchy(indent: String) {
            println("$indent${joint.name}")
            children.forEach {
                it.printHierarchy("$indent    ")
            }
        }
    }
}