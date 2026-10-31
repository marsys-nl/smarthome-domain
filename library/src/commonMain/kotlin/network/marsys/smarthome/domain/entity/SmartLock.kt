package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.Capability.Context
import network.marsys.smarthome.domain.capability.OnOff
import network.marsys.smarthome.domain.i18n.TranslationKey

data class SmartLock(
    override val identifier: EntityIdentifier,
    override val naming: EntityNaming = SmartLock.naming(),
    override val state: Entity.State = State.Unknown,
) : Entity, Entity.Activatable {
    override val active: Boolean
        get() = state is State.Known && state.onOff.value.current

    sealed interface State : Entity.State {
        data class Known(
            val onOff: Capability.Required<OnOff>,
        ) : State, Entity.State.Known {
            override fun find(type: Capability.Type<*>, context: Context): Capability<*, *>? =
                when (type) {
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

    companion object : Entity.Type<SmartLock> {
        override val defaultNamingKey = TranslationKey("smart-lock")
    }
}
