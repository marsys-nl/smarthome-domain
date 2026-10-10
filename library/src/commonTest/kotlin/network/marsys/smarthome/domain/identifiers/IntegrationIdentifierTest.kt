package network.marsys.smarthome.domain.identifiers

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isEqualTo

val integrationIdentifierTest by testSuite(
    name = "Integration identifier tests",
) {
    test(name = "Initializing integration identifier with namespace only fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration") }
            .hasMessage("Integration identifier must start with 'integration.'.")
    }

    test(name = "Initializing integration identifier with namespace and dot only fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration.") }
            .hasMessage("Integration identifier must contain a namespace and a name.")
    }

    test(name = "Initializing integration identifier with no namespace fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration-name") }
            .hasMessage("Integration identifier must start with 'integration.'.")
    }

    test(name = "Initializing integration identifier with wrong namespace fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("test.integration-name") }
            .hasMessage("Integration identifier must start with 'integration.'.")
    }

    test(name = "Initializing integration identifier with namespace and valid name succeeds") {
        expectThat(IntegrationIdentifier("integration.system"))
            .get(IntegrationIdentifier::value)
            .isEqualTo("integration.system")
    }

    test(name = "Initializing integration identifier with multiple separator characters succeeds") {
        expectThat(IntegrationIdentifier("integration.system.test"))
            .get(IntegrationIdentifier::value)
            .isEqualTo("integration.system.test")
    }

    test(name = "Initializing integration identifier with a long value succeeds") {
        expectThat(IntegrationIdentifier("integration." + "a".repeat(255 - "integration.".length)))
            .get(IntegrationIdentifier::value)
            .get(String::length)
            .isEqualTo(255)
    }

    test(name = "Initializing integration identifier with only dashes fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration.---") }
            .hasMessage("Integration identifier must contain at least one letter.")
    }

    test(name = "Initializing integration identifier with only dots fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration....") }
            .hasMessage("Integration identifier must contain at least one letter.")
    }

    test(name = "Initializing integration identifier starting with a dot fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration..a") }
            .hasMessage("Integration identifier must start with a letter.")
    }

    test(name = "Initializing integration identifier ending with a dot fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration.a.") }
            .hasMessage("Integration identifier must end with a letter or digit.")
    }

    test(name = "Initializing integration identifier starting with a dash fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration.-a") }
            .hasMessage("Integration identifier must start with a letter.")
    }

    test(name = "Initializing integration identifier ending with a dash fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration.a-") }
            .hasMessage("Integration identifier must end with a letter or digit.")
    }

    test(name = "Initializing integration identifier with leading whitespace fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration. test.entity") }
            .hasMessage("Integration identifier must start with a letter.")
    }

    test(name = "Initializing integration identifier with trailing whitespace fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration.test.entity ") }
            .hasMessage("Integration identifier must end with a letter or digit.")
    }

    test(name = "Initializing integration identifier with non-ASCII letters fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration.tëst.entiíy") }
            .hasMessage("Integration identifier can only contain letters, digits, dashes, and dots.")
    }

    test(name = "Initializing integration identifier with emoji symbol fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration.test.\\uD83D\\uDE42.entity") }
            .hasMessage("Integration identifier can only contain letters, digits, dashes, and dots.")
    }

    testSuite(name = "Initialising integration identifier with invalid chars fails") {
        listOf(
            "integration.test integration",
            "integration.test_integration",
            "integration.test@integration",
            "integration.test#integration",
        ).forEach {
            test(name = "Identifier with value '$it'") {
                expectThrows<IllegalArgumentException> { IntegrationIdentifier(it) }
                    .hasMessage("Integration identifier can only contain letters, digits, dashes, and dots.")
            }
        }
    }

    testSuite(name = "Initializing integration identifier with too few characters fails") {
        (1..3).forEach {
            test(name = "Value with $it characters") {
                expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration." + "a".repeat(it)) }
                    .hasMessage("Integration identifier must contain at least 4 allowed characters.")
            }
        }
    }

    test(name = "Initialising integration identifier with consecutive dots fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration.system..test") }
            .hasMessage("Integration identifier should not contain consecutive separator characters (dots or dashes).")
    }

    test(name = "Initialising integration identifier with a very long value fails") {
        expectThrows<IllegalArgumentException> { IntegrationIdentifier("integration." + "a".repeat(1000)) }
            .hasMessage("Integration identifier cannot be longer than 255 characters.")
    }

    test(name = "Casting integration identifier to a string actually outputs the identifier as-is") {
        expectThat(IntegrationIdentifier("integration.system").toString())
            .isEqualTo("integration.system")
    }
}
