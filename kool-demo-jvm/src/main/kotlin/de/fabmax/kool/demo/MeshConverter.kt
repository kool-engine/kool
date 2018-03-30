package de.fabmax.kool.demo

import de.fabmax.kool.util.serialization.MeshConverter
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.FileOutputStream

fun convertMesh() {
    val meshes = MeshConverter.convertMeshes("player.fbx")
    FileOutputStream("player.kmf").use { out ->
        out.write(ProtoBuf.dump(meshes[0]))
    }
}
