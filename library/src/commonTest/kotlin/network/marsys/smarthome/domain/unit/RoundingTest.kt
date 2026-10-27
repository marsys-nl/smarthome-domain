package network.marsys.smarthome.domain.unit

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isEqualTo

val roundingTest by testSuite(
    name = "Rounding tests",
) {
    testSuite(name = "Adaptive decimals") {
        test(name = "When rounding it rounds the value to the specified number of decimals") {
            val rounding = AdaptiveDecimals(decimals = 2, maxLeadingZeroes = 6)
            expectThat(rounding.round(1.23456))
                .isEqualTo(1.23)
        }

        test(name = "When rounding it rounds the value to the specified number of decimals with leading zeroes") {
            val rounding = AdaptiveDecimals(decimals = 2, maxLeadingZeroes = 4)
            expectThat(rounding.round(0.00123456))
                .isEqualTo(0.0012)
        }

        test(name = "When rounding leaves a non-finite value untouched") {
            expectThat(AdaptiveDecimals().round(Double.NaN))
                .isEqualTo(Double.NaN)
        }

        test(name = "When rounding leaves a value of zero untouched") {
            expectThat(AdaptiveDecimals().round(0.0))
                .isEqualTo(0.0)
        }

        test(name = "When negative decimals is provided it throws an exception") {
            expectThrows<IllegalArgumentException> { AdaptiveDecimals(decimals = -1) }
                .hasMessage("decimals must not be negative, but was -1")
        }

        test(name = "When max leading zeroes provided is lower than the provided decimals it throws an exception") {
            expectThrows<IllegalArgumentException> { AdaptiveDecimals(decimals = 2, maxLeadingZeroes = 1) }
                .hasMessage("maxLeadingZeroes must be at least decimals (2), but was 1")
        }
    }

    testSuite(name = "Fixed amount of decimals") {
        test(name = "When rounding it rounds the value to the specified number of decimals") {
            val rounding = Decimals(decimals = 2)
            expectThat(rounding.round(1.23456))
                .isEqualTo(1.23)
        }

        test(name = "When formatting it rounds the value to the specified number of decimals, even if there are no decimals in the value") {
            val rounding = Decimals(decimals = 2)
            expectThat(rounding.format(1.1))
                .isEqualTo("1.10")
        }

        test(name = "When formatting it rounds the negative value to the specified number of decimals") {
            val rounding = Decimals(decimals = 2)
            expectThat(rounding.format(-1.23456))
                .isEqualTo("-1.23")
        }

        test(name = "When formatting a negative value that rounds to zero the sign is dropped") {
            val rounding = Decimals(decimals = 2)
            expectThat(rounding.format(-0.001))
                .isEqualTo("0.00")
        }

        test(name = "When rounding leaves a non-finite value untouched") {
            expectThat(Decimals(decimals = 2).round(Double.NaN))
                .isEqualTo(Double.NaN)
        }

        test(name = "When rounding leaves a value of zero untouched") {
            expectThat(Decimals(decimals = 2).round(0.0))
                .isEqualTo(0.0)
        }

        test(name = "When negative decimals is provided it throws an exception") {
            expectThrows<IllegalArgumentException> { Decimals(decimals = -1) }
                .hasMessage("decimals must not be negative, but was -1")
        }
    }

    testSuite(name = "Nearest multiple") {
        test(name = "When rounding it rounds the value to the nearest multiple of the step - 0.5") {
            val rounding = NearestMultiple(step = 0.5)
            expectThat(rounding.round(1.23))
                .isEqualTo(1.0)
        }

        test(name = "When rounding it rounds the value to the nearest multiple of the step - 0.1") {
            val rounding = NearestMultiple(step = 0.1)
            expectThat(rounding.round(1.23))
                .isEqualTo(1.2)
        }

        test(name = "When rounding it rounds the value to the nearest multiple of the step - 0.05") {
            val rounding = NearestMultiple(step = 0.05)
            expectThat(rounding.round(1.23))
                .isEqualTo(1.25)
        }

        test(name = "When rounding leaves a non-finite value untouched") {
            expectThat(NearestMultiple(step = 0.5).round(Double.NaN))
                .isEqualTo(Double.NaN)
        }

        test(name = "A step that cannot be expressed in fractional digits falls back to the maximum") {
            val rounding = NearestMultiple(step = 1e-16)

            expectThat(rounding.round(1.23))
                .isEqualTo(1.23)
        }

        test(name = "A whole-number step needs no fractional digits") {
            expectThat(NearestMultiple(step = 2.0).round(1.23))
                .isEqualTo(2.0)
        }

        test(name = "A multi-digit step keeps all its decimals") {
            expectThat(NearestMultiple(step = 0.25).round(1.23))
                .isEqualTo(1.25)
        }

        test(name = "When negative step is provided it throws an exception") {
            expectThrows<IllegalArgumentException> { NearestMultiple(step = -0.5) }
                .hasMessage("step must be positive, but was -0.5")
        }
    }
}
