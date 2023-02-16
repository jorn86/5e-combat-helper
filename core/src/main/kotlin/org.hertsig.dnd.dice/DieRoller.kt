package org.hertsig.dnd.dice

class DieRoller {
    private val rolls = mutableListOf<DieRoll>()
    private var modifier = 0
    val result get() = DieRolls(rolls, modifier)

    infix fun Int.d(size: Int) {
        rolls.addAll(DieRolls.rollDice(this, size))
    }

    operator fun Unit.plus(value: Int) { modifier += value }
    operator fun Unit.minus(value: Int) { modifier -= value }

    companion object {
        fun roll(rolls: DieRoller.() -> Unit) = DieRoller().also(rolls).result
    }
}
