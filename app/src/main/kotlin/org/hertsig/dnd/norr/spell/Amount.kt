package org.hertsig.dnd.norr.spell

interface Amount {
    fun display() = listOfNotNull(amount(), when (val it = type()) {
        "feet" -> "ft."
        else -> it
    }).joinToString(" ")

    fun type(): String
    fun amount(): Int?
}
