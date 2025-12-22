plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugindep.kotlin)
    implementation(libs.plugindep.kotlin.serialization)
    implementation(libs.plugindep.kotlin.atomicfu)
    implementation(libs.plugindep.kotlin.jsplainobjs)
    implementation(libs.plugindep.dokka)
    implementation(libs.plugindep.android.library)
    implementation(libs.plugindep.maven.publish)
}
