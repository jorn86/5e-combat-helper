package org.hertsig.dnd.norr.spell

interface Time {
    fun display(): String {
        return "${number()} " + when (val it = unit().trim()) {
            "bonus" -> "bonus action"
            else -> it
        }
    }

    fun number(): Int
    fun unit(): String
}
