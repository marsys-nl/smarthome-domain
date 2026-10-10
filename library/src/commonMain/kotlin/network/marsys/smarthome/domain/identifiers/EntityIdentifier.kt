package network.marsys.smarthome.domain.identifiers

import kotlinx.serialization.Serializable
import network.marsys.smarthome.domain.validation.RESERVED_NAMESPACES
import network.marsys.smarthome.domain.validation.Rules
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class EntityIdentifier(val value: String) : Identifier {
    init {
        Rules.validate(
            value = value,
            subject = "Entity identifier",
            rules = listOf(
                Rules.notStartWithPrefix(
                    prefixes = RESERVED_NAMESPACES,
                ),
            ) + Identifier.rules,
        )
    }

    override fun toString(): String = value
}
