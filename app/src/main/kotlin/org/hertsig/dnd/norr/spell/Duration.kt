package org.hertsig.dnd.norr.spell

import org.hertsig.dnd.norr.bestiary.NullToEmptyList
import org.hertsig.magic.Magic
import org.hertsig.magic.analyze

interface Duration {
    fun display(): String {
        val ends = ends()
        val duration = duration()
        return when {
            type() == "instant" -> "instantaneous"
            ends.isNotEmpty() -> ends.joinToString(" or ", transform = ::displayEnds)
            duration == null -> { analyze("Empty duration"); "" }
            concentration() == true -> "${duration.display()} (concentration)"
            else -> duration.display()
        }
    }

    fun type(): String
    fun duration(): Amount?
    @Magic(mapper = NullToEmptyList::class)
    fun ends(): List<String>
    fun concentration(): Boolean?
}

fun displayEnds(text: String) = when (text) {
    "dispel" -> "until dispelled"
    "trigger" -> "see description"
    else -> text
}
