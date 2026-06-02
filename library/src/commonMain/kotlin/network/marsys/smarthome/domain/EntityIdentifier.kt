package network.marsys.smarthome.domain

import kotlin.jvm.JvmInline

@JvmInline
value class EntityIdentifier(val value: String) {
    init {
        require(value.isNotBlank()) {
            "Entity identifier cannot be empty or blank."
        }

        require(value.any { it.isLetter() }) {
            "Entity identifier must contain at least one letter."
        }

        require(value.first().isLetter() && value.last().isLetter()) {
            "Entity identifier must start and end with a letter."
        }

        require(value.all { it in ALLOWED_CHARS }) {
            "Entity identifier can only contain letters, digits, dashes, and dots."
        }

        require(value.count { it in ALLOWED_CHARS } >= REQUIRED_ALLOWED_CHARS) {
            "Entity identifier must contain at least ${REQUIRED_ALLOWED_CHARS} allowed characters."
        }

        require(
            value = value
                .zipWithNext()
                .none { it.first in SEPARATOR_CHARS && it.second in SEPARATOR_CHARS },
        ) {
            "Entity identifier should not contain consecutive separator characters (dots or dashes)."
        }

        require(value.length <= MAX_IDENTIFIER_LENGTH) {
            "Entity identifier cannot be longer than ${MAX_IDENTIFIER_LENGTH} characters."
        }
    }

    override fun toString(): String = value

    companion object {
        private const val REQUIRED_ALLOWED_CHARS = 5
        private const val MAX_IDENTIFIER_LENGTH = 255
        private val SEPARATOR_CHARS = listOf('.', '-')
        private val ALLOWED_CHARS = ('a'..'z') + ('0'..'9') + SEPARATOR_CHARS
    }
}
