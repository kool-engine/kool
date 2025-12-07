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
            api(libs.box2d.jni)
            listOf(
                "natives-linux", "natives-linux-arm64",
                "natives-windows", "natives-windows-arm64",
                "natives-macos", "natives-macos-arm64"
            ).forEach { platform -> runtimeOnly("${libs.box2d.jni.get()}:$platform") }
        }

        jsMain.dependencies {
            implementation(npm(libs.box2d.wasm.get().name, libs.versions.box2d.wasm.get()))
            //implementation(npm(File("$projectDir/npm/kool-box2d-wasm")))
        }

//        androidMain.dependencies {
//            api(libs.box2d.android)
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
