package network.marsys.smarthome.domain.capability

import de.infix.testBalloon.framework.core.testSuite
import dev.nmarsman.expect.api.expectThat
import dev.nmarsman.expect.assertions.isA
import dev.nmarsman.expect.assertions.isFalse
import dev.nmarsman.expect.assertions.isNull
import dev.nmarsman.expect.assertions.isTrue

val CapabilityContextTest by testSuite(
    name = "Capability context tests",
) {
    test(name = "An empty context doesn't contain any elements") {
        expectThat(Example in Capability.Context.Empty)
            .isFalse()
    }

    test(name = "Adding an element to an empty context results in a context that contains said element") {
        expectThat(Capability.Context.Empty + Example)
            .isA<Example>()
    }

    test(name = "Adding an empty context to an element results ignores the empty context and returns the element") {
        expectThat(Example + Capability.Context.Empty)
            .isA<Example>()
    }

    test(name = "Adding two elements results in a context that contains both elements") {
        expectThat(Example + Another) {
            get { Example in this }.isTrue()
            get { Another in this }.isTrue()
        }
    }

    test(name = "Adding a third element to a combined context results in a context that contains all three elements") {
        expectThat(Example + Another + Third) {
            get { Example in this }.isTrue()
            get { Another in this }.isTrue()
            get { Third in this }.isTrue()
        }
    }

    test(name = "Combining two combined contexts results in a context that contains all elements") {
        expectThat((Example + Another) + (Third + Fourth)) {
            get { Example in this }.isTrue()
            get { Another in this }.isTrue()
            get { Third in this }.isTrue()
            get { Fourth in this }.isTrue()
        }
    }

    test(name = "Checking if an element is contained in a context that contains it returns true") {
        expectThat(Example in (Capability.Context.Empty + Example))
            .isTrue()
    }

    test(name = "Checking if an element is contained in a context that doesn't contain it returns false") {
        expectThat(Example in (Capability.Context.Empty + Another))
            .isFalse()
    }

    test(name = "Getting an element by key from an empty context returns null") {
        expectThat(Capability.Context.Empty[Example])
            .isNull()
    }

    test(name = "Getting an element by its own key returns the element") {
        expectThat(Example[Example])
            .isA<Example>()
    }

    test(name = "Getting an element by another key returns null") {
        expectThat(Example[Another])
            .isNull()
    }
}

private data object Example : Capability.Context.Element, Capability.Context.Key<Example> {
    override val key: Capability.Context.Key<Example>
        get() = Example
}

private data object Another : Capability.Context.Element, Capability.Context.Key<Another> {
    override val key: Capability.Context.Key<Another>
        get() = Another
}

private data object Third : Capability.Context.Element, Capability.Context.Key<Third> {
    override val key: Capability.Context.Key<Third>
        get() = Third
}

private data object Fourth : Capability.Context.Element, Capability.Context.Key<Fourth> {
    override val key: Capability.Context.Key<Fourth>
        get() = Fourth
}
