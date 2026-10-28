package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import network.marsys.smarthome.domain.unit.gigabytes

val MeasureDataSizeTest by testSuite(
    name = "Measure data size capability tests",
) {
    test(name = "Updating the measure data size capability preserves the context") {
        val capability = MeasureDataSize(current = 50.gigabytes, context = Example)

        expectThat(capability.updateWith(value = 25.gigabytes))
            .get(MeasureDataSize::context)
            .isA<Example>()
    }

    test(name = "Updating the measure data size capability mutates the value") {
        val capability = MeasureDataSize(current = 50.gigabytes, context = Example)
        val updated = capability.updateWith(value = 25.gigabytes)

        expectThat(updated)
            .get(MeasureDataSize::current)
            .isEqualTo(25.gigabytes)
    }

    test(name = "Updating the measure data size capability does not mutate the original capability") {
        val capability = MeasureDataSize(current = 50.gigabytes, context = Example)
        capability.updateWith(value = 25.gigabytes)

        expectThat(capability)
            .get(MeasureDataSize::current)
            .isEqualTo(50.gigabytes)
    }

    test(name = "Measure data size capability can be created with default context") {
        expectThat(MeasureDataSize(current = 50.gigabytes))
            .get(MeasureDataSize::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Measure data size capability can be associated with a context") {
        expectThat(MeasureDataSize(current = 50.gigabytes) with Example)
            .get(MeasureDataSize::context)
            .isA<Example>()
    }
}
