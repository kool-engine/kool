package de.fabmax.kool.demo

import de.fabmax.kool.util.serialization.ModelConverter
import kotlinx.serialization.dump
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream


fun main() {
    val model = ModelConverter.convertModel("docs/assets/player.fbx", false, 10)
    GZIPOutputStream(FileOutputStream("docs/assets/player.kmfz")).use { out ->
        out.write(ProtoBuf.dump(model))
    }
}
