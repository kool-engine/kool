package de.fabmax.kool.demo

import de.fabmax.kool.util.serialization.ModelConverter
import kotlinx.serialization.dump
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.FileOutputStream


fun main(args: Array<String>) {
    val model = ModelConverter.convertModel("docs/assets/player.fbx", false)
    model.lodRootNode[0].printNodeHierarchy(model)
    FileOutputStream("docs/assets/player.kmf").use { out ->
        out.write(ProtoBuf.dump(model))
    }
}
