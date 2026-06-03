package network.marsys.smarthome.domain.unit

/**
 * Time dimension, with the second (`s`) as its canonical base unit.
 *
 * Time scales by non-decimal multiples (minutes, hours, days) rather than metric prefixes, so
 * its quantities are rendered as a composite breakdown (e.g. `"1 h 23 min 45 s"`) by default.
 */
sealed class Time(
    override val symbol: String,
    private val secondsPerUnit: Double,
) : Unit<Dimension.Time> {
    final override val scale: Scale<Dimension.Time>
        get() = CompositeScale(units, largestUnit = Day, smallestUnit = Second)

    final override fun toBaseUnit(value: Double): Double =
        value * secondsPerUnit

    final override fun fromBaseUnit(value: Double): Double =
        value / secondsPerUnit
}

private val units = listOf(Second, Minute, Hour, Day)

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

/**
 * Formats this duration as a composite string (e.g. `"1 h 23 min 45 s"`), broken down between
 * [largestUnit] and [smallestUnit] and rounded to the resolution of [smallestUnit].
 */
fun Quantity<Dimension.Time>.format(
    largestUnit: Time = Day,
    smallestUnit: Time = Second,
): String = format(
    scale = CompositeScale(
        units = units,
        largestUnit = largestUnit,
        smallestUnit = smallestUnit,
    ),
)

/**
 * Decomposes this duration into its ordered, whole-number components (e.g. `[1 h, 23 min, 45 s]`)
 * between [largestUnit] and [smallestUnit], rounded to the resolution of [smallestUnit].
 */
fun Quantity<Dimension.Time>.toComponents(
    largestUnit: Time = Day,
    smallestUnit: Time = Second,
): List<Quantity<Dimension.Time>> =
    CompositeScale(
        units = units,
        largestUnit = largestUnit,
        smallestUnit = smallestUnit,
    ).components(value, unit)
