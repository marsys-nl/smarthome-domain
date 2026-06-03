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
     * The [Scale] strategy used to render quantities expressed in this unit, e.g. metric
     * prefixes or a composite breakdown. Defaults to a plain value-and-symbol rendering.
     */
    val scale: Scale<D> get() = PlainScale()

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
}
