package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.i18n.TranslationKey

data class EntityNaming(
    val key: TranslationKey,
    val qualifier: TranslationKey? = null,
)
