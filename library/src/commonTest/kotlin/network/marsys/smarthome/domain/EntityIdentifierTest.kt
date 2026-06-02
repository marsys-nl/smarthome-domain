package network.marsys.smarthome.domain

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isEqualTo

val entityIdentifierTest by testSuite(
    name = "Entity identifier tests",
) {
    test(name = "Initializing entity identifier with valid value succeeds") {
        expectThat(EntityIdentifier("test.entity"))
            .get(EntityIdentifier::value)
            .isEqualTo("test.entity")
    }

    test(name = "Initializing entity identifier with only letters succeeds") {
        expectThat(EntityIdentifier("testentity"))
            .get(EntityIdentifier::value)
            .isEqualTo("testentity")
    }

    test(name = "Initializing entity identifier with multiple separator characters succeeds") {
        expectThat(EntityIdentifier("test.entity.test"))
            .get(EntityIdentifier::value)
            .isEqualTo("test.entity.test")
    }

    test(name = "Initializing entity identifier with a long value succeeds") {
        expectThat(EntityIdentifier("a".repeat(255)))
            .get(EntityIdentifier::value)
            .get(String::length)
            .isEqualTo(255)
    }

    test(name = "Initializing entity identifier with empty value fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("") }
            .hasMessage("Entity identifier cannot be empty or blank.")
    }

    test(name = "Initializing entity identifier with only dashes fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("---") }
            .hasMessage("Entity identifier must contain at least one letter.")
    }

    test(name = "Initializing entity identifier with only dots fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("...") }
            .hasMessage("Entity identifier must contain at least one letter.")
    }

    test(name = "Initializing entity identifier starting with a dot fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier(".a") }
            .hasMessage("Entity identifier must start and end with a letter.")
    }

    test(name = "Initializing entity identifier ending with a dot fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("a.") }
            .hasMessage("Entity identifier must start and end with a letter.")
    }

    test(name = "Initializing entity identifier starting with a dot fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("-a") }
            .hasMessage("Entity identifier must start and end with a letter.")
    }

    test(name = "Initializing entity identifier ending with a dot fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("a-") }
            .hasMessage("Entity identifier must start and end with a letter.")
    }

    test(name = "Initializing entity identifier with leading whitespace fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier(" test.entity") }
            .hasMessage("Entity identifier must start and end with a letter.")
    }

    test(name = "Initializing entity identifier with trailing whitespace fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("test.entity ") }
            .hasMessage("Entity identifier must start and end with a letter.")
    }

    test(name = "Initializing entity identifier with non-ASCII letters fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("tëst.entiíy") }
            .hasMessage("Entity identifier can only contain letters, digits, dashes, and dots.")
    }

    test(name = "Initializing entity identifier with emoji symbol fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("test.\\uD83D\\uDE42.entity") }
            .hasMessage("Entity identifier can only contain letters, digits, dashes, and dots.")
    }

    testSuite(name = "Initialising entity identifier with invalid chars fails") {
        listOf("test entity", "test_entity", "test@entity", "test#entity").forEach {
            test(name = "Identifier with value '$it'") {
                expectThrows<IllegalArgumentException> { EntityIdentifier(it) }
                    .hasMessage("Entity identifier can only contain letters, digits, dashes, and dots.")
            }
        }
    }

    testSuite(name = "Initializing entity identifier with too few characters fails") {
        (1..4).forEach {
            test(name = "Value with $it characters") {
                expectThrows<IllegalArgumentException> { EntityIdentifier("a".repeat(it)) }
                    .hasMessage("Entity identifier must contain at least 5 allowed characters.")
            }
        }
    }

    test(name = "Initialising entity identifier with consecutive dots fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("test..entity") }
            .hasMessage("Entity identifier should not contain consecutive separator characters (dots or dashes).")
    }

    test(name = "Initialising entity identifier with a very long value fails") {
        expectThrows<IllegalArgumentException> { EntityIdentifier("a".repeat(1000)) }
            .hasMessage("Entity identifier cannot be longer than 255 characters.")
    }
}
