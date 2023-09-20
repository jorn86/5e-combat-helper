package org.hertsig.dnd.norr.spell

interface Time {
    fun display() = listOfNotNull(displayAmount(number(), unit()), condition()).joinToString()

    fun number(): Int
    fun unit(): String
    fun condition(): String?
}
