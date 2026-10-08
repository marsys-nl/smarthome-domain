package network.marsys.smarthome.domain.identifiers

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class EntityIdentifier(val value: String) {
    init {
        require(RESERVED_NAMESPACES.none { value.startsWith(it) }) {
            "Entity identifier cannot start with a reserved namespace " +
                "(${RESERVED_NAMESPACES.first { value.startsWith(it) }})."
        }

        IdentifierRules.validate(value, "Entity identifier")
    }

    override fun toString(): String = value

    companion object {
        internal val RESERVED_NAMESPACES = listOf(
            IntegrationIdentifier.NAMESPACE,
        )
    }
}
