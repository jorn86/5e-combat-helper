package org.hertsig.dnd.dice

import kotlin.math.absoluteValue

data class Dice(private val sizes: List<Int>, val modifier: Int = 0, val type: String = "") {
    val average get() = sizes.sumOf { it + 1 } / 2.0 + modifier
    fun roll() = DieRolls(sizes.map { DieRoll.roll(it) }, modifier, type)

    operator fun plus(dice: Dice) = Dice(sizes + dice.sizes, modifier + dice.modifier, type.ifBlank { dice.type })
    operator fun plus(modifier: Int) = copy(modifier = this.modifier + modifier)
    operator fun minus(modifier: Int) = copy(modifier = this.modifier - modifier)
    operator fun invoke(type: String) = copy(type = type)
    fun doubleDice() = copy(sizes = sizes + sizes)

    fun asString(withAverage: Boolean = true) = sizes.groupBy { it }.map { (size, sizes) -> "${sizes.size}d$size" }
        .joinToString(" + ", postfix = "${modifier(modifier)}${average(withAverage)} $type").trim()

    private fun average(withAverage: Boolean) = if (withAverage) " (${average.toInt()})" else ""

    override fun toString() = asString()

    companion object {
        val none = Dice(listOf())

        fun parseOptional(string: String): Dice? {
            if (string.isBlank()) return null
            return parse(string)
        }

        fun parse(string: String): Dice {
            val (_, dice, modifier, type) = Regex("(.*?)([+-]\\s*\\d+)?(\\s+[A-Za-z\\s,&/()]*)").matchEntire(string)!!.groupValues
            val sizes = dice.split("+").flatMap {
                val (amount, size) = it.trim().split("d")
                List(amount.trim().toInt()) { size.trim().toInt() }
            }
            return Dice(sizes, modifier.replace(Regex("\\s+"), "").toIntOrNull() ?: 0, type.trim())
        }
    }
}

infix fun Int.d(size: Int) = Dice(List(this) { size })

private fun modifier(modifier: Int) = when {
    modifier == 0 -> ""
    modifier < 0 -> " - ${modifier.absoluteValue}"
    else -> " + $modifier"
}
