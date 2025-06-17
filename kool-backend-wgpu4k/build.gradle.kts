plugins {
    id("kool.lib-conventions")
    id("kool.publish-conventions")
}

kotlin {
    jvmToolchain(22)
    sourceSets {
        commonMain.dependencies {
            api(project(":kool-core"))
            api(libs.kotlin.coroutines)
            implementation(libs.bundles.wgpu4k)
        }

        desktopMain.dependencies {
            implementation(libs.bundles.wgpu4k.surface.utils)
        }
    }
}
