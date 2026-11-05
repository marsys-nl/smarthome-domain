package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isTrue
import network.marsys.smarthome.domain.entity.EntityIdentifier

val OnOffTest by testSuite(
    name = "OnOff capability tests",
) {
    test(name = "Updating the on-off capability preserves the context") {
        val capability = OnOff(current = true, context = Example)

        expectThat(capability.updateWith(value = false))
            .get(OnOff::context)
            .isA<Example>()
    }

    test(name = "Updating the on-off capability mutates the value") {
        val capability = OnOff(current = true, context = Example)
        val updated = capability.updateWith(value = false)

        expectThat(updated)
            .get(OnOff::current)
            .isFalse()
    }

    test(name = "Updating the on-off capability does not mutate the original capability") {
        val capability = OnOff(current = true, context = Example)
        capability.updateWith(value = false)

        expectThat(capability)
            .get(OnOff::current)
            .isTrue()
    }

    test(name = "On-off capability can be created with default context") {
        expectThat(OnOff(current = true))
            .get(OnOff::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "On-off capability can be associated with a context") {
        expectThat(OnOff(current = true) with Example)
            .get(OnOff::context)
            .isA<Example>()
    }

    test(name = "On-off capability is of the correct type") {
        expectThat(OnOff(current = true))
            .get(OnOff::type)
            .isEqualTo(OnOff)
    }

    test(name = "On-off capability on entity action is able to be created") {
        val action = OnOff.Toggle.On(
            identifier = EntityIdentifier("light.test"),
        )

        expectThat(action)
            .isA<OnOff.Toggle>()
    }

    test(name = "On-off capability off entity action is able to be created") {
        val action = OnOff.Toggle.Off(
            identifier = EntityIdentifier("light.test"),
        )

        expectThat(action)
            .isA<OnOff.Toggle>()
    }
}
