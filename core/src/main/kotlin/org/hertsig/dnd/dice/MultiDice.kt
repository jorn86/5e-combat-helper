package org.hertsig.dnd.dice

data class MultiDice(val main: Dice, val extra: List<Dice>) {
    constructor(main:Dice, vararg other: Dice): this(main, other.toList())
    constructor(all: List<Dice>): this(all.first(), all.subList(1, all.size))

    val average get() = main.average + extra.sumOf { it.average }
    private val all get() = listOf(main) + extra

    fun roll() = MultiDieRolls(all.map { it.roll() }.toList())
    fun doubleDice() = MultiDice(main.doubleDice(), extra.map { it.doubleDice() })

    operator fun plus(dice: Dice) = if (!main.isRelevant()) MultiDice(dice, extra) else MultiDice(main, extra + dice).reduce()
    operator fun plus(modifier: Int) = MultiDice(main + modifier, extra)
    operator fun minus(modifier: Int) = MultiDice(main - modifier, extra)
    operator fun times(multiplier: Int) = MultiDice(main * multiplier, extra.map { it * multiplier })

    fun asString(withAverage: Boolean = false): String {
        val average = if (withAverage) " (${average.toInt()})" else ""
        return all.map { it.asString(withAverage && extra.size > 1) }.filter { it.isNotBlank() }
            .joinToString(" + ", postfix = average)
    }

    private fun reduce(): MultiDice {
        val dice = all.groupBy { it.type.trim() }
            .mapValues { (type, dice) -> dice.reduce(Dice::plus)(type) }
        val main = dice.getValue(main.type.trim())
        return MultiDice(main, dice.values.toList() - main)
    }

    override fun toString() = asString()

    companion object {
        val D20 = MultiDice(Dice.D20)
    }
}
