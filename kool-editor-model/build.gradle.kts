plugins {
    id("kool.lib-conventions")
    id("kool.publish-conventions")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kool-core"))
            api(project(":kool-physics"))
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.reflect)
            implementation(libs.kotlin.atomicfu)
        }
    }
}
