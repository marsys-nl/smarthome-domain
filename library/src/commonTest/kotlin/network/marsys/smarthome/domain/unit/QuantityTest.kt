package network.marsys.smarthome.domain.unit

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isEqualTo

val quantityTest by testSuite(
    name = "Quantity tests",
) {
    testSuite(name = "Building quantities") {
        test(name = "A builder keeps the original value") {
            expectThat(1.5.wattHours)
                .get(Quantity<*>::value)
                .isEqualTo(1.5)
        }

        test(name = "A builder keeps the chosen unit") {
            expectThat(1.5.wattHours)
                .get(Quantity<*>::unit)
                .isEqualTo(WattHour)
        }

        test(name = "measuredIn builds a quantity from a number and unit") {
            expectThat(230.measuredIn(unit = Watt))
                .get(Quantity<*>::value)
                .isEqualTo(230.0)
        }

        test(name = "The kilowatts builder records the equivalent in watts") {
            expectThat(2.kilowatts)
                .get(Quantity<*>::value)
                .isEqualTo(2_000.0)
        }

        test(name = "The kilowatt-hours builder records the equivalent in watt-hours") {
            expectThat(1.5.kilowattHours)
                .get(Quantity<*>::value)
                .isEqualTo(1_500.0)
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

        test(name = "Watt-seconds convert to joules one to one") {
            expectThat(5.wattSeconds into Joule)
                .get(Quantity<*>::value)
                .isEqualTo(5.0)
        }

        test(name = "Watt-minutes convert to joules") {
            expectThat(2.wattMinutes into Joule)
                .get(Quantity<*>::value)
                .isEqualTo(120.0)
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

        test(name = "Minutes convert to seconds") {
            expectThat(2.minutes into Second)
                .get(Quantity<*>::value)
                .isEqualTo(120.0)
        }

        test(name = "Hours convert to seconds") {
            expectThat(1.hours into Second)
                .get(Quantity<*>::value)
                .isEqualTo(3_600.0)
        }

        test(name = "Days convert to hours") {
            expectThat(1.days into Hour)
                .get(Quantity<*>::value)
                .isEqualTo(24.0)
        }

        test(name = "Seconds convert to minutes") {
            expectThat(90.seconds into Minute)
                .get(Quantity<*>::value)
                .isEqualTo(1.5)
        }
    }

    testSuite(name = "Arithmetic within a dimension") {
        test(name = "Adding keeps the left-hand unit") {
            expectThat(1.wattHours + 3_600.joules)
                .get(Quantity<*>::unit)
                .isEqualTo(WattHour)
        }

        test(name = "Adding converts and sums the physical magnitudes") {
            expectThat(1.wattHours + 3_600.joules)
                .get(Quantity<*>::value)
                .isEqualTo(2.0)
        }

        test(name = "Subtracting converts and sums the physical magnitudes") {
            expectThat(2.wattHours - 3_600.joules)
                .get(Quantity<*>::value)
                .isEqualTo(1.0)
        }
    }

    testSuite(name = "Comparing across units") {
        test(name = "A larger quantity compares as greater") {
            expectThat(1.wattHours > 3_000.joules)
                .isEqualTo(true)
        }

        test(name = "Physically equal quantities compare as equal") {
            expectThat(1.wattHours.compareTo(3_600.joules))
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
            expectThat(1.5.wattHours == 1.5.wattHours)
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
            expectThat(0.5.measuredIn(unit = BlankSymbolUnit))
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

        test(name = "The kilowatts builder renders back through automatic prefixing") {
            expectThat(2.kilowatts.toString())
                .isEqualTo("2 kW")
        }

        test(name = "An explicitly supplied scale overrides the unit's default") {
            expectThat(1_500.watts)
                .get { format(MetricScale(MetricPrefix.MEGA)) }
                .isEqualTo("0.0015 MW")
        }

        test(name = "A large current is scaled to a readable prefix") {
            expectThat(1_500.amperes.toString())
                .isEqualTo("1.5 kA")
        }

        test(name = "A voltage within the base range keeps the base unit") {
            expectThat(230.volts.toString())
                .isEqualTo("230 V")
        }

        test(name = "A large voltage is scaled to a readable prefix") {
            expectThat(11_000.volts.toString())
                .isEqualTo("11 kV")
        }

        test(name = "A duration is rendered as a composite by default") {
            expectThat(90.seconds.toString())
                .isEqualTo("1 min 30 s")
        }

        test(name = "A duration can be printed as recorded with a plain scale") {
            expectThat(90.seconds)
                .get { format(PlainScale()) }
                .isEqualTo("90 s")
        }

        test(name = "A whole-minute duration renders without smaller components") {
            expectThat(5.minutes.toString())
                .isEqualTo("5 min")
        }
    }
}

private data object BlankSymbolUnit : Unit<Dimension.Ratio> {
    override val symbol: String = ""

    override fun toBaseUnit(value: Double): Double = value
    override fun fromBaseUnit(value: Double): Double = value
}
