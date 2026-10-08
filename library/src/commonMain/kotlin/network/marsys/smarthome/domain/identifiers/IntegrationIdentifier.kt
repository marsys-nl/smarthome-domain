package network.marsys.smarthome.domain.identifiers

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class IntegrationIdentifier(val value: String) {
    init {
        require(value.startsWith(NAMESPACE)) {
            "Integration identifier must start with the namespace '$NAMESPACE'."
        }

        require(value.length > NAMESPACE.length) {
            "Integration identifier must contain a namespace and a name."
        }

        IdentifierRules.validate(namespace, "Integration identifier")
    }

    val namespace: String
        get() = value
            .removePrefix(NAMESPACE)

    override fun toString(): String = value

    companion object {
        internal const val NAMESPACE = "integration."
    }
}
