package de.fabmax.kool.scene.animation

import de.fabmax.kool.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.AttributeType
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.Mat4f
import de.fabmax.kool.util.Mat4fStack
import de.fabmax.kool.util.MutableVec3f

class Armature(meshData: MeshData, name: String?) : Mesh(meshData, name) {

    val rootBones = mutableListOf<Bone>()
    val bones = mutableMapOf<String, Bone>()

    private val withNormals = meshData.hasAttribute(Attribute.NORMALS)

    // animations are held in map and list, to get index-based list access for less overhead in render loop
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

    override fun render(ctx: RenderContext) {
        applyAnimation(ctx.deltaT)
        super.render(ctx)
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

    companion object {
        const val MAX_BONES = 100

        val BONE_WEIGHTS = Attribute("attrib_bone_weights", AttributeType.VEC_4F)
        val BONE_INDICES = Attribute("attrib_bone_indices", AttributeType.VEC_4I)
    }
}