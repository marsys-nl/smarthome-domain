plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.test.balloon)
}

group = "network.marsys.smarthome"
version = libs.versions.smarthome.domain.version.get()

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    applyDefaultHierarchyTemplate()

    jvm()

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.expect.core)
            implementation(libs.test.balloon.core)
        }
    }
}
