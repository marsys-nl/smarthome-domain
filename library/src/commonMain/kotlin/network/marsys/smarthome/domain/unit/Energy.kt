package network.marsys.smarthome.domain.unit

/**
 * Energy dimension, with the joule (`J`) as its canonical base unit.
 */
sealed class Energy(
    override val symbol: String,
    private val joulesPerUnit: Double,
) : Unit<Dimension.Energy> {
    final override val scale: Scale<Dimension.Energy> = MetricScale()

    final override fun toBaseUnit(value: Double): Double =
        value * joulesPerUnit

    final override fun fromBaseUnit(value: Double): Double =
        value / joulesPerUnit
}

/**
 * An energy expressed in joules, the SI unit of energy.
 */
data object Joule : Energy(
    symbol = "J",
    joulesPerUnit = 1.0,
)

/**
 * An energy expressed in watt-seconds. One watt-second equals exactly one [Joule].
 */
data object WattSecond : Energy(
    symbol = "W·s",
    joulesPerUnit = 1.0,
)

/**
 * An energy expressed in watt-minutes. One watt-minute equals 60 [Joule]s.
 */
data object WattMinute : Energy(
    symbol = "W·min",
    joulesPerUnit = 60.0,
)

/**
 * An energy expressed in watt-hours, a common unit of energy in the context of electricity.
 */
data object WattHour : Energy(
    symbol = "Wh",
    joulesPerUnit = 3_600.0,
)

val Number.joules: Quantity<Dimension.Energy> get() =
    measuredIn(unit = Joule)

val Number.wattSeconds: Quantity<Dimension.Energy> get() =
    measuredIn(unit = WattSecond)

val Number.wattMinutes: Quantity<Dimension.Energy> get() =
    measuredIn(unit = WattMinute)

val Number.wattHours: Quantity<Dimension.Energy> get() =
    measuredIn(unit = WattHour)

val Number.kilowattHours: Quantity<Dimension.Energy> get() =
    measuredIn(unit = WattHour, prefix = MetricPrefix.KILO)
