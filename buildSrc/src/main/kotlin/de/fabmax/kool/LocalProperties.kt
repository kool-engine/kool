import org.gradle.api.Project
import java.io.File
import java.util.*

class LocalProperties private constructor(file: File) : Properties() {
    val isRelease: Boolean get() = getProperty("kool.isRelease").toBoolean()

    init {
        if (file.exists()) {
            file.reader().use { load(it) }
        }
    }

    operator fun get(key: String): String? = getProperty(key, System.getenv(key))

    companion object {
        fun get(project: Project): LocalProperties {
            return LocalProperties(project.rootProject.file("local.properties"))
        }
    }
}