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
     * The decimal [MetricPrefix]es that are sensible to display values of this unit with.
     *
     * When empty (the default) no prefixing takes place and the [symbol] is used as-is,
     * which is the case for e.g. temperatures.
     */
    val prefixes: List<MetricPrefix> get() = emptyList()

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
     * Formats [value] as a string, scaling it by [prefix] and prepending the prefix symbol
     * to the unit symbol, e.g. formatting `1.5` watts with [MetricPrefix.KILO] yields
     * `"1.5 kW"`. The bare value is rendered when [prefix] is [MetricPrefix.NONE].
     */
    fun format(
        value: Double,
        prefix: MetricPrefix = MetricPrefix.NONE,
    ): String {
        val scaled = value / prefix.factor

        return if (scaled % 1.0 == 0.0) {
            "${scaled.toInt()}${formatSymbol(prefix)}"
        } else {
            "$scaled${formatSymbol(prefix)}"
        }
    }

    private fun formatSymbol(prefix: MetricPrefix): String {
        val prefixedSymbol = "${prefix.symbol}$symbol"

        return if (spaceBetweenMagnitude && prefixedSymbol.isNotBlank()) {
            " $prefixedSymbol"
        } else {
            prefixedSymbol
        }
    }
}
