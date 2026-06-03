package network.marsys.smarthome.domain.unit

/**
 * Ratio dimension, with the percent (`%`) as its canonical base unit.
 */
sealed class Ratio(
    override val symbol: String,
    private val percentPerUnit: Double,
) : Unit<Dimension.Ratio> {
    final override val spaceBetweenMagnitude: Boolean
        get() = false

    final override fun toBaseUnit(value: Double): Double =
        value * percentPerUnit

    final override fun fromBaseUnit(value: Double): Double =
        value / percentPerUnit
}

/**
 * A ratio expressed as a percentage, i.e. a value between 0 and 100, where 100% corresponds to the whole.
 * Values greater than 100% are allowed.
 */
data object Percent : Ratio(
    symbol = "%",
    percentPerUnit = 1.0,
)

/**
 * A ratio expressed as a fraction, i.e. a value between 0 and 1, where 1 corresponds to the whole.
 * Values greater than 1 are allowed.
 */
data object Fraction : Ratio(
    symbol = "",
    percentPerUnit = 100.0,
)

val Number.percent: Quantity<Dimension.Ratio> get() =
    measuredIn(unit = Percent)

val Number.fraction: Quantity<Dimension.Ratio> get() =
    measuredIn(unit = Fraction)
