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
import network.marsys.smarthome.domain.capability.MeasureLoad
import network.marsys.smarthome.domain.capability.OnOff
import network.marsys.smarthome.domain.unit.percent

val LightTest by testSuite(
    name = "Light tests",
) {
    test(name = "Constructing a light with no naming key uses the default naming key") {
        val entity = Light(
            identifier = EntityIdentifier("test.light"),
        )

        expectThat(entity)
            .get(Entity::naming)
            .get(EntityNaming::key)
            .isEqualTo(Light.defaultNamingKey)
    }

    test(name = "Constructing a light with no explicit state uses the unknown state") {
        val entity = Light(
            identifier = EntityIdentifier("test.light"),
        )

        expectThat(entity)
            .get(Entity::state)
            .isA<Entity.State.Unknown>()
    }

    testSuite(
        name = "Light implements activatable",
    ) {
        test(name = "Light is active when state is known and on-off capability is on") {
            val light = Light(
                identifier = EntityIdentifier("test.light"),
                state = Light.State.Known(
                    onOff = required(OnOff(current = true)),
                    brightness = optional(null),
                ),
            )

            expectThat(light)
                .get(Light::active)
                .isTrue()
        }

        test(name = "Light is inactive when state is unknown") {
            val light = Light(
                identifier = EntityIdentifier("test.light"),
                state = Light.State.Unknown,
            )

            expectThat(light)
                .get(Light::active)
                .isFalse()
        }

        test(name = "Light is inactive when state is known and on-off capability is off") {
            val light = Light(
                identifier = EntityIdentifier("test.light"),
                state = Light.State.Known(
                    onOff = required(OnOff(current = false)),
                    brightness = optional(null),
                ),
            )

            expectThat(light)
                .get(Light::active)
                .isFalse()
        }
    }

    testSuite(
        name = "Getting capability from state",
    ) {
        test(name = "Getting on-off capability from known state returns the capability when it is provided") {
            val state = Light.State.Known(
                onOff = required(OnOff(current = true)),
                brightness = optional(null),
            )

            expectThat(state)
                .get { get(OnOff) }
                .isA<OnOff>()
        }

        test(name = "Getting brightness capability from known state returns the capability when it is provided") {
            val state = Light.State.Known(
                onOff = required(OnOff(current = true)),
                brightness = optional(Brightness(current = 50.percent)),
            )

            expectThat(state)
                .get { get(Brightness) }
                .isA<Brightness>()
        }

        test(name = "Getting brightness capability from known state returns null when it is not provided") {
            val state = Light.State.Known(
                onOff = required(OnOff(current = true)),
                brightness = optional(null),
            )

            expectThat(state)
                .get { get(Brightness) }
                .isNull()
        }

        test(name = "Getting a for light unknown capability from known state returns null") {
            val state = Light.State.Known(
                onOff = required(OnOff(current = true)),
                brightness = optional(null),
            )

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
            val state = Light.State.Known(
                onOff = required(OnOff(current = true)),
                brightness = optional(null),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isEqualTo(state.copy(onOff = state.onOff.updateWith(capability = update)))
        }

        test(name = "Updating the brightness capability succeeds when it is available") {
            val update = Brightness(current = 25.percent)
            val state = Light.State.Known(
                onOff = required(OnOff(current = true)),
                brightness = optional(Brightness(current = 50.percent)),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isEqualTo(state.copy(brightness = state.brightness.updateWith(capability = update)))
        }

        test(name = "Updating the brightness capability keeps the brightness capability as unsupported") {
            val update = Brightness(current = 25.percent)
            val state = Light.State.Known(
                onOff = required(OnOff(current = true)),
                brightness = optional(null),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isA<Light.State.Known>()
                .get(Light.State.Known::brightness)
                .isA<Capability.Unsupported>()
        }

        test(name = "Updating an unsupported capability throws an exception") {
            val update = MeasureLoad(current = 50.percent)
            val state = Light.State.Known(
                onOff = required(OnOff(current = true)),
                brightness = optional(null),
            )

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Unsupported 'MeasureLoad' capability provided for 'Light.State.Known'")
        }
    }
}
