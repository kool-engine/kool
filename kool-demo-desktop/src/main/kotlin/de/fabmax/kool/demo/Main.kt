package de.fabmax.kool.demo

import de.fabmax.kool.platform.PlatformImpl

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    Demo(PlatformImpl.initContext(), "globeDemo")

    /*val z = 4
    val n = (1 shl z) - 1
    for (x in 0..n) {
        for (y in 0..n) {
            println("$z/$x/$y.png")
            val url = URL("http://a.tile.openstreetmap.org/$z/$x/$y.png")
            val instream = url.openStream()
            val outstream = FileOutputStream("tiles/${z}_${x}_$y.png")
            val buf = ByteArray(4096)
            var len = 1
            while (len > 0) {
                len = instream.read(buf)
                if (len > 0) {
                    outstream.write(buf, 0, len)
                }
            }
            instream.close()
            outstream.close()
        }
    }*/
}
