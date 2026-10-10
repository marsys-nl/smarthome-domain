package network.marsys.smarthome.domain.identifiers

import kotlinx.serialization.Serializable
import network.marsys.smarthome.domain.validation.Rules
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class IntegrationIdentifier(val value: String) : Identifier {
    init {
        Rules.validate(
            value = value,
            subject = "Integration identifier",
            rules = listOf(
                Rules.startWithPrefix(PREFIX),
                Rules.minLength(
                    min = PREFIX.length,
                    description = "contain a namespace and a name",
                ),
            ),
        )

        Rules.validate(
            value = namespace,
            subject = "Integration identifier",
            rules = Identifier.rules,
        )
    }

    val namespace: String
        get() = value
            .removePrefix(PREFIX)

    override fun toString(): String = value

    companion object {
        internal const val PREFIX = "integration."
    }
}
