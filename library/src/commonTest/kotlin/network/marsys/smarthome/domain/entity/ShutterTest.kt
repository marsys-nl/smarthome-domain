package network.marsys.smarthome.domain.entity

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isNull
import network.marsys.smarthome.domain.capability.Capability.Companion.optional
import network.marsys.smarthome.domain.capability.Capability.Companion.required
import network.marsys.smarthome.domain.capability.MeasureLoad
import network.marsys.smarthome.domain.capability.Movement
import network.marsys.smarthome.domain.capability.Movement.Direction
import network.marsys.smarthome.domain.capability.Opened
import network.marsys.smarthome.domain.capability.Position
import network.marsys.smarthome.domain.entity.Cover.Control
import network.marsys.smarthome.domain.unit.percent

val ShutterTest by testSuite(
    name = "Shutter test",
) {
    test(name = "Constructing a shutter with no naming key uses the default naming key") {
        val shutter = Shutter(identifier = EntityIdentifier("test.shutter"))

        expectThat(shutter)
            .get(Entity::naming)
            .get(EntityNaming::key)
            .isEqualTo(Shutter.defaultNamingKey)
    }

    test(name = "Constructing a shutter with no explicit state uses the unknown state") {
        val shutter = Shutter(identifier = EntityIdentifier("test.shutter"))

        expectThat(shutter)
            .get(Entity::state)
            .isA<Entity.State.Unknown>()
    }

    test(name = "Shutter has a vertical orientation") {
        val shutter = Shutter(identifier = EntityIdentifier("test.shutter"))

        expectThat(shutter)
            .get(Cover::orientation)
            .isEqualTo(Cover.Orientation.Vertical)
    }

    testSuite(
        name = "Updating state",
    ) {
        test(name = "Updating the position capability succeeds") {
            val update = Position(current = 75.percent)
            val state = Shutter.State.Known(
                control = Control.WithPosition(
                    position = required(Position(current = 50.percent)),
                    movement = optional(null),
                ),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isEqualTo(state.copy(control = state.control.updateWith(capability = update)))
        }

        test(name = "Updating the position capability fails when there is no position capability available") {
            val update = Position(current = 75.percent)
            val state = Shutter.State.Known(
                control = Control.WithoutPosition(
                    opened = required(Opened(current = true)),
                    movement = optional(Movement(current = Direction.Idle)),
                ),
            )

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Unsupported 'Position' capability provided for 'Cover.Control.WithoutPosition'")
        }

        test(name = "Updating the movement capability succeeds") {
            val update = Movement(current = Direction.Opening)
            val state = Shutter.State.Known(
                control = Control.WithPosition(
                    position = required(Position(current = 50.percent)),
                    movement = optional(Movement(current = Direction.Idle)),
                ),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isEqualTo(state.copy(control = state.control.updateWith(capability = update)))
        }

        test(name = "Updating the opened capability succeeds") {
            val update = Opened(current = false)
            val state = Shutter.State.Known(
                control = Control.WithoutPosition(
                    opened = required(Opened(current = true)),
                    movement = optional(Movement(current = Direction.Idle)),
                ),
            )

            val updated = state.updateWith(capability = update)

            expectThat(updated)
                .isEqualTo(state.copy(control = state.control.updateWith(capability = update)))
        }

        test(name = "Updating the opened capability fails when opened capability is computed") {
            val update = Opened(current = false)
            val state = Shutter.State.Known(
                control = Control.WithPosition(
                    position = required(Position(current = 50.percent)),
                    movement = optional(null),
                ),
            )

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Unsupported 'Opened' capability provided for 'Cover.Control.WithPosition'")
        }

        test(name = "Updating an unsupported capability throws an exception") {
            val update = MeasureLoad(current = 50.percent)
            val state = Shutter.State.Known(
                control = Control.WithoutPosition(
                    opened = required(Opened(current = true)),
                    movement = optional(Movement(current = Direction.Idle)),
                ),
            )

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Unsupported 'MeasureLoad' capability provided for 'Shutter.State.Known'")
        }
    }
}
