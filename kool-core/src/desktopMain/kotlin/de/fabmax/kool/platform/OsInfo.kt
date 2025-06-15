package de.fabmax.kool.platform

object OsInfo {

    val os: OS
    val arch: String

    val isWindows: Boolean get() = os == OS.WINDOWS
    val isLinux: Boolean get() = os == OS.LINUX
    val isMacOsX: Boolean get() = os == OS.MACOS_X

    init {
        val osName = System.getProperty("os.name", "unknown").lowercase()
        os = when {
            "windows" in osName -> OS.WINDOWS
            "linux" in osName -> OS.LINUX
            arrayOf("mac os x", "osx", "darwin").any { it in osName } -> OS.MACOS_X
            else -> OS.UNKNOWN
        }
        arch = System.getProperty("os.arch", "unknown")
    }

    enum class OS {
        WINDOWS,
        LINUX,
        MACOS_X,
        UNKNOWN
    }
}