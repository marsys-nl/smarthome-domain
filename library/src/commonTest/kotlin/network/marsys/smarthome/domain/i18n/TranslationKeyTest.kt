package network.marsys.smarthome.domain.i18n

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isEqualTo
import kotlin.getValue

val translationKeyTest by testSuite(
    name = "Translation key tests",
) {
    test(name = "Initializing translation key with valid value succeeds") {
        expectThat(TranslationKey("kitchen.lamp"))
            .get(TranslationKey::value)
            .isEqualTo("kitchen.lamp")
    }

    test(name = "Initializing translation key with multiple separator characters succeeds") {
        expectThat(TranslationKey("kitchen.lamp.ceiling-light"))
            .get(TranslationKey::value)
            .isEqualTo("kitchen.lamp.ceiling-light")
    }

    test(name = "Initializing translation key with a long value succeeds") {
        expectThat(TranslationKey("a".repeat(255)))
            .get(TranslationKey::value)
            .get(String::length)
            .isEqualTo(255)
    }

    test(name = "Initializing translation key with only dashes fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("---") }
            .hasMessage("Translation key must start with a letter.")
    }

    test(name = "Initializing translation key with only dots fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("...") }
            .hasMessage("Translation key must start with a letter.")
    }

    test(name = "Initializing translation key starting with a dot fails") {
        expectThrows<IllegalArgumentException> { TranslationKey(".a") }
            .hasMessage("Translation key must start with a letter.")
    }

    test(name = "Initializing translation key ending with a dot fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("a.") }
            .hasMessage("Translation key must end with a letter.")
    }

    test(name = "Initializing translation key starting with a dash fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("-a") }
            .hasMessage("Translation key must start with a letter.")
    }

    test(name = "Initializing translation key ending with a dash fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("a-") }
            .hasMessage("Translation key must end with a letter.")
    }

    test(name = "Initializing translation key with leading whitespace fails") {
        expectThrows<IllegalArgumentException> { TranslationKey(" test.entity") }
            .hasMessage("Translation key must start with a letter.")
    }

    test(name = "Initializing translation key with trailing whitespace fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("test.entity ") }
            .hasMessage("Translation key must end with a letter.")
    }

    test(name = "Initializing translation key with non-ASCII letters fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("tëst.entiíy") }
            .hasMessage("Translation key can only contain letters, dashes, and dots.")
    }

    test(name = "Initializing translation key with emoji symbol fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("test.\\uD83D\\uDE42.entity") }
            .hasMessage("Translation key can only contain letters, dashes, and dots.")
    }

    testSuite(name = "Initialising translation key with invalid chars fails") {
        listOf(
            "test integration",
            "test_integration",
            "test@integration",
            "test#integration",
        ).forEach {
            test(name = "Identifier with value '$it'") {
                expectThrows<IllegalArgumentException> { TranslationKey(it) }
                    .hasMessage("Translation key can only contain letters, dashes, and dots.")
            }
        }
    }

    testSuite(name = "Initializing translation key with too few characters fails") {
        (1..3).forEach {
            test(name = "Value with $it characters") {
                expectThrows<IllegalArgumentException> { TranslationKey("a".repeat(it)) }
                    .hasMessage("Translation key must be at least 4 characters long.")
            }
        }
    }

    test(name = "Initialising translation key with consecutive dots fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("system..test") }
            .hasMessage("Translation key should not contain consecutive separator characters (dots or dashes).")
    }

    test(name = "Initialising translation key with a very long value fails") {
        expectThrows<IllegalArgumentException> { TranslationKey("a".repeat(1000)) }
            .hasMessage("Translation key cannot be longer than 255 characters.")
    }

    test(name = "Casting translation key to a string actually outputs the identifier as-is") {
        expectThat(TranslationKey("system").toString())
            .isEqualTo("system")
    }
}
