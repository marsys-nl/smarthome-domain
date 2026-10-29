package network.marsys.smarthome.domain.entity

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isNotNull
import dev.nmarsman.expect.assertions.isNull
import network.marsys.smarthome.domain.capability.OnOff
import network.marsys.smarthome.domain.i18n.TranslationKey

val EntityTest by testSuite(
    name = "Entity tests",
) {
    testSuite(
        name = "Unknown entity state",
    ) {
        test(name = "Update on unknown entity state returns unknown entity state") {
            val update = OnOff(current = true)
            val state: Entity.State = Light.State.Unknown

            expectThat(state.updateWith(update))
                .isA<Entity.State.Unknown>()
        }

        test(name = "Trying to find an capability on unknown entity state returns null") {
            val state: Entity.State = Light.State.Unknown

            expectThat(state.find(OnOff))
                .isNull()
        }
    }

    testSuite(
        name = "Entity naming",
    ) {
        test(name = "When no naming key is provided, the default naming key is used") {
            expectThat(Light.naming())
                .get(EntityNaming::key)
                .isEqualTo(Light.defaultNamingKey)
        }

        test(name = "When a naming key is provided, the given naming key is used") {
            expectThat(Light.naming(key = TranslationKey("custom.key")))
                .get(EntityNaming::key)
                .get(TranslationKey::value)
                .isEqualTo("custom.key")
        }

        test(name = "When no qualifier is provided, the qualifier is null") {
            expectThat(Light.naming())
                .get(EntityNaming::qualifier)
                .isNull()
        }

        test(name = "When a qualifier is provided, the given qualifier is used") {
            expectThat(Light.naming(qualifier = TranslationKey("custom.qualifier")))
                .get(EntityNaming::qualifier)
                .isNotNull()
                .get(TranslationKey::value)
                .isEqualTo("custom.qualifier")
        }
    }
}
