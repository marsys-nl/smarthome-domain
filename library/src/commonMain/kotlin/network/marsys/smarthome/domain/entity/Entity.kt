package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.TranslationKey
import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.Capability.Context
import network.marsys.smarthome.domain.zone.Zone
import network.marsys.smarthome.domain.zone.ZoneIdentifier
import kotlin.reflect.KClass

sealed interface Entity {
    val identifier: EntityIdentifier
    val naming: EntityNaming
    val state: State
    val type: Type<*>

    val zone: ZoneIdentifier?
        get() = null

    sealed interface State {
        fun find(type: Capability.Type<*>, context: Context = Context.Empty): Capability<*, *>?
        fun updateWith(capability: Capability<*, *>): State

        sealed interface Known : State

        sealed interface Unknown : State {
            override fun find(type: Capability.Type<*>, context: Context): Capability<*, *>? = null
            override fun updateWith(capability: Capability<*, *>): State = this
        }

        @Suppress("UNCHECKED_CAST")
        fun <C : Capability<*, *>> get(type: Capability.Type<C>, context: Context = Context.Empty): C? =
            find(
                type = type,
                context = context,
            ) as? C
    }

    /*
     * Traits
     */

    interface Activatable {
        val active: Boolean
    }

    /*
     * Entity type
     */

    sealed interface Type<E : Entity> {
        val defaultNamingKey: String
    }
}

fun Entity.Type<*>.naming(
    key: TranslationKey? = null,
    qualifier: TranslationKey? = null,
    zone: Zone? = null,
): EntityNaming = EntityNaming(
    key = key ?: TranslationKey(value = createTranslationKey(defaultNamingKey, zone)),
    qualifier = qualifier,
)

internal fun createTranslationKey(defaultNamingKey: String, zone: Zone?): String =
    when {
        zone is Zone -> "${defaultNamingKey}.${zone.name.key}"
        else -> defaultNamingKey
    }

val KClass<*>.formattedName: String get() = this.qualifiedName!!
    .replace("network.marsys.smarthome.domain.entity", "")
    .replace("network.marsys.smarthome.domain.capability", "")
    .trimStart('.')

internal fun Entity.State.unsupportedCapabilityError(capability: Capability<*, *>): Nothing =
    error("Unsupported '${capability::class.formattedName}' capability provided for '${this::class.formattedName}'")

internal fun Cover.Control.unsupportedCapabilityError(capability: Capability<*, *>): Nothing =
    error("Unsupported '${capability::class.formattedName}' capability provided for '${this::class.formattedName}'")
