package network.marsys.smarthome.domain.identifiers

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class EntityIdentifier(val value: String) {
    init {
        IdentifierRules.validate(value, "Entity identifier")
    }

    override fun toString(): String = value
}
