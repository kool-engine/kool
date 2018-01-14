package de.fabmax.kool.scene.animation

import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.Mat4f
import de.fabmax.kool.util.Mat4fStack
import de.fabmax.kool.util.MutableVec3f

class Armature(val meshData: MeshData) {

    val rootBones = mutableListOf<Bone>()
    val bones = mutableMapOf<String, Bone>()

    val animations = mutableMapOf<String, Animation>()
    var activeAnimation = ""

    private val transform = Mat4fStack()
    private val tmpVec = MutableVec3f()

    private val originalMeshData = MeshData(meshData.hasNormals, false, false)
    private val meshV: IndexedVertexList.Item = meshData.data[0]
    private val origV: IndexedVertexList.Item

    init {
        for (i in 0 until meshData.data.size) {
            meshV.index = i
            originalMeshData.addVertex {
                position.set(meshV.position)
                if (meshData.hasNormals) {
                    normal.set(meshV.normal)
                }
            }
        }
        origV = originalMeshData.data[0]
    }

    fun normalizeBoneWeights() {
        val sums = FloatArray(originalMeshData.data.size)
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

    fun applyAnimation(time: Double) {
        meshData.isBatchUpdate = true
        meshData.isSyncRequired = true

        clearMesh()

        animations[activeAnimation]?.apply(time)
        for (i in rootBones.indices) {
            applyBone(rootBones[i], transform)
        }

        meshData.isBatchUpdate = false
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
        for (i in 0 until meshData.data.size) {
            meshV.index = i
            meshV.position.set(0f, 0f, 0f)
            if (meshData.hasNormals) {
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

            if (meshData.hasNormals) {
                tmpVec.set(origV.normal)
                bone.offsetMatrix.transform(tmpVec, 0f)
                transform.transform(tmpVec, 0f)
                tmpVec *= bone.vertexWeights[i]
                meshV.normal += tmpVec
            }
        }
    }
}