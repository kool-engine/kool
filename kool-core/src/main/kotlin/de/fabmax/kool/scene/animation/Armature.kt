package de.fabmax.kool.scene.animation

import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.util.*

class Armature(val meshData: MeshData) {

    val rootBones = mutableListOf<Bone>()
    val bones = mutableMapOf<String, Bone>()

    private val withNormals = meshData.hasAttribute(Attribute.NORMALS)

    private val animations = mutableMapOf<String, Animation>()
    private val animationList = mutableListOf<Animation>()
    private var animationPos = 0f
    var animationSpeed = 1f

    private val transform = Mat4fStack()
    private val tmpVec = MutableVec3f()

    private val originalMeshData = MeshData(meshData.vertexAttributes)
    private val meshV: IndexedVertexList.Vertex = meshData[0]
    private val origV: IndexedVertexList.Vertex

    init {
        for (i in 0 until meshData.numVertices) {
            meshV.index = i
            originalMeshData.addVertex {
                position.set(meshV.position)
                if (withNormals) {
                    normal.set(meshV.normal)
                }
            }
        }
        origV = originalMeshData[0]
    }

    fun getAnimation(name: String): Animation? {
        return animations[name]
    }

    fun addAnimation(name: String, animation: Animation) {
        animations[name] = animation
        animationList += animation
    }

    fun removeAnimation(name: String) {
        animationList.remove(animations.remove(name))
    }

    fun normalizeBoneWeights() {
        val sums = FloatArray(originalMeshData.numVertices)
        for (bone in bones.values) {
            for (i in bone.vertexIds.indices) {
                sums[bone.vertexIds[i]] += bone.vertexWeights[i]
            }
        }

        for (bone in bones.values) {
            for (i in bone.vertexIds.indices) {
                bone.vertexWeights[i] /= sums[bone.vertexIds[i]]
            }
        }
    }

    fun applyAnimation(deltaT: Double) {
        var clear = true
        var weightedDuration = 0f
        for (i in animationList.indices) {
            val anim = animationList[i]
            if (anim.weight > 0) {
                weightedDuration += anim.duration * anim.weight
            }
        }
        animationPos = (animationPos + deltaT.toFloat() / weightedDuration * animationSpeed) % 1f
        for (i in animationList.indices) {
            val anim = animationList[i]
            if (anim.weight > 0) {
                anim.apply(animationPos, clear)
                clear = false
            }
        }

        if (!clear) {
            // only update mesh if an animation was applied
            meshData.isBatchUpdate = true
            meshData.isSyncRequired = true
            clearMesh()
            for (i in rootBones.indices) {
                applyBone(rootBones[i], transform)
            }
            meshData.isBatchUpdate = false
        }
    }

    private fun applyBone(bone: Bone, transform: Mat4fStack) {
        transform.push()

        transform.mul(bone.transform)
        softTransformMesh(bone, transform)
        for (i in bone.children.indices) {
            applyBone(bone.children[i], transform)
        }

        transform.pop()
    }

    private fun clearMesh() {
        for (i in 0 until meshData.numVertices) {
            meshV.index = i
            meshV.position.set(0f, 0f, 0f)
            if (withNormals) {
                meshV.normal.set(0f, 0f, 0f)
            }
        }
    }

    private fun softTransformMesh(bone: Bone, transform: Mat4f) {
        for (i in bone.vertexIds.indices) {
            meshV.index = bone.vertexIds[i]
            origV.index = bone.vertexIds[i]

            tmpVec.set(origV.position)
            bone.offsetMatrix.transform(tmpVec)
            transform.transform(tmpVec)
            tmpVec *= bone.vertexWeights[i]
            meshV.position += tmpVec

            if (withNormals) {
                tmpVec.set(origV.normal)
                bone.offsetMatrix.transform(tmpVec, 0f)
                transform.transform(tmpVec, 0f)
                tmpVec *= bone.vertexWeights[i]
                meshV.normal += tmpVec
            }
        }
    }
}