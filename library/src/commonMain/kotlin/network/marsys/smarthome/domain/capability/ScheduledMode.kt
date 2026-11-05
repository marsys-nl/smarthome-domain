package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.entity.Entity
import network.marsys.smarthome.domain.entity.EntityIdentifier

data class ScheduledMode(
    override val current: Boolean,
    override val context: Context = Context.Empty,
) : Capability<Boolean, ScheduledMode>() {
    override val type: Type<ScheduledMode>
        get() = ScheduledMode

    override fun updateWith(value: Boolean, context: Context) =
        copy(
            current = value,
            context = context,
        )

    sealed interface Toggle : Entity.Action {
        data class On(
            override val identifier: EntityIdentifier,
        ) : Toggle

        data class Off(
            override val identifier: EntityIdentifier,
        ) : Toggle
    }

    companion object : Type<ScheduledMode>
}
