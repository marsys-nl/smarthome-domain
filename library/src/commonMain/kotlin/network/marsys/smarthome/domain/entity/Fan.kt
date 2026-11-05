package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.OnOff
import network.marsys.smarthome.domain.zone.ZoneIdentifier

data class Fan(
    override val identifier: EntityIdentifier,
    override val naming: EntityNaming = Fan.naming(),
    override val state: Entity.State = State.Unknown,
    override val zone: ZoneIdentifier? = null,
) : Entity, Entity.Activatable {
    override val active: Boolean
        get() = state is State.Known && state.onOff.value.current

    override val type: Entity.Type<*> = Fan

    sealed interface State : Entity.State {
        data class Known(
            val onOff: Capability.Required<OnOff>,
        ) : State, Entity.State.Known {
            override fun find(query: Capability.Query<*>): Capability<*, *>? =
                when (query.type) {
                    OnOff -> onOff.value
                    else -> null
                }

            override fun updateWith(
                capability: Capability<*, *>,
            ): State = when (capability) {
                is OnOff -> copy(onOff = onOff.updateWith(capability = capability))
                else -> unsupportedCapabilityError(capability = capability)
            }
        }

        data object Unknown : State, Entity.State.Unknown
    }

    companion object : Entity.Type<Fan> {
        override val defaultNamingKey = "fan"
    }
}
