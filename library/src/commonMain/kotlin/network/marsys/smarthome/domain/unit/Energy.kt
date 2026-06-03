package network.marsys.smarthome.domain.unit

/**
 * Energy dimension, with the joule (`J`) as its canonical base unit.
 */
sealed class Energy(
    override val symbol: String,
    private val joulesPerUnit: Double,
    override val prefixes: List<MetricPrefix> = emptyList(),
) : Unit<Dimension.Energy> {
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
    prefixes = listOf(
        MetricPrefix.NONE,
        MetricPrefix.KILO,
        MetricPrefix.MEGA,
        MetricPrefix.GIGA,
    ),
)

/**
 * An energy expressed in watt-hours, a common unit of energy in the context of electricity.
 */
data object WattHour : Energy(
    symbol = "Wh",
    joulesPerUnit = 3_600.0,
    prefixes = listOf(
        MetricPrefix.NONE,
        MetricPrefix.KILO,
        MetricPrefix.MEGA,
        MetricPrefix.GIGA,
    ),
)

/**
 * An energy expressed in kilowatt-hours, a common unit of energy in the context of electricity,
 * especially for household energy consumption.
 */
data object KilowattHour : Energy(
    symbol = "kWh",
    joulesPerUnit = 3_600_000.0,
)

val Number.joules: Quantity<Dimension.Energy> get() =
    measuredIn(Joule)

val Number.wattHours: Quantity<Dimension.Energy> get() =
    measuredIn(WattHour)

val Number.kilowattHours: Quantity<Dimension.Energy> get() =
    measuredIn(KilowattHour)
