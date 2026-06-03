package network.marsys.smarthome.domain.unit

import kotlin.math.abs

/**
 * A decimal prefix that scales a unit by a power of ten, such as kilo (`k`, ×10³)
 * or milli (`m`, ×10⁻³).
 */
enum class MetricPrefix(
    val symbol: String,
    val factor: Double,
) {
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
internal fun List<MetricPrefix>.preferredFor(value: Double): MetricPrefix {
    val fallback = minByOrNull { it.factor } ?: MetricPrefix.NONE
    val magnitude = abs(value)

    if (magnitude == 0.0) {
        return fallback
    }

    return sortedBy { it.factor }
        .lastOrNull { it.factor <= magnitude }
        ?: fallback
}
