package de.fabmax.kool.util.serialization

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.shading.AttributeType
import org.lwjgl.PointerBuffer
import org.lwjgl.assimp.*

object MeshConverter {

    fun convertMeshes(file: String, invertFaceOrientation: Boolean = false): ModelData =
            convertMeshes(Assimp.aiImportFile(file, Assimp.aiProcess_JoinIdenticalVertices)!!, invertFaceOrientation)

    fun convertMeshes(aiScene: AIScene, invertFaceOrientation: Boolean = false): ModelData {
        val meshes = mutableListOf<MeshData>()

        val nodes = mutableMapOf<String, SceneNode>()
        val root = traverseSceneGraph(aiScene.mRootNode()!!, null, nodes)
        //logI { "Loaded scene: ${nodes.size} nodes" }
        //printSceneGraph(root, "")

        val materials = makeMaterials(aiScene)

        aiScene.mMeshes().meshes().forEach { aiMesh ->
            val meshName = aiMesh.mName().dataString()
            val posList = mutableListOf<Float>()
            val normalList = mutableListOf<Float>()
            val uvList = mutableListOf<Float>()
            val colorList = mutableListOf<Float>()
            val armature = makeArmature(aiMesh, nodes)
            val animations = makeAnimations(aiScene)

            makeVertices(aiMesh, posList, normalList, uvList, colorList)

            val indices = when (aiMesh.mPrimitiveTypes()) {
                Assimp.aiPrimitiveType_TRIANGLE -> makeTriangleIndices(aiMesh, invertFaceOrientation)
                Assimp.aiPrimitiveType_POLYGON -> makeTriangleIndices(aiMesh, invertFaceOrientation)
                else -> emptyList()
            }

            val attribs = mutableMapOf(MeshData.ATTRIB_POSITIONS to AttributeList(AttributeType.VEC_3F, posList))
            if (!normalList.isEmpty()) {
                attribs[MeshData.ATTRIB_NORMALS] = AttributeList(AttributeType.VEC_3F, normalList)
            }
            if (!uvList.isEmpty()) {
                attribs[MeshData.ATTRIB_TEXTURE_COORDS] = AttributeList(AttributeType.VEC_2F, uvList)
            }
            if (!colorList.isEmpty()) {
                attribs[MeshData.ATTRIB_COLORS] = AttributeList(AttributeType.COLOR_4F, colorList)
            }
            meshes += MeshData(meshName, PrimitiveType.TRIANGLES, attribs, indices, armature, animations, aiMesh.mMaterialIndex())
        }

        return ModelData(ModelData.VERSION, meshes, listOf(convertSceneGraph(root)), materials)
    }

    private fun makeMaterials(aiScene: AIScene): List<MaterialData> {
        val materials = mutableListOf<MaterialData>()

        aiScene.mMaterials().materials().forEach { mat ->
            var name = ""
            var ambientColor = emptyList<Float>()
            var diffuseColor = emptyList<Float>()
            var specularColor = emptyList<Float>()
            var emissiveColor = emptyList<Float>()
            var shininess = 10f
            var reflectivity = 0f

            mat.mProperties().materialProperties().forEach { prop ->
                val propName = prop.mKey().dataString()
                when (propName) {
                    Assimp.AI_MATKEY_NAME -> name = prop.getStringValue()
                    Assimp.AI_MATKEY_COLOR_AMBIENT -> ambientColor = prop.getFloatValue().toList()
                    Assimp.AI_MATKEY_COLOR_DIFFUSE -> diffuseColor = prop.getFloatValue().toList()
                    Assimp.AI_MATKEY_COLOR_SPECULAR -> specularColor = prop.getFloatValue().toList()
                    Assimp.AI_MATKEY_COLOR_EMISSIVE -> emissiveColor = prop.getFloatValue().toList()
                    Assimp.AI_MATKEY_SHININESS -> shininess = prop.getFloatValue()[0]
                    Assimp.AI_MATKEY_REFLECTIVITY -> reflectivity = prop.getFloatValue()[0]

                    // todo: support more material properties, textures!
                }
            }

            materials += MaterialData(name, ambientColor, diffuseColor, specularColor, emissiveColor, shininess, reflectivity)
        }

        return materials
    }

    private fun AIMaterialProperty.getFloatValue(): FloatArray {
        val floats = FloatArray(mDataLength() / 4)
        val buf = mData()
        for (i in floats.indices) {
            floats[i] = buf.float
        }
        return floats
    }

    private fun AIMaterialProperty.getDoubleValue(): DoubleArray {
        val doubles = DoubleArray(mDataLength() / 8)
        val buf = mData()
        for (i in doubles.indices) {
            doubles[i] = buf.double
        }
        return doubles
    }

    private fun AIMaterialProperty.getStringValue(): String {
        // fixme: this should work, but it doesn't...
        //return AIString.create(MemoryUtil.memGetAddress(address() + AIMaterialProperty.MDATA)).dataString()

        val bytes = ByteArray(mDataLength())
        mData().get(bytes)
        // first four bytes contain string header... (length)
        return String(bytes, 4, bytes.size-4)
    }

    private fun AIMaterialProperty.getIntValue(): IntArray {
        val ints = IntArray(mDataLength() / 4)
        val buf = mData()
        for (i in ints.indices) {
            ints[i] = buf.int
        }
        return ints
    }

    /**
     * fixme: method currently takes all animations in the scene instead of only the ones relevant for the current
     * mesh. Not good if there is more than one mesh in the scene
     */
    private fun makeAnimations(aiScene: AIScene): List<AnimationData> {
        val animations = mutableListOf<AnimationData>()

        for (anim in aiScene.mAnimations().animations()) {
            val name = anim.mName().dataString()
            val ticks = anim.mTicksPerSecond()
            val channels = mutableListOf<NodeAnimationData>()
            animations += AnimationData(name, (anim.mDuration() / ticks).toFloat(), channels)

            for (channel in anim.mChannels().nodeAnimations()) {
                val chName = channel.mNodeName().dataString()
                val rotKeys = mutableListOf<Vec4KeyData>()
                val posKeys = mutableListOf<Vec3KeyData>()
                val scalingKeys = mutableListOf<Vec3KeyData>()
                channels += NodeAnimationData(chName, posKeys, rotKeys, scalingKeys)

                var prevRot: Vec4KeyData? = null
                for (key in channel.mRotationKeys()!!) {
                    val q = key.mValue()
                    if (prevRot == null || q.x() != prevRot.x || q.y() != prevRot.y || q.z() != prevRot.z ||
                            q.w() != prevRot.w || !channel.mRotationKeys()!!.hasRemaining()) {
                        val time = (key.mTime() / ticks).toFloat()
                        val rot = Vec4KeyData(time, q.x(), q.y(), q.z(), q.w())
                        rotKeys += rot
                        prevRot = rot
                    }
                }

                var prevPos: Vec3KeyData? = null
                for (key in channel.mPositionKeys()!!) {
                    val v = key.mValue()
                    if (prevPos == null || v.x() != prevPos.x || v.y() != prevPos.y || v.z() != prevPos.z ||
                            !channel.mPositionKeys()!!.hasRemaining()) {
                        val time = (key.mTime() / ticks).toFloat()
                        val pos = Vec3KeyData(time, v.x(), v.y(), v.z())
                        posKeys += pos
                        prevPos = pos
                    }
                }

                var prevScl: Vec3KeyData? = null
                for (key in channel.mScalingKeys()!!) {
                    val v = key.mValue()
                    if (prevScl == null || v.x() != prevScl.x || v.y() != prevScl.y || v.z() != prevScl.z ||
                            !channel.mScalingKeys()!!.hasRemaining()) {
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
            val name = aiBone.mName().dataString()
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

    private fun makeTriangleIndices(aiMesh: AIMesh, invertFaceOrientation: Boolean): List<Int> {
        val indices = mutableListOf<Int>()

        for (face in aiMesh.mFaces()) {
            val idcs = face.mIndices()
            when {
                face.mNumIndices() == 3 && !invertFaceOrientation -> {
                    indices += idcs[0]
                    indices += idcs[1]
                    indices += idcs[2]
                }
                face.mNumIndices() == 3 && invertFaceOrientation -> {
                    indices += idcs[2]
                    indices += idcs[1]
                    indices += idcs[0]
                }
                face.mNumIndices() == 4 && !invertFaceOrientation -> {
                    indices += idcs[0]
                    indices += idcs[1]
                    indices += idcs[2]

                    indices += idcs[0]
                    indices += idcs[2]
                    indices += idcs[3]
                }
                face.mNumIndices() == 4 && invertFaceOrientation -> {
                    indices += idcs[2]
                    indices += idcs[1]
                    indices += idcs[0]

                    indices += idcs[3]
                    indices += idcs[2]
                    indices += idcs[0]
                }
                else -> throw IllegalArgumentException("Invalid number of face vertices: ${face.mNumIndices()}")
            }
        }
        return indices
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

    private fun traverseSceneGraph(aiNode: AINode, parent: SceneNode?, result: MutableMap<String, SceneNode>): SceneNode {
        val node = SceneNode(aiNode.mName().dataString(), parent, aiNode)
        result[node.name] = node

        if (parent != null) {
            parent.children += node
        }

        if (aiNode.mNumChildren() > 0) {
            aiNode.mChildren().nodes().forEach {
                traverseSceneGraph(it, node, result)
            }
        }
        return node
    }

    private fun printSceneGraph(node: SceneNode, indent: String) {
        println("$indent${node.name}")
        node.children.forEach {
            printSceneGraph(it, "$indent  ")
        }
        for (idx in 0 until node.aiNode.mNumMeshes()) {
            println("$indent  m: ${node.aiNode.mMeshes()!![idx]}")
        }
    }

    private fun convertSceneGraph(node: SceneNode): ModelNodeData {
        val children = mutableListOf<ModelNodeData>()
        val meshes = mutableListOf<Int>()
        node.children.forEach {
            val childNode = convertSceneGraph(it)
            if (childNode.countMeshesBelow() > 0) {
                children += childNode
            }
        }
        for (idx in 0 until node.aiNode.mNumMeshes()) {
            meshes += node.aiNode.mMeshes()!![idx]
        }
        return ModelNodeData(node.name, node.aiNode.mTransformation().asFloats(), children, meshes)
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

//    private fun AIString.asString(): String {
//        return String(ByteArray(data().capacity()) { i -> data()[i] })
//    }

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

    private class MaterialPointerIt(buf: PointerBuffer?) : PointerIt<AIMaterial>(buf, { ptr -> AIMaterial.create(ptr) })
    private fun PointerBuffer?.materials(): MaterialPointerIt = MaterialPointerIt(this)

    private class MaterialPropertyPointerIt(buf: PointerBuffer?) : PointerIt<AIMaterialProperty>(buf, { ptr -> AIMaterialProperty.create(ptr) })
    private fun PointerBuffer?.materialProperties(): MaterialPropertyPointerIt = MaterialPropertyPointerIt(this)
}