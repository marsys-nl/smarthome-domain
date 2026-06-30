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

    operator fun plus(other: Number): Quantity<D> =
        plus(Quantity(other.toDouble(), unit))

    /**
     * Subtracts [other] from this measurement, keeping this measurement's [unit].
     */
    operator fun minus(other: Quantity<D>): Quantity<D> =
        Quantity(value = value - other.into(unit).value, unit = unit)

    operator fun minus(other: Number): Quantity<D> =
        minus(Quantity(other.toDouble(), unit))

    /**
     * Compares this measurement to [other] by comparing their values in the dimension's canonical base unit.
     */
    override fun compareTo(other: Quantity<D>): Int =
        baseValue.compareTo(other.baseValue)

    /**
     * Formats this measurement as a string, rounding the value and appending the unit's symbol, e.g. `230 W`.
     */
    override fun toString(): String = format()

    /**
     * Formats this measurement as a string using [scale], which defaults to the unit's own
     * [Unit.scale] (metric prefixes, a composite breakdown, etc.).
     *
     * Pass a different scale to override the rendering, e.g. [PlainScale] to print the value
     * as recorded or [MetricScale] to force a specific prefix.
     */
    fun format(scale: Scale<D> = unit.scale): String = with(round()) {
        scale.format(value, unit)
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
internal fun <D : Dimension> Number.measuredIn(
    unit: Unit<D>,
    prefix: MetricPrefix = MetricPrefix.NONE,
): Quantity<D> = Quantity(
    value = toDouble() * prefix.factor,
    unit = unit,
)
