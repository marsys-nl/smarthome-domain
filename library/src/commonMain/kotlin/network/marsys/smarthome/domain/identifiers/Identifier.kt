package network.marsys.smarthome.domain.identifiers

import network.marsys.smarthome.domain.validation.Rules

interface Identifier {
    companion object {
        internal val rules = listOf(
            Rules.notBlank,
            Rules.containsLetter,
            Rules.startsWithLetter,
            Rules.endsWith(),
            Rules.containsOnly(),
            Rules.containsAtLeast(),
            Rules.noConsecutiveCharacters(),
            Rules.maxLength(),
        )
    }
}
