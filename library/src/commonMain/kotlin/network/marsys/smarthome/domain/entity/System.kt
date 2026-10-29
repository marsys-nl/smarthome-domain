package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.Capability.Context
import network.marsys.smarthome.domain.capability.MeasureDataSize
import network.marsys.smarthome.domain.capability.MeasureLoad
import network.marsys.smarthome.domain.capability.MeasureTemperature
import network.marsys.smarthome.domain.i18n.TranslationKey
import kotlin.time.Instant

data class System(
    override val identifier: EntityIdentifier,
    override val naming: EntityNaming = System.naming(),
    override val state: Entity.State = State.Unknown,
) : Entity {
    sealed interface State : Entity.State {
        data class Known(
            val host: Host,
            val processor: Processor,
            val memory: Memory,
            val startedAt: StartedAt,
        ) : State, Entity.State.Known {
            override fun find(type: Capability.Type<*>, context: Context): Capability<*, *>? =
                when (type) {
                    MeasureDataSize if MemoryType.Total in context ->
                        memory.total.value

                    MeasureDataSize if MemoryType.Available in context ->
                        memory.available.value

                    MeasureDataSize if MemoryType.SwapTotal in context ->
                        memory.swap.total.value

                    MeasureDataSize if MemoryType.SwapAvailable in context ->
                        memory.swap.available.value

                    MeasureDataSize ->
                        error("Context is required for 'measure data size' capability")

                    MeasureLoad ->
                        processor.load.value

                    MeasureTemperature if processor.temperature is Capability.Available<*> ->
                        processor.temperature.value

                    else -> null
                }

            override fun updateWith(
                capability: Capability<*, *>,
            ): Entity.State = when (capability) {
                is MeasureDataSize if MemoryType.Total in capability.context ->
                    copy(memory = memory.copy(total = memory.total.updateWith(capability)))

                is MeasureDataSize if MemoryType.Available in capability.context ->
                    copy(memory = memory.copy(available = memory.available.updateWith(capability)))

                is MeasureDataSize if MemoryType.SwapTotal in capability.context ->
                    copy(
                        memory = memory.copy(
                            swap = memory.swap.copy(
                                total = memory.swap.total.updateWith(capability),
                            ),
                        ),
                    )

                is MeasureDataSize if MemoryType.SwapAvailable in capability.context ->
                    copy(
                        memory = memory.copy(
                            swap = memory.swap.copy(
                                available = memory.swap.available.updateWith(capability),
                            ),
                        ),
                    )

                is MeasureDataSize ->
                    error("Context is required for 'measure data size' capability")

                is MeasureLoad ->
                    copy(processor = processor.copy(load = processor.load.updateWith(capability)))

                is MeasureTemperature ->
                    copy(processor = processor.copy(temperature = processor.temperature.updateWith(capability)))

                else -> unsupportedCapabilityError(capability = capability)
            }
        }

        data object Unknown : State, Entity.State.Unknown
    }

    data class Host(
        val device: Device,
        val operatingSystem: OperatingSystem,
    ) {
        data class Device(
            val manufacturer: String,
            val model: String,
            val architecture: String,
            val physicalCores: Int,
            val logicalCores: Int,
        )

        data class OperatingSystem(
            val description: String,
            val family: String,
            val version: String?,
            val bitness: Int,
        )
    }

    data class StartedAt(
        val host: Instant,
        val application: Instant,
    )

    data class Processor(
        val load: Capability.Required<MeasureLoad>,
        val temperature: Capability.Optional<MeasureTemperature>,
    )

    data class Memory(
        val total: Capability.Required<MeasureDataSize>,
        val available: Capability.Required<MeasureDataSize>,
        val swap: Swap,
    ) {
        data class Swap(
            val total: Capability.Required<MeasureDataSize>,
            val available: Capability.Required<MeasureDataSize>,
        )
    }

    sealed interface MemoryType {
        data object Total : MemoryType, Context.Element, Context.Key<Total> {
            override val key: Context.Key<Total> = Total
        }

        data object Available : MemoryType, Context.Element, Context.Key<Available> {
            override val key: Context.Key<Available> = Available
        }

        data object SwapTotal : MemoryType, Context.Element, Context.Key<SwapTotal> {
            override val key: Context.Key<SwapTotal> = SwapTotal
        }

        data object SwapAvailable : MemoryType, Context.Element, Context.Key<SwapAvailable> {
            override val key: Context.Key<SwapAvailable> = SwapAvailable
        }
    }

    companion object : Entity.Type<System> {
        override val defaultNamingKey = TranslationKey("system")
    }
}
