package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.unit.Dimension
import network.marsys.smarthome.domain.unit.Quantity

data class WindowDetection(
    override val current: Boolean,
    override val context: Context = Context.Empty,
) : Capability<Boolean, WindowDetection>() {
    override fun updateWith(value: Boolean, context: Context) =
        copy(
            current = value,
            context = context,
        )
}
