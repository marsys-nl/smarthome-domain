package network.marsys.smarthome.domain.capability

data class OnOff(
    override val current: Boolean,
    override val context: Context = Context.Empty,
) : Capability<Boolean, OnOff>() {
    override fun updateWith(value: Boolean, context: Context) =
        copy(
            current = value,
            context = context,
        )
}
