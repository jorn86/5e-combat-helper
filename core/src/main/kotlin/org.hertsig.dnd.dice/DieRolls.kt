package org.hertsig.dnd.dice

data class MultiDieRolls(val rolls: List<DieRolls>) {
    constructor(vararg rolls: DieRolls): this(rolls.toList())

    val total get() = rolls.sumOf { it.total }
    val allDice get() = rolls.flatMap { it.dice }

    fun display(detail: (List<Int>) -> String = { "" }) = rolls.joinToString(" + ") { it.display(detail) }
}

data class DieRolls(val dice: List<DieRoll>, val modifier: Int = 0, val type: String = "") {
    val total get() = dice.sumOf { it.result } + modifier
    private val grouped get() = dice.groupBy { it.size }.mapValues { (_, it) -> it.highToLow }

    fun display(detail: (List<Int>) -> String = { "" }) = "$total " + grouped
        .map { (dieSize, rolls) -> "${rolls.size}d$dieSize=${rolls.total}${detail(rolls.map { it.result })}" }
        .joinToString(", ", "[", ", $modifier]")

    override fun toString() = display { it.joinToString(",", " (", ")") }
}
