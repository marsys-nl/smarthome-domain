package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isTrue

val ScheduledModeTest by testSuite(
    name = "Scheduled mode capability tests",
) {
    test(name = "Updating the scheduled mode capability preserves the context") {
        val capability = ScheduledMode(current = true, context = Example)

        expectThat(capability.updateWith(value = false))
            .get(ScheduledMode::context)
            .isA<Example>()
    }

    test(name = "Updating the scheduled mode capability mutates the value") {
        val capability = ScheduledMode(current = true, context = Example)
        val updated = capability.updateWith(value = false)

        expectThat(updated)
            .get(ScheduledMode::current)
            .isFalse()
    }

    test(name = "Updating the scheduled mode capability does not mutate the original capability") {
        val capability = ScheduledMode(current = true, context = Example)
        capability.updateWith(value = false)

        expectThat(capability)
            .get(ScheduledMode::current)
            .isTrue()
    }

    test(name = "Scheduled mode capability can be created with default context") {
        expectThat(ScheduledMode(current = true))
            .get(ScheduledMode::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Scheduled mode capability can be associated with a context") {
        expectThat(ScheduledMode(current = true) with Example)
            .get(ScheduledMode::context)
            .isA<Example>()
    }
}
