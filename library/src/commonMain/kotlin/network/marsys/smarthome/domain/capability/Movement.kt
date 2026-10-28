package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.capability.Movement.Direction

data class Movement(
    override val current: Direction,
    override val context: Context = Context.Empty,
) : Capability<Direction, Movement>() {
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
}
