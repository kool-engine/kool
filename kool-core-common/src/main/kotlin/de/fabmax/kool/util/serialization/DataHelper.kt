package de.fabmax.kool.util.serialization

import de.fabmax.kool.KoolException
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.animation.Armature
import de.fabmax.kool.scene.animation.Bone
import de.fabmax.kool.shading.Attribute
import kotlinx.serialization.protobuf.ProtoBuf

fun loadMesh(data: ByteArray, generateNormals: Boolean = true, generateTangents: Boolean = false): Mesh {
    return loadMesh(ProtoBuf.load<MeshData>(data), generateNormals, generateTangents)
}

fun loadMesh(data: MeshData, generateNormals: Boolean = true, generateTangents: Boolean = false): Mesh {
    val attributes = mutableSetOf(Attribute.POSITIONS)
    if (data.hasNormals || generateNormals) { attributes += Attribute.NORMALS }
    if (data.hasTangents || generateTangents) { attributes += Attribute.TANGENTS }
    if (data.hasColors) { attributes += Attribute.COLORS }
    if (data.hasTexCoords) { attributes += Attribute.TEXTURE_COORDS }

    val meshData = de.fabmax.kool.scene.MeshData(attributes)

    // add mesh vertices
    val positions = data.attributes[MeshData.ATTRIB_POSITIONS] ?: throw KoolException("Mesh has no positions")
    val normals = data.attributes[MeshData.ATTRIB_NORMALS]
    val texCoords = data.attributes[MeshData.ATTRIB_TEXTURE_COORDS]
    val colors = data.attributes[MeshData.ATTRIB_COLORS]
    val tangents = data.attributes[MeshData.ATTRIB_TANGENTS]
    for (i in 0 until positions.size / 3) {
        meshData.addVertex {
            position.set(positions[i*3], positions[i*3+1], positions[i*3+2])
            if (normals != null) {
                normal.set(normals[i*3], normals[i*3+1], normals[i*3+2])
            }
            if (texCoords != null) {
                texCoord.set(texCoords[i*2], texCoords[i*2+1])
            }
            if (colors != null) {
                color.set(colors[i*4], colors[i*4+1], colors[i*4+2], colors[i*4+3])
            }
            if (tangents!= null) {
                tangent.set(tangents[i*3], tangents[i*3+1], tangents[i*3+2])
            }
        }
    }

    // add vertex indices
    if (data.indices.isEmpty()) {
        for (i in 0 until data.numVertices) {
            meshData.addIndex(i)
        }
    } else {
        meshData.addIndices(data.indices)
    }

    if (!data.hasNormals && generateNormals) {
        meshData.generateNormals()
    }
    if (!data.hasTangents && generateTangents) {
        meshData.generateTangents()
    }

    return if (!data.armature.isEmpty()) {
        buildAramature(meshData, data)
    } else {
        Mesh(meshData, data.name)
    }
}

private fun buildAramature(meshData: de.fabmax.kool.scene.MeshData, data: MeshData): Armature {
    // create armature with bones
    val mesh = Armature(meshData, data.name)

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

    return mesh
}
