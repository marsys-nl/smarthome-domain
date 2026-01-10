plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "network.marsys.smarthome"
version = libs.versions.smarthome.domain.version.get()

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())

    applyDefaultHierarchyTemplate()

    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()
}
