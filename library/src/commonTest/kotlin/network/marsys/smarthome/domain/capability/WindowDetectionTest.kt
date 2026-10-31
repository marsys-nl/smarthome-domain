package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isTrue

val WindowDetectionTest by testSuite(
    name = "Window detection capability tests",
) {
    test(name = "Updating the window detection capability preserves the context") {
        val capability = WindowDetection(current = true, context = Example)

        expectThat(capability.updateWith(value = false))
            .get(WindowDetection::context)
            .isA<Example>()
    }

    test(name = "Updating the window detection capability mutates the value") {
        val capability = WindowDetection(current = true, context = Example)
        val updated = capability.updateWith(value = false)

        expectThat(updated)
            .get(WindowDetection::current)
            .isFalse()
    }

    test(name = "Updating the window detection capability does not mutate the original capability") {
        val capability = WindowDetection(current = true, context = Example)
        capability.updateWith(value = false)

        expectThat(capability)
            .get(WindowDetection::current)
            .isTrue()
    }

    test(name = "Window detection capability can be created with default context") {
        expectThat(WindowDetection(current = true))
            .get(WindowDetection::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Window detection capability can be associated with a context") {
        expectThat(WindowDetection(current = true) with Example)
            .get(WindowDetection::context)
            .isA<Example>()
    }

    test(name = "Window detection capability is of the correct type") {
        expectThat(WindowDetection(current = true))
            .get(WindowDetection::type)
            .isEqualTo(WindowDetection)
    }
}
