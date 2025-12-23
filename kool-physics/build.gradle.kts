plugins {
    id("kool.androidlib-conventions")
    id("kool.lib-conventions")
    id("kool.publish-conventions")
    alias(libs.plugins.webidl)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kool-core"))
            implementation(libs.kotlin.coroutines)
            implementation(libs.kotlin.atomicfu)
        }

        desktopMain.dependencies {
            api(libs.physx.jni)
            listOf("natives-linux", "natives-windows", "natives-macos", "natives-macos-arm64").forEach { platform ->
                runtimeOnly("${libs.physx.jni.get()}:$platform")
            }
        }

        jsMain.dependencies {
            api(npm(libs.physx.wasm.get().name, libs.versions.physx.wasm.get()))
//            api(npm(File("$projectDir/npm/physx-js-webidl")))
        }

        webMain.dependencies {
            api(npm(libs.physx.wasm.get().name, libs.versions.physx.wasm.get()))
        }

//        androidMain.dependencies {
//            api(libs.physx.android)
//        }
    }
}

webidl {
    localProperties["physxjs.webidldir"]?.let { modelPath = file(it) }
    modelName = "PhysXJs"

    generateKotlinJsInterfaces {
        outputDirectory = file("${projectDir}/src/webMain/kotlin/physx")
        packagePrefix = "physx"
        moduleName = "physx-js-webidl"
        modulePromiseName = "PhysX"
    }
}

tasks.register("transformDesktopToOtherPlatforms") {
    dependsOn("transformDesktopToWeb", "transformDesktopToAndroid")
    group = "generate"
}

tasks.register<TransformTask>("transformDesktopToWeb") {
    group = "generate"
    target = "js"

    val srcs = collectPhysicsFilesToTransform()
    val dsts = srcs.map {
        it.path
            .replace("desktopMain", "webMain")
            .replace(".desktop.kt", ".web.kt")
    }
    srcFiles = files(srcs)
    dstFiles = files(dsts)
}


tasks.register<TransformTask>("transformDesktopToAndroid") {
    group = "generate"
    target = "android"

    val srcs = collectPhysicsFilesToTransform()
    val dsts = srcs.map {
        it.path
            .replace("desktopMain", "androidMain")
            .replace(".desktop.kt", ".android.kt")
    }
    srcFiles = files(srcs)
    dstFiles = files(dsts)
}

fun collectPhysicsFilesToTransform(): List<File> {
    val dirs = listOf(
        "${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/articulations/",
        "${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/character/",
        "${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/geometry/",
        "${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/joints/",
        "${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/vehicle/",
    )
    val excludeFiles = setOf(
        "ControllerBehaviorCallback.desktop.kt",
        "ControllerHitListener.desktop.kt",
    )

    return buildList {
        dirs.flatMap { File(it).listFiles()!!.filterNotNull() }
            .filter { it.name !in excludeFiles }
            .forEach { add(it) }
        add(File("${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/Material.desktop.kt"))
        add(File("${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/RigidActor.desktop.kt"))
        add(File("${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/RigidBody.desktop.kt"))
        add(File("${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/RigidDynamic.desktop.kt"))
        add(File("${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/RigidStatic.desktop.kt"))
        add(File("${projectDir}/src/desktopMain/kotlin/de/fabmax/kool/physics/Shape.desktop.kt"))
    }
}

abstract class TransformTask : DefaultTask() {
    @get:Input lateinit var target: String
    @get:InputFiles lateinit var srcFiles: FileCollection
    @get:InputFiles lateinit var dstFiles: FileCollection

    @TaskAction
    fun action() {
        srcFiles.zip(dstFiles) { src, dst ->
            val srcLines = src.readLines()
            val dstLines = dst.readLines()

            val copySrc = srcLines.drop(srcLines.takeFileHeader().size)
            val copyResult = dstLines.takeFileHeader() + "// GENERATED CODE BELOW:\n// Transformed from desktop source\n" + copySrc.map {
                it.replace("//@$target: ", "")
            }
            dst.writeText(copyResult.joinToString("\n"))
        }
    }

    fun List<String>.takeFileHeader(): List<String> = takeWhile {
        it.startsWith("@file:") ||
        it.startsWith("package") ||
        it.startsWith("import") ||
        it.isBlank()
    }
}
