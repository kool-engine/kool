package de.fabmax.kool.demo

import de.fabmax.kool.createContext
import de.fabmax.kool.util.serialization.MeshConverter
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.FileOutputStream

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    Demo(createContext(),"simpleDemo")

//    for (i in 1..10) {
//        val img = timedMs("decoding") {
//            ImageIO.read(File("leaf.png"))
//        }
//        println("image size: ${img.width}x${img.height}")
//
//        val buf = createUint8Buffer(img.width * img.height * 3)
//        timedMs("slow") {
//            slowCopyImage(img, buf, GL_RGB, img.width, img.height)
//        }
//
//        buf.flip()
//        timedMs("fast") {
//            fastCopyImage(img, buf, GL_RGB)
//        }
//    }
}

//fun main(args: Array<String>) {
//
//    for (r in 1..10) {
//        val n = 10_000_000
//        val nj = 10
//
//        var buf = FloatBuffer.allocate(n)
//        val perf = PerfTimer()
//        for (j in 0 until nj) {
//            for (i in 0 until n) {
//                buf.put(i, i.toFloat())
//            }
//        }
//        perf.logMs("FloatBuffer took %.3f")
//
//        buf = ByteBuffer.allocateDirect(n * 4).asFloatBuffer()
//        perf.reset()
//        for (j in 0 until nj) {
//            for (i in 0 until n) {
//                buf.put(i, i.toFloat())
//            }
//        }
//        perf.logMs("Direct FloatBuffer took %.3f")
//
//        val arr = FloatArray(n)
//        perf.reset()
//        for (j in 0 until nj) {
//            for (i in 0 until n) {
//                arr[i] = i.toFloat()
//            }
//        }
//        perf.logMs("FloatArray took %.3f")
//        println()
//    }
//}

fun convertMesh() {
    val meshes = MeshConverter.convertMeshes("player.fbx")
    FileOutputStream("player.kmf").use { out ->
        out.write(ProtoBuf.dump(meshes[0]))
    }
}
