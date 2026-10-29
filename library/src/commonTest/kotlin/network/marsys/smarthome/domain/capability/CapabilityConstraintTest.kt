package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isFalse
import network.marsys.smarthome.domain.capability.Capability.Companion.optional
import network.marsys.smarthome.domain.capability.Capability.Companion.required

val CapabilityConstraintTests by testSuite(
    name = "Capability constraint tests",
) {
    test(name = "Updating a required constraint keeps the constraint type as required") {
        val initial = Capability.Required(OnOff(current = true))

        expectThat(initial.updateWith(OnOff(current = false)))
            .isA<Capability.Required<OnOff>>()
    }

    test(name = "Required constraint updates with a new capability") {
        val initial = Capability.Required(OnOff(current = true))

        expectThat(initial.updateWith(OnOff(current = false)))
            .isA<Capability.Present<OnOff>>()
            .get(Capability.Present<OnOff>::value)
            .get(OnOff::current)
            .isFalse()
    }

    test(name = "Updating an optional constraint when there is a value keeps the constraint type as available") {
        val initial: Capability.Optional<OnOff> = Capability.Available(OnOff(current = true))

        expectThat(initial.updateWith(OnOff(current = false)))
            .isA<Capability.Available<OnOff>>()
    }

    test(name = "Required optional updates with a new capability") {
        val initial: Capability.Optional<OnOff> = Capability.Available(OnOff(current = true))

        expectThat(initial.updateWith(OnOff(current = false)))
            .isA<Capability.Present<OnOff>>()
            .get(Capability.Present<OnOff>::value)
            .get(OnOff::current)
            .isFalse()
    }

    test(name = "Updating an optional constraint without value keeps the constraint type as unsupported") {
        val initial: Capability.Optional<OnOff> = Capability.Unsupported

        expectThat(initial.updateWith(OnOff(current = false)))
            .isA<Capability.Unsupported>()
    }

    test(name = "Creating a required capability results in a required capability constraint") {
        expectThat(required(OnOff(current = true)))
            .isA<Capability.Required<OnOff>>()
    }

    test(name = "Creating an optional capability providing a capability results in an available capability constraint") {
        expectThat(optional(OnOff(current = true)))
            .isA<Capability.Optional<OnOff>>()
            .isA<Capability.Available<OnOff>>()
    }

    test(name = "Creating an optional capability providing a null capability results in an unsupported capability constraint") {
        expectThat(optional(null))
            .isA<Capability.Optional<*>>()
            .isA<Capability.Unsupported>()
    }
}
