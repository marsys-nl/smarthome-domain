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

val SmartPlugTest by testSuite(
    name = "Smart plug tests",
) {
    test(name = "Constructing a smart plug with no naming key uses the default naming key") {
        val entity = SmartPlug(
            identifier = EntityIdentifier("test.smart-plug"),
        )

        expectThat(entity)
            .get(Entity::naming)
            .get(EntityNaming::key)
            .isEqualTo(SmartPlug.defaultNamingKey)
    }

    test(name = "Constructing a smart plug with no explicit state uses the unknown state") {
        val entity = SmartPlug(
            identifier = EntityIdentifier("test.smart-plug"),
        )

        expectThat(entity)
            .get(Entity::state)
            .isA<Entity.State.Unknown>()
    }

    testSuite(
        name = "Smart plug implements activatable",
    ) {
        test(name = "Smart plug is active when state is known and on-off capability is on") {
            val plug = SmartPlug(
                identifier = EntityIdentifier("test.smart-plug"),
                state = SmartPlug.State.Known(
                    onOff = required(OnOff(current = true)),
                ),
            )

            expectThat(plug)
                .get(SmartPlug::active)
                .isTrue()
        }

        test(name = "Smart plug is inactive when state is unknown") {
            val plug = SmartPlug(
                identifier = EntityIdentifier("test.smart-plug"),
                state = SmartPlug.State.Unknown,
            )

            expectThat(plug)
                .get(SmartPlug::active)
                .isFalse()
        }

        test(name = "Smart plug is inactive when state is known and on-off capability is off") {
            val plug = SmartPlug(
                identifier = EntityIdentifier("test.smart-plug"),
                state = SmartPlug.State.Known(
                    onOff = required(OnOff(current = false)),
                ),
            )

            expectThat(plug)
                .get(SmartPlug::active)
                .isFalse()
        }
    }

    testSuite(
        name = "Getting capability from state",
    ) {
        test(name = "Getting on-off capability from known state succeeds") {
            val state = SmartPlug.State.Known(
                onOff = required(OnOff(current = true)),
            )

            expectThat(state)
                .get { get(OnOff) }
                .isA<OnOff>()
        }

        test(name = "Getting an unknown capability from known state returns null") {
            val state = SmartPlug.State.Known(
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
            val state = SmartPlug.State.Known(
                onOff = required(OnOff(current = true)),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isEqualTo(state.copy(onOff = state.onOff.updateWith(capability = update)))
        }

        test(name = "Updating an unsupported capability throws an exception") {
            val update = Brightness(current = 50.percent)
            val state = SmartPlug.State.Known(
                onOff = required(OnOff(current = true)),
            )

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Unsupported 'Brightness' capability provided for 'SmartPlug.State.Known'")
        }
    }
}
