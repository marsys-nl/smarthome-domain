package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.Capability.Context
import network.marsys.smarthome.domain.capability.Movement
import network.marsys.smarthome.domain.capability.Opened
import network.marsys.smarthome.domain.capability.Position
import network.marsys.smarthome.domain.i18n.TranslationKey

data class Shutter(
    override val identifier: EntityIdentifier,
    override val naming: EntityNaming = Shutter.naming(),
    override val state: State = State.Unknown,
) : Cover() {
    override val orientation: Orientation
        get() = Orientation.Vertical

    sealed interface State : Cover.State {
        data class Known(
            override val control: Control,
        ) : State, Cover.State.Known {
            override fun updateWith(capability: Capability<*, *>): Entity.State.Known =
                when (capability) {
                    is Movement -> copy(control = control.updateWith(capability))
                    is Opened -> copy(control = control.updateWith(capability))
                    is Position -> copy(control = control.updateWith(capability))
                    else -> unsupportedCapabilityError(capability = capability)
                }
        }

        data object Unknown : State, Entity.State.Unknown
    }

    companion object : Entity.Type<Shutter> {
        override val defaultNamingKey = TranslationKey("shutter")
    }
}
