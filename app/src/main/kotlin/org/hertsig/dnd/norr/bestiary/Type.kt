package org.hertsig.dnd.norr.bestiary

import org.hertsig.dnd.combat.element.cap
import org.hertsig.magic.Mapper

interface Type {
    fun type(): String
    fun tags(): List<Any>?

    fun display(): String {
        val tags = tags()
        return if (!tags.isNullOrEmpty()) {
            tags.joinToString(", ", "${type()} (", ")").cap()
        } else {
            type()
        }.cap()
    }
}

object TypeMapper: Mapper {
    override fun invoke(value: Any?) = if (value is String) mapOf("type" to value) else value
}
