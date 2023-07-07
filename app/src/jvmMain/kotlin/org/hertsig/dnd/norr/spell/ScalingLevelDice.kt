package org.hertsig.dnd.norr.spell

interface ScalingLevelDice {
    fun label(): String
    fun scaling(): Map<Int, String>
}
