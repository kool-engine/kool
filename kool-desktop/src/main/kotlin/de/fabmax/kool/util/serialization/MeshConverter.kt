package de.fabmax.kool.util.serialization

import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.Vec4f
import org.lwjgl.PointerBuffer
import org.lwjgl.assimp.*

object MeshConverter {

    fun convertMeshes(file: String): List<MeshData> =
            convertMeshes(Assimp.aiImportFile(file, Assimp.aiProcess_JoinIdenticalVertices))

    fun convertMeshes(aiScene: AIScene): List<MeshData> {
        val meshes = mutableListOf<MeshData>()

        val nodes = mutableMapOf<String, SceneNode>()
        traverseSceneGraph(aiScene.mRootNode(), null, nodes)

        aiScene.mMeshes().meshes().forEach { aiMesh ->
            val meshName = aiMesh.mName().asString()
            val posList = mutableListOf<Float>()
            val normalList = mutableListOf<Float>()
            val uvList = mutableListOf<Float>()
            val colorList = mutableListOf<Float>()
            val triList = makeTriangles(aiMesh)
            val armature = makeArmature(aiMesh, nodes)
            val animations = makeAnimations(aiScene)

            makeVertices(aiMesh, posList, normalList, uvList, colorList)

            meshes += MeshData(meshName, triList, posList, normalList, uvList, colorList, armature, animations)
        }

        return meshes
    }

    /**
     * fixme: method currently takes all animations in the scene instead of only the ones relevant for the current
     * mesh. Not good if there are more than one mesh in the scene
     */
    private fun makeAnimations(aiScene: AIScene): List<AnimationData> {
        val animations = mutableListOf<AnimationData>()

        for (anim in aiScene.mAnimations().animations()) {
            val name = anim.mName().asString()
            val ticks = anim.mTicksPerSecond()
            val channels = mutableListOf<NodeAnimationData>()
            animations += AnimationData(name, (anim.mDuration() / ticks).toFloat(), channels)

            for (channel in anim.mChannels().nodeAnimations()) {
                val chName = channel.mNodeName().asString()
                val rotKeys = mutableListOf<Vec4KeyData>()
                val posKeys = mutableListOf<Vec3KeyData>()
                val scalingKeys = mutableListOf<Vec3KeyData>()
                channels += NodeAnimationData(chName, posKeys, rotKeys, scalingKeys)

                var prevRot: Vec4KeyData? = null
                for (key in channel.mRotationKeys()) {
                    val q = key.mValue()
                    if (prevRot == null || q.x() != prevRot.x || q.y() != prevRot.y || q.z() != prevRot.z ||
                            q.w() != prevRot.w || !channel.mRotationKeys().hasRemaining()) {
                        val time = (key.mTime() / ticks).toFloat()
                        val rot = Vec4KeyData(time, q.x(), q.y(), q.z(), q.w())
                        rotKeys += rot
                        prevRot = rot
                    }
                }

                var prevPos: Vec3KeyData? = null
                for (key in channel.mPositionKeys()) {
                    val v = key.mValue()
                    if (prevPos == null || v.x() != prevPos.x || v.y() != prevPos.y || v.z() != prevPos.z ||
                            !channel.mPositionKeys().hasRemaining()) {
                        val time = (key.mTime() / ticks).toFloat()
                        val pos = Vec3KeyData(time, v.x(), v.y(), v.z())
                        posKeys += pos
                        prevPos = pos
                    }
                }

                var prevScl: Vec3KeyData? = null
                for (key in channel.mScalingKeys()) {
                    val v = key.mValue()
                    if (prevScl == null || v.x() != prevScl.x || v.y() != prevScl.y || v.z() != prevScl.z ||
                            !channel.mScalingKeys().hasRemaining()) {
                        val time = (key.mTime() / ticks).toFloat()
                        val scl = Vec3KeyData(time, v.x(), v.y(), v.z())
                        scalingKeys += scl
                        prevScl = scl
                    }
                }
            }
        }

        return animations
    }

    private fun makeArmature(aiMesh: AIMesh, nodes: Map<String, SceneNode>): List<BoneData> {
        val armature = mutableListOf<BoneData>()
        aiMesh.mBones().bones().forEach { aiBone ->
            val name = aiBone.mName().asString()
            val parent = nodes[name]?.parent?.name ?: ""
            val children = nodes[name]?.getChildrenNames() ?: emptyList()
            val offsetMat = aiBone.mOffsetMatrix().asFloats()
            val ids = mutableListOf<Int>()
            val weights = mutableListOf<Float>()

            for (vertWeight in aiBone.mWeights()) {
                ids += vertWeight.mVertexId()
                weights += vertWeight.mWeight()
            }

            armature += BoneData(name, parent, children, offsetMat, ids, weights)
        }
        return armature
    }

    private fun makeTriangles(aiMesh: AIMesh): List<Int> {
        val triList = mutableListOf<Int>()

        for (face in aiMesh.mFaces()) {
            val idcs = face.mIndices()
            when (face.mNumIndices()) {
                3 -> {
                    triList += idcs[0]
                    triList += idcs[1]
                    triList += idcs[2]
                }
                4 -> {
                    triList += idcs[0]
                    triList += idcs[1]
                    triList += idcs[2]

                    triList += idcs[0]
                    triList += idcs[2]
                    triList += idcs[3]
                }
                else -> throw IllegalArgumentException("Invalid number of face vertices: ${face.mNumIndices()}")
            }
        }
        return triList
    }

    private fun makeVertices(aiMesh: AIMesh, posList: MutableList<Float>, normalList: MutableList<Float>,
                             uvList: MutableList<Float>, colorList: MutableList<Float>) {

        val positions = aiMesh.mVertices()
        val normals: AIVector3D.Buffer? = aiMesh.mNormals()
        val uvs: AIVector3D.Buffer? = aiMesh.mTextureCoords(0)
        val colors: AIColor4D.Buffer? = aiMesh.mColors(0)

        for (i in 0 until aiMesh.mNumVertices()) {
            posList += positions[i].x()
            posList += positions[i].y()
            posList += positions[i].z()

            if (normals != null) {
                normalList += normals[i].x()
                normalList += normals[i].y()
                normalList += normals[i].z()
            }

            if (uvs != null) {
                uvList += uvs[i].x()
                uvList += uvs[i].y()
            }

            if (colors != null) {
                colorList += colors[i].r()
                colorList += colors[i].g()
                colorList += colors[i].b()
                colorList += colors[i].a()
            }
        }
    }

    private fun traverseSceneGraph(aiNode: AINode, parent: SceneNode?, result: MutableMap<String, SceneNode>) {
        val node = SceneNode(aiNode.mName().asString(), parent, aiNode)
        result[node.name] = node

        if (parent != null) {
            parent.children += node
        }

        if (aiNode.mNumChildren() > 0) {
            aiNode.mChildren().nodes().forEach {
                traverseSceneGraph(it, node, result)
            }
        }
    }

    private fun AIMatrix4x4.asFloats(): List<Float> {
        val floats = mutableListOf<Float>()
        floats += a1();     floats += b1();     floats += c1();     floats += d1()
        floats += a2();     floats += b2();     floats += c2();     floats += d2()
        floats += a3();     floats += b3();     floats += c3();     floats += d3()
        floats += a4();     floats += b4();     floats += c4();     floats += d4()
        return floats
    }

    private fun AIVector3D.asVec3f(): Vec3f = Vec3f(x(), y(), z())

    private fun AIQuaternion.asVec4f(): Vec4f = Vec4f(x(), y(), z(), w())

    private fun AIString.asString(): String {
        return String(ByteArray(data().capacity()) { i -> data()[i] })
    }

    private class SceneNode(val name: String, val parent: SceneNode?, val aiNode: AINode) {
        val children = mutableListOf<SceneNode>()

        fun getChildrenNames(): List<String> = children.map { it.name }
    }

    private open class PointerIt<out T>(private val buf: PointerBuffer?, private val fac: (Long) -> T) : Iterator<T> {
        override fun hasNext(): Boolean = buf?.hasRemaining() ?: false
        override fun next(): T = fac(buf!!.get())
    }

    private class AnimationPointerIt(buf: PointerBuffer?) : PointerIt<AIAnimation>(buf, { ptr -> AIAnimation.create(ptr) })
    private fun PointerBuffer?.animations(): AnimationPointerIt = AnimationPointerIt(this)

    private class BonePointerIt(buf: PointerBuffer?) : PointerIt<AIBone>(buf, { ptr -> AIBone.create(ptr) })
    private fun PointerBuffer?.bones(): BonePointerIt = BonePointerIt(this)

    private class MeshPointerIt(buf: PointerBuffer?) : PointerIt<AIMesh>(buf, { ptr -> AIMesh.create(ptr) })
    private fun PointerBuffer?.meshes(): MeshPointerIt = MeshPointerIt(this)

    private class NodeAnimationPointerIt(buf: PointerBuffer?) : PointerIt<AINodeAnim>(buf, { ptr -> AINodeAnim.create(ptr) })
    private fun PointerBuffer?.nodeAnimations(): NodeAnimationPointerIt = NodeAnimationPointerIt(this)

    private class NodePointerIt(buf: PointerBuffer?) : PointerIt<AINode>(buf, { ptr -> AINode.create(ptr) })
    private fun PointerBuffer?.nodes(): NodePointerIt = NodePointerIt(this)
}