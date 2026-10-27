package network.marsys.smarthome.domain.unit

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round as kotlinRound

/**
 * Strategy that decides how precise a magnitude is rounded for display.
 *
 * It is orthogonal to [Scale]: the scale decides *which* magnitude and symbol are shown, the
 * rounding decides *how precise* that magnitude is. Rounding is applied as late as possible,
 * after scaling, so intermediate arithmetic keeps full precision.
 */
fun interface Rounding {
    /**
     * Rounds [value] for display purposes.
     */
    fun round(value: Double): Double

    /**
     * Renders the rounded [value] as text. Only finite values are passed in.
     * The default drops trailing zeroes, so `40.0` reads as `"40"`.
     */
    fun format(value: Double): String = with(round(value)) {
        if (this % 1.0 == 0.0) {
            toLong().toString()
        } else {
            toString()
        }
    }

    companion object {
        val None: Rounding = Rounding { it }
        val Default: Rounding = AdaptiveDecimals()
    }
}

/**
 * Rounds to [decimals] fractional digits, but shifts the window towards the first significant
 * digit when the value is smaller than one, so small magnitudes do not collapse to zero.
 *
 * With `decimals = 2` and `maxDecimals = 6`:
 * - `1.5123` reads as `1.51`      (no leading zeroes, plain two decimals)
 * - `0.0015123` reads as `0.0015` (two leading zeroes, so four decimals)
 * - `0.000000015` reads as `0`    (seven leading zeroes exceed maxDecimals)
 */
class AdaptiveDecimals(
    private val decimals: Int = DEFAULT_DECIMALS,
    private val maxLeadingZeroes: Int = decimals + MAX_LEADING_ZEROES,
) : Rounding {
    init {
        require(decimals >= 0) {
            "decimals must not be negative, but was $decimals"
        }

        require(maxLeadingZeroes >= decimals) {
            "maxLeadingZeroes must be at least decimals ($decimals), but was $maxLeadingZeroes"
        }
    }

    override fun round(value: Double): Double {
        if (!value.isFinite() || value == 0.0) {
            return value
        }

        return 10.0.pow(effectiveDecimals(value)).let { factor ->
            kotlinRound(value * factor) / factor
        }
    }

    private fun effectiveDecimals(value: Double): Int {
        val leadingZeroes = floor(-log10(abs(value)))
            .toInt()
            .coerceAtLeast(0)

        return (decimals + leadingZeroes)
            .coerceAtMost(maxLeadingZeroes)
    }

    private companion object {
        private const val DEFAULT_DECIMALS = 2
        private const val MAX_LEADING_ZEROES = 4
    }
}

/**
 * Rounds to at most [decimals] fractional digits, half away from zero. Trailing zeroes are
 * dropped when rendering, so with two decimals `41.667` reads as `41.67` and `40.0` as `40`.
 */
class Decimals(
    private val decimals: Int,
) : Rounding {
    init {
        require(decimals >= 0) {
            "decimals must not be negative, but was $decimals"
        }
    }

    override fun round(value: Double): Double {
        if (!value.isFinite() || value == 0.0) {
            return value
        }

        return 10.0.pow(decimals).let { factor ->
            kotlinRound(value * factor) / factor
        }
    }

    override fun format(value: Double): String {
        val factor = (0 until decimals)
            .fold(1L) { acc, _ -> acc * 10L }

        val units = round(abs(value) * factor).toLong()
        val sign = if (value < 0.0 && units != 0L) {
            "-"
        } else {
            ""
        }

        if (decimals == 0) {
            return "$sign${units}"
        }

        val fraction = (units % factor)
            .toString()
            .padStart(decimals, '0')

        return "${sign}${units / factor}.${fraction}"
    }
}

/**
 * Rounds to the nearest multiple of [step], e.g. `21.3` with a step of `0.5` reads as `21.5`.
 */
class NearestMultiple(
    private val step: Double,
) : Rounding {
    init {
        require(step > 0.0) {
            "step must be positive, but was $step"
        }
    }

    private val factor: Double = 10.0.pow(decimalsNeededFor(step))

    @Suppress("UseIfInsteadOfWhen")
    override fun round(value: Double): Double =
        when {
            value.isFinite() -> kotlinRound(kotlinRound(value / step) * step * factor) / factor
            else -> value
        }

    private companion object {
        private const val MAX_DECIMALS = 15
        private const val EPSILON = 1e-9

        private fun decimalsNeededFor(step: Double): Int =
            (0..MAX_DECIMALS).firstOrNull { decimals ->
                val scaled = step * 10.0.pow(decimals)
                scaled >= 1.0 && abs(scaled - kotlinRound(scaled)) < EPSILON
            } ?: MAX_DECIMALS
    }
}
