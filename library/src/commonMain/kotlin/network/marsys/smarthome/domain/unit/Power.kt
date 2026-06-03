package network.marsys.smarthome.domain.unit

/**
 * Power dimension, with the watt (`W`) as its canonical base unit.
 */
sealed class Power(
    override val symbol: String,
    private val wattsPerUnit: Double,
    override val prefixes: List<MetricPrefix> = emptyList(),
) : Unit<Dimension.Power> {
    final override fun toBaseUnit(value: Double): Double =
        value * wattsPerUnit

    final override fun fromBaseUnit(value: Double): Double =
        value / wattsPerUnit
}

/**
 * A power expressed in watts, the SI unit of power.
 */
data object Watt : Power(
    symbol = "W",
    wattsPerUnit = 1.0,
    prefixes = listOf(
        MetricPrefix.NONE,
        MetricPrefix.KILO,
        MetricPrefix.MEGA,
        MetricPrefix.GIGA,
    ),
)

/**
 * A power expressed in kilowatts, a common unit of power in the context of electricity,
 * especially for household energy consumption.
 */
data object Kilowatt : Power(
    symbol = "kW",
    wattsPerUnit = 1_000.0,
)

val Number.watts: Quantity<Dimension.Power> get() =
    measuredIn(Watt)

val Number.kilowatts: Quantity<Dimension.Power> get() =
    measuredIn(Kilowatt)
