package network.marsys.smarthome.domain.unit

/**
 * Electric potential difference (voltage) dimension, with the volt (`V`) as its canonical
 * base unit.
 */
sealed class Voltage(
    override val symbol: String,
    private val voltsPerUnit: Double,
) : Unit<Dimension.Voltage> {
    final override val scale: Scale<Dimension.Voltage> = MetricScale()

    final override fun toBaseUnit(value: Double): Double =
        value * voltsPerUnit

    final override fun fromBaseUnit(value: Double): Double =
        value / voltsPerUnit
}

/**
 * A potential difference expressed in volts, the SI unit of electric potential difference.
 */
data object Volt : Voltage(
    symbol = "V",
    voltsPerUnit = 1.0,
)

val Number.volts: Quantity<Dimension.Voltage> get() =
    measuredIn(Volt)
