package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.api.expectThrows
import dev.nmarsman.expect.assertions.hasMessage
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isNull
import dev.nmarsman.expect.assertions.isTrue
import network.marsys.smarthome.domain.capability.Capability.Companion.optional
import network.marsys.smarthome.domain.capability.Capability.Companion.required
import network.marsys.smarthome.domain.capability.Movement.Direction
import network.marsys.smarthome.domain.entity.Blind
import network.marsys.smarthome.domain.entity.Cover.Control
import network.marsys.smarthome.domain.entity.Curtain
import network.marsys.smarthome.domain.entity.Shutter
import network.marsys.smarthome.domain.unit.percent

val CoverTest by testSuite(
    name = "Cover tests",
) {
    testSuite(
        name = "Cover control with position",
    ) {
        test(name = "Computed capability opened is true when position is greater than 0 percent") {
            val control = Control.WithPosition(
                position = required(Position(current = 50.percent)),
                movement = optional(null),
            )

            expectThat(control.opened)
                .get(Capability.Computed<Opened>::value)
                .get(Opened::current)
                .isTrue()
        }

        test(name = "Computed capability opened is false when position is equal to 0 percent") {
            val control = Control.WithPosition(
                position = required(Position(current = 0.percent)),
                movement = optional(null),
            )

            expectThat(control.opened)
                .get(Capability.Computed<Opened>::value)
                .get(Opened::current)
                .isFalse()
        }

        testSuite(
            name = "Getting capability from state",
        ) {
            test(name = "Getting position capability from known state succeeds") {
                val state = Blind.State.Known(
                    control = Control.WithPosition(
                        position = required(Position(current = 50.percent)),
                        movement = optional(Movement(current = Direction.Opening)),
                    ),
                )

                expectThat(state)
                    .get { find(Position) }
                    .isA<Position>()
            }

            test(name = "Getting position capability from known state returns null when it is not provided") {
                val state = Curtain.State.Known(
                    control = Control.WithoutPosition(
                        opened = required(Opened(current = true)),
                        movement = optional(null),
                    ),
                )

                expectThat(state)
                    .get { get(Position) }
                    .isNull()
            }

            test(name = "Getting opened capability from known state succeeds") {
                val state = Shutter.State.Known(
                    control = Control.WithPosition(
                        position = required(Position(current = 50.percent)),
                        movement = optional(Movement(current = Direction.Opening)),
                    ),
                )

                expectThat(state)
                    .get { get(Opened) }
                    .isA<Opened>()
            }

            test(name = "Getting movement capability from known state succeeds") {
                val state = Blind.State.Known(
                    control = Control.WithPosition(
                        position = required(Position(current = 50.percent)),
                        movement = optional(Movement(current = Direction.Opening)),
                    ),
                )

                expectThat(state)
                    .get { get(Movement) }
                    .isA<Movement>()
            }

            test(name = "Getting movement capability from known state returns null when it is not provided") {
                val state = Curtain.State.Known(
                    control = Control.WithPosition(
                        position = required(Position(current = 50.percent)),
                        movement = optional(null),
                    ),
                )

                expectThat(state)
                    .get { get(Movement) }
                    .isNull()
            }
        }

        testSuite(
            name = "Updating control",
        ) {
            test(name = "Updating the position capability succeeds") {
                val update = Position(current = 80.percent)
                val control = Control.WithPosition(
                    position = required(Position(current = 50.percent)),
                    movement = optional(null),
                )

                val updated = control.updateWith(capability = update)

                expectThat(updated)
                    .isEqualTo(control.copy(position = control.position.updateWith(capability = update)))
            }

            test(name = "Updating the movement capability succeeds when it is available") {
                val update = Movement(current = Direction.Opening)
                val control = Control.WithPosition(
                    position = required(Position(current = 50.percent)),
                    movement = optional(Movement(current = Direction.Idle)),
                )

                val updated = control.updateWith(capability = update)

                expectThat(updated)
                    .isEqualTo(control.copy(movement = control.movement.updateWith(capability = update)))
            }

            test(name = "Updating the movement capability keeps the movement capability as unsupported") {
                val update = Movement(current = Direction.Opening)
                val control = Control.WithPosition(
                    position = required(Position(current = 50.percent)),
                    movement = optional(null),
                )

                val updated = control.updateWith(capability = update)

                expectThat(updated)
                    .isA<Control.WithPosition>()
                    .get(Control.WithPosition::movement)
                    .isA<Capability.Unsupported>()
            }

            test(name = "Updating an unsupported capability throws an exception") {
                val update = MeasureLoad(current = 50.percent)
                val control = Control.WithPosition(
                    position = required(Position(current = 50.percent)),
                    movement = optional(null),
                )

                expectThrows<IllegalStateException> { control.updateWith(capability = update) }
                    .hasMessage("Unsupported 'MeasureLoad' capability provided for 'Cover.Control.WithPosition'")
            }
        }
    }

    testSuite(
        name = "Cover control without position",
    ) {
        test(name = "Position capability is unsupported") {
            val control = Control.WithoutPosition(
                opened = required(Opened(current = true)),
                movement = optional(Movement(current = Direction.Idle)),
            )

            expectThat(control.position)
                .isA<Capability.Unsupported>()
        }

        testSuite(
            name = "Updating control",
        ) {
            test(name = "Updating the opened capability succeeds") {
                val update = Opened(current = false)
                val control = Control.WithoutPosition(
                    opened = required(Opened(current = true)),
                    movement = optional(null),
                )

                val updated = control.updateWith(capability = update)

                expectThat(updated)
                    .isEqualTo(control.copy(opened = control.opened.updateWith(capability = update)))
            }

            test(name = "Updating the movement capability succeeds when it is available") {
                val update = Movement(current = Direction.Opening)
                val control = Control.WithoutPosition(
                    opened = required(Opened(current = true)),
                    movement = optional(Movement(current = Direction.Idle)),
                )

                val updated = control.updateWith(capability = update)

                expectThat(updated)
                    .isEqualTo(control.copy(movement = control.movement.updateWith(capability = update)))
            }

            test(name = "Updating the movement capability keeps the movement capability as unsupported") {
                val update = Movement(current = Direction.Opening)
                val control = Control.WithoutPosition(
                    opened = required(Opened(current = true)),
                    movement = optional(null),
                )

                val updated = control.updateWith(capability = update)

                expectThat(updated)
                    .isA<Control.WithoutPosition>()
                    .get(Control.WithoutPosition::movement)
                    .isA<Capability.Unsupported>()
            }

            test(name = "Updating an unsupported capability throws an exception") {
                val update = MeasureLoad(current = 50.percent)
                val control = Control.WithoutPosition(
                    opened = required(Opened(current = true)),
                    movement = optional(null),
                )

                expectThrows<IllegalStateException> { control.updateWith(capability = update) }
                    .hasMessage("Unsupported 'MeasureLoad' capability provided for 'Cover.Control.WithoutPosition'")
            }
        }
    }
}
