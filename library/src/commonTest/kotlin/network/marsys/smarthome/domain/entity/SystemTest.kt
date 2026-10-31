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
import network.marsys.smarthome.domain.capability.MeasureDataSize
import network.marsys.smarthome.domain.capability.MeasureLoad
import network.marsys.smarthome.domain.capability.MeasureTemperature
import network.marsys.smarthome.domain.capability.OnOff
import network.marsys.smarthome.domain.unit.celsius
import network.marsys.smarthome.domain.unit.gigabytes
import network.marsys.smarthome.domain.unit.percent
import kotlin.time.Instant

val SystemTest by testSuite(
    name = "System tests",
) {
    test(name = "Constructing a system with no naming key uses the default naming key") {
        val entity = System(
            identifier = EntityIdentifier("test.system"),
        )

        expectThat(entity)
            .get(Entity::naming)
            .get(EntityNaming::key)
            .isEqualTo(System.defaultNamingKey)
    }

    test(name = "Constructing a system with no explicit state uses the unknown state") {
        val entity = System(
            identifier = EntityIdentifier("test.system"),
        )

        expectThat(entity)
            .get(Entity::state)
            .isA<Entity.State.Unknown>()
    }

    testSuite(
        name = "Getting capability from state",
    ) {
        test(name = "Getting 'measure load' capability from known state succeeds") {
            val state = System.known()

            expectThat(state)
                .get { get(MeasureLoad) }
                .isA<MeasureLoad>()
        }

        test(name = "Getting 'measure temperature' capability from known state succeeds") {
            val state = System.known()

            expectThat(state)
                .get { get(MeasureTemperature) }
                .isA<MeasureTemperature>()
        }

        test(name = "Getting 'measure temperature' capability from known state returns null if it is not available") {
            val state = System.known(
                processor = System.processor(
                    temperature = optional(null),
                ),
            )

            expectThat(state)
                .get { get(MeasureTemperature) }
                .isNull()
        }

        test(name = "Getting 'measure data size' capability from known state without context fails") {
            val state = System.known()

            expectThrows<IllegalStateException> { state.get(MeasureDataSize) }
                .hasMessage("Context is required for 'measure data size' capability")
        }

        test(name = "Getting 'measure data size' with total memory type context capability from known state succeeds") {
            val state = System.known()

            expectThat(state)
                .get { get(MeasureDataSize, context = System.MemoryType.Total) }
                .isA<MeasureDataSize>()
        }

        test(name = "Getting 'measure data size' with available memory type context capability from known state succeeds") {
            val state = System.known()

            expectThat(state)
                .get { get(MeasureDataSize, context = System.MemoryType.Available) }
                .isA<MeasureDataSize>()
        }

        test(name = "Getting 'measure data size' with swap total memory type context capability from known state succeeds") {
            val state = System.known()

            expectThat(state)
                .get { get(MeasureDataSize, context = System.MemoryType.SwapTotal) }
                .isA<MeasureDataSize>()
        }

        test(name = "Getting 'measure data size' with swap available memory type context capability from known state succeeds") {
            val state = System.known()

            expectThat(state)
                .get { get(MeasureDataSize, context = System.MemoryType.SwapAvailable) }
                .isA<MeasureDataSize>()
        }

        test(name = "Getting an unknown capability from system known state returns null") {
            val state = System.known()

            expectThat(state)
                .get { get(OnOff) }
                .isNull()
        }
    }

    testSuite(
        name = "Updating state",
    ) {
        test(name = "Updating the memory total 'measure data size' capability succeeds") {
            val update = MeasureDataSize(current = 16.gigabytes) with System.MemoryType.Total
            val state = System.known()

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                memory = state.memory.copy(
                    total = state.memory.total
                        .updateWith(capability = update),
                ),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the memory available 'measure data size' capability succeeds") {
            val update = MeasureDataSize(current = 4.gigabytes) with System.MemoryType.Available
            val state = System.known()

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                memory = state.memory.copy(
                    available = state.memory.available
                        .updateWith(capability = update),
                ),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the swap memory total 'measure data size' capability succeeds") {
            val update = MeasureDataSize(current = 8.gigabytes) with System.MemoryType.SwapTotal
            val state = System.known()

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                memory = state.memory.copy(
                    swap = state.memory.swap.copy(
                        total = state.memory.swap.total
                            .updateWith(capability = update),
                    ),
                ),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the swap memory available 'measure data size' capability succeeds") {
            val update = MeasureDataSize(current = 12.gigabytes) with System.MemoryType.SwapAvailable
            val state = System.known()

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                memory = state.memory.copy(
                    swap = state.memory.swap.copy(
                        available = state.memory.swap.available
                            .updateWith(capability = update),
                    ),
                ),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the 'measure data size' capability without context fails") {
            val update = MeasureDataSize(current = 16.gigabytes)
            val state = System.known()

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Context is required for 'measure data size' capability")
        }

        test(name = "Updating the 'measure load' capability succeeds") {
            val update = MeasureLoad(current = 75.percent)
            val state = System.known()

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                processor = state.processor.copy(
                    load = state.processor.load
                        .updateWith(capability = update),
                ),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating the 'measure temperature' capability succeeds") {
            val update = MeasureTemperature(current = 25.celsius)
            val state = System.known()

            val updated = state.updateWith(capability = update)
            val expected = state.copy(
                processor = state.processor.copy(
                    temperature = state.processor.temperature
                        .updateWith(capability = update),
                ),
            )

            expectThat(updated)
                .isEqualTo(expected)
        }

        test(name = "Updating an unsupported capability throws an exception") {
            val update = OnOff(current = true)
            val state = System.known()

            expectThrows<IllegalStateException> { state.updateWith(capability = update) }
                .hasMessage("Unsupported 'OnOff' capability provided for 'System.State.Known'")
        }
    }
}

private fun System.Companion.known(
    host: System.Host = System.host(),
    processor: System.Processor = System.processor(),
    memory: System.Memory = System.memory(),
    startedAt: System.StartedAt = System.startedAt(),
) = System.State.Known(
    host = host,
    processor = processor,
    memory = memory,
    startedAt = startedAt,
)

private fun System.Companion.host(
    device: System.Host.Device = System.device(),
    operatingSystem: System.Host.OperatingSystem = System.operatingSystem(),
) = System.Host(
    device = device,
    operatingSystem = operatingSystem,
)

private fun System.Companion.device(
    manufacturer: String = "manufacturer",
    model: String = "model",
    architecture: String = "x64",
    physicalCores: Int = 4,
    logicalCores: Int = 8,
) = System.Host.Device(
    manufacturer = manufacturer,
    model = model,
    architecture = architecture,
    physicalCores = physicalCores,
    logicalCores = logicalCores,
)

private fun System.Companion.operatingSystem(
    description: String = "description",
    family: String = "family",
    version: String? = "version",
    bitness: Int = 64,
) = System.Host.OperatingSystem(
    description = description,
    family = family,
    version = version,
    bitness = bitness,
)

private fun System.Companion.processor(
    load: Capability.Required<MeasureLoad> = required(MeasureLoad(current = 50.percent)),
    temperature: Capability.Optional<MeasureTemperature> = optional(MeasureTemperature(current = 30.celsius)),
) = System.Processor(
    load = load,
    temperature = temperature,
)

private fun System.Companion.memory(
    total: Capability.Required<MeasureDataSize> = required(MeasureDataSize(current = 8.gigabytes)),
    available: Capability.Required<MeasureDataSize> = required(MeasureDataSize(current = 4.gigabytes)),
    swap: System.Memory.Swap = System.swap(),
) = System.Memory(
    total = total,
    available = available,
    swap = swap,
)

private fun System.Companion.swap(
    total: Capability.Required<MeasureDataSize> = required(MeasureDataSize(current = 2.gigabytes)),
    available: Capability.Required<MeasureDataSize> = required(MeasureDataSize(current = 1.gigabytes)),
) = System.Memory.Swap(
    total = total,
    available = available,
)

private fun System.Companion.startedAt(
    host: Instant = Instant.parse("2024-01-01T00:00:00Z"),
    application: Instant = Instant.parse("2024-01-01T00:00:00Z"),
) = System.StartedAt(
    host = host,
    application = application,
)
