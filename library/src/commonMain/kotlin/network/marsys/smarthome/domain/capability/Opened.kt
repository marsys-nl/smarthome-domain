package network.marsys.smarthome.domain.capability

data class Opened(
    override val current: Boolean,
    override val context: Context = Context.Empty,
) : Capability<Boolean, Opened>() {
    override fun updateWith(value: Boolean, context: Context) =
        copy(
            current = value,
            context = context,
        )
}
