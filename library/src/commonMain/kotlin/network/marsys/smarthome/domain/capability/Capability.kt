package network.marsys.smarthome.domain.capability

sealed class Capability<T : Any, S : Capability<T, S>> {
    abstract val current: T
    abstract val context: Context
    abstract val type: Type<S>

    protected abstract fun updateWith(
        value: T,
        context: Context,
    ): S

    fun updateWith(value: T): S =
        updateWith(value = value, context = context)

    infix fun with(context: Context): S =
        updateWith(value = current, context = this.context + context)

    /*
     * Context
     */

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

    /*
     * Constraints
     */

    /**
     * Represents a constraint on a capability.
     */
    sealed interface Constraint<out C : Capability<*, *>>

    /**
     * Represents a constraint that indicates that the capability is present and has a value.
     */
    sealed interface Present<out C : Capability<*, *>> : Constraint<C> {
        val value: C
    }

    /**
     * Represents a constraint that indicates that the capability is required and must have a value.
     */
    data class Required<C : Capability<*, *>>(
        override val value: C,
    ) : Constraint<C>, Present<C> {
        fun updateWith(capability: @UnsafeVariance C): Required<C> =
            copy(value = capability)
    }

    /**
     * Represents a constraint that indicates that the capability is optional and may or may not have a value.
     */
    sealed class Optional<out C : Capability<*, *>> : Constraint<C> {
        @Suppress("UNCHECKED_CAST")
        fun updateWith(capability: @UnsafeVariance C): Optional<C> =
            when (this) {
                is Unsupported -> this
                is Available<*> -> update(capability = capability)
            } as Optional<C>
    }

    /**
     * Represents a constraint that indicates that the capability is unsupported and does not have a value.
     */
    data object Unsupported : Optional<Nothing>()

    /**
     * Represents a constraint that indicates that the capability is available and has a value.
     */
    data class Available<C : Capability<*, *>>(
        override val value: C,
    ) : Optional<C>(), Present<C> {
        @Suppress("UNCHECKED_CAST")
        internal fun update(capability: Capability<*, *>): Available<C> =
            copy(value = capability as C)
    }

    companion object {
        fun <T : Capability<*, *>> required(value: T) = Required(value)
        fun <T : Capability<*, *>> optional(value: T?) = when (value) {
            null -> Unsupported
            else -> Available(value)
        }
    }

    /*
     * Type
     */

    interface Type<C : Capability<*, *>>
}
