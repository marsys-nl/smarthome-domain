package network.marsys.smarthome.domain.unit

/**
 * A measurement: a [value] paired with the [unit] it was recorded in.
 *
 * A quantity can be converted to any other unit of the same [Dimension] with
 * [into], compared across units, and combined with [plus] / [minus].
 * Because the dimension is a type parameter, mixing dimensions (e.g. adding an
 * energy to a temperature) is rejected at compile time.
 */
data class Quantity<D : Dimension>(
    val value: Double,
    val unit: Unit<D>,
) : Comparable<Quantity<D>> {
    private val baseValue: Double get() = unit.toBaseUnit(value)

    /**
     * Returns this measurement converted to [target].
     */
    infix fun into(target: Unit<D>): Quantity<D> =
        Quantity(value = target.fromBaseUnit(baseValue), unit = target)

    /**
     * Adds [other] to this measurement, keeping this measurement's [unit].
     */
    operator fun plus(other: Quantity<D>): Quantity<D> =
        Quantity(value = value + other.into(unit).value, unit = unit)

    /**
     * Subtracts [other] from this measurement, keeping this measurement's [unit].
     */
    operator fun minus(other: Quantity<D>): Quantity<D> =
        Quantity(value = value - other.into(unit).value, unit = unit)

    /**
     * Compares this measurement to [other] by comparing their values in the dimension's canonical base unit.
     */
    override fun compareTo(other: Quantity<D>): Int =
        baseValue.compareTo(other.baseValue)

    /**
     * Formats this measurement as a string, rounding the value and appending the unit's symbol, e.g. `230 W`.
     */
    override fun toString(): String = with(round()) {
        unit.format(value)
    }

    /**
     * Rounds the value of this measurement according to the rounding rules of its unit,
     * e.g. rounding a temperature in Celsius to the nearest half degree.
     */
    fun round(): Quantity<D> =
        Quantity(value = unit.round(value), unit = unit)
}

/**
 * Creates a [Quantity] from this number expressed in [unit], e.g. `230 measuredIn PowerUnit.Watt`.
 */
internal infix fun <D : Dimension> Number.measuredIn(unit: Unit<D>): Quantity<D> =
    Quantity(value = toDouble(), unit = unit)
