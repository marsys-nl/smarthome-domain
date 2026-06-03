package network.marsys.smarthome.domain.unit

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isEqualTo

val durationTest by testSuite(
    name = "Composite duration formatting tests",
) {
    testSuite(name = "Composite rendering") {
        test(name = "Breaks a duration down across hours, minutes and seconds") {
            expectThat(5_025.seconds.toString())
                .isEqualTo("1 h 23 min 45 s")
        }

        test(name = "Includes days as the largest unit by default") {
            expectThat(90_000.seconds.toString())
                .isEqualTo("1 d 1 h")
        }

        test(name = "Omits interior zero components") {
            expectThat(3_645.seconds.toString())
                .isEqualTo("1 h 45 s")
        }

        test(name = "Omits trailing zero components") {
            expectThat(3_600.seconds.toString())
                .isEqualTo("1 h")
        }

        test(name = "Renders a zero duration with the smallest unit") {
            expectThat(0.seconds.toString())
                .isEqualTo("0 s")
        }

        test(name = "Renders a negative duration behind a leading minus") {
            expectThat((-90).seconds.toString())
                .isEqualTo("-1 min 30 s")
        }

        test(name = "A negative value rounding to zero has no sign") {
            expectThat((-0.4).seconds.toString())
                .isEqualTo("0 s")
        }
    }

    testSuite(name = "Custom bounds and rounding") {
        test(name = "Bounding the units rounds at the smallest one") {
            expectThat(5_025.seconds)
                .get { format(largestUnit = Hour, smallestUnit = Minute) }
                .isEqualTo("1 h 24 min")
        }
    }

    testSuite(name = "Structured decomposition") {
        test(name = "Exposes the ordered components as quantities") {
            expectThat(5_025.seconds)
                .get(Quantity<Dimension.Time>::toComponents)
                .get { joinToString(separator = " ") { it.format(PlainScale()) } }
                .isEqualTo("1 h 23 min 45 s")
        }

        test(name = "Produces one component per non-zero unit") {
            expectThat(5_025.seconds)
                .get(Quantity<Dimension.Time>::toComponents)
                .get(List<*>::size)
                .isEqualTo(3)
        }
    }
}
