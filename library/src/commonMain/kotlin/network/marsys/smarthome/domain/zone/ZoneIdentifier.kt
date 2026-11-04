package network.marsys.smarthome.domain.zone

import network.marsys.smarthome.domain.identifiers.Identifier
import network.marsys.smarthome.domain.identifiers.IntegrationIdentifier
import network.marsys.smarthome.domain.validation.RESERVED_NAMESPACES
import network.marsys.smarthome.domain.validation.Rules
import kotlin.jvm.JvmInline

@JvmInline
value class ZoneIdentifier(val value: String) : Identifier {
    init {
        Rules.validate(
            value = value,
            subject = "Zone identifier",
            rules = listOf(
                Rules.startWithPrefix(PREFIX),
                Rules.minLength(
                    min = PREFIX.length + 1,
                    description = "contain a namespace and a name",
                ),
            ),
        )

        Rules.validate(
            value = namespace,
            subject = "Zone identifier",
            rules = Identifier.rules,
        )
    }

    val namespace: String
        get() = value
            .removePrefix(PREFIX)

    override fun toString(): String = value

    companion object {
        internal const val PREFIX = "zone."
    }
}
