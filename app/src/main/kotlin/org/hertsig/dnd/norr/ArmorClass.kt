package org.hertsig.dnd.norr

import org.hertsig.magic.Magic
import org.hertsig.magic.Mapper

@Magic(mapper = ArmorClassMapper::class)
interface ArmorClass {
    fun ac(): Int
    fun from(): List<Any>?

    fun display(): String {
        val sources = from().orEmpty()
        val source = if (sources.isEmpty()) "" else sources.joinToString(", ", " (", ")")
        return "${ac()}$source"
    }
}

object ArmorClassMapper: Mapper {
    override fun invoke(value: Any?) = if (value is Int) mapOf("ac" to value) else value
}
