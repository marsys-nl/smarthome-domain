plugins {
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.test.balloon) apply false
}

detekt {
    allRules = true
    buildUponDefaultConfig = true
    config.from("$rootDir/config/detekt/detekt.yml")

    source.from(
        "$rootDir/library/src/commonMain/kotlin",
    )
}
