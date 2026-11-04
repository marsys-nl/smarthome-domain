package network.marsys.smarthome.domain.zone

import network.marsys.smarthome.domain.TranslationKey
import network.marsys.smarthome.domain.entity.Entity
import network.marsys.smarthome.domain.entity.EntityNaming
import network.marsys.smarthome.domain.entity.createTranslationKey

data class Zone(
    val identifier: ZoneIdentifier,
    val name: ZoneNaming,
)

fun Zone.entity(
    type: Entity.Type<*>,
    qualifier: TranslationKey? = null,
): EntityNaming = EntityNaming(
    key = TranslationKey(value = createTranslationKey(type.defaultNamingKey, this)),
    qualifier = qualifier,
)
