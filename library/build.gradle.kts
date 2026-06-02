plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.test.balloon)
}

group = "network.marsys.smarthome"
version = libs.versions.smarthome.domain.get()

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

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = "network.marsys.smarthome",
        artifactId = "smarthome-domain",
        version = libs.versions.smarthome.domain.get(),
    )

    pom {
        name.set("Smarthome - Domain")
        description.set("Domain related definitions for the Smarthome ecosystem. Including value objects, identifiers and such.")
        inceptionYear.set("2025")

        organization {
            name.set("Marsys")
            url.set("https://www.marsys.network")
        }

        developers {
            developer {
                id.set("nmrsmn")
                name.set("Niels Marsman")
                email.set("niels.marsman@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/marsys-nl/smarthome-domain")
            connection.set("git@github.com:marsys-nl/smarthome-domain.git")
        }

        licenses {
            license {
                name = "The MIT License (MIT)"
                url = "https://mit-license.org/"
                distribution = "https://mit-license.org/"
            }
        }
    }
}
