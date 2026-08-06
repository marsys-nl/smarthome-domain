package network.marsys.smarthome.domain.identifiers

internal data object IdentifierRules {
    private const val REQUIRED_ALLOWED_CHARS = 5
    private const val MAX_IDENTIFIER_LENGTH = 255
    private val SEPARATOR_CHARS = listOf('.', '-')
    private val ALLOWED_CHARS = ('a'..'z') + ('0'..'9') + SEPARATOR_CHARS

    fun validate(value: String, identifierType: String) {
        require(value.isNotBlank()) {
            "$identifierType cannot be empty or blank."
        }

        require(value.any { it.isLetter() }) {
            "$identifierType must contain at least one letter."
        }

        require(value.first().isLetter() && value.last().isLetter()) {
            "$identifierType must start and end with a letter."
        }

        require(value.all { it in ALLOWED_CHARS }) {
            "$identifierType can only contain letters, digits, dashes, and dots."
        }

        require(value.count { it in ALLOWED_CHARS } >= REQUIRED_ALLOWED_CHARS) {
            "$identifierType must contain at least ${REQUIRED_ALLOWED_CHARS} allowed characters."
        }

        require(
            value = value
                .zipWithNext()
                .none { it.first in SEPARATOR_CHARS && it.second in SEPARATOR_CHARS },
        ) {
            "$identifierType should not contain consecutive separator characters (dots or dashes)."
        }

        require(value.length <= MAX_IDENTIFIER_LENGTH) {
            "$identifierType cannot be longer than ${MAX_IDENTIFIER_LENGTH} characters."
        }
    }
}
