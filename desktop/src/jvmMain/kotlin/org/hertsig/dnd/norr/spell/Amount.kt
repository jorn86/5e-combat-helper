package org.hertsig.dnd.norr.spell

import org.hertsig.util.plural

interface Amount {
    fun display() = displayAmount(amount(), type())

    fun amount(): Int?
    fun type(): String
}

internal fun displayAmount(amount: Int?, type: String): String {
    return when {
        amount == null -> type
        type == "feet" -> "$amount ft."
        type == "action" -> "Action"
        type == "bonus" -> "Bonus action"
        else -> plural(amount, type.trimEnd('s'))
    }
}
