package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isTrue

val MovementTest by testSuite(
    name = "Movement capability tests",
) {
    test(name = "Updating the movement capability preserves the context") {
        val capability = Movement(current = Movement.Direction.Idle, context = Example)

        expectThat(capability.updateWith(value = Movement.Direction.Closing))
            .get(Movement::context)
            .isA<Example>()
    }

    test(name = "Updating the movement capability mutates the value") {
        val capability = Movement(current = Movement.Direction.Idle, context = Example)
        val updated = capability.updateWith(value = Movement.Direction.Opening)

        expectThat(updated)
            .get(Movement::current)
            .isEqualTo(Movement.Direction.Opening)
    }

    test(name = "Updating the movement capability does not mutate the original capability") {
        val capability = Movement(current = Movement.Direction.Idle, context = Example)
        capability.updateWith(value = Movement.Direction.Closing)

        expectThat(capability)
            .get(Movement::current)
            .isEqualTo(Movement.Direction.Idle)
    }

    test(name = "Movement capability can be created with default context") {
        expectThat(Movement(current = Movement.Direction.Idle))
            .get(Movement::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Movement capability can be associated with a context") {
        expectThat(Movement(current = Movement.Direction.Idle) with Example)
            .get(Movement::context)
            .isA<Example>()
    }
}
