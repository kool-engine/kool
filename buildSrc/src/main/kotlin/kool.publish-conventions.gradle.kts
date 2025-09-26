plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral()
    if (!version.toString().endsWith("-SNAPSHOT")) {
        signAllPublications()
    }

    coordinates(group.toString(), name, version.toString())

    pom {
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
}