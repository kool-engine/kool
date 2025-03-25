plugins {
    `maven-publish`
    signing
}

afterEvaluate {
    publishing {
        publications {
            publications.filterIsInstance<MavenPublication>().forEach { pub ->
                pub.pom {
                    name.set(project.name)
                    description.set("A multiplatform OpenGL / Vulkan graphics engine written in kotlin")
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

        val props = LocalProperties.get(project)
        repositories {
            maven {
                url = if (version.toString().endsWith("-SNAPSHOT")) {
                    uri("https://oss.sonatype.org/content/repositories/snapshots")
                } else {
                    uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                }
                credentials {
                    username = props["MAVEN_USERNAME"]
                    password = props["MAVEN_PASSWORD"]
                }
            }
        }
        if (props.isRelease && !version.toString().endsWith("-SNAPSHOT")) {
            signing {
                publications.forEach {
                    val privateKey = props["GPG_PRIVATE_KEY"]
                    val password = props["GPG_PASSWORD"]
                    useInMemoryPgpKeys(privateKey, password)
                    sign(it)
                }
            }
        }
    }
}