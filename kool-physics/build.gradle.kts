@file:Suppress("UNUSED_VARIABLE")

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                useIR = true
            }
        }
    }
    //js(IR) { // kinda works as well but requires clean before build (1.4.21)
    js {
        browser { }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(DepsCommon.kotlinCoroutines)
                implementation(project(":kool-core"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(DepsJvm.lwjgl())
                runtimeOnly(DepsJvm.lwjglNatives())

                implementation("de.fabmax:physx-jni:0.3.2")
                runtimeOnly("de.fabmax:physx-jni:0.3.2:native-win64")
                runtimeOnly("de.fabmax:physx-jni:0.3.2:native-linux64")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("physx-js-webidl", "0.3.2"))
            }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
                useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}
