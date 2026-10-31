package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.unit.Dimension
import network.marsys.smarthome.domain.unit.Quantity
import network.marsys.smarthome.domain.unit.celsius

data class TargetTemperature(
    override val current: Quantity<Dimension.Temperature>,
    override val context: Context = Context.Empty,
    val range: ClosedRange<Quantity<Dimension.Temperature>> = defaultTemperatureRange,
) : Capability<Quantity<Dimension.Temperature>, TargetTemperature>() {
    override val type: Type<TargetTemperature>
        get() = TargetTemperature

    init {
        require(current in range) {
            "Current temperature $current is out of range. Given range: $range."
        }
    }

    override fun updateWith(value: Quantity<Dimension.Temperature>, context: Context) =
        copy(
            current = value,
            context = context,
        )

    companion object : Type<TargetTemperature> {
        private val MIN_TARGET_TEMPERATURE = 5.celsius
        private val MAX_TARGET_TEMPERATURE = 35.celsius

        private val defaultTemperatureRange = MIN_TARGET_TEMPERATURE..MAX_TARGET_TEMPERATURE
    }
}
