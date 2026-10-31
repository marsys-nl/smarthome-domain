package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import network.marsys.smarthome.domain.unit.percent

val BrightnessTest by testSuite(
    name = "Brightness capability tests",
) {
    test(name = "Updating the brightness capability preserves the context") {
        val capability = Brightness(current = 50.percent, context = Example)

        expectThat(capability.updateWith(value = 25.percent))
            .get(Brightness::context)
            .isA<Example>()
    }

    test(name = "Updating the brightness capability mutates the value") {
        val capability = Brightness(current = 50.percent, context = Example)
        val updated = capability.updateWith(value = 25.percent)

        expectThat(updated)
            .get(Brightness::current)
            .isEqualTo(25.percent)
    }

    test(name = "Updating the brightness capability does not mutate the original capability") {
        val capability = Brightness(current = 50.percent, context = Example)
        capability.updateWith(value = 25.percent)

        expectThat(capability)
            .get(Brightness::current)
            .isEqualTo(50.percent)
    }

    test(name = "Brightness capability can be created with default context") {
        expectThat(Brightness(current = 50.percent))
            .get(Brightness::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Brightness capability can be associated with a context") {
        expectThat(Brightness(current = 50.percent) with Example)
            .get(Brightness::context)
            .isA<Example>()
    }

    test(name = "Brightness capability is of the correct type") {
        expectThat(Brightness(current = 50.percent))
            .get(Brightness::type)
            .isEqualTo(Brightness)
    }
}
