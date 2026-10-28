package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import network.marsys.smarthome.domain.unit.percent

val PositionTest by testSuite(
    name = "Position capability tests",
) {
    test(name = "Updating the position capability preserves the context") {
        val capability = Position(current = 50.percent, context = Example)

        expectThat(capability.updateWith(value = 25.percent))
            .get(Position::context)
            .isA<Example>()
    }

    test(name = "Updating the position capability mutates the value") {
        val capability = Position(current = 50.percent, context = Example)
        val updated = capability.updateWith(value = 25.percent)

        expectThat(updated)
            .get(Position::current)
            .isEqualTo(25.percent)
    }

    test(name = "Updating the position capability does not mutate the original capability") {
        val capability = Position(current = 50.percent, context = Example)
        capability.updateWith(value = 25.percent)

        expectThat(capability)
            .get(Position::current)
            .isEqualTo(50.percent)
    }

    test(name = "Position capability can be created with default context") {
        expectThat(Position(current = 50.percent))
            .get(Position::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Position capability can be associated with a context") {
        expectThat(Position(current = 50.percent) with Example)
            .get(Position::context)
            .isA<Example>()
    }
}
