package org.hertsig.dnd.dice

import java.util.Random

var defaultRandom = Random()

data class DieRoll(val size: Int, val result: Int) {
    val expectedAverage get() = (size + 1.0) / 2
    override fun toString() = "d$size=$result"

    companion object {
        fun roll(size: Int, modifier: Int = 0, random: Random = defaultRandom) = DieRoll(size, random.nextInt(size) + 1)
    }
}

val List<DieRoll>.total get() = sumOf { it.result }
val List<DieRoll>.highToLow get() = sortedByDescending { it.result }
