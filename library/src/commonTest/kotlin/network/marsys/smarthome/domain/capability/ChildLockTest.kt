package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isTrue

val ChildLockTest by testSuite(
    name = "Child lock capability tests",
) {
    test(name = "Updating the child lock capability preserves the context") {
        val capability = ChildLock(current = true, context = Example)

        expectThat(capability.updateWith(value = false))
            .get(ChildLock::context)
            .isA<Example>()
    }

    test(name = "Updating the child lock capability mutates the value") {
        val capability = ChildLock(current = true, context = Example)
        val updated = capability.updateWith(value = false)

        expectThat(updated)
            .get(ChildLock::current)
            .isFalse()
    }

    test(name = "Updating the child lock capability does not mutate the original capability") {
        val capability = ChildLock(current = true, context = Example)
        capability.updateWith(value = false)

        expectThat(capability)
            .get(ChildLock::current)
            .isTrue()
    }

    test(name = "Child lock capability can be created with default context") {
        expectThat(ChildLock(current = true))
            .get(ChildLock::context)
            .isA<Capability.Context.Empty>()
    }

    test(name = "Child lock capability can be associated with a context") {
        expectThat(ChildLock(current = true) with Example)
            .get(ChildLock::context)
            .isA<Example>()
    }
}
