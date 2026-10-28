package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import network.marsys.smarthome.domain.unit.celsius

val TargetTemperatureTest by testSuite(
    name = "Target temperature capability tests",
) {
    test(name = "Updating the target temperature capability preserves the context") {
        val capability = TargetTemperature(current = 30.celsius, context = Example)

        expectThat(capability.updateWith(value = 25.celsius))
            .get(TargetTemperature::context)
            .isA<Example>()
    }

    test(name = "Updating the target temperature capability mutates the value") {
        val capability = TargetTemperature(current = 30.celsius, context = Example)
        val updated = capability.updateWith(value = 25.celsius)

        expectThat(updated)
            .get(TargetTemperature::current)
            .isEqualTo(25.celsius)
    }

    test(name = "Updating the target temperature capability does not mutate the original capability") {
        val capability = TargetTemperature(current = 30.celsius, context = Example)
        capability.updateWith(value = 25.celsius)

        expectThat(capability)
            .get(TargetTemperature::current)
            .isEqualTo(30.celsius)
    }

    test(name = "Target temperature capability can be created with default context") {
        expectThat(TargetTemperature(current = 30.celsius))
            .get(TargetTemperature::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Target temperature capability can be associated with a context") {
        expectThat(TargetTemperature(current = 30.celsius) with Example)
            .get(TargetTemperature::context)
            .isA<Example>()
    }

    test(name = "Target temperature capability should have a current value between the supplied range") {
        val range = 10.celsius..20.celsius

        expectThrows<IllegalArgumentException> { TargetTemperature(current = 30.celsius, range = range) }
            .hasMessage("Current temperature 30°C is out of range. Given range: 10°C..20°C.")
    }

    test(name = "Updating the target temperature capability should have a current value between the supplied range") {
        val capability = TargetTemperature(current = 30.celsius, range = 5.celsius..35.celsius)

        expectThrows<IllegalArgumentException> { capability.updateWith(value = 50.celsius) }
            .hasMessage("Current temperature 50°C is out of range. Given range: 5°C..35°C.")
    }

    test(name = "Target temperature capability has a default range of 5°C to 35°C") {
        expectThat(TargetTemperature(current = 15.celsius))
            .get(TargetTemperature::range)
            .isEqualTo(5.celsius..35.celsius)
    }
}
