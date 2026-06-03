package network.marsys.smarthome.domain.unit

/**
 * A physical dimension a measurement can be expressed in, such as energy, power
 * or temperature.
 *
 * A [Dimension] is a pure phantom (marker) type. It carries no state and is only
 * used as a type parameter to keep measurements of different dimensions from
 * being mixed at compile time (e.g. you cannot add an energy to a temperature).
 */
sealed interface Dimension {
    interface ElectricCurrent : Dimension
    interface Energy : Dimension
    interface Ratio : Dimension
    interface Power : Dimension
    interface Temperature : Dimension
    interface Time : Dimension
    interface Voltage : Dimension
}
