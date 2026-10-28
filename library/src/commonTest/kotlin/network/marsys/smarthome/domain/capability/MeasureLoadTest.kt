package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import network.marsys.smarthome.domain.unit.percent

val MeasureLoadTest by testSuite(
    name = "Measure load capability tests",
) {
    test(name = "Updating the measure load capability preserves the context") {
        val capability = MeasureLoad(current = 50.percent, context = Example)

        expectThat(capability.updateWith(value = 25.percent))
            .get(MeasureLoad::context)
            .isA<Example>()
    }

    test(name = "Updating the measure load capability mutates the value") {
        val capability = MeasureLoad(current = 50.percent, context = Example)
        val updated = capability.updateWith(value = 25.percent)

        expectThat(updated)
            .get(MeasureLoad::current)
            .isEqualTo(25.percent)
    }

    test(name = "Updating the measure load capability does not mutate the original capability") {
        val capability = MeasureLoad(current = 50.percent, context = Example)
        capability.updateWith(value = 25.percent)

        expectThat(capability)
            .get(MeasureLoad::current)
            .isEqualTo(50.percent)
    }

    test(name = "Measure load capability can be created with default context") {
        expectThat(MeasureLoad(current = 50.percent))
            .get(MeasureLoad::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Measure load capability can be associated with a context") {
        expectThat(MeasureLoad(current = 50.percent) with Example)
            .get(MeasureLoad::context)
            .isA<Example>()
    }
}
