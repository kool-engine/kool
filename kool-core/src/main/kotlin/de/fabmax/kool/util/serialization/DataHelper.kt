package de.fabmax.kool.util.serialization

import de.fabmax.kool.gl.GL_DYNAMIC_DRAW
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.animation.Armature
import de.fabmax.kool.scene.animation.Bone
import de.fabmax.kool.shading.Attribute
import kotlinx.serialization.protobuf.ProtoBuf

fun loadMesh(data: ByteArray): Mesh {
    return loadMesh(ProtoBuf.load<MeshData>(data))
}

fun loadMesh(data: MeshData): Mesh {
    val attributes = mutableSetOf(Attribute.POSITIONS)
    if (data.hasNormals()) {
        attributes += Attribute.NORMALS
    }
    if (data.hasColors()) {
        attributes += Attribute.COLORS
    }
    if (data.hasTexCoords()) {
        attributes += Attribute.TEXTURE_COORDS
    }

    val meshData = de.fabmax.kool.scene.MeshData(attributes)

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

    val mesh: Mesh
    if (!data.armature.isEmpty()) {
        // create armature with bones
        mesh = Armature(meshData, data.name)

        // 1st pass: create bones
        data.armature.forEach {
            val bone = Bone(it.name, it.vertexIds.size)
            mesh.bones[bone.name] = bone

            bone.offsetMatrix.set(it.offsetMatrix)
            for (i in it.vertexIds.indices) {
                bone.vertexIds[i] = it.vertexIds[i]
                bone.vertexWeights[i] = it.vertexWeights[i]
            }
        }

        // 2nd pass: build bone hierarchy
        data.armature.forEach {
            val bone = mesh.bones[it.name]!!
            bone.parent = mesh.bones[it.parent]
            if (bone.parent == null) {
                mesh.rootBones += bone
            }

            it.children.forEach { childName ->
                val child = mesh.bones[childName]
                if (child != null) {
                    bone.children += child
                }
            }
        }

        // apply bones to mesh data
        mesh.updateBones()

        // load animations
        data.animations.forEach { mesh.addAnimation(it.name, it.getAnimation(mesh.bones)) }

    } else {
        mesh = Mesh(meshData, data.name)
    }

    return mesh
}
