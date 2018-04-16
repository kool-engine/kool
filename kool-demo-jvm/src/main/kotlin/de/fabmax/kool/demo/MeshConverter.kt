package de.fabmax.kool.demo

import de.fabmax.kool.util.serialization.MeshConverter
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.FileOutputStream


fun main(args: Array<String>) {
    val meshes = MeshConverter.convertMeshes("docs/assets/player.fbx")
    FileOutputStream("docs/assets/player.kmf").use { out ->
        out.write(ProtoBuf.dump(meshes[0]))
    }
}
