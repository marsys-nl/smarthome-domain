package network.marsys.smarthome.domain.capability

data class ChildLock(
    override val current: Boolean,
    override val context: Context = Context.Empty,
) : Capability<Boolean, ChildLock>() {
    override val type: Type<ChildLock>
        get() = ChildLock

    override fun updateWith(value: Boolean, context: Context) =
        copy(
            current = value,
            context = context,
        )

    companion object : Type<ChildLock>
}
