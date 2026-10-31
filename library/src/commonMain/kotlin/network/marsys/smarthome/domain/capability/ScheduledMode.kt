package network.marsys.smarthome.domain.capability

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

    companion object : Type<ScheduledMode>
}
