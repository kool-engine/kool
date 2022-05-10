package de.fabmax.kool.modules.ksl

class BasicVertexConfig {

    var isInstanced = false
    var isFlipBacksideNormals = true
    var maxNumberOfBones = 0
    val isArmature: Boolean
        get() = maxNumberOfBones > 0

    fun enableArmature(maxNumberOfBones: Int = 32) {
        this.maxNumberOfBones = maxNumberOfBones
    }

}