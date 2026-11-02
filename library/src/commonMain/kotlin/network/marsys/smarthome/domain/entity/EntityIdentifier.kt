package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.identifiers.Identifier
import network.marsys.smarthome.domain.validation.RESERVED_NAMESPACES
import network.marsys.smarthome.domain.validation.Rules
import kotlin.jvm.JvmInline

@JvmInline
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
