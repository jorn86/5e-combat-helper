package org.hertsig.dnd.norr.spell

import org.hertsig.magic.Mapper

interface MaterialComponent {
    fun text(): String
    fun cost(): Int?
}

object MaterialComponentMapper: Mapper {
    override fun invoke(value: Any?) = if (value is String) mapOf("text" to value) else value
}
