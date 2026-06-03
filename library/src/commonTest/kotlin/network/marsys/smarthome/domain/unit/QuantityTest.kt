package network.marsys.smarthome.domain.unit

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.Assertion
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isEqualTo
import kotlin.math.absoluteValue

val quantityTest by testSuite(
    name = "Quantity tests",
) {
    testSuite(name = "Building quantities") {
        test(name = "A builder keeps the original value") {
            expectThat(1.5.kilowattHours)
                .get(Quantity<*>::value)
                .isEqualTo(1.5)
        }

        test(name = "A builder keeps the chosen unit") {
            expectThat(1.5.kilowattHours)
                .get(Quantity<*>::unit)
                .isEqualTo(KilowattHour)
        }

        test(name = "measuredIn builds a quantity from a number and unit") {
            expectThat(230 measuredIn Watt)
                .get(Quantity<*>::value)
                .isEqualTo(230.0)
        }
    }

    testSuite(name = "Converting between units") {
        test(name = "Kilowatt-hours convert to watt-hours") {
            expectThat(1.5.kilowattHours into WattHour)
                .get(Quantity<*>::value)
                .isEqualTo(1_500.0)
        }

        test(name = "Kilowatt-hours convert to joules") {
            expectThat(1.5.kilowattHours into Joule)
                .get(Quantity<*>::value)
                .isEqualTo(5_400_000.0)
        }

        test(name = "Watts convert to kilowatts") {
            expectThat(1_500.watts into Kilowatt)
                .get(Quantity<*>::value)
                .isEqualTo(1.5)
        }

        test(name = "Fractions convert to percentages") {
            expectThat(0.5.fraction into Percent)
                .get(Quantity<*>::value)
                .isEqualTo(50.0)
        }

        test(name = "Percentages convert to fractions") {
            expectThat(25.percent into Fraction)
                .get(Quantity<*>::value)
                .isEqualTo(0.25)
        }

        test(name = "Degrees Celsius convert to kelvin") {
            expectThat(0.celsius into Kelvin)
                .get(Quantity<*>::value)
                .isEqualTo(273.15)
        }

        test(name = "Degrees Celsius convert to degrees Fahrenheit") {
            expectThat(100.celsius into Fahrenheit)
                .get(Quantity<*>::value)
                .isEqualTo(212.0, tolerance = 0.0001)
        }

        test(name = "Degrees Fahrenheit convert to degrees Celsius") {
            expectThat(32.fahrenheit into Celsius)
                .get(Quantity<*>::value)
                .isEqualTo(0.0)
        }
    }

    testSuite(name = "Arithmetic within a dimension") {
        test(name = "Adding keeps the left-hand unit") {
            expectThat(1.kilowattHours + 500.wattHours)
                .get(Quantity<*>::unit)
                .isEqualTo(KilowattHour)
        }

        test(name = "Adding sums the physical magnitudes") {
            expectThat(1.kilowattHours + 500.wattHours)
                .get(Quantity<*>::value)
                .isEqualTo(1.5)
        }

        test(name = "Subtracting sums the physical magnitudes") {
            expectThat(2.kilowattHours - 500.wattHours)
                .get(Quantity<*>::value)
                .isEqualTo(1.5)
        }
    }

    testSuite(name = "Comparing across units") {
        test(name = "A larger quantity compares as greater") {
            expectThat(1.kilowattHours > 999.wattHours)
                .isEqualTo(true)
        }

        test(name = "Physically equal quantities compare as equal") {
            expectThat(1.kilowattHours.compareTo(1_000.wattHours))
                .isEqualTo(0)
        }
    }

    testSuite(name = "Structural equality") {
        test(name = "A quantity equals itself") {
            val quantity = 1.0.joules
            expectThat(quantity == quantity)
                .isEqualTo(true)
        }

        test(name = "Equal value and unit are equal") {
            expectThat(1.5.kilowattHours == 1.5.kilowattHours)
                .isEqualTo(true)
        }

        test(name = "A different value is not equal") {
            expectThat(1.0.joules == 2.0.joules)
                .isEqualTo(false)
        }

        test(name = "The same value in a different unit is not equal") {
            expectThat(1.0.joules == 1.0.wattHours)
                .isEqualTo(false)
        }

        test(name = "A non-quantity is not equal") {
            expectThat(1.0.joules.equals("1.0 J"))
                .isEqualTo(false)
        }
    }

    testSuite(name = "Formatting for display") {
        test(name = "A unit with a symbol is rendered with the symbol") {
            expectThat(1.5.kilowattHours)
                .get(Quantity<*>::toString)
                .isEqualTo("1.5 kWh")
        }

        test(name = "A percentage is rendered with its symbol") {
            expectThat(50.0.percent)
                .get(Quantity<*>::toString)
                .isEqualTo("50%")
        }

        test(name = "A unit without a symbol is rendered as the bare value") {
            expectThat(0.5.fraction)
                .get(Quantity<*>::toString)
                .isEqualTo("0.5")
        }

        test(name = "A spaced unit with a blank symbol is rendered as the bare value") {
            expectThat(0.5 measuredIn BlankSymbolUnit)
                .get(Quantity<*>::toString)
                .isEqualTo("0.5")
        }

        test(name = "A large power is scaled up to a readable prefix") {
            expectThat(1_500.watts.toString())
                .isEqualTo("1.5 kW")
        }

        test(name = "A whole scaled value drops its decimals") {
            expectThat(1_000.watts.toString())
                .isEqualTo("1 kW")
        }

        test(name = "A value within the base range keeps the base unit") {
            expectThat(750.watts.toString())
                .isEqualTo("750 W")
        }

        test(name = "Energy in watt-hours is scaled to kilowatt-hours") {
            expectThat(2_500.wattHours.toString())
                .isEqualTo("2.5 kWh")
        }

        test(name = "A temperature is never scaled, as it has no acceptable prefixes") {
            expectThat(1_500.kelvin.toString())
                .isEqualTo("1500K")
        }

        test(name = "An already-prefixed convenience unit is left as-is") {
            expectThat(2.kilowatts.toString())
                .isEqualTo("2 kW")
        }

        test(name = "An explicitly supplied prefix overrides automatic selection") {
            expectThat(1_500.watts.format(MetricPrefix.MEGA))
                .isEqualTo("0.0015 MW")
        }
    }
}

private data object BlankSymbolUnit : Unit<Dimension.Ratio> {
    override val symbol: String = ""

    override fun toBaseUnit(value: Double): Double = value
    override fun fromBaseUnit(value: Double): Double = value
}
