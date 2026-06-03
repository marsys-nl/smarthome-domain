package network.marsys.smarthome.domain.unit

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round

/**
 * Strategy that decides how a [Quantity] is scaled and rendered for display.
 *
 * It decouples *what* is measured (a value in a [Unit]) from *how it reads*: metric prefixes,
 * a composite breakdown across several units, or a plain value and symbol.
 * New display systems are added as new implementations, without touching [Unit] or [Quantity].
 */
interface Scale<D : Dimension> {
    /**
     * Renders [value], expressed in [unit], as a display string.
     */
    fun format(value: Double, unit: Unit<D>): String
}

/**
 * Renders a value as-is: the bare value followed by the unit symbol, with no scaling.
 * This is the default for units whose values are read at their recorded magnitude, such as
 * temperatures and ratios.
 */
class PlainScale<D : Dimension> : Scale<D> {
    override fun format(value: Double, unit: Unit<D>): String =
        formatMagnitude(value, unit.symbol, unit.spaceBetweenMagnitude)
}

/**
 * Scales a value with the most readable decimal [MetricPrefix] from [prefixes], e.g. rendering
 * `1500` watts as `"1.5 kW"`. Construct with a single prefix to force it.
 */
class MetricScale<D : Dimension>(
    private val prefixes: List<MetricPrefix> = defaultPrefixes,
) : Scale<D> {
    constructor(prefix: MetricPrefix) : this(listOf(prefix))

    override fun format(value: Double, unit: Unit<D>): String {
        val prefix = prefixes.preferredFor(value)

        return formatMagnitude(
            value = value / prefix.factor,
            symbol = "${prefix.symbol}${unit.symbol}",
            spaceBetweenMagnitude = unit.spaceBetweenMagnitude,
        )
    }

    companion object {
        internal val defaultPrefixes: List<MetricPrefix> = listOf(
            MetricPrefix.NONE,
            MetricPrefix.KILO,
            MetricPrefix.MEGA,
            MetricPrefix.GIGA,
        )
    }
}

/**
 * Breaks a value down across a family of [units], rendering it as a composite such as
 * `"1 h 23 min 45 s"`.
 *
 * The value is first rounded to the resolution of [smallestUnit] and then split greedily, from
 * [largestUnit] down to [smallestUnit], into whole-number components. Zero components are
 * omitted; an all-zero value renders as `"0 {smallestUnit.symbol}"`; negative values render
 * their magnitude behind a leading `-`.
 */
class CompositeScale<D : Dimension>(
    private val units: List<Unit<D>>,
    private val largestUnit: Unit<D>,
    private val smallestUnit: Unit<D>,
) : Scale<D> {
    override fun format(value: Double, unit: Unit<D>): String {
        val components = components(value, unit)
        val rendered = components.joinToString(separator = " ") { component ->
            formatMagnitude(
                value = component.value,
                symbol = component.unit.symbol,
                spaceBetweenMagnitude = component.unit.spaceBetweenMagnitude,
            )
        }

        return if (value < 0.0 && components.any { it.value != 0.0 }) {
            "-$rendered"
        } else {
            rendered
        }
    }

    /**
     * Decomposes [value], expressed in [unit], into ordered whole-number components from
     * [largestUnit] down to [smallestUnit].
     */
    fun components(value: Double, unit: Unit<D>): List<Quantity<D>> {
        val smallestSize = smallestUnit.toBaseUnit(1.0)
        val ordered = units
            .filter { it.toBaseUnit(1.0) in smallestSize..largestUnit.toBaseUnit(1.0) }
            .sortedByDescending { it.toBaseUnit(1.0) }

        var remaining = round(abs(unit.toBaseUnit(value)) / smallestSize) * smallestSize
        val result = mutableListOf<Quantity<D>>()

        ordered.forEachIndexed { index, componentUnit ->
            val size = componentUnit.toBaseUnit(1.0)
            val count = if (index == ordered.lastIndex) {
                round(remaining / size)
            } else {
                floor(remaining / size)
            }
            remaining -= count * size

            if (count != 0.0) {
                result += Quantity(value = count, unit = componentUnit)
            }
        }

        return result.ifEmpty {
            listOf(Quantity(value = 0.0, unit = smallestUnit))
        }
    }
}

/**
 * Renders [value] followed by [symbol], dropping the fractional part when the value is whole
 * and inserting a space before a non-blank [symbol] when [spaceBetweenMagnitude] is set.
 */
internal fun formatMagnitude(
    value: Double,
    symbol: String,
    spaceBetweenMagnitude: Boolean,
): String {
    val separator = if (spaceBetweenMagnitude && symbol.isNotBlank()) {
        " "
    } else {
        ""
    }

    val magnitude = if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        value.toString()
    }

    return "$magnitude$separator$symbol"
}
