import org.gradle.internal.os.OperatingSystem

object Versions {
    val kotlin = "1.9.21"
    val kotlinCoroutines = "1.7.3"
    val kotlinSerialization = "1.6.1"
    val dokka = "1.9.10"
    val atomicfu = "0.23.0"
}

object DepsCommon {
    val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
    val kotlinSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinSerialization}"
    val kotlinSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}"
    val kotlinReflection = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
}

object DepsJvm {
    val lwjglVersion = "3.3.3"
    val nativeLibsSuffix = OperatingSystem.current().let {
        when {
            it.isLinux -> "natives-linux"
            it.isMacOsX -> "natives-macos"
            else -> "natives-windows"
        }
    }

    val physxJniVersion = "2.3.1"
    val physxJni = "de.fabmax:physx-jni:${physxJniVersion}"
    val physxJniRuntime = "de.fabmax:physx-jni:${physxJniVersion}:${nativeLibsSuffix}"

    fun lwjgl(subLib: String? = null): String {
        return if (subLib != null) {
            "org.lwjgl:lwjgl-$subLib:${lwjglVersion}"
        } else {
            "org.lwjgl:lwjgl:${lwjglVersion}"
        }
    }

    fun lwjglNatives(subLib: String? = null): String {
        return if (subLib != null) {
            "org.lwjgl:lwjgl-$subLib:${lwjglVersion}:${nativeLibsSuffix}"
        } else {
            "org.lwjgl:lwjgl:${lwjglVersion}:${nativeLibsSuffix}"
        }
    }
}
