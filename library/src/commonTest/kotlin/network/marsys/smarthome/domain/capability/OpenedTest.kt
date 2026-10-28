package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isTrue

val OpenedTest by testSuite(
    name = "Opened capability tests",
) {
    test(name = "Updating the opened capability preserves the context") {
        val capability = Opened(current = true, context = Example)

        expectThat(capability.updateWith(value = false))
            .get(Opened::context)
            .isA<Example>()
    }

    test(name = "Updating the opened capability mutates the value") {
        val capability = Opened(current = true, context = Example)
        val updated = capability.updateWith(value = false)

        expectThat(updated)
            .get(Opened::current)
            .isFalse()
    }

    test(name = "Updating the opened capability does not mutate the original capability") {
        val capability = Opened(current = true, context = Example)
        capability.updateWith(value = false)

        expectThat(capability)
            .get(Opened::current)
            .isTrue()
    }

    test(name = "Opened capability can be created with default context") {
        expectThat(Opened(current = true))
            .get(Opened::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Opened capability can be associated with a context") {
        expectThat(Opened(current = true) with Example)
            .get(Opened::context)
            .isA<Example>()
    }
}
