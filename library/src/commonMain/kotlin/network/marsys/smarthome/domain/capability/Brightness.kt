package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.unit.Dimension
import network.marsys.smarthome.domain.unit.Quantity

data class Brightness(
    override val current: Quantity<Dimension.Ratio>,
    override val context: Context = Context.Empty,
) : Capability<Quantity<Dimension.Ratio>, Brightness>() {
    override fun updateWith(value: Quantity<Dimension.Ratio>, context: Context) =
        copy(
            current = value,
            context = context,
        )
}
