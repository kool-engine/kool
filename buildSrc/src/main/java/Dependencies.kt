import org.gradle.internal.os.OperatingSystem

object Versions {
    val kotlinVersion = "1.3.72"
    val kotlinCorroutinesVersion = "1.3.7"
    val kotlinSerializationVersion = "0.20.0"

    val lwjglVersion = "3.2.3"
    val lwjglNatives = when (OperatingSystem.current()) {
        OperatingSystem.LINUX -> "natives-linux"
        OperatingSystem.MAC_OS -> "natives-macos"
        else -> "natives-windows"
    }
}

object DepsCommon {
    val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.kotlinCorroutinesVersion}"
    val kotlinSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.kotlinSerializationVersion}"
}

object DepsJvm {
    val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCorroutinesVersion}"
    val kotlinSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinSerializationVersion}"

    val jTransforms = "com.github.wendykierp:JTransforms:3.1"

    fun lwjgl(subLib: String? = null): String {
        return if (subLib != null) {
            "org.lwjgl:lwjgl-$subLib:${Versions.lwjglVersion}"
        } else {
            "org.lwjgl:lwjgl:${Versions.lwjglVersion}"
        }
    }

    fun lwjglNatives(subLib: String? = null): String {
        return if (subLib != null) {
            "org.lwjgl:lwjgl-$subLib:${Versions.lwjglVersion}:${Versions.lwjglNatives}"
        } else {
            "org.lwjgl:lwjgl:${Versions.lwjglVersion}:${Versions.lwjglNatives}"
        }
    }
}

object DepsJs {
    val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.kotlinCorroutinesVersion}"
    val kotlinSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${Versions.kotlinSerializationVersion}"
}