package network.marsys.smarthome.domain.identifiers

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isEqualTo

val integrationIdentifierTest by testSuite(
    name = "Integration identifier tests",
) {
    test(name = "Initializing integration identifier with valid value succeeds") {
        expectThat(IntegrationIdentifier("test.integration"))
            .get(IntegrationIdentifier::value)
            .isEqualTo("test.integration")
    }

    test(name = "Initializing integration identifier with only letters succeeds") {
        expectThat(IntegrationIdentifier("testintegration"))
            .get(IntegrationIdentifier::value)
            .isEqualTo("testintegration")
    }

    test(name = "Initializing integration identifier with multiple separator characters succeeds") {
        expectThat(IntegrationIdentifier("test.integration.test"))
            .get(IntegrationIdentifier::value)
            .isEqualTo("test.integration.test")
    }

    test(name = "Initializing integration identifier with a long value succeeds") {
        expectThat(IntegrationIdentifier("a".repeat(255)))
            .get(IntegrationIdentifier::value)
            .get(String::length)
            .isEqualTo(255)
    }

    test(name = "Initializing integration identifier with empty value fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("") }
            .hasMessage("Integration identifier cannot be empty or blank.")
    }

    test(name = "Initializing integration identifier with only dashes fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("---") }
            .hasMessage("Integration identifier must contain at least one letter.")
    }

    test(name = "Initializing integration identifier with only dots fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("...") }
            .hasMessage("Integration identifier must contain at least one letter.")
    }

    test(name = "Initializing integration identifier starting with a dot fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier(".a") }
            .hasMessage("Integration identifier must start and end with a letter.")
    }

    test(name = "Initializing integration identifier ending with a dot fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("a.") }
            .hasMessage("Integration identifier must start and end with a letter.")
    }

    test(name = "Initializing integration identifier starting with a dash fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("-a") }
            .hasMessage("Integration identifier must start and end with a letter.")
    }

    test(name = "Initializing integration identifier ending with a dash fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("a-") }
            .hasMessage("Integration identifier must start and end with a letter.")
    }

    test(name = "Initializing integration identifier with leading whitespace fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier(" test.entity") }
            .hasMessage("Integration identifier must start and end with a letter.")
    }

    test(name = "Initializing integration identifier with trailing whitespace fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("test.entity ") }
            .hasMessage("Integration identifier must start and end with a letter.")
    }

    test(name = "Initializing integration identifier with non-ASCII letters fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("tëst.entiíy") }
            .hasMessage("Integration identifier can only contain letters, digits, dashes, and dots.")
    }

    test(name = "Initializing integration identifier with emoji symbol fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("test.\\uD83D\\uDE42.entity") }
            .hasMessage("Integration identifier can only contain letters, digits, dashes, and dots.")
    }

    testSuite(name = "Initialising integration identifier with invalid chars fails") {
        listOf("test integration", "test_integration", "test@integration", "test#integration").forEach {
            test(name = "Identifier with value '$it'") {
                expectThrows<IllegalArgumentException> { IntegrationIdentifier(it) }
                    .hasMessage("Integration identifier can only contain letters, digits, dashes, and dots.")
            }
        }
    }

    testSuite(name = "Initializing integration identifier with too few characters fails") {
        (1..4).forEach {
            test(name = "Value with $it characters") {
                expectThrows<IllegalArgumentException> { IntegrationIdentifier("a".repeat(it)) }
                    .hasMessage("Integration identifier must contain at least 5 allowed characters.")
            }
        }
    }

    test(name = "Initialising integration identifier with consecutive dots fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("test..integration") }
            .hasMessage("Integration identifier should not contain consecutive separator characters (dots or dashes).")
    }

    test(name = "Initialising integration identifier with a very long value fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("a".repeat(1000)) }
            .hasMessage("Integration identifier cannot be longer than 255 characters.")
    }

    test(name = "Casting integration identifier to a string actually outputs the identifier as-is") {
        expectThat(IntegrationIdentifier("test.integration").toString())
            .isEqualTo("test.integration")
    }
}
