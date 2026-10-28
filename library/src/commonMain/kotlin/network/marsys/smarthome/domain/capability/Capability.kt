package network.marsys.smarthome.domain.capability

sealed class Capability {

    /**
     * Context is a set of additional information that can be associated with a capability.
     * It can be used to provide additional context for the capability, for example to
     * uniquely identify a capability in a specific context.
     */
    interface Context {
        operator fun <E : Element> get(key: Key<E>): E?
        operator fun plus(context: Context): Context

        operator fun contains(element: Element): Boolean =
            get(element.key) != null

        fun <R> fold(initial: R, operation: (R, Element) -> R): R

        interface Key<E : Element>

        interface Element : Context {
            val key: Key<*>

            @Suppress("UNCHECKED_CAST")
            override fun <E : Element> get(key: Key<E>): E? =
                takeIf { this.key == key } as? E

            override fun plus(context: Context): Context =
                context.fold(this as Context) { accumulator, element ->
                    CombinedContext(accumulator, element)
                }

            override fun <R> fold(initial: R, operation: (R, Element) -> R): R =
                operation(initial, this)
        }

        /**
         * Represents an empty context with no elements.
         */
        data object Empty : Context {
            override fun <E : Element> get(key: Key<E>): E? = null

            override fun plus(context: Context): Context = context

            override fun <R> fold(initial: R, operation: (R, Element) -> R): R = initial
        }

        /**
         * Represents a context that combines multiple elements into a single context.
         */
        private class CombinedContext(
            private val left: Context,
            private val element: Element,
        ) : Context {
            @Suppress("UseIfInsteadOfWhen")
            override fun <E : Element> get(key: Key<E>): E? {
                var current: Context = this

                while (true) {
                    current = when (current) {
                        is CombinedContext ->
                            current.element[key]?.let { return it } ?: current.left

                        else ->
                            return current[key]
                    }
                }
            }

            override fun plus(context: Context): Context =
                context.fold(this as Context) { accumulator, element ->
                    CombinedContext(accumulator, element)
                }

            override fun <R> fold(initial: R, operation: (R, Element) -> R): R =
                operation(left.fold(initial, operation), element)
        }
    }
}
