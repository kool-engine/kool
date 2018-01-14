package de.fabmax.kool.util.serialization

import de.fabmax.kool.gl.GL_DYNAMIC_DRAW
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.animation.Armature
import de.fabmax.kool.scene.animation.Bone
import kotlinx.serialization.protobuf.ProtoBuf

fun loadModel(data: ByteArray): Model {
    return loadModel(ProtoBuf.load<MeshData>(data))
}

fun loadModel(data: MeshData): Model {
    val meshData = de.fabmax.kool.scene.MeshData(data.hasNormals(), data.hasColors(), data.hasTexCoords())

    if (!data.armature.isEmpty()) {
        meshData.usage = GL_DYNAMIC_DRAW
    }

    // add mesh vertices
    for (i in 0 until data.positions.size / 3) {
        meshData.addVertex {
            position.set(data.positions[i*3], data.positions[i*3+1], data.positions[i*3+2])
            if (data.hasNormals()) {
                normal.set(data.normals[i*3], data.normals[i*3+1], data.normals[i*3+2])
            }
            if (data.hasTexCoords()) {
                texCoord.set(data.uvs[i*2], data.uvs[i*2+1])
            }
            if (data.hasColors()) {
                color.set(data.colors[i*4], data.colors[i*4+1], data.colors[i*4+2], data.colors[i*4+3])
            }
        }
    }

    // add triangle indices
    for (i in 0 until data.triangles.size step 3) {
        meshData.addTriIndices(data.triangles[i], data.triangles[i+1], data.triangles[i+2])
    }

    // create armature with bones
    val armature = Armature(meshData)

    // 1st pass: create bones
    data.armature.forEach {
        val bone = Bone(it.name, it.vertexIds.size)
        armature.bones[bone.name] = bone

        bone.offsetMatrix.set(it.offsetMatrix)
        for (i in it.vertexIds.indices) {
            bone.vertexIds[i] = it.vertexIds[i]
            bone.vertexWeights[i] = it.vertexWeights[i]
        }
    }

    // 2nd pass: build bone hierarchy
    data.armature.forEach {
        val bone = armature.bones[it.name]!!
        bone.parent = armature.bones[it.parent]
        if (bone.parent == null) {
            armature.rootBones += bone
        }

        it.children.forEach { childName ->
            val child = armature.bones[childName]
            if (child != null) {
                bone.children += child
            }
        }
    }

    // make sure bone weights are normed
    armature.normalizeBoneWeights()

    // load animations
    data.animations.forEach { armature.addAnimation(it.name, it.getAnimation(armature.bones)) }

    val model = Model(data.name)
    model.addGeometry(meshData, armature, null)
    return model
}
