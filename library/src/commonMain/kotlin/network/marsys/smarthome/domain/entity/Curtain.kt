package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.Movement
import network.marsys.smarthome.domain.capability.Opened
import network.marsys.smarthome.domain.capability.Position
import network.marsys.smarthome.domain.zone.ZoneIdentifier

data class Curtain(
    override val identifier: EntityIdentifier,
    override val naming: EntityNaming = Curtain.naming(),
    override val state: State = State.Unknown,
    override val zone: ZoneIdentifier? = null,
) : Cover() {
    override val orientation: Orientation
        get() = Orientation.Horizontal

    override val type: Entity.Type<*> = Curtain

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

    companion object : Entity.Type<Curtain> {
        override val defaultNamingKey = "curtain"
    }
}
