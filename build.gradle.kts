import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType

plugins {
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.kotlin.kover) apply true
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.test.balloon) apply false
}

val ktlint: Configuration by configurations.creating

dependencies {
    ktlint(libs.ktlint) {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

detekt {
    allRules = true
    buildUponDefaultConfig = true
    config.from("$rootDir/config/detekt/detekt.yml")

    source.from(
        "$rootDir/library/src/commonMain/kotlin",
    )
}

kover {
    merge {
        allProjects {
            it.buildFile.exists()
        }
    }

    reports {
        verify {
            rule {
                groupBy.set(GroupingEntityType.CLASS)

                minBound(
                    minValue = 100,
                    coverageUnits = CoverageUnit.BRANCH,
                    aggregationForGroup = AggregationType.COVERED_PERCENTAGE,
                )
            }
        }
    }
}

tasks.register("ktlintCheck", JavaExec::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "**.kt",
        "**.kts",
        "!**/build/**",
    )
}
