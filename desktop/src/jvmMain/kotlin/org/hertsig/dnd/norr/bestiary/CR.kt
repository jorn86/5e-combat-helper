package org.hertsig.dnd.norr.bestiary

import org.hertsig.magic.Mapper

interface CR {
    fun cr(): String
    fun lair(): String?
}

object ChallengeRatingMapper : Mapper {
    override fun invoke(value: Any?) = if (value is String) mapOf("cr" to value) else value
}
