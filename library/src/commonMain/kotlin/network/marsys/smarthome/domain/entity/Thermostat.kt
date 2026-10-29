package network.marsys.smarthome.domain.entity

import network.marsys.smarthome.domain.capability.Capability
import network.marsys.smarthome.domain.capability.Capability.Context
import network.marsys.smarthome.domain.capability.ChildLock
import network.marsys.smarthome.domain.capability.MeasureDataSize
import network.marsys.smarthome.domain.capability.MeasureTemperature
import network.marsys.smarthome.domain.capability.OnOff
import network.marsys.smarthome.domain.capability.ScheduledMode
import network.marsys.smarthome.domain.capability.TargetTemperature
import network.marsys.smarthome.domain.capability.ThermostatMode
import network.marsys.smarthome.domain.capability.WindowDetection
import network.marsys.smarthome.domain.i18n.TranslationKey

data class Thermostat(
    override val identifier: EntityIdentifier,
    override val naming: EntityNaming = Thermostat.naming(),
    override val state: Entity.State = State.Unknown,
) : Entity, Entity.Activatable {
    override val active: Boolean
        get() = state is State.Known && state.onOff.value.current

    sealed interface State : Entity.State {
        data class Known(
            val onOff: Capability.Required<OnOff>,
            val mode: Capability.Required<ThermostatMode>,
            val temperatures: Temperatures,
            val childLock: Capability.Optional<ChildLock> = Capability.Unsupported,
            val windowDetection: Capability.Optional<WindowDetection> = Capability.Unsupported,
            val scheduledMode: Capability.Optional<ScheduledMode> = Capability.Unsupported,
        ) : State, Entity.State.Known {
            override fun find(type: Capability.Type<*>, context: Context): Capability<*, *>? =
                when (type) {
                    OnOff -> onOff.value

                    ThermostatMode -> mode.value

                    TargetTemperature -> temperatures.target.value

                    MeasureTemperature if TemperatureType.Current in context ->
                        temperatures.current.value

                    MeasureTemperature if TemperatureType.Outdoor in context &&
                        temperatures.outdoor is Capability.Available<MeasureTemperature> ->
                        temperatures.outdoor.value

                    MeasureTemperature if TemperatureType.Outdoor !in context ->
                        error("Context is required for 'measure temperature' capability")

                    ChildLock if childLock is Capability.Available<ChildLock> ->
                        childLock.value

                    WindowDetection if windowDetection is Capability.Available<WindowDetection> ->
                        windowDetection.value

                    ScheduledMode if scheduledMode is Capability.Available<ScheduledMode> ->
                        scheduledMode.value

                    else -> null
                }

            override fun updateWith(
                capability: Capability<*, *>,
            ): Entity.State = when (capability) {
                is OnOff ->
                    copy(onOff = onOff.updateWith(capability = capability))

                is ThermostatMode ->
                    copy(mode = mode.updateWith(capability = capability))

                is TargetTemperature ->
                    copy(temperatures = temperatures.copy(target = temperatures.target.updateWith(capability)))

                is MeasureTemperature if TemperatureType.Current in capability.context ->
                    copy(temperatures = temperatures.copy(current = temperatures.current.updateWith(capability)))

                is MeasureTemperature if TemperatureType.Outdoor in capability.context ->
                    copy(temperatures = temperatures.copy(outdoor = temperatures.outdoor.updateWith(capability)))

                is MeasureTemperature ->
                    error("Context is required for 'measure temperature' capability")

                is ChildLock ->
                    copy(childLock = childLock.updateWith(capability))

                is WindowDetection ->
                    copy(windowDetection = windowDetection.updateWith(capability))

                is ScheduledMode ->
                    copy(scheduledMode = scheduledMode.updateWith(capability))

                else -> unsupportedCapabilityError(capability)
            }
        }

        data object Unknown : State, Entity.State.Unknown
    }

    data class Temperatures(
        val target: Capability.Required<TargetTemperature>,
        val current: Capability.Required<MeasureTemperature>,
        val outdoor: Capability.Optional<MeasureTemperature> = Capability.Unsupported,
    )

    sealed interface TemperatureType {
        data object Current : TemperatureType, Context.Element, Context.Key<Current> {
            override val key: Context.Key<*> = Current
        }

        data object Outdoor : TemperatureType, Context.Element, Context.Key<Outdoor> {
            override val key: Context.Key<*> = Outdoor
        }
    }

    companion object : Entity.Type<Thermostat> {
        override val defaultNamingKey = TranslationKey("thermostat")
    }
}
