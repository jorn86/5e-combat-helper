package org.hertsig.dnd.norr.spell

interface Time {
    fun display() = displayAmount(number(), unit())

    fun number(): Int
    fun unit(): String
}
