import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinAtomicFu)
    `maven-publish`
    signing
}

kotlin {
    jvm("desktop") { }
    jvmToolchain(11)
    js(IR) {
        binaries.library()
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.coroutines)
            api(libs.kotlin.serialization.core)
            api(libs.kotlin.serialization.json)
            api(libs.kotlin.reflect)
            api(libs.kotlin.atomicfu)
            api(project(":kool-core"))
            api(project(":kool-physics"))
            api(project(":kool-editor-model"))
            implementation(libs.kotlin.datetime)
        }

        val desktopTest by getting
        desktopTest.dependencies {
            implementation(fileTree("${projectDir}/../kool-demo/runtimeLibs") { include("*.jar") })
            implementation(libs.jsvg)

            // add all native libs potentially needed for running demo as runtimeOnly dependencies, so that they can
            // be found by the cacheRuntimeLibs task
            listOf("natives-linux", "natives-windows", "natives-macos", "natives-macos-arm64").forEach { platform ->
                runtimeOnly("${libs.lwjgl.core.get()}:$platform")
                runtimeOnly("${libs.lwjgl.glfw.get()}:$platform")
                runtimeOnly("${libs.lwjgl.jemalloc.get()}:$platform")
                runtimeOnly("${libs.lwjgl.nfd.get()}:$platform")
                runtimeOnly("${libs.lwjgl.opengl.get()}:$platform")
                runtimeOnly("${libs.lwjgl.shaderc.get()}:$platform")
                runtimeOnly("${libs.lwjgl.stb.get()}:$platform")
                runtimeOnly("${libs.lwjgl.vma.get()}:$platform")
                runtimeOnly("${libs.physxjni.get()}:$platform")
            }
        }
    }
}

tasks["clean"].doLast {
    delete("${rootDir}/dist/kool-editor")
}

publishing {
    publications {
        publications.filterIsInstance<MavenPublication>().forEach { pub ->
            pub.pom {
                name.set("kool-editor")
                description.set("kool project editor")
                url.set("https://github.com/fabmax/kool")
                developers {
                    developer {
                        name.set("Max Thiele")
                        email.set("fabmax.thiele@gmail.com")
                        organization.set("github")
                        organizationUrl.set("https://github.com/fabmax")
                    }
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/fabmax/kool.git")
                    developerConnection.set("scm:git:ssh://github.com:fabmax/kool.git")
                    url.set("https://github.com/fabmax/kool/tree/main")
                }
            }

            // generating javadoc isn't supported for multiplatform projects -> add a dummy javadoc jar
            // containing the README.md to make maven central happy
            var docJarAppendix = pub.name
            val docTaskName = "dummyJavadoc${pub.name}"
            if (pub.name == "kotlinMultiplatform") {
                docJarAppendix = ""
            }
            tasks.register<Jar>(docTaskName) {
                if (docJarAppendix.isNotEmpty()) {
                    archiveAppendix.set(docJarAppendix)
                }
                archiveClassifier.set("javadoc")
                from("$rootDir/README.md")
            }
            pub.artifact(tasks[docTaskName])
        }
    }

    if (File("publishingCredentials.properties").exists()) {
        val props = Properties()
        props.load(FileInputStream("publishingCredentials.properties"))

        repositories {
            maven {
                url = if (version.toString().endsWith("-SNAPSHOT")) {
                    uri("https://oss.sonatype.org/content/repositories/snapshots")
                } else {
                    uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                }
                credentials {
                    username = props.getProperty("publishUser")
                    password = props.getProperty("publishPassword")
                }
            }
        }

        signing {
            publications.forEach {
                sign(it)
            }
        }
    }
}
