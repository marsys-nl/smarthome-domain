package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isTrue
import network.marsys.smarthome.domain.entity.EntityIdentifier

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

    test(name = "Movement capability is of the correct type") {
        expectThat(Movement(current = Movement.Direction.Opening))
            .get(Movement::type)
            .isEqualTo(Movement)
    }

    test(name = "Movement capability open entity action is able to be created") {
        val action = Movement.Move.Open(
            identifier = EntityIdentifier("thermostat.test"),
        )

        expectThat(action)
            .isA<Movement.Move>()
    }

    test(name = "Movement capability close entity action is able to be created") {
        val action = Movement.Move.Close(
            identifier = EntityIdentifier("blind.test"),
        )

        expectThat(action)
            .isA<Movement.Move>()
    }

    test(name = "Movement capability stop entity action is able to be created") {
        val action = Movement.Move.Stop(
            identifier = EntityIdentifier("blind.test"),
        )

        expectThat(action)
            .isA<Movement.Move>()
    }
}
