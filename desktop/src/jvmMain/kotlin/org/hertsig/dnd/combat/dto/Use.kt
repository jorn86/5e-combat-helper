package org.hertsig.dnd.combat.dto

sealed interface Use {
    val amount: Int
    val reset: String
    val display: String

    object Unlimited: Use {
        override val amount get() = 0
        override val reset = "unlimited"
        override val display = ""
        override fun toString() = "unlimited"
    }

    data class Limited(override val amount: Int, override val reset: String): Use {
        override val display = "(${this})"
        override fun toString() = "$amount/$reset"
    }

    companion object {
        fun parse(string: String): Use {
            return if (string.contains('/')) {
                val amount = string.substringBefore('/').toInt()
                Limited(amount, string.substringAfter('/'))
            } else {
                Unlimited
            }
        }
    }
}
