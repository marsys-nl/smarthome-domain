package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isTrue
import network.marsys.smarthome.domain.capability.ThermostatMode.Mode

val ThermostatModeTest by testSuite(
    name = "Thermostat mode capability tests",
) {
    test(name = "Updating the thermostat mode capability preserves the context") {
        val capability = ThermostatMode(current = Mode.Heat, context = Example)

        expectThat(capability.updateWith(value = Mode.Cool))
            .get(ThermostatMode::context)
            .isA<Example>()
    }

    test(name = "Updating the thermostat mode capability mutates the value") {
        val capability = ThermostatMode(current = Mode.Heat, context = Example)
        val updated = capability.updateWith(value = Mode.Cool)

        expectThat(updated)
            .get(ThermostatMode::current)
            .isEqualTo(Mode.Cool)
    }

    test(name = "Updating the thermostat mode capability does not mutate the original capability") {
        val capability = ThermostatMode(current = Mode.Heat, context = Example)
        capability.updateWith(value = Mode.Cool)

        expectThat(capability)
            .get(ThermostatMode::current)
            .isEqualTo(Mode.Heat)
    }

    test(name = "Thermostat mode capability can be created with default context") {
        expectThat(ThermostatMode(current = Mode.Heat))
            .get(ThermostatMode::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Thermostat mode capability can be associated with a context") {
        expectThat(ThermostatMode(current = Mode.Heat) with Example)
            .get(ThermostatMode::context)
            .isA<Example>()
    }

    testSuite(name = "Thermostat mode capability supports modes correctly when supplied") {
        Mode.entries.forEach { mode ->
            test(name = "When initializing with '$mode' as the current mode and supported modes, it supports that mode") {
                val capability = ThermostatMode(current = mode, supported = setOf(mode))

                expectThat(capability.supports(mode))
                    .isTrue()
            }
        }
    }

    testSuite(name = "Thermostat mode capability doesn't supports modes when not supplied") {
        Mode.entries.forEach { mode ->
            test(name = "When initializing an exception is thrown when '$mode' is given but not supported") {
                val supported = (Mode.entries - mode).toSet()

                expectThrows<IllegalArgumentException> { ThermostatMode(current = mode, supported = supported) }
                    .hasMessage("Current mode '$mode' is not supported. Supported modes are: $supported.")
            }
        }
    }

    test(name = "Thermostat mode capability supports heat and cool modes correctly when Auto is supported") {
        expectThat(ThermostatMode(current = Mode.Auto, supported = setOf(Mode.Auto))) {
            get { supports(Mode.Heat) }.isEqualTo(true)
            get { supports(Mode.Cool) }.isEqualTo(true)
        }
    }

    test(name = "Thermostat mode capability doesn't support heat mode when only cool mode is supported") {
        expectThat(ThermostatMode(current = Mode.Cool, supported = setOf(Mode.Cool)))
            .get { supports(Mode.Heat) }
            .isEqualTo(false)
    }

    test(name = "Thermostat mode capability doesn't support cool mode when only heat mode is supported") {
        expectThat(ThermostatMode(current = Mode.Heat, supported = setOf(Mode.Heat)))
            .get { supports(Mode.Cool) }
            .isEqualTo(false)
    }
}
