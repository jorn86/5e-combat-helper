package org.hertsig.dnd.dice

data class DieRolls(val dice: List<DieRoll>, val modifier: Int = 0, val type: String = "untyped") {
    val total get() = dice.sumOf { it.result } + modifier
    val grouped get() = dice.groupBy { it.size }.mapValues { (_, it) -> it.highToLow }

    fun display(detail: (List<Int>) -> String = { "" }) = "$total " + grouped
        .map { (dieSize, rolls) -> "${rolls.size}d$dieSize=${rolls.total}${detail(rolls.map { it.result })}" }
        .joinToString(", ", "[", ", $modifier]")

    fun invoke(type: String = "untyped") = copy(type = type)

    operator fun plus(rolls: List<DieRoll>) = DieRolls(dice + rolls, modifier)
    operator fun plus(modifier: Int) = DieRolls(dice, this.modifier + modifier)
    operator fun minus(modifier: Int) = DieRolls(dice, this.modifier - modifier)

    override fun toString() = display { it.joinToString(",", " (", ")") }

    companion object {
        fun rollDice(amount: Int, size: Int) = (1..amount).map { DieRoll.roll(size) }
    }
}
