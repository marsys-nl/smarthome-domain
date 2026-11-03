package network.marsys.smarthome.domain.zone

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isEqualTo

val ZoneIdentifierTest by testSuite(
    name = "Zone identifier tests",
) {
    test(name = "Initializing zone identifier with namespace only fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone") }
            .hasMessage("Zone identifier must start with 'zone.'.")
    }

    test(name = "Initializing zone identifier with namespace and dot only fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone.") }
            .hasMessage("Zone identifier must contain a namespace and a name.")
    }

    test(name = "Initializing zone identifier with no namespace fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone-name") }
            .hasMessage("Zone identifier must start with 'zone.'.")
    }

    test(name = "Initializing zone identifier with wrong namespace fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("test.zone-name") }
            .hasMessage("Zone identifier must start with 'zone.'.")
    }

    test(name = "Initializing zone identifier with namespace and valid name succeeds") {
        expectThat(ZoneIdentifier("zone.living-room"))
            .get(ZoneIdentifier::value)
            .isEqualTo("zone.living-room")
    }

    test(name = "Initializing zone identifier with multiple separator characters succeeds") {
        expectThat(ZoneIdentifier("zone.living-room.test"))
            .get(ZoneIdentifier::value)
            .isEqualTo("zone.living-room.test")
    }

    test(name = "Initializing zone identifier with a long value succeeds") {
        expectThat(ZoneIdentifier("zone." + "a".repeat(255 - "zone.".length)))
            .get(ZoneIdentifier::value)
            .get(String::length)
            .isEqualTo(255)
    }

    test(name = "Initializing zone identifier with only dashes fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone.---") }
            .hasMessage("Zone identifier must contain at least one letter.")
    }

    test(name = "Initializing zone identifier with only dots fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone....") }
            .hasMessage("Zone identifier must contain at least one letter.")
    }

    test(name = "Initializing zone identifier starting with a dot fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone..a") }
            .hasMessage("Zone identifier must start with a letter.")
    }

    test(name = "Initializing zone identifier ending with a dot fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone.a.") }
            .hasMessage("Zone identifier must end with a letter or digit.")
    }

    test(name = "Initializing zone identifier starting with a dash fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone.-a") }
            .hasMessage("Zone identifier must start with a letter.")
    }

    test(name = "Initializing zone identifier ending with a dash fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone.a-") }
            .hasMessage("Zone identifier must end with a letter or digit.")
    }

    test(name = "Initializing zone identifier with leading whitespace fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone. test.entity") }
            .hasMessage("Zone identifier must start with a letter.")
    }

    test(name = "Initializing zone identifier with trailing whitespace fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone.test.entity ") }
            .hasMessage("Zone identifier must end with a letter or digit.")
    }

    test(name = "Initializing zone identifier with non-ASCII letters fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone.tëst.entiíy") }
            .hasMessage("Zone identifier can only contain letters, digits, dashes, and dots.")
    }

    test(name = "Initializing zone identifier with emoji symbol fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone.test.\\uD83D\\uDE42.entity") }
            .hasMessage("Zone identifier can only contain letters, digits, dashes, and dots.")
    }

    testSuite(name = "Initialising zone identifier with invalid chars fails") {
        listOf(
            "zone.test zone",
            "zone.test_zone",
            "zone.test@zone",
            "zone.test#zone",
        ).forEach {
            test(name = "Identifier with value '$it'") {
                expectThrows<IllegalArgumentException> { ZoneIdentifier(it) }
                    .hasMessage("Zone identifier can only contain letters, digits, dashes, and dots.")
            }
        }
    }

    testSuite(name = "Initializing zone identifier with too few characters fails") {
        (1..3).forEach {
            test(name = "Value with $it characters") {
                expectThrows<IllegalArgumentException> { ZoneIdentifier("zone." + "a".repeat(it)) }
                    .hasMessage("Zone identifier must contain at least 4 allowed characters.")
            }
        }
    }

    test(name = "Initialising zone identifier with consecutive dots fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone.system..test") }
            .hasMessage("Zone identifier should not contain consecutive separator characters (dots or dashes).")
    }

    test(name = "Initialising zone identifier with a very long value fails") {
        expectThrows<IllegalArgumentException> { ZoneIdentifier("zone." + "a".repeat(1000)) }
            .hasMessage("Zone identifier cannot be longer than 255 characters.")
    }

    test(name = "Casting zone identifier to a string actually outputs the identifier as-is") {
        expectThat(ZoneIdentifier("zone.living-room").toString())
            .isEqualTo("zone.living-room")
    }
}
