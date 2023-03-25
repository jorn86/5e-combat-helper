package org.hertsig.dnd.norr

import org.hertsig.magic.Mapper

object SingleToListMapper: Mapper {
    override fun invoke(value: Any?) = when (value) {
        null -> emptyList()
        !is List<*> -> listOf(value)
        else -> value
    }
}
