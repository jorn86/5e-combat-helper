package org.hertsig.dnd.dice

import kotlin.math.absoluteValue

data class Dice(val sizes: Collection<Int>, val modifier: Int = 0, val type: String = "") {
    init { require(sizes.all { it > 0 }) }
    val average get() = sizes.sumOf { it + 1 } / 2.0 + modifier
    fun roll() = DieRolls(sizes.map { DieRoll.roll(it) }, modifier, type)
    fun doubleDice() = Dice(sizes + sizes, modifier, type)
    fun isRelevant() = modifier != 0 || sizes.isNotEmpty()

    operator fun plus(dice: Dice) = Dice(sizes + dice.sizes, modifier + dice.modifier, type.ifBlank { dice.type })
    operator fun plus(modifier: Int) = Dice(sizes, this.modifier + modifier, type)
    operator fun minus(modifier: Int) = Dice(sizes, this.modifier - modifier, type)
    operator fun times(multiplier: Int) = Dice(sizes * multiplier, modifier * multiplier, type)
    operator fun invoke(type: String) = Dice(sizes, modifier, type.ifBlank { this.type })

    fun asString(withAverage: Boolean = false, short: Boolean = false, withType: Boolean = true): String {
        if (short && sizes.size == 1 && modifier == 0) return "d${sizes.single()}"
        var value = sizes.groupBy { it }.map { (size, sizes) -> "${sizes.size}d$size" }.joinToString(" + ")
        if (value.isBlank()) value = modifier.toString() else value += modifier(modifier)
        val average = if (withAverage) "(${average.toInt()})" else ""
        return listOfNotNull(value, average, type.takeIf { withType }).filter { it.isNotBlank() }.joinToString(" ")
    }

    override fun toString() = asString()

    companion object {
        val NONE = Dice(listOf())
        val D20 = (1 d 20)
    }
}

infix fun Int.d(size: Int) = Dice(List(this) { size })

private fun modifier(modifier: Int) = when {
    modifier == 0 -> ""
    modifier < 0 -> " - ${modifier.absoluteValue}"
    else -> " + $modifier"
}

private operator fun <T> Collection<T>.times(multiplier: Int) = List(multiplier) { this }.flatten()
