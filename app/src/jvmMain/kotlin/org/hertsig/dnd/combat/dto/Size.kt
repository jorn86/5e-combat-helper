package org.hertsig.dnd.combat.dto

enum class Size {
    TINY, SMALL, MEDIUM, LARGE, HUGE, GARGANTUAN;

    companion object {
        operator fun invoke(value: String) = when (value) {
            "T" -> TINY
            "S" -> SMALL
            "M" -> MEDIUM
            "L" -> LARGE
            "H" -> HUGE
            "G" -> GARGANTUAN
            else -> error("Unknown size $value")
        }
    }
}
