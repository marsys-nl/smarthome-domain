package network.marsys.smarthome.domain.unit

import kotlin.math.abs
import kotlin.math.pow

/**
 * Interface that defines what a prefix should look like.
 */
interface Prefix {
    val symbol: String
    val factor: Double
}

/**
 * A binary prefix that scales a unit by a power of 2, such as kibi (`Ki`, x2¹⁰)
 * or gibi (`Gi`, x2³⁰).
 */
@Suppress("MagicNumber")
enum class BinaryPrefix(
    override val symbol: String,
    override val factor: Double,
) : Prefix {
    NONE(symbol = "", factor = 1.0),
    KIBI(symbol = "Ki", factor = 2.0.pow(10)),
    MEBI(symbol = "Mi", factor = 2.0.pow(20)),
    GIBI(symbol = "Gi", factor = 2.0.pow(30)),
    TEBI(symbol = "Ti", factor = 2.0.pow(40)),
    PEBI(symbol = "Pi", factor = 2.0.pow(50)),
}

/**
 * A decimal prefix that scales a unit by a power of ten, such as kilo (`k`, ×10³)
 * or milli (`m`, ×10⁻³).
 */
enum class MetricPrefix(
    override val symbol: String,
    override val factor: Double,
) : Prefix {
    NANO(symbol = "n", factor = 1e-9),
    MICRO(symbol = "µ", factor = 1e-6),
    MILLI(symbol = "m", factor = 1e-3),
    CENTI(symbol = "c", factor = 1e-2),

    NONE(symbol = "", factor = 1e0),

    KILO(symbol = "k", factor = 1e3),
    MEGA(symbol = "M", factor = 1e6),
    GIGA(symbol = "G", factor = 1e9),
    TERA(symbol = "T", factor = 1e12),
    PETA(symbol = "P", factor = 1e15),
}

/**
 * Selects the most readable prefix from this list for [value]: the largest prefix whose
 * [MetricPrefix.factor] does not exceed the magnitude of [value], so that the scaled value
 * is at least `1` whenever possible.
 *
 * Returns [MetricPrefix.NONE] when the list is empty, meaning no prefixing takes place.
 * Falls back to the smallest available prefix when [value] is zero or smaller than every
 * available prefix.
 */
internal fun <T : Prefix> List<T>.preferredFor(
    value: Double,
    baseValue: T,
): T {
    val fallback = minByOrNull { it.factor } ?: baseValue
    val magnitude = abs(value)

    if (magnitude == 0.0) {
        return fallback
    }

    return sortedBy { it.factor }
        .lastOrNull { it.factor <= magnitude }
        ?: fallback
}

internal fun List<BinaryPrefix>.preferredFor(value: Double): BinaryPrefix =
    preferredFor(value = value, baseValue = BinaryPrefix.NONE)

internal fun List<MetricPrefix>.preferredFor(value: Double): MetricPrefix =
    preferredFor(value = value, baseValue = MetricPrefix.NONE)
