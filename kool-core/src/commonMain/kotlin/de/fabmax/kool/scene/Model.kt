package de.fabmax.kool.scene

import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.scene.animation.Animation
import de.fabmax.kool.scene.animation.Skin

class Model(name: String? = null) : Node(name) {

    val nodes = mutableMapOf<String, Node>()
    val meshes = mutableMapOf<String, Mesh>()
    val textures = mutableMapOf<String, Texture2d>()

    val animations = mutableListOf<Animation>()
    val skins = mutableListOf<Skin>()

    fun disableAllAnimations() {
        enableAnimation(-1)
    }

    fun enableAnimation(iAnimation: Int) {
        for (i in animations.indices) {
            animations[i].weight = if (i == iAnimation) 1f else 0f
        }
    }

    fun setAnimationWeight(iAnimation: Int, weight: Float) {
        if (iAnimation in animations.indices) {
            animations[iAnimation].weight = weight
        }
    }

    fun applyAnimation(deltaT: Float) {
        for (i in animations.indices) {
            animations[i].reset()
        }

        var firstActive = true
        for (i in animations.indices) {
            if (animations[i].weight > 0f) {
                animations[i].apply(deltaT, firstActive)
                firstActive = false
            }
        }
        for (i in skins.indices) {
            skins[i].updateJointTransforms()
        }
    }

    fun printHierarchy() {
        printHierarchy("")
    }

    private fun Node.printHierarchy(indent: String) {
        println("$indent$name [${children.filterIsInstance<Mesh>().count()} meshes]")
        children.forEach {
            if (it !is Mesh) {
                it.printHierarchy("$indent    ")
            } else {
                println("$indent    ${it.name}")
            }
        }
    }

    override fun release() {
        textures.values.forEach { it.release() }
        super.release()
    }
}