package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isTrue
import network.marsys.smarthome.domain.capability.ThermostatMode.Mode
import network.marsys.smarthome.domain.capability.ThermostatStatus.Status
import network.marsys.smarthome.domain.unit.celsius

val ThermostatStatusTest by testSuite(
    name = "Thermostat status capability tests",
) {
    test(name = "Updating the thermostat status capability preserves the context") {
        val capability = ThermostatStatus(current = Status.Idle, context = Example)

        expectThat(capability.updateWith(value = Status.Heating))
            .get(ThermostatStatus::context)
            .isA<Example>()
    }

    test(name = "Updating the thermostat status capability mutates the value") {
        val capability = ThermostatStatus(current = Status.Idle, context = Example)
        val updated = capability.updateWith(value = Status.Heating)

        expectThat(updated)
            .get(ThermostatStatus::current)
            .isEqualTo(Status.Heating)
    }

    test(name = "Updating the thermostat status capability does not mutate the original capability") {
        val capability = ThermostatStatus(current = Status.Idle, context = Example)
        capability.updateWith(value = Status.Heating)

        expectThat(capability)
            .get(ThermostatStatus::current)
            .isEqualTo(Status.Idle)
    }

    test(name = "Thermostat status capability can be created with default context") {
        expectThat(ThermostatStatus(current = Status.Idle))
            .get(ThermostatStatus::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Thermostat status capability can be associated with a context") {
        expectThat(ThermostatStatus(current = Status.Idle) with Example)
            .get(ThermostatStatus::context)
            .isA<Example>()
    }

    testSuite(name = "Thermostat status capability value is computed correctly") {
        test(name = "When on-off is off, the status is off") {
            val computed = ThermostatStatus.compute(
                onOff = OnOff(current = false),
                mode = ThermostatMode(current = Mode.Heat),
                targetTemperature = 25.celsius,
                currentTemperature = 20.celsius,
            )

            expectThat(computed)
                .isEqualTo(Status.Off)
        }

        test(name = "When mode is heating and current temperature is above the target, the status is idle") {
            val computed = ThermostatStatus.compute(
                onOff = OnOff(current = true),
                mode = ThermostatMode(current = Mode.Heat),
                targetTemperature = 21.celsius,
                currentTemperature = 25.celsius,
            )

            expectThat(computed)
                .isEqualTo(Status.Idle)
        }

        test(name = "When mode is heating and current temperature is below the target, the status is heating") {
            val computed = ThermostatStatus.compute(
                onOff = OnOff(current = true),
                mode = ThermostatMode(current = Mode.Heat),
                targetTemperature = 25.celsius,
                currentTemperature = 20.celsius,
            )

            expectThat(computed)
                .isEqualTo(Status.Heating)
        }

        test(name = "When mode is cooling and current temperature is below the target, the status is idle") {
            val computed = ThermostatStatus.compute(
                onOff = OnOff(current = true),
                mode = ThermostatMode(current = Mode.Cool),
                targetTemperature = 25.celsius,
                currentTemperature = 21.celsius,
            )

            expectThat(computed)
                .isEqualTo(Status.Idle)
        }

        test(name = "When mode is cooling and current temperature is above the target, the status is cooling") {
            val computed = ThermostatStatus.compute(
                onOff = OnOff(current = true),
                mode = ThermostatMode(current = Mode.Cool),
                targetTemperature = 21.celsius,
                currentTemperature = 25.celsius,
            )

            expectThat(computed)
                .isEqualTo(Status.Cooling)
        }

        test(name = "When mode is auto and current temperature is below the target, the status is heating") {
            val computed = ThermostatStatus.compute(
                onOff = OnOff(current = true),
                mode = ThermostatMode(current = Mode.Auto),
                targetTemperature = 25.celsius,
                currentTemperature = 21.celsius,
            )

            expectThat(computed)
                .isEqualTo(Status.Heating)
        }

        test(name = "When mode is auto and current temperature is above the target, the status is cooling") {
            val computed = ThermostatStatus.compute(
                onOff = OnOff(current = true),
                mode = ThermostatMode(current = Mode.Auto),
                targetTemperature = 21.celsius,
                currentTemperature = 25.celsius,
            )

            expectThat(computed)
                .isEqualTo(Status.Cooling)
        }

        test(name = "When mode is auto and current temperature is equal to the target, the status is idle") {
            val computed = ThermostatStatus.compute(
                onOff = OnOff(current = true),
                mode = ThermostatMode(current = Mode.Auto),
                targetTemperature = 25.celsius,
                currentTemperature = 25.celsius,
            )

            expectThat(computed)
                .isEqualTo(Status.Idle)
        }
    }

    test(name = "Thermostat mode capability is of the correct type") {
        expectThat(ThermostatStatus(current = Status.Idle))
            .get(ThermostatStatus::type)
            .isEqualTo(ThermostatStatus)
    }
}
