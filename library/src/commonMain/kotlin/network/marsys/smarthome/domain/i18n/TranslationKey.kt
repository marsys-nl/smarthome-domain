package network.marsys.smarthome.domain.i18n

import kotlinx.serialization.Serializable
import network.marsys.smarthome.domain.validation.LETTERS
import network.marsys.smarthome.domain.validation.Rules
import network.marsys.smarthome.domain.validation.SEPARATOR_CHARS
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class TranslationKey(val value: String) {
    init {
        Rules.validate(
            value = value,
            subject = "Translation key",
            rules = listOf(
                Rules.notBlank,
                Rules.startsWithLetter,
                Rules.endsWith(
                    allowed = LETTERS.toList(),
                    description = "a letter",
                ),
                Rules.containsOnly(
                    allowed = LETTERS + SEPARATOR_CHARS,
                    description = "letters, dashes, and dots",
                ),
                Rules.noConsecutiveCharacters(),
                Rules.maxLength(255),
                Rules.minLength(4),
            ),
        )
    }

    override fun toString(): String = value
}
