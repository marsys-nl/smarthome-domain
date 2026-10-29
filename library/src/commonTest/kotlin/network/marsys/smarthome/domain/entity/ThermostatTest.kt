package network.marsys.smarthome.domain.entity

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isNull
import dev.nmarsman.expect.assertions.isTrue
import network.marsys.smarthome.domain.capability.Brightness
import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.Capability.Companion.optional
import network.marsys.smarthome.domain.capability.Capability.Companion.required
import network.marsys.smarthome.domain.capability.ChildLock
import network.marsys.smarthome.domain.capability.MeasureDataSize
import network.marsys.smarthome.domain.capability.MeasureLoad
import network.marsys.smarthome.domain.capability.MeasureTemperature
import network.marsys.smarthome.domain.capability.OnOff
import network.marsys.smarthome.domain.capability.ScheduledMode
import network.marsys.smarthome.domain.capability.TargetTemperature
import network.marsys.smarthome.domain.capability.ThermostatMode
import network.marsys.smarthome.domain.capability.ThermostatMode.Mode
import network.marsys.smarthome.domain.capability.WindowDetection
import network.marsys.smarthome.domain.unit.celsius
import network.marsys.smarthome.domain.unit.gigabytes
import network.marsys.smarthome.domain.unit.percent

val ThermostatTest by testSuite(
    name = "Thermostat tests",
) {
    test(name = "Constructing a thermostat with no naming key uses the default naming key") {
        val entity = Thermostat(
            identifier = EntityIdentifier("test.thermostat"),
        )

        expectThat(entity)
            .get(Entity::naming)
            .get(EntityNaming::key)
            .isEqualTo(Thermostat.defaultNamingKey)
    }

    test(name = "Constructing a thermostat with no explicit state uses the unknown state") {
        val entity = Thermostat(
            identifier = EntityIdentifier("test.thermostat"),
        )

        expectThat(entity)
            .get(Entity::state)
            .isA<Entity.State.Unknown>()
    }

    testSuite(
        name = "Thermostat implements activatable",
    ) {
        test(name = "Thermostat is active when state is known and on-off capability is on") {
            val thermostat = Thermostat(
                identifier = EntityIdentifier("test.thermostat"),
                state = Thermostat.known(),
            )

            expectThat(thermostat)
                .get(Thermostat::active)
                .isTrue()
        }

        test(name = "Thermostat is inactive when state is unknown") {
            val thermostat = Thermostat(
                identifier = EntityIdentifier("test.thermostat"),
                state = Thermostat.State.Unknown,
            )

            expectThat(thermostat)
                .get(Thermostat::active)
                .isFalse()
        }

        test(name = "Thermostat is inactive when state is known and on-off capability is off") {
            val thermostat = Thermostat(
                identifier = EntityIdentifier("test.thermostat"),
                state = Thermostat.known(
                    onOff = required(OnOff(current = false)),
                ),
            )

            expectThat(thermostat)
                .get(Thermostat::active)
                .isFalse()
        }
    }

    testSuite(
        name = "Getting capability from state",
    ) {
        test(name = "Getting on-off capability from known state succeeds") {
            val state = Thermostat.known(
                onOff = required(OnOff(current = true)),
            )

            expectThat(state)
                .get { get(OnOff) }
                .isA<OnOff>()
        }

        test(name = "Getting thermostat mode capability from known state succeeds") {
            val state = Thermostat.known(
                mode = required(ThermostatMode(current = Mode.Auto)),
            )

            expectThat(state)
                .get { get(ThermostatMode) }
                .isA<ThermostatMode>()
        }

        test(name = "Getting target temperature capability from known state succeeds") {
            val state = Thermostat.known(
                targetTemperature = required(TargetTemperature(current = 21.celsius)),
            )

            expectThat(state)
                .get { get(TargetTemperature) }
                .isA<TargetTemperature>()
        }

        test(name = "Getting temperature capability from known state without context fails") {
            val state = Thermostat.known()

            expectThrows<IllegalStateException> { state.get(MeasureTemperature) }
                .hasMessage("Context is required for 'measure temperature' capability")
        }

        test(name = "Getting current temperature capability from known state succeeds") {
            val state = Thermostat.known(
                currentTemperature = required(MeasureTemperature(current = 21.celsius)),
            )

            expectThat(state)
                .get { get(MeasureTemperature, context = Thermostat.TemperatureType.Current) }
                .isA<MeasureTemperature>()
        }

        test(name = "Getting outdoor temperature capability from known state succeeds") {
            val state = Thermostat.known(
                outdoorTemperature = optional(MeasureTemperature(current = 21.celsius)),
            )

            expectThat(state)
                .get { get(MeasureTemperature, context = Thermostat.TemperatureType.Outdoor) }
                .isA<MeasureTemperature>()
        }

        test(name = "Getting outdoor temperature capability from known state returns null if it is not available") {
            val state = Thermostat.known(
                outdoorTemperature = optional(null),
            )

            expectThat(state)
                .get { get(MeasureTemperature, context = Thermostat.TemperatureType.Outdoor) }
                .isNull()
        }

        test(name = "Getting child lock capability from known state succeeds") {
            val state = Thermostat.known(
                childLock = optional(ChildLock(current = true)),
            )

            expectThat(state)
                .get { get(ChildLock) }
                .isA<ChildLock>()
        }

        test(name = "Getting child lock capability from known state returns null if it is not available") {
            val state = Thermostat.known(
                childLock = optional(null),
            )

            expectThat(state)
                .get { get(ChildLock) }
                .isNull()
        }

        test(name = "Getting scheduled mode capability from known state succeeds") {
            val state = Thermostat.known(
                scheduledMode = optional(ScheduledMode(current = true)),
            )

            expectThat(state)
                .get { get(ScheduledMode) }
                .isA<ScheduledMode>()
        }

        test(name = "Getting scheduled mode capability from known state returns null if it is not available") {
            val state = Thermostat.known(
                scheduledMode = optional(null),
            )

            expectThat(state)
                .get { get(ScheduledMode) }
                .isNull()
        }

        test(name = "Getting window detection capability from known state succeeds") {
            val state = Thermostat.known(
                windowDetection = optional(WindowDetection(current = true)),
            )

            expectThat(state)
                .get { get(WindowDetection) }
                .isA<WindowDetection>()
        }

        test(name = "Getting window detection capability from known state returns null if it is not available") {
            val state = Thermostat.known(
                windowDetection = optional(null),
            )

            expectThat(state)
                .get { get(WindowDetection) }
                .isNull()
        }

        test(name = "Getting an unknown capability from known state returns null") {
            val state = Thermostat.known()

            expectThat(state)
                .get { get(MeasureLoad) }
                .isNull()
        }
    }

    testSuite(
        name = "Updating state",
    ) {
        test(name = "Updating the on-off capability succeeds") {
            val update = OnOff(current = false)
            val state = Thermostat.known(
                onOff = required(OnOff(current = true)),
            )

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                onOff = state.onOff.updateWith(capability = update),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the thermostat mode capability succeeds") {
            val update = ThermostatMode(current = Mode.Heat)
            val state = Thermostat.known(
                mode = required(ThermostatMode(current = Mode.Auto)),
            )

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                mode = state.mode.updateWith(capability = update),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the target temperature capability succeeds") {
            val update = TargetTemperature(current = 25.celsius)
            val state = Thermostat.known(
                targetTemperature = required(TargetTemperature(current = 30.celsius)),
            )

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                temperatures = state.temperatures.copy(
                    target = state.temperatures.target.updateWith(capability = update),
                ),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the current temperature capability succeeds") {
            val update = MeasureTemperature(current = 25.celsius) with Thermostat.TemperatureType.Current
            val state = Thermostat.known(
                currentTemperature = required(MeasureTemperature(current = 30.celsius)),
            )

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                temperatures = state.temperatures.copy(
                    current = state.temperatures.current.updateWith(capability = update),
                ),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the outdoor temperature capability succeeds when it is available") {
            val update = MeasureTemperature(current = 25.celsius) with Thermostat.TemperatureType.Outdoor
            val state = Thermostat.known(
                outdoorTemperature = optional(MeasureTemperature(current = 30.celsius)),
            )

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                temperatures = state.temperatures.copy(
                    outdoor = state.temperatures.outdoor.updateWith(capability = update),
                ),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the outdoor temperature capability keeps the capability as unsupported") {
            val update = MeasureTemperature(current = 25.celsius) with Thermostat.TemperatureType.Outdoor
            val state = Thermostat.known(
                outdoorTemperature = optional(null),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isA<Thermostat.State.Known>()
                .get(Thermostat.State.Known::temperatures)
                .get(Thermostat.Temperatures::outdoor)
                .isA<Capability.Unsupported>()
        }

        test(name = "Updating the measure temperature capability without context fails") {
            val update = MeasureTemperature(current = 35.celsius)
            val state = Thermostat.known()

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Context is required for 'measure temperature' capability")
        }

        test(name = "Updating the child lock capability succeeds when it is available") {
            val update = ChildLock(current = false)
            val state = Thermostat.known(
                childLock = optional(ChildLock(current = true)),
            )

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                childLock = state.childLock.updateWith(capability = update),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the child lock capability keeps the capability as unsupported") {
            val update = ChildLock(current = false)
            val state = Thermostat.known(
                childLock = optional(null),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isA<Thermostat.State.Known>()
                .get(Thermostat.State.Known::childLock)
                .isA<Capability.Unsupported>()
        }

        test(name = "Updating the scheduled mode capability succeeds when it is available") {
            val update = ScheduledMode(current = false)
            val state = Thermostat.known(
                scheduledMode = optional(ScheduledMode(current = true)),
            )

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                scheduledMode = state.scheduledMode.updateWith(capability = update),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the scheduled mode capability keeps the capability as unsupported") {
            val update = ScheduledMode(current = false)
            val state = Thermostat.known(
                scheduledMode = optional(null),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isA<Thermostat.State.Known>()
                .get(Thermostat.State.Known::scheduledMode)
                .isA<Capability.Unsupported>()
        }

        test(name = "Updating the window detection capability succeeds when it is available") {
            val update = WindowDetection(current = false)
            val state = Thermostat.known(
                windowDetection = optional(WindowDetection(current = true)),
            )

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                windowDetection = state.windowDetection.updateWith(capability = update),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the window detection capability keeps the capability as unsupported") {
            val update = WindowDetection(current = false)
            val state = Thermostat.known(
                windowDetection = optional(null),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isA<Thermostat.State.Known>()
                .get(Thermostat.State.Known::windowDetection)
                .isA<Capability.Unsupported>()
        }

        test(name = "Updating an unsupported capability throws an exception") {
            val update = MeasureLoad(current = 50.percent)
            val state = Thermostat.known()

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Unsupported 'MeasureLoad' capability provided for 'Thermostat.State.Known'")
        }
    }
}

private fun Thermostat.Companion.known(
    onOff: Capability.Required<OnOff> = required(OnOff(current = true)),
    mode: Capability.Required<ThermostatMode> = required(ThermostatMode(current = Mode.Heat)),
    targetTemperature: Capability.Required<TargetTemperature> = required(TargetTemperature(current = 20.celsius)),
    currentTemperature: Capability.Required<MeasureTemperature> = required(MeasureTemperature(current = 23.celsius)),
    outdoorTemperature: Capability.Optional<MeasureTemperature> = optional(null),
    childLock: Capability.Optional<ChildLock> = optional(ChildLock(current = true)),
    scheduledMode: Capability.Optional<ScheduledMode> = optional(ScheduledMode(current = true)),
    windowDetection: Capability.Optional<WindowDetection> = optional(WindowDetection(current = true)),
) = Thermostat.State.Known(
    onOff = onOff,
    mode = mode,
    temperatures = Thermostat.Temperatures(
        target = targetTemperature,
        current = currentTemperature,
        outdoor = outdoorTemperature,
    ),
    childLock = childLock,
    scheduledMode = scheduledMode,
    windowDetection = windowDetection,
)
