package network.marsys.smarthome.domain.capability

import network.marsys.smarthome.domain.unit.Dimension
import network.marsys.smarthome.domain.unit.Quantity

data class ThermostatStatus(
    override val current: Status,
    override val context: Context = Context.Empty,
) : Capability<ThermostatStatus.Status, ThermostatStatus>() {
    override val type: Type<ThermostatStatus>
        get() = ThermostatStatus

    override fun updateWith(value: Status, context: Context) =
        copy(
            current = value,
            context = context,
        )

    sealed interface Status {
        data object Off : Status
        data object Idle : Status
        data object Heating : Status
        data object Cooling : Status
    }

    companion object : Type<ThermostatStatus> {
        fun compute(
            onOff: OnOff,
            mode: ThermostatMode,
            targetTemperature: Quantity<Dimension.Temperature>,
            currentTemperature: Quantity<Dimension.Temperature>,
        ): Status {
            if (!onOff.current) {
                return Status.Off
            }

            return when (mode.current) {
                ThermostatMode.Mode.Heat -> when {
                    currentTemperature < targetTemperature -> Status.Heating
                    else -> Status.Idle
                }

                ThermostatMode.Mode.Cool -> when {
                    currentTemperature > targetTemperature -> Status.Cooling
                    else -> Status.Idle
                }

                ThermostatMode.Mode.Auto -> when {
                    currentTemperature < targetTemperature -> Status.Heating
                    currentTemperature > targetTemperature -> Status.Cooling
                    else -> Status.Idle
                }
            }
        }
    }
}
