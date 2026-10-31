package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.unit.Dimension
import network.marsys.smarthome.domain.unit.Quantity

data class MeasureDataSize(
    override val current: Quantity<Dimension.DigitalInformation>,
    override val context: Context = Context.Empty,
) : Capability<Quantity<Dimension.DigitalInformation>, MeasureDataSize>() {
    override val type: Type<MeasureDataSize>
        get() = MeasureDataSize

    override fun updateWith(value: Quantity<Dimension.DigitalInformation>, context: Context) =
        copy(
            current = value,
            context = context,
        )

    companion object : Type<MeasureDataSize>
}
