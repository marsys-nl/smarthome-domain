package network.marsys.smarthome.domain.identifiers

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class IntegrationIdentifier(val value: String) {
    init {
        IdentifierRules.validate(value, "Integration identifier")
    }

    override fun toString(): String = value
}
