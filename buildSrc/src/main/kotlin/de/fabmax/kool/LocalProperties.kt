import org.gradle.api.Project
import java.io.File
import java.util.*

class LocalProperties private constructor(file: File) : Properties() {
    val isRelease: Boolean get() = getProperty("kool.isRelease", "true").toBoolean()

    init {
        if (file.exists()) {
            file.reader().use { load(it) }
        }
    }

    operator fun get(key: String): String? = getProperty(key, System.getenv(key))

    fun getOrElse(key: String, default: String) = get(key) ?: default

    companion object {
        fun get(project: Project): LocalProperties {
            return LocalProperties(project.rootProject.file("local.properties"))
        }
    }
}

val Project.localProperties: LocalProperties get() = LocalProperties.get(this)
