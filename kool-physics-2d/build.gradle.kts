plugins {
    id("kool.androidlib-conventions")
    id("kool.lib-conventions")
    id("kool.publish-conventions")
    id("de.fabmax.webidl-util") version "0.10.5-SNAPSHOT"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kool-core"))
            implementation(libs.kotlin.coroutines)
            implementation(libs.kotlin.atomicfu)
        }

        desktopMain.dependencies {
            api(libs.box2djni)
            listOf("natives-linux", "natives-windows", "natives-macos", "natives-macos-arm64").forEach { platform ->
                runtimeOnly("${libs.box2djni.get()}:$platform")
            }
        }

        jsMain.dependencies {
            implementation(npm(File("$projectDir/npm/kool-box2d-wasm")))
        }

//        androidMain.dependencies {
//            api(libs.box2djniandroid)
//        }
    }
}

webidl {
    localProperties["box2dwasm.webidldir"]?.let { modelPath = file(it) }
    modelName = "Box2dWasm"

    generateKotlinJsInterfaces {
        outputDirectory = file("${projectDir}/src/jsMain/kotlin/box2d")
        packagePrefix = "box2d"
        moduleName = "kool-box2d-wasm"
        modulePromiseName = "Box2D"
    }
}
