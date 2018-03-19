package de.fabmax.kool.scene.animation

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.Mat4fStack
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.AttributeType
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.createFloat32Buffer
import de.fabmax.kool.util.logW

class Armature(meshData: MeshData, name: String?) : Mesh(meshData, name) {

    val rootBones = mutableListOf<Bone>()
    val bones = mutableMapOf<String, Bone>()

    private val indexedBones = mutableListOf<Bone>()
    private var boneTransforms: Float32Buffer? = null

    // to do mesh animation in vertex shader we need to add integer mesh attributes (OpenGL (ES) >= 3.0)
    var isCpuAnimated = false

    // animations are held in map and list, to get index-based list access for less overhead in render loop
    private val animations = mutableMapOf<String, Animation>()
    private val animationList = mutableListOf<Animation>()
    private var animationPos = 0f

    var animationSpeed = 1f

    private val transform = Mat4fStack()
    private val tmpTransform = Mat4f()
    private val tmpVec = MutableVec3f()

    private val originalMeshData = meshData
    private val meshV: IndexedVertexList.Vertex
    private val origV: IndexedVertexList.Vertex

    private data class BoneWeight(var weight: Float, var id: Int)

    init {
        // extend mesh data with bone vertex attributes
        val armatureAttribs = mutableSetOf(BONE_WEIGHTS, BONE_INDICES)
        armatureAttribs.addAll(meshData.vertexAttributes)
        this.meshData = MeshData(armatureAttribs)

        origV = originalMeshData[0]
        for (i in 0 until originalMeshData.numVertices) {
            origV.index = i
            this.meshData.addVertex { set(origV) }
        }
        for (i in 0 until originalMeshData.numIndices) {
            this.meshData.addIndex(originalMeshData.vertexList.indices[i])
        }
        meshV = this.meshData[0]
    }

    private fun addBoneWeight(boneWeights: Array<BoneWeight>, boneId: Int, boneWeight: Float) {
        for (i in 0..3) {
            if (boneWeight > boneWeights[i].weight) {
                boneWeights[i].weight = boneWeight
                boneWeights[i].id = boneId
                break
            }
        }
    }

    /**
     * Normalizes bone weights such that the sum of weights of all influencing bones per vertex is one. Also every
     * vertex can be influenced by up to four bones. In case more bones influence a vertex the four bones with highest
     * weights are chosen.
     */
    fun updateBones() {
        indexedBones.clear()
        indexedBones.addAll(bones.values)

        boneTransforms = createFloat32Buffer(indexedBones.size * 16)
        tmpTransform.setIdentity()

        // collect bone weights per vertex
        val boneWeights = Array(meshData.numVertices) { Array(4) { BoneWeight(0f, 0) } }
        indexedBones.forEachIndexed { boneId, bone ->
            bone.id = boneId
            boneTransforms!!.position = boneId * 16
            tmpTransform.toBuffer(boneTransforms!!)
            boneTransforms!!.limit = boneTransforms!!.capacity

            for (i in bone.vertexIds.indices) {
                val vertexId = bone.vertexIds[i]
                addBoneWeight(boneWeights[vertexId], boneId, bone.vertexWeights[i])
            }
        }

        // normalize bone weights
        for (vertexBoneWeights in boneWeights) {
            val weightSum = vertexBoneWeights.sumByDouble { w -> w.weight.toDouble() }.toFloat()
            vertexBoneWeights.forEach { w -> w.weight /= weightSum }
        }

        // write back normalized weights ton bones to get normalized weights for cpu animation
        indexedBones.forEachIndexed { boneId, bone ->
            for (i in bone.vertexIds.indices) {
                val vertexId = bone.vertexIds[i]
                bone.vertexWeights[i] = 0f
                for (boneW in boneWeights[vertexId]) {
                    if (boneW.id == boneId) {
                        bone.vertexWeights[i] = boneW.weight
                        break
                    }
                }
            }
        }

        // set bone vertex attributes
        for (i in 0 until meshData.numVertices) {
            val boneWs = boneWeights[i]
            meshV.index = i
            meshV.getVec4fAttribute(BONE_WEIGHTS)?.set(boneWs[0].weight, boneWs[1].weight, boneWs[2].weight, boneWs[3].weight)
            meshV.getVec4iAttribute(BONE_INDICES)?.set(boneWs[0].id, boneWs[1].id, boneWs[2].id, boneWs[3].id)
        }
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

    override fun preRender(ctx: KoolContext) {
        if (ctx.deltaT > 0) {
            applyAnimation(ctx)
        }
        super.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        val shader = this.shader
        if (shader is BasicShader) {
            shader.bones = boneTransforms
        }
        super.render(ctx)
    }

    private fun applyAnimation(ctx: KoolContext) {
        var update = false

        // interpolate animation duration of active animations (needed for smooth interpolation between animations)
        var weightedDuration = 0f
        for (i in animationList.indices) {
            val anim = animationList[i]
            if (anim.weight > 0) {
                weightedDuration += anim.duration * anim.weight
            }
        }
        animationPos = (animationPos + ctx.deltaT / weightedDuration * animationSpeed) % 1f

        // update all active (weighted) animations
        for (i in animationList.indices) {
            val anim = animationList[i]
            if (anim.weight > 0) {
                // clear bone transform before applying first animation with weight > 0
                anim.apply(animationPos, !update)
                update = true
            }
        }

        if (update) {
            if (!isCpuAnimated && !ctx.glCapabilities.shaderIntAttribs) {
                logW { "Vertex shader animation requested, but not supported by hardware. Falling back to CPU based mesh animation" }
                isCpuAnimated = true
            }

            // only update mesh if an animation was applied
            if (isCpuAnimated) {
                // transform mesh vertex positions on CPU
                meshData.batchUpdate {
                    clearMesh()
                    for (i in rootBones.indices) {
                        applyBone(rootBones[i], transform, isCpuAnimated)
                    }
                }

            } else {
                // transform mesh vertex positions on vertex shader, we only need to compute bone transforms
                for (i in rootBones.indices) {
                    applyBone(rootBones[i], transform, isCpuAnimated)
                }

            }
        }
    }

    private fun applyBone(bone: Bone, transform: Mat4fStack, updateMesh: Boolean) {
        transform.push()
        transform.mul(bone.transform).mul(bone.offsetMatrix, tmpTransform)

        if (updateMesh) {
            // transform mesh vertices on CPU
            softTransformMesh(bone, tmpTransform)

        } else {
            // set bone transform matrix for use in vertex shader
            boneTransforms!!.position = 16 * bone.id
            tmpTransform.toBuffer(boneTransforms!!)
            boneTransforms!!.limit = boneTransforms!!.capacity
        }

        for (i in bone.children.indices) {
            applyBone(bone.children[i], transform, updateMesh)
        }

        transform.pop()
    }

    private fun clearMesh() {
        for (i in 0 until meshData.numVertices) {
            meshV.index = i
            meshV.position.set(0f, 0f, 0f)
            meshV.normal.set(0f, 0f, 0f)
        }
    }

    private fun softTransformMesh(bone: Bone, transform: Mat4f) {
        for (i in bone.vertexIds.indices) {
            meshV.index = bone.vertexIds[i]
            origV.index = bone.vertexIds[i]

            tmpVec.set(origV.position)
            transform.transform(tmpVec)
            tmpVec *= bone.vertexWeights[i]
            meshV.position += tmpVec

            tmpVec.set(origV.normal)
            transform.transform(tmpVec, 0f)
            tmpVec *= bone.vertexWeights[i]
            meshV.normal += tmpVec
        }
    }

    companion object {
        val BONE_WEIGHTS = Attribute("attrib_bone_weights", AttributeType.VEC_4F)
        val BONE_INDICES = Attribute("attrib_bone_indices", AttributeType.VEC_4I)
    }
}