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
import network.marsys.smarthome.domain.capability.Capability.Companion.required
import network.marsys.smarthome.domain.capability.MeasureLoad
import network.marsys.smarthome.domain.capability.OnOff
import network.marsys.smarthome.domain.unit.percent

val FanTest by testSuite(
    name = "Fan tests",
) {
    test(name = "Constructing a fan with no naming key uses the default naming key") {
        val entity = Fan(
            identifier = EntityIdentifier("test.fan"),
        )

        expectThat(entity)
            .get(Entity::naming)
            .get(EntityNaming::key)
            .isEqualTo(Fan.defaultNamingKey)
    }

    test(name = "Constructing a fan with no explicit state uses the unknown state") {
        val entity = Fan(
            identifier = EntityIdentifier("test.fan"),
        )

        expectThat(entity)
            .get(Entity::state)
            .isA<Entity.State.Unknown>()
    }

    testSuite(
        name = "Fan implements activatable",
    ) {
        test(name = "Fan is active when state is known and on-off capability is on") {
            val fan = Fan(
                identifier = EntityIdentifier("test.fan"),
                state = Fan.State.Known(
                    onOff = required(OnOff(current = true)),
                ),
            )

            expectThat(fan)
                .get(Fan::active)
                .isTrue()
        }

        test(name = "Fan is inactive when state is unknown") {
            val fan = Fan(
                identifier = EntityIdentifier("test.fan"),
                state = Fan.State.Unknown,
            )

            expectThat(fan)
                .get(Fan::active)
                .isFalse()
        }

        test(name = "Fan is inactive when state is known and on-off capability is off") {
            val fan = Fan(
                identifier = EntityIdentifier("test.fan"),
                state = Fan.State.Known(
                    onOff = required(OnOff(current = false)),
                ),
            )

            expectThat(fan)
                .get(Fan::active)
                .isFalse()
        }
    }

    testSuite(
        name = "Getting capability from state",
    ) {
        test(name = "Getting on-off capability from known state succeeds") {
            val state = Fan.State.Known(
                onOff = required(OnOff(current = true)),
            )

            expectThat(state)
                .get { get(OnOff) }
                .isA<OnOff>()
        }

        test(name = "Getting an unknown capability from known state returns null") {
            val state = Fan.State.Known(
                onOff = required(OnOff(current = true)),
            )

            expectThat(state)
                .get { get(Brightness) }
                .isNull()
        }
    }

    testSuite(
        name = "Updating state",
    ) {
        test(name = "Updating the on-off capability succeeds") {
            val update = OnOff(current = false)
            val state = Fan.State.Known(
                onOff = required(OnOff(current = true)),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isEqualTo(state.copy(onOff = state.onOff.updateWith(capability = update)))
        }

        test(name = "Updating an unsupported capability throws an exception") {
            val update = MeasureLoad(current = 50.percent)
            val state = Fan.State.Known(
                onOff = required(OnOff(current = true)),
            )

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Unsupported 'MeasureLoad' capability provided for 'Fan.State.Known'")
        }
    }
}
