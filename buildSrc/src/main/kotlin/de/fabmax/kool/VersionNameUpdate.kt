package de.fabmax.kool

import localProperties
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

open class VersionNameUpdate : DefaultTask() {
    @Input
    var versionName = "0.0.0"

    @Input
    var filesToUpdate = listOf<String>()

    @TaskAction
    fun updateVersions() {
        filesToUpdate.map { File(it) }.forEach { file ->
            val versionStr = versionName.replace("SNAPSHOT", SimpleDateFormat("yyMMdd.HHmm", Locale.US).format(Date()))
            val text = file.readText()
            val lineSep = if ("\r\n" in text) "\r\n" else "\n"

            if (project.localProperties.isRelease) {
                var updated = false
                val lines = text.lines().toMutableList()
                for (i in lines.indices) {
                    val startI = lines[i].indexOf("const val KOOL_VERSION = ")
                    if (startI >= 0) {
                        lines[i] = lines[i].substring(0 until startI) + "const val KOOL_VERSION = \"$versionStr\""
                        updated = true
                        break
                    }
                }

                if (updated) {
                    file.writeText(lines.joinToString(lineSep))
                }
            }
        }
    }
}