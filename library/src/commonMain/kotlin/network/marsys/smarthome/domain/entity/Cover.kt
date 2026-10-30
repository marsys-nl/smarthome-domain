package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.Capability.Context
import network.marsys.smarthome.domain.capability.Movement
import network.marsys.smarthome.domain.capability.Opened
import network.marsys.smarthome.domain.capability.Position
import network.marsys.smarthome.domain.unit.percent

sealed class Cover : Entity {
    abstract val orientation: Orientation
    abstract override val state: State

    sealed interface Orientation {
        data object Horizontal : Orientation
        data object Vertical : Orientation
    }

    sealed interface State : Entity.State {
        sealed interface Known : State, Entity.State.Known {
            val control: Control

            override fun find(type: Capability.Type<*>, context: Context): Capability<*, *>? {
                val movement = control.movement
                val position = control.position

                return when (type) {
                    Movement if movement is Capability.Available<Movement> ->
                        movement.value

                    Position if position is Capability.Present<Position> ->
                        position.value

                    Opened ->
                        control.opened.value

                    else -> null
                }
            }
        }
    }

    sealed interface Control {
        val movement: Capability.Optional<Movement>
        val opened: Capability.Present<Opened>
        val position: Capability.Constraint<Position>
            get() = Capability.Unsupported

        fun updateWith(capability: Capability<*, *>): Control

        data class WithPosition(
            override val position: Capability.Required<Position>,
            override val movement: Capability.Optional<Movement> = Capability.Unsupported,
        ) : Control {
            override val opened: Capability.Computed<Opened>
                get() = Capability.Computed(
                    value = Opened(current = position.value.current > 0.percent),
                )

            override fun updateWith(capability: Capability<*, *>): Control =
                when (capability) {
                    is Movement -> copy(movement = movement.updateWith(capability))
                    is Position -> copy(position = position.updateWith(capability))
                    else -> unsupportedCapabilityError(capability = capability)
                }
        }

        data class WithoutPosition(
            override val opened: Capability.Required<Opened>,
            override val movement: Capability.Optional<Movement> = Capability.Unsupported,
        ) : Control {
            override fun updateWith(capability: Capability<*, *>): Control =
                when (capability) {
                    is Movement -> copy(movement = movement.updateWith(capability))
                    is Opened -> copy(opened = opened.updateWith(capability))
                    else -> unsupportedCapabilityError(capability = capability)
                }
        }
    }
}
