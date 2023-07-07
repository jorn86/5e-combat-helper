package org.hertsig.dnd.norr.bestiary

import org.hertsig.magic.Magic
import org.hertsig.magic.Mapper

interface Speeds {
    @Magic(mapper = SpeedMapper::class) fun burrow(): Speed?
    @Magic(mapper = SpeedMapper::class) fun climb(): Speed?
    @Magic(mapper = SpeedMapper::class) fun fly(): Speed?
    @Magic(mapper = SpeedMapper::class) fun swim(): Speed?
    @Magic(mapper = SpeedMapper::class) fun walk(): Speed?
}

interface Speed {
    fun number(): Int
    fun condition(): String?
    fun cond(): String? // ?

    fun display(): String {
        val condition = condition() ?: cond()
        val text = "${number()} ft."
        return if (condition.isNullOrBlank()) text else "$text $condition"
    }
}

object SpeedMapper: Mapper {
    override fun invoke(value: Any?) = if (value is Int) mapOf("number" to value) else value
}
