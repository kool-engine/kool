import de.fabmax.kool.GenerateVariantsFromFloatPrototype
import de.fabmax.kool.VersionNameUpdate

plugins {
    id("kool.androidlib-conventions")
    id("kool.lib-conventions")
    id("kool.publish-conventions")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.coroutines)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.atomicfu)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        desktopMain.dependencies {
            api(libs.bundles.lwjgl)
            implementation(libs.jsvg)

            listOf("natives-linux", "natives-windows", "natives-macos", "natives-macos-arm64").forEach { platform ->
                val hasVulkanRuntime = "macos" in platform
                libs.bundles.lwjgl.get().filter { it.name != "lwjgl-vulkan" || hasVulkanRuntime }.forEach { lib ->
                    runtimeOnly("$lib:$platform")
                }
            }
        }
        desktopTest.dependencies {
            implementation(libs.kotlin.test.junit)
        }

        jsMain.dependencies {
            implementation(npm("pako", "2.0.4"))
            implementation(npm("jszip", "3.10.1"))
        }

//        androidMain.dependencies {
//            implementation(libs.androidsvg)
//        }
    }
}

//android {
//    externalNativeBuild {
//        cmake {
//            path = file("src/androidMain/cpp/CMakeLists.txt")
//            version = "3.22.1"
//        }
//    }
//}

tasks["clean"].doLast {
    delete("${projectDir}/.cxx")
}

tasks.register<GenerateVariantsFromFloatPrototype>("generateDoubleAndIntVariants") {
    filesToUpdate = kotlin.sourceSets.findByName("commonMain")?.kotlin
        ?.sourceDirectories
        ?.flatMap {
            listOf(
                File(it, "de/fabmax/kool/math/Vec2.kt"),
                File(it, "de/fabmax/kool/math/Vec3.kt"),
                File(it, "de/fabmax/kool/math/Vec4.kt")
            )
        }
        ?.filter { it.exists() }
        ?.map { it.absolutePath }
        ?: emptyList()
}

tasks.register<GenerateVariantsFromFloatPrototype>("generateDoubleOnlyVariants") {
    generateIntTypes = false
    filesToUpdate = kotlin.sourceSets.findByName("commonMain")?.kotlin
        ?.sourceDirectories
        ?.flatMap {
            listOf(
                File(it, "de/fabmax/kool/math/Angle.kt"),
                File(it, "de/fabmax/kool/math/Mat2.kt"),
                File(it, "de/fabmax/kool/math/Mat3.kt"),
                File(it, "de/fabmax/kool/math/Mat4.kt"),
                File(it, "de/fabmax/kool/math/Mat4Stack.kt"),
                File(it, "de/fabmax/kool/math/Quat.kt"),
                File(it, "de/fabmax/kool/math/Pose.kt"),
                File(it, "de/fabmax/kool/math/PointDistance.kt"),
                File(it, "de/fabmax/kool/math/Plane.kt"),
                File(it, "de/fabmax/kool/math/Ray.kt"),
                File(it, "de/fabmax/kool/math/spatial/BoundingBox.kt"),
                File(it, "de/fabmax/kool/scene/MatrixTransform.kt"),
                File(it, "de/fabmax/kool/scene/TrsTransform.kt"),
            )
        }
        ?.filter { it.exists() }
        ?.map { it.absolutePath }
        ?: emptyList()
}

tasks.register("generateTypeVariants") {
    dependsOn("generateDoubleAndIntVariants", "generateDoubleOnlyVariants")
}

tasks.register<VersionNameUpdate>("updateVersion") {
    versionName = "$version"
    filesToUpdate = listOf(
        kotlin.sourceSets.findByName("commonMain")?.kotlin
            ?.sourceDirectories
            ?.map { File(it, "de/fabmax/kool/KoolContext.kt") }
            ?.find { it.exists() }?.absolutePath ?: ""
    )
}

tasks["compileKotlinJs"].dependsOn("generateTypeVariants")
tasks["compileKotlinDesktop"].dependsOn("generateTypeVariants")
