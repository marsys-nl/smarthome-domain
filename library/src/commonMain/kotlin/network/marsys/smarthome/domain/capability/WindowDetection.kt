package network.marsys.smarthome.domain.capability

data class WindowDetection(
    override val current: Boolean,
    override val context: Context = Context.Empty,
) : Capability<Boolean, WindowDetection>() {
    override fun updateWith(value: Boolean, context: Context) =
        copy(
            current = value,
            context = context,
        )
}
