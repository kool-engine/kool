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
            implementation(libs.compose.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        desktopMain.dependencies {
            api(libs.bundles.lwjgl)
            implementation(libs.jsvg)
            implementation("org.lwjglx:lwjgl3-awt:0.2.5") { exclude(group = "org.lwjgl") }

            listOf("natives-linux", "natives-windows", "natives-macos", "natives-macos-arm64").forEach { platform ->
                val nonNativeLibs = mutableListOf("lwjgl-jawt")
                if ("macos" !in platform) nonNativeLibs += "lwjgl-vulkan"
                libs.bundles.lwjgl.get()
                    .filter { it.name !in nonNativeLibs }
                    .forEach { lib -> runtimeOnly("$lib:$platform") }
            }
            compileOnly(libs.jspecify.annotations)   // used / needed by lwjgl
        }
        desktopTest.dependencies {
            implementation(libs.kotlin.test.junit)
        }

        webMain.dependencies {
            implementation(libs.kotlinx.browser)
            implementation(npm("pako", "2.0.4"))
            implementation(npm("jszip", "3.10.1"))
        }

//        androidMain.dependencies {
//            implementation(libs.androidsvg)
//        }
    }
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

// Render tests render images with the headless Vulkan backend and compare them against reference images. They
// require a Vulkan capable device (or a software implementation like lavapipe) and are therefore not part of the
// regular desktopTest task. Run them via `./gradlew :kool-core:renderTest`. Gradle properties prefixed with
// `kool.renderTest.` are forwarded as system properties, e.g. `-Pkool.renderTest.updateReferences=true` re-generates
// the reference images (see RenderTestContext for all options).
val renderTestPackage = "de/fabmax/kool/rendertest/**"
val desktopTestTask = tasks.named<Test>("desktopTest") {
    exclude(renderTestPackage)
}

tasks.register<Test>("renderTest") {
    group = "verification"
    description = "Runs the headless Vulkan render tests, comparing rendered images against reference images."

    testClassesDirs = files(desktopTestTask.map { it.testClassesDirs })
    classpath = files(desktopTestTask.map { it.classpath })
    javaLauncher = desktopTestTask.flatMap { it.javaLauncher }
    useJUnit()
    include(renderTestPackage)

    // there can only be one kool context per JVM: use a separate JVM for each test class
    forkEvery = 1
    jvmArgs("--enable-native-access=ALL-UNNAMED")

    val referenceDir = layout.projectDirectory.dir("src/desktopTest/resources/render-references")
    val reportDir = layout.buildDirectory.dir("reports/render-tests/images")
    systemProperty("java.awt.headless", "true")
    systemProperty("kool.renderTest.referenceDir", referenceDir.asFile.absolutePath)
    systemProperty("kool.renderTest.reportDir", reportDir.get().asFile.absolutePath)
    providers.gradlePropertiesPrefixedBy("kool.renderTest.").get().forEach { (key, value) ->
        systemProperty(key, value)
    }
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    // rendering depends on the GPU / driver, never consider this up-to-date
    outputs.upToDateWhen { false }
    doFirst { reportDir.get().asFile.deleteRecursively() }
}
