package network.marsys.smarthome.domain.unit

/**
 * A unit of measurement within a single [Dimension].
 *
 * Every dimension defines one canonical *base unit*. A unit knows how to convert
 * a value expressed in itself to and from that base unit, which is what makes
 * conversions between any two units of the same dimension possible.
 *
 * [toBaseUnit] and [fromBaseUnit] must be exact inverses of each other.
 */
interface Unit<D : Dimension> {
    /**
     * Human-readable symbol of the unit, e.g. `"kWh"`, `"W"` or `"°C"`.
     */
    val symbol: String

    /**
     * Whether a space should be printed between the value and the symbol
     * when formatting a quantity expressed in this unit.
     */
    val spaceBetweenMagnitude: Boolean get() = true

    /**
     * Converts [value], expressed in this unit, to the dimension's base unit.
     */
    fun toBaseUnit(value: Double): Double

    /**
     * Converts [value], expressed in the dimension's base unit, to this unit.
     */
    fun fromBaseUnit(value: Double): Double

    /**
     * Rounds [value] for display purposes, if necessary.
     * By default, this is a no-op, but some units may choose to override it,
     * e.g. to round temperatures to the nearest 0.5°C.
     */
    fun round(value: Double): Double = value

    /**
     * Formats [value] as a string with the unit symbol, e.g. `"1.5 kWh"` or `"20°C"`
     * based upon the symbol, if the value is a whole number and if a space between
     * magnitude is needed.
     */
    fun format(value: Double): String =
        if (value % 1.0 == 0.0) {
            "${value.toInt()}${formatSymbol()}"
        } else {
            "${value}${formatSymbol()}"
        }

    private fun formatSymbol(): String =
        if (spaceBetweenMagnitude && symbol.isNotBlank()) {
            " $symbol"
        } else {
            symbol
        }
}
