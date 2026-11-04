package network.marsys.smarthome.domain.zone

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isEqualTo
import dev.nmarsman.expect.assertions.isNull
import network.marsys.smarthome.domain.TranslationKey
import network.marsys.smarthome.domain.entity.EntityNaming
import network.marsys.smarthome.domain.entity.Light

val ZoneTest by testSuite(
    name = "Zone tests",
) {
    testSuite(
        name = "Entity naming",
    ) {
        test(name = "When a zone is provided, a merged naming key is used") {
            val zone = Zone(
                identifier = ZoneIdentifier("zone.bedroom"),
                name = ZoneNaming(key = TranslationKey("bedroom")),
            )

            expectThat(zone.entity(type = Light))
                .get(EntityNaming::key)
                .get(TranslationKey::value)
                .isEqualTo("light.bedroom")
        }

        test(name = "When no qualifier key is provided, the qualifier key is null") {
            val zone = Zone(
                identifier = ZoneIdentifier("zone.bedroom"),
                name = ZoneNaming(key = TranslationKey("bedroom")),
            )

            expectThat(zone.entity(type = Light))
                .get(EntityNaming::qualifier)
                .isNull()
        }

        test(name = "When a qualifier key is provided, the qualifier key is filled") {
            val zone = Zone(
                identifier = ZoneIdentifier("zone.bedroom"),
                name = ZoneNaming(key = TranslationKey("bedroom")),
            )

            expectThat(zone.entity(type = Light, qualifier = TranslationKey("ceiling")))
                .get(EntityNaming::qualifier)
                .isA<TranslationKey>()
                .get(TranslationKey::value)
                .isEqualTo("ceiling")
        }
    }
}
