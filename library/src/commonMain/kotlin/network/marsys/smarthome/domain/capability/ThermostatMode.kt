package network.marsys.smarthome.domain.capability

data class ThermostatMode(
    override val current: Mode,
    override val context: Context = Context.Empty,
    private val supported: Set<Mode> = Mode.entries.toSet(),
) : Capability<ThermostatMode.Mode, ThermostatMode>() {
    init {
        require(current in supported) {
            "Current mode '$current' is not supported. Supported modes are: $supported."
        }
    }

    fun supports(mode: Mode): Boolean = when (mode) {
        in supported -> true
        Mode.Heat if Mode.Auto in supported -> true
        Mode.Cool if Mode.Auto in supported -> true
        else -> false
    }

    override fun updateWith(value: Mode, context: Context) =
        copy(
            current = value,
            context = context,
        )

    enum class Mode {
        Heat,
        Cool,
        Auto,
    }
}
