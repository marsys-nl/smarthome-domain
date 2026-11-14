package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.capability.Movement.Direction
import network.marsys.smarthome.domain.entity.Entity
import network.marsys.smarthome.domain.entity.EntityIdentifier

data class Movement(
    override val current: Direction,
    override val context: Context = Context.Empty,
) : Capability<Direction, Movement>() {
    override val type: Type<Movement>
        get() = Movement

    override fun updateWith(value: Direction, context: Context) =
        copy(
            current = value,
            context = context,
        )

    enum class Direction {
        Idle,
        Opening,
        Closing,
    }

    sealed interface Move : Action {
        data class Open(
            override val entity: EntityIdentifier,
            override val context: Context = Context.Empty,
        ) : Move

        data class Close(
            override val entity: EntityIdentifier,
            override val context: Context = Context.Empty,
        ) : Move

        data class Stop(
            override val entity: EntityIdentifier,
            override val context: Context = Context.Empty,
        ) : Move
    }

    companion object : Type<Movement>
}
