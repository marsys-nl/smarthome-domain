package network.marsys.smarthome.domain.validation

import network.marsys.smarthome.domain.identifiers.IntegrationIdentifier

internal fun interface Rule {
    fun check(value: String, subject: String)
}

internal object Rules {
    val notBlank = Rule { value, subject ->
        require(value.isNotBlank()) {
            "$subject cannot be empty or blank."
        }
    }

    val containsLetter = Rule { value, subject ->
        require(value.any { it.isLetter() }) {
            "$subject must contain at least one letter."
        }
    }

    val startsWithLetter = Rule { value, subject ->
        require(value.first().isLetter()) {
            "$subject must start with a letter."
        }
    }

    fun endsWith(
        allowed: Collection<Char> = LETTERS + DIGITS,
        description: String = "a letter or digit",
    ) = Rule { value, subject ->
        require(value.last() in allowed) {
            "$subject must end with $description."
        }
    }

    fun containsOnly(
        allowed: Collection<Char> = ALLOWED_CHARS,
        description: String = "letters, digits, dashes, and dots",
    ) = Rule { value, subject ->
        require(value.all { it in allowed }) {
            "$subject can only contain $description."
        }
    }

    fun containsAtLeast(
        required: Int = 4,
        allowed: Collection<Char> = ALLOWED_CHARS,
        description: String = "allowed characters",
    ) = Rule { value, subject ->
        require(value.count { it in allowed } >= required) {
            "$subject must contain at least $required $description."
        }
    }

    fun maxLength(
        @Suppress("MagicNumber")
        max: Int = 255,
    ) = Rule { value, subject ->
        require(value.length <= max) {
            "$subject cannot be longer than $max characters."
        }
    }

    fun minLength(
        min: Int = 4,
        description: String = "be at least $min characters long",
    ) = Rule { value, subject ->
        require(value.length > min) {
            "$subject must $description."
        }
    }

    fun noConsecutiveCharacters(
        characters: Collection<Char> = SEPARATOR_CHARS,
        description: String = "separator characters (dots or dashes)",
    ) = Rule { value, subject ->
        require(
            value
                .zipWithNext()
                .none { it.first in characters && it.second in characters },
        ) {
            "$subject should not contain consecutive $description."
        }
    }

    fun startWithPrefix(
        prefix: String,
    ) = Rule { value, subject ->
        require(value.startsWith(prefix)) {
            "$subject must start with '$prefix'."
        }
    }

    fun notStartWithPrefix(
        prefixes: Collection<String> = emptyList(),
        description: String = "a reserved prefix",
    ) = Rule { value, subject ->
        require(
            value = prefixes
                .none { value.startsWith(it) },
        ) {
            "$subject cannot start with $description (${prefixes.first { value.startsWith(it) }})."
        }
    }

    fun validate(
        value: String,
        subject: String,
        rules: Collection<Rule>,
    ) = rules.forEach { it.check(value, subject) }
}

internal val RESERVED_NAMESPACES = listOf(
    IntegrationIdentifier.PREFIX,
)

internal val LETTERS = 'a'..'z'
internal val DIGITS = '0'..'9'
internal val SEPARATOR_CHARS = listOf('.', '-')
internal val ALLOWED_CHARS = LETTERS + DIGITS + SEPARATOR_CHARS
