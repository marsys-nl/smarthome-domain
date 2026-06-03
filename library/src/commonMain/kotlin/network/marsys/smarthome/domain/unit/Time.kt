package network.marsys.smarthome.domain.unit

/**
 * Time dimension, with the second (`s`) as its canonical base unit.
 *
 * Time scales by non-decimal multiples (minutes, hours, days) rather than metric prefixes,
 * so its units carry no [Unit.prefixes] and are never rescaled when formatted.
 */
sealed class Time(
    override val symbol: String,
    private val secondsPerUnit: Double,
) : Unit<Dimension.Time> {
    final override fun toBaseUnit(value: Double): Double =
        value * secondsPerUnit

    final override fun fromBaseUnit(value: Double): Double =
        value / secondsPerUnit
}

/**
 * A duration expressed in seconds, the SI unit of time.
 */
data object Second : Time(
    symbol = "s",
    secondsPerUnit = 1.0,
)

/**
 * A duration expressed in minutes. One minute equals 60 [Second]s.
 */
data object Minute : Time(
    symbol = "min",
    secondsPerUnit = 60.0,
)

/**
 * A duration expressed in hours. One hour equals 3,600 [Second]s.
 */
data object Hour : Time(
    symbol = "h",
    secondsPerUnit = 3_600.0,
)

/**
 * A duration expressed in days. One day equals 86,400 [Second]s.
 */
data object Day : Time(
    symbol = "d",
    secondsPerUnit = 86_400.0,
)

val Number.seconds: Quantity<Dimension.Time> get() =
    measuredIn(Second)

val Number.minutes: Quantity<Dimension.Time> get() =
    measuredIn(Minute)

val Number.hours: Quantity<Dimension.Time> get() =
    measuredIn(Hour)

val Number.days: Quantity<Dimension.Time> get() =
    measuredIn(Day)
