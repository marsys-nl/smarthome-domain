package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.capability.Brightness
import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.OnOff
import network.marsys.smarthome.domain.i18n.TranslationKey

data class Light(
    override val identifier: EntityIdentifier,
    override val naming: EntityNaming = Light.naming(),
    override val state: Entity.State = State.Unknown,
) : Entity, Entity.Activatable {
    override val active: Boolean
        get() = state is State.Known && state.onOff.value.current

    sealed interface State : Entity.State {
        data class Known(
            val onOff: Capability.Required<OnOff>,
            val brightness: Capability.Optional<Brightness>,
        ) : State, Entity.State.Known {
            override fun find(type: Capability.Type<*>, context: Capability.Context): Capability<*, *>? =
                when (type) {
                    Brightness if brightness is Capability.Available<Brightness> -> brightness.value
                    OnOff -> onOff.value
                    else -> null
                }

            override fun updateWith(
                capability: Capability<*, *>,
            ): State = when (capability) {
                is OnOff -> copy(onOff = onOff.updateWith(capability = capability))
                is Brightness -> copy(brightness = brightness.updateWith(capability = capability))
                else -> unsupportedCapabilityError(capability = capability)
            }
        }

        data object Unknown : State, Entity.State.Unknown
    }

    companion object : Entity.Type<Light> {
        override val defaultNamingKey = TranslationKey("light")
    }
}
