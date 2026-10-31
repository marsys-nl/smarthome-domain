package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.unit.Dimension
import network.marsys.smarthome.domain.unit.Quantity

data class MeasureLoad(
    override val current: Quantity<Dimension.Ratio>,
    override val context: Context = Context.Empty,
) : Capability<Quantity<Dimension.Ratio>, MeasureLoad>() {
    override val type: Type<MeasureLoad>
        get() = MeasureLoad

    override fun updateWith(value: Quantity<Dimension.Ratio>, context: Context) =
        copy(
            current = value,
            context = context,
        )

    companion object : Type<MeasureLoad>
}
