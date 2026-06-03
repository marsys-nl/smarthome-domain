package network.marsys.smarthome.domain.unit

/**
 * Electric current dimension, with the ampere (`A`) as its canonical base unit.
 */
sealed class ElectricCurrent(
    override val symbol: String,
    private val amperesPerUnit: Double,
) : Unit<Dimension.ElectricCurrent> {
    final override val prefixes: List<MetricPrefix> = listOf(
        MetricPrefix.NONE,
        MetricPrefix.KILO,
        MetricPrefix.MEGA,
        MetricPrefix.GIGA,
    )

    final override fun toBaseUnit(value: Double): Double =
        value * amperesPerUnit

    final override fun fromBaseUnit(value: Double): Double =
        value / amperesPerUnit
}

/**
 * An electric current expressed in amperes, the SI unit of electric current.
 */
data object Ampere : ElectricCurrent(
    symbol = "A",
    amperesPerUnit = 1.0,
)

val Number.amperes: Quantity<Dimension.ElectricCurrent> get() =
    measuredIn(Ampere)
