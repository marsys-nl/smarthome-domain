package network.marsys.smarthome.domain.zone

import network.marsys.smarthome.domain.TranslationKey

data class ZoneNaming(
    val key: TranslationKey,
    val qualifier: TranslationKey? = null,
)
