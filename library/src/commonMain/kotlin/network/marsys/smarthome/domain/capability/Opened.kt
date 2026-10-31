package network.marsys.smarthome.domain.capability

data class Opened(
    override val current: Boolean,
    override val context: Context = Context.Empty,
) : Capability<Boolean, Opened>() {
    override val type: Type<Opened>
        get() = Opened

    override fun updateWith(value: Boolean, context: Context) =
        copy(
            current = value,
            context = context,
        )

    companion object : Type<Opened>
}
