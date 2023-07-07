package org.hertsig.dnd.norr.spell

interface Range {
    fun display() = listOfNotNull(distance().display(), type().takeIf { it != "point" }).joinToString(" ")

    fun type(): String
    fun distance(): Amount
}
