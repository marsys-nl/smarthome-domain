package network.marsys.smarthome.domain.unit

/**
 * Digital information dimension, with the byte (`B`) as its canonical base unit.
 */
sealed class DataSize(
    override val symbol: String,
) : Unit<Dimension.DigitalInformation> {
    final override fun toBaseUnit(value: Double): Double = value
    final override fun fromBaseUnit(value: Double): Double = value
}

/**
 * Digital information expressed in bytes, the SI unit of digital information.
 * Metric scaling
 */
data object Byte : DataSize(
    symbol = "B",
) {
    override val scale: Scale<Dimension.DigitalInformation> = MetricScale(
        prefixes = listOf(
            MetricPrefix.NONE,
            MetricPrefix.KILO,
            MetricPrefix.MEGA,
            MetricPrefix.GIGA,
            MetricPrefix.TERA,
            MetricPrefix.PETA,
        ),
    )
}

typealias MetricByte = Byte

data object BinaryByte : DataSize(
    symbol = "B",
) {
    override val scale: Scale<Dimension.DigitalInformation> = BinaryScale()
}

/* SI */

val Number.bytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = MetricByte)

val Number.kilobytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = MetricByte, prefix = MetricPrefix.KILO)

val Number.megabytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = MetricByte, prefix = MetricPrefix.MEGA)

val Number.gigabytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = MetricByte, prefix = MetricPrefix.GIGA)

val Number.terabytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = MetricByte, prefix = MetricPrefix.TERA)

val Number.petabytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = MetricByte, prefix = MetricPrefix.PETA)

/* Binary */

val Number.kibibytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = BinaryByte, prefix = BinaryPrefix.KIBI)

val Number.mebibytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = BinaryByte, prefix = BinaryPrefix.MEBI)

val Number.gibibytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = BinaryByte, prefix = BinaryPrefix.GIBI)

val Number.tebibytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = BinaryByte, prefix = BinaryPrefix.TEBI)

val Number.pebibytes: Quantity<Dimension.DigitalInformation> get() =
    measuredIn(unit = BinaryByte, prefix = BinaryPrefix.PEBI)
