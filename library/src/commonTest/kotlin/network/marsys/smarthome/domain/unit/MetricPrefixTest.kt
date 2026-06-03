package network.marsys.smarthome.domain.unit

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isEqualTo

val metricPrefixTest by testSuite(
    name = "Metric prefix tests",
) {
    testSuite(name = "Selecting a preferred prefix") {
        val prefixes = listOf(
            MetricPrefix.NONE,
            MetricPrefix.KILO,
            MetricPrefix.MEGA,
        )

        test(name = "Picks the largest prefix that keeps the value at or above one") {
            expectThat(prefixes.preferredFor(2_500_000.0))
                .isEqualTo(MetricPrefix.MEGA)
        }

        test(name = "Picks no prefix for a value within the base range") {
            expectThat(prefixes.preferredFor(42.0))
                .isEqualTo(MetricPrefix.NONE)
        }

        test(name = "Selects on magnitude, ignoring the sign") {
            expectThat(prefixes.preferredFor(-2_500_000.0))
                .isEqualTo(MetricPrefix.MEGA)
        }

        test(name = "Falls back to the smallest prefix for a value below every prefix") {
            expectThat(prefixes.preferredFor(0.0005))
                .isEqualTo(MetricPrefix.NONE)
        }

        test(name = "Falls back to the smallest available prefix for zero") {
            val prefixes = listOf(MetricPrefix.KILO, MetricPrefix.MEGA)
            expectThat(prefixes.preferredFor(0.0))
                .isEqualTo(MetricPrefix.KILO)
        }

        test(name = "Uses no prefix when the list of prefixes is empty") {
            expectThat(emptyList<MetricPrefix>().preferredFor(1_000.0))
                .isEqualTo(MetricPrefix.NONE)
        }
    }
}
