package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.unit.Dimension
import network.marsys.smarthome.domain.unit.Quantity

data class MeasureTemperature(
    override val current: Quantity<Dimension.Temperature>,
    override val context: Context = Context.Empty,
) : Capability<Quantity<Dimension.Temperature>, MeasureTemperature>() {
    override val type: Type<MeasureTemperature>
        get() = MeasureTemperature

    override fun updateWith(value: Quantity<Dimension.Temperature>, context: Context) =
        copy(
            current = value,
            context = context,
        )

    companion object : Type<MeasureTemperature>
}
