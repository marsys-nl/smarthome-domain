package network.marsys.smarthome.domain.unit

/**
 * Temperature dimension, with the kelvin (`K`) as its canonical base unit.
 */
sealed class Temperature(
    override val symbol: String,
    private val factor: Double,
    private val offsetInKelvin: Double,
) : Unit<Dimension.Temperature> {
    final override val spaceBetweenMagnitude: Boolean
        get() = false

    override val rounding: Rounding
        get() = NearestMultiple(step = 0.1)

    final override fun toBaseUnit(value: Double): Double =
        value * factor + offsetInKelvin

    final override fun fromBaseUnit(value: Double): Double =
        (value - offsetInKelvin) / factor
}

/**
 * A temperature expressed in kelvin, the SI unit of temperature.
 */
data object Kelvin : Temperature(
    symbol = "K",
    factor = 1.0,
    offsetInKelvin = 0.0,
)

/**
 * A temperature expressed in degrees Celsius, a common unit of temperature in everyday life.
 */
data object Celsius : Temperature(
    symbol = "°C",
    factor = 1.0,
    offsetInKelvin = CELSIUS_OFFSET_IN_KELVIN,
) {
    override val rounding: Rounding
        get() = NearestMultiple(step = 0.5)
}

/**
 * A temperature expressed in degrees Fahrenheit, a common unit of temperature in everyday life.
 */
data object Fahrenheit : Temperature(
    symbol = "°F",
    factor = FAHRENHEIT_KELVIN_PER_DEGREE,
    offsetInKelvin = CELSIUS_OFFSET_IN_KELVIN - FAHRENHEIT_FREEZING_POINT * FAHRENHEIT_KELVIN_PER_DEGREE,
)

val Number.kelvin: Quantity<Dimension.Temperature> get() =
    measuredIn(unit = Kelvin)

val Number.celsius: Quantity<Dimension.Temperature> get() =
    measuredIn(unit = Celsius)

val Number.fahrenheit: Quantity<Dimension.Temperature> get() =
    measuredIn(unit = Fahrenheit)

private const val CELSIUS_OFFSET_IN_KELVIN = 273.15

private const val FAHRENHEIT_KELVIN_PER_DEGREE = 5.0 / 9.0
private const val FAHRENHEIT_FREEZING_POINT = 32.0
