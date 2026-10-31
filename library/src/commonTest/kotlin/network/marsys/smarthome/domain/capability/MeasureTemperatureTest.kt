package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import network.marsys.smarthome.domain.unit.celsius

val MeasureTemperatureTest by testSuite(
    name = "Measure temperature capability tests",
) {
    test(name = "Updating the measure temperature capability preserves the context") {
        val capability = MeasureTemperature(current = 50.celsius, context = Example)

        expectThat(capability.updateWith(value = 25.celsius))
            .get(MeasureTemperature::context)
            .isA<Example>()
    }

    test(name = "Updating the measure temperature capability mutates the value") {
        val capability = MeasureTemperature(current = 50.celsius, context = Example)
        val updated = capability.updateWith(value = 25.celsius)

        expectThat(updated)
            .get(MeasureTemperature::current)
            .isEqualTo(25.celsius)
    }

    test(name = "Updating the measure temperature capability does not mutate the original capability") {
        val capability = MeasureTemperature(current = 50.celsius, context = Example)
        capability.updateWith(value = 25.celsius)

        expectThat(capability)
            .get(MeasureTemperature::current)
            .isEqualTo(50.celsius)
    }

    test(name = "Measure temperature capability can be created with default context") {
        expectThat(MeasureTemperature(current = 50.celsius))
            .get(MeasureTemperature::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Measure temperature capability can be associated with a context") {
        expectThat(MeasureTemperature(current = 50.celsius) with Example)
            .get(MeasureTemperature::context)
            .isA<Example>()
    }

    test(name = "Measure temperature capability is of the correct type") {
        expectThat(MeasureTemperature(current = 50.celsius))
            .get(MeasureTemperature::type)
            .isEqualTo(MeasureTemperature)
    }
}
