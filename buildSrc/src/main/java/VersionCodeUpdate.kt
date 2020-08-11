import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

fun updateVersionCode(koolContextSrcFile: File, version: Any) {
    val srcBuilder = StringBuilder()
    var replaced = false
    koolContextSrcFile.readLines().forEach {
        val idx = it.indexOf("const val KOOL_VERSION = ")
        val str = if (idx >= 0) {
            replaced = true
            val versionStr = version.toString().replace("SNAPSHOT", SimpleDateFormat("yyMMdd.HHmm").format(Date()))
            it.substring(0 until idx) + "const val KOOL_VERSION = \"$versionStr\""
        } else {
            it
        }
        srcBuilder.append(str).append(System.lineSeparator())
    }

    if (replaced) {
        FileWriter(koolContextSrcFile).use {
            it.write(srcBuilder.toString())
        }
    }
}