package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.entity.Entity
import network.marsys.smarthome.domain.entity.EntityIdentifier

data class OnOff(
    override val current: Boolean,
    override val context: Context = Context.Empty,
) : Capability<Boolean, OnOff>() {
    override val type: Type<OnOff>
        get() = OnOff

    override fun updateWith(value: Boolean, context: Context) =
        copy(
            current = value,
            context = context,
        )

    sealed interface Toggle : Action {
        data class On(
            override val entity: EntityIdentifier,
            override val context: Context = Context.Empty,
        ) : Toggle

        data class Off(
            override val entity: EntityIdentifier,
            override val context: Context = Context.Empty,
        ) : Toggle
    }

    companion object : Type<OnOff>
}
