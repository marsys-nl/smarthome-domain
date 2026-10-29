package network.marsys.smarthome.domain.capability

data class OnOff(
    override val current: Boolean,
    override val context: Context = Context.Empty,
) : Capability<Boolean, OnOff>() {
    override val type: Type<OnOff>
        get() = OnOff

    override fun updateWith(value: Boolean, context: Context) =
        copy(
            current = value,
            context = context,
        )

    companion object : Type<OnOff>
}
