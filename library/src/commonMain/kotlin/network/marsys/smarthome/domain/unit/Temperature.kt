package network.marsys.smarthome.domain.unit

import kotlin.math.round as kotlinRound

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

    final override fun toBaseUnit(value: Double): Double =
        value * factor + offsetInKelvin

    final override fun fromBaseUnit(value: Double): Double =
        (value - offsetInKelvin) / factor

    override fun round(value: Double): Double =
        kotlinRound(value * TEMPERATURE_ROUNDING_OFFSET) / TEMPERATURE_ROUNDING_OFFSET
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
    override fun round(value: Double): Double =
        kotlinRound(value * CELSIUS_ROUNDING_OFFSET) / CELSIUS_ROUNDING_OFFSET
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
    measuredIn(Kelvin)

val Number.celsius: Quantity<Dimension.Temperature> get() =
    measuredIn(Celsius)

val Number.fahrenheit: Quantity<Dimension.Temperature> get() =
    measuredIn(Fahrenheit)

private const val CELSIUS_OFFSET_IN_KELVIN = 273.15
private const val CELSIUS_ROUNDING_OFFSET = 2.0

private const val FAHRENHEIT_KELVIN_PER_DEGREE = 5.0 / 9.0
private const val FAHRENHEIT_FREEZING_POINT = 32.0

private const val TEMPERATURE_ROUNDING_OFFSET = 10.0
