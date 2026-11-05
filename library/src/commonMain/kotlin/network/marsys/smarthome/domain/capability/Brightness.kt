package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.entity.Entity
import network.marsys.smarthome.domain.entity.EntityIdentifier
import network.marsys.smarthome.domain.unit.Dimension
import network.marsys.smarthome.domain.unit.Quantity

data class Brightness(
    override val current: Quantity<Dimension.Ratio>,
    override val context: Context = Context.Empty,
) : Capability<Quantity<Dimension.Ratio>, Brightness>() {
    override val type: Type<Brightness>
        get() = Brightness

    override fun updateWith(value: Quantity<Dimension.Ratio>, context: Context) =
        copy(
            current = value,
            context = context,
        )

    data class SetBrightness(
        override val identifier: EntityIdentifier,
        val brightness: Quantity<Dimension.Ratio>,
    ) : Entity.Action

    companion object : Type<Brightness>
}
